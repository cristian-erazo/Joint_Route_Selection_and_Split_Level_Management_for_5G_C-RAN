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
     * Processing capacity required in the CU node.
     */
    public int CUprc;
    /**
     * Processing capacity required in the DU node.
     */
    public int DUprc;

    /**
     *
     * @param type Type of flow: 1 (Fronthaul eCPRI), 2 (Fronthaul after XPU
     * usage) or 3 (Backhaul).
     * @param bw Bandwidth parameter in Mbps.
     * @param delay Delay parameter in milliseconds.
     * @param c Class parameter (1 or 2).
     * @param CUprc
     * @param DUprc
     */
    public Flow(int type, double bw, double delay, int c, int CUprc, int DUprc) {
        this.type = type;
        this.bw = bw;
        this.delay = delay;
        this.c = c;
        this.CUprc = CUprc;
        this.DUprc = DUprc;
    }

    @Override
    public String toString() {
        return String.format("(%.2f,%.2f,%d,%d)", bw, delay, CUprc, DUprc);
    }
}
