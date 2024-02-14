package domain.monoobjective.implementation.metaheuristics.algorithms;

import domain.EvaluationFunction;
import domain.monoobjective.MonoObjectiveSolution;
import domain.monoobjective.implementation.MatrixSolution;
import domain.monoobjective.implementation.greedy.comparators.NodeComparator;
import domain.monoobjective.implementation.greedy.comparators.RequestComparator;
import domain.monoobjective.implementation.metaheuristics.Metaheuristic;
import domain.paths.PathSolution;
import domain.problem.ProblemInstance;
import domain.problem.graph.Link;
import domain.problem.graph.Node;
import domain.problem.virtual.Request;
import domain.problem.virtual.VirtualLink;
import domain.problem.virtual.VirtualNode;
import domain.util.Tools;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.TreeSet;

/**
 *
 * @author cristian.erazo@cinvestav.mx
 */
public class IterativeSearch extends Metaheuristic<MatrixSolution> {

    private int it;
    private List<Node> cus;
    private List<Node> dus;
    private List<Request> R;
    protected TreeSet<Node> tCUs;
    protected TreeSet<Node> tDUs;
    private boolean doProcedure2;
    private final int populationSize;
    private final boolean doSecuencial;
    private final List<Request> requests;

    public IterativeSearch(boolean doSecuencial, ProblemInstance instance, EvaluationFunction<Double, MatrixSolution> fx, Random rand, int numIterations, int populationSize) {
        super(instance, fx, rand, numIterations);
        this.doSecuencial = doSecuencial;
        cus = new ArrayList<>(instance.CUs);
        dus = new ArrayList<>(instance.DUs);
        this.populationSize = populationSize;
        R = new ArrayList<>(instance.requests.length);
        requests = Arrays.asList(instance.requests);
        for (Request request : instance.requests) {
            R.add(request);
        }
        tDUs = new TreeSet<>();
        tCUs = new TreeSet<>();
//        cusAllowable = new boolean[instance.requests.length][instance.CUs.size()];
//        dusAllowable = new boolean[instance.requests.length][instance.DUs.size()];
//        for (int i = 0; i < instance.requests.length; i++) {
//            Arrays.fill(cusAllowable[i], true);
//            Arrays.fill(dusAllowable[i], true);
//        }
    }

    @Override
    public MonoObjectiveSolution<Integer[], Double> run() {
        double initT = 10, coolingRate = 0.999, temperature = initT;
        MatrixSolution[] pop = new MatrixSolution[populationSize];
        //generate initial solution
        MatrixSolution currentSolution = generateInitialSolution();
        it = 0;
        doProcedure2 = true;
        if (!doSecuencial) {
            numIterations /= 2;
            doProcedure2 = false;
        }
        evaluateFitnessFunction(currentSolution);
        //temperature *= evaluateFitnessFunction(currentSolution);
        double avg;
        for (; it < numIterations; it++) {
            avg = 0;
            for (int i = 0; i < populationSize; i++) {
                //modify solution and run "constructive algorithm"
                pop[i] = allocateNodesAndPaths(modifySolution(currentSolution));
                //evaluate fitness function
                avg += evaluateFitnessFunction(pop[i]);
                System.out.println(currentSolution);
            }
            //update parameters
            temperature *= coolingRate;
            //update solution
            for (int i = 0; i < populationSize; i++) {
                currentSolution = updateSolution(pop[i], currentSolution, temperature);
            }
            avg /= populationSize;
            /*if (Tools.ECHO) {
                System.out.print(String.format("[%d] AvgFx: %f Best: %s", it, avg, best.toString()));
                System.out.println();
            }*/
        }
        if (!doSecuencial) {
            temperature = initT;// * currentSolution.getObjective();
            numIterations *= 2;
            doProcedure2 = true;
            for (; it < numIterations; it++) {
                avg = 0;
                for (int i = 0; i < populationSize; i++) {
                    //modify solution and run "constructive algorithm"
                    pop[i] = allocateNodesAndPaths(modifySolution(currentSolution));
                    //evaluate fitness function
                    avg += evaluateFitnessFunction(pop[i]);
                    System.out.println(currentSolution);
                }
                //Update parameters
                temperature *= coolingRate;
                //update solution
                /*for (int i = 0; i < populationSize; i++) {
                    currentSolution = updateSolution(pop[i], currentSolution, temperature);
                }*/
                avg /= populationSize;
                /*if (Tools.ECHO) {
                    System.out.print(String.format("[%d] AvgFx: %f Best: %s", it, avg, best.toString()));
                    System.out.println();
                }*/
            }
        }
        best.setObjective(-1.);
        best.gn = instance.validate(best);
        fx.evaluate(best);
        if (Tools.ECHO) {
            System.out.println(String.format("[%d] Best: %s", best.it, best));
        }
        //return best solution found
        return best;
    }

