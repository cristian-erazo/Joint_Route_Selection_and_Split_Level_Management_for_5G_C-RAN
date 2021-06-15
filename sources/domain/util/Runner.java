package domain.util;

import domain.monoobjective.MonoObjectiveSolution;
import domain.monoobjective.implementation.*;
import domain.monoobjective.implementation.exhaustive.ExhaustiveSearch;
import domain.monoobjective.implementation.functions.AcceptanceRatio;
import domain.monoobjective.implementation.functions.MinorRequestObjectiveFunction;
import domain.monoobjective.implementation.functions.RequestObjectiveFunction;
import domain.monoobjective.implementation.functions.SimilarObjectiveFunction;
import domain.monoobjective.implementation.greedy.GreedyAlgorithm;
import domain.monoobjective.implementation.greedy.algorithms.*;
import domain.monoobjective.implementation.greedy.comparators.*;
import domain.monoobjective.implementation.metaheuristics.algorithms.GeneticAlgorithm;
import domain.monoobjective.implementation.metaheuristics.algorithms.crossover.*;
import domain.monoobjective.implementation.metaheuristics.algorithms.initialization.*;
import domain.monoobjective.implementation.metaheuristics.algorithms.mutation.*;
import domain.monoobjective.implementation.metaheuristics.algorithms.reparation.GreedyReparator;
import domain.monoobjective.implementation.metaheuristics.algorithms.reparation.Reparator;
import domain.monoobjective.implementation.metaheuristics.algorithms.selection.*;
import domain.monoobjective.implementation.mip_sat.SCRDF_MIP;
import domain.paths.SearchPath;
import domain.paths.algorithms.*;
import domain.problem.ProblemInstance;
import domain.problem.graph.Flow;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Serializable;
import java.io.Writer;
import java.util.Arrays;
import java.util.Random;

/**
 *
 * @author cristian.erazo@cinvestav.mx
 */
public class Runner implements Serializable {

    public String requestFolder, graphFile, pathsFile, outFile;
    private final Flow[] functionalSplits = {new Flow(0, 151, 30000, 1), new Flow(1, 151, 30000, 1), new Flow(2, 151, 6000, 1), new Flow(3, 151, 6000, 1), new Flow(4, 152, 250, 1), new Flow(5, 173, 250, 1), new Flow(6, 933, 250, 1), new Flow(7, 1075, 250, 1), new Flow(8, 1966, 250, 1), new Flow(9, 2457.6, 250, 1)};
    private int algId, popSize, numIterations;
    private ProblemInstance problemInstance;
    private double w1, w2, w3, g1, g2, g3;
    private static Runner instance;
    private boolean isFully;
    private Tools utils;
    private File paths;

    private Runner() {
        utils = Tools.getInstance();
        requestFolder = null;
        graphFile = null;
        pathsFile = null;
        instance = null;
        outFile = null;
        isFully = true;
        paths = null;
        w1 = 0.7;
        w2 = 0.2;
        g1 = 0.7;
        g2 = 0.2;
        g3 = 0.1;
        w3 = -1;
        Tools.crxId = 0;
        algId = -1;
        popSize = -1;
        numIterations = -1;
    }

    /**
     *
     * @return
     */
    public static synchronized Runner getInstance() {
        if (instance == null) {
            instance = new Runner();
        }
        return instance;
    }

    public ProblemInstance getProblemInstance() {
        if (problemInstance == null) {
            problemInstance = new ProblemInstance(utils.getNodes(), utils.getLinks(), functionalSplits, utils.getPaths(), utils.getRequests(), utils.getCUs(), utils.getDUs(), utils.getE(), utils.getMaxVirtualDUs(), utils.getMaxVirtualCUs(), isFully ? 3 : 2);
            utils.loadBoundaries(problemInstance);
        }
        return problemInstance;
    }

