package domain.util;

import domain.monoobjective.implementation.MatrixSolution;
import domain.paths.PathSolution;
import domain.problem.ProblemInstance;
import domain.problem.graph.Link;
import domain.problem.graph.Location;
import domain.problem.graph.Node;
import domain.problem.virtual.Request;
import domain.problem.virtual.VirtualLink;
import domain.problem.virtual.VirtualNode;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 *
 * @author cristian.erazo@cinvestav.mx
 */
public class Tools implements Serializable {

    private static Tools instance = null;
    public static final String VERSION = "6.0.0";

    public static int FxId = 0;
    public static int init = 1;
    public static int crxId = 0;
    public static int repId = 0;
    public static double mut = 2;
    public static double C = 10.;
    public static double T = 0.75;
    public static double cross = 0.5;
    public static double rank = 0.475;
    public static double alpha = 0.75;
    public static boolean ECHO = false;
    public static PrintStream out = null;
    public static boolean fitAll = false;
    public static boolean noTime = false;
    public static final int INF = 999999;
    public static double counterWeight = 1.0;
    public static boolean noSolution = false;
    public static boolean isMaximization = true;
    public static int K;

    private int E;
    private int seed;
    private Random rand;
    private long nPaths;
    private Node[] nodes;
    private Link[][] links;
    private List<Node> CUs;
    private List<Node> DUs;
    private int maxVirtualDUs;
    private int maxVirtualCUs;
    private Request[] requests;
    private List<PathSolution>[][] paths;

    private Tools() {
        this.K = 0;
        this.E = -1;
        this.seed = -1;
        this.nPaths = 0;
        this.CUs = null;
        this.DUs = null;
        this.rand = null;
        this.nodes = null;
        this.links = null;
        this.requests = null;
    }

    /**
     *
     * @return
     */
    public static synchronized Tools getInstance() {
        if (instance == null) {
            instance = new Tools();
        }
        return instance;
    }

    /**
     * Sets the seed of the generator of random values.
     *
     * @param seed Seed to be set, if its value is less or equal to 0, then a
     * random seed is set.
     * @return The reference of the generator.
     */
    public synchronized Random setSeed(int seed) {
        if (seed < 1) {
            this.seed = (new Random()).nextInt(Integer.MAX_VALUE) + 1;
        } else {
            this.seed = seed;
        }
        rand = new Random(this.seed);
        return rand;
    }

    /**
     *
     * @return
     */
    public synchronized Random getRandom() {
        if (rand == null) {
            return setSeed(-1);
        }
        return rand;
    }

    /**
     *
     * @return
     */
    public int getSeed() {
        return seed;
    }

    /**
     *
     * @return
     */
    public double getNextDouble() {
        if (rand == null) {
            setSeed(-1);
        }
        return rand.nextDouble();
    }

    /**
     *
     * @return
     */
    public int getNextInt() {
        if (rand == null) {
            setSeed(-1);
        }
        return (int) (Integer.MAX_VALUE * rand.nextDouble());
    }

    /**
     *
     * @param a
     * @param b
     * @return
     */
    public int getNextInt(int a, int b) {
        if (a == b) {
            throw new ArithmeticException("The input values must be different.");
        }
        return (int) (a < b ? (b - a + 1) * getNextDouble() + a : (a - b + 1) * getNextDouble() + b);
    }

    /**
     *
     * @param a
     * @param b
     * @return
     */
    public double getNextDouble(double a, double b) {
        if (a == b) {
            throw new ArithmeticException("The input values must be different.");
        }
        return a < b ? (b - a + 1) * getNextDouble() + a : (a - b + 1) * getNextDouble() + b;
    }

