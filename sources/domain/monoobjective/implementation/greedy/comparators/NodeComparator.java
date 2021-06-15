package domain.monoobjective.implementation.greedy.comparators;

import domain.problem.graph.Node;
import java.util.Comparator;

/**
 *
 * @author cristian.erazo@cinvestav.mx
 */
public abstract class NodeComparator implements Comparator<Node> {

    protected Node base;

    public NodeComparator() {
        base = null;
    }

    public void setBase(Node base) {
        this.base = base;
    }

    @Override
    public abstract int compare(Node o1, Node o2);
}
