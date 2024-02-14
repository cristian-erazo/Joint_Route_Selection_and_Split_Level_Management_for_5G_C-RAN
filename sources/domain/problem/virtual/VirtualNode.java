package domain.problem.virtual;

import domain.problem.graph.Location;
import domain.problem.graph.Node;
import java.util.List;

/**
 *
 * @author cristian.erazo@cinvestav.mx
 */
public class VirtualNode extends Node {

    /**
     * The index (from the node list) of the physical node.
     */
    public int indxNode;
    /**
     * The index of the virtual node in the array representation.
     */
    public int indx;

    /**
     * This builder method instantiates a virtual node.
     *
     * @param indxNode The position of the physical node from the node list.
     * @param indx The position of this virtual node in the array
     * representation.
     * @param type The nodeType of the virtual node: 1=virtual DU, 2=virtual CU.
     * @param pos The index of this node, in the virtual node list.
     * @param location The coordinates of this virtual node.
     * @param nears List of virtual node neighbours.
     */
    public VirtualNode(int indxNode, int indx, int type, int pos, Location location, List<Integer> nears) {
        super(type, pos, location, nears);
        this.indxNode = indxNode;
        this.indx = indx;
    }

    @Override
    public String toString() {
        if (nodeType == 1) {
            return String.format("(%d|%d,%d)", nodePosition, prc, prb);
        } else {
            return String.format("(%d|%d)", nodePosition, prc);
        }
    }

    public VirtualNode copy() {
        VirtualNode copy = new VirtualNode(indxNode, indx, nodeType, prc, location, nears);
        copy.ant = ant;
        copy.prb = prb;
        copy.prc = prc;
        return copy;
    }
}
