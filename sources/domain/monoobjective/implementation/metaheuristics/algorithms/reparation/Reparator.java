package domain.monoobjective.implementation.metaheuristics.algorithms.reparation;

import domain.Algorithm;

/**
 *
 * @author cristian.erazo@cinvestav.mx
 * @param <T>
 */
public interface Reparator<T> extends Algorithm {

    @Override
    public T run();

    public void setIndividual(T ind);

}
