package domain.paths.algorithms;

import domain.paths.PathSolution;
import domain.paths.SearchPath;
import domain.problem.graph.Link;
import domain.problem.graph.Node;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 *
 * @author cristian.erazo@cinvestav.mx
 */
public class ExhaustiveSearchPath extends SearchPath {

    private final boolean isFully;

    /**
     *
     * @param nodes List of all network nodes.
     * @param links List of all network links.
     * @param isFully If this parameter is false, then, all links are added in
     * the [0][0] index.
     */
    public ExhaustiveSearchPath(Node[] nodes, Link[][] links, boolean isFully) {
        this.nodes = nodes;
        this.DUs = new ArrayList<>();
        this.CUs = new ArrayList<>();
        for (Node nodo : nodes) {
            if (nodo.nodeType == 1) {
                DUs.add(nodo);
            } else if (nodo.nodeType == 2) {
                CUs.add(nodo);
            } else if (nodo.nodeType == 4) {
                DUs.add(nodo);
                CUs.add(nodo);
            }
        }
        this.links = links;
        this.isFully = isFully;
    }

    /**
     * This function obtains all routes for each DU to each CU.
     *
     * @return A matrix where each cell corresponds to a list of paths between a
     * CU node and a DU node.
     */
    @Override
    public List<PathSolution>[][] run() {
        List<PathSolution>[][] paths;
        if (isFully) {
            paths = new ArrayList[nodes.length][nodes.length];
        } else {
            paths = new ArrayList[1][1];
            paths[0][0] = new ArrayList<>();
        }
        nPaths = 0;
        if (isFully) {
            for (Node DU : DUs) {
                for (Node CU : CUs) {
                    paths[DU.nodePosition][CU.nodePosition] = new ArrayList<>();
                    getAllPaths(paths[DU.nodePosition][CU.nodePosition], DU, CU);
                    if (paths[DU.nodePosition][CU.nodePosition].isEmpty()) {
                        paths[DU.nodePosition][CU.nodePosition] = null;
                    }
                    paths[CU.nodePosition][DU.nodePosition] = paths[DU.nodePosition][CU.nodePosition];
                }
            }
        } else {
            for (Node DU : DUs) {
                for (Node CU : CUs) {
                    getAllPaths(paths[0][0], DU, CU);
                }
            }
        }
        return paths;
    }

    private void getAllPaths(List<PathSolution> paths, Node init, Node dest) {
        Queue<PathSolution> q = new ArrayDeque<>();
        PathSolution p = new PathSolution();
        p.addNodeToPath(init);
        q.add(p);
        while (!q.isEmpty()) {
            p = q.poll();
            Node currNode = p.getNodesOfPath().get(p.getNodesOfPath().size() - 1);
            if (currNode.nodePosition == dest.nodePosition) {
                paths.add(p);
                nPaths++;
            } else {
                for (int vecino : currNode.nears) {
                    if (!p.getNodesOfPath().contains(nodes[vecino])) {
                        PathSolution newpath = p.copy();
                        newpath.addNodeToPath(nodes[vecino]);
                        newpath.addLinkToPath(links[currNode.nodePosition][vecino]);
                        q.add(newpath);
                    }
                }
            }
        }
    }
}
