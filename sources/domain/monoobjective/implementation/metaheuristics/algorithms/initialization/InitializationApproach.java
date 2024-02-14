package domain.monoobjective.implementation.metaheuristics.algorithms.initialization;

import domain.monoobjective.implementation.MatrixSolution;
import domain.operators.Initialization;
import domain.problem.ProblemInstance;
import domain.problem.graph.Node;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;

/**
 *
 * @author D_mon
 * @param <T>
 */
public abstract class InitializationApproach<T extends MatrixSolution> implements Initialization<T> {

    protected final ProblemInstance instance;
    protected TreeSet<Node> tCUs;
    protected TreeSet<Node> tDUs;
    protected int popSize;
    protected Random rand;

    public InitializationApproach(Random rand, ProblemInstance instance, int popSize) {
        this.instance = instance;
        this.popSize = popSize;
        this.rand = rand;
        this.tCUs = new TreeSet<>();
        this.tDUs = new TreeSet<>();
    }

    protected abstract T initializeSolution(int n, int m);

    @Override
    public abstract List<T> run();
}
