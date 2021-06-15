package domain.monoobjective.implementation.metaheuristics.algorithms.crossover;

import domain.monoobjective.implementation.MatrixSolution;
import domain.problem.ProblemInstance;
import domain.problem.virtual.VirtualNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 *
 * @author cristian.erazo@cinvestav.mx
 */
public class LaplaceCrossover implements Crossover<MatrixSolution> {

    private List<MatrixSolution> parents;
    private ProblemInstance instance;
    private List<Integer> indx;
    private final double prob;
    private final double a;
    private final double b;
    private Random rand;
    private int nChilds;

    public LaplaceCrossover(Random rand, ProblemInstance instance, double prob, double a, double b) {
        this.parents = new ArrayList<>();
        this.indx = new ArrayList<>();
        this.instance = instance;
        this.prob = prob;
        this.rand = rand;
        this.a = a;
        this.b = b;
    }

    @Override
    public List<MatrixSolution> run() {
        if (nChilds > 0 && !parents.isEmpty() && parents != null && nChilds <= parents.size()) {
            int k = 0, n = parents.get(0).getN(), m = parents.get(0).getM(), res, i, j;
            double u, Betha;
            List<MatrixSolution> childs = new ArrayList<>();
            Collections.shuffle(parents, rand);
            while (childs.size() < nChilds) {
                MatrixSolution p1 = parents.get(k);
                k = (k + 1) % parents.size();
                MatrixSolution p2 = parents.get(k);
                k = (k + 1) % parents.size();
                MatrixSolution c1 = p1.copy();
                MatrixSolution c2 = p2.copy();
                if (instance.isFullyRepresentated()) {
                    int defP1, defP2;
                    for (i = 0; i < n; i++) {
                        for (VirtualNode vDU : instance.requests[i].vDUs) {
                            j = vDU.indx;
                            indx.add(j);
                            defP1 = instance.DUs.indexOf(instance.nodes[p1.data[i][j]]);
                            defP2 = instance.DUs.indexOf(instance.nodes[p2.data[i][j]]);
                            u = rand.nextDouble();
                            Betha = (u > 0.5 ? a + b * Math.log(u) : a - b * Math.log(u));
                            res = (defP1 > defP2 ? defP1 - defP2 : defP2 - defP1);
                            if (rand.nextDouble() < prob) {
                                c1.data[i][j] = instance.DUs.get(adjustValue(defP1 + Betha * res, i, j)).nodePosition;
                            }
                            if (rand.nextDouble() < prob) {
                                c2.data[i][j] = instance.DUs.get(adjustValue(defP2 + Betha * res, i, j)).nodePosition;
                            }
                        }
                        for (VirtualNode vCU : instance.requests[i].vCUs) {
                            j = vCU.indx;
                            indx.add(j);
                            defP1 = instance.CUs.indexOf(instance.nodes[p1.data[i][j]]);
                            defP2 = instance.CUs.indexOf(instance.nodes[p2.data[i][j]]);
                            u = rand.nextDouble();
                            Betha = (u > 0.5 ? a + b * Math.log(u) : a - b * Math.log(u));
                            res = (defP1 > defP2 ? defP1 - defP2 : defP2 - defP1);
                            if (rand.nextDouble() < prob) {
                                c1.data[i][j] = instance.CUs.get(adjustValue(defP1 + Betha * res, i, j)).nodePosition;
                            }
                            if (rand.nextDouble() < prob) {
                                c2.data[i][j] = instance.CUs.get(adjustValue(defP2 + Betha * res, i, j)).nodePosition;
                            }
                        }
                        for (j = 0; j < m; j++) {
                            if (c1.data[i][j] == -1 || c2.data[i][j] == -1) {
                                break;
                            }
                            if (!indx.contains(j)) {
                                u = rand.nextDouble();
                                Betha = (u > 0.5 ? a + b * Math.log(u) : a - b * Math.log(u));
                                res = (p1.data[i][j] > p2.data[i][j] ? p1.data[i][j] - p2.data[i][j] : p2.data[i][j] - p1.data[i][j]);
                                if (rand.nextDouble() < prob) {
                                    c1.data[i][j] = adjustValue(p1.data[i][j] + Betha * res, i, j);
                                }
                                if (rand.nextDouble() < prob) {
                                    c2.data[i][j] = adjustValue(p2.data[i][j] + Betha * res, i, j);
                                }
                            }
                        }
                        indx.clear();
                    }
                } else {
                    for (i = 0; i < n; i++) {
                        for (j = 0; j < m; j++) {
                            if (c1.data[i][j] == -1 || c2.data[i][j] == -1) {
                                break;
                            }
                            u = rand.nextDouble();
                            Betha = (u > 0.5 ? a + b * Math.log(u) : a - b * Math.log(u));
                            res = (p1.data[i][j] > p2.data[i][j] ? p1.data[i][j] - p2.data[i][j] : p2.data[i][j] - p1.data[i][j]);
                            if (rand.nextDouble() < prob) {
                                c1.data[i][j] = adjustValue(p1.data[i][j] + Betha * res, i, j);
                            }
                            if (rand.nextDouble() < prob) {
                                c2.data[i][j] = adjustValue(p2.data[i][j] + Betha * res, i, j);
                            }
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

    private int adjustValue(double newValue, int i, int j) {
        if (newValue - ((double) ((int) newValue)) > 0.000000001 && rand.nextDouble() <= 0.5) {
            newValue += 1;
        }
        /*if (newValue > instance.max.data[i][j]) {
            newValue = instance.max.data[i][j] - 1;
        }
        if (newValue < instance.min.data[i][j]) {
            newValue = instance.min.data[i][j];
        }*/
        if (newValue < instance.min.data[i][j] || newValue > instance.max.data[i][j]) {
            if (newValue < 0) {
                newValue *= -1;
            }
            newValue = instance.min.data[i][j] + (newValue % (instance.max.data[i][j] - instance.min.data[i][j]));
        }
        /*if (newValue > instance.max.data[i][j]) {
            newValue = instance.min.data[i][j];
        }
        if (newValue < instance.min.data[i][j]) {
            newValue = instance.max.data[i][j] - 1;
        }*/
        return (int) newValue;
    }

    @Override
    public void setParents(List<MatrixSolution> parents) {
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
