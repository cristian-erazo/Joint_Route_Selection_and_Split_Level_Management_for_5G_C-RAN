package domain.monoobjective.implementation.metaheuristics.algorithms.initialization;

import domain.monoobjective.implementation.MatrixSolution;
import domain.monoobjective.implementation.greedy.comparators.RequestComparator;
import domain.monoobjective.implementation.greedy.comparators.VirtualLinkComparator;
import domain.paths.PathSolution;
import domain.problem.ProblemInstance;
import domain.problem.graph.Flow;
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
import java.util.Objects;
import java.util.Random;
import java.util.TreeSet;

/**
 *
 * @author cristian.erazo@cinvestav.mx
 * @param <T>
 */
public class GreedyPaths<T extends MatrixSolution> extends InitializationApproach<T> {

    private Comparator<VirtualLink> virtualLinkComparator;
    private Comparator<Request> requestComparator;
    private List<Node> possibleDUs;
    private List<Node> possibleCUs;
    private List<PathSolution> possiblePaths;

    public GreedyPaths(ProblemInstance instance, int popSize, Random rand) {
        super(rand, instance, popSize);
        this.virtualLinkComparator = new VirtualLinkComparator();
        this.requestComparator = new RequestComparator();
        int nearTotalDUs = (int) (instance.DUs.size() * 0.35);
        possibleDUs = new ArrayList<>(nearTotalDUs);
        possibleCUs = new ArrayList<>((int) (instance.CUs.size() * 0.5));
        possiblePaths = new ArrayList<>(3 * nearTotalDUs);
    }

    @Override
    public List<T> run() {
        if (popSize > 0) {
            int n = instance.requests.length, m = instance.maxVirtualDUs * instance.step, j;
            List<T> population = new ArrayList<>();
            tCUs = new TreeSet<>();
            tDUs = new TreeSet<>();
            List<Request> requestsList = Arrays.asList(instance.requests);
            List<Request> requests = new ArrayList<>(requestsList);
            for (int k = 0; k < popSize; k++) {
                T init = initializeSolution(n, m);
                init.accepted = new boolean[n];
                for (int i = 0; i < instance.requests.length; i++) {//para cada peticion, asignar el orden inicial y las posiciones de la matriz
                    j = 0;//cuenta las posiciones de la columna para asignarla al grafo
                    for (VirtualNode vDU : instance.requests[i].vDUs) {//para cada nodo vDU, asignar la posicion a la que le corresponde en la matriz
                        vDU.indx = j;//asignar la posicion de la columna al nodo vDU
                        // indice para las rutas ...
                        instance.requests[i].vLinks[vDU.nodePosition][vDU.nears.get(0)].indx = j + instance.pathPosition;
                        // ...
                        j += instance.step;//la siguiente posicion dependera del tipo de representacion
                    }
                    for (; j < init.getM(); j++) {
                        init.data[i][j] = -1;
                    }
                }
                if (k != 0 || !findPaths(requests, init, requestsList)) {
                    random(init);
                }
                population.add(init);
            }
            return population;
        }
        return null;
    }

    private void random(T init) {
        int j;
        for (int i = 0; i < instance.requests.length; i++) {
            for (VirtualNode vDU : instance.requests[i].vDUs) {
                j = vDU.indx;
                init.data[i][j] = instance.min.data[i][j] + (Objects.equals(instance.max.data[i][j], instance.min.data[i][j]) ? 0 : (int) (rand.nextDouble() * (instance.max.data[i][j] - instance.min.data[i][j])));
                init.data[i][j + instance.splitPosition] = instance.min.data[i][j + instance.splitPosition] + (int) (rand.nextDouble() * (instance.max.data[i][j + instance.splitPosition] - instance.min.data[i][j + instance.splitPosition]));
            }
        }
    }

