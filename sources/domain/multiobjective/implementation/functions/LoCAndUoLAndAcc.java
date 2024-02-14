package domain.multiobjective.implementation.functions;

import domain.multiobjective.implementation.MultiObjectiveMatrixSolution;
import domain.problem.ProblemInstance;

/**
 *
 * @author D_mon
 */
public class LoCAndUoLAndAcc extends LevelOfCentralizationAndUseOfLinks {

    public LoCAndUoLAndAcc(ProblemInstance instance, double w1, double w2, double w3, boolean isMaximization) {
        super(instance, w1, w2, w3, isMaximization, 3);
    }

    @Override
    public Double eval(MultiObjectiveMatrixSolution s, int objID) {
        switch (objID) {
            case 0:
            case 1:
                return super.eval(s, objID);
            case 2:
                return ((double) s.nAccepted) / ((double) instance.requests.length);
        }
        return 0.;
    }
}
