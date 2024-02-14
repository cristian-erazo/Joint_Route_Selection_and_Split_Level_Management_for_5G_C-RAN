package domain.monoobjective.implementation.metaheuristics.algorithms.crossover;

import domain.monoobjective.implementation.MatrixSolution;
import domain.operators.Crossover;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 *
 * @author cristian.erazo@cinvestav.mx
 * @param <T>
 */
public class Uniform<T extends MatrixSolution> implements Crossover<T> {

    private final double prob;
    private List<T> parents;
    private Random rand;
    private int nChilds;

    public Uniform(Random rand, double prob) {
        nChilds = 0;
        this.rand = rand;
        this.prob = prob;
        parents = new ArrayList<>();
    }

    @Override
    public List<T> run() {
        if (nChilds > 0 && parents != null && !parents.isEmpty() && nChilds <= parents.size()) {
            int k = 0, n = parents.get(0).getN(), m = parents.get(0).getM();
            List<T> childs = new ArrayList<>();
            Collections.shuffle(parents, rand);
            while (childs.size() < nChilds) {
                T p1 = parents.get(k);
                k = (k + 1) % parents.size();
                T p2 = parents.get(k);
                k = (k + 1) % parents.size();
                T c1 = (T) p1.copy();
                T c2 = (T) p2.copy();
                for (int i = 0; i < n; i++) {
                    if (rand.nextDouble() < prob) {
                        c1.accepted[i] = p2.accepted[i];
                    }
                    if (rand.nextDouble() < prob) {
                        c2.accepted[i] = p1.accepted[i];
                    }
                    for (int j = 0; j < m; j++) {
                        if (rand.nextDouble() < prob) {
                            c1.data[i][j] = p2.data[i][j];
                        }
                        if (rand.nextDouble() < prob) {
                            c2.data[i][j] = p1.data[i][j];
                        }
                    }
                }
                c1.setObjective(-1.);
                c2.setObjective(-1.);
                childs.add(c1);
                childs.add(c2);
            }
            parents.clear();
            return childs;
        } else {
            System.out.println("Crossed ZERO!");
        }
        return null;
    }

    @Override
    public void setParents(List<T> parents) {
        if (!this.parents.isEmpty()) {
            this.parents.clear();
        }
        this.parents.addAll(parents);
    }

    @Override
    public void setNumberOfChilds(int nChilds) {
        this.nChilds = nChilds;
    }
}
