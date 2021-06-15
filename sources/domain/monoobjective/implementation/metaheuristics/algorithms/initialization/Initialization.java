package domain.monoobjective.implementation.metaheuristics.algorithms.initialization;

import domain.Algorithm;
import domain.Solution;
import java.util.List;

/**
 *
 * @author cristian.erazo@cinvestav.mx
 * @param <E>
 */
public interface Initialization<E extends Solution<Integer[]>> extends Algorithm {

    @Override
    public List<E> run();
}
