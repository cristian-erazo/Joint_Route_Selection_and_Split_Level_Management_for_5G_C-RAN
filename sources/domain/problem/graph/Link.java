package domain.problem.graph;

import java.io.Serializable;

/**
 *
 * @author cristian.erazo@cinvestav.mx
 */
public class Link implements Serializable {

    /**
     * The bandwith of the link.
     */
    public double bw;
    /**
     * The used bandwith of this link.
     */
    public double usedBw;
    /**
     * The processing time in milliseconds.
     */
    public double processingTime;
    /**
     * The length of the link.
     */
    public double linkLength;
    /**
     * The type of the link (1=fiber.2=copper.3=wireless).
     */
    public int linkType;
    /**
     * The relative cost of this link (relative to linkType).
     */
    public double linkCost;
    /**
     * The index of the source node.
     */
    public int source;
    /**
     * The index of the destination node.
     */
    public int destination;

    /**
     *
     * @param capacity The link bandwith.
     * @param processingTime The processing time in milliseconds.
     * @param linkLength The length of the link.
     * @param linkCost The relative cost of the link (relative to linkType).
     * @param linkType The link type. Where: 1. optical fiber link, 2. copper
     * link, and 3. wireless link.
     * @param source The index of the source node.
     * @param destination The index of the destination node.
     */
    public Link(double capacity, double processingTime, double linkLength, double linkCost, int linkType, int source, int destination) {
        this.bw = capacity;
        this.processingTime = processingTime;
        this.linkLength = linkLength;
        this.linkCost = linkCost;
        this.linkType = linkType;
        this.source = source;
        this.destination = destination;
        this.usedBw = 0;
    }

    @Override
    public String toString() {
        return String.format("(%.2f|%.2f)", bw, usedBw);
    }

    @Override
    public boolean equals(Object obj) {
        Link o = (Link) obj;
        return source == o.source && destination == o.destination;
    }

    public Link copySubInstance() {
        return new Link(bw - usedBw, processingTime, linkLength, linkCost, linkType, source, destination);
    }
}
