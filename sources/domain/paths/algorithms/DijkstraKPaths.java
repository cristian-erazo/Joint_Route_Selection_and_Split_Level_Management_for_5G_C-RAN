package domain.paths.algorithms;

import domain.paths.PathSolution;
import domain.paths.SearchPath;
import domain.problem.graph.Link;
import domain.problem.graph.Node;
import domain.util.Tools;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/**
 *
 * @author cristian.erazo@cinvestav.mx
 */
public class DijkstraKPaths extends SearchPath {

    private final int K;
    private Nodo[] nodo;
    private List<Node> path;
    private final boolean isFully;
    private List<Link> removedLinks;
    private List<Node> removedNodes;
    private PriorityQueue<Nodo> Q;

    public DijkstraKPaths(Node[] nodes, Link[][] links, boolean isFully, int K) {
        this.removedNodes = new ArrayList<>();
        this.removedLinks = new ArrayList<>();
        this.links = links;
        this.nodes = nodes;
        this.CUs = new ArrayList<>();
        this.DUs = new ArrayList<>();
        for (Node nodo : nodes) {
            if (nodo.nodeType == 1) {
                DUs.add(nodo);
            } else if (nodo.nodeType == 2) {
                CUs.add(nodo);
            } else if (nodo.nodeType == 4) {
                DUs.add(nodo);
                CUs.add(nodo);
            }
        }
        this.path = new ArrayList<>(nodes.length);
        this.nodo = new Nodo[nodes.length];
        this.isFully = isFully;
        this.K = K;
        this.Q = new PriorityQueue<>(nodes.length, new Comparator<Nodo>() {
            @Override
            public int compare(Nodo o1, Nodo o2) {
                return Double.compare(o1.cost, o2.cost);
            }
        });
    }

    @Override
    public List<PathSolution>[][] run() {
        List<PathSolution>[][] paths;
        if (isFully) {
            paths = new ArrayList[nodes.length][nodes.length];
        } else {
            paths = new ArrayList[1][1];
            paths[0][0] = new ArrayList<>();
        }
        nPaths = 0;
        List<PathSolution> pathList;
        if (isFully) {
            for (Node DU : DUs) {
                for (Node CU : CUs) {
                    if (DU.nodePosition != CU.nodePosition) {
                        paths[DU.nodePosition][CU.nodePosition] = getKPaths(DU, CU);
                        paths[CU.nodePosition][DU.nodePosition] = paths[DU.nodePosition][CU.nodePosition];
                    }
                }
            }
        } else {
            for (Node DU : DUs) {
                for (Node CU : CUs) {
                    if (DU.nodePosition != CU.nodePosition) {
                        pathList = getKPaths(DU, CU);
                        if (pathList != null) {
                            paths[0][0].addAll(pathList);
                        }
                    }
                }
            }
        }
        return paths;
    }