    private boolean findPaths(List<Request> requests, MatrixSolution init, List<Request> requestsList) {
        Node DU, CU;
        Link minLink;
        boolean isValid;
        VirtualNode vDU, vCU;
        int k, nAccepted = 0, splitIndx;
        Collections.sort(requests, requestComparator);
        init.isValid = true;
        for (Request request : requests) {
            k = requestsList.indexOf(request);
            init.accepted[k] = true;//marcar como aceptada
            Collections.sort(request.virtualLinks, virtualLinkComparator);
            for (VirtualLink vLink : request.virtualLinks) {//asignar todos los enlaces virtuales
                vLink.indxPath = -1;
                splitIndx = instance.getMinSplitIndex(vLink);
                vCU = request.vNodes[vLink.source];
                vDU = request.vNodes[vLink.destination];
                if (vCU.nodeType == 1) {
                    vDU = vCU;
                    vCU = request.vNodes[vLink.destination];
                }
                for (PathSolution path : listPossiblePaths(vDU, vCU, vLink, instance.splits[splitIndx])) {
                    CU = path.getNodesOfPath().get(0);
                    DU = path.getNodesOfPath().get(path.getNodesOfPath().size() - 1);
                    if (DU.nodeType == 2) {
                        CU = DU;
                        DU = path.getNodesOfPath().get(0);
                    }
                    boolean wasAdded = tCUs.contains(CU);
                    if (vCU.indxNode == -1 && wasAdded) {
                        continue;
                    } else if (vCU.indxNode != -1 && vCU.indxNode != CU.nodePosition) {
                        continue;
                    } else if (vCU.indxNode == -1) {
                        if (!ProblemInstance.validateAssignament(CU, vCU)) {
                            continue;
                        }
                    }
                    wasAdded = tDUs.contains(DU);
                    if (vDU.indxNode == -1 && wasAdded) {
                        continue;
                    } else if (vDU.indxNode != -1 && vDU.indxNode != DU.nodePosition) {
                        continue;
                    } else if (vDU.indxNode == -1) {
                        if (!ProblemInstance.validateAssignament(vDU, DU)) {
                            continue;
                        }
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
                        assignResources(init.data[k], vLink, path, vCU, vDU, CU, DU, indx);
                        break;
                    }
                }
                if (vLink.indxPath == -1) {//no es una solucion valida
                    init.accepted[k] = false;//marcar como no-aceptada
                    break;
                }
            }
            if (init.accepted[k]) {
                nAccepted++;
                tCUs.clear();
                tDUs.clear();
            } else {
                cleanAssignments(k, init);
                if (!tCUs.isEmpty()) {
                    tCUs.clear();
                }
                if (!tDUs.isEmpty()) {
                    tDUs.clear();
                }
                if (Tools.fitAll) {
                    return false;
                }
            }
        }
        init.setnAccepted(nAccepted);
        if (nAccepted > 0) {
            instance.cleanResources();
            return true;
        } else {
            return false;
        }
    }

    /**
     *
     * @param splitIndx
     * @param minLink
     * @param pathDelay
     * @param maxDelay
     * @return
     */
    private int improveSplit(int splitIndx, Link minLink, double pathDelay, double maxDelay) {
        //la ruta es valida, elegir el mejor split
        int i = splitIndx + 1;
        while (i < instance.splits.length && (instance.splits[i].bw + minLink.usedBw) <= minLink.bw && pathDelay <= instance.splits[i].delay && pathDelay <= maxDelay) {
            splitIndx = i;
            i++;
        }
        return splitIndx;
    }

