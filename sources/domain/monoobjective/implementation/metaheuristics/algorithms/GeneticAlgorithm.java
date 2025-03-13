package domain.monoobjective.implementation.metaheuristics.algorithms;

import domain.util.threads.RunObjective;
import domain.EvaluationFunction;
import domain.monoobjective.implementation.MatrixSolution;
import domain.monoobjective.implementation.metaheuristics.Metaheuristic;
import domain.operators.Crossover;
import domain.operators.Initialization;
import domain.operators.Mutation;
import domain.operators.Reparator;
import domain.operators.Selection;
import domain.problem.ProblemInstance;
import domain.util.Tools;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 *
 * @author cristian.erazo@cinvestav.mx
 */
public class GeneticAlgorithm extends Metaheuristic<MatrixSolution> {

    protected final int popSize;
    protected Mutation<MatrixSolution> mutation;
    protected Crossover<MatrixSolution> crossover;
    protected Selection<MatrixSolution> selection;
    protected Initialization<MatrixSolution> initalization;
    public List<CrowdingDistanceMatrixSolution> paretoFront;
    private List<CrowdingDistanceMatrixSolution> remove;
    private CrowdingDistanceComparator comparator;

    public GeneticAlgorithm(ProblemInstance instance, EvaluationFunction<Double, MatrixSolution> fx, Mutation<MatrixSolution> mutation, Crossover<MatrixSolution> crossover, Selection<MatrixSolution> selection, Initialization<MatrixSolution> initialization, Reparator<MatrixSolution> reparator, Random rand, int numIterations, int popSize) {
        super(instance, fx, rand, numIterations, Tools.numThreads);
        this.popSize = popSize;
        this.mutation = mutation;
        this.crossover = crossover;
        this.selection = selection;
        this.initalization = initialization;
        remove = paretoFront = null;
        this.reparator = reparator;
        comparator = null;
    }

    @Override
    public MatrixSolution run() {
        int nChilds = popSize / 2;
        numNotValidSolutions = it = 0;
        crossover.setNumberOfChilds(nChilds);
        List<MatrixSolution> population = initalization.run();
        evaluatePopulation(population);
        if (Tools.echo) {
            System.out.println(String.format("[%d] AvgFx: %f Best: %f %b", it, avgFx, best.getObjective(), best.isValid));
        }
        while (it < numIterations) {
            it++;
            crossover.setParents(population);
            for (MatrixSolution child : crossover.run()) {
                mutation.setProbability(Tools.mut / (double) (child.getN() * child.getM()));
                mutation.setIndividual(child);
                MatrixSolution h = mutation.run();
                if (!population.contains(h)) {
                    population.add(h);
                }
            }
            if (threads == null) {
                evaluatePopulation(population);
            } else {
                concurrentEvaluatePopulation(population);
            }
            selection.setList(population);
            selection.setSelectionSize(popSize);
            population = selection.run();
            if (Tools.echo) {
                System.out.print(String.format("[%d] AvgFx: %f Best: %s\n", it, avgFx, best.toString()));
            }
            if (paretoFront != null && paretoFront.size() > popSize) {
                reduceByCrowdingDistance();
            }
        }
        best.setObjective(-1.);
        best.gn = instance.validate(best);
        fx.evaluate(best);
        if (Tools.echo) {
            System.out.println(String.format("[%d] Best: %s", best.it, best));
        }
        return best;
    }

    protected double evaluatePopulation(List<MatrixSolution> population) {
        avgFx = 0;
        for (MatrixSolution solution : population) {
            if (solution.getObjective() == -1) {
                solution.it = it;
                solution.gn = instance.validate(solution);//se evaluan las restricciones del problema
                if (reparator != null && solution.gn > 0) {
                    reparator.setIndividual(solution);
                    solution = reparator.run();
                }
                if (solution.gn > 0) {
                    numNotValidSolutions++;
                }
                fx.evaluate(solution);
                if (best == null || fx.isBetter(solution, best)) {//si solucion actual es mejor, entonces actualizar la mejor solucion
                    best = (MatrixSolution) solution.copy();
                }
                if (Tools.printFront && solution.gn == 0 && solution.isValid) {
                    updateFront(solution);
                }
            }
            avgFx += solution.getObjective();
        }
        avgFx /= (double) population.size();
        return avgFx;
    }

    @Override
    protected void getThreadResults(RunObjective thread) {
        if (thread.getBestSolution() != null && (best == null || fx.isBetter(thread.getBestSolution(), best))) {//si solucion actual es mejor, entonces actualizar la mejor solucion
            best = (MatrixSolution) thread.getBestSolution();
        }
        if (Tools.printFront) {
            for (MatrixSolution candidateSolution : thread.getCandidateSolutions()) {
                if (candidateSolution.gn == 0 && candidateSolution.isValid) {
                    updateFront(candidateSolution);
                }
            }
        }
    }

