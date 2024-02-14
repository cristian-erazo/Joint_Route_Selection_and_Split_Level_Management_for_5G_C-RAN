package domain.monoobjective.implementation.greedy;

import domain.EvaluationFunction;
import domain.monoobjective.MonoObjectiveAlgorithm;
import domain.monoobjective.implementation.MatrixSolution;
import domain.problem.ProblemInstance;
import domain.problem.graph.Link;
import domain.problem.graph.Node;
import domain.problem.virtual.VirtualNode;
import java.util.TreeSet;

/**
 *
 * @author cristian.erazo@cinvestav.mx
 */
public abstract class GreedyAlgorithm implements MonoObjectiveAlgorithm<Integer[], Double> {

    /**
     * The instance of the problem to be solved.
     */
    protected final ProblemInstance instance;
    /**
     * The objective function.
     */
    protected final EvaluationFunction<Double, MatrixSolution> fx;
    /**
     * The best solution found.
     */
    protected MatrixSolution best;
    /**
     *
     */
    protected TreeSet<Node> tCUs;
    /**
     *
     */
    protected TreeSet<Node> tDUs;

    public GreedyAlgorithm(ProblemInstance instance, EvaluationFunction<Double, MatrixSolution> fx) {
        this.instance = instance;
        this.fx = fx;
        tCUs = new TreeSet<>();
        tDUs = new TreeSet<>();
    }

    @Override
    public abstract MatrixSolution run();

    protected void initialize() {
        int pos;
        tCUs.clear();
        tDUs.clear();
        if (instance.isFullyRepresentated()) {
            best = new MatrixSolution(instance.requests.length, instance.step * instance.maxVirtualDUs + instance.maxVirtualCUs);
            best.accepted = new boolean[instance.requests.length];
            for (int k = 0; k < instance.requests.length; k++) {//para cada peticion, asignar el orden inicial y las posiciones de la matriz
                pos = 0;//cuenta las posiciones de la columna para asignarla al grafo
                for (VirtualNode vCU : instance.requests[k].vCUs) {//para cada nodo vCU, asignar la posicion a la que le corresponde en la matriz
                    vCU.indx = pos;//asignar la posicion de la columna al nodo vCU
                    pos++;//la posicion de la DU sera la inmediatamente siguiente
                    for (Integer vdu : vCU.nears) {//obtener los nodos DU a los que se conecta (cada CU se conecta por un enlace)
                        instance.requests[k].vNodes[vdu].indx = pos;//asignar posiciones relativas al nodo vCU
                        pos += instance.step;//la siguiente posicion dependera del tipo de representacion
                    }
                }
                for (int i = pos; i < best.getM(); i++) {
                    best.data[k][i] = -1;
                }
            }
        } else {
            best = new MatrixSolution(instance.requests.length, instance.step * instance.maxVirtualDUs);
            best.accepted = new boolean[instance.requests.length];
            for (int k = 0; k < instance.requests.length; k++) {//para cada peticion, asignar el orden inicial y las posiciones de la matriz
                pos = 0;//cuenta las posiciones de la columna para asignarla al grafo
                for (VirtualNode vDU : instance.requests[k].vDUs) {//obtener los nodos DU a los que se conecta (cada CU se conecta por un enlace)
                    for (Integer vcu : vDU.nears) {
                        if (instance.requests[k].vNodes[vcu].indx == -1) {
                            instance.requests[k].vNodes[vcu].indx = pos;
                        }
                    }
                    vDU.indx = pos;//asignar posiciones relativas al nodo vCU
                    pos += instance.step;//la siguiente posicion dependera del tipo de representacion
                }
                for (int i = pos; i < best.getM(); i++) {
                    best.data[k][i] = -1;
                }
            }
        }
    }

    /**
     *
     * @param splitIndx
     * @param minLink
     * @param pathDelay
     * @param maxDelay
     * @return
     */
    protected int mejorarSplit(int splitIndx, Link minLink, double pathDelay, double maxDelay) {
        //la ruta es valida, elegir el mejor split
        int i = splitIndx + 1;
        while (i < instance.splits.length && (instance.splits[i].bw + minLink.usedBw) <= minLink.bw && pathDelay <= instance.splits[i].delay && pathDelay <= maxDelay) {
            splitIndx = i;
            i++;
        }
        return splitIndx;
    }
}
