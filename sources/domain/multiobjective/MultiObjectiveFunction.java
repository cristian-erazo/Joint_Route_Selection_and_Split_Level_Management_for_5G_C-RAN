package domain.multiobjective;

import domain.EvaluationFunction;
import domain.multiobjective.implementation.MultiObjectiveMatrixSolution;
import domain.problem.ProblemInstance;
import domain.util.Tools;
import java.util.List;

/**
 *
 * @author cristian.erazo@cinvestav.mx
 */
public abstract class MultiObjectiveFunction implements EvaluationFunction<Double[], MultiObjectiveMatrixSolution> {

    protected final ProblemInstance instance;
    protected boolean isMaximization;
    protected long EFO;
    protected int nObj;

    public MultiObjectiveFunction(ProblemInstance instance, boolean isMaximization, int nObj) {
        EFO = 0;
        this.nObj = nObj;
        this.instance = instance;
        this.isMaximization = isMaximization;
    }

    @Override
    public Double[] evaluate(MultiObjectiveMatrixSolution s) {
        Double[] objs = s.getObjectives();
        if (objs != null) {
            if (s.getObjective() == -1.) {
                for (int i = 0; i < nObj; i++) {
                    s.setObjective(i, eval(s, i));
                }
                s.setObjective(eval(s));
                EFO++;
            }
            return s.getObjectives();
        } else {
            throw new NullPointerException();
        }
    }

    @Override
    public Double[] evaluate(List<MultiObjectiveMatrixSolution> p) {
        Double[] val = new Double[nObj];
        for (int i = 0; i < nObj; i++) {
            val[i] = 0.;
        }
        for (MultiObjectiveMatrixSolution s : p) {
            Double[] res = evaluate(s);
            for (int i = 0; i < nObj; i++) {
                val[i] += res[i];
            }
        }
        for (int i = 0; i < nObj; i++) {
            val[i] /= (double) p.size();
        }
        return val;
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

    @Override
    public boolean isBetter(MultiObjectiveMatrixSolution a, MultiObjectiveMatrixSolution b) {
        double va, vb;
        va = a.getObjective() + (a.getGn() * a.getGn());
        vb = b.getObjective() + (b.getGn() * b.getGn());
        if (isMaximization()) {
            if (a.getGn() > 0) {
                va *= -1.;
            }
            if (b.getGn() > 0) {
                vb *= -1.;
            }
            return va > vb;
        } else {
            return va < vb;
        }
    }

    public abstract Double eval(MultiObjectiveMatrixSolution s, int objID);

    protected abstract Double eval(MultiObjectiveMatrixSolution s);
}
