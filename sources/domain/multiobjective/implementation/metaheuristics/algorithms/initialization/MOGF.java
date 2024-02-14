package domain.multiobjective.implementation.metaheuristics.algorithms.initialization;

import domain.monoobjective.implementation.metaheuristics.algorithms.initialization.GreedyFull;
import domain.multiobjective.implementation.MultiObjectiveMatrixSolution;
import domain.problem.ProblemInstance;
import java.util.Random;

/**
 *
 * @author D_mon
 */
public class MOGF extends GreedyFull<MultiObjectiveMatrixSolution> {

    private final int numObjs;

    public MOGF(ProblemInstance instance, int popSize, Random rand, int numObjs) {
        super(instance, popSize, rand);
        this.numObjs = numObjs;
    }

    @Override
    protected MultiObjectiveMatrixSolution initializeSolution(int n, int m) {
        return new MultiObjectiveMatrixSolution(n, m, numObjs);
    }
}
