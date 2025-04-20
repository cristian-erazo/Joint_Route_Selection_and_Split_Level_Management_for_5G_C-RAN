package domain.multiobjective.implementation;

import domain.Solution;
import domain.monoobjective.implementation.MatrixSolution;
import domain.multiobjective.MultiObjectiveSolution;
import java.util.Arrays;

/**
 *
 * @author cristian.erazo@cinvestav.mx
 */
public class MultiObjectiveMatrixSolution extends MatrixSolution implements MultiObjectiveSolution<Integer[], Double> {

    /**
     * The number of objectives.
     */
    protected int nObjectives;
    /**
     * The objectives values.
     */
    protected Double[] objectives;
    /**
     *
     */
    //public int rank;
    /**
     *
     */
    public double crowdingDistance;

    /**
     *
     */
    //public List<MultiObjectiveMatrixSolution> dominates;
    /**
     *
     */
    //public int numDominants;
    private MultiObjectiveMatrixSolution(int n, int m) {
        super(n, m, 0., 0);
        //rank = -1;
        crowdingDistance = -1.;
        //dominates = null;
        //numDominants = -1;
    }

    public MultiObjectiveMatrixSolution(MatrixSolution s, int nObjectives) {
        super(s.accepted, s.nAccepted, s.data, s.quality, s.it, s.gn, s.quality);
        this.nObjectives = nObjectives;
        this.crowdingDistance = -1;
        objectives = new Double[nObjectives];
        for (int i = 0; i < nObjectives; i++) {
            objectives[i] = -1.;
        }
    }

    public MultiObjectiveMatrixSolution(int n, int m, int nObjectives) {
        super(n, m);
        this.nObjectives = nObjectives;
        objectives = new Double[nObjectives];
        for (int i = 0; i < nObjectives; i++) {
            objectives[i] = -1.;
        }
        //rank = -1;
        crowdingDistance = -1.;
        //dominates = null;
        //numDominants = -1;
    }

    @Override
    public int getNumberOfObjectives() {
        return nObjectives;
    }

    @Override
    public Double[] getObjectives() {
        return objectives;
    }

    @Override
    public void setObjective(int pos, Double val) {
        if (objectives != null && val != null && nObjectives > 0 && pos >= 0 && pos < nObjectives) {
            objectives[pos] = val;
        }
    }

    @Override
    public int compareTo(Solution obj) {
        MultiObjectiveMatrixSolution o = (MultiObjectiveMatrixSolution) obj;
        /*int c = Integer.compare(rank, o.rank);
        if (c == 0) {*/
        return Double.compare(-crowdingDistance, -o.crowdingDistance);
        /*} else {
            return c;
        }*/
    }

    @Override
    public Solution copy() {
        MultiObjectiveMatrixSolution copy = new MultiObjectiveMatrixSolution(n, m);
        for (int i = 0; i < n; i++) {
            System.arraycopy(data[i], 0, copy.data[i], 0, m);
        }
        copy.it = it;
        copy.gn = gn;
        copy.isValid = isValid;
        copy.nAccepted = nAccepted;
        if (accepted != null) {
            copy.accepted = new boolean[accepted.length];
            System.arraycopy(accepted, 0, copy.accepted, 0, accepted.length);
        }
        copy.nObjectives = nObjectives;
        copy.objectives = new Double[nObjectives];
        System.arraycopy(objectives, 0, copy.objectives, 0, nObjectives);
        copy.objectiveValue = objectiveValue;
        //copy.rank = rank;
        copy.crowdingDistance = crowdingDistance;
        /*if (dominates != null) {
            copy.dominates = new ArrayList<>(dominates);
        }
        copy.numDominants = numDominants;*/
        return copy;
    }

    @Override
    public boolean equals(Object obj) {
        MultiObjectiveMatrixSolution o = (MultiObjectiveMatrixSolution) obj;
        if (o.n != n) {
            return false;
        }
        if (accepted == null || o.accepted == null) {
            for (int i = 0; i < data.length; i++) {
                if (!Arrays.equals(data[i], o.data[i])) {
                    return false;
                }
            }
        } else {
            for (int i = 0; i < data.length; i++) {
                if (accepted[i] != o.accepted[i]) {
                    return false;
                } else if (accepted[i]) {
                    if (!Arrays.equals(data[i], o.data[i])) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int acc = 0;
        sb.append(String.format("[%d] ", it));
        if (objectives != null) {
            for (int i = 0; i < nObjectives; i++) {
                sb.append(String.format("Ob%d: %f ", i + 1, objectives[i]));
            }
        }
        if (gn > 0) {
            sb.append(String.format("#constraint: %.2f ", gn));
        }
        sb.append("@");
        for (int i = 0; i < n; i++) {
            if (accepted[i]) {
                acc++;
                sb.append('*');
            }
            sb.append(String.format("[%d|", i));
            for (int j = 0; j < m; j++) {
                sb.append(String.format("%d", data[i][j]));
                if (j + 1 < m) {
                    sb.append(' ');
                }
            }
            sb.append("]");
        }
        return String.format("%b f(x): %f Accepted: %d Rejected: %d %s", isValid, objectiveValue, acc, n - acc, sb.toString());
    }

    @Override
    public int dominates(Solution obj, boolean isMaximization) {
        MultiObjectiveMatrixSolution o = (MultiObjectiveMatrixSolution) obj;
        if (o.nObjectives == nObjectives) {
            int c1 = 0, c2 = 0;
            for (int i = 0; i < nObjectives; i++) {
                if (isMaximization) {
                    if (objectives[i] > o.objectives[i]) {
                        c1++;
                    } else if (o.objectives[i] > objectives[i]) {
                        c2++;
                    }
                } else {
                    if (objectives[i] < o.objectives[i]) {
                        c1++;
                    } else if (o.objectives[i] < objectives[i]) {
                        c2++;
                    }
                }
            }
            if (c2 == 0 && c1 > 0) {
                return 1;
            }
            if (c1 == 0 && c2 > 0) {
                return -1;
            }
            return 0;
        } else {
            throw new IndexOutOfBoundsException();
        }
    }
}
