package domain.monoobjective.implementation.metaheuristics.algorithms.reparation;

import domain.monoobjective.implementation.MatrixSolution;
import domain.monoobjective.implementation.greedy.comparators.NodeComparator;
import domain.monoobjective.implementation.greedy.comparators.NodeDistanceComparator;
import domain.monoobjective.implementation.greedy.comparators.NodeFittestResourcesComparator;
import domain.monoobjective.implementation.greedy.comparators.PathComparator;
import domain.operators.Reparator;
import domain.paths.PathSolution;
import domain.problem.ProblemInstance;
import domain.problem.graph.Node;
import domain.problem.virtual.VirtualLink;
import domain.problem.virtual.VirtualNode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;

/**
 *
 * @author cristian.erazo@cinvestav.mx
 * @param <T>
 */
public class GreedyReparator<T extends MatrixSolution> implements Reparator<T> {

    protected T s;
    protected Random r;
    protected final int[] idx;
    protected final boolean[] True;
    protected ProblemInstance instance;
    protected List<PathSolution> paths;
    protected NodeComparator distCmp;
    protected NodeComparator resCmp;
    protected List<Node> CUs;
    protected List<Node> DUs;
    protected PathComparator pthCmp;
    protected TreeSet<Node> visitedCUs;

    public GreedyReparator(Random r, ProblemInstance instance) {
        this.r = r;
        this.instance = instance;
        idx = new int[instance.requests.length];
        True = new boolean[instance.types[0].length];
        for (int i = 0; i < idx.length; i++) {
            idx[i] = i;
        }
        Arrays.fill(True, true);
        paths = (instance.isFullyRepresentated() ? null : new ArrayList<>(instance.mPaths[0][0]));
        distCmp = new NodeDistanceComparator();
        resCmp = new NodeFittestResourcesComparator();
        CUs = new ArrayList<>(instance.CUs);
        DUs = new ArrayList<>(instance.DUs);
        pthCmp = new PathComparator();
        visitedCUs = new TreeSet<>();
    }

