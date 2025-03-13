package domain.multiobjective.implementation.functions;

import domain.EvaluationFunction;
import domain.monoobjective.implementation.MatrixSolution;
import domain.multiobjective.MultiObjectiveFunction;
import domain.multiobjective.implementation.MultiObjectiveMatrixSolution;
import domain.problem.ProblemInstance;
import domain.problem.graph.Link;
import domain.problem.graph.Node;
import domain.problem.virtual.Request;
import domain.problem.virtual.VirtualNode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author D_mon
 */
public class CentralizationDegreeAndAcceptanceRate extends MultiObjectiveFunction {

    protected final double w1;
    protected final double w2;
    protected final double w3;
    protected List<List<Node>> pools;
    protected List<Link> usedLinks;

    public CentralizationDegreeAndAcceptanceRate(double w1, double w2, double w3, ProblemInstance instance, boolean isMaximization) {
        super(instance, isMaximization, 2);
        this.w1 = w1;
        this.w2 = w2;
        this.w3 = w3;
        pools = new ArrayList<>();
        usedLinks = new ArrayList<>();
    }

    @Override
    public Double eval(MultiObjectiveMatrixSolution s, int objID) {
        switch (objID) {
            case 0://Centralization
                return eval(s);
            case 1://Acceptance
                return ((double) s.nAccepted);
        }
        return 0.;
    }

    @Override
    public Double eval(MultiObjectiveMatrixSolution s) {
        double BBUs = 0, nvPHY = 0, nvMAC = 0, nvNW = 0, normUsedBw = 0;
        int mPrima = 0;//total de DUs solicitados.
        pools.add(new ArrayList<Node>());
        for (int i = 0; i < instance.requests.length; i++) {
            mPrima += instance.requests[i].vDUs.size();
            if (s.accepted[i]) {//si la peticion ha sido aceptada ...
                // evaluate BBUpools & splits functions
                for (VirtualNode vCU : instance.requests[i].vCUs) {
                    if (!wasVisited(pools, instance.nodes[vCU.indxNode]) && !hasGroup(pools, vCU)) {
                        BBUs++;
                    }
                }
                for (VirtualNode vDU : instance.requests[i].vDUs) {
                    int split = s.data[i][vDU.indx + instance.splitPosition];
                    if (split > 5) {
                        nvPHY++;
                    } else if (split > 1 && split < 6) {
                        nvMAC++;
                    } else {
                        nvNW++;
                    }
                }
                // evaluate bandwith for each virtual link
                if (instance.isFullyRepresentated()) {
                    for (VirtualNode vCU : instance.requests[i].vCUs) {
                        for (Integer vdu : vCU.nears) {
                            if (instance.mPaths[instance.requests[i].vNodes[vdu].indxNode][vCU.indxNode] != null && s.data[i][instance.requests[i].vNodes[vdu].indx + instance.pathPosition] < instance.mPaths[instance.requests[i].vNodes[vdu].indxNode][vCU.indxNode].size()) {
                                for (Link physicalLink : instance.mPaths[instance.requests[i].vNodes[vdu].indxNode][vCU.indxNode].get(s.data[i][instance.requests[i].vNodes[vdu].indx + instance.pathPosition]).getLinks()) {
                                    if (!usedLinks.contains(physicalLink)) {
                                        normUsedBw += physicalLink.linkCost * physicalLink.usedBw / physicalLink.bw;
                                        usedLinks.add(physicalLink);
                                    }
                                }
                            }
                        }
                    }
                } else {
                    for (VirtualNode vDU : instance.requests[i].vDUs) {
                        for (Link physicalLink : instance.mPaths[0][0].get(s.data[i][vDU.indx + instance.pathPosition]).getLinks()) {
                            if (!usedLinks.contains(physicalLink)) {
                                normUsedBw += physicalLink.linkCost * physicalLink.usedBw / physicalLink.bw;
                                usedLinks.add(physicalLink);
                            }
                        }
                    }
                }
            }
        }
        if (!pools.isEmpty()) {
            pools.clear();
        }
        if (!usedLinks.isEmpty()) {
            usedLinks.clear();
        }
        if (BBUs == 0) {
            BBUs = 1;
        }
        return ((w1 * nvPHY + w2 * nvMAC + w3 * nvNW) / (mPrima * BBUs)) * (1.0 - (normUsedBw / (double) instance.nLinks));
    }

    /**
     * This function validates if a physical node was visited.
     *
     * @param pools The list of pools.
     * @param cu The node to be validated.
     * @return true iff the node belongs to any pool, false otherwise.
     */
    private boolean wasVisited(List<List<Node>> pools, Node cu) {
        for (List<Node> pool : pools) {
            if (pool.contains(cu)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasGroup(List<List<Node>> pools, VirtualNode node) {
        List<Node> newPool = new ArrayList<>();
        Node physicalNode = instance.nodes[node.indxNode];
        boolean hasGroup = false;
        newPool.add(physicalNode);//agregar al pool el nodo fisico
        for (Integer v : physicalNode.nears) {//ubicar a los vecinos dentro del mismo pool si han sido asignados
            agregarVecinos(v, newPool, instance.nodes[v]);
        }
        //ubicar a todos los elementos en algun pool existente o agregarlos a uno nuevo
        Collections.sort(newPool);
        for (List<Node> pool : pools) {
            if (pool.isEmpty()) {//no hay grupos formados
                hasGroup = false;
                pool.addAll(newPool);
                break;
            } else {
                for (Node n : newPool) {
                    if (pool.contains(n)) {//agregar los nodos que son vecinos al pool (sin repetir nodos)
                        hasGroup = true;
                        addNeighbourhood(pool, newPool);
                        break;
                    }
                }
            }
        }
        if (!hasGroup) {//agregar nuevo pool
            pools.add(newPool);
        }
        return hasGroup;
    }

    /**
     * This method adds all nodes from the graph which are related and assigned
     * to the same pool, exploring the neighbors of the input node.
     *
     * @param curr Index of current node.
     * @param pool List of nodes in the pool.
     * @param vecino Current node.
     */
    private void agregarVecinos(Integer curr, List<Node> pool, Node vecino) {
        for (Request q : instance.requests) {
            for (VirtualNode vCU : q.vCUs) {
                if (curr == vCU.indxNode) {//si el nodo vecino es marcado como asignado por algun nodo virtual ...
                    if (!pool.contains(vecino)) {//y no ha sido agregado al mismo pool y es vecino
                        pool.add(vecino);//agregar vecino al mismo pool
                        for (Integer n : vecino.nears) {
                            //explorar en profundidad
                            agregarVecinos(n, pool, instance.nodes[n]);
                        }
                    }
                }
            }
        }
    }

    /**
     * This method adds a list of nodes to the input pool.
     *
     * @param pool The pool.
     * @param nodes The list of nodes.
     */
    private void addNeighbourhood(List<Node> pool, List<Node> nodes) {
        for (Node n : nodes) {
            if (!pool.contains(n)) {
                pool.add(n);
            }
        }
    }

    @Override
    public EvaluationFunction copy(ProblemInstance p) {
        return new CentralizationDegreeAndAcceptanceRate(w1, w2, w3, p, isMaximization);
    }
}
