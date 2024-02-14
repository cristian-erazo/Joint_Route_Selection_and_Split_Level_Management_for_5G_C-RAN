package domain.monoobjective.implementation.functions;

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
public class NodesUsage extends ObjectiveFunction {

    private TreeSet<Node> usedNodes;

    public NodesUsage(ProblemInstance instance, double w1, double w2, double w3, boolean isMaximization) {
        super(instance, w1, w2, w3, isMaximization);
        usedNodes = new TreeSet<>();
    }

    @Override
    protected Double eval(MatrixSolution s) {
        super.eval(s);
        int virtualizedFunctions = 0, numUsedNodes;
        for (int i = 0; i < instance.requests.length; i++) {
            if (s.accepted[i]) {
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
                            usedNodes.add(instance.nodes[vDU.indxNode]);
                            usedNodes.add(instance.nodes[vCU.indxNode]);
                            if (s.data[i][vDU.indx + instance.splitPosition] >= 0) {
                                virtualizedFunctions += s.data[i][vDU.indx + instance.splitPosition];
                            }
                            // instance.splits[s.data[i][vDU.indx + instance.splitPosition]]
                        }
                    } else {
                        if (vLink.indxPath != -1) {
                            PathSolution p = instance.mPaths[0][0].get(vLink.indxPath);
                            usedNodes.add(p.getNodesOfPath().get(0));
                            usedNodes.add(p.getNodesOfPath().get(p.getLength() - 1));
                            if (s.data[i][vLink.indx + instance.splitPosition] >= 0) {
                                virtualizedFunctions += s.data[i][vLink.indx + instance.splitPosition];
                            }
                        }
                    }
                }
            }
        }
        numUsedNodes = usedNodes.size();
        usedNodes.clear();
        return (double) (-numUsedNodes + virtualizedFunctions);
    }

}
