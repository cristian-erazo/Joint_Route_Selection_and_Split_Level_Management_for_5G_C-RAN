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
        x = Double.NaN;
        y = Double.NaN;
    }

    public Location(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return String.format("(%.2g, %.2g)", x, y);
    }
}
