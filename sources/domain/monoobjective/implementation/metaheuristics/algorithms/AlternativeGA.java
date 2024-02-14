package domain.monoobjective.implementation.metaheuristics.algorithms;

import domain.EvaluationFunction;
import domain.monoobjective.implementation.MatrixSolution;
import domain.monoobjective.implementation.metaheuristics.Metaheuristic;
import domain.operators.Crossover;
import domain.operators.Initialization;
import domain.operators.Mutation;
import domain.operators.Reparator;
import domain.problem.ProblemInstance;
import domain.util.Tools;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author cristian.erazo@cinvestav.mx
 */
public class AlternativeGA extends Metaheuristic<MatrixSolution> {

    protected long it;
    protected double avgFx;
    protected final int popSize;
    protected Mutation<MatrixSolution> mutation;
    protected Crossover<MatrixSolution> crossover;
    protected Reparator<MatrixSolution> reparator;
    protected Initialization<MatrixSolution> initalization;
    //protected boolean doRepair;
    //protected boolean atLeastOne;
    //protected List<MatrixSolution> listFeasibleSolutions;

    public AlternativeGA(ProblemInstance instance, EvaluationFunction<Double, MatrixSolution> fx, Mutation<MatrixSolution> mutation, Crossover<MatrixSolution> crossover, Initialization<MatrixSolution> initialization, Reparator<MatrixSolution> reparator, Random rand, int numIterations, int popSize) {
        super(instance, fx, rand, numIterations);
        this.popSize = popSize;
        this.mutation = mutation;
        this.crossover = crossover;
        this.initalization = initialization;
        this.reparator = reparator;
        //listFeasibleSolutions = new ArrayList<>(popSize);
    }

    @Override
    public MatrixSolution run() {
        int nChilds = popSize;
        double probMutation = 0.02;
        // atLeastOne = false;
        // doRepair = true;// first population must be repaired
        // listFeasibleSolutions.clear();
        numNotValidSolutions = it = 0;
        crossover.setNumberOfChilds(nChilds);
        List<MatrixSolution> population = initalization.run();// random generation
        evaluatePopulation(population);
        if (Tools.ECHO) {
            System.out.println(String.format("[%d] AvgFx: %f Best: %s", it, avgFx, best.toString()));
        }
        while (it < numIterations) {
            it++;
            crossover.setParents(population);// tournament selection
            for (MatrixSolution child : crossover.run()) {// one point crossover
                mutation.setProbability(probMutation);
                mutation.setIndividual(child);
                MatrixSolution h = mutation.run();// mutate 2% of combination path-split
                if (!population.contains(h)) {// discard repeated
                    population.add(h);
                }
            }
            evaluatePopulation(population);
            population = replaceRandomSolution(population);// replace one solution using the best found (so far)
            if (Tools.ECHO) {
                System.out.print(String.format("[%d] AvgFx: %f Best: %s", it, avgFx, best.toString()));
                System.out.println();
            }
        }
        best.setObjective(-1.);
        best.gn = instance.validate(best);
        fx.evaluate(best);
        if (Tools.ECHO) {
            System.out.println(String.format("[%d] Best: %s", best.it, best));
        }
        return best;
    }

    protected double evaluatePopulation(List<MatrixSolution> population) {
        avgFx = 0;
        for (int i = 0; i < population.size(); i++) {
            MatrixSolution solution = population.get(i);
            if (solution.getObjective() == -1) {
                solution.it = it;
                solution.gn = instance.validate(solution);//se evaluan las restricciones del problema
                if (/*doRepair && */reparator != null && solution.gn > 0) {
                    reparator.setIndividual(solution);
                    solution = reparator.run();
                    solution.gn = instance.validate(solution);
                }
                /*if (solution.gn > 0 || !solution.isValid) {
                    numNotValidSolutions++;
                    if (!doRepair && !listFeasibleSolutions.isEmpty()) {
                        //randomly replace using feasible list
                        System.out.println(listFeasibleSolutions.size());
                        population.set(i, (MatrixSolution) listFeasibleSolutions.get(rand.nextInt(listFeasibleSolutions.size())).copy());
                        solution = population.get(i);
                    }
                } else {*/
                fx.evaluate(solution);
                /*if (!listFeasibleSolutions.contains(solution)) {
                        //add to feasible list
                        listFeasibleSolutions.add((MatrixSolution) solution.copy());
                    }
                }*/
                if (best == null || fx.isBetter(solution, best)) {//si solucion actual es mejor, entonces actualizar la mejor solucion
                    best = (MatrixSolution) solution.copy();
                }
                /*if (solution.isValid) {
                    atLeastOne = true;
                }*/
            }
            avgFx += solution.getObjective();
        }
        avgFx /= (double) population.size();
        return avgFx;
    }

    private List<MatrixSolution> replaceRandomSolution(List<MatrixSolution> population) {
        if (best != null) {
            int i = rand.nextInt(population.size());
            population.set(i, (MatrixSolution) best.copy());
        }
        return population;
    }
}
