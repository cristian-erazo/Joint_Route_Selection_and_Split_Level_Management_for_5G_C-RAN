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
 * @param <T>
 */
public class Randomly<T extends MatrixSolution> extends InitializationApproach<T> {

    public Randomly(ProblemInstance instance, int popSize, Random rand) {
        super(rand, instance, popSize);
    }

    @Override
    public List<T> run() {
        int j;
        if (popSize > 0) {
            int n = instance.requests.length, m = instance.maxVirtualDUs * instance.step;
            if (instance.isFullyRepresentated()) {
                m += instance.maxVirtualCUs;
            }
            List<T> population = new ArrayList<>();
            if (instance.isFullyRepresentated()) {
                for (int k = 0; k < popSize; k++) {
                    T init = initializeSolution(n, m);
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
                                // indice para las rutas ...
                                instance.requests[i].vLinks[vdu][vCU.nodePosition].indx = j + instance.pathPosition;
                                // ...
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
                    T init = initializeSolution(n, m);
                    init.accepted = new boolean[n];
                    for (int i = 0; i < n; i++) {//para cada peticion, asignar el orden inicial y las posiciones de la matriz
                        j = 0;//cuenta las posiciones de la columna para asignarla al grafo
                        init.accepted[i] = rand.nextBoolean();
                        for (VirtualNode vDU : instance.requests[i].vDUs) {//para cada nodo vDU, asignar la posicion a la que le corresponde en la matriz
                            vDU.indx = j;//asignar la posicion de la columna al nodo vDU
                            init.data[i][j] = instance.min.data[i][j] + (Objects.equals(instance.max.data[i][j], instance.min.data[i][j]) ? 0 : rand.nextInt(instance.max.data[i][j] - instance.min.data[i][j]));
                            init.data[i][j + instance.splitPosition] = instance.min.data[i][j + instance.splitPosition] + rand.nextInt(instance.max.data[i][j + instance.splitPosition] - instance.min.data[i][j + instance.splitPosition]);
                            // indice para las rutas ...
                            instance.requests[i].vLinks[vDU.nodePosition][vDU.nears.get(0)].indx = j + instance.pathPosition;
                            // ...
                            j += instance.step;//la siguiente posicion dependera del tipo de representacion
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

    @Override
    protected T initializeSolution(int n, int m) {
        return (T) new MatrixSolution(n, m);
    }
}
