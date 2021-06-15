package domain.multiobjective.implementation;

import domain.multiobjective.MultiObjectiveSolution;

/**
 *
 * @author cristian.erazo@cinvestav.mx
 */
public class MultiObjectiveMatrixSolution implements MultiObjectiveSolution<Integer[], Double> {

    /**
     * The number of objectives.
     */
    private final int nObjectives;
    /**
     * The objectives values.
     */
    private Double[] objectives;
    /**
     * The number of resources requests.
     */
    private final int n;
    /**
     * The number of decisions to be made.
     */
    private final int m;
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

    public MultiObjectiveMatrixSolution(int n, int m, int nObjectives) {
        this.nObjectives = nObjectives;
        this.n = n;
        this.m = m;
        data = new Integer[n][m];
        objectives = new Double[nObjectives];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < m; j++) {
                data[i][j] = 0;
            }
        }
        for (int i = 0; i < nObjectives; i++) {
            objectives[i] = -1.;
        }
        nAccepted = 0;
        it = -1;
    }

    public void setnAccepted(int nAccepted) {
        this.nAccepted = nAccepted;
    }

    public int getnAccepted() {
        return nAccepted;
    }

    public boolean[] getAccepted() {
        return accepted;
    }

    public long getIt() {
        return it;
    }

    public int getM() {
        return m;
    }

    public int getN() {
        return n;
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
    public double getGn() {
        return gn;
    }

    @Override
    public void setObjective(int pos, Double val) {
        if (objectives != null && val != null && nObjectives > 0 && pos >= 0 && pos <= nObjectives) {
            objectives[pos] = val;
        }
    }

    @Override
    public int compareTo(MultiObjectiveSolution<Integer[], Double> o) {
        throw new UnsupportedOperationException("CompareTo not supported...");
    }

    @Override
    public MultiObjectiveSolution copy() {
        throw new UnsupportedOperationException("Copy not supported...");
    }

    @Override
    public Integer[][] getData() {
        return data;
    }

    @Override
    public boolean equals(Object obj) {
        throw new UnsupportedOperationException("Equals not supported...");
    }

    @Override
    public String toString() {
        throw new UnsupportedOperationException("To string not supported...");
    }
}
