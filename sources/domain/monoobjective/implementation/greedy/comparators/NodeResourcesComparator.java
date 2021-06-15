package domain.monoobjective.implementation.greedy.comparators;

import domain.problem.graph.Node;

/**
 *
 * @author cristian.erazo@cinvestav.mx
 */
public class NodeResourcesComparator extends NodeComparator {

    @Override
    public int compare(Node x, Node y) {
        if (x.nodeType == 2 && y.nodeType == 2) {//compare CU
            return Integer.compare(-1 * (x.prc - x.usedPRC), -1 * (y.prc - y.usedPRC));
        }
        if (x.nodeType == 1 && y.nodeType == 1) {//compare DU
            return Integer.compare(-1 * (x.ant - x.usedANT) * (x.prb - x.usedPRB) * (x.prc - x.usedPRC), -1 * (y.ant - y.usedANT) * (y.prb - y.usedPRB) * (y.prc - y.usedPRC));
        }
        //compare other nodes by position
        return x.compareTo(y);
    }
}
