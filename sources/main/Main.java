package main;

import domain.util.Runner;
import domain.util.Tools;

/**
 *
 * @author cristian.erazo@cinvestav.mx
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        int av = args.length;
        boolean version = false, help = false;
        Runner runner = Runner.getInstance();
        if (args.length > 0) {
            for (String arg : args) {
                if ("-verbose".compareToIgnoreCase(arg) == 0) {
                    Tools.ECHO = true;
                    av--;
                }
                if ("-version".compareToIgnoreCase(arg) == 0) {
                    if (!version) {
                        version = true;
                        System.out.println(String.format("SCRDF_v%s", Tools.VERSION));
                    }
                    av--;
                } else if ("-h".compareToIgnoreCase(arg) == 0 || "-help".compareToIgnoreCase(arg) == 0) {
                    if (!help) {
                        help = true;
                        displayInfo();
                    }
                    av--;
                }
            }
            if (!(help || version)) {
                if (av == 2) {
                    runner.runConfigFile(args[0], args[1]);
                } else if (av == 3) {
                    runner.runGurobi(args);
                } else if (av > 4) {
                    runner.runCommandLine(args);
                } else {
                    System.out.println("Wrong parameters. For help, input the option -h or -help.");
                }
            }
        } else {
            System.out.println("Missing parameters. For help, input the option -h or -help.");
        }
    }

    private static void displayInfo() {
        System.out.println("Usage:                                                                                                          ");
        System.out.println("<Graph_file:string_path> <Requests_folder:string_path> <algorithm:#id> <numIterations:int> <populationSize:int> ");
        System.out.println("     [-seed <#seed>] [-w1 <w_PHY:float> -w2 <w_MAC:float>] [-k <#paths>] [-paths <paths_file:string_path>]      ");
        System.out.println("           [-fitAll] [-usePaths] [-out <output_file:string_path>] [-verbose] [-h|-help]                         ");
        System.out.println("                                                                                                                ");
        System.out.println("Required parameters:                                                                                            ");
        System.out.println("Graph_file            Path of the GML file that represents the substrate network.                               ");
        System.out.println("Requests_folder       Path of the utils.getRequests() folder that contains the virtual networks.                ");
        System.out.println("algorithm             1. Exhaustive search. 2. Multi-choises greedy. 3. Path-choises greedy.                    ");
        System.out.println("                      4. Genetic Algorithm. 0. Make the file of paths.                                          ");
        System.out.println("numIterations         The number of iterations to run the algorithm. Used when the algorithm is set to 4.       ");
        System.out.println("populationSize        The number of individuals on each population (used for the Genetic Algorithm).            ");
        System.out.println("                                                                                                                ");
        System.out.println("Optional parameters:                                                                                            ");
        System.out.println("-seed <#seed>         Define the seed of the random number generator.                                           ");
        System.out.println("-w1 <w_PHY>           Weight for the physical layer splits. Default: 0.7                                        ");
        System.out.println("-w2 <w_MAC>           Weight for the medium access control layer. Default: 0.2                                  ");
        System.out.println("-k <#paths>           Define the maximum number of paths to explore (if no paths_file are loaded). When this    ");
        System.out.println("                      flag is not set (or is equal to zero), then all possible paths are explored.              ");
        System.out.println("-paths <paths_file>   Path of the file that contains the list of PathSolution. If paths_file exists, all paths  ");
        System.out.println("                      will be loaded, if not exists, then, all paths found will be saved.                       ");
        System.out.println("-fitAll               If this flag is set, the algorithm tries to find the best configuration for all the       ");
        System.out.println("                      requests. Otherwise, the algorithm tries to find the best configuration for some subset of");
        System.out.println("                      the requests.                                                                             ");
        System.out.println("-usePaths             Uses the path representation (used for exhaustive search).                                ");
        System.out.println("-out <output_file>    Sets the path of the output files (time and solution).                                    ");
        System.out.println("-verbose              Display information of each process.                                                      ");
        System.out.println("-h, -help             View the information of the current program version.                                      ");
    }
}