    public void runConfigFile(String paramsFilePath, String instanceFilePath) {
        try {
            if (loadFiles(paramsFilePath, instanceFilePath)) {
                if (algId == 0) {
                    runPathAlgorithm();
                } else {
                    runAlgorithm();
                }
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    public boolean loadFiles(String paramsFilePath, String instanceFilePath) {
        try {
            if (loadParameters(paramsFilePath)) {
                w3 = 1.0 - w1 - w2;
                loadInstanceFiles(instanceFilePath);
                return loadInstance();
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
        return false;
    }

    private boolean loadParameters(String filePath) throws FileNotFoundException, IOException, NumberFormatException {
        File f = new File(filePath);
        if (f.exists()) {
            BufferedReader br = new BufferedReader(new FileReader(f));
            String[] data;
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.isEmpty() && !line.startsWith("#")) {
                    data = line.split(" ");
                    switch (data[0]) {
                        case "seed":
                            utils.setSeed(Integer.parseInt(data[1]));
                            break;
                        case "representation":
                            switch (data[1]) {
                                case "1":
                                    isFully = true;
                                    break;
                                default:
                                    isFully = false;
                                    break;
                            }
                            break;
                        case "algorithm":
                            algId = Integer.parseInt(data[1]);
                            break;
                        case "iterations":
                            numIterations = Integer.parseInt(data[1]);
                            break;
                        case "mutation":
                            Tools.mut = Double.parseDouble(data[1]);
                            break;
                        case "crossover":
                            Tools.cross = Double.parseDouble(data[1]);
                            break;
                        case "ranking":
                            Tools.rank = Double.parseDouble(data[1]);
                            break;
                        case "initialization":
                            Tools.init = Integer.parseInt(data[1]);
                            break;
                        case "crossing":
                            Tools.crxId = Integer.parseInt(data[1]);
                            break;
                        case "population":
                            popSize = Integer.parseInt(data[1]);
                            break;
                        case "verbose":
                            Tools.ECHO = true;
                            break;
                        case "no-time":
                            Tools.noTime = true;
                            break;
                        case "no-sol":
                            Tools.noSolution = true;
                            break;
                        case "trace":
                            Tools.out = System.out;
                            break;
                        case "temperature":
                            Tools.T = Double.parseDouble(data[1]);
                            break;
                        case "alpha":
                            Tools.alpha = Double.parseDouble(data[1]);
                            break;
                        case "objective":
                            Tools.FxId = Integer.parseInt(data[1]);
                            break;
                        case "constant":
                            Tools.C = Double.parseDouble(data[1]);
                            break;
                        case "reparator":
                            Tools.repId = Integer.parseInt(data[1]);
                            break;
                        default:
                            System.out.println(String.format("No option avaliable for: %s", data[0]));
                            break;
                    }
                }
            }
            br.close();
            if ((algId == 2 && !isFully) || algId >= 3) {
                algId += 1;
            }
        } else {
            return false;
        }
        return true;
    }

    private void loadInstanceFiles(String filePath) {
        try {
            BufferedReader br = new BufferedReader(new FileReader(new File(filePath)));
            String line;
            String[] data;
            while ((line = br.readLine()) != null) {
                if (!line.isEmpty() && !line.startsWith("#")) {
                    data = line.split(" ");
                    switch (data[0]) {
                        case "network":
                            graphFile = data[1];
                            break;
                        case "request":
                            requestFolder = data[1];
                            break;
                        case "paths":
                            pathsFile = data[1];
                            break;
                        case "output":
                            outFile = data[1];
                            break;
                        case "K":
                            utils.K = Integer.parseInt(data[1]);
                            break;
                    }
                }
            }
            br.close();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    public boolean runCommandLine(String[] args) {
        try {
            loadParameters(args);
        } catch (IllegalArgumentException iae) {
            iae.printStackTrace(System.err);
            System.out.println("Error: " + iae.getLocalizedMessage());
        }
        if (algId == 0) {
            graphFile = args[0];
            runPathAlgorithm();
            return true;
        } else {
            if (w1 > 0 && w2 > 0 && w3 > 0 && w1 + w2 + w3 == 1) {
                graphFile = args[0];
                requestFolder = args[1];
                if (loadInstance()) {
                    try {
                        runAlgorithm();
                        return true;
                    } catch (IOException ioe) {
                        ioe.printStackTrace(System.err);
                        System.out.println("Error: " + ioe.getMessage());
                    }
                }
            } else {
                System.out.println("The values: <w_PHY:float> <w_MAC:float> must be positive.\nThe value for <w_PDCP:float> is obtained from: 1 - <w_PHY:float> - <w_MAC:float>");
            }
        }
        return false;
    }

    private void runPathAlgorithm() {
        paths = new File(pathsFile);
        if (!paths.exists()) {
            if (utils.loadGraph(new File(graphFile), g1, g2, g3)) {
                SearchPath pathsAlgorithm;
                if (utils.K > 0) {//k-paths
                    pathsAlgorithm = new DijkstraKPaths(utils.getNodes(), utils.getLinks(), isFully, utils.K);
                } else {//exhaustive search
                    pathsAlgorithm = new ExhaustiveSearchPath(utils.getNodes(), utils.getLinks(), isFully);
                }
                utils.setPaths(pathsAlgorithm.run());
                utils.setnPaths(pathsAlgorithm.getnPaths());
                if (!utils.savePaths(paths, isFully)) {
                    System.out.println(String.format("Warning: paths not saved !!."));
                    if (utils.getnPaths() > 0) {
                        System.out.println(Arrays.toString(utils.getPaths()));
                    } else {
                        System.out.println("There are no paths between DUs and CUs.");
                    }
                } else if (Tools.ECHO) {
                    System.out.println(String.format("Path file: [%s] generated. Num of paths: %d", pathsFile, utils.getnPaths()));
                }
            } else {
                System.out.println(String.format("The input file %s does not contain a valid graph !", graphFile));
            }
        }
    }

    private boolean loadInstance() throws IllegalArgumentException {
        if (utils.loadGraph(new File(graphFile), g1, g2, g3)) {
            if (utils.loadRequests(new File(requestFolder))) {
                return loadPaths();
            } else {
                System.out.println(String.format("The input folder %s does not contain valid requests !", requestFolder));
            }
        } else {
            System.out.println(String.format("The input file %s does not contain a valid graph !", graphFile));
        }
        return false;
    }

    private boolean loadPaths() throws IllegalArgumentException {
        paths = new File(pathsFile);
        if (paths.exists()) {
            if (!paths.isFile()) {
                throw new IllegalArgumentException("The parameter paths_file must be a file not a folder.");
            }
            if (!utils.loadPaths(paths, isFully)) {
                System.out.println(String.format("Error: paths can't be loaded from %s", pathsFile));
                return false;
            } else {
                if (Tools.ECHO) {
                    System.out.println(String.format("Path file: [%s] loaded. Num of paths: %d", pathsFile, utils.getnPaths()));
                }
            }
        } else {//generate paths and save the file ...
            SearchPath pathsAlgorithm;
            if (utils.K > 0) {//k-paths
                pathsAlgorithm = new DijkstraKPaths(utils.getNodes(), utils.getLinks(), isFully, utils.K);
            } else {//exhaustive search
                pathsAlgorithm = new ExhaustiveSearchPath(utils.getNodes(), utils.getLinks(), isFully);
            }
            utils.setPaths(pathsAlgorithm.run());
            utils.setnPaths(pathsAlgorithm.getnPaths());
            if (!utils.savePaths(paths, isFully)) {
                System.out.println(String.format("Warning: paths not saved !!."));
                return false;
            } else if (Tools.ECHO) {
                System.out.println(String.format("Path file: [%s] generated. Num of paths: %d", pathsFile, utils.getnPaths()));
            }
        }
        return true;
    }

    private void loadParameters(String[] args) throws IllegalArgumentException, NumberFormatException {
        int i = 5;
        while (i < args.length) {
            if ("-usepaths".compareToIgnoreCase(args[i]) == 0) {
                isFully = false;
            } else if ("-fitAll".compareToIgnoreCase(args[i]) == 0) {
                Tools.fitAll = true;
            } else if ("-seed".compareToIgnoreCase(args[i]) == 0) {
                utils.setSeed(Integer.parseInt(args[++i]));
            } else if ("-k".compareToIgnoreCase(args[i]) == 0) {
                utils.K = Integer.parseInt(args[++i]);
            } else if ("-paths".compareToIgnoreCase(args[i]) == 0) {
                pathsFile = args[++i];
            } else if ("-out".compareToIgnoreCase(args[i]) == 0) {
                outFile = args[++i];
            } else if ("-crxId".compareToIgnoreCase(args[i]) == 0) {
                Tools.crxId = Integer.parseInt(args[++i]);
            } else if ("-w1".compareToIgnoreCase(args[i]) == 0) {
                w1 = Double.parseDouble(args[++i]);
            } else if ("-w2".compareToIgnoreCase(args[i]) == 0) {
                w2 = Double.parseDouble(args[++i]);
            } else if ("-verbose".compareToIgnoreCase(args[i]) == 0) {
                Tools.ECHO = true;
            } else if ("-temp".compareToIgnoreCase(args[i]) == 0) {
                Tools.out = System.out;
            } else if ("-mut".compareToIgnoreCase(args[i]) == 0) {
                Tools.mut = Double.parseDouble(args[++i]);
            } else if ("-crx".compareToIgnoreCase(args[i]) == 0) {
                Tools.cross = Double.parseDouble(args[++i]);
            } else if ("-rnk".compareToIgnoreCase(args[i]) == 0) {
                Tools.rank = Double.parseDouble(args[++i]);
            } else if ("-init".compareToIgnoreCase(args[i]) == 0) {
                Tools.init = Integer.parseInt(args[++i]);
            } else if ("-no-time".compareToIgnoreCase(args[i]) == 0) {
                Tools.noTime = true;
            } else if ("-no-sol".compareToIgnoreCase(args[i]) == 0) {
                Tools.noSolution = true;
            } else if ("-FxId".compareToIgnoreCase(args[i]) == 0) {
                Tools.FxId = Integer.parseInt(args[++i]);
            } else if ("-constant".compareToIgnoreCase(args[i]) == 0) {
                Tools.C = Double.parseDouble(args[++i]);
            }
            i++;
        }
        w3 = 1.0 - w1 - w2;
        algId = Integer.parseInt(args[2]);
        switch (algId) {
            case 5:
            case 4:
                numIterations = Integer.parseInt(args[3]);
                popSize = Integer.parseInt(args[4]);
                break;
            case 3:
                isFully = false;
                break;
            case 2:
                isFully = true;
        }
    }

    private void runAlgorithm() throws IOException {
        long startTime;
        File sf = null, tf = null;
        ObjectiveFunction Fx;
        switch (algId) {
            case 1:
                if (outFile == null) {
                    if (isFully) {
                        outFile = "sol_full_ex";
                    } else {
                        outFile = "sol_path_ex";
                    }
                }
                if (!Tools.noSolution) {
                    sf = new File(String.format("%s.out", outFile));
                }
                if (!Tools.noTime) {
                    tf = new File(String.format("%s_time.out", outFile));
                }
                if (Tools.out != null) {
                    System.setOut(new PrintStream(new FileOutputStream(String.format("%s.generations", outFile), true), false));
                    Tools.ECHO = true;
                }
                startTime = System.nanoTime();
                problemInstance = new ProblemInstance(utils.getNodes(), utils.getLinks(), functionalSplits, utils.getPaths(), utils.getRequests(), utils.getCUs(), utils.getDUs(), utils.getE(), utils.getMaxVirtualDUs(), utils.getMaxVirtualCUs(), isFully ? 3 : 2);
                utils.loadBoundaries(problemInstance);
                Fx = getObjectiveFunction();
                runExhaustiveAlgorithm(Fx, startTime, utils, sf, tf);
                break;
            case 2:
                if (outFile == null) {
                    outFile = "sol_full_ga";
                }
                if (!Tools.noSolution) {
                    sf = new File(String.format("%s.out", outFile));
                }
                if (!Tools.noTime) {
                    tf = new File(String.format("%s_time.out", outFile));
                }
                if (Tools.out != null) {
                    System.setOut(new PrintStream(new FileOutputStream(String.format("%s.generations", outFile), true), false));
                    Tools.ECHO = true;
                }
                startTime = System.nanoTime();
                problemInstance = new ProblemInstance(utils.getNodes(), utils.getLinks(), functionalSplits, utils.getPaths(), utils.getRequests(), utils.getCUs(), utils.getDUs(), utils.getE(), utils.getMaxVirtualDUs(), utils.getMaxVirtualCUs(), 3);
                utils.loadBoundaries(problemInstance);
                Fx = getObjectiveFunction();
                runMultiChoisesAlgorithm(Fx, startTime, utils, sf, tf);
                break;
            case 3:
                if (outFile == null) {
                    outFile = "sol_path_ga";
                }
                if (!Tools.noSolution) {
                    sf = new File(String.format("%s.out", outFile));
                }
                if (!Tools.noTime) {
                    tf = new File(String.format("%s_time.out", outFile));
                }
                if (Tools.out != null) {
                    System.setOut(new PrintStream(new FileOutputStream(String.format("%s.generations", outFile), true), false));
                    Tools.ECHO = true;
                }
                startTime = System.nanoTime();
                problemInstance = new ProblemInstance(utils.getNodes(), utils.getLinks(), functionalSplits, utils.getPaths(), utils.getRequests(), utils.getCUs(), utils.getDUs(), utils.getE(), utils.getMaxVirtualDUs(), utils.getMaxVirtualCUs(), 2);
                utils.loadBoundaries(problemInstance);
                Fx = getObjectiveFunction();
                runPathChoisesAlgorithm(Fx, startTime, utils, sf, tf);
                break;
            case 4:
                if (outFile == null) {
                    if (isFully) {
                        outFile = "sol_full_ge";
                    } else {
                        outFile = "sol_path_ge";
                    }
                }
                if (!Tools.noSolution) {
                    sf = new File(String.format("%s.out", outFile));
                }
                if (!Tools.noTime) {
                    tf = new File(String.format("%s_time.out", outFile));
                }
                if (Tools.out != null) {
                    System.setOut(new PrintStream(new FileOutputStream(String.format("%s.generations", outFile), true), false));
                    Tools.ECHO = true;
                }
                startTime = System.nanoTime();
                problemInstance = new ProblemInstance(utils.getNodes(), utils.getLinks(), functionalSplits, utils.getPaths(), utils.getRequests(), utils.getCUs(), utils.getDUs(), utils.getE(), utils.getMaxVirtualDUs(), utils.getMaxVirtualCUs(), isFully ? 3 : 2);
                utils.loadBoundaries(problemInstance);
                Fx = getObjectiveFunction();
                runGeneticAlgorithm(Fx, numIterations, popSize, startTime, utils, sf, tf);
                break;
            default:
                System.out.println(String.format("\nAlgorithm [%d] not found !!\n", algId));
                break;
        }
        if (Tools.out != null) {
            System.out.flush();
            System.out.close();
            System.setOut(Tools.out);
        }
    }

    private void runPathChoisesAlgorithm(ObjectiveFunction fx, long startTime, Tools utils, File solutionFile, File timeFile) throws IOException {
        long timeElapsed;
        GreedyAlgorithm alg = new PathChoicesGreedyAlgorithm(new PathComparator(), new VirtualLinkComparator(), new RequestComparator(), problemInstance, fx);
        MonoObjectiveSolution solution = alg.run();
        timeElapsed = System.nanoTime() - startTime;
        writeFiles(String.format("[%s][%s][%s] Seed: %d #Iterations: %d Elapsed Time: %f ms EFO: %d\n", graphFile, requestFolder, pathsFile, utils.getSeed(), 1, (double) timeElapsed / 1000000., fx.EFO()), solution, solutionFile, timeFile);
    }

    private void runMultiChoisesAlgorithm(ObjectiveFunction fx, long startTime, Tools utils, File solutionFile, File timeFile) throws IOException {
        long timeElapsed;
        VirtualNodeComparator virtualNodeComparator = new VirtualNodeComparator();
        NodeResourcesComparator nodeComparator = new NodeResourcesComparator();
        GreedyAlgorithm alg = new MultiChoicesGreedyAlgorithm(new RequestComparator(), virtualNodeComparator, virtualNodeComparator, nodeComparator, new NodeDistanceComparator(), new PathComparator(), problemInstance, fx);
        MonoObjectiveSolution solution = alg.run();
        timeElapsed = System.nanoTime() - startTime;
        writeFiles(String.format("[%s][%s][%s] Seed: %d #Iterations: %d Elapsed Time: %f ms EFO: %d\n", graphFile, requestFolder, pathsFile, utils.getSeed(), 1, (double) timeElapsed / 1000000., fx.EFO()), solution, solutionFile, timeFile);
    }

    private void runExhaustiveAlgorithm(ObjectiveFunction fx, long startTime, Tools utils, File solutionFile, File timeFile) throws IOException {
        long timeElapsed;
        ExhaustiveSearch alg = new ExhaustiveSearch(problemInstance, fx);
        MonoObjectiveSolution solution = alg.run();
        timeElapsed = System.nanoTime() - startTime;
        writeFiles(String.format("[%s][%s][%s] Seed: %d #Iterations: %d Elapsed Time: %f ms EFO: %d\n", graphFile, requestFolder, pathsFile, utils.getSeed(), alg.getNumberOfIterations(), (double) timeElapsed / 1000000., fx.EFO()), solution, solutionFile, timeFile);
    }

    private void runGeneticAlgorithm(ObjectiveFunction fx, int numIterations, int popSize, long startTime, Tools utils, File solutionFile, File timeFile) throws IOException {
        long timeElapsed;
        Random rand = utils.getRandom();
        //Selection<MatrixSolution> selection = new Elitism(fx.isMaximization());
        //Mutation<MatrixSolution> mutation = new OneBit(rand, instance);
        Selection<MatrixSolution> selection = new StochasticRanking(fx.isMaximization(), rand, Tools.rank);
        Mutation<MatrixSolution> mutation = new MultiBit(rand, problemInstance);
        Initialization<MatrixSolution> init = Tools.init == 1 ? (problemInstance.isFullyRepresentated() ? new GreedyFull(problemInstance, popSize, rand) : new GreedyPaths(problemInstance, popSize, rand)) : new Randomly(problemInstance, popSize, rand);
        GeneticAlgorithm alg = new GeneticAlgorithm(problemInstance, fx, mutation, getCrossoverOperator(rand), selection, init, getReparator(rand, problemInstance), rand, numIterations, popSize);
        MonoObjectiveSolution solution = alg.run();
        timeElapsed = System.nanoTime() - startTime;
        writeFiles(String.format("[%s][%s][%s] Seed: %d #Iterations: %d Elapsed Time: %f ms EFO: %d #NotValid: %d\n", graphFile, requestFolder, pathsFile, utils.getSeed(), alg.getNumIterations(), (double) timeElapsed / 1000000., fx.EFO(), alg.getNumNotValidSolutions()), solution, solutionFile, timeFile);
    }

    public void runGurobi(String[] args) {
        try {
            String modelName = "SCRDF_MIP_" + args[2];
            SCRDF_MIP gurobiSolver = new SCRDF_MIP();
            gurobiSolver.initialize(args[0], args[1], modelName);// cantidad de variables
            gurobiSolver.initVars();// variables de decision
            gurobiSolver.initValues();// Establecer valores / matrices de costos
            gurobiSolver.initObjectiveFunction();// funcion objetivo
            gurobiSolver.initConstraints();// Constraints
            gurobiSolver.execModel(args[2] + ".lp", Tools.ECHO, Tools.ECHO, args[2]);// guardar, ejecutar el modelo
            gurobiSolver.clear();//limpiar elementos
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    private void writeFiles(String time, MonoObjectiveSolution solution, File solutionFile, File timeFile) throws IOException {
        String sol;
        if (Tools.out != null) {
            Tools.ECHO = false;
        }
        if (solution != null) {
            sol = String.format("[%s][%s][%s] %s\n", graphFile, requestFolder, pathsFile, solution.toString());
        } else {
            sol = String.format("[%s][%s][%s] No valid solution\n", graphFile, requestFolder, pathsFile);
        }
        if (solutionFile != null) {
            Writer ws1 = new FileWriter(solutionFile, true);
            ws1.write(sol);
            ws1.close();
        }
        if (timeFile != null) {
            Writer wt1 = new FileWriter(timeFile, true);
            wt1.write(time);
            wt1.close();
        }
        if (Tools.ECHO) {
            System.out.print(time);
            System.out.println(sol);
        }
    }

    private ObjectiveFunction getObjectiveFunction() {
        switch (Tools.FxId) {
            case 0:
                return new ObjectiveFunction(problemInstance, w1, w2, w3, Tools.isMaximization);
            case 1:
                return new RequestObjectiveFunction(problemInstance, w1, w2, w3, Tools.isMaximization);
            case 2:
                return new MinorRequestObjectiveFunction(problemInstance, w1, w2, w3, Tools.isMaximization, Tools.C);
            case 3:
                return new SimilarObjectiveFunction(problemInstance, w1, w2, w3, Tools.isMaximization, Tools.C);
            case 4:
                return new AcceptanceRatio(problemInstance, w1, w2, w3, Tools.isMaximization);
            default:
                return null;
        }
    }

    private Reparator<MatrixSolution> getReparator(Random rand, ProblemInstance problemInstance) {
        switch (Tools.repId) {
            case 1:
                return new GreedyReparator(rand, problemInstance);
            default:
                return null;
        }
    }

    private Crossover<MatrixSolution> getCrossoverOperator(Random rand) {
        switch (Tools.crxId) {
            case 0:
                return new Uniform(rand, Tools.cross);
            case 1:
                return new LaplaceCrossover(rand, problemInstance, Tools.cross, 0, 1);
            case 2:
                return new NewCrossover(Tools.cross, rand);
            default:
                return null;
        }
    }

}