    private void cleanAssignments(int k, MatrixSolution init) {
        VirtualNode vDU, vCU;
        Node DU, CU;
        for (VirtualLink vLink : instance.requests[k].virtualLinks) {
            if (vLink.indxPath == -1) {
                break;
            }
            vCU = instance.requests[k].vNodes[vLink.source];
            vDU = instance.requests[k].vNodes[vLink.destination];
            if (vCU.nodeType == 1) {
                vDU = vCU;
                vCU = instance.requests[k].vNodes[vLink.destination];
            }
            CU = instance.nodes[vCU.indxNode];
            DU = instance.nodes[vDU.indxNode];
            if (tCUs.remove(CU)) {
                CU.usedPRC -= vCU.prc;
                if (CU.usedPRC < 0) {
                    CU.usedPRC = 0;
                }
            }
            if (tDUs.remove(DU)) {
                DU.usedANT -= vDU.ant;
                if (DU.usedANT < 0) {
                    DU.usedANT = 0;
                }
                DU.usedPRB -= vDU.prb;
                if (DU.usedPRB < 0) {
                    DU.usedPRB = 0;
                }
                DU.usedPRC -= vDU.prc;
                if (DU.usedPRC < 0) {
                    DU.usedPRC = 0;
                }
            }
            for (Link link : instance.mPaths[0][0].get(vLink.indxPath).getLinks()) {
                link.usedBw -= instance.splits[init.data[k][vDU.indx + instance.splitPosition]].bw;
                if (link.usedBw < 0) {
                    link.usedBw = 0;
                }
            }
        }
    }

    private void assignResources(Integer[] data, VirtualLink vLink, PathSolution pathSolution, VirtualNode vCU, VirtualNode vDU, Node CU, Node DU, int splitIndx) {
        if (vCU.indxNode == -1) {//no se ha asignado previamente la CU
            if (!tCUs.add(CU)) {
                System.out.println("Error: 1");
            }
            CU.usedPRC += vCU.prc;
            if (CU.usedPRC > CU.prc) {
                CU.usedPRC = CU.prc;
            }
            vCU.indxNode = CU.nodePosition;
        }
        if (!tDUs.add(DU)) {
            System.out.println("Error: 2");
        }
        DU.usedPRC += vDU.prc;
        if (DU.usedPRC > DU.prc) {
            DU.usedPRC = DU.prc;
        }
        DU.usedANT += vDU.ant;
        if (DU.usedANT > DU.ant) {
            DU.usedANT = DU.ant;
        }
        DU.usedPRB += vDU.prb;
        if (DU.usedPRB > DU.prb) {
            DU.usedPRB = DU.prb;
        }
        vDU.indxNode = DU.nodePosition;
        for (Link link : pathSolution.getLinks()) {
            link.usedBw += instance.splits[splitIndx].bw;
            if (link.usedBw > link.bw) {
                link.usedBw = link.bw;
            }
        }
        vLink.indxPath = instance.mPaths[0][0].indexOf(pathSolution);
        data[vDU.indx + instance.pathPosition] = vLink.indxPath;
        data[vDU.indx + instance.splitPosition] = splitIndx;
    }

    @Override
    protected T initializeSolution(int n, int m) {
        return (T) new MatrixSolution(n, m);
    }

    private List<PathSolution> listPossiblePaths(VirtualNode vDU, VirtualNode vCU, VirtualLink vLink, Flow possibleSplit) {
        loadValidDUs(vDU);
        loadValidCUs(vCU);
        possiblePaths.clear();
        for (Node DU : possibleDUs) {
            int idx = DU.nodePosition;
            for (Node CU : possibleCUs) {
                int idy = CU.nodePosition;
                for (PathSolution path : instance.allPathsFromDUs.get(idx).get(idy)) {
                    if(ProblemInstance.validateAssignament(path, possibleSplit, vLink)) {
                        possiblePaths.add(path);
                    }
                }
            }
        }
        possibleDUs.clear();
        possibleCUs.clear();
        return possiblePaths;
    }

    private void loadValidDUs(VirtualNode vDU) {
        for (Node DU : instance.DUs) {
            if (ProblemInstance.validateAssignament(vDU, DU)) {
                possibleDUs.add(DU);
            }
        }
    }

    private void loadValidCUs(VirtualNode vCU) {
        for (Node CU : instance.CUs) {
            if (ProblemInstance.validateAssignament(CU, vCU)) {
                possibleCUs.add(CU);
            }
        }
    }
}
