package domain.monoobjective.implementation.metaheuristics.algorithms.mutation;

import domain.monoobjective.implementation.MatrixSolution;
import domain.operators.Mutation;
import domain.problem.ProblemInstance;
import domain.problem.virtual.VirtualNode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author cristian.erazo@cinvestav.mx
 * @param <T>
 */
public class MultiBit<T extends MatrixSolution> implements Mutation<T> {

    private T ind;
    private Random rand;
    private ProblemInstance p;
    private double probability;
    private List<Integer> indx;

    public MultiBit(Random rand, ProblemInstance p) {
        this.p = p;
        this.ind = null;
        this.rand = rand;
        probability = -1;
        indx = new ArrayList<>();
    }

    @Override
    public T run() {
        if (p != null && ind != null && p.min != null && p.max != null) {
            for (int i = 0; i < ind.getN(); i++) {
                if (rand.nextDouble() < probability) {
                    ind.accepted[i] = rand.nextDouble() < 0.5;
                }
                if (p.isFullyRepresentated()) {
                    for (VirtualNode vDU : p.requests[i].vDUs) {
                        indx.add(vDU.indx);
                        if (rand.nextDouble() < probability) {
                            ind.data[i][vDU.indx] = p.DUs.get(p.min.data[i][vDU.indx] + rand.nextInt(p.max.data[i][vDU.indx] - p.min.data[i][vDU.indx])).nodePosition;
                        }
                    }
                    for (VirtualNode vCU : p.requests[i].vCUs) {
                        indx.add(vCU.indx);
                        if (rand.nextDouble() < probability) {
                            ind.data[i][vCU.indx] = p.CUs.get(p.min.data[i][vCU.indx] + rand.nextInt(p.max.data[i][vCU.indx] - p.min.data[i][vCU.indx])).nodePosition;
                        }
                    }
                    for (int j = 0; j < ind.getM(); j++) {
                        if (ind.data[i][j] == -1) {
                            break;
                        }
                        if (!indx.contains(j)) {
                            if (rand.nextDouble() < probability) {
                                ind.data[i][j] = p.min.data[i][j] + rand.nextInt(p.max.data[i][j] - p.min.data[i][j]);
                            }
                        }
                    }
                    indx.clear();
                } else {
                    for (int j = 0; j < ind.getM(); j++) {
                        if (ind.data[i][j] == -1) {
                            break;
                        }
                        if (rand.nextDouble() < probability) {
                            ind.data[i][j] = p.min.data[i][j] + rand.nextInt(p.max.data[i][j] - p.min.data[i][j]);
                        }
                    }
                }
            }
            return ind;
        }
        return null;
    }

    @Override
    public void setIndividual(T ind) {
        this.ind = ind;
    }

    @Override
    public void setProbability(double probability) {
        this.probability = probability;
    }

}