    private MatrixSolution modifySolution(MatrixSolution currentSolution) {
        MatrixSolution solution = (MatrixSolution) currentSolution.copy();
        if (instance.requests.length > 1) {
            Collections.shuffle(R, rand);
            //Collections.swap(R, rand.nextInt(R.size()), rand.nextInt(R.size()));
            solution.accepted = randomSwap(solution.accepted);
            //for (int i = 0; i < solution.accepted.length; i++) {
            //solution.accepted[rand.nextInt(instance.requests.length)] = rand.nextBoolean();
            //}
        }
        if (!doProcedure2 || doSecuencial) {//modify the PPs for DUs part
            // randomly swap include/not-include DUs for each request
//            for (int i = 0; i < R.size(); i++) {
//                dusAllowable[i] = randomSwap(dusAllowable[i]);
//            }
            Collections.shuffle(dus, rand);
            //Collections.swap(dus, rand.nextInt(dus.size()), rand.nextInt(dus.size()));
        }
        if (doProcedure2 || doSecuencial) {
            // randomly swap include/not-include CUs for each request
//            for (int i = 0; i < R.size(); i++) {
//                cusAllowable[i] = randomSwap(cusAllowable[i]);
//            }
            Collections.shuffle(cus, rand);
            //Collections.swap(cus, rand.nextInt(cus.size()), rand.nextInt(cus.size()));
        }
        return solution;
    }

