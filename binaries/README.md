Joint Route Selection and Split Management for 5G C-RAN

-- Usage:
	java -jar SCRDF.jar <path to configuration file> <path to instance file> [<path to output file (gurobi only)>]

-- Examples

	-- To run Exhaustive: you must enter the configuration file and the instance file.
		java -jar SCRDF.jar examples/config_0 examples/inst_0

	-- To run Gurobi: you must enter a third parameter, which corresponds to the path of the output file.
		java -jar SCRDF.jar examples/config_1 examples/inst_1 ./examples/results/gurobi_solver

	-- To run Greedy (GRE): similar to running the exhaustive algorithm.
		java -jar SCRDF.jar examples/config_2 examples/inst_2

	-- To run Genetic with random initialization (EA^rnd): similar to running the exhaustive algorithm.
		java -jar SCRDF.jar examples/config_3 examples/inst_3

	-- To run Genetic with intelligent initialization (EA^gre): similar to running the exhaustive algorithm.
		java -jar SCRDF.jar examples/config_4 examples/inst_4

-- NOTE:
 - The execution of the algorithms differs in the attributes of the configuration file, for this reason, the provided examples contain different configuration files. The instance files vary because it was chosen to display the results in different output files.
 - Path files are generated during execution for each instance of the problem. In case a path file already exists, the file will be loaded and the saved paths will be used during the execution of the algorithm.
 - We use Java 1.8.


