/*
 */
package domain.util.threads;

import domain.EvaluationFunction;
import domain.monoobjective.implementation.MatrixSolution;
import domain.operators.Reparator;
import domain.problem.ProblemInstance;
import domain.util.Tools;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author cristian.erazo@cinvestav.mx
 */
public class RunObjective extends Thread {

    private final EvaluationFunction<Double, MatrixSolution> fx;
    private EvaluationFunction<Double, MatrixSolution>[] tFx;
    private final Reparator<MatrixSolution> reparator;
    private List<MatrixSolution> candidateSolutions;
    private Reparator<MatrixSolution>[] tRep;
    private List<MatrixSolution> population;
    private final ProblemInstance instance;
    private ProblemInstance[] tIns;
    private MatrixSolution best;
    private int numNotValid;
    private final long it;
    private final int inc;
    private final int id;
    private double sumFx;

    public RunObjective(List<MatrixSolution> population, ProblemInstance instance, Reparator reparator, EvaluationFunction fx, ProblemInstance[] tIns, Reparator[] tRep, EvaluationFunction[] tFx, int id, long it) {
        this.reparator = reparator;
        this.population = population;
        this.instance = instance;
        this.tRep = tRep;
        this.tIns = tIns;
        this.tFx = tFx;
        this.fx = fx;
        this.id = id;
        this.it = it;
        inc = tIns.length;
    }

    @Override
    public void run() {
        int chunk = population.size() / tIns.length;
        int res = population.size() % tIns.length;
        int limit = id * chunk + chunk;
        int start = id * chunk;
        if (id < res) {
            limit++;
        } else if (id == tIns.length - 1) {
            limit = population.size();
        }
        if (id != 0 && id <= res) {
            start = (id - 1) * chunk + chunk + 1;
        }
        numNotValid = 0;
        sumFx = 0;
        if (tIns[id] == null) {
            tIns[id] = instance.copy();
            tFx[id] = fx.copy(tIns[id]);
            if (reparator != null) {
                tRep[id] = reparator.copy(tIns[id]);
            }
        }
        candidateSolutions = Tools.printFront ? new ArrayList<>(chunk) : null;
        for (MatrixSolution solution : population.subList(start, limit)) {
            if (solution.getObjective() == -1) {
                solution.it = it;
                solution.gn = tIns[id].validate(solution);
                if (reparator != null && tRep[id] != null && solution.gn > 0) {
                    tRep[id].setIndividual(solution);
                    solution = tRep[id].run();
                }
                if (solution.gn > 0) {
                    numNotValid++;
                }
                tFx[id].evaluate(solution);
                if (best == null || fx.isBetter(solution, best)) {//si solucion actual es mejor, entonces actualizar la mejor solucion
                    best = (MatrixSolution) solution.copy();
                }
                if (Tools.printFront) {
                    candidateSolutions.add(solution);
                }
            }
            sumFx += solution.getObjective();
        }
    }

    public List<MatrixSolution> getCandidateSolutions() {
        return candidateSolutions;
    }

    public double getSumObjectives() {
        return sumFx;
    }

    public MatrixSolution getBestSolution() {
        return best;
    }

    public int getNumNotValid() {
        return numNotValid;
    }
}