    private MatrixSolution allocateNodesAndPaths(MatrixSolution solution) {
        solution.isValid = true;
        solution.gn = 0;
        solution.quality = 0;
        if (!doProcedure2 || doSecuencial) {// procedure 1
            int i, U = 0;
            for (Request request : R) {
                i = requests.indexOf(request);
                tDUs.clear();
                for (VirtualNode vDU : request.vDUs) {
                    vDU.indxNode = -1;
                    Node bestDU = null;
                    for (Node DU : dus) {
                        if (ProblemInstance.validateAssignament(vDU, DU) && !tDUs.contains(DU)) {
                            if (bestDU == null) {
                                bestDU = DU;
                            } else if (DU.usedPRC < bestDU.usedPRC
                                    || DU.usedPRB < bestDU.usedPRB
                                    || DU.usedANT < bestDU.usedANT) {
                                bestDU = DU;
                                break;
                            }
                        } else if (Tools.ECHO) {
                            System.out.println(String.format("[NoResources] %s -/- %s", vDU, DU));
                        }
                    }
                    if (bestDU == null) {
                        // no physical resources
                        U++;
                        instance.clearRequest(request, true, false, false);
                        solution.accepted[i] = false;
                        vDU.indxNode = instance.DUs.get(rand.nextInt(instance.max.data[i][vDU.indx])).nodePosition;
                        solution.data[i][vDU.indx] = vDU.indxNode;
                        break;
                    } else {
                        // update resources
                        tDUs.add(bestDU);
                        vDU.indxNode = bestDU.nodePosition;
                        solution.data[i][vDU.indx] = bestDU.nodePosition;
                        int g = instance.updateDU(i, bestDU, vDU, solution);
                        if (g != 0) {
                            System.out.println("DU WARNING: " + g);
                        }
                        if (Tools.ECHO) {
                            System.out.println(String.format("vCU %s assigned to %s", vDU, bestDU));
                        }
                    }
                }
            }
            solution.nAccepted = R.size() - U;
        }
        if (doProcedure2 || doSecuencial) {// procedure 2
            int i, U = 0;
            for (Request request : R) {
                i = requests.indexOf(request);
                tCUs.clear();
                for (VirtualNode vCU : request.vCUs) {
                    vCU.indxNode = -1;
                    Node bestCU = null;
                    List<PathSolution> tempPaths = new ArrayList<>();
                    List<PathSolution> bestPaths = new ArrayList<>();
                    for (Node cu : cus) {
                        if (ProblemInstance.validateAssignament(cu, vCU) && !tCUs.contains(cu)) {
                            tempPaths.clear();
                            for (Integer vdu : vCU.nears) {// for each demand (DU)
                                if (request.vNodes[vdu].indxNode == -1) {
                                    break;
                                }
                                PathSolution bestPath = null;
                                List<PathSolution> routes = instance.mPaths[request.vNodes[vdu].indxNode][cu.nodePosition];
                                if (request.vNodes[vdu].indxNode != -1 && routes != null && !routes.isEmpty()) {
                                    for (PathSolution route : routes) {
                                        if (ProblemInstance.validateAssignament(route, instance.splits[1], request.vLinks[vdu][vCU.nodePosition])) {
                                            if (bestPath == null) {
                                                bestPath = route;
                                            } else if (route.getDelay() < bestPath.getDelay()) {
                                                bestPath = route;
                                                break;
                                            }
                                        }
                                    }
                                }
                                if (bestPath == null) {
                                    if (!tempPaths.isEmpty()) {
                                        clearPaths(tempPaths);
                                        tempPaths.clear();
                                    }
                                    break;
                                } else {
                                    for (Link link : bestPath.getLinks()) {
                                        link.usedBw += instance.splits[1].bw;
                                        if (link.usedBw > link.bw) {
                                            System.out.println("LIMIT BW");
                                        }
                                    }
                                    tempPaths.add(bestPath);
                                }
                            }
                            if (tempPaths.size() == vCU.nears.size()) {
                                if (bestCU == null || cu.usedPRC < bestCU.usedPRC) {
                                    bestCU = cu;
                                    if (!bestPaths.isEmpty()) {
                                        clearPaths(bestPaths);
                                        bestPaths.clear();
                                    }
                                    bestPaths.addAll(tempPaths);
                                }
                            } else {
                                if (!tempPaths.isEmpty()) {
                                    clearPaths(tempPaths);
                                }
                            }
                        }
                    }
                    if (bestCU == null) {
                        // no physical resources
                        U++;
                        instance.clearRequest(request, false, true, true);
                        solution.accepted[i] = false;
                        solution.data[i][vCU.indx] = instance.CUs.get(rand.nextInt(instance.max.data[i][vCU.indx])).nodePosition;
                        break;
                    } else {
                        // update resources
                        tCUs.add(bestCU);
                        instance.clearRequest(request, false, false, true);
                        vCU.indxNode = bestCU.nodePosition;
                        solution.data[i][vCU.indx] = bestCU.nodePosition;
                        //bestCU.usedPRC += vCU.prc;
                        int g = instance.updateCU(i, bestCU, vCU, null, solution);
                        if (g != 0) {
                            System.out.println("CU WARNING: " + g);
                        }
                        for (int j = 0; j < vCU.nears.size(); j++) {
                            PathSolution path = bestPaths.get(j);
                            VirtualNode vDU = request.vNodes[vCU.nears.get(j)];
                            int pathID = instance.mPaths[vDU.indxNode][bestCU.nodePosition].indexOf(path);
                            if (pathID == -1) {
                                System.out.println("NO PATH FOUND !! ERROR !!!");
                            }
                            VirtualLink vLink = request.vLinks[vDU.nodePosition][vCU.nodePosition];
                            vLink.indxPath = pathID;
                            // solution.data[i][vDU.indx] = vDU.indxNode;
                            solution.data[i][vDU.indx + instance.pathPosition] = pathID;
                            g = instance.updatePath(i, vDU, vLink, instance.splits[1], solution, path);
                            if (g != 0) {
                                System.out.println("PATH (" + i + "," + j + ") WARNING: " + g);
                            }
                        }
                    }
                }
            }
            solution.nAccepted = R.size() - U;
        }
        solution.setObjective(-1.);
        return solution;
    }

    private void clearPaths(List<PathSolution> tempPaths) {
        for (PathSolution tempPath : tempPaths) {
            for (Link link : tempPath.getLinks()) {
                link.usedBw -= instance.splits[1].bw;
                if (link.usedBw < 0) {
                    link.usedBw = 0;
                }
            }
        }
    }

