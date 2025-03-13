package domain.operators;

import domain.Algorithm;
import domain.problem.ProblemInstance;

/**
 *
 * @author cristian.erazo@cinvestav.mx
 * @param <T>
 */
public interface Reparator<T> extends Algorithm {

    @Override
    public T run();

    public void setIndividual(T ind);

    public Reparator<T> copy(ProblemInstance p);
}
