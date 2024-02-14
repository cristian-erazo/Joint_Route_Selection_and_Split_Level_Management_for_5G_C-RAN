package domain.monoobjective.implementation.metaheuristics.algorithms.crossover;

import domain.monoobjective.implementation.MatrixSolution;
import domain.operators.Crossover;
import domain.util.Tools;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author cristian.erazo@cinvestav.mx
 * @param <T>
 */
public class OnePoinitCrossover<T extends MatrixSolution> implements Crossover<T> {

    private final double prob;
    private final int rounds;
    private List<T> parents;
    private Random rand;
    private int nChilds;

    public OnePoinitCrossover(Random rand, double prob, int rounds) {
        nChilds = 0;
        this.rand = rand;
        this.prob = prob;
        this.rounds = rounds;
        parents = new ArrayList<>();
    }

    @Override
    public List<T> run() {
        if (nChilds > 0 && parents != null && !parents.isEmpty()) {
            int k, n = parents.get(0).getN(), m = parents.get(0).getM(), a, b;
            List<T> childs = new ArrayList<>();
            for (k = 0; k < nChilds; k += 2) {
                T p1 = parents.get(k);
                T p2 = parents.get(k + 1);
                if (rand.nextDouble() < prob) {
                    T c1 = (T) p1.copy();
                    T c2 = (T) p2.copy();
                    a = rand.nextInt(n);
                    b = rand.nextInt(m);
                    for (int i = 0; i <= a; i++) {
                        c1.accepted[i] = p2.accepted[i];
                        c2.accepted[i] = p1.accepted[i];
                        for (int j = 0; j < m; j++) {
                            if (i < a || j <= b) {
                                c1.data[i][j] = p2.data[i][j];
                                c2.data[i][j] = p1.data[i][j];
                            } else {
                                break;
                            }
                        }
                    }
                    c1.setObjective(-1.);
                    c2.setObjective(-1.);
                    childs.add(c1);
                    childs.add(c2);
                } else {
                    childs.add(p1);
                    childs.add(p2);
                }
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
        if (this.nChilds > 0) {
            //tournament selection
            int a, b, A, B, temp;
            while (this.parents.size() < this.nChilds) {
                B = A = -1;
                for (int i = 0; i < rounds; i++) {
                    a = rand.nextInt(parents.size());
                    while ((b = rand.nextInt(parents.size())) == a) {
                        // repeats until a != b
                    }
                    if (A == -1) {
                        A = a;
                        B = b;
                        if (!Tools.isMaximization) {
                            if (parents.get(b).getObjective() < parents.get(a).getObjective()) {
                                A = b;
                                B = a;
                            }
                        } else {
                            if (parents.get(b).getObjective() > parents.get(a).getObjective()) {
                                A = b;
                                B = a;
                            }
                        }
                    } else {
                        if (!Tools.isMaximization) {
                            if (parents.get(b).getObjective() < parents.get(a).getObjective()) {
                                temp = a;
                                a = b;
                                b = temp;
                            }
                            if (parents.get(a).getObjective() < parents.get(A).getObjective()) {
                                B = A;
                                A = a;
                            }
                            if (parents.get(b).getObjective() < parents.get(B).getObjective()) {
                                B = b;
                            }
                        } else {
                            if (parents.get(b).getObjective() > parents.get(a).getObjective()) {
                                temp = a;
                                a = b;
                                b = temp;
                            }
                            if (parents.get(a).getObjective() > parents.get(A).getObjective()) {
                                B = A;
                                A = a;
                            }
                            if (parents.get(b).getObjective() > parents.get(B).getObjective()) {
                                B = b;
                            }
                        }
                    }
                }
                this.parents.add(parents.get(A));
                this.parents.add(parents.get(B));
            }
        }
    }

    @Override
    public void setNumberOfChilds(int nChilds) {
        this.nChilds = nChilds;
    }
}
