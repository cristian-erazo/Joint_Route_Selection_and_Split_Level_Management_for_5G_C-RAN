package domain.monoobjective.implementation.metaheuristics.algorithms.initialization;

import domain.monoobjective.implementation.MatrixSolution;
import domain.problem.ProblemInstance;
import domain.problem.virtual.VirtualNode;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 *
 * @author cristian.erazo@cinvestav.mx
 */
public class Randomly implements Initialization<MatrixSolution> {

    private final ProblemInstance instance;
    private int popSize;
    private Random rand;

    public Randomly(ProblemInstance instance, int popSize, Random rand) {
        this.instance = instance;
        this.popSize = popSize;
        this.rand = rand;
    }

    @Override
    public List<MatrixSolution> run() {
        if (popSize > 0) {
            int n = instance.requests.length, m = instance.maxVirtualDUs * instance.step, j;
            if (instance.isFullyRepresentated()) {
                m += instance.maxVirtualCUs;
            }
            List<MatrixSolution> population = new ArrayList<>();
            if (instance.isFullyRepresentated()) {
                for (int k = 0; k < popSize; k++) {
                    MatrixSolution init = new MatrixSolution(n, m);
                    init.accepted = new boolean[n];
                    for (int i = 0; i < n; i++) {//para cada peticion, asignar el orden inicial y las posiciones de la matriz
                        j = 0;//cuenta las posiciones de la columna para asignarla al grafo
                        init.accepted[i] = rand.nextBoolean();
                        for (VirtualNode vCU : instance.requests[i].vCUs) {//para cada nodo vCU, asignar la posicion a la que le corresponde en la matriz
                            vCU.indx = j;//asignar la posicion de la columna al nodo vCU
                            init.data[i][j] = instance.CUs.get(rand.nextInt(instance.max.data[i][j])).nodePosition;
                            j++;//la posicion de la DU sera la inmediatamente siguiente
                            for (Integer vdu : vCU.nears) {//obtener los nodos DU a los que se conecta (cada CU se conecta por un enlace)
                                instance.requests[i].vNodes[vdu].indx = j;//asignar posiciones relativas al nodo vCU
                                init.data[i][j] = instance.DUs.get(rand.nextInt(instance.max.data[i][j])).nodePosition;
                                init.data[i][j + instance.pathPosition] = instance.min.data[i][j + instance.pathPosition] + rand.nextInt(instance.max.data[i][j + instance.pathPosition] - instance.min.data[i][j + instance.pathPosition]);
                                init.data[i][j + instance.splitPosition] = instance.min.data[i][j + instance.splitPosition] + rand.nextInt(instance.max.data[i][j + instance.splitPosition] - instance.min.data[i][j + instance.splitPosition]);
                                j += instance.step;//la siguiente posicion dependera del tipo de representacion
                            }
                        }
                        for (; j < init.getM(); j++) {
                            init.data[i][j] = -1;
                        }
                    }
                    population.add(init);
                }
            } else {
                for (int k = 0; k < popSize; k++) {
                    MatrixSolution init = new MatrixSolution(n, m);
                    init.accepted = new boolean[n];
                    for (int i = 0; i < n; i++) {//para cada peticion, asignar el orden inicial y las posiciones de la matriz
                        j = 0;//cuenta las posiciones de la columna para asignarla al grafo
                        for (VirtualNode vCU : instance.requests[i].vCUs) {//para cada nodo vCU, asignar la posicion a la que le corresponde en la matriz
                            vCU.indx = j;//asignar la posicion de la columna al nodo vCU
                            for (Integer vdu : vCU.nears) {//obtener los nodos DU a los que se conecta (cada CU se conecta por un enlace)
                                instance.requests[i].vNodes[vdu].indx = j;//asignar posiciones relativas al nodo vCU
                                init.data[i][j] = instance.min.data[i][j] + (Objects.equals(instance.max.data[i][j], instance.min.data[i][j]) ? 0 : rand.nextInt(instance.max.data[i][j] - instance.min.data[i][j]));
                                init.data[i][j + instance.splitPosition] = instance.min.data[i][j + instance.splitPosition] + rand.nextInt(instance.max.data[i][j + instance.splitPosition] - instance.min.data[i][j + instance.splitPosition]);
                                j += instance.step;//la siguiente posicion dependera del tipo de representacion
                            }
                        }
                        for (; j < init.getM(); j++) {
                            init.data[i][j] = -1;
                        }
                    }
                    population.add(init);
                }
            }
            return population;
        }
        return null;
    }
}
