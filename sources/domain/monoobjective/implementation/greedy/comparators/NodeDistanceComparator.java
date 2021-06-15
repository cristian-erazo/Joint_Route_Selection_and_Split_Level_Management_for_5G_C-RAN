package domain.monoobjective.implementation.greedy.comparators;

import domain.problem.graph.Location;
import domain.problem.graph.Node;

/**
 *
 * @author cristian.erazo@cinvestav.mx
 */
public class NodeDistanceComparator extends NodeComparator {

    @Override
    public int compare(Node x, Node y) {
        if ((x.nodeType == 1 || x.nodeType == 4) && (y.nodeType == 1 || y.nodeType == 4)) {//compare DU
            return Double.compare(dist(x.location), dist(y.location));
        }//compare other nodes by CompareTo
        return x.compareTo(y);
    }

    private double dist(Location dest) {
        double x = base.location.x - dest.x, y = base.location.y - dest.y;
        return Math.sqrt(x * x + y * y);
    }
}