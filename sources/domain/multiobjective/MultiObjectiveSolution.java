package domain.multiobjective;

import domain.Solution;

/**
 *
 * @author cristian.erazo@cinvestav.mx
 * @param <E> Data type.
 * @param <T> Objective type.
 */
public interface MultiObjectiveSolution<E, T> extends Solution<E>, Comparable<MultiObjectiveSolution<E, T>> {

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

    @Override
    public int compareTo(MultiObjectiveSolution<E, T> o);

    public MultiObjectiveSolution copy();
}
