package domain.monoobjective.implementation.greedy.algorithms;

import domain.EvaluationFunction;
import domain.monoobjective.implementation.MatrixSolution;
import domain.monoobjective.implementation.greedy.GreedyAlgorithm;
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
public class PathChoicesGreedyAlgorithm extends GreedyAlgorithm {

    private final Comparator<PathSolution> pathComparator;
    private final Comparator<VirtualLink> virtualLinkComparator;
    private final Comparator<Request> requestComparator;
    private TreeSet<Node> tCUs;
    private TreeSet<Node> tDUs;

    public PathChoicesGreedyAlgorithm(Comparator<PathSolution> pathComparator, Comparator<VirtualLink> virtualLinkComparator, Comparator<Request> requestComparator, ProblemInstance instance, EvaluationFunction<Double, MatrixSolution> fx) {
        super(instance, fx);
        this.pathComparator = pathComparator;
        this.virtualLinkComparator = virtualLinkComparator;
        this.requestComparator = requestComparator;
    }

    @Override
    public MatrixSolution run() {
        Node DU, CU;
        Link minLink;
        boolean isValid;
        VirtualNode vDU, vCU;
        int k, nAccepted = 0, splitIndx;
        tCUs = new TreeSet<>();
        tDUs = new TreeSet<>();
        TreeSet<PathSolution> selected = new TreeSet<>();
        initialize();
        List<PathSolution> paths = new ArrayList<>(instance.mPaths[0][0]);
        final List<Request> requestsList = Arrays.asList(instance.requests);
        List<Request> requests = new ArrayList<>(requestsList);
        Collections.sort(requests, requestComparator);
        best.isValid = true;
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
                    if(!selected.contains(path)) {
                        CU = path.getNodesOfPath().get(0);
                        DU = path.getNodesOfPath().get(path.getNodesOfPath().size() - 1);
                        if (DU.nodeType == 2 || CU.nodeType == 1) {
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
                            if (!ProblemInstance.validateAssingament(vDU, DU)) {
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
                            int indx = mejorarSplit(splitIndx, minLink, path.getDelay(), vLink.maxDelay);
                            asignarRecursos(best.data[k], vLink, path, vCU, vDU, CU, DU, indx);
                            selected.add(path);
                            if (Tools.ECHO) {
                                System.out.println("[" + k + "] Accepted. " + instance.splits[indx].toString() + "-" + request.vLinks[vDU.nodePosition][vCU.nodePosition].toString() + " -> " + path.toString());
                            }
                            break;
                        } else if (Tools.ECHO) {
                            System.out.println("[" + k + "][NoResources] " + instance.splits[splitIndx].toString() + "-" + request.vLinks[vDU.nodePosition][vCU.nodePosition].toString() + " -> " + path.toString());
                        }
                    }
                }
                if (vLink.indxPath == -1) {//no es una solucion valida
                    best.accepted[k] = false;//marcar como no-aceptada
                    if (Tools.ECHO) {
                        System.out.println("[" + k + "] Rejected.");
                    }
                    break;
                }
            }
            if (best.accepted[k]) {
                nAccepted++;
                tCUs.clear();
                tDUs.clear();
            } else {
                liberarRecursos(k, instance.mPaths[0][0]);
                if (!tCUs.isEmpty()) {
                    if (Tools.ECHO) {
                        System.out.println("Warning CUs not empty !!\ntree_CUs clear !");
                    }
                    tCUs.clear();
                }
                if (!tDUs.isEmpty()) {
                    if (Tools.ECHO) {
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
        if (nAccepted < 1) {
            best.isValid = false;
            return null;
        } else {
            best.gn = instance.validate(best);
            fx.evaluate(best);
        }
        return best;
    }

    private void liberarRecursos(int k, List<PathSolution> paths) {
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
            for (Link link : paths.get(vLink.indxPath).getLinks()) {
                link.usedBw -= instance.splits[best.data[k][vDU.indx + 1]].bw;
                if (link.usedBw < 0) {
                    link.usedBw = 0;
                }
            }
        }
    }

    private void asignarRecursos(Integer[] data, VirtualLink vLink, PathSolution pathSolution, VirtualNode vCU, VirtualNode vDU, Node CU, Node DU, int splitIndx) {
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
        vLink.indxPath = instance.mPaths[0][0].indexOf(pathSolution);
        for (Link link : pathSolution.getLinks()) {
            link.usedBw += instance.splits[splitIndx].bw;
            if (link.usedBw > link.bw) {
                link.usedBw = link.bw;
            }
        }
        data[vDU.indx] = vLink.indxPath;
        data[vDU.indx + 1] = splitIndx;
    }
}
