package domain.monoobjective.implementation.metaheuristics.algorithms.reparation;

import domain.monoobjective.implementation.MatrixSolution;
import domain.operators.Reparator;
import domain.problem.ProblemInstance;
import java.util.Random;

/**
 *
 * @author cristian.erazo@cinvestav.mx
 * @param <T>
 */
public class RandomReparator<T extends MatrixSolution> implements Reparator<T> {

    protected Random rand;
    protected T solution;
    protected ProblemInstance instance;
    protected final int size;

    public RandomReparator(Random rand, ProblemInstance instance) {
        this.rand = rand;
        this.instance = instance;
        size = instance.requests.length;
    }

    @Override
    public T run() {
        if (solution != null && solution.gn > 0) {
            int i, j, newPath, newSplit, iter = 0, maxIter = 150;
            while (iter < maxIter) {
                T s = (T) solution.copy();
                s.setObjective(-1.);
                i = rand.nextInt(instance.requests.length);
                j = rand.nextInt(instance.requests[i].vDUs.size());
                s.accepted[i] = rand.nextDouble() < 0.02 ? !s.accepted[i] : s.accepted[i];
                if (instance.isFullyRepresentated()) {
                    j = instance.requests[i].vDUs.get(j).indx;
                    s.data[i][j] = instance.DUs.get(rand.nextInt(instance.max.data[i][j])).nodePosition;
                    s.data[i][j + instance.pathPosition] = instance.min.data[i][j + instance.pathPosition] + rand.nextInt(instance.max.data[i][j + instance.pathPosition] - instance.min.data[i][j + instance.pathPosition]);
                    s.data[i][j + instance.splitPosition] = instance.min.data[i][j + instance.splitPosition] + rand.nextInt(instance.max.data[i][j + instance.splitPosition] - instance.min.data[i][j + instance.splitPosition]);
                } else {
                    newPath = rand.nextInt(instance.mPaths[0][0].size());
                    newSplit = rand.nextInt(instance.splits.length);
                    s.data[i][instance.step * j + instance.pathPosition] = newPath;
                    s.data[i][instance.step * j + instance.splitPosition] = newSplit;
                }
                s.gn = instance.validate(s);
                if (solution.gn > s.gn) {
                    solution = s;
                    solution.setObjective(-1.);
                    if (solution.gn == 0) {
                        solution.isValid = true;
                        break;
                    }
                }
                iter++;
            }
        }
        return solution;
    }

    @Override
    public void setIndividual(T ind) {
        solution = ind;
    }
}
