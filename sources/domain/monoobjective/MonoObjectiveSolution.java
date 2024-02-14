package domain.monoobjective;

import domain.Solution;

/**
 *
 * @author cristian.erazo@cinvestav.mx
 * @param <E> Data type.
 * @param <T> Objective type.
 */
public interface MonoObjectiveSolution<E, T extends Comparable<T>> extends Solution<E>, Comparable<Solution> {

    /**
     * This function allows to obtain the objective value of the individual.
     *
     * @return The quality of the solution. If the individual has not been
     * evaluated by an objective function, the value will be -1.
     */
    public T getObjective();

    /**
     * This method allows you to set the quality of the solution.
     *
     * @param o Solution quality.
     */
    public void setObjective(T o);

    /**
     * This function allows you to obtain the number of constraints violated by
     * the solution. A feasible solution would return a value of 0.
     *
     * @return Number of constraints violated.
     */
    public double getGn();

    /**
     * This function allows you to compare this solution against another
     * solution.
     *
     * @param o Solution against which the comparison will be made.
     * @return An integer value.
     * @see Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(Solution o);

    /**
     * This function allows you to obtain a copy of the solution. To guarantee
     * that two objects are equal, you must override the operation of the
     * {@link Object#equals(java.lang.Object)} function.
     *
     * @return A solution whose attributes are equal to the current solution.
     */
    public Solution copy();
}
