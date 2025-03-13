package domain.monoobjective.implementation.greedy.algorithms;

import domain.EvaluationFunction;
import domain.monoobjective.implementation.MatrixSolution;
import domain.monoobjective.implementation.greedy.GreedyAlgorithm;
import domain.monoobjective.implementation.greedy.comparators.*;
import domain.paths.PathSolution;
import domain.problem.ProblemInstance;
import domain.problem.graph.Link;
import domain.problem.graph.Node;
import domain.problem.virtual.Request;
import domain.problem.virtual.VirtualLink;
import domain.problem.virtual.VirtualNode;
import domain.util.Tools;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;

/**
 *
 * @author cristian.erazo@cinvestav.mx
 */
public class NewOnlineGreedy extends GreedyAlgorithm {

    private List<PathSolution> paths;
    private TreeSet<PathSolution> selected;
    private final List<Request> requestsList;
    private RequestComparator requestComparator;
    private Comparator<PathSolution> pathComparator;
    private VirtualLinkComparator virtualLinkComparator;
    private List<Request> requests;

    public NewOnlineGreedy(ProblemInstance initial, ProblemInstance instance, EvaluationFunction<Double, MatrixSolution> fx) {
        super(instance, fx);
        selected = new TreeSet<>();
        paths = new ArrayList<>();
        for (int i = 0; i < instance.nodes.length - 1; i++) {
            for (int j = i + 1; j < instance.nodes.length; j++) {
                if (instance.mPaths[i][j] != null && !instance.mPaths[i][j].isEmpty()) {
                    paths.addAll(instance.mPaths[i][j]);
                }
            }
        }
        requestsList = Arrays.asList(instance.requests);
        requestComparator = new RequestComparator();
        pathComparator = new PathsComparator(initial);
        virtualLinkComparator = new VirtualLinkComparator();
        requests = new ArrayList<>(requestsList);
    }

    @Override
    public MatrixSolution run() {
        Node DU, CU;
        Link minLink;
        boolean isValid;
        VirtualNode vDU, vCU;
        int k, nAccepted = 0, splitIndx;
        initialize();
        best.isValid = true;
        Collections.sort(requests, requestComparator);
        for (Request request : requests) {
            k = requestsList.indexOf(request);
            best.accepted[k] = true;//marcar como aceptada
            Collections.sort(request.virtualLinks, virtualLinkComparator);
            selected.clear();
            for (VirtualLink vLink : request.virtualLinks) {//asignar todos los enlaces virtuales
                vLink.indxPath = -1;
                splitIndx = instance.getMinSplitIndex(vLink);
                vCU = request.vNodes[vLink.source];
                vDU = request.vNodes[vLink.destination];
                if (vCU.nodeType == 1) {
                    vDU = vCU;
                    vCU = request.vNodes[vLink.destination];
                }
                Collections.sort(paths, pathComparator);
                for (PathSolution path : paths) {
                    if (!selected.contains(path)) {
                        CU = path.getNodesOfPath().get(0);
                        DU = path.getNodesOfPath().get(path.getNodesOfPath().size() - 1);
                        if (DU.nodeType == 2 || CU.nodeType == 1) {
                            CU = DU;
                            DU = path.getNodesOfPath().get(0);
                        }
                        if ((vCU.indxNode == -1 && tCUs.contains(CU))
                                || (vCU.indxNode != -1 && vCU.indxNode != CU.nodePosition)
                                || (vCU.indxNode == -1 && !ProblemInstance.validateAssignament(CU, vCU))) {
                            continue;
                        }
                        if ((vDU.indxNode == -1 && tDUs.contains(DU))
                                || (vDU.indxNode != -1 && vDU.indxNode != DU.nodePosition)
                                || (vDU.indxNode == -1 && !ProblemInstance.validateAssignament(vDU, DU))) {
                            continue;
                        }
                        minLink = null;
                        isValid = true;
                        for (Link link : path.getLinks()) {//obtener el enlace con el menor ancho de banda disponible ..
                            if (link.usedBw + instance.splits[splitIndx].bw > link.bw) {
                                isValid = false;
                                break;
                            }
                            if (minLink == null || (link.bw - link.usedBw) < (minLink.bw - minLink.usedBw)) {
                                minLink = link;
                            }
                        }
                        if (isValid) {
                            int indx = improveSplit(splitIndx, minLink, path.getDelay(), vLink.maxDelay);
                            consumeResources(best.data[k], vLink, path, vCU, vDU, CU, DU, indx);
                            selected.add(path);
                            if (Tools.echo) {
                                System.out.println(String.format("[%d] Accepted. %s-%s -> %s", k, instance.splits[indx], request.vLinks[vDU.nodePosition][vCU.nodePosition], path.toString()));
                            }
                            break;
                        } else if (Tools.echo) {
                            System.out.println(String.format("[%d][NoResources] %s-%s  -> %s", k, instance.splits[splitIndx], request.vLinks[vDU.nodePosition][vCU.nodePosition], path));
                        }
                    }
                }
                if (vLink.indxPath == -1) {//no es una solucion valida
                    best.accepted[k] = false;//marcar como no-aceptada
                    if (Tools.echo) {
                        System.out.println(String.format("[%d] Rejected.", k));
                    }
                    break;
                }
            }
            if (best.accepted[k]) {
                nAccepted++;
                tCUs.clear();
                tDUs.clear();
            } else {
                freeResources(request, k);
                if (!tCUs.isEmpty()) {
                    if (Tools.echo) {
                        System.out.println("Warning CUs not empty !!\ntree_CUs clear !");
                    }
                    tCUs.clear();
                }
                if (!tDUs.isEmpty()) {
                    if (Tools.echo) {
                        System.out.println("Warning DUs not empty !!\ntree_DUs clear !");
                    }
                    tDUs.clear();
                }
                if (Tools.fitAll) {
                    return null;
                }
            }
        }
        best.setnAccepted(nAccepted);
        best.gn = instance.validate(best);
        fx.evaluate(best);
        return best;
    }