    private boolean[] randomSwap(boolean[] data) {
        //randomly select positions
        int a = rand.nextInt(data.length), b;
        while ((b = rand.nextInt(data.length)) == a) {
            //repeat until b != a
        }
        //swap
        boolean t = data[a];
        data[a] = data[b];
        data[b] = t;
        return data;
    }

    private double evaluateFitnessFunction(MatrixSolution solution) {
        solution.it = it;
        solution.gn = instance.validate(solution);
        if (!solution.isValid || solution.gn > 0) {
            numNotValidSolutions++;
        }
        return fx.evaluate(solution);
    }

    private MatrixSolution updateSolution(MatrixSolution solution, MatrixSolution currentSolution, double temperature) {
        if (fx.isBetter(solution, currentSolution)
                || rand.nextDouble() < Math.exp(-(solution.getObjective() - currentSolution.getObjective()) / temperature)) {
            currentSolution = (MatrixSolution) solution.copy();
        }
        if (best == null || fx.isBetter(currentSolution, best)) {
            best = (MatrixSolution) currentSolution.copy();
        }
        return currentSolution;
    }

    private MatrixSolution generateInitialSolution() {
        NodeComparator comparator = new NodeComparator() {
            @Override
            public int compare(Node x, Node y) {
                return Double.compare(-x.prc, -y.prc);
            }
        };
        R.sort(new RequestComparator());
        cus.sort(comparator);
        dus.sort(comparator);
        int n = instance.requests.length, m = instance.maxVirtualDUs * instance.step, j;
        if (instance.isFullyRepresentated()) {
            m += instance.maxVirtualCUs;
        }
        MatrixSolution sol = new MatrixSolution(n, m);
        sol.accepted = new boolean[n];
        if (instance.isFullyRepresentated()) {
            for (int i = 0; i < n; i++) {//para cada peticion, asignar el orden inicial y las posiciones de la matriz
                j = 0;
                sol.accepted[i] = true;// rand.nextBoolean();
                for (VirtualNode vCU : instance.requests[i].vCUs) {//para cada nodo vCU, asignar la posicion a la que le corresponde en la matriz
                    vCU.indx = j;//asignar la posicion de la columna al nodo vCU
                    sol.data[i][vCU.indx] = instance.CUs.get(rand.nextInt(instance.max.data[i][vCU.indx])).nodePosition;
                    j++;//la posicion de la DU sera la inmediatamente siguiente
                    for (Integer vdu : vCU.nears) {//obtener los nodos DU a los que se conecta (cada CU se conecta por un enlace)
                        instance.requests[i].vNodes[vdu].indx = j;//asignar posiciones relativas al nodo vCU
                        sol.data[i][j] = instance.DUs.get(rand.nextInt(instance.max.data[i][j])).nodePosition;
                        // sol.data[i][j + instance.pathPosition] = instance.min.data[i][j + instance.pathPosition] + rand.nextInt(instance.max.data[i][j + instance.pathPosition] - instance.min.data[i][j + instance.pathPosition]);
                        sol.data[i][j + instance.splitPosition] = 1; // Option 2 (PDCP-RLC)
                        instance.requests[i].vLinks[vdu][vCU.nodePosition].indx = j + instance.pathPosition;
                        j += instance.step;//la siguiente posicion dependera del tipo de representacion
                    }
                }
                for (; j < sol.getM(); j++) {
                    sol.data[i][j] = -1;
                }
            }
        } else {
            for (int i = 0; i < n; i++) {//para cada peticion, asignar el orden inicial y las posiciones de la matriz
                sol.accepted[i] = rand.nextBoolean();
                j = 0;
                for (VirtualNode vDU : instance.requests[i].vDUs) {//para cada nodo vDU, asignar la posicion a la que le corresponde en la matriz
                    vDU.indx = j;//asignar la posicion de la columna al nodo vDU
                    sol.data[i][j + instance.splitPosition] = 1; // Option 2 (PDCP-RLC)
                    j += instance.step;//la siguiente posicion dependera del tipo de representacion
                }
                for (; j < sol.getM(); j++) {
                    sol.data[i][j] = -1;
                }
            }
        }
        return sol;
    }
}
