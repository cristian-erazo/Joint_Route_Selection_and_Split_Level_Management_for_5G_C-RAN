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
public class FullChoicesGreedyAlgorithm extends GreedyAlgorithm {

    private final Comparator<Request> requestsComparator;
    private final Comparator<VirtualNode> vCUsComparator;
    private final Comparator<VirtualNode> vDUsComparator;
    private final Comparator<Node> CUsComparator;
    private final NodeComparator DUsComparator;
    private final Comparator<PathSolution> pathsComparator;
    private TreeSet<Node> tCUs;
    private TreeSet<Node> tDUs;

    public FullChoicesGreedyAlgorithm(Comparator<Request> requestsComparator, Comparator<VirtualNode> vCUsComparator, Comparator<VirtualNode> vDUsComparator, Comparator<Node> CUsComparator, NodeComparator DUsComparator, Comparator<PathSolution> pathsComparator, ProblemInstance instance, EvaluationFunction<Double, MatrixSolution> fx) {
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
        List<Request> requestsList = Arrays.asList(instance.requests);
        List<Request> requests = new ArrayList<>(requestsList);
        Collections.sort(requests, requestsComparator);
        for (Request request : requests) {
            k = requestsList.indexOf(request);//identificador de la peticion en el orden original (ingresado)
            best.accepted[k] = true;//marcar la peticion como atendida
            Collections.sort(request.vDUs, vDUsComparator);
            isValid = true;
            for (VirtualNode vDU : request.vDUs) {
                vDU.indxNode = -1;
                DUsComparator.setBase(vDU);
                Collections.sort(instance.DUs, DUsComparator);
                for (Node DU : instance.DUs) {
                    if (!tDUs.contains(DU) && ProblemInstance.validateAssingament(vDU, DU)) {
                        asignarRecursos(vDU, DU, k);
                        break;
                    }
                }
                if (vDU.indxNode == -1) {
                    best.accepted[k] = false;//marcar la peticion como no-atendida
                    isValid = false;
                    break;//pasar a la siguiente peticion
                }
            }
            if (isValid) {
                Collections.sort(request.vCUs, vCUsComparator);
                for (VirtualNode vCU : request.vCUs) {
                    Collections.sort(instance.CUs, CUsComparator);
                    for (Node CU : instance.CUs) {
                        if (!tCUs.contains(CU) && ProblemInstance.validateAssignament(CU, vCU)) {
                            for (Integer vdu : vCU.nears) {
                                splitIndx = instance.getMinSplitIndex(request.vLinks[vdu][vCU.nodePosition]);
                                if (splitIndx == -1) {
                                    break;
                                }
                            }
                        }
                    }
                    if (vCU.indxNode == -1) {
                        best.accepted[k] = false;
                        break;
                    }
                }
            } else {
                liberarRecursos(request);
            }
        }
        best.setnAccepted(nAccepted);
        instance.cleanResources();
        if (nAccepted < 1) {
            best.isValid = false;
            return null;
        } else {
            best.gn = instance.validate(best);
            fx.evaluate(best);
        }
        return best;
    }

    private void asignarRecursos(VirtualNode vDU, Node DU, int k) {
        vDU.indxNode = DU.nodePosition;
        DU.usedPRC += vDU.prc;
        if (DU.usedPRC > DU.prc) {
            DU.usedPRC = DU.prc;
        }
        best.data[k][vDU.indx] = DU.nodePosition;
        tDUs.add(DU);
    }

    /**
     *
     * @param request
     */
    private void liberarRecursos(Request request) {
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
        }
    }
}
