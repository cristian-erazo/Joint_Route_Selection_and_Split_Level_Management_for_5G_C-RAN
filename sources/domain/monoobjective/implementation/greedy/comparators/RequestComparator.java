package domain.monoobjective.implementation.greedy.comparators;

import domain.problem.virtual.Request;
import java.util.Comparator;

/**
 *
 * @author cristian.erazo@cinvestav.mx
 */
public class RequestComparator implements Comparator<Request> {

    @Override
    public int compare(Request p, Request q) {
        return Integer.compare(-1 * p.vCUs.size() * p.vDUs.size() * p.virtualLinks.size(), -1 * q.vCUs.size() * q.vDUs.size() * q.virtualLinks.size());
    }
}
