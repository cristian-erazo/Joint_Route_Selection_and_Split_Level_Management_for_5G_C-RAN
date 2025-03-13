package domain.monoobjective.implementation.metaheuristics.algorithms.initialization;

import domain.monoobjective.implementation.MatrixSolution;
import domain.monoobjective.implementation.greedy.comparators.NodeComparator;
import domain.monoobjective.implementation.greedy.comparators.NodeDistanceComparator;
import domain.monoobjective.implementation.greedy.comparators.NodeFittestResourcesComparator;
import domain.monoobjective.implementation.greedy.comparators.PathComparator;
import domain.monoobjective.implementation.greedy.comparators.RequestComparator;
import domain.monoobjective.implementation.greedy.comparators.VirtualNodeComparator;
import domain.paths.PathSolution;
import domain.problem.ProblemInstance;
import domain.problem.graph.Flow;
import domain.problem.graph.Link;
import domain.problem.graph.Node;
import domain.problem.virtual.Request;
import domain.problem.virtual.VirtualNode;
import domain.util.Tools;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;

/**
 *
 * @author cristian.erazo@cinvestav.mx
 * @param <T>
 */
public class GreedyFull<T extends MatrixSolution> extends InitializationApproach<T> {

    private final Comparator<PathSolution> pathsComparator;
    private final Comparator<Request> requestsComparator;
    private final Comparator<VirtualNode> vCUsComparator;
    private final Comparator<VirtualNode> vDUsComparator;
    private final NodePositionComparator naturalOrder;
    private final NodeComparator CUsComparator;
    private final NodeComparator DUsComparator;

    public GreedyFull(ProblemInstance instance, int popSize, Random rand) {
        super(rand, instance, popSize);
        vDUsComparator = vCUsComparator = new VirtualNodeComparator();
        CUsComparator = new NodeFittestResourcesComparator();
        requestsComparator = new RequestComparator();
        DUsComparator = new NodeDistanceComparator();
        naturalOrder = new NodePositionComparator();
        pathsComparator = new PathComparator();
    }

    @Override
    public List<T> run() {
        if (popSize > 0) {
            int n = instance.requests.length, m = instance.maxVirtualDUs * instance.step + instance.maxVirtualCUs, j;
            List<T> population = new ArrayList<>();
            List<Request> requestsList = Arrays.asList(instance.requests);
            List<Flow> splits = Arrays.asList(instance.splits);
            List<Flow> functionalSplits = new ArrayList<>(splits);
            List<Node> DUsList = new ArrayList<>();
            List<Node> CUsList = new ArrayList<>();
            DUsList.addAll(instance.DUs);
            CUsList.addAll(instance.CUs);
            tCUs = new TreeSet<>();
            tDUs = new TreeSet<>();
            for (int k = 0; k < popSize;) {
                T init = initializeSolution(n, m);
                init.accepted = new boolean[n];
                for (int i = 0; i < instance.requests.length; i++) {//para cada peticion, asignar el orden inicial y las posiciones de la matriz
                    j = 0;//cuenta las posiciones de la columna para asignarla al grafo
                    for (VirtualNode vCU : instance.requests[i].vCUs) {//para cada nodo vCU, asignar la posicion a la que le corresponde en la matriz
                        vCU.indx = j;//asignar la posicion de la columna al nodo vCU
                        j++;//la posicion de la DU sera la inmediatamente siguiente
                        for (Integer vdu : vCU.nears) {//obtener los nodos DU a los que se conecta (cada CU se conecta por un enlace)
                            instance.requests[i].vNodes[vdu].indx = j;//asignar posiciones relativas al nodo vCU
                            // indice para las rutas ...
                            instance.requests[i].vLinks[vdu][vCU.nodePosition].indx = j + instance.pathPosition;
                            // ...
                            j += instance.step;//la siguiente posicion dependera del tipo de representacion
                        }
                    }
                    for (; j < init.getM(); j++) {
                        init.data[i][j] = -1;
                    }
                }
                if (k == 0) {
                    if (!greedy(init, requestsList, DUsList, CUsList)) {
                        random(init, requestsList, splits, DUsList, CUsList, functionalSplits);
                    }
                } else {
                    random(init, requestsList, splits, DUsList, CUsList, functionalSplits);
                }
                //if (!population.contains(initializeSolution)) {
                population.add(init);
                k++;
                //}
            }
            return population;
        }
        return null;
    }

