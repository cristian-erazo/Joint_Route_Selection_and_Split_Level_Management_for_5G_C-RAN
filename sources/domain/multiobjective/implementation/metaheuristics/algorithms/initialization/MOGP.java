package domain.multiobjective.implementation.metaheuristics.algorithms.initialization;

import domain.monoobjective.implementation.MatrixSolution;
import domain.monoobjective.implementation.metaheuristics.algorithms.initialization.GreedyPaths;
import domain.multiobjective.implementation.MultiObjectiveMatrixSolution;
import domain.problem.ProblemInstance;
import java.util.Random;

/**
 *
 * @author D_mon
 */
public class MOGP extends GreedyPaths {

    private final int numObjs;

    public MOGP(ProblemInstance instance, int popSize, Random rand, int numObjs) {
        super(instance, popSize, rand);
        this.numObjs = numObjs;
    }

    @Override
    protected MatrixSolution initializeSolution(int n, int m) {
        return new MultiObjectiveMatrixSolution(n, m, numObjs);
    }
}
