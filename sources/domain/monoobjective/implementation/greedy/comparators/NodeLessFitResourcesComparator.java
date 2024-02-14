package domain.monoobjective.implementation.greedy.comparators;

import domain.problem.graph.Node;

/**
 *
 * @author cristian.erazo@cinvestav.mx
 */
public class NodeLessFitResourcesComparator extends NodeComparator {

    @Override
    public int compare(Node x, Node y) {
        if (x.nodeType == 2 && y.nodeType == 2) {//compare CU
            return Integer.compare(-1 * (x.prc - x.usedPRC - base.prc), -1 * (y.prc - y.usedPRC - base.prc));
        }
        if (x.nodeType == 1 && y.nodeType == 1) {//compare DU
            return Integer.compare(-1 * (x.ant - x.usedANT - base.ant + x.prb - x.usedPRB - base.prb + x.prc - x.usedPRC - base.prc), -1 * (y.ant - y.usedANT - base.ant + y.prb - y.usedPRB - base.prb + y.prc - y.usedPRC - base.prc));
        }
        //compare other nodes by list-index
        return x.compareTo(y);
    }

}
