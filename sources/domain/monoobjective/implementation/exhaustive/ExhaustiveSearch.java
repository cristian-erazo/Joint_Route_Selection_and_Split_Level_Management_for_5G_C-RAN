package domain.monoobjective.implementation.exhaustive;

import domain.EvaluationFunction;
import domain.monoobjective.MonoObjectiveAlgorithm;
import domain.monoobjective.MonoObjectiveSolution;
import domain.monoobjective.implementation.MatrixSolution;
import domain.problem.ProblemInstance;
import domain.problem.virtual.Request;
import domain.problem.virtual.VirtualNode;
import domain.util.Tools;

/**
 *
 * @author cristian.erazo@cinvestav.mx
 */
public class ExhaustiveSearch implements MonoObjectiveAlgorithm<MatrixSolution, Double> {

    private boolean isClean;
    /**
     * The instance of the problem to be solved.
     */
    private final ProblemInstance instance;
    /**
     * The objective function.
     */
    private final EvaluationFunction<Double, MatrixSolution> fx;
    /**
     * Number of iterations.
     */
    private long it;
    /**
     * The iteration number where the algorithm found the best solution.
     */
    private long bIt;
    /**
     * The best solution found.
     */
    private MatrixSolution best;

    /**
     *
     * @param instance The instance of the problem to be solved.
     * @param fx The objective function.
     */
    public ExhaustiveSearch(ProblemInstance instance, EvaluationFunction<Double, MatrixSolution> fx) {
        this.instance = instance;
        this.fx = fx;
    }

    @Override
    public MonoObjectiveSolution run() {
        best = null;
        MatrixSolution solution = initialize();//genera la primer solucion
        bIt = it = 0;
        isClean = true;
        explore(solution);
        return best;
    }

    /**
     *
     * @param solution
     */
    private void printState(MatrixSolution solution) {
        System.out.print(String.format("[%d] %s\n", it, solution.toString()));
    }

    /**
     *
     * @return The first possible solution.
     */
    private MatrixSolution initialize() {
        int q = instance.requests.length, m = instance.step * instance.maxVirtualDUs, pos;
        if (instance.isFullyRepresentated()) {//asignar las posiciones de la matriz a los grafos
            m += instance.maxVirtualCUs;//las columnas incluyen todas las decisiones a ser tomadas
        }
        MatrixSolution init = new MatrixSolution(q, m);//crea la matriz de q-filas y m-columnas (llena de ceros)
        init.accepted = new boolean[q];
        for (int k = 0; k < q; k++) { //para cada peticion, asignar el orden inicial y las posiciones de la matriz
            init.accepted[k] = Tools.fitAll;
            pos = 0;//cuenta las posiciones de la columna para asignarla al grafo
            for (VirtualNode vCU : instance.requests[k].vCUs) {//para cada nodo vCU, asignar la posicion a la que le corresponde en la matriz
                vCU.indx = pos;//asignar la posicion de la columna al nodo vCU
                if (instance.isFullyRepresentated()) {//si es una representacion completa, se debe agregar la posicion de la DU
                    init.data[k][pos] = instance.CUs.get(0).nodePosition;//CU fisica inicial
                    pos++;//la posicion de la DU sera la inmediatamente siguiente
                }
                for (Integer i : vCU.nears) {//obtener los nodos DU a los que se conecta (cada CU se conecta por un enlace)
                    if (instance.isFullyRepresentated()) {//si es una representacion completa, se debe agregar la posicion de la DU
                        init.data[k][pos] = instance.DUs.get(0).nodePosition;//DU fisica inicial
                    }
                    instance.requests[k].vNodes[i].indx = pos;//asignar posiciones relativas al nodo vCU
                    pos += instance.step;//la siguiente posicion dependera del tipo de representacion
                }
            }
            for (; pos < init.getM(); pos++) {
                init.data[k][pos] = -1;
            }
        }
        if (!Tools.fitAll) {
            init.accepted[q - 1] = true;
        }
        return init;
    }

