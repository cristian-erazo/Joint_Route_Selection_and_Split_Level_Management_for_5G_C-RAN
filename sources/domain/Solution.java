package domain;

import java.io.Serializable;

/**
 *
 * @author cristian.erazo@cinvestav.mx
 * @param <E> Data type.
 */
public interface Solution<E> extends Serializable {

    /**
     * This function allows to obtain the data that represents a solution (an
     * individual).
     *
     * @return Array of data that represents the solution of the problem.
     */
    public E[] getData();
}
