package domain.monoobjective.implementation.metaheuristics.algorithms.selection;

import domain.monoobjective.implementation.MatrixSolution;
import domain.operators.Selection;
import java.util.List;
import java.util.Random;

/**
 *
 * @author cristian.erazo@cinvestav.mx
 */
public class StochasticRanking implements Selection<MatrixSolution> {

    protected final boolean isMaximization;
    protected List<MatrixSolution> list;
    protected final double prob;
    protected Random rand;
    protected int size;

    public StochasticRanking(boolean isMaximization, Random rand, double prob) {
        size = 0;
        this.isMaximization = isMaximization;
        this.rand = rand;
        this.prob = prob;
    }

    @Override
    public List<MatrixSolution> run() {
        if (size > 0 && !list.isEmpty() && size <= list.size()) {
            int N = list.size(), i, j = 0, count;
            while (j < N) {
                count = 0;
                i = 0;
                while (i < N - 1) {
                    double u = rand.nextDouble();
                    if (list.get(i).gn == 0 && list.get(i + 1).gn == 0 || u < prob) {
                        if (isMaximization) {
                            if (list.get(i).getObjective() < list.get(i + 1).getObjective()) {
                                list.set(i, list.set(i + 1, list.get(i)));
                                count++;
                            }
                        } else {
                            if (list.get(i).getObjective() > list.get(i + 1).getObjective()) {
                                list.set(i, list.set(i + 1, list.get(i)));
                                count++;
                            }
                        }
                    } else {
                        if (list.get(i).gn > list.get(i + 1).gn) {
                            list.set(i, list.set(i + 1, list.get(i)));
                            count++;
                        }
                    }
                    i++;
                }
                if (count == 0) {
                    break;
                }
                j++;
            }
            return list.subList(0, size);
        }
        return null;
    }

    @Override
    public void setSelectionSize(int size) {
        this.size = size;
    }

    @Override
    public int getSelectionSize() {
        return size;
    }

    @Override
    public void setList(List<MatrixSolution> list) {
        this.list = list;
    }
}
