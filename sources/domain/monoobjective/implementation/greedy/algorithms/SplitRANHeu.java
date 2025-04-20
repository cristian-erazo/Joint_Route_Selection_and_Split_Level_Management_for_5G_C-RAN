package domain.monoobjective.implementation.greedy.algorithms;

import domain.EvaluationFunction;
import domain.monoobjective.implementation.MatrixSolution;
import domain.monoobjective.implementation.greedy.GreedyAlgorithm;
import domain.monoobjective.implementation.greedy.comparators.NodeComparator;
import domain.monoobjective.implementation.greedy.comparators.NodeDistanceComparator;
import domain.monoobjective.implementation.greedy.comparators.RequestComparator;
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
import java.util.List;

/**
 *
 * @author cristian.erazo@cinvestav.mx
 */
public class SplitRANHeu extends GreedyAlgorithm {

    private List<Request> req;
    private List<Request> Q;
    private List<Node> CUs;
    private List<Node> DUs;
    private List<Link> usedLinks;

    public SplitRANHeu(ProblemInstance instance, EvaluationFunction<Double, MatrixSolution> fx) {
        super(instance, fx);
        req = Arrays.asList(instance.requests);
        Q = new ArrayList<>(instance.requests.length);
        CUs = new ArrayList<>(instance.CUs.size());
        DUs = new ArrayList<>(instance.DUs.size());
        Q.addAll(req);
        CUs.addAll(instance.CUs);
        DUs.addAll(instance.DUs);
        usedLinks = new ArrayList<>((int) (instance.nLinks * 0.1));
    }

    @Override
    public MatrixSolution run() {
        int nAccepted = 0;
        initialize();
        best.it = 1;
        best.isValid = true;
        Q.sort(new RequestComparator());
        for (Request request : Q) {
            int q = req.indexOf(request);
            best.accepted[q] = true;
            int s = instance.splits.length - 1;
            while (s >= 0) {
                tCUs.clear();
                boolean assigned = true;
                for (VirtualNode vCU : request.vCUs) {
                    if (!assignCU(q, vCU)) {
                        assigned = false;
                        break;
                    }
                }
                tDUs.clear();
                if (assigned) {
                    for (VirtualNode vDU : request.vDUs) {
                        if (!assignDU(q, vDU)) {
                            assigned = false;
                            break;
                        }
                    }
                    if (assigned) {
                        for (VirtualLink vLink : request.virtualLinks) {
                            if (!ProblemInstance.validateAssignament(vLink, instance.splits[s])
                                    || !assignPath(q, vLink, request.vNodes[vLink.source], request.vNodes[vLink.destination], s)) {
                                assigned = false;
                                break;
                            } else {
                                best.data[q][vLink.indx + 1] = s;
                            }
                        }
                    }
                }
                if (assigned) {
                    best.accepted[q] = true;
                    nAccepted++;
                    if (Tools.echo) {
                        System.out.println(String.format("[%d] accepted [f_%d]", q, s));
                    }
                    break;
                } else {
                    clearAssignments(instance.requests[q], instance.splits[s]);
                }
                s--;
            }
            if (s < 0) {
                best.accepted[q] = false;
                if (Tools.echo) {
                    System.out.println(String.format("[%d] rejected [f_%d]", q, s));
                }
            }
        }
        best.setnAccepted(nAccepted);
        best.gn = instance.validate(best);
        fx.evaluate(best);
        if (Tools.echo) {
            System.out.println(String.format("result: %s", best));
        }
        return best;
    }

    private boolean assignCU(int q, VirtualNode vCU) {
        vCU.indxNode = best.data[q][vCU.indx] = -1;
        for (Node CU : CUs) {
            if (!tCUs.contains(CU) && ProblemInstance.validateAssignament(CU, vCU)) {
                assignResources(CU, vCU, q);
                return true;
            }
        }
        return false;
    }

    private boolean assignDU(int q, VirtualNode vDU) {
        vDU.indxNode = best.data[q][vDU.indx] = -1;
        Node selected = null;
        NodeComparator comparator = new NodeDistanceComparator();
        comparator.setBase(vDU);
        DUs.sort(comparator);
        for (Node DU : DUs) {
            if (!tDUs.contains(DU) && ProblemInstance.validateAssignament(vDU, DU)) {
                selected = DU;
                break;
            }
        }
        if (selected == null) {
            return false;
        } else {
            assignResources(selected, vDU, q);
            return true;
        }
    }

    private void assignResources(Node node, VirtualNode vNode, int q) {
        vNode.indxNode = best.data[q][vNode.indx] = node.nodePosition;
        if (vNode.nodeType == 1) {// DU node
            tDUs.add(node);
            node.usedPRC += vNode.prc;
            if (node.usedPRC > node.prc) {
                if (Tools.echo) {
                    System.out.println(String.format("[%d] DU PRC (%s) -> (%s)", q, vNode, node));
                }
                node.usedPRC = node.prc;
            }
            node.usedANT += vNode.ant;
            if (node.usedANT > node.ant) {
                if (Tools.echo) {
                    System.out.println(String.format("[%d] DU ANT (%s) -> (%s)", q, vNode, node));
                }
                node.usedANT = node.ant;
            }
            node.usedPRB += vNode.prb;
            if (node.usedPRB > node.prb) {
                if (Tools.echo) {
                    System.out.println(String.format("[%d] DU PRB (%s) -> (%s)", q, vNode, node));
                }
                node.usedPRB = node.prb;
            }
        } else if (vNode.nodeType == 2) {// CU node
            tCUs.add(node);
            node.usedPRC += vNode.prc;
            if (node.usedPRC > node.prc) {
                if (Tools.echo) {
                    System.out.println(String.format("[%d] CU PRC (%s) -> (%s)", q, vNode, node));
                }
                node.usedPRC = node.prc;
            }
        }
    }

