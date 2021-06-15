package domain.multiobjective.implementation;

import domain.EvaluationFunction;
import domain.util.Tools;
import java.util.List;

/**
 *
 * @author cristian.erazo@cinvestav.mx
 */
public class MultiObjectiveFunction implements EvaluationFunction<Double[], MultiObjectiveMatrixSolution> {

    public long EFO;

    @Override
    public Double[] evaluate(MultiObjectiveMatrixSolution s) {
        throw new UnsupportedOperationException("Evaluate individual not supported...");
    }

    @Override
    public Double[] evaluate(List<MultiObjectiveMatrixSolution> p) {
        throw new UnsupportedOperationException("Evaluate population not supported...");
    }

    @Override
    public boolean isMaximization() {
        return Tools.isMaximization;
    }

    @Override
    public double evaluateDiversity(List<MultiObjectiveMatrixSolution> p1, List<MultiObjectiveMatrixSolution> p2) {
        throw new UnsupportedOperationException("Evaluate Diversity not supported...");
    }

    @Override
    public long EFO() {
        return EFO;
    }
}
