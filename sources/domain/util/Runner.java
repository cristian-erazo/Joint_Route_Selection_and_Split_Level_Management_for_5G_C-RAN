package domain.util;

import domain.EvaluationFunction;
import domain.Solution;
import domain.monoobjective.MonoObjectiveSolution;
import domain.monoobjective.implementation.*;
import domain.monoobjective.implementation.exhaustive.ExhaustiveSearch;
import domain.monoobjective.implementation.functions.*;
import domain.monoobjective.implementation.greedy.GreedyAlgorithm;
import domain.monoobjective.implementation.greedy.algorithms.*;
import domain.monoobjective.implementation.greedy.comparators.*;
import domain.monoobjective.implementation.metaheuristics.algorithms.*;
import domain.monoobjective.implementation.metaheuristics.algorithms.crossover.*;
import domain.monoobjective.implementation.metaheuristics.algorithms.initialization.*;
import domain.monoobjective.implementation.metaheuristics.algorithms.mutation.*;
import domain.monoobjective.implementation.metaheuristics.algorithms.reparation.*;
import domain.monoobjective.implementation.metaheuristics.algorithms.selection.*;
import domain.monoobjective.implementation.mip_sat.SCRDF_MIP;
import domain.multiobjective.MultiObjectiveFunction;
import domain.multiobjective.implementation.MultiObjectiveMatrixSolution;
import domain.multiobjective.implementation.functions.*;
import domain.multiobjective.implementation.metaheuristics.algorithms.MOEA;
import domain.multiobjective.implementation.metaheuristics.algorithms.initialization.*;
import domain.multiobjective.implementation.metaheuristics.algorithms.selection.ModifiedStochasticRanking;
import domain.operators.*;
import domain.paths.*;
import domain.paths.algorithms.*;
import domain.problem.ProblemInstance;
import domain.problem.graph.*;
import domain.problem.virtual.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.io.Writer;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;

/**
 *
 * @author cristian.erazo@cinvestav.mx
 */
public class Runner implements Serializable {

    private static final String OUT_FORMAT = "%s.out";
    private static final String TIME_FORMAT = "%s_time.out";
    private static final String GREEDY_TIME_FORMAT = "[%s][%s][%s] Seed: %d #Iterations: %d Elapsed Time: %f ms EFO: %d\n";
    private static final String POPULATION_TIME_FORMAT = "[%s][%s][%s] Seed: %d #Iterations: %d Elapsed Time: %f ms EFO: %d #NotValid: %d\n";

    public String requestFolder, graphFile, pathsFile, outFile;
    private final Flow[] splitsReq = {
        /**
         * RRC-PDCP
         */
        new Flow(0, 151, 30000, 1, 1, 5),
        /**
         * PDCP-RLC
         */
        new Flow(1, 151, 30000, 1, 2, 4),
        /**
         * Intra-RLC
         */
        new Flow(2, 151, 6000, 1, 3, 3),
        /**
         * RLC-MAC
         */
        new Flow(3, 151, 6000, 1, 3, 3),
        /**
         * Intra-MAC
         */
        new Flow(4, 152, 250, 1, 4, 2),
        /**
         * MAC-PHY
         */
        new Flow(5, 173, 250, 1, 4, 2),
        /**
         * Intra-PHY-1
         */
        new Flow(6, 933, 250, 1, 5, 1),
        /**
         * Intra-PHY-2
         */
        new Flow(7, 1075, 250, 1, 5, 1),
        /**
         * Intra-PHY-3
         */
        new Flow(8, 1966, 250, 1, 5, 1),
        /**
         * PHY-RF
         */
        new Flow(9, 2457.6, 250, 1, 6, 1)
    };

    private int algId, popSize, numIterations;
    private ProblemInstance problemInstance;
    private double w1, w2, w3, g1, g2, g3;
    private static Runner instance = null;
    private Tools utils;
    private File paths;
    private LoCAndUoLAndAcc function;
    private StringBuilder sbMetrics;
    private StringBuilder sbConfig;
    private StringBuilder sbSolution;
    private StringBuilder sbTimes;
    private StringBuilder sbFronts;
    private StringBuilder sbConf;
    private StringBuilder sbGenerations;
    private File solutionFile;
    private File timeFile;
    private ProblemInstance residualNetwork;