    /**
     * This function determines dominance between two solutions using as a
     * comparison criterion the acceptance and solution quality.
     *
     * @param a Solution 1
     * @param b Solution 2
     * @return 1 if solution 1 dominates to solution 2, -1 if solution 2
     * dominates to solution 1, and 0 otherwise.
     */
    private int dominance(MatrixSolution a, MatrixSolution b) {
        int c1 = 0, c2 = 0;
        if (Tools.isMaximization) {
            if (a.getObjective() > b.getObjective()) {
                c1++;
            } else if (b.getObjective() > a.getObjective()) {
                c2++;
            }
        } else {
            if (a.getObjective() < b.getObjective()) {
                c1++;
            } else if (b.getObjective() < a.getObjective()) {
                c2++;
            }
        }
        if (a.nAccepted > b.nAccepted) {
            c1++;
        } else if (b.nAccepted > a.nAccepted) {
            c2++;
        }
        if (c2 == 0 && c1 > 0) {
            return 1;
        }
        if (c1 == 0 && c2 > 0) {
            return -1;
        }
        return 0;
    }

    private void updateFront(MatrixSolution solution) {
        if (paretoFront == null) {
            remove = new ArrayList<>(popSize);
            paretoFront = new ArrayList<>(popSize);
            comparator = new CrowdingDistanceComparator();
            paretoFront.add(new CrowdingDistanceMatrixSolution(solution));
        } else {
            CrowdingDistanceMatrixSolution newSolution = new CrowdingDistanceMatrixSolution(solution);
            if (!paretoFront.contains(newSolution)) {
                int numNonDominated = 0, c;
                for (CrowdingDistanceMatrixSolution individual : paretoFront) {
                    c = dominance(newSolution, individual);
                    if (c == 0) {
                        numNonDominated++;
                    } else if (c == 1) {
                        remove.add(individual);
                    }
                }
                if (numNonDominated == paretoFront.size() || remove.size() > 0) {
                    paretoFront.add(newSolution);
                }
                while (remove.size() > 0) {
                    paretoFront.remove(remove.remove(0));
                }
            }
        }
    }

    private void quicksort(List<CrowdingDistanceMatrixSolution> elements, int idx, int left, int right) {
        int i = left, j = right;
        CrowdingDistanceMatrixSolution pivot = elements.get((left + right) / 2);
        while (i <= j) {
            if (idx == 0) {
                while (elements.get(i).getObjective() > pivot.getObjective()) {
                    i++;
                }
                while (elements.get(j).getObjective() < pivot.getObjective()) {
                    j--;
                }
            } else {
                while (elements.get(i).nAccepted > pivot.nAccepted) {
                    i++;
                }
                while (elements.get(j).nAccepted < pivot.nAccepted) {
                    j--;
                }
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
    }

    private void reduceByCrowdingDistance() {
        int last = paretoFront.size() - 1;
        double req = instance.requests.length, val;
        for (CrowdingDistanceMatrixSolution individuo : paretoFront) {
            individuo.crowdingDistance = 0.0;
        }
        quicksort(paretoFront, 0, 0, last);
        paretoFront.get(0).crowdingDistance = paretoFront.get(last).crowdingDistance = Tools.MAX_VALUE;
        for (int i = 1; i < last; i++) {
            paretoFront.get(i).crowdingDistance += paretoFront.get(i + 1).getObjective() - paretoFront.get(i - 1).getObjective();
        }
        quicksort(paretoFront, 1, 0, last);
        paretoFront.get(0).crowdingDistance = paretoFront.get(last).crowdingDistance = Tools.MAX_VALUE;
        for (int i = 1; i < last; i++) {
            val = ((double) (paretoFront.get(i + 1).nAccepted - paretoFront.get(i - 1).nAccepted)) / req;
            paretoFront.get(i).crowdingDistance += val;
        }
        Collections.sort(paretoFront, comparator);
        paretoFront = paretoFront.subList(0, popSize);
    }
}

class CrowdingDistanceMatrixSolution extends MatrixSolution {

    public double crowdingDistance;
    public MatrixSolution s;

    public CrowdingDistanceMatrixSolution(MatrixSolution s) {
        super(s.accepted, s.nAccepted, s.data, s.getObjective(), s.it, s.gn, s.quality);
        crowdingDistance = 0.0;
        this.s = s;
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
}

class CrowdingDistanceComparator implements Comparator<CrowdingDistanceMatrixSolution> {

    @Override
    public int compare(CrowdingDistanceMatrixSolution o1, CrowdingDistanceMatrixSolution o2) {
        return Double.compare(-o1.crowdingDistance, -o2.crowdingDistance);
    }
}
