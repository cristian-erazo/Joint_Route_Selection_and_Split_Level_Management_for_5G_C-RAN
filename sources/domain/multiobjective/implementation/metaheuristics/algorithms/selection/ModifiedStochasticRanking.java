package domain.multiobjective.implementation.metaheuristics.algorithms.selection;

import domain.multiobjective.implementation.MultiObjectiveMatrixSolution;
import domain.operators.Selection;
import java.util.List;
import java.util.Random;

/**
 *
 * @author D_mon
 */
public class ModifiedStochasticRanking implements Selection<MultiObjectiveMatrixSolution> {

    protected List<MultiObjectiveMatrixSolution> list;
    protected final boolean isMaximization;
    protected final double prob;
    protected final int nObjs;
    protected Random rand;
    protected int size;
    private MultiObjectiveMatrixSolution[] best;
    private boolean[] contained;
    private int[] bestId;

    public ModifiedStochasticRanking(boolean isMaximization, Random rand, double prob, int nObjs) {
        this.isMaximization = isMaximization;
        this.nObjs = nObjs;
        this.rand = rand;
        this.prob = prob;
        this.size = 0;
        best = new MultiObjectiveMatrixSolution[nObjs + 1];
        contained = new boolean[nObjs + 1];
        bestId = new int[nObjs + 1];
    }

    @Override
    public List<MultiObjectiveMatrixSolution> run() {
        if (size > 0 && !list.isEmpty() && size <= list.size()) {
            int N = list.size(), i, j = 0, count, k, d;
            while (j < N) {
                k = 1;
                count = i = 0;
                while (i < N - 1) {
                    double u = rand.nextDouble();
                    if (list.get(i).gn == 0 && list.get(k).gn == 0 || u < prob) {
                        d = list.get(k).dominates(list.get(i), isMaximization);
                        if (d > 0) {
                            list.set(i, list.set(k, list.get(i)));
                            count++;
                        }
                    } else {
                        if (list.get(i).gn > list.get(k).gn) {
                            list.set(i, list.set(k, list.get(i)));
                            count++;
                        }
                    }
                    i = k;
                    k++;
                }
                if (count == 0) {
                    break;
                }
                j++;
            }
            validateBestValues();
            return list.subList(0, size);
        }
        return null;
    }

    private void validateBestValues() {
        int last = size - 1;
        for (int i = 0; i < nObjs + 1; i++) {
            best[i] = null;
            bestId[i] = -1;
            contained[i] = true;
        }
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).gn == 0) {
                for (int objID = 0; objID < nObjs; objID++) {
                    if (best[objID] == null || isBetter(list.get(i).getObjectives()[objID], best[objID].getObjectives()[objID])) {
                        best[objID] = list.get(i);
                        bestId[objID] = i;
                        if (i >= size) {
                            contained[objID] = false;
                        }
                    }
                }
                if (best[nObjs] == null || isBetter(list.get(i).getObjective(), best[nObjs].getObjective())) {
                    best[nObjs] = list.get(i);
                    bestId[nObjs] = i;
                    if (i >= size) {
                        contained[nObjs] = false;
                    }
                }
            }
        }
        for (int i = 0; i < nObjs; i++) {
            if (!contained[i]) {
                list.set(bestId[i], list.set(last - i, best[i]));
            }
        }
        if (!contained[nObjs]) {
            list.set(bestId[nObjs], list.set(last - nObjs, best[nObjs]));
        }
    }

    private boolean isBetter(double d1, double d2) {
        if (isMaximization) {
            return d1 > d2;
        } else {
            return d1 < d2;
        }
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
    public void setList(List<MultiObjectiveMatrixSolution> list) {
        this.list = list;
    }
}
