package domain.multiobjective.implementation.metaheuristics.algorithms;

import domain.multiobjective.MultiObjectiveFunction;
import domain.multiobjective.implementation.MultiObjectiveMatrixSolution;
import domain.multiobjective.implementation.metaheuristics.MultiObjectiveMetaheuristic;
import domain.operators.Crossover;
import domain.operators.Initialization;
import domain.operators.Mutation;
import domain.operators.Reparator;
import domain.operators.Selection;
import domain.problem.ProblemInstance;
import domain.util.Tools;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 *
 * @author D_mon
 */
public class MOEA extends MultiObjectiveMetaheuristic<MultiObjectiveMatrixSolution> {

    protected long it;
    protected long bIt;
    protected double avgFx;
    protected final int popSize;
    protected final int numObjs;
    protected List<MultiObjectiveMatrixSolution> nonDominatedSolutions;
    protected Mutation<MultiObjectiveMatrixSolution> mutation;
    protected Crossover<MultiObjectiveMatrixSolution> crossover;
    protected Selection<MultiObjectiveMatrixSolution> selection;
    protected Initialization<MultiObjectiveMatrixSolution> initalization;
    protected Reparator<MultiObjectiveMatrixSolution> reparator;

    public MOEA(ProblemInstance instance, MultiObjectiveFunction fx, Random rand, Mutation<MultiObjectiveMatrixSolution> mutation, Crossover<MultiObjectiveMatrixSolution> crossover, Selection<MultiObjectiveMatrixSolution> selection, Initialization<MultiObjectiveMatrixSolution> initialization, Reparator<MultiObjectiveMatrixSolution> reparator, int numIterations, int popSize, int numObjs) {
        super(instance, fx, rand, numIterations);
        this.mutation = mutation;
        this.crossover = crossover;
        this.selection = selection;
        this.initalization = initialization;
        this.reparator = reparator;
        this.popSize = popSize;
        this.numObjs = numObjs;
    }

    @Override
    public List run() {
        nonDominatedSolutions = new ArrayList<>(popSize);
        int nChilds = popSize / 2;
        numNotValidSolutions = bIt = it = 0;
        crossover.setNumberOfChilds(nChilds);
//        List<List<MultiObjectiveMatrixSolution>> selected = new ArrayList<>(2);
        List<MultiObjectiveMatrixSolution> population = initalization.run();
        population = evaluatePopulation(population);
//        selected.add(new ArrayList<>(population));
        if (Tools.ECHO) {
            System.out.print(String.format("[%d] AvgFx: %f Best: %f %b\n", it, avgFx, best.getObjective(), best.isValid));
        }
        while (it < numIterations) {
            it++;
            crossover.setParents(population);
            for (MultiObjectiveMatrixSolution child : crossover.run()) {
                mutation.setProbability(Tools.mut / (double) (child.getN() * child.getM()));
                mutation.setIndividual(child);
                MultiObjectiveMatrixSolution h = mutation.run();
                if (!population.contains(h)) {
                    h.it = it;
                    population.add(h);
                }
            }
            population = evaluatePopulation(population);
            selection.setList(population);
            selection.setSelectionSize(popSize);
            population = selection.run();
            if (Tools.ECHO) {
                System.out.print(String.format("[%d] AvgFx: %f Best: %s", it, avgFx, best));
//                if (it % 20 == 0) {
//                    selected.add(new ArrayList<>(population));
//                    System.out.print(String.format(" Dv: %.3g ", fx.evaluateDiversity(selected.remove(0), population)));
//                }
                System.out.println();
            }
            if (nonDominatedSolutions.size() > popSize) {
                reduceByCrowdingDistance();
            }
        }
        best.setObjective(-1.);
        best.gn = instance.validate(best);
        fx.evaluate(best);
        if (Tools.ECHO) {
            System.out.println(String.format("[%d] Best: %s", best.it, best.toString()));
        }
        return nonDominatedSolutions;
    }

    public MultiObjectiveMatrixSolution getBest() {
        return best;
    }

    protected List<MultiObjectiveMatrixSolution> evaluatePopulation(List<MultiObjectiveMatrixSolution> population) {
        avgFx = 0;
        //evaluar funcion objetivo y restricciones
        for (MultiObjectiveMatrixSolution solution : population) {
            if (solution.getObjective() == -1) {
                solution.gn = instance.validate(solution);//se evaluan las restricciones del problema
                if (reparator != null && solution.gn > 0) {
                    reparator.setIndividual(solution);
                    solution = reparator.run();
                }
                if (solution.gn > 0) {
                    numNotValidSolutions++;
                }
                fx.evaluate(solution);
                if (solution.it == -1) {
                    solution.it = it;
                }
                if (best == null || fx.isBetter(solution, best)) {//si solucion actual es mejor, entonces actualizar la mejor solucion
                    best = solution;
                    bIt = it;
                }
                if (solution.isValid && !nonDominatedSolutions.contains(solution)) {
                    addToNonDominatedSolutions(solution);
                }
            }
            avgFx += solution.getObjective();
        }
        avgFx /= (double) population.size();
        return population;
    }

    protected void reduceByCrowdingDistance() {
        int last = nonDominatedSolutions.size() - 1;
        for (MultiObjectiveMatrixSolution individuo : nonDominatedSolutions) {
            individuo.crowdingDistance = 0.0;
        }
        for (int idx = 0; idx < numObjs; idx++) {
            nonDominatedSolutions = quicksort(nonDominatedSolutions, idx, 0, last);
            nonDominatedSolutions.get(0).crowdingDistance = nonDominatedSolutions.get(last).crowdingDistance = Tools.MAX_VALUE;
            for (int i = 1; i < last; i++) {
                nonDominatedSolutions.get(i).crowdingDistance += nonDominatedSolutions.get(i + 1).getObjectives()[idx] - nonDominatedSolutions.get(i - 1).getObjectives()[idx];
            }
        }
        Collections.sort(nonDominatedSolutions);
        nonDominatedSolutions = nonDominatedSolutions.subList(0, popSize);
    }

    public static List<MultiObjectiveMatrixSolution> quicksort(List<MultiObjectiveMatrixSolution> elements, int idx, int left, int right) {
        int i = left, j = right;
        MultiObjectiveMatrixSolution pivot = elements.get((left + right) / 2);
        while (i <= j) {
            while (elements.get(i).getObjectives()[idx] > pivot.getObjectives()[idx]) {
                i++;
            }
            while (elements.get(j).getObjectives()[idx] < pivot.getObjectives()[idx]) {
                j--;
            }
            if (i <= j) {
                elements.set(j, elements.set(i, elements.get(j)));
                i++;
                j--;
            }
        }
        if (left < j) {
            quicksort(elements, idx, left, j);
        }
        if (i < right) {
            quicksort(elements, idx, i, right);
        }
        return elements;
    }

    private void addToNonDominatedSolutions(MultiObjectiveMatrixSolution s) {
        if (nonDominatedSolutions.isEmpty()) {
            nonDominatedSolutions.add(s);
        } else {
            boolean add = true;
            for (int i = 0; i < nonDominatedSolutions.size(); i++) {
                int d = s.dominates(nonDominatedSolutions.get(i), fx.isMaximization());
                if (d > 0) {
                    nonDominatedSolutions.remove(nonDominatedSolutions.get(i));
                } else if (d < 0) {
                    add = false;
                }
            }
            if (add) {
                nonDominatedSolutions.add(s);
            }
        }
    }
}
