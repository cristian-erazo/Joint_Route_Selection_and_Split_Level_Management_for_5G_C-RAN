package domain.monoobjective.implementation.metaheuristics.algorithms.reparation;

import domain.monoobjective.implementation.MatrixSolution;
import domain.operators.Reparator;
import domain.problem.ProblemInstance;
import domain.problem.graph.Node;
import domain.problem.virtual.VirtualNode;
import java.util.ArrayList;
import java.util.List;
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
    private final int col;
    private List<Node> availables;

    public RandomReparator(Random rand, ProblemInstance instance) {
        this.rand = rand;
        this.instance = instance;
        size = instance.requests.length;
        col = instance.step * instance.maxVirtualDUs + (instance.step == 3 ? instance.maxVirtualCUs : 0);
        availables = new ArrayList<>(instance.DUs.size());
    }

    @Override
    public T run() {
        if (solution != null && solution.gn > 0) {
            int i, j, iter = 0, maxIter = 50;
            T s = (T) solution.copy();
            while (iter < maxIter) {
                copySolution(solution, s);
                i = rand.nextInt(size);
                j = rand.nextInt(instance.requests[i].vDUs.size());
                if (rand.nextDouble() < 0.02) {
                    s.accepted[i] = rand.nextBoolean();
                }
                if (instance.isFullyRepresentated()) {
                    j = instance.requests[i].vDUs.get(j).indx;
                    s.data[i][j] = instance.DUs.get(rand.nextInt(instance.max.data[i][j])).nodePosition;
                    s.data[i][j + instance.pathPosition] = getRandomPath(i, j);
                    s.data[i][j + instance.splitPosition] = instance.min.data[i][j + instance.splitPosition] + rand.nextInt(instance.max.data[i][j + instance.splitPosition] - instance.min.data[i][j + instance.splitPosition]);
                } else {
                    s.data[i][instance.step * j + instance.pathPosition] = getRandomPath(i, j);
                    s.data[i][instance.step * j + instance.splitPosition] = rand.nextInt(instance.splits.length);
                }
                s.gn = instance.validate(s);
                if (s.gn < solution.gn) {
                    copySolution(s, solution);
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

    private void copySolution(T src, T dest) {
        for (int i = 0; i < size; i++) {
            System.arraycopy(src.data[i], 0, dest.data[i], 0, col);
        }
        System.arraycopy(src.accepted, 0, dest.accepted, 0, size);
        dest.gn = src.gn;
        dest.isValid = src.isValid;
        dest.nAccepted = src.nAccepted;
        dest.quality = src.quality;
    }

    @Override
    public void setIndividual(T ind) {
        solution = ind;
    }

    @Override
    public Reparator<T> copy(ProblemInstance p) {
        return new RandomReparator<>(new Random(rand.nextLong()), p);
    }

    private int getRandomPath(int q, int j) {
        if (instance.isFullyRepresentated()) {
            return instance.min.data[q][j + instance.pathPosition] + rand.nextInt(instance.max.data[q][j + instance.pathPosition] - instance.min.data[q][j + instance.pathPosition]);
        } else {
            loadValidDUs(instance.requests[q].vDUs.get(j));
            int tot = availables.size();
            if (tot > 0) {
                int idDU = availables.get(rand.nextInt(tot)).nodePosition;
                Object[] allCUs = instance.allPathsFromDUs.get(idDU).keySet().toArray();
                if (allCUs.length > 0) {
                    Integer idCU = (Integer) allCUs[rand.nextInt(allCUs.length)];
                    int totalPaths = instance.allPathsFromDUs.get(idDU).get(idCU).size();
                    if (totalPaths > 0) {
                        return instance.mPaths[0][0].indexOf(instance.allPathsFromDUs.get(idDU).get(idCU).get(rand.nextInt(totalPaths)));
                    }
                }
            }
            return rand.nextInt(instance.mPaths[0][0].size());
        }
    }

    private void loadValidDUs(VirtualNode vDU) {
        if (!availables.isEmpty()) {
            availables.clear();
        }
        for (Node DU : instance.DUs) {
            if (ProblemInstance.validateAssignament(vDU, DU)) {
                availables.add(DU);
            }
        }
    }
}