    public boolean loadGraph(File f, double g1, double g2, double g3) {
        if (f != null) {
            char[] name = f.getName() != null ? f.getName().toCharArray() : null;
            if (f.exists() && f.isFile() && name != null) {
                int[] data = {f.getName().indexOf("n") + 1, -1, -1};
                int n, i, j;
                if (getInt(1, name, data) && getInt(2, name, data)) {
                    n = data[1];
                    E = data[2];
                    try {
                        //leer los n-nodos y las e-aristas desde el archivo
                        BufferedReader br = new BufferedReader(new FileReader(f));
                        nodes = new Node[n];
                        links = new Link[n][n];
                        CUs = new ArrayList<>();
                        DUs = new ArrayList<>();
                        String line;
                        i = 0;
                        j = 0;
                        while ((line = br.readLine()) != null) {
                            line = line.trim();
                            if (line.contains("node")) {
                                if (i < nodes.length && loadNode(br, i)) {
                                    i++;
                                } else {
                                    br.close();
                                    return false;
                                }
                            } else if (line.contains("edge")) {
                                if (j < E && loadLink(br, g1, g2, g3)) {
                                    j++;
                                } else {
                                    br.close();
                                    return false;
                                }
                            }
                        }
                        if (ECHO) {
                            System.out.println("Graph loaded !");
                            System.out.println(String.format("#Nodes: %d, #Edges: %d", n, E));
                        }
                        br.close();
                        return true;
                    } catch (Exception ex) {
                        ex.printStackTrace(System.out);
                    }
                } else {
                    System.out.println("Upssss");
                }
            }
        }
        return false;
    }

