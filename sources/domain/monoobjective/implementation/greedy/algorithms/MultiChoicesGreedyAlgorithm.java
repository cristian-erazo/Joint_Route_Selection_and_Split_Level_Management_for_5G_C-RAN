package domain.monoobjective.implementation.greedy.algorithms;

import domain.EvaluationFunction;
import domain.monoobjective.implementation.MatrixSolution;
import domain.monoobjective.implementation.greedy.GreedyAlgorithm;
import domain.monoobjective.implementation.greedy.comparators.NodeComparator;
import domain.paths.PathSolution;
import domain.problem.ProblemInstance;
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
import java.util.TreeSet;

/**
 *
 * @author cristian.erazo@cinvestav.mx
 */
public class MultiChoicesGreedyAlgorithm extends GreedyAlgorithm {

    private final Comparator<Request> requestsComparator;
    private final Comparator<VirtualNode> vCUsComparator;
    private final Comparator<VirtualNode> vDUsComparator;
    private final Comparator<Node> CUsComparator;
    private final NodeComparator DUsComparator;
    private final Comparator<PathSolution> pathsComparator;
    private TreeSet<Node> tCUs;
    private TreeSet<Node> tDUs;

    public MultiChoicesGreedyAlgorithm(Comparator<Request> requestsComparator, Comparator<VirtualNode> vCUsComparator, Comparator<VirtualNode> vDUsComparator, Comparator<Node> CUsComparator, NodeComparator DUsComparator, Comparator<PathSolution> pathsComparator, ProblemInstance instance, EvaluationFunction<Double, MatrixSolution> fx) {
        super(instance, fx);
        this.requestsComparator = requestsComparator;
        this.vCUsComparator = vCUsComparator;
        this.vDUsComparator = vDUsComparator;
        this.CUsComparator = CUsComparator;
        this.DUsComparator = DUsComparator;
        this.pathsComparator = pathsComparator;
    }

