package domain.monoobjective.implementation;

import domain.EvaluationFunction;
import domain.problem.ProblemInstance;
import domain.problem.graph.Link;
import domain.problem.graph.Node;
import domain.problem.virtual.Request;
import domain.problem.virtual.VirtualNode;
import domain.util.Tools;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author cristian.erazo@cinvestav.mx
 */
public class ObjectiveFunction implements EvaluationFunction<Double, MatrixSolution> {

    private final ProblemInstance instance;
    private boolean isMaximization;
    private final double w1;
    private final double w2;
    private final double w3;
    private List<List<Node>> pools;
    private List<Link> usedLinks;
    private FitnessComparator fc;
    private long EFO;

    public ObjectiveFunction(ProblemInstance instance, double w1, double w2, double w3, boolean isMaximization) {
        this.instance = instance;
        this.w1 = w1;
        this.w2 = w2;
        this.w3 = w3;
        this.isMaximization = isMaximization;
        pools = new ArrayList<>();//se puede mejorar a una lista de arboles!
        usedLinks = new ArrayList<>();
        fc = new FitnessComparator(isMaximization);
        EFO = 0;
    }

    @Override
    public Double evaluate(MatrixSolution e) {
        if (e.getObjective() == -1) {
            e.setObjective(eval(e));
            EFO++;
        }
        return e.getObjective();
    }

    @Override
    public Double evaluate(List<MatrixSolution> solutions) {
        double avg = 0;
        for (MatrixSolution s : solutions) {
            if (s.getObjective() == -1) {
                s.setObjective(eval(s));
                EFO++;
            }
            avg += s.getObjective();
        }
        return avg / solutions.size();
    }

    /**
     * This function evaluates the centralization of the solution.
     *
     * @param s The current solution.
     * @return The degree of centralization.
     */
    protected Double eval(MatrixSolution s) {
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

    public void setMaximization(boolean isMaximization) {
        this.isMaximization = isMaximization;
    }

    @Override
    public boolean isMaximization() {
        return isMaximization;
    }

    @Override
    public double evaluateDiversity(List<MatrixSolution> pop1, List<MatrixSolution> pop2) {
        if (pop1.size() == pop2.size()) {
            double diversity = 0, dif1, dif2, prob_a, prob_b;
            int l = pop1.size(), q25 = (int) (((l + 1.) / 4.) + 0.5), p = (int) (l * 0.20);
            Collections.sort(pop1, fc);
            Collections.sort(pop2, fc);
            MatrixSolution l1 = pop1.get(0), l2 = pop2.get(0), q1 = pop1.get(q25), q2 = pop2.get(q25);
            Collections.shuffle(pop1, Tools.getInstance().getRandom());
            Collections.shuffle(pop2, Tools.getInstance().getRandom());
            double lampda1 = l1.getObjective() - q1.getObjective(), lampda2 = l2.getObjective() - q2.getObjective(), lampdaCuadrado1 = lampda1 * lampda1, lampdaCuadrado2 = lampda2 * lampda2;
            for (int i = 0; i < p; i++) {
                dif1 = l1.getObjective() - pop1.get(i).getObjective();
                dif2 = l2.getObjective() - pop2.get(i).getObjective();
                if (lampdaCuadrado1 == 0) {
                    prob_a = 0;
                } else {
                    prob_a = Math.exp(-(dif1 * dif1) / lampdaCuadrado1);
                }
                if (lampdaCuadrado2 == 0) {
                    prob_b = 0;
                } else {
                    prob_b = Math.exp(-(dif2 * dif2) / lampdaCuadrado2);
                }
                diversity += pop1.get(i).distance(pop2.get(i)) * prob_a * prob_b;
            }
            return diversity;
        } else {
            throw new IllegalArgumentException(String.format("Error: the populations must be of the same size (%d, %d).", pop1.size(), pop2.size()));
        }
    }

    @Override
    public long EFO() {
        return EFO;
    }
}

class FitnessComparator implements Comparator<MatrixSolution> {

    boolean isMaximization;

    public FitnessComparator(boolean isMaximization) {
        this.isMaximization = isMaximization;
    }

    @Override
    public int compare(MatrixSolution o1, MatrixSolution o2) {
        double v1, v2;
        v1 = o1.getObjective() + (o1.gn * o1.gn);
        v2 = o2.getObjective() + (o2.gn * o2.gn);
        if (isMaximization) {
            if (o1.gn > 0) {
                v1 *= -1.;
            }
            if (o2.gn > 0) {
                v2 *= -1.;
            }
            return Double.compare(v2, v1);
        } else {
            return Double.compare(v2, v1);
        }
    }
}