    private boolean greedy(T init, List<Request> requestsList, List<Node> DUsList, List<Node> CUsList) {
        Link minLink;
        boolean isValid;
        int nAccepted = 0, k, splitIndx;
        List<PathSolution> paths = new ArrayList<>();
        List<Request> requests = new ArrayList<>(requestsList);
        Collections.sort(requests, requestsComparator);
        init.isValid = true;
        for (Request request : requests) {//para cada peticion...
            k = requestsList.indexOf(request);//identificador de la peticion en el orden original (ingresado)
            init.accepted[k] = true;//marcar la peticion como atendida
            Collections.sort(request.vCUs, vCUsComparator);
            isValid = true;
            for (VirtualNode vCU : request.vCUs) {//asignar CUs fisicos
                vCU.indxNode = -1;
                CUsComparator.setBase(vCU);
                Collections.sort(CUsList, CUsComparator);
                for (Node CU : CUsList) {
                    if (ProblemInstance.validateAssignament(CU, vCU) && tCUs.add(CU)) {
                        asignarRecursos(init, vCU, CU, k);
                        break;
                    }
                }
                if (vCU.indxNode == -1) {
                    init.accepted[k] = false;//marcar la peticion como no-atendida
                    isValid = false;
                    break;//pasar a la siguiente peticion
                }
            }
            tCUs.clear();
            if (!isValid) {//no se asignaron todas las CUs virtuales, liberar las asignadas..
                liberarRecursos(request);
            } else {//se asignaron todas las CUs virtuales, asignar las DUs virtuales ..
                Collections.sort(request.vDUs, vDUsComparator);
                for (VirtualNode vDU : request.vDUs) {//asignar DUs fisicos para cada DU virtual
                    vDU.indxNode = -1;
                    DUsComparator.setBase(vDU);
                    Collections.sort(DUsList, DUsComparator);
                    for (Node DU : DUsList) {
                        if (!tDUs.contains(DU)) {
                            for (int vcu : vDU.nears) {//por cada enlace del vDU
                                splitIndx = instance.getMinSplitIndex(request.vLinks[vDU.nodePosition][vcu]);
                                request.vLinks[vDU.nodePosition][vcu].indxPath = -1;
                                if (splitIndx == -1) {
                                    break;
                                }
                                if (ProblemInstance.validateAssignament(vDU, DU) && instance.mPaths[DU.nodePosition][request.vNodes[vcu].indxNode] != null && !instance.mPaths[DU.nodePosition][request.vNodes[vcu].indxNode].isEmpty()) {
                                    paths.addAll(instance.mPaths[DU.nodePosition][request.vNodes[vcu].indxNode]);
                                    Collections.sort(paths, pathsComparator);
                                    //validar la posible ruta y elegir el mejor split
                                    for (PathSolution path : paths) {
                                        if (request.vLinks[vDU.nodePosition][vcu].maxDelay >= path.getDelay() && request.vLinks[vDU.nodePosition][vcu].bw <= path.getMinBw()) {
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
                                            if (isValid) {//es una asignacion valida, tratar de mejorar el split y asignar los recursos ...
                                                asignarRecursos(init, vDU, DU, k, instance.mPaths[DU.nodePosition][request.vNodes[vcu].indxNode].indexOf(path), mejorarSplit(splitIndx, minLink, path.getDelay(), request.vLinks[vDU.nodePosition][vcu].maxDelay), path);
                                                request.vLinks[vDU.nodePosition][vcu].indxPath = init.data[k][vDU.indx + instance.pathPosition];
                                                break;//se hizo la asignacion, dejar de buscar rutas o splits
                                            }
                                        }
                                    }
                                    paths.clear();
                                }
                            }
                            if (vDU.indxNode != -1) {
                                break;//se hizo la asignacion .. pasar a la siguiente DU virtual
                            }
                        }
                    }
                    if (vDU.indxNode == -1) {//no se puede atender la peticion
                        init.accepted[k] = false;//marcar la peticion como no-atendida
                        break;//pasar a la siguiente peticion
                    }
                }
                tDUs.clear();
                if (init.accepted[k]) {//solucion atendida?
                    nAccepted++;
                } else {//no fue atendida, liberar recursos..
                    liberarRecursos(init, request, k);
                    liberarRecursos(request);
                    if (Tools.fitAll) {
                        return false;
                    }
                }
            }
        }
        init.setnAccepted(nAccepted);
        for (Request request : instance.requests) {
            Collections.sort(request.vCUs, naturalOrder);
            Collections.sort(request.vDUs, naturalOrder);
        }
        if (nAccepted < 1) {
            init.isValid = false;
        }
        instance.cleanResources();
        return init.isValid;
    }

