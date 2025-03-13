package domain.monoobjective.implementation.functions;

import domain.EvaluationFunction;
import domain.monoobjective.implementation.MatrixSolution;
import domain.monoobjective.implementation.ObjectiveFunction;
import domain.problem.ProblemInstance;

/**
 *
 * @author cristian.erazo@cinvestav.mx
 */
public class AcceptanceRate extends ObjectiveFunction {

    public AcceptanceRate(ProblemInstance instance, double w1, double w2, double w3, boolean isMaximization) {
        super(instance, w1, w2, w3, isMaximization);
    }

    @Override
    protected Double eval(MatrixSolution s) {
        super.eval(s);
        return (double) s.nAccepted / (double) s.getN();
    }

    @Override
    public EvaluationFunction<Double, MatrixSolution> copy(ProblemInstance p) {
        return new AcceptanceRate(p, w1, w2, w3, isMaximization);
    }

}