    /**
     *
     * @param splitIndx
     * @param minLink
     * @param pathDelay
     * @param maxDelay
     * @return
     */
    @Override
    protected int improveSplit(int splitIndx, Link minLink, double pathDelay, double maxDelay) {
        int i = splitIndx + 1;
        while (i < instance.splits.length - 3 && (instance.splits[i].bw + minLink.usedBw) <= minLink.bw && pathDelay <= instance.splits[i].delay && pathDelay <= maxDelay) {
            splitIndx = i;
            i++;
        }
        return splitIndx;
    }

    /**
     *
     * @param request
     * @param k
     */
    private void freeResources(Request request, int k) {
        VirtualNode vCU;
        for (VirtualNode vDU : request.vDUs) {
            if (vDU.indxNode == -1) {
                break;
            }
            instance.nodes[vDU.indxNode].usedPRC -= vDU.prc;
            if (instance.nodes[vDU.indxNode].usedPRC < 0) {
                if (Tools.echo) {
                    System.out.println(String.format("prc %d < 0", instance.nodes[vDU.indxNode].usedPRC));
                }
                instance.nodes[vDU.indxNode].usedPRC = 0;
            }
            instance.nodes[vDU.indxNode].usedANT -= vDU.ant;
            if (instance.nodes[vDU.indxNode].usedANT < 0) {
                if (Tools.echo) {
                    System.out.println(String.format("ant %d < 0", instance.nodes[vDU.indxNode].usedANT));
                }
                instance.nodes[vDU.indxNode].usedANT = 0;
            }
            instance.nodes[vDU.indxNode].usedPRB -= vDU.prb;
            if (instance.nodes[vDU.indxNode].usedPRB < 0) {
                if (Tools.echo) {
                    System.out.println(String.format("prb %d < 0", instance.nodes[vDU.indxNode].usedPRB));
                }
                instance.nodes[vDU.indxNode].usedPRB = 0;
            }
            for (int n : vDU.nears) {
                vCU = request.vNodes[n];
                if (instance.mPaths[vCU.indxNode][vDU.indxNode] != null && !instance.mPaths[vCU.indxNode][vDU.indxNode].isEmpty()) {
                    for (Link link : instance.mPaths[vCU.indxNode][vDU.indxNode].get(best.data[k][vDU.indx + instance.pathPosition]).getLinks()) {//actualizar ancho de banda para los enlaces
                        link.usedBw -= instance.splits[best.data[k][vDU.indx + instance.splitPosition]].bw;
                        if (link.usedBw < 0) {
                            if (Tools.echo) {
                                System.out.println(String.format("bw %.2f < 0", link.usedBw));
                            }
                            link.usedBw = 0;
                        }
                    }
                }
            }
        }
        freeResources(request);
    }

