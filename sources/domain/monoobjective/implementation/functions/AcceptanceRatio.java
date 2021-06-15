package domain.monoobjective.implementation.functions;

import domain.monoobjective.implementation.MatrixSolution;
import domain.monoobjective.implementation.ObjectiveFunction;
import domain.problem.ProblemInstance;

/**
 *
 * @author cristian.erazo@cinvestav.mx
 */
public class AcceptanceRatio extends ObjectiveFunction {

    public AcceptanceRatio(ProblemInstance instance, double w1, double w2, double w3, boolean isMaximization) {
        super(instance, w1, w2, w3, isMaximization);
    }

    @Override
    protected Double eval(MatrixSolution s) {
        return (double) s.nAccepted / (double) s.getN();
    }
}
