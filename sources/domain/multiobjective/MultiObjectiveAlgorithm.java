package domain.multiobjective;

import domain.Algorithm;

/**
 *
 * @author cristian.erazo@cinvestav.mx
 * @param <E>
 * @param <T>
 */
public interface MultiObjectiveAlgorithm<E, T extends Comparable<T>> extends Algorithm {

    @Override
    public MultiObjectiveSolution<E, T> run();
}
