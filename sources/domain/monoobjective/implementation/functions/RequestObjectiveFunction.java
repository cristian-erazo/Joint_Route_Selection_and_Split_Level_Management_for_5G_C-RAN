package domain.monoobjective.implementation.functions;

import domain.EvaluationFunction;
import domain.monoobjective.implementation.MatrixSolution;
import domain.monoobjective.implementation.ObjectiveFunction;
import domain.problem.ProblemInstance;

/**
 *
 * @author cristian.erazo@cinvestav.mx
 */
public class RequestObjectiveFunction extends ObjectiveFunction {

    public RequestObjectiveFunction(ProblemInstance instance, double w1, double w2, double w3, boolean isMaximization) {
        super(instance, w1, w2, w3, isMaximization);
    }

    @Override
    protected Double eval(MatrixSolution s) {
        return super.eval(s) + s.nAccepted;
    }

    @Override
    public EvaluationFunction<Double, MatrixSolution> copy(ProblemInstance p) {
        return new RequestObjectiveFunction(p, w1, w2, w3, isMaximization);
    }
}
