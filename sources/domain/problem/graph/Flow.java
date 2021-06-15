package domain.problem.graph;

/**
 *
 * @author cristian.erazo@cinvestav.mx
 */
public class Flow {

    /**
     * Type of flow: 1 (Fronthaul eCPRI), 2 (Fronthaul after XPU usage) or 3
     * (Backhaul).
     */
    public int type;
    /**
     * Bandwidth parameter in Mbps.
     */
    public double bw;
    /**
     * Delay parameter in milliseconds.
     */
    public double delay;
    /**
     * Class parameter (1 or 2).
     */
    public int c;

    /**
     *
     * @param type Type of flow: 1 (Fronthaul eCPRI), 2 (Fronthaul after XPU
     * usage) or 3 (Backhaul).
     * @param bw Bandwidth parameter in Mbps.
     * @param delay Delay parameter in milliseconds.
     * @param c Class parameter (1 or 2).
     */
    public Flow(int type, double bw, double delay, int c) {
        this.type = type;
        this.bw = bw;
        this.delay = delay;
        this.c = c;
    }

    @Override
    public String toString() {
        return String.format("(%.2f,%.2f)", bw, delay);
    }
}
