package domain.paths;

import domain.Algorithm;
import domain.problem.graph.Link;
import domain.problem.graph.Node;
import java.util.List;

/**
 *
 * @author cristian.erazo@cinvestav.mx
 */
public abstract class SearchPath implements Algorithm {

    /**
     * Vector of nodes.
     */
    protected Node[] nodes;
    /**
     * List of DUs nodes.
     */
    protected List<Node> DUs;
    /**
     * List of CUs nodes.
     */
    protected List<Node> CUs;
    /**
     * Matrix of all links between nodes.
     */
    protected Link[][] links;
    /**
     * The total paths.
     */
    protected long nPaths;

    /**
     *
     * @return The total paths found.
     */
    public long getnPaths() {
        return nPaths;
    }

    @Override
    public abstract List<PathSolution>[][] run();
}
