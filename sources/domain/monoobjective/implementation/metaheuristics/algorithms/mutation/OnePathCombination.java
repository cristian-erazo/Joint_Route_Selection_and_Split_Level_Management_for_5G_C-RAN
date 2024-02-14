package domain.monoobjective.implementation.metaheuristics.algorithms.mutation;

import domain.monoobjective.implementation.MatrixSolution;
import domain.operators.Mutation;
import domain.problem.ProblemInstance;
import domain.problem.virtual.VirtualNode;
import java.util.Random;

/**
 *
 * @author cristian.erazo@cinvestav.mx
 */
public class OnePathCombination implements Mutation<MatrixSolution> {

    private MatrixSolution ind;
    private ProblemInstance p;
    private Random rand;
    private double prob;

    public OnePathCombination(Random rand, ProblemInstance problemInstance) {
        prob = -1;
        this.ind = null;
        this.rand = rand;
        this.p = problemInstance;
    }

    @Override
    public MatrixSolution run() {
        if (ind != null) {
            for (int i = 0; i < ind.getN(); i++) {
                if (rand.nextDouble() <= prob) {
                    ind.accepted[i] = rand.nextDouble() < 0.5;
                }
                if (p.isFullyRepresentated()) {
                    for (VirtualNode vDU : p.requests[i].vDUs) {
                        if (rand.nextDouble() < prob) {
                            ind.data[i][vDU.indx + p.pathPosition] = p.DUs.get(p.min.data[i][vDU.indx + p.pathPosition] + rand.nextInt(p.max.data[i][vDU.indx + p.pathPosition] - p.min.data[i][vDU.indx + p.pathPosition])).nodePosition;
                            ind.data[i][vDU.indx + p.splitPosition] = p.DUs.get(p.min.data[i][vDU.indx + p.splitPosition] + rand.nextInt(p.max.data[i][vDU.indx + p.splitPosition] - p.min.data[i][vDU.indx + p.splitPosition])).nodePosition;
                        }
                    }
                } else {
                    for (int j = 0; j < ind.getM(); j += p.step) {
                        if (ind.data[i][j] == -1) {
                            break;
                        }
                        if (rand.nextDouble() <= prob) {
                            ind.setObjective(-1.);
                            ind.data[i][j + p.pathPosition] = rand.nextInt(p.mPaths[0][0].size());
                            ind.data[i][j + p.splitPosition] = rand.nextInt(p.splits.length);
                        }
                    }
                }
            }
        }
        return ind;
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