    private List<PathSolution> getKPaths(Node DU, Node CU) {
        List<PathSolution> A, B;
        PathSolution p = dijkstra(DU, CU);
        if (Tools.ECHO) {
            System.out.println("From " + DU.nodePosition + " To " + CU.nodePosition);
        }
        if (p != null) {
            A = new ArrayList<>();
            B = new ArrayList<>();
            A.add(p);
            if (Tools.ECHO) {
                System.out.print(String.format("[%d]", nPaths));
                for (Node node : p.getNodesOfPath()) {
                    System.out.print(" " + node.nodePosition);
                }
                System.out.println();
            }
            nPaths++;
            for (int k = 1; k < K; k++) {
                for (int i = 0; i < A.get(k - 1).getNodesOfPath().size() - 2; i++) {
                    Node spurNode = A.get(k - 1).getNodesOfPath().get(i);
                    List<Node> rootPath = A.get(k - 1).getNodesOfPath().subList(0, i + 1);
                    for (PathSolution acceptedPaths : A) {
                        if (acceptedPaths.getNodesOfPath().containsAll(rootPath)) {
                            acceptedPaths.getLinks().get(i).usedBw = -1;//marcado como no valido ...
                            removedLinks.add(acceptedPaths.getLinks().get(i));
                        }
                    }
                    for (Node node : rootPath) {
                        node.usedPRC = -1;//marcado como visitado
                        removedNodes.add(node);
                    }
                    PathSolution spurPath = dijkstra(spurNode, CU);//incluye el nodo spurNodo
                    if (spurPath != null) {
                        PathSolution totalPath = addSubPath(rootPath);//incluye el nodo spurNodo
                        for (Node node : spurPath.getNodesOfPath()) {
                            if (node.nodePosition != spurNode.nodePosition) {
                                totalPath.addNodeToPath(node);
                            }
                        }
                        for (Link link : spurPath.getLinks()) {
                            totalPath.addLinkToPath(link);
                        }
                        if (!B.contains(totalPath)) {//determina si B contiene una ruta igual a la encontrada
                            B.add(totalPath);
                        }
                    }
                    while (!removedLinks.isEmpty()) {
                        removedLinks.remove(0).usedBw = 0;
                    }
                    while (!removedNodes.isEmpty()) {
                        removedNodes.remove(0).usedPRC = 0;
                    }
                }
                if (B.isEmpty()) {
                    break;
                }
                Collections.sort(B);
                if (Tools.ECHO) {
                    System.out.print(String.format("[%d]", nPaths));
                    for (Node node : B.get(0).getNodesOfPath()) {
                        System.out.print(" " + node.nodePosition);
                    }
                    System.out.println();
                }
                A.add(B.remove(0));
                nPaths++;
            }
            return A;
        } else {
            return null;
        }
    }

    private PathSolution addSubPath(List<Node> path) {
        PathSolution pathSolution = new PathSolution();
        Node last = null;
        for (Node node : path) {
            pathSolution.addNodeToPath(node);
            if (last != null) {
                pathSolution.addLinkToPath(links[last.nodePosition][node.nodePosition]);
            }
            last = node;
        }
        return pathSolution;
    }

    private PathSolution dijkstra(Node init, Node end) {
        if (!Q.isEmpty()) {
            Q.clear();
        }
        for (int i = 0; i < nodes.length; i++) {
            nodo[i] = new Nodo(Tools.INF, i, -1);
            if (i == init.nodePosition) {
                nodo[i].cost = 0;
            }
            Q.add(nodo[i]);
        }
        while (!Q.isEmpty()) {
            Nodo u = Q.poll();
            if (u.nodePosition == end.nodePosition) {
                if (u.prev == -1) {
                    return null;
                }
                return buildPath(u);
            }
            for (Integer v : nodes[u.nodePosition].nears) {
                if (nodes[v].usedPRC == 0 && links[u.nodePosition][v].usedBw == 0) {//si no han sido marcados el enlace ni el nodo
                    double alt = u.cost + links[u.nodePosition][v].bw;
                    if (alt < nodo[v].cost) {
                        nodo[v].cost = alt;
                        nodo[v].prev = u.nodePosition;
                        if (Q.remove(nodo[v])) {
                            Q.add(nodo[v]);
                        }
                    }
                }
            }
        }
        return null;
    }

    private PathSolution buildPath(Nodo u) {
        if (!path.isEmpty()) {
            path.clear();
        }
        path.add(0, nodes[u.nodePosition]);
        while (u.prev != -1) {
            u = nodo[u.prev];
            path.add(0, nodes[u.nodePosition]);
        }
        return addSubPath(path);
    }
}

class Nodo implements Comparable<Nodo> {

    public double cost;
    public int nodePosition;
    public int prev;

    public Nodo(double cost, int nodePosition, int prev) {
        this.cost = cost;
        this.nodePosition = nodePosition;
        this.prev = prev;
    }

    @Override
    public int compareTo(Nodo o) {
        return Double.compare(cost, o.cost);
    }
}