    @Override
    public T run() {
        if (s != null) {
            VirtualNode vCU, vDU;
            VirtualLink vLink;
            boolean assigned;
            int q, x;
            shuffle(idx);
            instance.cleanResources();
            s.isValid = true;
            visitedCUs.clear();
            for (x = 0; x < idx.length; x++) {
                q = idx[x];
                vCU = vDU = null;
                if (s.accepted[q]) {
                    if (!Arrays.equals(instance.isValid[q], True)) {
                        for (int j = 0; j < s.getM(); j++) {
                            if (instance.isValid[q][j]) {
                                switch (instance.types[q][j]) {
                                    case 1://cu
                                        vCU = getVirtualCU(q, j);
                                        break;
                                    case 2://du
                                        vDU = getVirtualDU(q, j);
                                        break;
                                }
                            } else {//repair !!
                                switch (instance.types[q][j]) {
                                    case 1://cu
                                        vCU = getVirtualCU(q, j);
                                        resCmp.setBase(vCU);
                                        Collections.sort(CUs, resCmp);
                                        assigned = false;
                                        for (Node CU : CUs) {
                                            if (!visitedCUs.contains(CU) && !instance.tCUs.contains(CU) && ProblemInstance.validateAssignament(CU, vCU)) {
                                                vCU.indxNode = CU.nodePosition;
                                                s.data[q][j] = vCU.indxNode;
                                                assigned = true;
                                                break;
                                            }
                                        }
                                        if (!assigned) {
                                            s.accepted[q] = false;
                                            Arrays.fill(instance.isValid[q], false);
                                            j = s.getM();
                                        }
                                        break;
                                    case 2://du
                                        vDU = getVirtualDU(q, j);
                                        distCmp.setBase(vDU);
                                        Collections.sort(DUs, distCmp);
                                        assigned = false;
                                        for (Node DU : DUs) {
                                            if (!instance.tDUs.contains(DU) && ProblemInstance.validateAssignament(vDU, DU)) {
                                                vDU.indxNode = DU.nodePosition;
                                                if (instance.mPaths[s.data[q][vCU.indx]][vDU.indxNode] != null) {
                                                    s.data[q][j] = vDU.indxNode;
                                                    assigned = true;
                                                    break;
                                                }
                                            }
                                        }
                                        if (!assigned) {
                                            visitedCUs.add(instance.nodes[s.data[q][vCU.indx]]);
                                            Arrays.fill(instance.isValid[q], vCU.indx, j, false);
                                            j = vCU.indx - 1;
                                        }
                                        break;
                                    case 3://path
                                        if (instance.isFullyRepresentated()) {
                                            List<PathSolution> t = instance.mPaths[s.data[q][vCU.indx]][s.data[q][vDU.indx]];
                                            if (t != null) {
                                                vLink = instance.requests[q].vLinks[vCU.nodePosition][vDU.nodePosition];
                                                paths = new ArrayList<>(t);
                                                Collections.sort(paths, pthCmp);
                                                s.data[q][j] = -1;
                                                for (PathSolution path : paths) {
                                                    if (ProblemInstance.validateAssignament(path, instance.splits[s.data[q][j + 1]], vLink)) {
                                                        s.data[q][j] = t.indexOf(path);
                                                        break;
                                                    }
                                                }
                                                if (s.data[q][j] == -1) {
                                                    s.data[q][j] = 0;
                                                }
                                            } else {
                                                instance.isValid[q][j - 1] = false;
                                                j = j - 2;
                                            }
                                        } else {
                                            vDU = instance.requests[q].vDUs.get(j / instance.step);
                                            vCU = instance.requests[q].vNodes[vDU.nears.get(0)];
                                            vLink = instance.requests[q].vLinks[vDU.nodePosition][vCU.nodePosition];
                                            Collections.sort(paths, pthCmp);
                                            assigned = false;
                                            for (PathSolution path : paths) {
                                                Node CU = path.getNodesOfPath().get(0);
                                                Node DU = path.getNodesOfPath().get(path.getNodesOfPath().size() - 1);
                                                if (DU.nodeType == 2) {
                                                    CU = DU;
                                                    DU = path.getNodesOfPath().get(0);
                                                }
                                                boolean wasAdded = instance.tCUs.contains(CU);
                                                if (vCU.indxNode == -1 && wasAdded) {
                                                    continue;
                                                } else if (vCU.indxNode != -1 && vCU.indxNode != CU.nodePosition) {
                                                    continue;
                                                } else if (vCU.indxNode == -1) {
                                                    if (!ProblemInstance.validateAssignament(CU, vCU)) {
                                                        continue;
                                                    }
                                                }
                                                wasAdded = instance.tDUs.contains(DU);
                                                if (vDU.indxNode == -1 && wasAdded) {
                                                    continue;
                                                } else if (vDU.indxNode != -1 && vDU.indxNode != DU.nodePosition) {
                                                    continue;
                                                } else if (vDU.indxNode == -1) {
                                                    if (!ProblemInstance.validateAssignament(vDU, DU)) {
                                                        continue;
                                                    }
                                                }
                                                if (ProblemInstance.validateAssignament(path, instance.splits[s.data[q][j + instance.splitPosition]], vLink)) {
                                                    s.data[q][j] = instance.mPaths[0][0].indexOf(path);
                                                    assigned = true;
                                                    break;
                                                }
                                            }
                                            if (!assigned) {
                                                s.accepted[q] = false;
                                                Arrays.fill(instance.isValid[q], false);
                                                j = s.getM();
                                            }
                                        }
                                        break;
                                    case 4://split
                                        if (instance.isFullyRepresentated()) {
                                            s.data[q][j] = instance.getMinSplitIndex(instance.requests[q].vLinks[vCU.nodePosition][vDU.nodePosition]);
                                            if (j + 1 < s.getM() && instance.types[q][j + 1] == 1) {
                                                visitedCUs.clear();
                                            }
                                        } else {
                                            s.data[q][j] = instance.getMinSplitIndex(instance.requests[q].virtualLinks.get(j / instance.step));
                                        }
                                        break;
                                }
                            }
                        }
                        visitedCUs.clear();
                    }
                    ProblemInstance.consumeResources(instance, s, q);
                }
            }
            s.gn = instance.validate(s);
        }
        return s;
    }

    @Override
    public void setIndividual(T ind) {
        s = ind;
    }

    private void shuffle(int[] arr) {
        int n = arr.length / 2, t, x, l = arr.length;
        for (int i = 0; i < n; i++) {
            x = r.nextInt(l);
            t = arr[x];
            arr[x] = arr[i];
            arr[i] = t;
        }
    }

    private VirtualNode getVirtualCU(int q, int i) {
        while (i >= 0 && instance.types[q][i] != 1) {
            i--;
        }
        if (i >= 0) {
            for (VirtualNode vCU : instance.requests[q].vCUs) {
                if (vCU.indx == i) {
                    return vCU;
                }
            }
        }
        return null;
    }

    private VirtualNode getVirtualDU(int q, int i) {
        while (i > 0 && instance.types[q][i] != 2) {
            i--;
        }
        if (i > 0) {
            for (VirtualNode vDU : instance.requests[q].vDUs) {
                if (vDU.indx == i) {
                    return vDU;
                }
            }
        }
        return null;
    }

    @Override
    public Reparator<T> copy(ProblemInstance p) {
        return new GreedyReparator<>(new Random(r.nextLong()), p);
    }
}
