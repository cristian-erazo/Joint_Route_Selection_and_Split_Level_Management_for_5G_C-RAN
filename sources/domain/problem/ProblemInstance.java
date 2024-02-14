package domain.problem;

import domain.monoobjective.implementation.MatrixSolution;
import domain.paths.PathSolution;
import domain.problem.graph.Flow;
import domain.problem.graph.Link;
import domain.problem.graph.Location;
import domain.problem.graph.Node;
import domain.problem.virtual.Request;
import domain.problem.virtual.VirtualLink;
import domain.problem.virtual.VirtualNode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

/**
 *
 * @author cristian.erazo@cinvestav.mx
 */
public class ProblemInstance {

    /**
     * The maximum number of virtual CUs requested.
     */
    public final int maxVirtualCUs;
    /**
     * The maximum number of virtual DUs requested.
     */
    public final int maxVirtualDUs;
    /**
     * List of CUs nodes.
     */
    public final List<Node> CUs;
    /**
     * List of DUs nodes.
     */
    public final List<Node> DUs;
    /**
     * All network nodes.
     */
    public final Node[] nodes;
    /**
     * Matrix of all paths between DUs and CUs. Matrix of [nNodes,nNodes].
     */
    public List<PathSolution>[][] mPaths;
    /**
     * All functional splits constraints.
     */
    public final Flow[] splits;
    /**
     * Matrix of all links between nodes.
     */
    public final Link[][] links;
    /**
     * The list of requests.
     */
    public final Request[] requests;
    /**
     * The size of each step.
     */
    public final int step;
    /**
     * The total amount of links.
     */
    public final int nLinks;
    /**
     * The relative position of the selected functional split.
     */
    public final int splitPosition;
    /**
     * The relative position of the selected path.
     */
    public final int pathPosition;
    /**
     * The minimum values for each element in the matrix solution.
     */
    public MatrixSolution min;
    /**
     * The maximum values for each element in the matrix solution.
     */
    public MatrixSolution max;
    /**
     * An auxiliary structure used to validate the number of CU assigned per
     * request.
     */
    public TreeSet<Node> tCUs;
    /**
     * An auxiliary structure used to validate the number of DU assigned per
     * request.
     */
    public TreeSet<Node> tDUs;
    /**
     * An auxiliary structure used to determine the type of element in each
     * matrix cell.
     */
    public final int[][] types;
    /**
     *
     */
    public boolean[][] isValid;

    /**
     *
     * @param nodes All network nodes.
     * @param links All network links.
     * @param splits All functional split (and constraints).
     * @param mPaths All the paths between the CUs and DUs.
     * @param requests The q-requests of resources.
     * @param CUs All the CU nodes.
     * @param DUs All the DU nodes.
     * @param nLinks The total of links.
     * @param maxVirtualDUs The maximum virtual DUs required.
     * @param maxVirtualCUs The maximum virtual CUs required.
     * @param step This value determines the representation selected.
     */
    public ProblemInstance(Node[] nodes, Link[][] links, Flow[] splits, List<PathSolution>[][] mPaths, Request[] requests, List<Node> CUs, List<Node> DUs, int nLinks, int maxVirtualDUs, int maxVirtualCUs, int step) {
        this.step = step;
        this.nodes = nodes;
        this.splits = splits;
        this.links = links;
        this.mPaths = mPaths;
        this.requests = requests;
        this.nLinks = nLinks;
        this.maxVirtualDUs = maxVirtualDUs;
        this.maxVirtualCUs = maxVirtualCUs;
        this.DUs = DUs;
        this.CUs = CUs;
        //el split esta en step-1, la ruta en step-2
        pathPosition = step - 2;
        splitPosition = step - 1;
        tDUs = new TreeSet<>();
        tCUs = new TreeSet<>();
        int col = (step * maxVirtualDUs + (step == 3 ? maxVirtualCUs : 0)), i = 0, j;
        types = new int[requests.length][col];
        isValid = new boolean[requests.length][col];
        if (step == 3) {
            for (Request request : requests) {//para cada peticion, asignar el orden inicial y las posiciones de la matriz
                j = 0;
                for (VirtualNode vCU : request.vCUs) {//para cada nodo vCU, asignar la posicion a la que le corresponde en la matriz
                    types[i][j++] = 1;//vCU
                    for (Integer vdu : vCU.nears) {//obtener los nodos DU a los que se conecta (cada CU se conecta por un enlace)
                        types[i][j++] = 2;//vDU
                        request.vLinks[vdu][vCU.nodePosition].indx = j;
                        types[i][j++] = 3;//path
                        types[i][j++] = 4;//split
                    }
                }
                for (; j < col; j++) {
                    types[i][j] = -1;
                }
                i++;
            }
        } else {
            for (Request request : requests) {//para cada peticion, asignar el orden inicial y las posiciones de la matriz
                j = 0;
                for (VirtualNode vCU : request.vCUs) {//para cada nodo vCU, asignar la posicion a la que le corresponde en la matriz
                    for (Integer vdu : vCU.nears) {//obtener los nodos DU a los que se conecta (cada CU se conecta por un enlace)
                        request.vLinks[vdu][vCU.nodePosition].indx = j;
                        types[i][j++] = 3;//path
                        types[i][j++] = 4;//split
                    }
                }
                for (; j < col; j++) {
                    types[i][j] = -1;
                }
                i++;
            }
        }
    }

