package domain.monoobjective.implementation.functions;

import domain.monoobjective.implementation.MatrixSolution;
import domain.monoobjective.implementation.ObjectiveFunction;
import domain.problem.ProblemInstance;

/**
 *
 * @author cristian.erazo@cinvestav.mx
 */
public class MinorRequestObjectiveFunction extends ObjectiveFunction {

    private final double C;

    public MinorRequestObjectiveFunction(ProblemInstance instance, double w1, double w2, double w3, boolean isMaximization, double C) {
        super(instance, w1, w2, w3, isMaximization);
        this.C = C;
    }

    @Override
    protected Double eval(MatrixSolution s) {
        return C * super.eval(s) + ((double) s.nAccepted / (double) s.getN());
    }
}