    /**
     *
     * @param solution
     */
    private void explore(MatrixSolution solution) {
        int q, i, idCU, idDU, posCU, posDU;
        while (true) {
            evaluate(solution);
            for (q = 0; q < instance.requests.length;) {//para cada peticion
                if (solution.accepted[q] && instance.requests[q].vDUs.size() <= instance.DUs.size() && instance.requests[q].vCUs.size() <= instance.CUs.size()) {
                    if (instance.isFullyRepresentated()) {//si es la representacion completa...
                        //[Request][CU|DU|Path|Split|DU|Path|Split] //6
                        i = instance.step * instance.requests[q].vDUs.size() + instance.requests[q].vCUs.size() - 1;//el cambio se hara en la ultima posicion, es decir, una division funcional
                        while (i >= 0) {//aun hay elementos por explorar?
                            solution.data[q][i]++;//incrementar la division funcional
                            if (solution.data[q][i] < instance.splits.length) {//la division se puede explorar?
                                evaluate(solution);//evaluar la solucion
                                break;
                            } else {//no se puede explorar ...
                                solution.data[q][i] = 0;//fijar en cero y explorar otra ruta
                                i--;//pasar a la posicion del acarreo para generar el siguiente cambio valido
                            }
                            solution.data[q][i]++;//pasa a la siguiente ruta..
                            VirtualNode vDU = getVirtualDU(instance.requests[q], i - 1);//obtiene la DU virtual
                            VirtualNode vCU = getVirtualCU(instance.requests[q], i - 2);//obtiene la CU virtual mas cercana
                            posDU = solution.data[q][vDU.indx];
                            posCU = solution.data[q][vCU.indx];
                            if (instance.mPaths[posDU][posCU] != null && solution.data[q][i] < instance.mPaths[posDU][posCU].size()) {//la ruta se puede explorar?
                                evaluate(solution);
                                break;
                            } else {//no hay ruta posible, pasar a la siguiente DU fisica
                                solution.data[q][i] = 0;//fijar a la primer ruta y explorar otra DU fisica
                                i--;//explorar otra DU fisica para la DU virtual
                            }
                            idDU = instance.DUs.indexOf(instance.nodes[posDU]) + 1;
                            if (idDU < instance.DUs.size()) {//la DU fisica se puede asignar ..
                                solution.data[q][i] = instance.DUs.get(idDU).nodePosition;
                                evaluate(solution);
                                break;
                            } else {//no se puede asignar la DU, pasar a la siguiente DU virtual o CU fisica
                                solution.data[q][i] = instance.DUs.get(0).nodePosition;
                                i--;
                            }
                            if (vCU.indx == i) {//cambiar la CU
                                idCU = instance.CUs.indexOf(instance.nodes[posCU]) + 1;
                                if (idCU < instance.CUs.size()) {//la CU fisica se puede asignar ..
                                    solution.data[q][i] = instance.CUs.get(idCU).nodePosition;
                                    evaluate(solution);
                                    break;
                                } else {//no se puede asignar la CU, pasar a la siguiente division funcional de la siguiente DU virtual
                                    solution.data[q][i] = instance.CUs.get(0).nodePosition;
                                    i--;
                                }
                            }
                        }
                    } else {//en caso de no ser una representacion completa...
                        //[Request][Path|Split]
                        i = instance.step * instance.requests[q].vDUs.size() - 1;//el cambio se hara en la ultima posicion, es decir, una division funcional
                        while (i >= 0) {//aun hay elementos por explorar?
                            solution.data[q][i]++;//incrementar la division funcional
                            if (solution.data[q][i] < instance.splits.length) {//la division se puede explorar?
                                evaluate(solution);//evaluar la solucion
                                break;
                            } else {//no se puede explorar ...
                                solution.data[q][i] = 0;//fijar en cero y explorar otra ruta
                                i--;//pasar a la posicion del acarreo para generar el siguiente cambio valido
                            }
                            solution.data[q][i]++;//pasa a la siguiente ruta..
                            if (solution.data[q][i] < instance.mPaths[0][0].size()) {//la ruta se puede explorar?
                                evaluate(solution);
                                break;
                            } else {
                                solution.data[q][i] = 0;//no!! fijar a la primer ruta y explorar otra division funcional para otro enlace virtual
                                i--;//explorar otra division funcional para otro enlace virtual
                            }
                        }
                    }
                } else {
                    q++;
                    continue;
                }
                if (i < 0) {
                    q++;
                } else {
                    q = 0;
                }
            }
            if (Tools.fitAll) {
                break;
            } else {//buscar atender o rechazar otra peticion
                i = instance.requests.length - 1;
                while (i >= 0) {
                    solution.accepted[i] = !solution.accepted[i];
                    if (solution.accepted[i] == false) {
                        i--;
                    } else {
                        break;
                    }
                }
                if (i < 0) {
                    break;
                }
            }
        }
    }

    private void evaluate(MatrixSolution solution) {
        //establecer todos los recursos fisicos como disponibles (limpiar los valores usados)
        solution.setObjective(-1.);
        solution.gn = instance.validate(solution);//se evaluan las restricciones del problema
        if (solution.gn == 0) {//si no hay restricciones sin cumplir
            fx.evaluate(solution);//se evalua la funcion objetivo
            if (best == null || fx.isBetter(solution, best)) {//si solucion actual es mejor, entonces actualizar la mejor solucion
                best = (MatrixSolution) solution.copy();
                bIt = it;
            }
        }
        it++;
        if (Tools.ECHO) {
            printState(solution);//imprime el estado actual del sistema
        }
    }

    /**
     *
     * @return
     */
    public long getNumberOfIterations() {
        return it;
    }

    /**
     *
     * @return
     */
    public long getBestIterationNumber() {
        return bIt;
    }

    @Override
    public String toString() {
        return String.format("Best solution\nFound at: %d\n%s\n", bIt, best.toString());
    }

    private VirtualNode getVirtualDU(Request request, int i) {
        for (VirtualNode vDU : request.vDUs) {
            if (vDU.indx == i) {
                return vDU;
            }
        }
        return null;
    }

    private VirtualNode getVirtualCU(Request request, int i) {
        int id = request.vCUs.size() - 1;
        VirtualNode last = request.vCUs.get(id);
        if (last.indx < i) {
            return last;
        }
        if (request.vCUs.get(0).indx == i) {
            return request.vCUs.get(0);
        }
        while (id >= 0) {
            if (request.vCUs.get(id).indx <= i) {
                return request.vCUs.get(id);
            }
            id--;
        }
        return null;
    }
}
