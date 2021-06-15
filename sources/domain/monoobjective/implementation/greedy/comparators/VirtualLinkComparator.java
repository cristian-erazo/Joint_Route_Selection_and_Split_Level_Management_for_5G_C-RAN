package domain.monoobjective.implementation.greedy.comparators;

import domain.problem.virtual.VirtualLink;
import java.util.Comparator;

/**
 *
 * @author cristian.erazo@cinvestav.mx
 */
public class VirtualLinkComparator implements Comparator<VirtualLink> {

    @Override
    public int compare(VirtualLink v, VirtualLink w) {
        return Double.compare(-1 * v.bw * v.maxDelay, -1 * w.bw * w.maxDelay);
    }
}
