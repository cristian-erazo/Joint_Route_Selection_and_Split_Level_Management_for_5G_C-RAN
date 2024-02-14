package domain.multiobjective;

import domain.Algorithm;
import java.util.List;

/**
 *
 * @author cristian.erazo@cinvestav.mx
 * @param <E>
 * @param <T>
 */
public interface MultiObjectiveAlgorithm<E, T extends Comparable<T>> extends Algorithm {

    @Override
    public List<MultiObjectiveSolution> run();
}