    /**
     *
     * @param request
     * @param k
     */
    private void liberarRecursos(T s, Request request, int k) {
        VirtualNode vCU;
        for (VirtualNode vDU : request.vDUs) {
            if (vDU.indxNode == -1) {
                break;
            }
            instance.nodes[vDU.indxNode].usedPRC -= vDU.prc;
            if (instance.nodes[vDU.indxNode].usedPRC < 0) {
                instance.nodes[vDU.indxNode].usedPRC = 0;
            }
            instance.nodes[vDU.indxNode].usedANT -= vDU.ant;
            if (instance.nodes[vDU.indxNode].usedANT < 0) {
                instance.nodes[vDU.indxNode].usedANT = 0;
            }
            instance.nodes[vDU.indxNode].usedPRB -= vDU.prb;
            if (instance.nodes[vDU.indxNode].usedPRB < 0) {
                instance.nodes[vDU.indxNode].usedPRB = 0;
            }
            for (int n : vDU.nears) {
                if (request.vLinks[vDU.nodePosition][n].indxPath == -1) {
                    break;
                }
                vCU = request.vNodes[n];
                if (instance.mPaths[vCU.indxNode][vDU.indxNode] != null && !instance.mPaths[vCU.indxNode][vDU.indxNode].isEmpty()) {
                    for (Link link : instance.mPaths[vCU.indxNode][vDU.indxNode].get(s.data[k][vDU.indx + instance.pathPosition]).getLinks()) {//actualizar ancho de banda para los enlaces
                        link.usedBw -= instance.splits[s.data[k][vDU.indx + instance.splitPosition]].bw;
                        if (link.usedBw < 0) {
                            link.usedBw = 0;
                        }
                    }
                }
            }
        }
    }

    /**
     *
     * @param request
     */
    private void liberarRecursos(Request request) {
        for (VirtualNode vCU : request.vCUs) {
            if (vCU.indxNode == -1) {
                break;
            }
            instance.nodes[vCU.indxNode].usedPRC -= vCU.prc;
            if (instance.nodes[vCU.indxNode].usedPRC < 0) {
                instance.nodes[vCU.indxNode].usedPRC = 0;
            }
        }
    }

    /**
     *
     * @param virtualNode
     * @param physicalNode
     * @param k
     */
    private void asignarRecursos(T s, VirtualNode virtualNode, Node physicalNode, int k) {
        virtualNode.indxNode = physicalNode.nodePosition;//asignacion de indice del nodo fisico
        physicalNode.usedPRC += virtualNode.prc;//aumento del ancho de banda usado
        if (physicalNode.usedPRC > physicalNode.prc) {
            physicalNode.usedPRC = physicalNode.prc;
        }
        s.data[k][virtualNode.indx] = physicalNode.nodePosition;//asignacion Nodo fisico en la matriz
    }