    /**
     *
     * @return true iff the solution is fully represented. false otherwise.
     */
    public boolean isFullyRepresentated() {
        return step == 3;
    }

    /**
     * This method determines if the physical node can support the input virtual
     * node.
     *
     * @param CU The physical node.
     * @param vCU The virtual node.
     * @return true iff the physical node has the available amount of PRC to
     * support the virtual node. false otherwise.
     */
    public static boolean validateAssignament(Node CU, VirtualNode vCU) {
        return CU.prc >= CU.usedPRC + vCU.prc;
    }

    /**
     *
     * @param path
     * @param split
     * @param vLink
     * @return
     */
    public static boolean validateAssignament(PathSolution path, Flow split, VirtualLink vLink) {
        if (path.getMinBw() >= split.bw && path.getDelay() <= split.delay && path.getDelay() <= vLink.maxDelay) {
            for (Link link : path.getLinks()) {//para cada enlace, validar el ancho de banda
                if (link.bw < split.bw + link.usedBw) {//no hay ancho de banda en el enlace..
                    return false;
                }
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     *
     * @param vDU
     * @param DU
     * @return
     */
    public static boolean validateAssignament(VirtualNode vDU, Node DU) {
        return distance(vDU.location, DU.location) <= DU.theta && DU.ant >= (vDU.ant + DU.usedANT) && DU.prc >= (vDU.prc + DU.usedPRC) && DU.prb >= (vDU.prb + DU.usedPRB);
    }

    /**
     *
     * @param vLink
     * @param split
     * @return
     */
    public static boolean validateAssignament(VirtualLink vLink, Flow split) {
        return split.bw >= vLink.bw/* && vLink.maxDelay >= split.delay*/;
    }

    /**
     *
     * @param vl The virtual link.
     * @return The index (between 0 and |F|-1) of the functional split that
     * meets the requirements of bandwidth and delay.
     */
    public int getMinSplitIndex(VirtualLink vl) {
        for (int i = 0; i < splits.length; i++) {
            if (splits[i].bw >= vl.bw/* && vl.maxDelay >= splits[i].delay*/) {
                return i;
            }
        }
        return -1;
    }

    public static double distance(Location a, Location b) {
        double x = b.x - a.x, y = b.y - a.y;
        return Math.sqrt((x * x) + (y * y));
    }

    /**
     * This method cleans up the assignments made to physical resources.
     */
    public void cleanResources() {
        for (Node CU : CUs) {
            CU.usedPRC = 0;
        }
        for (Node DU : DUs) {
            DU.usedANT = 0;
            DU.usedPRB = 0;
            DU.usedPRC = 0;
        }
        if (isFullyRepresentated()) {
            for (List<PathSolution>[] rows : mPaths) {
                for (List<PathSolution> paths : rows) {
                    if (paths != null) {
                        for (PathSolution path : paths) {
                            for (Link link : path.getLinks()) {
                                link.usedBw = 0;
                            }
                        }
                    }
                }
            }
        } else {
            for (PathSolution pathSolution : mPaths[0][0]) {
                for (Link link : pathSolution.getLinks()) {
                    link.usedBw = 0;
                }
            }
        }
        for (Request request : requests) {
            for (VirtualNode vCU : request.vCUs) {
                vCU.indxNode = -1;
            }
            for (VirtualNode vDU : request.vDUs) {
                vDU.indxNode = -1;
            }
            for (VirtualLink virtualLink : request.virtualLinks) {
                virtualLink.indxPath = -1;
            }
        }
        tCUs.clear();
        tDUs.clear();
    }

    public void clearRequest(Request request, boolean clearDUs, boolean clearCUs, boolean clearPaths) {
        if (clearPaths) {
            for (VirtualLink vLink : request.virtualLinks) {
                VirtualNode vDU = request.vNodes[vLink.destination], vCU = request.vNodes[vLink.source];
                if (vLink.indxPath != -1 && vDU.indxNode != -1 && vCU.indxNode != -1) {
                    for (Link link : mPaths[vDU.indxNode][vCU.indxNode].get(vLink.indxPath).getLinks()) {
                        link.usedBw -= splits[1].bw;
                        if (link.usedBw > 0) {
                            link.usedBw = 0;
                        }
                    }
                }
            }
        }
        if (clearDUs) {
            for (VirtualNode vDU : request.vDUs) {
                if (vDU.indxNode != -1) {
                    nodes[vDU.indxNode].usedPRC -= vDU.prc;
                    if (nodes[vDU.indxNode].usedPRC < 0) {
                        nodes[vDU.indxNode].usedPRC = 0;
                    }
                    nodes[vDU.indxNode].usedPRB -= vDU.prb;
                    if (nodes[vDU.indxNode].usedPRB < 0) {
                        nodes[vDU.indxNode].usedPRB = 0;
                    }
                    nodes[vDU.indxNode].usedANT -= vDU.ant;
                    if (nodes[vDU.indxNode].usedANT < 0) {
                        nodes[vDU.indxNode].usedANT = 0;
                    }
                }
            }
        }
        if (clearCUs) {
            for (VirtualNode vCU : request.vCUs) {
                if (vCU.indxNode != -1) {
                    nodes[vCU.indxNode].usedPRC -= vCU.prc;
                    if (nodes[vCU.indxNode].usedPRC < 0) {
                        nodes[vCU.indxNode].usedPRC = 0;
                    }
                }
            }
        }
    }

    /**
     * This function validates all problem constraints. Also, it evaluates the
     * allocations on physical resources.
     *
     * @param solution The current solution.
     * @return 0 iff the solution meets each constraint. Otherwise, a value
     * greater than 0.
     */
    public int validate(MatrixSolution solution) {
        int nRejected = 0, count = 0;
        VirtualNode vDU, vCU;
        PathSolution path;
        Node CU, DU;
        cleanResources();
        solution.isValid = true;
        for (int q = 0; q < requests.length; q++) {//para cada peticion, se debe procesar el orden dado, asignando los recursos a los elementos marcados por la solucion actual
            Arrays.fill(isValid[q], true);
            if (solution.accepted[q]) {//obtenemos los recursos asignados y evaluamos las restricciones
                for (VirtualLink vLink : requests[q].virtualLinks) {
                    vDU = requests[q].vNodes[vLink.source];
                    vCU = requests[q].vNodes[vLink.destination];
                    if (vDU.nodeType == 2) {
                        vDU = vCU;
                        vCU = requests[q].vNodes[vLink.source];
                    }
                    if (isFullyRepresentated()) {//El indice corresponde a las rutas relativas
                        //[CU|DU|Fx|P|DU|Fx|P|CU|DU|Fx|P]
                        count += updateCU(q, nodes[solution.data[q][vCU.indx]], vCU, vDU, solution) + updateDU(q, nodes[solution.data[q][vDU.indx]], vDU, solution);
                        if (mPaths[vDU.indxNode][vCU.indxNode] != null) {
                            vLink.indxPath = solution.data[q][vDU.indx + pathPosition];
                            if (solution.data[q][vDU.indx + pathPosition] < mPaths[vDU.indxNode][vCU.indxNode].size()) {
                                count += updatePath(q, vDU, vLink, splits[solution.data[q][vDU.indx + splitPosition]], solution, mPaths[vDU.indxNode][vCU.indxNode].get(solution.data[q][vDU.indx + pathPosition]));
                            } else {
                                solution.isValid = false;
                                isValid[q][vDU.indx + pathPosition] = false;
                                count++;
                            }
                        } else {
                            solution.isValid = false;
                            isValid[q][vDU.indx + pathPosition] = false;
                            count++;
                        }
                    } else {//El indice corresponde al identificador de ruta (tanto las DUs como las CUs apuntan a la posicion de la ruta en la representacion)
                        //[P|Fx|P|Fx|P|Fx]
                        if (solution.data[q][vDU.indx] < mPaths[0][0].size()) {
                            path = mPaths[0][0].get(solution.data[q][vDU.indx]);
                            DU = path.getNodesOfPath().get(0);
                            CU = path.getNodesOfPath().get(path.getLength() - 1);
                            if (CU.nodeType == 1 || DU.nodeType == 2) {//si el nodo CU de la lista es un DU, intercambiar
                                DU = CU;
                                CU = path.getNodesOfPath().get(0);
                            }
                            vLink.indxPath = solution.data[q][vDU.indx + pathPosition];
                            count += updateDU(q, DU, vDU, solution) + updateCU(q, CU, vCU, vDU, solution) + updatePath(q, vDU, vLink, splits[solution.data[q][vDU.indx + splitPosition]], solution, path);
                        } else {
                            solution.isValid = false;
                            isValid[q][vDU.indx + pathPosition] = false;
                            count++;
                        }
                    }
                }
                if (solution.isValid && (tCUs.size() != requests[q].vCUs.size() || tDUs.size() != requests[q].vDUs.size())) {
                    solution.isValid = false;
                }
                if (!tCUs.isEmpty()) {
                    tCUs.clear();
                }
                if (!tDUs.isEmpty()) {
                    tDUs.clear();
                }
            } else {
                nRejected++;
                Arrays.fill(isValid[q], 0, solution.getM(), false);
            }
        }
        solution.setnAccepted(requests.length - nRejected);
        if (solution.getnAccepted() == 0) {
            solution.isValid = false;
            count++;// no CU pools !!
        }
        return count;
    }

    public int updatePath(int q, VirtualNode vDU, VirtualLink vLink, Flow split, MatrixSolution solution, PathSolution path) {
        int value = 0;
        if (!ProblemInstance.validateAssignament(vLink, split)) {
            solution.isValid = false;
            isValid[q][vDU.indx + splitPosition] = false;
            value++;
        }
        if (!ProblemInstance.validateAssignament(path, split, vLink)) {
            solution.isValid = false;
            if (path.getDelay() > vLink.maxDelay) {
                isValid[q][vDU.indx + pathPosition] = false;
            } else {
                isValid[q][vDU.indx + splitPosition] = false;
                isValid[q][vDU.indx + pathPosition] = false;
            }
            value++;
        }
        for (Link link : path.getLinks()) {
            link.usedBw += split.bw;
            if (link.usedBw > link.bw) {
                link.usedBw = link.bw;
            }
        }
        return value;
    }

    public int updateDU(int q, Node DU, VirtualNode vDU, MatrixSolution solution) {
        int value = 0;
        boolean wasAdded = tDUs.contains(DU);
        if (vDU.indxNode == -1 && wasAdded) {
            vDU.indxNode = DU.nodePosition;
            solution.isValid = false;
            isValid[q][vDU.indx] = false;
            value++;
        } else if (vDU.indxNode != -1 && vDU.indxNode != DU.nodePosition) {
            solution.isValid = false;
            isValid[q][vDU.indx] = false;
            value++;
        } else if (vDU.indxNode == -1) {
            vDU.indxNode = DU.nodePosition;
            if (!validateAssignament(vDU, DU)) {
                solution.isValid = false;
                isValid[q][vDU.indx] = false;
                value++;
            }
            DU.usedPRC += vDU.prc;
            if (DU.usedPRC > DU.prc) {
                DU.usedPRC = DU.prc;
            }
            DU.usedANT += vDU.ant;
            if (DU.usedANT > DU.ant) {
                DU.usedANT = DU.ant;
            }
            DU.usedPRB += vDU.prb;
            if (DU.usedPRB > DU.prb) {
                DU.usedPRB = DU.prb;
            }
        }
        tDUs.add(DU);
        return value;
    }

    public int updateCU(int q, Node CU, VirtualNode vCU, VirtualNode vDU, MatrixSolution solution) {
        int value = 0;
        if (vCU.indxNode == -1 && tCUs.contains(CU)) {
            vCU.indxNode = CU.nodePosition;
            solution.isValid = false;
            if (isFullyRepresentated()) {
                isValid[q][vCU.indx] = false;
            } else {
                isValid[q][vDU.indx] = false;
            }
            value++;
        } else if (vCU.indxNode != -1 && vCU.indxNode != CU.nodePosition) {
            solution.isValid = false;
            if (isFullyRepresentated()) {
                isValid[q][vCU.indx] = false;
            } else {
                isValid[q][vDU.indx] = false;
            }
            value++;
        } else if (vCU.indxNode == -1) {
            vCU.indxNode = CU.nodePosition;
            if (!ProblemInstance.validateAssignament(CU, vCU)) {
                solution.isValid = false;
                if (isFullyRepresentated()) {
                    isValid[q][vCU.indx] = false;
                } else {
                    isValid[q][vDU.indx] = false;
                }
                value++;
            }
            CU.usedPRC += vCU.prc;
            if (CU.usedPRC > CU.prc) {
                CU.usedPRC = CU.prc;
            }
        }
        tCUs.add(CU);
        return value;
    }

    public static void consumeResources(ProblemInstance instance, MatrixSolution solution, int q) {
        VirtualNode vDU, vCU;
        PathSolution path;
        Node CU, DU;
        for (VirtualLink vLink : instance.requests[q].virtualLinks) {
            vDU = instance.requests[q].vNodes[vLink.source];
            vCU = instance.requests[q].vNodes[vLink.destination];
            if (vDU.nodeType == 2) {
                vDU = vCU;
                vCU = instance.requests[q].vNodes[vLink.source];
            }
            if (instance.isFullyRepresentated()) {//El indice corresponde a las rutas relativas
                //[CU|DU|Fx|P|DU|Fx|P|CU|DU|Fx|P]
                instance.updateCU(q, instance.nodes[solution.data[q][vCU.indx]], vCU, vDU, solution);
                instance.updateDU(q, instance.nodes[solution.data[q][vDU.indx]], vDU, solution);
                if (instance.mPaths[vDU.indxNode][vCU.indxNode] != null) {
                    if (solution.data[q][vDU.indx + instance.pathPosition] < instance.mPaths[vDU.indxNode][vCU.indxNode].size()) {
                        vLink.indxPath = solution.data[q][vDU.indx + instance.pathPosition];
                        instance.updatePath(q, vDU, vLink, instance.splits[solution.data[q][vDU.indx + instance.splitPosition]], solution, instance.mPaths[vDU.indxNode][vCU.indxNode].get(solution.data[q][vDU.indx + instance.pathPosition]));
                    } else {
                        solution.isValid = false;
                    }
                } else {
                    solution.isValid = false;
                }
            } else {//El indice corresponde al identificador de ruta (tanto las DUs como las CUs apuntan a la posicion de la ruta en la representacion)
                //[P|Fx|P|Fx|P|Fx]
                if (solution.data[q][vDU.indx] < instance.mPaths[0][0].size()) {
                    path = instance.mPaths[0][0].get(solution.data[q][vDU.indx]);
                    DU = path.getNodesOfPath().get(0);
                    CU = path.getNodesOfPath().get(path.getLength() - 1);
                    if (CU.nodeType == 1 || DU.nodeType == 2) {//si el nodo CU de la lista es un DU, intercambiar
                        DU = CU;
                        CU = path.getNodesOfPath().get(0);
                    }
                    instance.updateDU(q, DU, vDU, solution);
                    instance.updateCU(q, CU, vCU, vDU, solution);
                    vLink.indxPath = solution.data[q][vDU.indx + instance.pathPosition];
                    instance.updatePath(q, vDU, vLink, instance.splits[solution.data[q][vDU.indx + instance.splitPosition]], solution, path);
                } else {
                    solution.isValid = false;
                }
            }
        }
    }

    public ProblemInstance copySubInstance() {
        Node[] newNodes = new Node[nodes.length];
        Link[][] newLinks = new Link[nodes.length][nodes.length];
        List<PathSolution>[][] newPaths;
        if (isFullyRepresentated()) {
            newPaths = new ArrayList[nodes.length][nodes.length];
        } else {
            newPaths = new ArrayList[1][1];
        }
        List<Node> newDUs = new ArrayList<>(), newCUs = new ArrayList<>();
        for (int i = 0; i < nodes.length; i++) {
            newNodes[i] = nodes[i].copySubInstance();//copia el nodo con la capacidad disponible
            switch (newNodes[i].nodeType) {
                case 4:
                    newCUs.add(newNodes[i]);
                case 1:
                    newDUs.add(newNodes[i]);
                    break;
                case 2:
                    newCUs.add(newNodes[i]);
                    break;
            }
        }
        for (int i = 0; i < nodes.length - 1; i++) {
            for (int j = i + 1; j < nodes.length; j++) {
                if (links[i][j] != null) {
                    newLinks[i][j] = links[i][j].copySubInstance();//copia el enlace con la capacidad disponible
                    newLinks[j][i] = newLinks[i][j];
                }
            }
        }
        if (isFullyRepresentated()) {
            for (int i = 0; i < nodes.length; i++) {
                for (int j = i + 1; j < nodes.length; j++) {
                    if (mPaths[i][j] != null) {
                        if (newPaths[i][j] == null) {
                            newPaths[i][j] = new ArrayList<>();
                            newPaths[j][i] = newPaths[i][j];
                        }
                        for (PathSolution pathSolution : mPaths[i][j]) {
                            newPaths[i][j].add(copyPathSolutionSubInstance(pathSolution, newLinks, newNodes));
                        }
                    }
                }
            }
        } else {
            if (mPaths[0][0] != null) {
                if (newPaths[0][0] == null) {
                    newPaths[0][0] = new ArrayList<>();
                }
                for (PathSolution pathSolution : mPaths[0][0]) {
                    newPaths[0][0].add(copyPathSolutionSubInstance(pathSolution, newLinks, newNodes));
                }
            }
        }
        return new ProblemInstance(newNodes, newLinks, splits, newPaths, requests, newCUs, newDUs, nLinks, maxVirtualDUs, maxVirtualCUs, step);
    }

    private PathSolution copyPathSolutionSubInstance(PathSolution pathSolution, Link[][] newLinks, Node[] newNodes) {
        PathSolution newPath = new PathSolution();
        for (Link link : pathSolution.getLinks()) {
            newPath.addLinkToPath(newLinks[link.source][link.destination]);
        }
        for (Node node : pathSolution.getNodesOfPath()) {
            newPath.addNodeToPath(newNodes[node.nodePosition]);
        }
        return newPath;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Node node : nodes) {
            if (node.nodeType == 2) {
                sb.append(String.format("%s ", node));
            }
        }
        sb.append("| ");
        for (Node node : nodes) {
            if (node.nodeType == 1) {
                sb.append(String.format("%s ", node));
            }
        }
        return String.format("< %s>", sb.toString());
    }
}
