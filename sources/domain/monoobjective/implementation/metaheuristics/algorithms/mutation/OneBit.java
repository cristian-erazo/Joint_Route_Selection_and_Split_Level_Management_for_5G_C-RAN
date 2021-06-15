package domain.monoobjective.implementation.metaheuristics.algorithms.mutation;

import domain.monoobjective.implementation.MatrixSolution;
import domain.problem.ProblemInstance;
import domain.problem.virtual.VirtualNode;
import java.util.List;
import java.util.Random;

/**
 *
 * @author cristian.erazo@cinvestav.mx
 */
public class OneBit implements Mutation<MatrixSolution> {

    private ProblemInstance p;
    private MatrixSolution ind;
    private Random rand;
    private double prob;

    public OneBit(Random rand, ProblemInstance p) {
        prob = -1;
        this.p = p;
        this.ind = null;
        this.rand = rand;
    }

    @Override
    public MatrixSolution run() {
        if (p != null && ind != null && p.min != null && p.max != null) {
            int i = (int) (rand.nextDouble() * ind.getN()), j, id;
            do {
                j = (int) (rand.nextDouble() * (ind.getM() + 1));
                if (j == ind.getM()) {
                    ind.accepted[i] = !ind.accepted[i];
                    break;
                } else if (ind.data[i][j] != -1) {
                    int range = p.max.data[i][j] - p.min.data[i][j];
                    if (p.isFullyRepresentated()) {
                        if (existsIndex(j, p.requests[i].vCUs)) {
                            id = (int) (p.CUs.indexOf(p.nodes[ind.data[i][j]]) + prob * range);
                            if (id >= p.max.data[i][j]) {
                                id = p.min.data[i][j] + id % range;
                            }
                            ind.data[i][j] = p.CUs.get(id).nodePosition;
                        } else if (existsIndex(j, p.requests[i].vDUs)) {
                            id = (int) (p.DUs.indexOf(p.nodes[ind.data[i][j]]) + prob * range);
                            if (id >= p.max.data[i][j]) {
                                id = p.min.data[i][j] + id % range;
                            }
                            ind.data[i][j] = p.DUs.get(id).nodePosition;
                        } else {
                            ind.data[i][j] += (int) (prob * range);
                            if (ind.data[i][j] >= p.max.data[i][j]) {
                                ind.data[i][j] = p.min.data[i][j] + ind.data[i][j] % range;
                            }
                        }
                    } else {
                        ind.data[i][j] = (int) (ind.data[i][j] + prob * range);
                        if (ind.data[i][j] >= p.max.data[i][j]) {
                            ind.data[i][j] = p.min.data[i][j] + ind.data[i][j] % range;
                        }
                    }
                }
            } while (ind.data[i][j] == -1);
            return ind;
        }
        return null;
    }

    private boolean existsIndex(int indx, List<VirtualNode> list) {
        for (VirtualNode node : list) {
            if (node.indx == indx) {
                return true;
            } else if (node.indx > indx) {
                break;
            }
        }
        return false;
    }

    @Override
    public void setIndividual(MatrixSolution ind) {
        this.ind = ind;
    }

    @Override
    public void setProbability(double prob) {
        this.prob = prob;
    }
}
