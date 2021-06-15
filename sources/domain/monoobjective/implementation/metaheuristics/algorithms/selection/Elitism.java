package domain.monoobjective.implementation.metaheuristics.algorithms.selection;

import domain.monoobjective.implementation.MatrixSolution;
import domain.util.Tools;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Elitism implements Selection<MatrixSolution> {

    private List<MatrixSolution> list;
    private final boolean isMaximization;
    private int size;

    public Elitism(boolean isMaximization) {
        size = 0;
        this.list = new ArrayList<>();
        this.isMaximization = isMaximization;
    }

    @Override
    public List<MatrixSolution> run() {
        if (size > 0 && !list.isEmpty() && size <= list.size()) {
            if (isMaximization) {
                Collections.sort(list, new Comparator<MatrixSolution>() {
                    @Override
                    public int compare(MatrixSolution o1, MatrixSolution o2) {
                        return Double.compare(- o1.getObjective() + Tools.counterWeight * o1.gn, - o2.getObjective() + Tools.counterWeight * o2.gn);
                    }
                });
            } else {
                Collections.sort(list, new Comparator<MatrixSolution>() {
                    @Override
                    public int compare(MatrixSolution o1, MatrixSolution o2) {
                        return Double.compare(o1.getObjective() + Tools.counterWeight * o1.gn, o2.getObjective() + Tools.counterWeight * o2.gn);
                    }
                });
            }
            return list.subList(0, size);
        }
        return null;
    }

    @Override
    public void setSelectionSize(int amount) {
        this.size = amount;
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
