package domain.monoobjective.implementation;

import domain.Solution;
import domain.monoobjective.MonoObjectiveSolution;
import domain.util.Tools;
import java.util.Arrays;

/**
 *
 * @author cristian.erazo@cinvestav.mx
 */
public class MatrixSolution implements MonoObjectiveSolution<Integer[], Double> {

    /**
     * The centralization level.
     */
    protected Double objectiveValue;
    /**
     * The number of resources requests.
     */
    protected final int n;
    /**
     * The number of decisions to be made.
     */
    protected final int m;
    /**
     * The configuration for the network.
     */
    public Integer[][] data;
    /**
     * Determine if the request is accepted.
     */
    public boolean[] accepted;
    /**
     * The number of requests accepted.
     */
    public int nAccepted;
    /**
     * Determine if the solution is valid.
     */
    public boolean isValid;
    /**
     * The number of not-valid constrains.
     */
    public double gn;
    /**
     * The iteration where the solution was made.
     */
    public long it;
    /**
     * The solution quality.
     */
    public double quality;

    public MatrixSolution(boolean[] accepted, int nAccepted, Integer[][] data, double objectiveValue, long it, double gn, double quality) {
        this.it = it;
        this.gn = gn;
        this.data = data;
        this.n = data.length;
        this.isValid = gn == 0;
        this.quality = quality;
        this.m = data[0].length;
        this.accepted = accepted;
        this.nAccepted = nAccepted;
        this.objectiveValue = objectiveValue;
    }

    public MatrixSolution(int n, int m, double objectiveValue, long it) {
        this.n = n;
        this.m = m;
        this.it = it;
        data = new Integer[n][m];
        this.objectiveValue = objectiveValue;
    }

    /**
     *
     * @param n The number of rows (requests).
     * @param m The number of columns (decisions).
     */
    public MatrixSolution(int n, int m) {
        this.objectiveValue = -1.0;
        this.n = n;
        this.m = m;
        data = new Integer[n][m];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                data[i][j] = 0;
            }
        }
        nAccepted = 0;
        it = -1;
    }

    /**
     *
     * @return The number of columns.
     */
    public int getM() {
        return m;
    }

    /**
     *
     * @return The number of rows.
     */
    public int getN() {
        return n;
    }

    /**
     *
     * @return The vector of accepted requests.
     */
    public boolean[] getAccepted() {
        return accepted;
    }

    /**
     *
     * @return The number of accepted requests.
     */
    public int getnAccepted() {
        return nAccepted;
    }

    /**
     *
     * @param nAccepted The number of accepted requests.
     */
    public void setnAccepted(int nAccepted) {
        this.nAccepted = nAccepted;
    }

    @Override
    public Integer[][] getData() {
        return data;
    }

    @Override
    public Double getObjective() {
        return objectiveValue;
    }

    @Override
    public void setObjective(Double objectiveValue) {
        this.objectiveValue = objectiveValue;
    }

    @Override
    public Solution copy() {
        MatrixSolution copy = new MatrixSolution(n, m, objectiveValue, it);
        for (int i = 0; i < n; i++) {
            System.arraycopy(data[i], 0, copy.data[i], 0, m);
        }
        copy.quality = quality;
        copy.gn = gn;
        copy.isValid = isValid;
        copy.nAccepted = nAccepted;
        if (accepted != null) {
            copy.accepted = new boolean[accepted.length];
            System.arraycopy(accepted, 0, copy.accepted, 0, accepted.length);
        }
        return copy;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int acc = 0;
        for (int i = 0; i < n; i++) {
            if (accepted[i]) {
                acc++;
                sb.append('*');
            }
            sb.append(String.format("[%d|", i));
            for (int j = 0; j < m; j++) {
                sb.append(String.format("%d", data[i][j]));
                if (j + 1 < m) {
                    sb.append(',');
                }
            }
            sb.append("]");
        }
        return String.format("[%d]%b %.0ff(x): %f Accepted: %d Rejected: %d Q(x): %f@%s", it, isValid, gn, objectiveValue, acc, n - acc, quality, sb.toString());
    }

    @Override
    public boolean equals(Object obj) {
        MatrixSolution o = (MatrixSolution) obj;
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
    public int compareTo(Solution obj) {
        MonoObjectiveSolution<Integer[], Double> o = (MonoObjectiveSolution<Integer[], Double>) obj;
        if (getObjective() != null && o.getObjective() != null) {
            if (Tools.isMaximization) {
                return o.getObjective().compareTo(getObjective());
            } else {
                return getObjective().compareTo(o.getObjective());
            }
        } else {
            throw new NullPointerException("The objective is uninitialized.");
        }
    }

    public double distance(MatrixSolution s) {
        double distance = 0, dif;
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                if (data[i][j] == -1 && s.data[i][j] == -1) {
                    break;
                }
                dif = data[i][j] - s.data[i][j];
                distance += dif * dif;
            }
        }
        return Math.sqrt(distance);
    }

    @Override
    public double getGn() {
        return gn;
    }
}