    private boolean assignPath(int q, VirtualLink vLink, VirtualNode v1, VirtualNode v2, int splitIndx) {
        best.data[q][vLink.indx] = vLink.indxPath = -1;
        if (instance.mPaths[v1.indxNode][v2.indxNode] != null) {
            for (int i = 0; i < instance.mPaths[v1.indxNode][v2.indxNode].size(); i++) {
                if (ProblemInstance.validateAssignament(instance.mPaths[v1.indxNode][v2.indxNode].get(i), instance.splits[splitIndx], vLink)) {
                    assignResources(instance.mPaths[v1.indxNode][v2.indxNode].get(i), vLink, q, i, splitIndx);
                    return true;
                }// flow splitting unnecessary
                /* else if (i == 0 && splitTraffic(v1.indxNode, v2.indxNode, vLink, q, i, splitIndx)) {// split the traffic
                return true;
            }*/
            }
        }
        return false;
    }

    private void assignResources(PathSolution pathSolution, VirtualLink vLink, int q, int pathIndx, int splitIndx) {
        best.data[q][vLink.indx] = pathIndx;
        vLink.indxPath = pathIndx;
        vLink.split = splitIndx;
        for (Link link : pathSolution.getLinks()) {
            link.usedBw += instance.splits[splitIndx].bw;
            if (link.usedBw > link.bw) {
                if (Tools.echo) {
                    System.out.println(String.format("[%d] Link BW (%s) -> (%s)", q, vLink, link));
                }
                link.usedBw = link.bw;
            }
        }
    }

    private boolean splitTraffic(int idx, int idy, VirtualLink vLink, int q, int pathIndx, int splitIndx) {
        best.data[q][vLink.indx] = pathIndx;
        vLink.indxPath = pathIndx;
        vLink.split = splitIndx;
        if (instance.nodes[idx].nodeType == 2) {
            int aux = idx;
            idx = idy;
            idy = aux;
        }
        List<PathSolution> allPaths = instance.allPathsFromDUs.get(idx).get(idy);
        double partBW = instance.splits[splitIndx].bw / allPaths.size();
        for (int i = pathIndx; i < allPaths.size(); i++) {
            for (Link link : allPaths.get(i).getLinks()) {
                if (link.usedBw + partBW <= link.bw) {
                    link.usedBw += partBW;
                    usedLinks.add(link);
                } else {
                    for (Link usedLink : usedLinks) {
                        usedLink.usedBw -= partBW;
                        if (usedLink.usedBw < 0) {
                            if (Tools.echo) {
                                System.out.println(String.format("[%d] Link part BW [%.1f] (%s) -> (%s)", q, partBW, vLink, link));
                            }
                            link.usedBw = 0;
                        }
                    }
                    usedLinks.clear();
                    return false;
                }
            }
        }
        usedLinks.clear();
        return true;
    }

    private void clearAssignments(Request q, Flow split) {
        for (VirtualNode vCU : q.vCUs) {
            if (vCU.indxNode != -1) {
                instance.nodes[vCU.indxNode].usedPRC -= vCU.prc;
                if (instance.nodes[vCU.indxNode].usedPRC < 0) {
                    System.out.println("Warning!! CU PRC less equal to zero");
                }
            }
        }
        for (VirtualNode vDU : q.vDUs) {
            if (vDU.indxNode != -1) {
                instance.nodes[vDU.indxNode].usedPRC -= vDU.prc;
                if (instance.nodes[vDU.indxNode].usedPRC < 0) {
                    if (Tools.echo) {
                        System.out.println("Warning!! DU PRC less equal to zero");
                    }
                    instance.nodes[vDU.indxNode].usedPRC = 0;
                }
                instance.nodes[vDU.indxNode].usedANT -= vDU.ant;
                if (instance.nodes[vDU.indxNode].usedANT < 0) {
                    if (Tools.echo) {
                        System.out.println("Warning!! DU ANT less equal to zero");
                    }
                    instance.nodes[vDU.indxNode].usedANT = 0;
                }
                instance.nodes[vDU.indxNode].usedPRB -= vDU.prb;
                if (instance.nodes[vDU.indxNode].usedPRB < 0) {
                    if (Tools.echo) {
                        System.out.println("Warning!! DU PRB less equal to zero");
                    }
                    instance.nodes[vDU.indxNode].usedPRB = 0;
                }
            }
        }
        for (VirtualLink vLink : q.virtualLinks) {
            if (vLink.indxPath != -1) {
                int n1 = q.vNodes[vLink.source].indxNode;
                int n2 = q.vNodes[vLink.destination].indxNode;
                for (Link link : instance.mPaths[n1][n2].get(vLink.indxPath).getLinks()) {
                    link.usedBw -= split.bw;
                    if (link.usedBw < 0) {
                        if (Tools.echo) {
                            System.out.println("Warning!! LINK BW less than zero");
                        }
                        link.usedBw = 0;
                    }
                }
            }
        }
    }
}
