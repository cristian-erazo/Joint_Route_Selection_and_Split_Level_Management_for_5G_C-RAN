package domain.monoobjective.implementation.functions;

import domain.EvaluationFunction;
import domain.monoobjective.implementation.MatrixSolution;
import domain.monoobjective.implementation.ObjectiveFunction;
import domain.paths.PathSolution;
import domain.problem.ProblemInstance;
import domain.problem.graph.Node;
import domain.problem.virtual.VirtualLink;
import domain.problem.virtual.VirtualNode;
import java.util.TreeSet;

/**
 *
 * @author cristian.erazo@cinvestav.mx
 */
public class LessLatencyAndNodesUsage extends ObjectiveFunction {

    private TreeSet<Node> usedNodes;

    public LessLatencyAndNodesUsage(ProblemInstance instance, double w1, double w2, double w3, boolean isMaximization) {
        super(instance, w1, w2, w3, isMaximization);
        usedNodes = new TreeSet<>();
    }

    @Override
    protected Double eval(MatrixSolution s) {
        super.eval(s);
        int numUsedNodes, numAccepted = 0;
        double latency = 0, A = 100000/*10^5*/, alpha = 10000000/*10^7*/;
        for (int i = 0; i < instance.requests.length; i++) {
            if (s.accepted[i]) {
                numAccepted++;
                for (VirtualLink vLink : instance.requests[i].virtualLinks) {
                    if (instance.isFullyRepresentated()) {
                        if (instance.requests[i].vNodes[vLink.source].indxNode != -1
                                && instance.requests[i].vNodes[vLink.destination].indxNode != -1) {
                            VirtualNode vDU = instance.requests[i].vNodes[vLink.source];
                            VirtualNode vCU = instance.requests[i].vNodes[vLink.destination];
                            if (vDU.nodeType == 2) {
                                vCU = vDU;
                                vDU = instance.requests[i].vNodes[vLink.destination];
                            }
                            Node du = instance.nodes[vDU.indxNode];
                            Node cu = instance.nodes[vCU.indxNode];
                            usedNodes.add(du);
                            usedNodes.add(cu);
                            if (instance.mPaths[du.nodePosition][cu.nodePosition] != null && vLink.indxPath != -1 && vLink.indxPath < instance.mPaths[du.nodePosition][cu.nodePosition].size()) {
                                latency += instance.mPaths[du.nodePosition][cu.nodePosition].get(vLink.indxPath).getDelay();
                            }
                        }
                    } else {
                        if (vLink.indxPath != -1) {
                            PathSolution p = instance.mPaths[0][0].get(vLink.indxPath);
                            usedNodes.add(p.getNodesOfPath().get(0));
                            usedNodes.add(p.getNodesOfPath().get(p.getLength() - 1));
                            latency += p.getDelay();
                        }
                    }
                }
            }
        }
        numUsedNodes = usedNodes.size();
        usedNodes.clear();
        return (double) -((A * numUsedNodes + latency) + alpha * (instance.requests.length - numAccepted));
    }

    @Override
    public boolean isBetter(MatrixSolution a, MatrixSolution b) {
        double A = evaluate(a), B = evaluate(b);
        if (a.gn > 0) {
            A *= a.gn;
        }
        if (b.gn > 0) {
            B *= b.gn;
        }
        if (isMaximization) {
            if (!a.isValid || a.gn > 0) {
                A += -999999999;
            }
            if (!b.isValid || b.gn > 0) {
                B += -999999999;
            }
            return A > B;
        } else {
            if (!a.isValid || a.gn > 0) {
                A += 999999999;
            }
            if (!b.isValid || b.gn > 0) {
                B += 999999999;
            }
            return A < B;
        }
    }

    @Override
    public EvaluationFunction<Double, MatrixSolution> copy(ProblemInstance p) {
        return new LessLatencyAndNodesUsage(p, w1, w2, w3, isMaximization);
    }
}
