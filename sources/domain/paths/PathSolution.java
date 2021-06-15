package domain.paths;

import domain.problem.graph.Link;
import domain.problem.graph.Node;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author cristian.erazo@cinvestav.mx
 */
public class PathSolution implements Serializable, Comparable<PathSolution> {

    protected double delay;
    /**
     * The length of the path.
     */
    protected int length;
    /**
     * List of nodes of the path.
     */
    protected List<Node> path;
    /**
     * List of links of the path.
     */
    protected List<Link> links;
    /**
     * The maximum bw of the path.
     */
    protected double minBw;

    public PathSolution() {
        path = new ArrayList<>();
        links = new ArrayList<>();
        minBw = Integer.MAX_VALUE;
        length = 0;
        delay = 0;
    }

    /**
     *
     * @return
     */
    public List<Link> getLinks() {
        return links;
    }

    /**
     *
     * @return
     */
    public List<Node> getNodesOfPath() {
        return path;
    }

    /**
     *
     * @return The number of nodes in the path.
     */
    public int getLength() {
        return length;
    }

    /**
     *
     * @param e
     */
    public void addNodeToPath(Node e) {
        path.add(e);
        length++;
    }

    /**
     *
     * @param link
     */
    public void addLinkToPath(Link link) {
        links.add(link);
        if (link.bw < minBw) {
            minBw = link.bw;
        }
        delay += link.processingTime + (link.linkLength / (link.linkType == 2 ? 200. : 300.)) + (1200. / link.bw);
    }

    /**
     *
     * @param node
     */
    public void removeNode(Node node) {
        if (path.remove(node)) {
            length--;
        }
    }

    /**
     *
     * @param link
     */
    public void removeLink(Link link) {
        links.remove(link);
        delay -= link.processingTime + (link.linkLength / (link.linkType == 2 ? 200. : 300.)) + (1200. / link.bw);
        if (minBw == link.bw) {//update minBw
            minBw = Integer.MAX_VALUE;
            for (Link x : links) {
                if (x.bw < minBw) {
                    minBw = x.bw;
                }
            }
        }
    }

    /**
     * This function returns the current bandwidth of the path.
     *
     * @return the maximum bandwith of the path.
     */
    public double getMinBw() {
        return minBw;
    }

    /**
     * This function returns the current delay of the path.
     *
     * @return the delay of the path in milliseconds.
     */
    public double getDelay() {
        return delay;
    }

    /**
     * This function generates a copy of this path solution.
     *
     * @return A copy of the path solution.
     */
    public PathSolution copy() {
        PathSolution copy = new PathSolution();
        copy.path.addAll(path);
        copy.links.addAll(links);
        copy.length = length;
        copy.minBw = minBw;
        copy.delay = delay;
        return copy;
    }

    /**
     * This function determines if the paths share some link.
     *
     * @param p The path destination be evaluated.
     * @return true iff at least one link is shared between the input path and
     * the path of this solution. false otherwise.
     */
    public boolean containsLinks(PathSolution p) {
        for (Link l : p.getLinks()) {
            if (links.contains(l)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        double minBwAva = Integer.MAX_VALUE, ava;
        for (Link link : links) {
            ava = link.bw - link.usedBw;
            if (ava < minBwAva) {
                minBwAva = ava;
            }
        }
        return String.format("(%.2f,%.2f)", minBwAva, delay);
    }

    @Override
    public boolean equals(Object obj) {
        PathSolution p = (PathSolution) obj;
        if (p.getLength() == length) {
            return p.getLinks().containsAll(links);
        } else {
            return false;
        }
    }

    @Override
    public int compareTo(PathSolution o) {
        return Double.compare(minBw, o.minBw);
    }
}