    private Runner() {
        utils = Tools.getInstance();
        requestFolder = null;
        graphFile = null;
        pathsFile = null;
        outFile = null;
        Tools.isFully = true;
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
        function = null;
        sbMetrics = null;
        sbConfig = null;
        sbSolution = null;
        sbTimes = null;
        sbFronts = null;
        sbConf = null;
        sbGenerations = null;
        solutionFile = null;
        timeFile = null;
        residualNetwork = null;
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
            problemInstance = new ProblemInstance(utils.getNodes(), utils.getLinks(), splitsReq, utils.getPaths(), utils.getRequests(), utils.getCUs(), utils.getDUs(), utils.getE(), utils.getMaxVirtualDUs(), utils.getMaxVirtualCUs(), Tools.isFully ? 3 : 2);
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
                    sbSolution = new StringBuilder();
                    sbTimes = new StringBuilder();
                    if (Tools.out != null) {
                        setOutput();
                    }
                    runAlgorithm();
                    saveResults();
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
                            Tools.k = Integer.parseInt(data[1]);
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
                sbSolution = new StringBuilder();
                sbTimes = new StringBuilder();
                if (loadInstance()) {
                    try {
                        if (Tools.out != null) {
                            setOutput();
                        }
                        runAlgorithm();
                        saveResults();
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

    private void setOutput() {
        if (sbGenerations == null) {
            sbGenerations = new StringBuilder();
        }
        System.setOut(new PrintStream(new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                sbGenerations.append((char) b);
            }
        }));
        Tools.echo = false;
    }

    private void runPathAlgorithm() {
        paths = new File(pathsFile);
        if (!paths.exists()) {
            if (utils.loadGraph(new File(graphFile), g1, g2, g3, false)) {
                SearchPath pathsAlgorithm;
                if (Tools.k > 0) {//k-paths
                    pathsAlgorithm = new DijkstraKPaths(utils.getNodes(), utils.getLinks(), Tools.isFully, Tools.k);
                } else {//exhaustive search
                    pathsAlgorithm = new ExhaustiveSearchPath(utils.getNodes(), utils.getLinks(), Tools.isFully);
                }
                utils.setPaths(pathsAlgorithm.run());
                utils.setnPaths(pathsAlgorithm.getnPaths());
                if (!utils.savePaths(paths, Tools.isFully)) {
                    System.out.println("Warning: paths not saved !!.");
                    if (utils.getnPaths() > 0) {
                        System.out.println(Arrays.toString(utils.getPaths()));
                    } else {
                        System.out.println("There are no paths between DUs and CUs.");
                    }
                } else if (Tools.echo) {
                    System.out.println(String.format("Path file: [%s] generated. Num of paths: %d", pathsFile, utils.getnPaths()));
                }
            } else {
                System.out.println(String.format("The input file %s does not contain a valid graph !", graphFile));
            }
        }
    }

    private boolean loadInstance() throws IllegalArgumentException {
        if (utils.loadGraph(new File(graphFile), g1, g2, g3, false)) {
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
            if (!utils.loadPaths(paths, Tools.isFully)) {
                System.out.println(String.format("Error: paths can't be loaded from %s", pathsFile));
                return false;
            } else {
                if (Tools.echo) {
                    System.out.println(String.format("Path file: [%s] loaded. Num of paths: %d", pathsFile, utils.getnPaths()));
                }
            }
        } else {//generate paths and save the file ...
            SearchPath pathsAlgorithm;
            if (Tools.k > 0) {//k-paths
                pathsAlgorithm = new DijkstraKPaths(utils.getNodes(), utils.getLinks(), Tools.isFully, Tools.k);
            } else {//exhaustive search
                pathsAlgorithm = new ExhaustiveSearchPath(utils.getNodes(), utils.getLinks(), Tools.isFully);
            }
            utils.setPaths(pathsAlgorithm.run());
            utils.setnPaths(pathsAlgorithm.getnPaths());
            if (!utils.savePaths(paths, Tools.isFully)) {
                System.out.println(String.format("Warning: paths not saved !!."));
                return false;
            } else if (Tools.echo) {
                System.out.println(String.format("Path file: [%s] generated. Num of paths: %d", pathsFile, utils.getnPaths()));
            }
        }
        return true;
    }

    private void loadParameters(String[] args) throws IllegalArgumentException, NumberFormatException {
        int i = 1;
        while (i < args.length) {
            if ("-usepaths".compareToIgnoreCase(args[i]) == 0) {
                Tools.isFully = false;
            } else if ("-fitAll".compareToIgnoreCase(args[i]) == 0) {
                Tools.fitAll = true;
            } else if ("-seed".compareToIgnoreCase(args[i]) == 0) {
                utils.setSeed(Integer.parseInt(args[++i]));
            } else if ("-k".compareToIgnoreCase(args[i]) == 0) {
                Tools.k = Integer.parseInt(args[++i]);
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
                Tools.echo = true;
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
                Tools.fxId = Integer.parseInt(args[++i]);
            } else if ("-constant".compareToIgnoreCase(args[i]) == 0) {
                Tools.c = Double.parseDouble(args[++i]);
            } else if ("-fronts".compareToIgnoreCase(args[i]) == 0) {
                Tools.printFront = true;
            } else if ("-CUcomparator".compareToIgnoreCase(args[i]) == 0) {
                Tools.cuNodeComparatorId = Integer.parseInt(args[++i]);
            }
            i++;
        }
        w3 = 1.0 - w1 - w2;
        algId = Integer.parseInt(args[2]);
        if (Tools.fxId > 4) {
            algId = 5;
        }
        switch (algId) {
            case 5:
            case 4:
                numIterations = Integer.parseInt(args[3]);
                popSize = Integer.parseInt(args[4]);
                break;
            case 9:
            case 3:
                Tools.isFully = false;
                break;
            case 2:
                Tools.isFully = true;
        }
    }

    private boolean loadParameters(String filePath) throws NumberFormatException, IOException {
        File f = new File(filePath);
        if (f.exists()) {
            try (BufferedReader br = new BufferedReader(new FileReader(f))) {
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
                                Tools.isFully = "1".equals(data[1]);
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
                                Tools.echo = true;
                                break;
                            case "no-time":
                                Tools.noTime = true;
                                break;
                            case "no-sol":
                                Tools.noSolution = true;
                                break;
                            case "fronts":
                                Tools.printFront = true;
                                break;
                            case "trace":
                                Tools.out = System.out;
                                break;
                            case "temperature":
                                Tools.t = Double.parseDouble(data[1]);
                                break;
                            case "alpha":
                                Tools.alpha = Double.parseDouble(data[1]);
                                break;
                            case "objective":
                                Tools.fxId = Integer.parseInt(data[1]);
                                break;
                            case "constant":
                                Tools.c = Double.parseDouble(data[1]);
                                break;
                            case "reparator":
                                Tools.repId = Integer.parseInt(data[1]);
                                break;
                            case "CUcomparator":
                                Tools.cuNodeComparatorId = Integer.parseInt(data[1]);
                                break;
                            default:
                                System.out.println(String.format("No option avaliable for: %s", data[0]));
                                break;
                        }
                    }
                }
            }
            if (algId == 9) {
                Tools.isFully = false;
            }
            if (algId == 2 && !Tools.isFully) {
                algId = 3;
            } else if (algId == 3 && Tools.isFully) {
                algId = 2;
            } else if (Tools.fxId >= 5 && Tools.fxId < 8) {
                algId = 5;
            } else if ((algId == 4 || algId == 9) && numIterations == -1) {
                System.out.println("No iterations in configuration file!!");
                return false;
            }
        } else {
            return false;
        }
        return true;
    }

    private void runAlgorithm() throws IOException {
        long startTime;
        EvaluationFunction fx;
        switch (algId) {
            case 1:
                if (outFile == null) {
                    if (Tools.isFully) {
                        outFile = "sol_full_ex";
                    } else {
                        outFile = "sol_path_ex";
                    }
                }
                if (!Tools.noSolution) {
                    solutionFile = new File(String.format(OUT_FORMAT, outFile));
                }
                if (!Tools.noTime) {
                    timeFile = new File(String.format(TIME_FORMAT, outFile));
                }
                startTime = System.nanoTime();
                problemInstance = new ProblemInstance(utils.getNodes(), utils.getLinks(), splitsReq, utils.getPaths(), utils.getRequests(), utils.getCUs(), utils.getDUs(), utils.getE(), utils.getMaxVirtualDUs(), utils.getMaxVirtualCUs(), Tools.isFully ? 3 : 2);
                utils.loadBoundaries(problemInstance);
                fx = getObjectiveFunction(Tools.fxId);
                if (Tools.runOnlineVersion) {
                    function = new LoCAndUoLAndAcc(problemInstance, w1, w2, w3, Tools.isMaximization);
                }
                runExhaustiveAlgorithm(fx, startTime);
                break;
            case 2:
                if (outFile == null) {
                    outFile = "sol_full_ga";
                }
                if (!Tools.noSolution) {
                    solutionFile = new File(String.format(OUT_FORMAT, outFile));
                }
                if (!Tools.noTime) {
                    timeFile = new File(String.format(TIME_FORMAT, outFile));
                }
                startTime = System.nanoTime();
                problemInstance = new ProblemInstance(utils.getNodes(), utils.getLinks(), splitsReq, utils.getPaths(), utils.getRequests(), utils.getCUs(), utils.getDUs(), utils.getE(), utils.getMaxVirtualDUs(), utils.getMaxVirtualCUs(), 3);
                utils.loadBoundaries(problemInstance);
                fx = getObjectiveFunction(Tools.fxId);
                if (Tools.runOnlineVersion) {
                    function = new LoCAndUoLAndAcc(problemInstance, w1, w2, w3, Tools.isMaximization);
                }
                runMultiChoisesAlgorithm(fx, startTime);
                break;
            case 3:
                if (outFile == null) {
                    outFile = "sol_path_ga";
                }
                if (!Tools.noSolution) {
                    solutionFile = new File(String.format(OUT_FORMAT, outFile));
                }
                if (!Tools.noTime) {
                    timeFile = new File(String.format(TIME_FORMAT, outFile));
                }
                startTime = System.nanoTime();
                problemInstance = new ProblemInstance(utils.getNodes(), utils.getLinks(), splitsReq, utils.getPaths(), utils.getRequests(), utils.getCUs(), utils.getDUs(), utils.getE(), utils.getMaxVirtualDUs(), utils.getMaxVirtualCUs(), 2);
                utils.loadBoundaries(problemInstance);
                fx = getObjectiveFunction(Tools.fxId);
                if (Tools.runOnlineVersion) {
                    function = new LoCAndUoLAndAcc(problemInstance, w1, w2, w3, Tools.isMaximization);
                }
                runPathChoisesAlgorithm(fx, startTime);
                break;
            case 4:
                if (outFile == null) {
                    outFile = Tools.isFully ? "sol_full_ge" : "sol_path_ge";
                }
                if (!Tools.noSolution) {
                    solutionFile = new File(String.format(OUT_FORMAT, outFile));
                }
                if (!Tools.noTime) {
                    timeFile = new File(String.format(TIME_FORMAT, outFile));
                }
                startTime = System.nanoTime();
                problemInstance = new ProblemInstance(utils.getNodes(), utils.getLinks(), splitsReq, utils.getPaths(), utils.getRequests(), utils.getCUs(), utils.getDUs(), utils.getE(), utils.getMaxVirtualDUs(), utils.getMaxVirtualCUs(), Tools.isFully ? 3 : 2);
                utils.loadBoundaries(problemInstance);
                fx = getObjectiveFunction(Tools.fxId);
                if (Tools.runOnlineVersion) {
                    function = new LoCAndUoLAndAcc(problemInstance, w1, w2, w3, Tools.isMaximization);
                    Tools.printFront = false;
                }
                runGeneticAlgorithm(fx, startTime);
                break;
            case 5:
                if (outFile == null) {
                    outFile = Tools.isFully ? "sol_full_moea" : "sol_path_moea";
                }
                if (!Tools.noSolution) {
                    solutionFile = new File(String.format(OUT_FORMAT, outFile));
                }
                if (!Tools.noTime) {
                    timeFile = new File(String.format(TIME_FORMAT, outFile));
                }
                startTime = System.nanoTime();
                problemInstance = new ProblemInstance(utils.getNodes(), utils.getLinks(), splitsReq, utils.getPaths(), utils.getRequests(), utils.getCUs(), utils.getDUs(), utils.getE(), utils.getMaxVirtualDUs(), utils.getMaxVirtualCUs(), Tools.isFully ? 3 : 2);
                utils.loadBoundaries(problemInstance);
                if (Tools.fxId <= 4) {
                    switch (Tools.fxId) {
                        case 0:
                            Tools.fxId = 5;
                            break;
                        case 1:
                        case 2:
                            Tools.fxId = 6;
                            break;
                        case 3:
                        case 4:
                            Tools.fxId = 7;
                            break;
                    }
                }
                fx = getObjectiveFunction(Tools.fxId);
                if (Tools.runOnlineVersion) {
                    function = new LoCAndUoLAndAcc(problemInstance, w1, w2, w3, Tools.isMaximization);
                }
                runMOEA(fx, startTime);
                break;
            case 6:
                if (outFile == null) {
                    outFile = "sol_full_ch";
                }
                if (!Tools.noSolution) {
                    solutionFile = new File(String.format(OUT_FORMAT, outFile));
                }
                if (!Tools.noTime) {
                    timeFile = new File(String.format(TIME_FORMAT, outFile));
                }
                startTime = System.nanoTime();
                problemInstance = new ProblemInstance(utils.getNodes(), utils.getLinks(), splitsReq, utils.getPaths(), utils.getRequests(), utils.getCUs(), utils.getDUs(), utils.getE(), utils.getMaxVirtualDUs(), utils.getMaxVirtualCUs(), 3);
                utils.loadBoundaries(problemInstance);
                fx = getObjectiveFunction(Tools.fxId);
                if (Tools.runOnlineVersion) {
                    function = new LoCAndUoLAndAcc(problemInstance, w1, w2, w3, Tools.isMaximization);
                }
                runFullChoisesAlgorithm(fx, startTime);
                break;
            case 7:
                if (outFile == null) {
                    outFile = "sol_rand_ch";
                }
                if (!Tools.noSolution) {
                    solutionFile = new File(String.format(OUT_FORMAT, outFile));
                }
                if (!Tools.noTime) {
                    timeFile = new File(String.format(TIME_FORMAT, outFile));
                }
                startTime = System.nanoTime();
                problemInstance = new ProblemInstance(utils.getNodes(), utils.getLinks(), splitsReq, utils.getPaths(), utils.getRequests(), utils.getCUs(), utils.getDUs(), utils.getE(), utils.getMaxVirtualDUs(), utils.getMaxVirtualCUs(), 3);
                utils.loadBoundaries(problemInstance);
                fx = getObjectiveFunction(Tools.fxId);
                if (Tools.runOnlineVersion) {
                    function = new LoCAndUoLAndAcc(problemInstance, w1, w2, w3, Tools.isMaximization);
                }
                runRandomChoisesAlgorithm(fx, startTime);
                break;
            case 8:
                if (outFile == null) {
                    outFile = "sol_new_gre";
                }
                if (!Tools.noSolution) {
                    solutionFile = new File(String.format(OUT_FORMAT, outFile));
                }
                if (!Tools.noTime) {
                    timeFile = new File(String.format(TIME_FORMAT, outFile));
                }
                startTime = System.nanoTime();
                problemInstance = new ProblemInstance(utils.getNodes(), utils.getLinks(), splitsReq, utils.getPaths(), utils.getRequests(), utils.getCUs(), utils.getDUs(), utils.getE(), utils.getMaxVirtualDUs(), utils.getMaxVirtualCUs(), 3);
                utils.loadBoundaries(problemInstance);
                fx = getObjectiveFunction(Tools.fxId);
                if (Tools.runOnlineVersion) {
                    function = new LoCAndUoLAndAcc(problemInstance, w1, w2, w3, Tools.isMaximization);
                }
                runNewGreedyAlgorithm(fx, startTime);
                break;
            case 9:
                if (outFile == null) {
                    outFile = "sol_new_GA";
                }
                if (!Tools.noSolution) {
                    solutionFile = new File(String.format(OUT_FORMAT, outFile));
                }
                if (!Tools.noTime) {
                    timeFile = new File(String.format(TIME_FORMAT, outFile));
                }
                startTime = System.nanoTime();
                problemInstance = new ProblemInstance(utils.getNodes(), utils.getLinks(), splitsReq, utils.getPaths(), utils.getRequests(), utils.getCUs(), utils.getDUs(), utils.getE(), utils.getMaxVirtualDUs(), utils.getMaxVirtualCUs(), Tools.isFully ? 3 : 2);
                utils.loadBoundaries(problemInstance);
                fx = getObjectiveFunction(Tools.fxId);
                if (Tools.runOnlineVersion) {
                    function = new LoCAndUoLAndAcc(problemInstance, w1, w2, w3, Tools.isMaximization);
                }
                runNewGeneticAlgorithm(fx, startTime);
                break;
            case 10:
                if (outFile == null) {
                    outFile = "sol_IS";
                }
                if (!Tools.noSolution) {
                    solutionFile = new File(String.format(OUT_FORMAT, outFile));
                }
                if (!Tools.noTime) {
                    timeFile = new File(String.format(TIME_FORMAT, outFile));
                }
                startTime = System.nanoTime();
                problemInstance = new ProblemInstance(utils.getNodes(), utils.getLinks(), splitsReq, utils.getPaths(), utils.getRequests(), utils.getCUs(), utils.getDUs(), utils.getE(), utils.getMaxVirtualDUs(), utils.getMaxVirtualCUs(), Tools.isFully ? 3 : 2);
                utils.loadBoundaries(problemInstance);
                fx = getObjectiveFunction(Tools.fxId);
                if (Tools.runOnlineVersion) {
                    function = new LoCAndUoLAndAcc(problemInstance, w1, w2, w3, Tools.isMaximization);
                }
                runIterativeSearchAlgorithm(fx, startTime);
                break;
            case 11:
                if (outFile == null) {
                    outFile = "sol_splitRANheu";
                }
                if (!Tools.noSolution) {
                    solutionFile = new File(String.format(OUT_FORMAT, outFile));
                }
                if (!Tools.noTime) {
                    timeFile = new File(String.format(TIME_FORMAT, outFile));
                }
                startTime = System.nanoTime();
                problemInstance = new ProblemInstance(utils.getNodes(), utils.getLinks(), splitsReq, utils.getPaths(), utils.getRequests(), utils.getCUs(), utils.getDUs(), utils.getE(), utils.getMaxVirtualDUs(), utils.getMaxVirtualCUs(), Tools.isFully ? 3 : 2);
                utils.loadBoundaries(problemInstance);
                fx = getObjectiveFunction(Tools.fxId);
                if (Tools.runOnlineVersion) {
                    function = new LoCAndUoLAndAcc(problemInstance, w1, w2, w3, Tools.isMaximization);
                }
                runSplitRANHeu(fx, startTime);
                break;
            default:
                System.out.println(String.format("\nAlgorithm [%d] not found !!\n", algId));
                break;
        }
    }

    private void runPathChoisesAlgorithm(EvaluationFunction fx, long startTime) throws IOException {
        long timeElapsed;
        GreedyAlgorithm alg = new PathChoicesGreedyAlgorithm(new PathComparator(), new VirtualLinkComparator(), new RequestComparator(), problemInstance, fx);
        if (Tools.out != null) {
            Tools.echo = true;
        }
        MonoObjectiveSolution solution = alg.run();
        timeElapsed = System.nanoTime() - startTime;
        if (Tools.out != null) {
            Tools.echo = false;
        }
        double timeMS = timeElapsed / 1000000.;
        if (Tools.runOnlineVersion) {
            markRequest((MatrixSolution) solution);
        }
        storeResults(String.format(GREEDY_TIME_FORMAT, graphFile, requestFolder, pathsFile, utils.getSeed(), 1, timeMS, fx.EFO()), solution, timeMS);
    }

    private void runMultiChoisesAlgorithm(EvaluationFunction fx, long startTime) throws IOException {
        long timeElapsed;
        VirtualNodeComparator virtualNodeComparator = new VirtualNodeComparator();
        NodeFittestResourcesComparator CUsComparator = new NodeFittestResourcesComparator();
        GreedyAlgorithm alg = new MultiChoicesGreedyAlgorithm(new RequestComparator(), virtualNodeComparator, virtualNodeComparator, CUsComparator, new NodeDistanceComparator(), new PathComparator(), problemInstance, fx);
        if (Tools.out != null) {
            Tools.echo = true;
        }
        MonoObjectiveSolution solution = alg.run();
        timeElapsed = System.nanoTime() - startTime;
        if (Tools.out != null) {
            Tools.echo = false;
        }
        double timeMS = timeElapsed / 1000000.;
        if (Tools.runOnlineVersion) {
            markRequest((MatrixSolution) solution);
        }
        storeResults(String.format(GREEDY_TIME_FORMAT, graphFile, requestFolder, pathsFile, utils.getSeed(), 1, timeMS, fx.EFO()), solution, timeMS);
    }

    private void runExhaustiveAlgorithm(EvaluationFunction fx, long startTime) throws IOException {
        long timeElapsed;
        ExhaustiveSearch alg = new ExhaustiveSearch(problemInstance, fx);
        if (Tools.out != null) {
            Tools.echo = true;
        }
        MonoObjectiveSolution solution = alg.run();
        timeElapsed = System.nanoTime() - startTime;
        if (Tools.out != null) {
            Tools.echo = false;
        }
        double timeMS = timeElapsed / 1000000.;
        if (Tools.runOnlineVersion) {
            markRequest((MatrixSolution) solution);
        }
        storeResults(String.format(GREEDY_TIME_FORMAT, graphFile, requestFolder, pathsFile, utils.getSeed(), alg.getNumberOfIterations(), timeMS, fx.EFO()), solution, timeMS);
    }

    private void runGeneticAlgorithm(EvaluationFunction fx, long startTime) throws IOException {
        long timeElapsed;
        Random rand = utils.getRandom();
        Selection<MatrixSolution> selection = new StochasticRanking(fx.isMaximization(), rand, Tools.rank);
        Mutation<MatrixSolution> mutation = new MultiBit(rand, problemInstance);
        Crossover<MatrixSolution> cross = (Crossover<MatrixSolution>) getCrossoverOperator(rand, Tools.crxId);
        Reparator<MatrixSolution> rep = (Reparator<MatrixSolution>) getReparator(rand, problemInstance, Tools.repId);
        Initialization<MatrixSolution> init = (Initialization<MatrixSolution>) getInitialization(rand, Tools.init);
        GeneticAlgorithm alg = new GeneticAlgorithm(problemInstance, fx, mutation, cross, selection, init, rep, rand, numIterations, popSize);
        if (Tools.out != null) {
            Tools.echo = true;
        }
        MonoObjectiveSolution solution = alg.run();
        timeElapsed = System.nanoTime() - startTime;
        if (Tools.out != null) {
            Tools.echo = false;
        }
        double timeMS = timeElapsed / 1000000.;
        if (Tools.printFront) {
            storeFronts(alg.paretoFront);
        }
        if (Tools.runOnlineVersion) {
            markRequest((MatrixSolution) solution);
        }
        storeResults(String.format(POPULATION_TIME_FORMAT, graphFile, requestFolder, pathsFile, utils.getSeed(), alg.getNumIterations(), timeMS, fx.EFO(), alg.getNumNotValidSolutions()), solution, timeMS);
    }

    public void runGurobi(String[] args) {
        try {
            String modelName = "SCRDF_" + args[2];
            SCRDF_MIP gurobiSolver = new SCRDF_MIP();
            gurobiSolver.initialize(args[0], args[1], modelName);// cantidad de variables
            gurobiSolver.initVars();// variables de decision
            gurobiSolver.initValues();// Establecer valores / matrices de costos
            gurobiSolver.initObjectiveFunction();// funcion objetivo
            gurobiSolver.initConstraints();// Constraints
            gurobiSolver.execModel(String.format("%s.lp", args[2]), Tools.echo, Tools.echo, args[2]);// guardar, ejecutar el modelo
            gurobiSolver.clear();//limpiar elementos
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    private void runMOEA(EvaluationFunction Fx, long startTime) throws IOException {
        long timeElapsed;
        int numObjs = 2;
        if (Tools.fxId == 7) {
            numObjs = 3;
        }
        Random rand = utils.getRandom();
        MultiObjectiveFunction fx = (MultiObjectiveFunction) Fx;
        Selection<MultiObjectiveMatrixSolution> selection = new ModifiedStochasticRanking(fx.isMaximization(), rand, Tools.rank, numObjs);
        Mutation<MultiObjectiveMatrixSolution> mutation = new MultiBit(rand, problemInstance);
        Crossover<MultiObjectiveMatrixSolution> cross = (Crossover<MultiObjectiveMatrixSolution>) getCrossoverOperator(rand, Tools.crxId);
        Reparator<MultiObjectiveMatrixSolution> rep = (Reparator<MultiObjectiveMatrixSolution>) getReparator(rand, problemInstance, Tools.repId);
        Initialization init = Tools.init == 1 ? (problemInstance.isFullyRepresentated() ? new MOGF(problemInstance, popSize, rand, numObjs) : new MOGP(problemInstance, popSize, rand, numObjs)) : new MORand(problemInstance, popSize, rand, numObjs);
        MOEA alg = new MOEA(problemInstance, fx, rand, mutation, cross, selection, init, rep, numIterations, popSize, numObjs);
        if (Tools.out != null) {
            Tools.echo = true;
        }
        List<MultiObjectiveMatrixSolution> fronts = alg.run();
        timeElapsed = System.nanoTime() - startTime;
        if (Tools.out != null) {
            Tools.echo = false;
        }
        double timeMS = timeElapsed / 1000000.;
        if (Tools.printFront) {
            Collections.sort(fronts, (MultiObjectiveMatrixSolution o1, MultiObjectiveMatrixSolution o2) -> Double.compare(-o1.getObjective(), -o2.getObjective()));
            storeFronts(fronts);
        }
        if (Tools.runOnlineVersion) {
            markRequest((MatrixSolution) alg.getBest());
        }
        storeResults(String.format(POPULATION_TIME_FORMAT, graphFile, requestFolder, pathsFile, utils.getSeed(), alg.getNumIterations(), timeMS, fx.EFO(), alg.getNumNotValidSolutions()), alg.getBest(), timeMS);
    }

    private void runFullChoisesAlgorithm(EvaluationFunction fx, long startTime) throws IOException {
        long timeElapsed;
        RequestComparator requestComparator = (RequestComparator) getComparator(0);
        VirtualNodeComparator vCUComparator = (VirtualNodeComparator) getComparator(3);
        VirtualNodeComparator vDUComparator = (VirtualNodeComparator) getComparator(4);
        NodeComparator cuComparator = (NodeComparator) getComparator(1);
        NodeComparator duComparator = (NodeComparator) getComparator(2);
        Comparator<PathSolution> pathComparator = getComparator(5);
        GreedyAlgorithm alg = new FullChoicesGreedyAlgorithm(requestComparator, vCUComparator, vDUComparator, cuComparator, duComparator, pathComparator, problemInstance, fx);
        if (Tools.out != null) {
            Tools.echo = true;
        }
        MonoObjectiveSolution solution = alg.run();
        timeElapsed = System.nanoTime() - startTime;
        if (Tools.out != null) {
            Tools.echo = false;
        }
        double timeMS = timeElapsed / 1000000.;
        if (Tools.runOnlineVersion) {
            markRequest((MatrixSolution) solution);
        }
        storeResults(String.format(GREEDY_TIME_FORMAT, graphFile, requestFolder, pathsFile, utils.getSeed(), 1, timeMS, fx.EFO()), solution, timeMS);
    }

    private void runRandomChoisesAlgorithm(EvaluationFunction fx, long startTime) throws IOException {
        long timeElapsed;
        Random rand = utils.getRandom();
        GreedyAlgorithm alg = new RandomChoicesAlgorithm(rand, problemInstance, fx);
        if (Tools.out != null) {
            Tools.echo = true;
        }
        MonoObjectiveSolution solution = alg.run();
        timeElapsed = System.nanoTime() - startTime;
        if (Tools.out != null) {
            Tools.echo = false;
        }
        double timeMS = timeElapsed / 1000000.;
        if (Tools.runOnlineVersion) {
            markRequest((MatrixSolution) solution);
        }
        storeResults(String.format(GREEDY_TIME_FORMAT, graphFile, requestFolder, pathsFile, utils.getSeed(), 1, timeMS, fx.EFO()), solution, timeMS);
    }

    private void runNewGreedyAlgorithm(EvaluationFunction fx, long startTime) throws IOException {
        long timeElapsed;
        GreedyAlgorithm alg = new NewOnlineGreedy(residualNetwork, problemInstance, fx);
        if (Tools.out != null) {
            Tools.echo = true;
        }
        MonoObjectiveSolution solution = alg.run();
        timeElapsed = System.nanoTime() - startTime;
        if (Tools.out != null) {
            Tools.echo = false;
        }
        double timeMS = timeElapsed / 1000000.;
        if (Tools.runOnlineVersion) {
            markRequest((MatrixSolution) solution);
        }
        storeResults(String.format(GREEDY_TIME_FORMAT, graphFile, requestFolder, pathsFile, utils.getSeed(), 1, timeMS, fx.EFO()), solution, timeMS);
    }

    private void runNewGeneticAlgorithm(EvaluationFunction fx, long startTime) throws IOException {
        long timeElapsed;
        Random rand = utils.getRandom();
        Mutation<MatrixSolution> mutation = new OnePathCombination(rand, problemInstance);
        Crossover<MatrixSolution> cross = (Crossover<MatrixSolution>) getCrossoverOperator(rand, 3 /*Tools.crxId*/);
        Reparator<MatrixSolution> rep = (Reparator<MatrixSolution>) getReparator(rand, problemInstance, 2/*Tools.repId*/);
        Initialization<MatrixSolution> init = (Initialization<MatrixSolution>) getInitialization(rand, 2/*Tools.init*/);
        AlternativeGA alg = new AlternativeGA(problemInstance, fx, mutation, cross, init, rep, rand, numIterations, popSize);
        if (Tools.out != null) {
            Tools.echo = true;
        }
        MonoObjectiveSolution solution = alg.run();
        timeElapsed = System.nanoTime() - startTime;
        if (Tools.out != null) {
            Tools.echo = false;
        }
        double timeMS = timeElapsed / 1000000.;
        if (Tools.runOnlineVersion) {
            markRequest((MatrixSolution) solution);
        }
        storeResults(String.format(POPULATION_TIME_FORMAT, graphFile, requestFolder, pathsFile, utils.getSeed(), alg.getNumIterations(), timeMS, fx.EFO(), alg.getNumNotValidSolutions()), solution, timeMS);
    }

    private void runIterativeSearchAlgorithm(EvaluationFunction fx, long startTime) throws IOException {
        long timeElapsed;
        Random rand = utils.getRandom();
        IterativeSearch alg = new IterativeSearch(false, problemInstance, fx, rand, numIterations, popSize);
        if (Tools.out != null) {
            Tools.echo = true;
        }
        MonoObjectiveSolution solution = alg.run();
        timeElapsed = System.nanoTime() - startTime;
        if (Tools.out != null) {
            Tools.echo = false;
        }
        double timeMS = timeElapsed / 1000000.;
        if (Tools.runOnlineVersion) {
            markRequest((MatrixSolution) solution);
        }
        storeResults(String.format(POPULATION_TIME_FORMAT, graphFile, requestFolder, pathsFile, utils.getSeed(), alg.getNumIterations(), timeMS, fx.EFO(), alg.getNumNotValidSolutions()), solution, timeMS);
    }

    private void runSplitRANHeu(EvaluationFunction fx, long startTime) throws IOException {
        long timeElapsed;
        GreedyAlgorithm alg = new SplitRANHeu(problemInstance, fx);
        if (Tools.out != null) {
            Tools.echo = true;
        }
        MonoObjectiveSolution solution = alg.run();
        timeElapsed = System.nanoTime() - startTime;
        if (Tools.out != null) {
            Tools.echo = false;
        }
        double timeMS = timeElapsed / 1000000.;
        if (Tools.runOnlineVersion) {
            markRequest((MatrixSolution) solution);
        }
        storeResults(String.format(GREEDY_TIME_FORMAT, graphFile, requestFolder, pathsFile, utils.getSeed(), 1, timeMS, fx.EFO()), solution, timeMS);
    }

    private void storeFronts(List<? extends Solution> fronts) throws IOException {
        if (sbFronts == null) {
            sbFronts = new StringBuilder();
        }
        if (sbConf == null) {
            sbConf = new StringBuilder();
        }
        int i = 0;
        for (Solution it : fronts) {
            String s = it.toString();
            String[] data = s.split("@");
            if (data != null && data.length > 1) {
                sbFronts.append(String.format("%d %s", ++i, data[0].concat("\n")));
                sbConf.append(String.format("%d %s", i, data[1].concat("\n")));
                if (Tools.echo) {
                    System.out.println(String.format("Fr.%d| %s", i, s));
                }
            }
        }
    }

    private void markRequest(MatrixSolution solution) {
        if (solution != null) {
            if (solution.isValid && solution.gn == 0) {
                for (int i = 0; i < solution.accepted.length; i++) {
                    utils.getRequests()[i].accepted = solution.accepted[i];
                }
            } else {
                solution.isValid = false;
                for (Request request : utils.getRequests()) {
                    request.accepted = false;
                }
            }
        } else {
            for (Request request : utils.getRequests()) {
                request.accepted = false;
            }
        }
    }

    private void storeResults(String time, MonoObjectiveSolution solution, double timeMS) throws IOException {
        if (!Tools.runOnlineVersion) {
            String sol;
            if (solution != null) {
                sol = String.format("[%s][%s][%s] %s\n", graphFile, requestFolder, pathsFile, solution.toString());
            } else {
                sol = String.format("[%s][%s][%s] No valid solution", graphFile, requestFolder, pathsFile).concat("\n");
            }
            if (solutionFile != null) {
                sbSolution.append(sol);
            }
            if (timeFile != null) {
                sbTimes.append(time);
            }
            if (Tools.echo) {
                System.out.print(time);
                System.out.println(sol);
            }
        } else {
            if (solution != null) {
                MultiObjectiveMatrixSolution m = new MultiObjectiveMatrixSolution((MatrixSolution) solution, 3);
                sbMetrics.append(String.format("%f\t%f\t%d\t%f\t%d\t%b\n", function.eval(m, 0), function.eval(m, 1), m.nAccepted, timeMS, m.accepted.length, m.isValid));
            } else {
                sbMetrics.append(String.format("0\t0\t0\t%f\t \tfalse", timeMS).concat("\n"));
            }
            sbConfig.append(getConfiguration().concat("\n"));
        }
    }

    /**
     *
     * @param fx 0: DoC*(1-UoL), 1: DoC*(1-UoL)+acc, 2: c*DoC*(1-UoL)+acc/q, 3:
     * c*DoC*(1-UoL)+acc, 4: acc/q, 5: [DoC,UoL], 6: [DoC*(1-UoL),acc/q], 7:
     * [DoC,UoL,acc/q]
     * @return
     */
    private EvaluationFunction getObjectiveFunction(int fx) {
        switch (fx) {
            case 0:
                return new ObjectiveFunction(problemInstance, w1, w2, w3, Tools.isMaximization);
            case 1:
                return new RequestObjectiveFunction(problemInstance, w1, w2, w3, Tools.isMaximization);
            case 2:
                return new MinorRequestObjectiveFunction(problemInstance, w1, w2, w3, Tools.isMaximization, Tools.c);
            case 3:
                return new SimilarObjectiveFunction(problemInstance, w1, w2, w3, Tools.isMaximization, Tools.c);
            case 4:
                return new AcceptanceRate(problemInstance, w1, w2, w3, Tools.isMaximization);
            case 5:
                return new LevelOfCentralizationAndUseOfLinks(problemInstance, w1, w2, w3, Tools.isMaximization);
            case 6:
                return new CentralizationDegreeAndAcceptanceRate(w1, w2, w3, problemInstance, Tools.isMaximization);
            case 7:
                return new LoCAndUoLAndAcc(problemInstance, w1, w2, w3, Tools.isMaximization);
            case 8:
                return new NodesUsage(problemInstance, w1, w2, w3, Tools.isMaximization);
            case 9:
                return new LessLatencyAndNodesUsage(problemInstance, w1, w2, w3, Tools.isMaximization);
            default:
                return null;
        }
    }

    /**
     *
     * @param rand
     * @param problemInstance
     * @param rep 1: GreedyReparator, 2: random reparator
     * @return
     */
    private Reparator getReparator(Random rand, ProblemInstance problemInstance, int rep) {
        switch (rep) {
            case 1:
                return new GreedyReparator<>(rand, problemInstance);
            case 2:
                return new RandomReparator<>(rand, problemInstance);
            default:
                return null;
        }
    }

    /**
     *
     * @param opc
     * @return
     */
    private Comparator getComparator(int opc) {
        switch (opc) {
            case 0:// request comparator
                if (Tools.requestComparatorId == 1) {
                    return new RequestComparator();
                }
                break;
            case 1:// CU comparator
                if (Tools.cuNodeComparatorId == 1) {
                    return new NodeFittestResourcesComparator();
                }
                if (Tools.cuNodeComparatorId == 2) {
                    return new NodeLessFitResourcesComparator();
                }
                break;
            case 2://DU comparator
                if (Tools.duNodeComparatorId == 1) {
                    return new NodeDistanceComparator();
                }
                break;
            case 3:// vCU comparator
                if (Tools.vCuNodeComparatorId == 1) {
                    return new VirtualNodeComparator();
                }
                break;
            case 4:// vDU comparator
                if (Tools.vDuNodeComparatorId == 1) {
                    return new VirtualNodeComparator();
                }
                break;
            case 5:
                if (Tools.pathsComparatorId == 1) {
                    return new PathComparator();
                }
                break;
        }
        return null;
    }

    /**
     *
     * @param rand
     * @param crx 0: Uniform, 1: Laplace, 2: NewCrossover, 3: Tournament
     * selection and OnePoint crossover.
     * @return
     */
    private Crossover getCrossoverOperator(Random rand, int crx) {
        switch (crx) {
            case 0:
                return new Uniform<>(rand, Tools.cross);
            case 1:
                return new LaplaceCrossover<>(rand, problemInstance, Tools.cross, 0, 1);
            case 2:
                return new NewCrossover<>(Tools.cross, rand);
            case 3:
                int rounds = 20;//num. rounds for tournament selection
                double prob = 0.75;//recombination rate
                return new OnePoinitCrossover<>(rand, prob/*Tools.cross*/, rounds);
            default:
                return null;
        }
    }

    /**
     *
     * @param rand
     * @param init 0: Randomly, 1: Greedy (Path or Full), 2: RandomCentralized
     * @return
     */
    private Initialization getInitialization(Random rand, int init) {
        switch (init) {
            case 0:
                return new Randomly(problemInstance, popSize, rand);
            case 1:
                return problemInstance.isFullyRepresentated()
                        ? new GreedyFull(problemInstance, popSize, rand)
                        : new GreedyPaths(problemInstance, popSize, rand);
            case 2:
                return new RandomCentralized<>(rand, problemInstance, popSize);
            default:
                return null;
        }
    }

    public void runOnlineVersion(String configFile, String paramsFilePath, String outputName) throws IOException {
        if (!loadParameters(paramsFilePath)) {
            return;
        }
        outFile = new File(new File(Paths.get(configFile).getParent().toString(), "results"), outputName).getAbsolutePath();
        sbMetrics = new StringBuilder();
        sbConfig = new StringBuilder();
        if (Tools.out != null) {
            setOutput();
        }
        if (Tools.echo) {
            System.out.println(String.format("Reading scenario: %s", configFile));
        }
        try (BufferedReader br = new BufferedReader(new FileReader(new File(configFile)))) {
            String line = br.readLine();//read substrate network at begining of file
            Tools.k = 3;
            graphFile = line;//path of substrate network
            pathsFile = new StringBuilder().append(line).append(".k3.paths").toString();//path to store the paths
            List<Request> requestList = new ArrayList<>();
            List<String> requestName = new ArrayList<>();
            w3 = 1.0 - w1 - w2;
            if (utils.loadGraph(new File(graphFile), g1, g2, g3, true) && loadPaths()) {//if network and paths are loaded then
                if (Tools.echo) {
                    System.out.printf("nDUs: %d, nCUs: %d\n", utils.getDUs().size(), utils.getCUs().size());
                    System.out.println();
                }
                //read the requests
                int reqIdx = 0, maxVirtualDUs, maxVirtualCUs;
                while ((line = br.readLine()) != null) {
                    String[] data = line.split("\t");
                    switch (data[1]) {
                        case "add":
                            int n,
                             maxN = 0;
                            maxVirtualDUs = 0;
                            maxVirtualCUs = 0;
                            requestFolder = data[2];
                            String[] totalRequestsAtTime = data[2].split(",");
                            Request[] requests = new Request[totalRequestsAtTime.length];
                            for (int i = 0; i < totalRequestsAtTime.length; i++) {
                                requestList.add(new Request());
                                requestName.add(totalRequestsAtTime[i]);
                                if (utils.loadRequest(new File(totalRequestsAtTime[i]), requestList.get(reqIdx))) {
                                    requests[i] = requestList.get(reqIdx);
                                    for (VirtualNode vNode : requests[i].vNodes) {
                                        vNode.ant = 0;
                                    }
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
                                    if (Tools.echo) {
                                        System.out.println(String.format("%s->Request_[%d] in buffer, vCUs:%d, vDUs:%d", data[0], reqIdx + 1, requestList.get(reqIdx).vCUs.size(), requestList.get(reqIdx).vDUs.size()));
                                    }
                                } else {
                                    System.out.println(String.format("ERROR AL CARGAR PETICION: %s", requestName.get(reqIdx)));
                                }
                                reqIdx++;
                            }
                            utils.setMaxVirtualCUs(maxVirtualCUs);
                            utils.setMaxVirtualDUs(maxVirtualDUs);
                            utils.setRequests(requests);
                            if (problemInstance != null) {
                                residualNetwork = problemInstance.copySubInstance();
                                utils.setPaths(residualNetwork.mPaths);
                                utils.setCUs(residualNetwork.CUs);
                                utils.setDUs(residualNetwork.DUs);
                                utils.setNodes(residualNetwork.nodes);
                                residualNetwork = problemInstance;
                                problemInstance.allPathsFromDUs = utils.getAllPathsFromDUs();
                                runAlgorithm();
                                problemInstance = residualNetwork;
                                utils.setPaths(residualNetwork.mPaths);
                                utils.setCUs(residualNetwork.CUs);
                                utils.setDUs(residualNetwork.DUs);
                                utils.setNodes(residualNetwork.nodes);
                                for (Request request : requests) {
                                    if (request.accepted) {
                                        updateResources(request);
                                    }
                                }
                            } else {
                                runAlgorithm();
                            }
                            break;
                        case "release":
                            int i;
                            for (String requestFilePath : data[2].split(",")) {
                                if ((i = requestName.indexOf(requestFilePath)) != -1) {
                                    if (Tools.echo) {
                                        System.out.println(String.format("%s-> release Request_[%d]", data[0], i + 1));
                                    }
                                    releaseResources(requestList.get(i));
                                }
                            }
                            break;
                        default:
                            break;
                    }
                }
                saveResults();
            } else {
                System.out.println("Unable to load substrate network!!");
            }
        }
    }

    private void releaseResources(Request request) {
        if (request.accepted) {
            for (VirtualNode vCU : request.vCUs) {
                utils.getNodes()[vCU.indxNode].usedPRC -= vCU.prc;
                if (utils.getNodes()[vCU.indxNode].usedPRC < 0) {
                    if (Tools.echo) {
                        System.out.println(String.format("vCU prc %s", utils.getNodes()[vCU.indxNode]));
                    }
                    utils.getNodes()[vCU.indxNode].usedPRC = 0;
                }
            }
            for (VirtualNode vDU : request.vDUs) {
                if (vDU.indxNode == -1) {
                    break;
                }
                utils.getNodes()[vDU.indxNode].usedPRC -= vDU.prc;
                if (utils.getNodes()[vDU.indxNode].usedPRC < 0) {
                    if (Tools.echo) {
                        System.out.println(String.format("prc %s", utils.getNodes()[vDU.indxNode]));
                    }
                    utils.getNodes()[vDU.indxNode].usedPRC = 0;
                }
                utils.getNodes()[vDU.indxNode].usedANT -= vDU.ant;
                if (utils.getNodes()[vDU.indxNode].usedANT < 0) {
                    if (Tools.echo) {
                        System.out.println(String.format("ant %s", utils.getNodes()[vDU.indxNode]));
                    }
                    utils.getNodes()[vDU.indxNode].usedANT = 0;
                }
                utils.getNodes()[vDU.indxNode].usedPRB -= vDU.prb;
                if (utils.getNodes()[vDU.indxNode].usedPRB < 0) {
                    if (Tools.echo) {
                        System.out.println(String.format("prb %s", utils.getNodes()[vDU.indxNode]));
                    }
                    utils.getNodes()[vDU.indxNode].usedPRB = 0;
                }
            }
            for (VirtualLink vLink : request.virtualLinks) {
                if (Tools.isFully) {
                    int src = request.vNodes[vLink.source].indxNode, dst = request.vNodes[vLink.destination].indxNode;
                    for (Link link : utils.getPaths()[src][dst].get(vLink.indxPath).getLinks()) {
                        link.usedBw -= vLink.bw;
                        if (link.usedBw < 0) {
                            if (Tools.echo) {
                                System.out.println(String.format("bw %s", link));
                            }
                            link.usedBw = 0;
                        }
                    }
                } else {
                    for (Link link : utils.getPaths()[0][0].get(vLink.indxPath).getLinks()) {
                        link.usedBw -= vLink.bw;
                        if (link.usedBw < 0) {
                            if (Tools.echo) {
                                System.out.println(String.format("bw %s", link));
                            }
                            link.usedBw = 0;
                        }
                    }
                }
            }
            if (Tools.echo) {
                System.out.println("Resources released successfully\n");
            }
        } else if (Tools.echo) {
            System.out.println("Request not acepted.");
        }
    }

    private void updateResources(Request request) {
        for (VirtualNode vCU : request.vCUs) {
            utils.getNodes()[vCU.indxNode].usedPRC += vCU.prc;
            if (utils.getNodes()[vCU.indxNode].usedPRC > utils.getNodes()[vCU.indxNode].prc) {
                if (Tools.echo) {
                    System.out.println(String.format("CU prc %s", utils.getNodes()[vCU.indxNode]));
                }
                utils.getNodes()[vCU.indxNode].usedPRC = utils.getNodes()[vCU.indxNode].prc;
            }
        }
        for (VirtualNode vDU : request.vDUs) {
            if (vDU.indxNode == -1) {
                break;
            }
            utils.getNodes()[vDU.indxNode].usedPRC += vDU.prc;
            if (utils.getNodes()[vDU.indxNode].usedPRC > utils.getNodes()[vDU.indxNode].prc) {
                if (Tools.echo) {
                    System.out.println(String.format("DU prc %s", utils.getNodes()[vDU.indxNode]));
                }
                utils.getNodes()[vDU.indxNode].usedPRC = utils.getNodes()[vDU.indxNode].prc;
            }
            utils.getNodes()[vDU.indxNode].usedANT += vDU.ant;
            if (utils.getNodes()[vDU.indxNode].usedANT > utils.getNodes()[vDU.indxNode].ant) {
                if (Tools.echo) {
                    System.out.println(String.format("DU ant %s", utils.getNodes()[vDU.indxNode]));
                }
                utils.getNodes()[vDU.indxNode].usedANT = utils.getNodes()[vDU.indxNode].ant;
            }
            utils.getNodes()[vDU.indxNode].usedPRB += vDU.prb;
            if (utils.getNodes()[vDU.indxNode].usedPRB > utils.getNodes()[vDU.indxNode].prb) {
                if (Tools.echo) {
                    System.out.println(String.format("DU prb %s", utils.getNodes()[vDU.indxNode]));
                }
                utils.getNodes()[vDU.indxNode].usedPRB = utils.getNodes()[vDU.indxNode].prb;
            }
        }
        for (VirtualLink vLink : request.virtualLinks) {
            if (Tools.isFully) {
                int src = request.vNodes[vLink.source].indxNode, dst = request.vNodes[vLink.destination].indxNode;
                for (Link link : utils.getPaths()[src][dst].get(vLink.indxPath).getLinks()) {
                    link.usedBw += vLink.bw;
                    if (link.usedBw > link.bw) {
                        if (Tools.echo) {
                            System.out.println(String.format("Link bw %s", link));
                        }
                        link.usedBw = link.bw;
                    }
                }
            } else {
                for (Link link : utils.getPaths()[0][0].get(vLink.indxPath).getLinks()) {
                    link.usedBw += vLink.bw;
                    if (link.usedBw > link.bw) {
                        if (Tools.echo) {
                            System.out.println(String.format("Link bw %s", link));
                        }
                        link.usedBw = link.bw;
                    }
                }
            }
        }
    }

    private String getConfiguration() {
        if (algId == 4 || algId == 5 || algId == 9) {
            int elle = utils.getRequests().length * (problemInstance.step * utils.getMaxVirtualDUs() + (Tools.isFully ? utils.getMaxVirtualCUs() : 0));
            return String.format("algId:%d\tcrxId:%d\trepId:%d\tK:%d\tInit:%d\t"
                    + "mut:%.0f/%d\tcrx:%.2f\trnk:%.3f\tnIt:%d\tPop:%d\t"
                    + "seed:%d\trepr:%d\tC:%.2f\tFx:%d\tCUcomp:%d",
                    algId, Tools.crxId, Tools.repId, Tools.k, Tools.init,
                    Tools.mut, elle, Tools.cross, Tools.rank, numIterations,
                    popSize, utils.getSeed(), (Tools.isFully ? 1 : 0), Tools.c,
                    Tools.fxId, Tools.cuNodeComparatorId);
        }
        if (algId == 6) {
            return String.format("algId:%d\tK:%d\tseed:%d\trepr:%d\tC:%.2f\t"
                    + "Fx:%d\tCUcomp:%d",
                    algId, Tools.k, utils.getSeed(), (Tools.isFully ? 1 : 0), Tools.c,
                    Tools.fxId, Tools.cuNodeComparatorId);
        }
        return String.format("algId:%d\tK:%d\tseed:%d\trepr:%d\tC:%.2f\tFx:%d",
                algId, Tools.k, utils.getSeed(), (Tools.isFully ? 1 : 0), Tools.c,
                Tools.fxId);
    }

    private void saveResults() throws IOException {
        if (Tools.out != null) {
            try (Writer ws1 = new FileWriter(String.format("%s.generations", outFile), true)) {
                ws1.write(sbGenerations.toString());
            }
            Tools.echo = true;
            System.setOut(Tools.out);
        }
        if (sbFronts != null && sbConf != null) {
            try (FileWriter fwr = new FileWriter(String.format("%s.front", outFile), true); FileWriter cwr = new FileWriter(String.format("%s.conf", outFile), true)) {
                fwr.write(sbFronts.toString());
                cwr.write(sbConf.toString());
            }
        }
        if (!Tools.runOnlineVersion) {
            if (solutionFile != null) {
                try (Writer ws1 = new FileWriter(solutionFile, true)) {
                    ws1.write(sbSolution.toString());
                }
            }
            if (timeFile != null) {
                try (Writer wt1 = new FileWriter(timeFile, true)) {
                    wt1.write(sbTimes.toString());
                }
            }
        } else {
            if (function != null) {
                try (Writer wm1 = new FileWriter(String.format("%s.metrics", outFile), true)) {
                    wm1.write(sbMetrics.toString());
                }
            }
            try (Writer wm2 = new FileWriter(String.format("%s.settings", outFile), true)) {
                wm2.write(sbConfig.toString());
            }
        }
    }
}
