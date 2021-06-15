package domain.monoobjective.implementation.metaheuristics.algorithms;

import domain.EvaluationFunction;
import domain.monoobjective.implementation.MatrixSolution;
import domain.monoobjective.implementation.metaheuristics.Metaheuristic;
import domain.monoobjective.implementation.metaheuristics.algorithms.crossover.Crossover;
import domain.monoobjective.implementation.metaheuristics.algorithms.initialization.Initialization;
import domain.monoobjective.implementation.metaheuristics.algorithms.mutation.Mutation;
import domain.monoobjective.implementation.metaheuristics.algorithms.reparation.Reparator;
import domain.monoobjective.implementation.metaheuristics.algorithms.selection.Selection;
import domain.problem.ProblemInstance;
import domain.util.Tools;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author cristian.erazo@cinvestav.mx
 */
public class GeneticAlgorithm extends Metaheuristic<MatrixSolution> {

    protected long it;
    protected long bIt;
    protected double avgFx;
    protected final int popSize;
    protected Mutation<MatrixSolution> mutation;
    protected Crossover<MatrixSolution> crossover;
    protected Selection<MatrixSolution> selection;
    protected Initialization<MatrixSolution> initalization;
    protected Reparator<MatrixSolution> reparator;

    public GeneticAlgorithm(ProblemInstance instance, EvaluationFunction<Double, MatrixSolution> fx, Mutation<MatrixSolution> mutation, Crossover<MatrixSolution> crossover, Selection<MatrixSolution> selection, Initialization<MatrixSolution> initialization, Reparator<MatrixSolution> reparator, Random rand, int numIterations, int popSize) {
        super(instance, fx, rand, numIterations);
        this.popSize = popSize;
        this.mutation = mutation;
        this.crossover = crossover;
        this.selection = selection;
        this.initalization = initialization;
        this.reparator = reparator;
    }

    @Override
    public MatrixSolution run() {
        int nChilds = popSize / 2;
        numNotValidSolutions = bIt = it = 0;
        crossover.setNumberOfChilds(nChilds);
        List<List<MatrixSolution>> selected = new ArrayList<>();
        List<MatrixSolution> population = initalization.run();
        evaluatePopulation(population);
        selected.add(new ArrayList<>(population));
        if (Tools.ECHO) {
            System.out.println(String.format("[%d] AvgFx: %f Best: %f %b", it, avgFx, best.getObjective(), best.isValid));
        }
        while (it < numIterations) {
            crossover.setParents(population);
            for (MatrixSolution child : crossover.run()) {
                mutation.setProbability(Tools.mut / (double) (child.getN() * child.getM()));
                mutation.setIndividual(child);
                MatrixSolution h = mutation.run();
                if (!population.contains(h)) {
                    population.add(h);
                }
            }
            it++;
            evaluatePopulation(population);
            selection.setList(population);
            selection.setSelectionSize(popSize);
            population = selection.run();
            if (Tools.ECHO) {
                System.out.print(String.format("[%d] AvgFx: %f Best: %f %b", it, avgFx, best.getObjective(), best.isValid));
                if (it % 20 == 0) {
                    selected.add(new ArrayList<>(population));
                    System.out.print(String.format(" Dv: %.3g ", fx.evaluateDiversity(selected.remove(0), population)));
                }
                System.out.println();
            }
        }
        best.gn = instance.validate(best);
        fx.evaluate(best);
        if (Tools.ECHO) {
            System.out.println(String.format("[%d] Best: %f %b", bIt, best.getObjective(), best.isValid));
        }
        return best;
    }

    protected double evaluatePopulation(List<MatrixSolution> population) {
        avgFx = 0;
        for (MatrixSolution solution : population) {
            if (solution.getObjective() == -1) {
                solution.gn = instance.validate(solution);//se evaluan las restricciones del problema
                if (reparator != null && solution.gn > 0) {
                    reparator.setIndividual(solution);
                    solution = reparator.run();
                }
                if (solution.gn > 0) {
                    numNotValidSolutions++;
                }
                /*double val = */
                fx.evaluate(solution);
                //solution.setObjective((fx.isMaximization() ? val - (solution.gn * solution.gn) : val + (solution.gn * solution.gn)));
                if (best == null || isBetter(solution, best)) {//si solucion actual es mejor, entonces actualizar la mejor solucion
                    best = solution.copy();
                    bIt = it;
                }
                //solution.setObjective(val);
            }
            avgFx += solution.getObjective();
        }
        avgFx /= (double) population.size();
        return avgFx;
    }
}
