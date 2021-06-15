package domain.monoobjective.implementation.greedy.comparators;

import domain.paths.PathSolution;
import java.util.Comparator;

/**
 *
 * @author cristian.erazo@cinvestav.mx
 */
public class PathComparator implements Comparator<PathSolution> {

    //private final double MAX = Integer.MAX_VALUE - 1;

    @Override
    public int compare(PathSolution p, PathSolution q) {
        return Double.compare(/*p.getMinBw() * */p.getDelay(),/* q.getMinBw() * */ q.getDelay());
    }
}
