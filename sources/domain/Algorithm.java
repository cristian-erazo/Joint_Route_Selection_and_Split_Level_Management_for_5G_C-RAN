package domain;

import java.io.Serializable;

/**
 *
 * @author cristian.erazo@cinvestav.mx
 */
public interface Algorithm extends Serializable {

    /**
     * This function starts the execution of the algorithm.
     * @return The result of running the algorithm.
     */
    public Object run();
}
