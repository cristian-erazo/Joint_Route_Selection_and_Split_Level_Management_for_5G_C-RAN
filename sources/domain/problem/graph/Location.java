package domain.problem.graph;

import java.io.Serializable;

/**
 *
 * @author cristian.erazo@cinvestav.mx
 */
public class Location implements Serializable {

    /**
     * X-coordinate.
     */
    public double x;
    /**
     * Y-coordinate.
     */
    public double y;

    public Location() {
        x = -1;
        y = -1;
    }

    public Location(double x, double y) {
        this.x = x;
        this.y = y;
    }
}
