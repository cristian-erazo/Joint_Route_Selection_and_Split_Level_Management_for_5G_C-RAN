package domain.monoobjective.implementation.greedy.comparators;

import domain.paths.PathSolution;
import java.util.Comparator;

/**
 *
 * @author cristian.erazo@cinvestav.mx
 */
public class PathComparator implements Comparator<PathSolution> {

    @Override
    public int compare(PathSolution p, PathSolution q) {
        return Double.compare(-1 * p.getMinBw() /* p.getDelay()*/, -1 * q.getMinBw() /* q.getDelay()*/);
    }
}
