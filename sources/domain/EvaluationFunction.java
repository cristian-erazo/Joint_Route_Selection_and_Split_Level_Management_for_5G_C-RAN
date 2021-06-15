package domain;

import java.io.Serializable;
import java.util.List;

/**
 *
 * @author cristian.erazo@cinvestav.mx
 * @param <E> Type of return value (Range).
 * @param <Solution> Type of input elements (Domain).
 */
public interface EvaluationFunction<E, Solution> extends Serializable {

    /**
     * This function evaluates the quality of a solution.
     *
     * @param s Solution to be evaluated.
     * @return Solution quality.
     */
    public E evaluate(Solution s);

    /**
     * This function evaluates the quality of the entered solution list.
     *
     * @param p List of solutions (population).
     * @return Quality of the solutions evaluated (measure of central tendency).
     */
    public E evaluate(List<Solution> p);

    /**
     * This function determines whether or not you want to maximize the
     * function.
     *
     * @return <i>true</i> iff the function is marked as maximization.
     * <i>false</i> otherwise.
     */
    public boolean isMaximization();

    /**
     * Determine the diversity between two populations of individuals (lists of
     * solutions).
     *
     * @param p1 List to be compared.
     * @param p2 List to be compared.
     * @return Diversity value between the two populations. Zero means
     * populations without diversity.
     */
    public double evaluateDiversity(List<Solution> p1, List<Solution> p2);

    /**
     * This function allows you to get the current number of evaluations of the
     * function.
     *
     * @return Number of times the
     * {@link EvaluationFunction#evaluate(java.lang.Object) evaluate(s)} method was called.
     */
    public long EFO();
}
