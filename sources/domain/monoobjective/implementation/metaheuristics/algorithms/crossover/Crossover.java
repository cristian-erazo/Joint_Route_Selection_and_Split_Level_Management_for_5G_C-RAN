package domain.monoobjective.implementation.metaheuristics.algorithms.crossover;

import domain.Algorithm;
import java.util.List;

/**
 *
 * @author cristian.erazo@cinvestav.mx
 * @param <T>
 */
public interface Crossover<T> extends Algorithm {

    @Override
    public List<T> run();

    public void setParents(List<T> parents);

    public void setNumberOfChilds(int nChilds);

}
