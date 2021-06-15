package domain.problem.virtual;

import java.util.List;

/**
 *
 * @author cristian.erazo@cinvestav.mx
 */
public class Request {

    /**
     * List of CUs nodes.
     */
    public List<VirtualNode> vCUs;
    /**
     * List of DUs nodes.
     */
    public List<VirtualNode> vDUs;
    /**
     * All virtual nodes.
     */
    public VirtualNode[] vNodes;
    /**
     * Matrix of all links between virtual nodes.
     */
    public VirtualLink[][] vLinks;
    /**
     * List of all links between virtual nodes.
     */
    public List<VirtualLink> virtualLinks;
}