    /**
     *
     * @param request
     */
    private void freeResources(Request request) {
        for (VirtualNode vCU : request.vCUs) {
            if (vCU.indxNode == -1) {
                break;
            }
            instance.nodes[vCU.indxNode].usedPRC -= vCU.prc;
            if (instance.nodes[vCU.indxNode].usedPRC < 0) {
                if (Tools.echo) {
                    System.out.println(String.format("PRC %d < 0", instance.nodes[vCU.indxNode].usedPRC));
                }
                instance.nodes[vCU.indxNode].usedPRC = 0;
            }
        }
    }

    private void consumeResources(Integer[] data, VirtualLink vLink, PathSolution pathSolution, VirtualNode vCU, VirtualNode vDU, Node CU, Node DU, int splitIndx) {
        if (vCU.indxNode == -1) {//no se ha asignado previamente la CU
            if (!tCUs.add(CU)) {
                System.out.println("Error while adding to CU tree!!");
            }
            CU.usedPRC += vCU.prc;
            if (CU.usedPRC > CU.prc) {
                System.out.println("Error while embedding the CU prc");
                CU.usedPRC = CU.prc;
            }
            vCU.indxNode = CU.nodePosition;
            data[vCU.indx] = CU.nodePosition;
        }
        if (!tDUs.add(DU)) {
            System.out.println("Error while adding to DU tree!!");
        }
        DU.usedPRC += vDU.prc;
        if (DU.usedPRC > DU.prc) {
            System.out.println("Error while embedding the DU prc");
            DU.usedPRC = DU.prc;
        }
        DU.usedANT += vDU.ant;
        if (DU.usedANT > DU.ant) {
            System.out.println("Error while embedding the DU ant");
            DU.usedANT = DU.ant;
        }
        DU.usedPRB += vDU.prb;
        if (DU.usedPRB > DU.prb) {
            System.out.println("Error while embedding the DU prb");
            DU.usedPRB = DU.prb;
        }
        vDU.indxNode = DU.nodePosition;
        data[vDU.indx] = DU.nodePosition;
        vLink.indxPath = instance.mPaths[vDU.indxNode][vCU.indxNode].indexOf(pathSolution);
        for (Link link : pathSolution.getLinks()) {
            link.usedBw += instance.splits[splitIndx].bw;
            if (link.usedBw > link.bw) {
                System.out.println("Error while embedding link bw");
                link.usedBw = link.bw;
            }
        }
        data[vDU.indx + instance.pathPosition] = vLink.indxPath;
        data[vDU.indx + instance.splitPosition] = splitIndx;
    }

    private class PathsComparator implements Comparator<PathSolution> {

        private final ProblemInstance initial;

        public PathsComparator(ProblemInstance initial) {
            this.initial = initial;
        }

        @Override
        public int compare(PathSolution o1, PathSolution o2) {
            if (initial != null) {
                PathSolution p1 = null, p2 = null;
                Node nodeInitial1 = o1.getNodesOfPath().get(0),
                        nodeEnd1 = o1.getNodesOfPath().get(o1.getNodesOfPath().size() - 1);
                Node nodeInitial2 = o2.getNodesOfPath().get(0),
                        nodeEnd2 = o2.getNodesOfPath().get(o2.getNodesOfPath().size() - 1);
                List<PathSolution> l1 = initial.mPaths[nodeInitial1.nodePosition][nodeEnd1.nodePosition],
                        l2 = initial.mPaths[nodeInitial2.nodePosition][nodeEnd2.nodePosition];
                int idx = l1.indexOf(o1);
                if (idx != -1) {
                    p1 = l1.get(idx);
                }
                idx = l2.indexOf(o2);
                if (idx != -1) {
                    p2 = l2.get(idx);
                }
                if (p1 != null && p2 != null) {
                    int n1 = 0, n2 = 0;
                    for (Link link : p1.getLinks()) {
                        if (link.usedBw > 0) {
                            n1++;
                        }
                    }
                    for (Link link : p2.getLinks()) {
                        if (link.usedBw > 0) {
                            n2++;
                        }
                    }
                    return Integer.compare(n1, n2);
                } else {
                    System.out.println("Error!! no matches !!");
                }
            }
            return Integer.compare(o1.getLength(), o2.getLength());
        }
    }
}
