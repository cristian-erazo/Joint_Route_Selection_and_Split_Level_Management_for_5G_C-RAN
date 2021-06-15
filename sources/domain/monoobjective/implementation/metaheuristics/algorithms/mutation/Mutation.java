package domain.monoobjective.implementation.metaheuristics.algorithms.mutation;

import domain.Algorithm;

/**
 *
 * @author cristian.erazo@cinvestav.mx
 * @param <T>
 */
public interface Mutation<T> extends Algorithm {

    @Override
    public T run();

    public void setIndividual(T ind);

    public void setProbability(double prob);
}
