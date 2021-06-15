package domain.problem.graph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author cristian.erazo@cinvestav.mx
 */
public class Node implements Comparable<Node>, Serializable {

    /**
     * The nodeType of the node: 1=DU, 2=CU,3=v, 4=DUandCU.
     */
    public int nodeType;
    /**
     * The coordinates of this node.
     */
    public Location location;
    /**
     * The index of the position of this node, in the node list.
     */
    public int nodePosition;
    /**
     * This list is build using the position (in the node list) of the
     * neighbors' nodes.
     */
    public List<Integer> nears;
    /**
     * Maximum amount of computational resources.
     */
    public int prc;
    /**
     * The amount of computational resources currently used.
     */
    public int usedPRC;
    /**
     * Number of antennas (used only when nodeType=1 or nodeType=4).
     */
    public int ant;
    /**
     * The number of antennas currently used.
     */
    public int usedANT;
    /**
     * The amount of physical resource blocks (used only when nodeType=1 or
     * nodeType=4).
     */
    public int prb;
    /**
     * The amount of physical resource blocks currently used.
     */
    public int usedPRB;
    /**
     * Coverage radius (used only when nodeType=1 or nodeType=4).
     */
    public double theta;

    public Node() {
        nears = new ArrayList<>();
        nodeType = -1;
        nodePosition = -1;
        prc = -1;
        usedANT = 0;
        usedPRB = 0;
        usedPRC = 0;
    }

    /**
     *
     * @param type The nodeType of the node: 1=DU, 2=CU,3=v, 4=DU/CU
     * @param pos Index of the position of the node.
     * @param location The position of this node.
     * @param nears List of neighbours.
     * @param links List of links from fronthaul until destination.
     */
    public Node(int type, int pos, Location location, List<Integer> nears, List<Link> links) {
        this.nodeType = type;
        this.nodePosition = pos;
        this.location = location;
        this.nears = nears;
        usedANT = 0;
        usedPRB = 0;
        usedPRC = 0;
    }

    @Override
    public int compareTo(Node o) {
        return Integer.compare(nodePosition, o.nodePosition);
    }

    @Override
    public String toString() {
        if (nodeType == 1) {
            return String.format("(%d,%d,%d|%d,%d,%d)", prc, ant, prb, usedPRC, usedANT, usedPRB);
        } else {
            return String.format("(%d|%d)", prc, usedPRC);
        }
    }
}
