package domain.monoobjective;

import domain.Algorithm;

/**
 *
 * @author cristian.erazo@cinvestav.mx
 * @param <E> Data type.
 * @param <T> Objective type.
 */
public interface MonoObjectiveAlgorithm<E, T extends Comparable<T>> extends Algorithm {

    /**
     * This function allows you to start the execution of a mono-objective
     * algorithm.
     *
     * @return The mono-objective solution obtained at the end of the algorithm
     * execution.
     */
    @Override
    public MonoObjectiveSolution<E, T> run();
}