    @Override
    public MatrixSolution run() {
        Link minLink;
        boolean isValid;
        int nAccepted = 0, k, splitIndx;
        tCUs = new TreeSet<>();
        tDUs = new TreeSet<>();
        initialize();
        List<PathSolution> paths = new ArrayList<>();
        final List<Request> requestsList = Arrays.asList(instance.requests);
        List<Request> requests = new ArrayList<>(requestsList);
        Collections.sort(requests, requestsComparator);
        best.isValid = true;
        for (Request request : requests) {//para cada peticion...
            k = requestsList.indexOf(request);//identificador de la peticion en el orden original (ingresado)
            best.accepted[k] = true;//marcar la peticion como atendida
            Collections.sort(request.vCUs, vCUsComparator);
            isValid = true;
            for (VirtualNode vCU : request.vCUs) {//asignar CUs fisicos
                vCU.indxNode = -1;
                Collections.sort(instance.CUs, CUsComparator);
                for (Node CU : instance.CUs) {
                    if (ProblemInstance.validateAssignament(CU, vCU) && tCUs.add(CU)) {
                        asignarRecursos(vCU, CU, k);
                        if (Tools.ECHO) {
                            System.out.println("CU assigned " + vCU.toString() + " -> " + CU.toString());
                        }
                        break;
                    } else if (Tools.ECHO) {
                        System.out.println("[NoResources] " + vCU.toString() + " -> " + CU.toString());
                    }
                }
                if (vCU.indxNode == -1) {
                    best.accepted[k] = false;//marcar la peticion como no-atendida
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
                    Collections.sort(instance.DUs, DUsComparator);
                    for (Node DU : instance.DUs) {
                        if (!tDUs.contains(DU)) {
                            for (int vcu : vDU.nears) {//por cada enlace del vDU
                                splitIndx = instance.getMinSplitIndex(request.vLinks[vDU.nodePosition][vcu]);
                                if (splitIndx == -1) {
                                    break;
                                }
                                if (ProblemInstance.validateAssingament(vDU, DU) && instance.mPaths[DU.nodePosition][request.vNodes[vcu].indxNode] != null && !instance.mPaths[DU.nodePosition][request.vNodes[vcu].indxNode].isEmpty()) {
                                    paths.addAll(instance.mPaths[DU.nodePosition][request.vNodes[vcu].indxNode]);
                                    Collections.sort(paths, pathsComparator);
                                    //validar la posible ruta y elegir el mejor split
                                    for (PathSolution path : paths) {
                                        request.vLinks[vDU.nodePosition][vcu].indxPath = -1;
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
                                                asignarRecursos(vDU, DU, k, instance.mPaths[DU.nodePosition][request.vNodes[vcu].indxNode].indexOf(path), mejorarSplit(splitIndx, minLink, path.getDelay(), request.vLinks[vDU.nodePosition][vcu].maxDelay), path);
                                                request.vLinks[vDU.nodePosition][vcu].indxPath = best.data[k][vDU.indx + 1];
                                                if (Tools.ECHO) {
                                                    System.out.println("vDU " + vDU.indxNode + " assigned to DU " + DU.nodePosition + ".");
                                                }
                                                if (!tDUs.add(DU)) {
                                                    System.out.println("¡¡ An error has occurred !!");
                                                }
                                                break;//se hizo la asignacion, dejar de buscar rutas o splits
                                            }
                                        } else if (Tools.ECHO) {
                                            System.out.println("[" + k + "][NoResources] " + instance.splits[splitIndx].toString() + "-" + request.vLinks[vDU.nodePosition][vcu].toString() + " -> " + path.toString());
                                        }
                                        if (request.vLinks[vDU.nodePosition][vcu].indxPath != -1) {
                                            break;
                                        }
                                    }
                                    paths.clear();
//                                    if (vDU.indxNode != -1) {
//                                    break;//se hizo la asignacion .. pasar a la siguiente DU virtual
//                                    }
                                } else if (Tools.ECHO) {
                                    System.out.println("[NoResources] " + vDU.toString() + " -> " + DU.toString());
                                }
                            }
                            if (vDU.indxNode != -1) {
                                break;//se hizo la asignacion .. pasar a la siguiente DU virtual
                            }
                        }
                    }
                    if (vDU.indxNode == -1) {//no se puede atender la peticion
                        best.accepted[k] = false;//marcar la peticion como no-atendida
                        break;//pasar a la siguiente peticion
                    }
                }
                tDUs.clear();
                if (best.accepted[k]) {//solucion atendida?
                    nAccepted++;
                    if (Tools.ECHO) {
                        System.out.println("[" + k + "] Accepted.");
                    }
                } else {//no fue atendida, liberar recursos..
                    liberarRecursos(request, k);
                    if (Tools.ECHO) {
                        System.out.println("[" + k + "] Rejected.");
                    }
                    if (Tools.fitAll) {
                        return null;
                    }
                }
            }
        }
        best.setnAccepted(nAccepted);
        if (nAccepted < 1) {
            best.isValid = false;
            return null;
        } else {
            best.gn = instance.validate(best);
            fx.evaluate(best);
        }
        return best;
    }

    /**
     *
     * @param request
     * @param k
     */
    private void liberarRecursos(Request request, int k) {
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
                vCU = request.vNodes[n];
                if (instance.mPaths[vCU.indxNode][vDU.indxNode] != null && !instance.mPaths[vCU.indxNode][vDU.indxNode].isEmpty()) {
                    for (Link link : instance.mPaths[vCU.indxNode][vDU.indxNode].get(best.data[k][vDU.indx + instance.pathPosition]).getLinks()) {//actualizar ancho de banda para los enlaces
                        link.usedBw -= instance.splits[best.data[k][vDU.indx + instance.splitPosition]].bw;
                        if (link.usedBw < 0) {
                            link.usedBw = 0;
                        }
                    }
                }
            }
        }
        liberarRecursos(request);
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
    private void asignarRecursos(VirtualNode virtualNode, Node physicalNode, int k) {
        virtualNode.indxNode = physicalNode.nodePosition;//asignacion de indice del nodo fisico
        physicalNode.usedPRC += virtualNode.prc;//aumento del ancho de banda usado
        if (physicalNode.usedPRC > physicalNode.prc) {
            physicalNode.usedPRC = physicalNode.prc;
        }
        best.data[k][virtualNode.indx] = physicalNode.nodePosition;//asignacion Nodo fisico en la matriz
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
    private void asignarRecursos(VirtualNode vDU, Node DU, int k, int pathIndx, int splitIndx, PathSolution pathSolution) {
        asignarRecursos(vDU, DU, k);
        DU.usedANT += vDU.ant;
        if (DU.usedANT > DU.ant) {
            DU.usedANT = DU.ant;
        }
        DU.usedPRB += vDU.prb;
        if (DU.usedPRB > DU.prb) {
            DU.usedPRB = DU.prb;
        }
        best.data[k][vDU.indx + instance.pathPosition] = pathIndx;//asignacion de ruta
        best.data[k][vDU.indx + instance.splitPosition] = splitIndx;//asignacion de split
        for (Link link : pathSolution.getLinks()) {//actualizar ancho de banda usado para cada enlace de la ruta
            link.usedBw += instance.splits[splitIndx].bw;
            if (link.usedBw > link.bw) {
                link.usedBw = link.bw;
            }
        }
    }
}
