package domain.multiobjective;

import domain.Solution;

/**
 *
 * @author cristian.erazo@cinvestav.mx
 * @param <E> Data type.
 * @param <T> Objective type.
 */
public interface MultiObjectiveSolution<E, T extends Comparable> extends Solution<E>, Comparable<Solution> {

    /**
     *
     * @return
     */
    public int getNumberOfObjectives();

    /**
     *
     * @return
     */
    public T[] getObjectives();

    /**
     *
     * @return
     */
    public double getGn();

    /**
     *
     * @param pos
     * @param val
     */
    public void setObjective(int pos, T val);

    /**
     *
     * @param s
     * @param isMaximization
     * @return
     */
    public int dominates(Solution s, boolean isMaximization);

    @Override
    public int compareTo(Solution o);

    public Solution copy();
}
