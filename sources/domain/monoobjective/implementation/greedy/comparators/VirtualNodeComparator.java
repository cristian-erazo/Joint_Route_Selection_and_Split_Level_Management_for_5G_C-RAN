package domain.monoobjective.implementation.greedy.comparators;

import domain.problem.virtual.VirtualNode;
import java.util.Comparator;

/**
 *
 * @author cristian.erazo@cinvestav.mx
 */
public class VirtualNodeComparator implements Comparator<VirtualNode> {

    public VirtualNodeComparator() {
    }

    @Override
    public int compare(VirtualNode x, VirtualNode y) {
        if (x.nodeType == 2 && y.nodeType == 2) {//compare virtual CU
            return Integer.compare(-1 * x.prc, -1 * y.prc);
        }
        if (x.nodeType == 1 && y.nodeType == 1) {//compare virtual DU
            return Integer.compare(-1 * (x.ant + x.prb + x.prc), -1 * (y.ant + y.prb + y.prc));
        }
        //compare other nodes by position
        return x.compareTo(y);
    }

}