    private boolean getInt(int i, char[] name, int[] data) {
        try {
            StringBuilder sb = new StringBuilder();
            int x = data[0];
            while (x < name.length && name[x] > 47 && name[x] < 58) {
                sb.append(name[x]);
                x++;
            }
            data[0] = x + 1;
            data[i] = Integer.parseInt(sb.toString());
            return true;
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
        return false;
    }

    private boolean loadNode(BufferedReader br, int i) throws IOException {
        /* id #
        * label $
        * x #.#
        * y #.#
        * nodeType # */
        String ln;
        int values = 4;
        Node node = new Node();
        Location location = new Location();
        while ((ln = br.readLine()) != null && values > 0) {
            String[] data = ln.trim().split(" ");
            if (data[0].contains("id") && node.nodePosition == -1) {
                node.nodePosition = Integer.parseInt(data[1]);
                values--;
            } else if (data[0].contains("x") && location.x == -1) {
                location.x = Double.parseDouble(data[1]);
                values--;
            } else if (data[0].contains("y") && location.y == -1) {
                location.y = Double.parseDouble(data[1]);
                values--;
            } else if (data[0].contains("type") && node.nodeType == -1) {
                node.nodeType = Integer.parseInt(data[1]);
                if (node.nodeType == 1 || node.nodeType == 4) {
                    values += 3;
                } else if (node.nodeType == 3) {
                    values--;
                }
            } else if (data[0].contains("prc")) {
                node.prc = Integer.parseInt(data[1]);
                values--;
            } else if (data[0].contains("ant")) {
                node.ant = Integer.parseInt(data[1]);
                values--;
            } else if (data[0].contains("prb")) {
                node.prb = Integer.parseInt(data[1]);
                values--;
            } else if (data[0].contains("theta")) {
                node.theta = Double.parseDouble(data[1]);
                values--;
            }
        }
        if (values > 0 || node.nodeType == -1 || node.nodePosition == -1 || location.x == -1 || location.y == -1) {
            return false;
        }
        if ((node.nodeType == 2 || node.nodeType == 1 || node.nodeType == 4) && node.prc < 0) {
            return false;
        }
        if (node.nodeType == 1 && (node.ant < 0 || node.prb < 0 || node.theta < 0)) {
            return false;
        }
        if (node.nodeType == 4 && (node.ant < 0 || node.prb < 0 || node.theta < 0)) {
            return false;
        }
        node.location = location;
        nodes[i] = node;
        switch (nodes[i].nodeType) {
            case 1:
                DUs.add(nodes[i]);
                break;
            case 2:
                CUs.add(nodes[i]);
                break;
            case 4:
                DUs.add(nodes[i]);
                CUs.add(nodes[i]);
            default:
                break;
        }
        return true;
    }

    private boolean loadLink(BufferedReader br, double costFiber, double costCopper, double costWireless) throws IOException {
        /* source #
        * target #
        * bandwith #.#
        * nodeType # */
        String line;
        double capacity = -1, linkLength = -1;
        Node to = null, from = null;
        int processingTime = 5/*1518*/, values = 5, id, linkType = -1;
        if (nodes == null) {
            return false;
        }
        while ((line = br.readLine()) != null && values > 0) {
            String[] data = line.trim().split(" ");
            if (data[0].contains("source")) {
                id = Integer.parseInt(data[1]);
                if (id >= 0 && id < nodes.length) {
                    from = nodes[id];
                    values--;
                } else {
                    return false;
                }
            } else if (data[0].contains("target")) {
                id = Integer.parseInt(data[1]);
                if (id >= 0 && id < nodes.length) {
                    to = nodes[id];
                    values--;
                } else {
                    return false;
                }
            } else if (data[0].contains("bandwith")) {
                capacity = Double.parseDouble(data[1]);
                values--;
            } else if (data[0].contains("type")) {
                linkType = Integer.parseInt(data[1]);
                values--;
            } else if (data[0].contains("distance")) {
                linkLength = Double.parseDouble(data[1]);
                values--;
            }
        }
        if (values > 0 || linkType < 0 || from == null || to == null || capacity < 0 || linkLength < 0) {
            return false;
        }
        links[from.nodePosition][to.nodePosition] = new Link(capacity, processingTime, linkLength, (linkType == 1 ? costFiber : linkType == 2 ? costCopper : costWireless), linkType, from.nodePosition, to.nodePosition);
        links[to.nodePosition][from.nodePosition] = links[from.nodePosition][to.nodePosition];
        if (!from.nears.contains(to.nodePosition)) {
            from.nears.add(to.nodePosition);
        }
        if (!to.nears.contains(from.nodePosition)) {
            to.nears.add(from.nodePosition);
        }
        return true;
    }

    public Node[] getNodes() {
        return nodes;
    }

    public Link[][] getLinks() {
        return links;
    }

    public int getE() {
        return E;
    }

    public Request[] getRequests() {
        return requests;
    }

    public boolean loadRequests(File folder) {
        maxVirtualDUs = 0;
        maxVirtualCUs = 0;
        int i;
        if (!folder.isDirectory()) {
            return false;
        }
        File[] files = folder.listFiles(new GMLFilter());
        requests = new Request[files.length];
        for (int j = 0; j < files.length; j++) {
            requests[j] = new Request();
        }
        int maxN = 0, n;
        for (File file : files) {
            i = Integer.parseInt(file.getName().split("n")[0].split("R")[1]) - 1;
            if (ECHO) {
                System.out.println(String.format("Loading request: %s", file.getName()));
            }
            if (!loadRequest(file, requests[i])) {
                return false;
            } else {
                n = requests[i].vCUs.size() + requests[i].vDUs.size() * 3;
                if (requests[i].vCUs.size() > maxVirtualCUs) {
                    maxVirtualCUs = requests[i].vCUs.size();
                }
                if (requests[i].vDUs.size() > maxVirtualDUs) {
                    maxVirtualDUs = requests[i].vDUs.size();
                }
                if (maxN < n) {
                    maxN = n;
                }
            }
            i++;
        }
        return true;
    }

    public boolean validateRepresentation(ProblemInstance problemInstance, MatrixSolution matrixSolution) {
        return problemInstance.isFullyRepresentated() == (matrixSolution.getM() == problemInstance.maxVirtualCUs + 3 * problemInstance.maxVirtualDUs);
    }

    public int getMaxVirtualCUs() {
        return maxVirtualCUs;
    }

    public int getMaxVirtualDUs() {
        return maxVirtualDUs;
    }

    private boolean loadRequest(File file, Request request) {
        char[] name = file.getName().toCharArray();
        int[] data = {file.getName().indexOf("n") + 1, -1, -1};
        int i, nNodos, j, nEnlaces;
        if (getInt(1, name, data) && getInt(2, name, data)) {
            nNodos = data[1];
            nEnlaces = data[2];
            try {
                //leer los n-nodos y las e-aristas desde el archivo
                BufferedReader br = new BufferedReader(new FileReader(file));
                request.vNodes = new VirtualNode[nNodos];
                request.vLinks = new VirtualLink[nNodos][nNodos];
                request.virtualLinks = new ArrayList<>();
                request.vCUs = new ArrayList<>();
                request.vDUs = new ArrayList<>();
                String line;
                i = 0;
                j = 0;
                while ((line = br.readLine()) != null) {
                    line = line.trim();
                    if (line.contains("node")) {
                        if (i < request.vNodes.length && loadVirtualNode(br, i, request)) {
                            i++;
                        } else {
                            br.close();
                            return false;
                        }
                    } else if (line.contains("edge")) {
                        if (j < nEnlaces && loadVirtualLink(br, request)) {
                            j++;
                        } else {
                            br.close();
                            return false;
                        }
                    }
                }
                if (ECHO) {
                    System.out.println("Request loaded !");
                    System.out.println(String.format("#Virtual Nodes: %d, #Virtual Edges: %d", nNodos, nEnlaces));
                }
                br.close();
                return true;
            } catch (Exception ex) {
                ex.printStackTrace(System.out);
            }
        } else {
            System.out.println("Upssss");
        }
        return false;
    }

    private boolean loadVirtualNode(BufferedReader br, int i, Request request) throws IOException {
        String ln;
        Location location = new Location();
        int values = 2, nodePosition = -1, nodeType = -1, ant = -1, prb = -1, prc = -1;
        while ((ln = br.readLine()) != null && values > 0) {
            String[] data = ln.trim().split(" ");
            if (data[0].contains("id") && nodePosition == -1) {
                nodePosition = Integer.parseInt(data[1]);
                values--;
            } else if (data[0].contains("type") && nodeType == -1) {
                nodeType = Integer.parseInt(data[1]);
                if (nodeType == 1) {
                    values += 4;
                }
            } else if (data[0].contains("prc")) {
                prc = Integer.parseInt(data[1]);
                values--;
            } else if (data[0].contains("x") && location.x == -1) {
                location.x = Double.parseDouble(data[1]);
                values--;
            } else if (data[0].contains("y") && location.y == -1) {
                location.y = Double.parseDouble(data[1]);
                values--;
            } else if (data[0].contains("ant")) {
                ant = Integer.parseInt(data[1]);
                values--;
            } else if (data[0].contains("prb")) {
                prb = Integer.parseInt(data[1]);
                values--;
            }
        }
        if (values > 0 || nodeType == -1 || nodePosition == -1 || prc == -1) {
            return false;
        }
        if (nodeType == 1 && (ant < 0 || location.x == -1 || location.y == -1 || prb < 0)) {
            return false;
        }
        request.vNodes[i] = new VirtualNode(-1, -1, nodeType, i, location, new ArrayList<Integer>());
        request.vNodes[i].prc = prc;
        if (nodeType == 1) {//DU request
            request.vNodes[i].ant = ant;
            request.vNodes[i].prb = prb;
            request.vDUs.add(request.vNodes[i]);
        } else {//CU request
            request.vCUs.add(request.vNodes[i]);
        }
        return true;
    }

    private boolean loadVirtualLink(BufferedReader br, Request request) throws IOException {
        String line;
        double bandwith = -1, delay = -1;
        VirtualNode destination = null, source = null;
        int values = 4, id;
        if (nodes == null) {
            return false;
        }
        while ((line = br.readLine()) != null && values > 0) {
            String[] data = line.trim().split(" ");
            if (data[0].contains("source")) {
                id = Integer.parseInt(data[1]);
                if (id >= 0 && id < request.vNodes.length) {
                    source = request.vNodes[id];
                    values--;
                } else {
                    return false;
                }
            } else if (data[0].contains("target")) {
                id = Integer.parseInt(data[1]);
                if (id >= 0 && id < request.vNodes.length) {
                    destination = request.vNodes[id];
                    values--;
                } else {
                    return false;
                }
            } else if (data[0].contains("bandwith")) {
                bandwith = Double.parseDouble(data[1]);
                values--;
            } else if (data[0].contains("delay")) {
                delay = Double.parseDouble(data[1]);
                values--;
            }
        }
        if (values > 0 || source == null || destination == null || bandwith < 0 || delay < 0) {
            return false;
        }
        request.vLinks[source.nodePosition][destination.nodePosition] = new VirtualLink(bandwith, delay, source.nodePosition, destination.nodePosition);
        request.vLinks[destination.nodePosition][source.nodePosition] = request.vLinks[source.nodePosition][destination.nodePosition];
        if (!source.nears.contains(destination.nodePosition)) {
            source.nears.add(destination.nodePosition);
        }
        if (!destination.nears.contains(source.nodePosition)) {
            destination.nears.add(source.nodePosition);
        }
        if (!request.virtualLinks.contains(request.vLinks[source.nodePosition][destination.nodePosition])) {
            request.virtualLinks.add(request.vLinks[source.nodePosition][destination.nodePosition]);
        }
        return true;
    }

    public boolean savePaths(File file, boolean isFully) {
        try {
            if (paths != null && nPaths > 0) {
                ObjectOutputStream ous = new ObjectOutputStream(new FileOutputStream(file));
                List<List<Integer>> allPaths = new ArrayList<>();
                if (isFully) {
                    for (Node DU : DUs) {
                        for (Node CU : CUs) {
                            if (paths[DU.nodePosition][CU.nodePosition] != null) {
                                for (PathSolution path : paths[DU.nodePosition][CU.nodePosition]) {
                                    List<Integer> nodos = new ArrayList<>();
                                    for (Node node : path.getNodesOfPath()) {
                                        nodos.add(node.nodePosition);
                                    }
                                    allPaths.add(nodos);
                                }
                            }
                        }
                    }
                } else {
                    for (PathSolution path : paths[0][0]) {
                        List<Integer> nodos = new ArrayList<>();
                        for (Node node : path.getNodesOfPath()) {
                            nodos.add(node.nodePosition);
                        }
                        allPaths.add(nodos);
                    }
                }
                ous.writeLong(nPaths);
                ous.writeObject(allPaths);
                ous.close();
                return true;
            }
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
        }
        return false;
    }

    public boolean loadPaths(File file, boolean isFully) {
        try {
            ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
            nPaths = ois.readLong();
            List<List<Integer>> allPaths = (List<List<Integer>>) ois.readObject();
            ois.close();
            // replace the old-nodes|links-references to the new nodes|links references
            if (isFully) {
                paths = new ArrayList[nodes.length][nodes.length];
                while (!allPaths.isEmpty()) {
                    PathSolution newPathSolution = new PathSolution();
                    List<Integer> nodos = allPaths.remove(0);
                    int i = nodos.get(0), j = nodos.get(nodos.size() - 1);
                    if (paths[i][j] == null) {
                        paths[i][j] = new ArrayList<>();
                    }
                    Node last = null;
                    for (Integer nodo : nodos) {
                        newPathSolution.addNodeToPath(nodes[nodo]);
                        if (last != null) {
                            newPathSolution.addLinkToPath(links[last.nodePosition][nodo]);
                        }
                        last = nodes[nodo];
                    }
                    paths[i][j].add(newPathSolution);
                    if (paths[j][i] == null) {
                        paths[j][i] = paths[i][j];
                    }
                }
            } else {
                paths = new ArrayList[1][1];
                paths[0][0] = new ArrayList<>();
                while (!allPaths.isEmpty()) {
                    PathSolution newPathSolution = new PathSolution();
                    List<Integer> path = allPaths.remove(0);
                    Node last = null;
                    for (Integer node : path) {
                        newPathSolution.addNodeToPath(nodes[node]);
                        if (last != null) {
                            newPathSolution.addLinkToPath(links[last.nodePosition][node]);
                        }
                        last = nodes[node];
                    }
                    paths[0][0].add(newPathSolution);
                }
            }
            return true;
        } catch (Exception ex) {
            ex.printStackTrace(System.out);
        }
        return false;
    }

    public void loadBoundaries(ProblemInstance p) {
        p.max = p.min = null;
        int n = p.requests.length, m = p.maxVirtualDUs * p.step, j;
        if (p.isFullyRepresentated()) {
            m += p.maxVirtualCUs;
        }
        p.min = new MatrixSolution(n, m);
        p.max = new MatrixSolution(n, m);
        if (p.isFullyRepresentated()) {
            for (int i = 0; i < p.requests.length; i++) {//para cada peticion, asignar el orden inicial y las posiciones de la matriz
                j = 0;//cuenta las posiciones de la columna para asignarla al grafo
                for (VirtualNode vCU : p.requests[i].vCUs) {//para cada nodo vCU, asignar la posicion a la que le corresponde en la matriz
                    p.min.data[i][j] = 0;
                    p.max.data[i][j] = p.CUs.size();
                    j++;//la posicion de la DU sera la inmediatamente siguiente
                    for (Integer vdu : vCU.nears) {//obtener los nodos DU a los que se conecta (cada CU se conecta por un enlace)
                        p.min.data[i][j] = 0;
                        p.min.data[i][j + p.pathPosition] = 0;
                        p.min.data[i][j + p.splitPosition] = 0;
                        p.max.data[i][j] = p.DUs.size();
                        p.max.data[i][j + p.pathPosition] = K;
                        p.max.data[i][j + p.splitPosition] = p.splits.length;
                        j += p.step;//la siguiente posicion dependera del tipo de representacion
                    }
                }
            }
        } else {
            for (int i = 0; i < p.requests.length; i++) {//para cada peticion, asignar el orden inicial y las posiciones de la matriz
                j = 0;//cuenta las posiciones de la columna para asignarla al grafo
                for (VirtualNode vdu : p.requests[i].vDUs) {//obtener los nodos DU a los que se conecta (cada CU se conecta por un enlace)
                    p.min.data[i][j] = 0;
                    p.min.data[i][j + p.splitPosition] = 0;
                    p.max.data[i][j] = p.mPaths[0][0].size();
                    p.max.data[i][j + p.splitPosition] = p.splits.length;
                    j += p.step;//la siguiente posicion dependera del tipo de representacion
                }
            }
        }
    }

    public static double variance(double[] data) {
        double s = 0, sq = 0, n = data.length;
        for (double d : data) {
            s += d;
            sq += d * d;
        }
        //return (sq - (s * s) / n) / (n - 1);
        return (n * sq - s * s) / (n * n);
    }

    public void setPaths(List<PathSolution>[][] paths) {
        this.paths = paths;
    }

    public void setnPaths(long nPaths) {
        this.nPaths = nPaths;
    }

    public long getnPaths() {
        return nPaths;
    }

    public List<PathSolution>[][] getPaths() {
        return paths;
    }

    public List<Node> getCUs() {
        return CUs;
    }

    public List<Node> getDUs() {
        return DUs;

    }
}

class GMLFilter implements FileFilter {

    private static final String EXT = "gml";

    @Override
    public boolean accept(File pathname) {
        return pathname.isFile() && pathname.getName().endsWith(EXT);
    }
}