    /**
     *
     * @param vDU The virtual DU.
     * @param DU The physical DU.
     * @param k The index of the request.
     * @param pathIndx The relative path index.
     * @param splitIndx The split index.
     * @param pathSolution
     */
    private void asignarRecursos(T s, VirtualNode vDU, Node DU, int k, int pathIndx, int splitIndx, PathSolution pathSolution) {
        if (!tDUs.contains(DU)) {
            asignarRecursos(s, vDU, DU, k);
            DU.usedANT += vDU.ant;
            if (DU.usedANT > DU.ant) {
                DU.usedANT = DU.ant;
            }
            DU.usedPRB += vDU.prb;
            if (DU.usedPRB > DU.prb) {
                DU.usedPRB = DU.prb;
            }
            tDUs.add(DU);
        }
        s.data[k][vDU.indx + instance.pathPosition] = pathIndx;//asignacion de ruta
        s.data[k][vDU.indx + instance.splitPosition] = splitIndx;//asignacion de split
        for (Link link : pathSolution.getLinks()) {//actualizar ancho de banda usado para cada enlace de la ruta
            link.usedBw += instance.splits[splitIndx].bw;
            if (link.usedBw > link.bw) {
                link.usedBw = link.bw;
            }
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
    private int mejorarSplit(int splitIndx, Link minLink, double pathDelay, double maxDelay) {
        //la ruta es valida, elegir el mejor split
        int i = splitIndx + 1;
        while (i < instance.splits.length && (instance.splits[i].bw + minLink.usedBw) <= minLink.bw && pathDelay <= instance.splits[i].delay && pathDelay <= maxDelay) {
            splitIndx = i;
            i++;
        }
        return splitIndx;
    }

    private void random(T init, List<Request> requestsList, List<Flow> splits, List<Node> DUsList, List<Node> CUsList, List<Flow> functionalSplit) {
        List<Request> requests = new ArrayList<>(requestsList);
        List<PathSolution> paths = new ArrayList<>();
        List<VirtualNode> vCUs = new ArrayList<>();
        List<VirtualNode> vDUs = new ArrayList<>();
        Collections.shuffle(requests, rand);
        int nAccepted = 0, k;
        init.isValid = true;
        for (Request request : requests) {
            k = requestsList.indexOf(request);//identificador de la peticion en el orden original (ingresado)
            init.accepted[k] = true;//marcar la peticion como atendida
            vCUs.clear();
            vCUs.addAll(request.vCUs);
            vDUs.clear();
            vDUs.addAll(request.vDUs);
            Collections.shuffle(vCUs, rand);
            Collections.shuffle(vDUs, rand);
            for (VirtualNode vCU : vCUs) {
                vCU.indxNode = -1;
                Collections.shuffle(CUsList, rand);
                for (Node CU : CUsList) {
                    if (ProblemInstance.validateAssignament(CU, vCU) && !tCUs.add(CU)) {
                        asignarRecursos(init, vCU, CU, k);
                        break;
                    }
                }
                if (vCU.indxNode == -1) {
                    init.accepted[k] = false;//marcar la peticion como no-atendida
                    vCU.indxNode = CUsList.get(0).nodePosition;
                    init.data[k][vCU.indx] = vCU.indxNode;
                }
            }
            tCUs.clear();
            for (VirtualNode vDU : vDUs) {
                vDU.indxNode = -1;
                Collections.shuffle(DUsList, rand);
                for (Node DU : DUsList) {
                    if (!tDUs.contains(DU) && ProblemInstance.validateAssignament(vDU, DU)) {
                        for (int vcu : vDU.nears) {//por cada enlace del vDU
                            request.vLinks[vDU.nodePosition][vcu].indxPath = -1;
                            if (instance.mPaths[DU.nodePosition][request.vNodes[vcu].indxNode] != null && !instance.mPaths[DU.nodePosition][request.vNodes[vcu].indxNode].isEmpty()) {
                                paths.addAll(instance.mPaths[DU.nodePosition][request.vNodes[vcu].indxNode]);
                                Collections.shuffle(paths, rand);
                                for (PathSolution path : paths) {
                                    Collections.shuffle(functionalSplit, rand);
                                    for (Flow split : functionalSplit) {
                                        if (ProblemInstance.validateAssignament(path, split, request.vLinks[vDU.nodePosition][vcu])) {
                                            asignarRecursos(init, vDU, DU, k, instance.mPaths[DU.nodePosition][request.vNodes[vcu].indxNode].indexOf(path), splits.indexOf(split), path);
                                            request.vLinks[vDU.nodePosition][vcu].indxPath = init.data[k][vDU.indx + instance.pathPosition];
                                            break;
                                        }
                                    }
                                    if (request.vLinks[vDU.nodePosition][vcu].indxPath != -1) {
                                        break;
                                    }
                                }
                                paths.clear();
                            }
                            if (request.vLinks[vDU.nodePosition][vcu].indxPath == -1) {
                                liberarRecursos(init, request, k);
                                break;
                            }
                        }
                        if (vDU.indxNode != -1) {
                            break;//se hizo la asignacion .. pasar a la siguiente DU virtual
                        }
                    }
                }
                if (vDU.indxNode == -1) {//no se puede atender la peticion
                    init.accepted[k] = false;//marcar la peticion como no-atendida
                    init.data[k][vDU.indx] = DUsList.get(0).nodePosition;
                    init.data[k][vDU.indx + instance.splitPosition] = splits.indexOf(functionalSplit.get(0));
                }
            }
            tDUs.clear();
            if (init.accepted[k]) {
                nAccepted++;
            }
        }
        init.setnAccepted(nAccepted);
        if (nAccepted < 1 || (Tools.fitAll && nAccepted < instance.requests.length)) {
            init.isValid = false;
        } else {
            instance.cleanResources();
        }
    }

    @Override
    protected T initializeSolution(int n, int m) {
        return (T) new MatrixSolution(n, m);
    }
}

class NodePositionComparator implements Comparator<VirtualNode> {

    @Override
    public int compare(VirtualNode o1, VirtualNode o2) {
        return Integer.compare(o1.indx, o2.indx);
    }
}
