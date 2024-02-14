package domain.monoobjective.implementation.mip_sat;

import domain.monoobjective.implementation.MatrixSolution;
import domain.monoobjective.implementation.ObjectiveFunction;
import domain.monoobjective.implementation.greedy.GreedyAlgorithm;
import domain.monoobjective.implementation.greedy.algorithms.MultiChoicesGreedyAlgorithm;
import domain.monoobjective.implementation.greedy.algorithms.PathChoicesGreedyAlgorithm;
import domain.monoobjective.implementation.greedy.comparators.NodeDistanceComparator;
import domain.monoobjective.implementation.greedy.comparators.NodeFittestResourcesComparator;
import domain.monoobjective.implementation.greedy.comparators.PathComparator;
import domain.monoobjective.implementation.greedy.comparators.RequestComparator;
import domain.monoobjective.implementation.greedy.comparators.VirtualLinkComparator;
import domain.monoobjective.implementation.greedy.comparators.VirtualNodeComparator;
import domain.monoobjective.implementation.metaheuristics.algorithms.GeneticAlgorithm;
import domain.monoobjective.implementation.metaheuristics.algorithms.crossover.Uniform;
import domain.monoobjective.implementation.metaheuristics.algorithms.initialization.GreedyFull;
import domain.monoobjective.implementation.metaheuristics.algorithms.initialization.GreedyPaths;
import domain.monoobjective.implementation.metaheuristics.algorithms.mutation.MultiBit;
import domain.monoobjective.implementation.metaheuristics.algorithms.selection.StochasticRanking;
import domain.operators.Initialization;
import domain.operators.Mutation;
import domain.operators.Selection;
import domain.paths.PathSolution;
import domain.problem.ProblemInstance;
import domain.problem.graph.*;
import domain.problem.virtual.VirtualNode;
import domain.util.Runner;
import domain.util.Tools;
import gurobi.*;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 *
 * @author cristian.erazo@cinvestav.mx
 */
public class SCRDF_MIP {

    private GRBEnv env;
    private Runner runner;
    private GRBModel model;
    private ProblemInstance instance;
    private List<PathSolution> paths;
    private int nCUs;
    private int nDUs;
    private int nPaths;
    private int nEdges;
    private int n_vDUs;
    private int n_vCUs;
    private int nSplits;
    private int nRequests;
    private GRBVar[] b_CUs;
    private GRBVar[] b_req;
    private GRBVar[] b_path;
    private GRBVar[][] b_Path_CU;
    private GRBVar[][] b_Path_DU;
    private GRBVar[][] b_req_path;
    private GRBVar[][] b_path_edge;
    private GRBVar[][] b_req_vDUs_L1;
    private GRBVar[][] b_req_vDUs_L2;
    private GRBVar[][] b_req_vDUs_L3;
    private GRBVar[][][] b_req_vCU_CU;
    private GRBVar[][][] b_req_vDU_DU;
    private GRBVar[][][] b_req_vDU_vCU;
    private GRBVar[][][] b_req_vDU_Path;
    private GRBVar[][][] b_req_vDU_Split;
    private GRBVar[][][][] b_req_vDU_vCU_Path;
    private long start;
    private long loadTime;
    private double elapsedTime;
    public ObjectiveFunction Fx;
    public MatrixSolution best;
    public double solverTotalTime;

    public SCRDF_MIP() throws GRBException {
        Fx = null;
        env = new GRBEnv();
        model = new GRBModel(env);
        // set the output text off
        if (!Tools.ECHO) {
            model.set(GRB.IntParam.LogToConsole, 0);
            model.set(GRB.StringParam.LogFile, "");
            model.set(GRB.IntParam.OutputFlag, 0);
        }
        model.set(GRB.DoubleParam.TimeLimit, 3 * 60 * 60);//tiempo máximo de 3 horas
//        model.set(GRB.IntParam.Threads, 4);
        runner = Runner.getInstance();
    }

    public void initialize(String config, String inst, String modelName) throws GRBException {
        start = System.nanoTime();
        if (!runner.loadFiles(config, inst)) {//load configurations and instance information
            throw new GRBException("Error: config file and/or instance file can't be loaded.");
        }
        instance = runner.getProblemInstance();
        loadTime = System.nanoTime() - start;
        nRequests = instance.requests.length;
        nEdges = instance.nLinks;
        nCUs = instance.CUs.size();
        nDUs = instance.DUs.size();
        paths = new ArrayList<>();
        if (instance.isFullyRepresentated()) {
            nPaths = 0;
            for (Node CU : instance.CUs) {
                for (Node DU : instance.DUs) {
                    if (instance.mPaths[CU.nodePosition][DU.nodePosition] != null) {
                        nPaths += instance.mPaths[CU.nodePosition][DU.nodePosition].size();
                        paths.addAll(instance.mPaths[CU.nodePosition][DU.nodePosition]);
                    }
                }
            }
        } else {
            paths.addAll(instance.mPaths[0][0]);
            nPaths = paths.size();
        }
        n_vDUs = instance.maxVirtualDUs;
        n_vCUs = instance.maxVirtualCUs;
        nSplits = instance.splits.length;
        b_req = new GRBVar[nRequests];
        b_req_vCU_CU = new GRBVar[nRequests][n_vCUs][nCUs];
        b_req_vDU_DU = new GRBVar[nRequests][n_vDUs][nDUs];
        b_req_vDU_Split = new GRBVar[nRequests][n_vDUs][nSplits];
        b_req_path = new GRBVar[nRequests][nPaths];
        b_Path_DU = new GRBVar[nPaths][nDUs];
        b_Path_CU = new GRBVar[nPaths][nCUs];
        b_req_vDUs_L1 = new GRBVar[nRequests][n_vDUs];
        b_req_vDUs_L2 = new GRBVar[nRequests][n_vDUs];
        b_req_vDUs_L3 = new GRBVar[nRequests][n_vDUs];
        b_req_vDU_vCU_Path = new GRBVar[nRequests][n_vDUs][n_vCUs][nPaths];
        b_req_vDU_Path = new GRBVar[nRequests][n_vDUs][nPaths];
        b_path_edge = new GRBVar[nPaths][nEdges];
        b_path = new GRBVar[nPaths];
        b_CUs = new GRBVar[nCUs];
        b_req_vDU_vCU = new GRBVar[nRequests][n_vDUs][n_vCUs];
        model.set(GRB.StringAttr.ModelName, modelName);
    }

    public void initVars() throws GRBException {
        // peticiones: aceptada = 1, rechazada = 0
        for (int r = 0; r < nRequests; r++) {
            b_req[r] = model.addVar(0., 1., 0., GRB.BINARY, "R_" + r);
        }
        // asignaciones de vCUs: aceptada = 1, rechazada = 0
        for (int r = 0; r < nRequests; r++) {
            for (int i = 0; i < n_vCUs; i++) {
                for (int j = 0; j < nCUs; j++) {
                    if (i < instance.requests[r].vCUs.size()) {
                        b_req_vCU_CU[r][i][j] = model.addVar(0., 1., 0., GRB.BINARY, "R_" + r + "_vCU_" + i + "_CU_" + j);
                    } else {
                        b_req_vCU_CU[r][i][j] = model.addVar(0., 0., 0., GRB.BINARY, "R_" + r + "_vCU_" + i + "_CU_" + j);
                    }
                }
            }
        }
        // asignaciones de vDUs: aceptada = 1, rechazada = 0
        for (int r = 0; r < nRequests; r++) {
            for (int i = 0; i < n_vDUs; i++) {
                for (int j = 0; j < nDUs; j++) {
                    if (i < instance.requests[r].vDUs.size()) {
                        b_req_vDU_DU[r][i][j] = model.addVar(0., 1., 0., GRB.BINARY, "R_" + r + "_vDU_" + i + "_DU_" + j);
                    } else {
                        b_req_vDU_DU[r][i][j] = model.addVar(0., 0., 0., GRB.BINARY, "R_" + r + "_vDU_" + i + "_DU_" + j);
                    }
                }
            }
        }
        // asignaciones de vDUs a Splits
        for (int r = 0; r < nRequests; r++) {
            for (int i = 0; i < n_vDUs; i++) {
                for (int j = 0; j < nSplits; j++) {
                    if (i < instance.requests[r].vDUs.size()) {
                        b_req_vDU_Split[r][i][j] = model.addVar(0., 1., 0., GRB.BINARY, "R_" + r + "_vDU_" + i + "_Split_" + j);
                    } else {
                        b_req_vDU_Split[r][i][j] = model.addVar(0., 0., 0., GRB.BINARY, "R_" + r + "_vDU_" + i + "_Split_" + j);
                    }
                }
            }
        }
        //asignaciones de vDUs a conjuntos de splits
        for (int r = 0; r < nRequests; r++) {
            for (int i = 0; i < n_vDUs; i++) {
                if (i < instance.requests[r].vDUs.size()) {
                    b_req_vDUs_L1[r][i] = model.addVar(0., 1., 0., GRB.BINARY, "R_" + r + "_vDU" + i + "_L1");
                } else {
                    b_req_vDUs_L1[r][i] = model.addVar(0., 0., 0., GRB.BINARY, "R_" + r + "_vDU" + i + "_L1");
                }
            }
        }
        for (int r = 0; r < nRequests; r++) {
            for (int i = 0; i < n_vDUs; i++) {
                if (i < instance.requests[r].vDUs.size()) {
                    b_req_vDUs_L2[r][i] = model.addVar(0., 1., 0., GRB.BINARY, "R_" + r + "_vDU" + i + "_L2");
                } else {
                    b_req_vDUs_L2[r][i] = model.addVar(0., 0., 0., GRB.BINARY, "R_" + r + "_vDU" + i + "_L2");
                }
            }
        }
        for (int r = 0; r < nRequests; r++) {
            for (int i = 0; i < n_vDUs; i++) {
                if (i < instance.requests[r].vDUs.size()) {
                    b_req_vDUs_L3[r][i] = model.addVar(0., 1., 0., GRB.BINARY, "R_" + r + "_vDU" + i + "_L3");
                } else {
                    b_req_vDUs_L3[r][i] = model.addVar(0., 0., 0., GRB.BINARY, "R_" + r + "_vDU" + i + "_L3");
                }
            }
        }
        //rutas por asignar
        for (int r = 0; r < nRequests; r++) {
            for (int p = 0; p < nPaths; p++) {
                b_req_path[r][p] = model.addVar(0., 1., 0., GRB.BINARY, "R_" + r + "_P_" + p);
            }
        }
        //asignaciones de vDUs a Rutas
        for (int r = 0; r < nRequests; r++) {
            for (int i = 0; i < n_vDUs; i++) {
                for (int j = 0; j < n_vCUs; j++) {
                    for (int p = 0; p < nPaths; p++) {
                        if (i < instance.requests[r].vDUs.size() && j < instance.requests[r].vCUs.size()) {
                            b_req_vDU_vCU_Path[r][i][j][p] = model.addVar(0., 1., 0., GRB.BINARY, "R_" + r + "_vDU_" + i + "_vCU_" + j + "_P_" + p);
                        } else {
                            b_req_vDU_vCU_Path[r][i][j][p] = model.addVar(0., 0., 0., GRB.BINARY, "R_" + r + "_vDU_" + i + "_vCU_" + j + "_P_" + p);
                        }
                    }
                }
            }
        }
        //asignaciones de vDUs a Rutas
        for (int r = 0; r < nRequests; r++) {
            for (int i = 0; i < n_vDUs; i++) {
                for (int p = 0; p < nPaths; p++) {
                    if (i < instance.requests[r].vDUs.size()) {
                        b_req_vDU_Path[r][i][p] = model.addVar(0., 1., 0., GRB.BINARY, "R_" + r + "_vDU_" + i + "_P_" + p);
                    } else {
                        b_req_vDU_Path[r][i][p] = model.addVar(0., 0., 0., GRB.BINARY, "R_" + r + "_vDU_" + i + "_P_" + p);
                    }
                }
            }
        }
        //rutas usadas
        for (int p = 0; p < nPaths; p++) {
            b_path[p] = model.addVar(0., 1., 0., GRB.BINARY, "Path_" + p);
        }
        //CUs asignados
        for (int i = 0; i < nCUs; i++) {
            b_CUs[i] = model.addVar(0., 1., 0., GRB.BINARY, "CU_" + i);
        }
    }

    public void initValues() throws GRBException {
        // enlaces entre vDUs y vCUs para cada peticion
        for (int r = 0; r < nRequests; r++) {
            for (int i = 0; i < n_vDUs; i++) {
                for (int j = 0; j < n_vCUs; j++) {
                    if (i < instance.requests[r].vDUs.size() && j < instance.requests[r].vCUs.size()) {
                        VirtualNode vDU = instance.requests[r].vDUs.get(i);
                        VirtualNode vCU = instance.requests[r].vCUs.get(j);
                        if (instance.requests[r].vLinks[vCU.nodePosition][vDU.nodePosition] != null) {
                            b_req_vDU_vCU[r][i][j] = model.addVar(1., 1., 0., GRB.BINARY, "R_" + r + "_vDU_" + i + "_vCU_" + j);
                        } else {
                            b_req_vDU_vCU[r][i][j] = model.addVar(0., 0., 0., GRB.BINARY, "R_" + r + "_vDU_" + i + "_vCU_" + j);
                        }
                    } else {
                        b_req_vDU_vCU[r][i][j] = model.addVar(0., 0., 0., GRB.BINARY, "R_" + r + "_vDU_" + i + "_vCU_" + j);
                    }
                }
            }
        }
        //asignaciones de rutas a nodos inciales y finales
        for (int p = 0; p < nPaths; p++) {
            for (int i = 0; i < nDUs; i++) {
                Node DU = paths.get(p).getNodesOfPath().get(0);
                if (DU.nodeType == 2) {
                    DU = paths.get(p).getNodesOfPath().get(paths.get(p).getNodesOfPath().size() - 1);
                }
                if (instance.DUs.get(i).nodePosition == DU.nodePosition) {
                    b_Path_DU[p][i] = model.addVar(1., 1., 0., GRB.BINARY, "P_" + p + "_DU_" + i);
                } else {
                    b_Path_DU[p][i] = model.addVar(0., 0., 0., GRB.BINARY, "P_" + p + "_DU_" + i);
                }
            }
        }
        for (int p = 0; p < nPaths; p++) {
            for (int j = 0; j < nCUs; j++) {
                Node CU = paths.get(p).getNodesOfPath().get(0);
                if (CU.nodeType == 1) {
                    CU = paths.get(p).getNodesOfPath().get(paths.get(p).getNodesOfPath().size() - 1);
                }
                if (instance.CUs.get(j).nodePosition == CU.nodePosition) {
                    b_Path_CU[p][j] = model.addVar(1., 1., 0., GRB.BINARY, "P_" + p + "_CU_" + j);
                } else {
                    b_Path_CU[p][j] = model.addVar(0., 0., 0., GRB.BINARY, "P_" + p + "_CU_" + j);
                }
            }
        }
        //costos/requisitos de ancho de banda de enlaces por cada ruta
        for (int p = 0; p < nPaths; p++) {
            for (int e = 0; e < nEdges; e++) {
                if (e < paths.get(p).getLinks().size()) {
                    b_path_edge[p][e] = model.addVar(0., 1., 0., GRB.BINARY, "Path_" + p + "_Link_" + e + "_bw");
                } else {
                    b_path_edge[p][e] = model.addVar(0., 0., 0., GRB.BINARY, "Path_" + p + "_Link_" + e + "_bw");
                }
            }
        }
    }

    public void initConstraints() throws GRBException {
        // PRC_CU
        for (int j = 0; j < nCUs; j++) {
            GRBLinExpr cu_prc_tot = new GRBLinExpr();//capacidad de procesamiento de las CUs
            for (int r = 0; r < nRequests; r++) {
                for (int i = 0; i < n_vCUs; i++) {
                    if (i < instance.requests[r].vCUs.size()) {
                        cu_prc_tot.addTerm(instance.requests[r].vCUs.get(i).prc, b_req_vCU_CU[r][i][j]);
                    }
                }
            }
            model.addConstr(cu_prc_tot, GRB.LESS_EQUAL, instance.CUs.get(j).prc, "R_PRC_CU_" + j);
        }
        // PRC_DU
        for (int j = 0; j < nDUs; ++j) {
            GRBLinExpr du_prc_tot = new GRBLinExpr();//capacidad de procesamiento de las DUs
            for (int r = 0; r < nRequests; ++r) {
                for (int i = 0; i < n_vDUs; i++) {
                    if (i < instance.requests[r].vDUs.size()) {
                        du_prc_tot.addTerm(instance.requests[r].vDUs.get(i).prc, b_req_vDU_DU[r][i][j]);
                    }
                }
            }
            model.addConstr(du_prc_tot, GRB.LESS_EQUAL, instance.DUs.get(j).prc, "R_PRC_DU_" + j);
        }
        // ANT_DU
        for (int j = 0; j < nDUs; ++j) {
            GRBLinExpr du_ant_tot = new GRBLinExpr();//cantidad de antenas disponibles por cada DU
            for (int r = 0; r < nRequests; ++r) {
                for (int i = 0; i < n_vDUs; i++) {
                    if (i < instance.requests[r].vDUs.size()) {
                        du_ant_tot.addTerm(instance.requests[r].vDUs.get(i).ant, b_req_vDU_DU[r][i][j]);
                    }
                }
            }
            model.addConstr(du_ant_tot, GRB.LESS_EQUAL, instance.DUs.get(j).ant, "R_ANT_DU_" + j);
        }
        // PRB_DU
        for (int j = 0; j < nDUs; ++j) {
            GRBLinExpr du_prb_tot = new GRBLinExpr();//cantidad de bloques de recursos fisicos por cada DU
            for (int r = 0; r < nRequests; ++r) {
                for (int i = 0; i < n_vDUs; i++) {
                    if (i < instance.requests[r].vDUs.size()) {
                        du_prb_tot.addTerm(instance.requests[r].vDUs.get(i).prb, b_req_vDU_DU[r][i][j]);
                    }
                }
            }
            model.addConstr(du_prb_tot, GRB.LESS_EQUAL, instance.DUs.get(j).prb, "R_PRB_DU_" + j);
        }
        // Dist_DU
        for (int r = 0; r < nRequests; ++r) {
            for (int i = 0; i < n_vDUs; i++) {
                if (i < instance.requests[r].vDUs.size()) {
                    for (int j = 0; j < nDUs; ++j) {
                        double d = ProblemInstance.distance(instance.requests[r].vDUs.get(i).location, instance.DUs.get(j).location);
                        GRBQuadExpr du_distance = new GRBQuadExpr();//radio de cobertura de cada DU
                        du_distance.addTerm(d, b_req_vDU_DU[r][i][j]);
                        model.addQConstr(du_distance, GRB.LESS_EQUAL, instance.DUs.get(j).theta, "R_" + r + "_vDU_" + i + "_DIST_DU_" + j);
                    }
                }
            }
        }
        // Assignaments vCU -> CU
        for (int r = 0; r < nRequests; r++) {
            for (int i = 0; i < instance.requests[r].vCUs.size(); i++) {//cada vCU se asigna a una CU
                GRBLinExpr asn_vcu_cu = new GRBLinExpr();
                for (int j = 0; j < nCUs; j++) {
                    asn_vcu_cu.addTerm(1., b_req_vCU_CU[r][i][j]);
                }
                model.addConstr(asn_vcu_cu, GRB.LESS_EQUAL, 1., "R_" + r + "_ASN_vCU" + i);
            }
        }
        // Assignaments vDU -> DU
        for (int r = 0; r < nRequests; r++) {
            for (int i = 0; i < instance.requests[r].vDUs.size(); i++) {//cada vDU se asigna a una DU
                GRBLinExpr asn_vdu_du = new GRBLinExpr();
                for (int j = 0; j < nDUs; j++) {
                    asn_vdu_du.addTerm(1., b_req_vDU_DU[r][i][j]);
                }
                model.addConstr(asn_vdu_du, GRB.LESS_EQUAL, 1., "R_" + r + "_ASN_vDU" + i);
            }
        }
        //validar la aceptacion/rechazo de las peticiones de acuerdo a las vCUs asignadas
        for (int r = 0; r < nRequests; r++) {
            GRBLinExpr acc_request = new GRBLinExpr();
            for (int i = 0; i < instance.requests[r].vCUs.size(); i++) {
                for (int j = 0; j < nCUs; j++) {
                    acc_request.addTerm(1., b_req_vCU_CU[r][i][j]);
                }
            }
            GRBLinExpr limit = new GRBLinExpr();
            limit.addTerm(instance.requests[r].vCUs.size(), b_req[r]);
            //model.addQConstr(acc_request, GRB.EQUAL, limit, "R_" + r + "_Accp_vCUs");
            model.addConstr(acc_request, GRB.EQUAL, limit, "R_" + r + "_Accp_vCUs");
        }
        //validar la aceptacion/rechazo de las peticiones de acuerdo a las vDUs asignadas
        for (int r = 0; r < nRequests; r++) {
            GRBLinExpr acc_request = new GRBLinExpr();
            for (int i = 0; i < instance.requests[r].vDUs.size(); i++) {
                for (int j = 0; j < nDUs; j++) {
                    //acc_request.addTerm(1., b_req[r], b_req_vDUs_DU[r][i][j]);
                    acc_request.addTerm(1., b_req_vDU_DU[r][i][j]);
                }
            }
            GRBLinExpr limit = new GRBLinExpr();
            limit.addTerm(instance.requests[r].vDUs.size(), b_req[r]);
            //model.addQConstr(acc_request, GRB.EQUAL, limit, "Accp_vDUs_" + r);
            model.addConstr(acc_request, GRB.EQUAL, limit, "Accp_vDUs_" + r);
        }
        // CU -> vCUs   -- Cada CU debe ser asignada a una única vCU (por petición)
        for (int r = 0; r < nRequests; r++) {
            for (int j = 0; j < nCUs; j++) {
                GRBQuadExpr diff_vcu_cu_per_req = new GRBQuadExpr();
                for (int i = 0; i < instance.requests[r].vCUs.size(); i++) {
                    diff_vcu_cu_per_req.addTerm(1., b_req[r], b_req_vCU_CU[r][i][j]);
                }
                model.addQConstr(diff_vcu_cu_per_req, GRB.LESS_EQUAL, 1., "CU_" + j + "_R_" + r);
            }
        }
        // DU -> vDUs   -- Cada DU debe ser asignada a una única vCU (por petición)
        for (int r = 0; r < nRequests; r++) {
            for (int j = 0; j < nDUs; j++) {
                GRBQuadExpr diff_vdu_du_per_req = new GRBQuadExpr();
                for (int i = 0; i < instance.requests[r].vDUs.size(); i++) {
                    diff_vdu_du_per_req.addTerm(1., b_req[r], b_req_vDU_DU[r][i][j]);
                }
                model.addQConstr(diff_vdu_du_per_req, GRB.LESS_EQUAL, 1., "DU_" + j + "_R_" + r);
            }
        }
        //validar la aceptacion/rechazo de las peticiones de acuerdo a las asignaciones de los splits
        for (int r = 0; r < nRequests; r++) {
            GRBQuadExpr acc_request = new GRBQuadExpr();
            for (int i = 0; i < instance.requests[r].vDUs.size(); i++) {
                for (int j = 0; j < nSplits; j++) {
                    acc_request.addTerm(1., b_req[r], b_req_vDU_Split[r][i][j]);
                }
            }
            GRBLinExpr limit = new GRBLinExpr();
            limit.addTerm(instance.requests[r].vDUs.size(), b_req[r]);
            model.addQConstr(acc_request, GRB.EQUAL, limit, "Asn_vDUs_" + r + "_Split");
        }
        //solo una asignacion por cada vDU x Split
        for (int r = 0; r < nRequests; r++) {
            for (int i = 0; i < n_vDUs; i++) {
                if (i < instance.requests[r].vDUs.size()) {
                    GRBQuadExpr asn_vdu_split = new GRBQuadExpr();
                    for (int s = 0; s < nSplits; s++) {
                        asn_vdu_split.addTerm(1., b_req[r], b_req_vDU_Split[r][i][s]);
                    }
                    model.addQConstr(asn_vdu_split, GRB.EQUAL, b_req[r], "R_" + r + "_vDU_" + i + "_Split");
                }/* else {
                    GRBLinExpr asn_vdu_split = new GRBLinExpr();
                    for (int s = 0; s < nSplits; s++) {
                        asn_vdu_split.addTerm(1., b_req_vDUs_Split[r][i][s]);
                    }
                    model.addConstr(asn_vdu_split, GRB.EQUAL, 0, "R_" + r + "_vDU_" + i + "_Split");
                }*/
            }
        }
        //restriccion de ancho de banda por asignacion de enlace vDU-vCU x Split_bw
        for (int r = 0; r < nRequests; r++) {
            for (int i = 0; i < n_vDUs; i++) {
                for (int j = 0; j < n_vCUs; j++) {
                    if (i < instance.requests[r].vDUs.size() && j < instance.requests[r].vCUs.size()) {
                        int idx = instance.requests[r].vCUs.get(j).nodePosition, idy = instance.requests[r].vDUs.get(i).nodePosition;
                        if (instance.requests[r].vLinks[idx][idy] != null) {
                            GRBLinExpr req_split_bw_vLink = new GRBLinExpr();
                            for (int s = 0; s < nSplits; s++) {
                                req_split_bw_vLink.addTerm(instance.splits[s].bw, b_req_vDU_Split[r][i][s]);
                            }
                            GRBLinExpr limit = new GRBLinExpr();
                            limit.addTerm(instance.requests[r].vLinks[idx][idy].bw, b_req_vDU_vCU[r][i][j]);
                            model.addConstr(req_split_bw_vLink, GRB.GREATER_EQUAL, limit, "R_" + r + "_vLink_" + i + "_" + j + "_split");
                        }
                    }/* else {
                        GRBLinExpr req_split_bw_vLink = new GRBLinExpr();
                        for (int s = 0; s < nSplits; s++) {
                            req_split_bw_vLink.addTerm(1., cost_req_Sbw[r][i][s]);
                        }
                        model.addConstr(req_split_bw_vLink, GRB.EQUAL, 0., "R_" + r + "_vLink_" + i + "_" + j + "_split");
                    }*/
                }
            }
        }
        //asignacion de grupo de acuerdo al split empleado
        for (int r = 0; r < nRequests; r++) {
            for (int i = 0; i < n_vDUs; i++) {
                if (i < instance.requests[r].vDUs.size()) {
                    GRBQuadExpr asn_l1_per_s = new GRBQuadExpr();
                    GRBQuadExpr asn_l2_per_s = new GRBQuadExpr();
                    GRBQuadExpr asn_l3_per_s = new GRBQuadExpr();
                    for (int s = 0; s < nSplits; s++) {
                        if (s == 0) {
                            asn_l3_per_s.addTerm(1., b_req[r], b_req_vDU_Split[r][i][s]);
                        } else if (s > 0 && s < 6) {
                            asn_l2_per_s.addTerm(1., b_req[r], b_req_vDU_Split[r][i][s]);
                        } else {
                            asn_l1_per_s.addTerm(1., b_req[r], b_req_vDU_Split[r][i][s]);
                        }
                    }
                    if (asn_l1_per_s.size() > 0) {
                        GRBLinExpr limit_l1 = new GRBLinExpr();
                        limit_l1.addTerm(1., b_req_vDUs_L1[r][i]);
                        model.addQConstr(asn_l1_per_s, GRB.EQUAL, limit_l1, "R_" + r + "_vDU_" + i + "_L1");
                    }
                    if (asn_l2_per_s.size() > 0) {
                        GRBLinExpr limit_l2 = new GRBLinExpr();
                        limit_l2.addTerm(1., b_req_vDUs_L2[r][i]);
                        model.addQConstr(asn_l2_per_s, GRB.EQUAL, limit_l2, "R_" + r + "_vDU_" + i + "_L2");
                    }
                    if (asn_l3_per_s.size() > 0) {
                        GRBLinExpr limit_l3 = new GRBLinExpr();
                        limit_l3.addTerm(1., b_req_vDUs_L3[r][i]);
                        model.addQConstr(asn_l3_per_s, GRB.EQUAL, limit_l3, "R_" + r + "_vDU_" + i + "_3");
                    }
                }/* else {
                    GRBLinExpr asn_l0_per_s = new GRBLinExpr();
                    for (int s = 0; s < nSplits; s++) {
                        asn_l0_per_s.addTerm(1., b_req_vDUs_Split[r][i][s]);
                    }
                    model.addConstr(asn_l0_per_s, GRB.EQUAL, 0., "R_" + r + "_vDU_" + i + "_L0");
                }*/
            }
        }
        //todas las vDUs aceptadas deben tener un grupo de split asignado
        for (int r = 0; r < nRequests; r++) {
            GRBLinExpr asn_s = new GRBLinExpr();
            for (int i = 0; i < instance.requests[r].vDUs.size(); i++) {
                asn_s.addTerm(1., b_req_vDUs_L1[r][i]);
                asn_s.addTerm(1., b_req_vDUs_L2[r][i]);
                asn_s.addTerm(1., b_req_vDUs_L3[r][i]);
            }
            GRBLinExpr limit = new GRBLinExpr();
            limit.addTerm(instance.requests[r].vDUs.size(), b_req[r]);
            model.addConstr(asn_s, GRB.EQUAL, limit, "R_" + r + "_fx");
        }
        //validar la aceptacion/rechazo de las peticiones de acuerdo a las asignaciones de rutas
        for (int r = 0; r < nRequests; r++) {
            for (int i = 0; i < n_vDUs; i++) {
                if (i < instance.requests[r].vDUs.size()) {
                    GRBQuadExpr acc_request = new GRBQuadExpr();
                    for (int p = 0; p < nPaths; p++) {
                        acc_request.addTerm(1., b_req_path[r][p], b_req_vDU_Path[r][i][p]);
                    }
                    model.addQConstr(acc_request, GRB.EQUAL, b_req[r], "Asn_R_" + r + "_vDU_" + i + "_Path");
                }/* else {
                    GRBLinExpr acc_request = new GRBLinExpr();
                    for (int p = 0; p < nPaths; p++) {
                        acc_request.addTerm(1., b_req_vDU_Path[r][i][p]);
                    }
                    model.addConstr(acc_request, GRB.EQUAL, 0., "Asn_R_" + r + "_vDU_" + i + "_Path");
                }*/
            }
        }
        //validar la aceptacion/rechazo de las peticiones de acuerdo a las asignaciones de rutas
        for (int r = 0; r < nRequests; r++) {
            GRBQuadExpr acc_request = new GRBQuadExpr();
            for (int i = 0; i < n_vDUs; i++) {
                for (int j = 0; j < n_vCUs; j++) {
                    for (int p = 0; p < nPaths; p++) {
                        acc_request.addTerm(1., b_req_path[r][p], b_req_vDU_vCU_Path[r][i][j][p]);
                    }
                }
            }
            GRBLinExpr limit = new GRBLinExpr();
            limit.addTerm(instance.requests[r].vDUs.size(), b_req[r]);
            model.addQConstr(acc_request, GRB.EQUAL, limit, "Asn_R_" + r + "_Path");
        }
        //asignacion de ruta para cada enlace vDU/vCU por cada peticion
        for (int r = 0; r < nRequests; r++) {
            for (int i = 0; i < n_vDUs; i++) {
                for (int j = 0; j < n_vCUs; j++) {
                    if (i < instance.requests[r].vDUs.size() && j < instance.requests[r].vCUs.size()) {
                        for (int p = 0; p < nPaths; p++) {
                            GRBQuadExpr ex = new GRBQuadExpr();
                            GRBQuadExpr limit = new GRBQuadExpr();
                            ex.addTerm(1., b_req_vDU_vCU[r][i][j], b_req_vDU_vCU_Path[r][i][j][p]);
                            limit.addTerm(1., b_req_vDU_Path[r][i][p], b_req[r]);
                            model.addQConstr(ex, GRB.EQUAL, limit, "R_" + r + "_vLink_" + i + "_" + j + "_Path_" + p);
                        }
                    }/* else {
                        for (int p = 0; p < nPaths; p++) {
                            GRBLinExpr ex = new GRBLinExpr();
                            ex.addTerm(1., b_req_vDUs_vCUs_Path[r][i][j][p]);
                            model.addConstr(ex, GRB.EQUAL, 0., "R_" + r + "_vLink_" + i + "_" + j + "_Path_" + p);
                        }
                    }*/
                }
            }
        }
        //validar ruta asignada con nodos asignados
        for (int r = 0; r < nRequests; r++) {
            for (int i = 0; i < n_vDUs; i++) {
                for (int j = 0; j < n_vCUs; j++) {
                    if (i < instance.requests[r].vDUs.size() && j < instance.requests[r].vCUs.size()) {
                        for (int p = 0; p < nPaths; p++) {
                            for (int x = 0; x < nDUs; x++) {
                                GRBQuadExpr limit = new GRBQuadExpr();
                                limit.addTerm(1., b_req_vDU_vCU[r][i][j], b_req_vDU_DU[r][i][x]);
                                GRBQuadExpr validate_DU = new GRBQuadExpr();
                                validate_DU.addTerm(1., b_req_vDU_Path[r][i][p], b_Path_DU[p][x]);
                                model.addQConstr(validate_DU, GRB.LESS_EQUAL, limit, "R_" + r + "_vLink_" + i + "_" + j + "_Path_" + p + "_DU_" + x);
                            }
                            for (int y = 0; y < nCUs; y++) {
                                GRBQuadExpr limit = new GRBQuadExpr();
                                limit.addTerm(1., b_req_vDU_vCU[r][i][j], b_req_vCU_CU[r][j][y]);
                                GRBQuadExpr validate_CU = new GRBQuadExpr();
                                validate_CU.addTerm(1., b_req_vDU_Path[r][i][p], b_Path_CU[p][y]);
                                model.addQConstr(validate_CU, GRB.LESS_EQUAL, limit, "R_" + r + "_vLink_" + i + "_" + j + "_Path_" + p + "_CU_" + y);
                            }
                        }
                    }/* else {
                        for (int p = 0; p < nPaths; p++) {
                            for (int x = 0; x < nDUs; x++) {
                                GRBLinExpr validate_DU = new GRBLinExpr();
                                validate_DU.addTerm(1., b_req_vDU_Path[r][i][p]);
                                model.addConstr(validate_DU, GRB.EQUAL, 0, "R_" + r + "_vLink_" + i + "_" + j + "_Path_" + p + "_DU_" + x);
                            }
                            for (int y = 0; y < nCUs; y++) {
                                GRBLinExpr validate_CU = new GRBLinExpr();
                                validate_CU.addTerm(1., b_req_vDU_Path[r][i][p]);
                                model.addConstr(validate_CU, GRB.EQUAL, 0, "R_" + r + "_vLink_" + i + "_" + j + "_Path_" + p + "_CU_" + y);
                            }
                        }
                    }*/
                }
            }
        }
        //restriccion de ancho de banda asignado (del split seleccionado) por cada enlace de ruta asignada
        for (int p = 0; p < nPaths; p++) {
            for (int e = 0; e < nEdges; e++) {
                if (e < paths.get(p).getLinks().size()) {
                    GRBQuadExpr sum_bw_asn = new GRBQuadExpr();
                    for (int r = 0; r < nRequests; r++) {
                        for (int i = 0; i < n_vDUs; i++) {
                            for (int s = 0; s < nSplits; s++) {
                                sum_bw_asn.addTerm(instance.splits[s].bw, b_req_vDU_Path[r][i][p], b_req_vDU_Split[r][i][s]);
                            }
                        }
                    }
                    GRBLinExpr limit = new GRBLinExpr();
                    limit.addTerm(paths.get(p).getLinks().get(e).bw, b_path_edge[p][e]);
                    model.addQConstr(sum_bw_asn, GRB.LESS_EQUAL, limit, "Path_" + p + "_Link_" + e + "_bw");
                } else {
                    model.addConstr(b_path_edge[p][e], GRB.EQUAL, 0., "Path_" + p + "_Link_" + e + "_bw");
                }
            }
        }
        //restriccion de delay de la ruta seleccionada y el split seleccionado
        for (int p = 0; p < nPaths; p++) {
            for (int r = 0; r < nRequests; r++) {
                for (int i = 0; i < n_vDUs; i++) {
                    if (i < instance.requests[r].vDUs.size()) {
                        GRBLinExpr delay_per_split = new GRBLinExpr();
                        GRBQuadExpr limit_path_delay = new GRBQuadExpr();
                        for (int s = 0; s < nSplits; s++) {
                            delay_per_split.addTerm(instance.splits[s].delay, b_req_vDU_Split[r][i][s]);
                        }
                        limit_path_delay.addTerm(paths.get(p).getDelay(), b_req_vDU_Path[r][i][p], b_path[p]);
                        model.addQConstr(delay_per_split, GRB.GREATER_EQUAL, limit_path_delay, "Path_" + p + "_R_" + r + "_vDU_" + i + "_delay");
                    }/* else {
                        GRBLinExpr delay_per_split = new GRBLinExpr();
                        for (int s = 0; s < nSplits; s++) {
                            delay_per_split.addTerm(1., cost_req_Sdl[r][i][s]);
                        }
                        model.addConstr(delay_per_split, GRB.EQUAL, 0., "Path_" + p + "_R_" + r + "_vDU_" + i + "_delay");
                    }*/
                }
            }
        }
        //restriccion de delay de la ruta seleccionada y enlace virtual
        for (int p = 0; p < nPaths; p++) {
            for (int r = 0; r < nRequests; r++) {
                for (int i = 0; i < n_vDUs; i++) {
                    for (int j = 0; j < n_vCUs; j++) {
                        if (i < instance.requests[r].vDUs.size() && j < instance.requests[r].vCUs.size()) {
                            int idx = instance.requests[r].vCUs.get(j).nodePosition, idy = instance.requests[r].vDUs.get(i).nodePosition;
                            if (instance.requests[r].vLinks[idx][idy] != null) {
                                GRBLinExpr delay_per_vLink = new GRBLinExpr();
                                GRBQuadExpr path_delay = new GRBQuadExpr();
                                delay_per_vLink.addTerm(instance.requests[r].vLinks[idx][idy].maxDelay, b_req_vDU_vCU[r][i][j]);
                                path_delay.addTerm(paths.get(p).getDelay(), b_req_vDU_Path[r][i][p], b_path[p]);
                                model.addQConstr(delay_per_vLink, GRB.GREATER_EQUAL, path_delay, "Path_" + p + "_R_" + r + "_vDU_" + i + "_vCU_" + j + "_delay");
                            }
                        }/* else {
                            GRBLinExpr delay_per_vLink = new GRBLinExpr();
                            delay_per_vLink.addTerm(1., cost_req_vDUs_vCUs_dl[r][i][j]);
                            model.addConstr(delay_per_vLink, GRB.GREATER_EQUAL, 0., "Path_" + p + "_R_" + r + "_vDU_" + i + "_vCU_" + j + "_delay");
                        }*/
                    }
                }
            }
        }
        //CUs seleccionados
        for (int j = 0; j < nCUs; j++) {
            GRBQuadExpr cu_asn = new GRBQuadExpr();
            for (int r = 0; r < nRequests; r++) {
                for (int i = 0; i < n_vCUs; i++) {//cada vCU se asigna a una CU
                    cu_asn.addTerm(1., b_CUs[j], b_req_vCU_CU[r][i][j]);
                }
            }
            model.addQConstr(cu_asn, GRB.GREATER_EQUAL, 0., "CU_" + j + "_ASN");
        }
        //rutas seleccionadas
        for (int p = 0; p < nPaths; p++) {
            GRBQuadExpr ex = new GRBQuadExpr();
            for (int r = 0; r < nRequests; r++) {
                ex.addTerm(1., b_path[p], b_req_path[r][p]);
            }
            model.addQConstr(ex, GRB.GREATER_EQUAL, 0., "Path_" + p + "_ASN");
        }
        // Validez de las soluciones
        GRBLinExpr valid = new GRBLinExpr();
        for (int r = 0; r < nRequests; r++) {
            valid.addTerm(1., b_req[r]);
        }
        model.addConstr(valid, GRB.GREATER_EQUAL, 1., "SOLUTION_FEASIBILITY");
    }

    public void execModel(String modelName, boolean showResults, boolean showResume, String resultFile) throws GRBException {
        model.optimize();
        elapsedTime = ((double) (System.nanoTime() - start)) / 1000000.;
        solverTotalTime = elapsedTime;
        //show results
        best = initSolution();
        if (Fx == null) {
            Fx = new ObjectiveFunction(instance, 0.7, 0.2, 0.1, true);
        }
        boolean aux = Tools.ECHO;
        if (model.get(GRB.IntAttr.SolCount) > 0) {//si existe una solucion imprimirla
            buildSolution();
            if (showResults) {
                model.write(modelName);//guardar modelo
                showResults(showResume);
                //compare results
            }
        } else if (showResults) {
            System.out.println("\nNo solutions found!!");
        }
        if (Tools.ECHO) {
            Tools.ECHO = false;
            runComparations();
            Tools.ECHO = aux;
        }
        //save results
        saveResults(resultFile);
    }

    public void clear() throws GRBException {
        //lista de objetos
        paths.clear();
        paths = null;
        //vector
        b_req = b_CUs = b_path = null;
        //matriz 2x2
        b_path_edge = b_Path_DU = b_Path_CU = b_req_vDUs_L1 = b_req_vDUs_L2
                = b_req_vDUs_L3 = b_req_path = null;
        //matriz 3x3
        b_req_vCU_CU = b_req_vDU_DU = b_req_vDU_Split = b_req_vDU_vCU
                = b_req_vDU_Path = null;
        //matriz 4x4
        b_req_vDU_vCU_Path = null;
        // Dispose of model and environment
        model.dispose();
        env.dispose();
    }

    private void showResults(boolean showResume) throws GRBException {
        int[] DUsOn = null;
        int[] CUsOn = null;
        Node src, end, tmp;
        int nAccepted = best.nAccepted, digits, split, idx_path, last;
        double[] x = model.get(GRB.DoubleAttr.X, b_req);
        double[][][] S_cu = model.get(GRB.DoubleAttr.X, b_req_vCU_CU);
        double[][][] S_du = model.get(GRB.DoubleAttr.X, b_req_vDU_DU);
        double[][][] s_split = model.get(GRB.DoubleAttr.X, b_req_vDU_Split);
        double[][][] vdu_path = model.get(GRB.DoubleAttr.X, b_req_vDU_Path);
        if (showResume) {
            DUsOn = new int[nDUs];
            CUsOn = new int[nCUs];
            Arrays.fill(DUsOn, 0);
            Arrays.fill(CUsOn, 0);
        }
        //show assignaments for vCUs
        digits = String.valueOf(instance.maxVirtualCUs > instance.CUs.size() ? instance.maxVirtualCUs : instance.CUs.size()).length();
        for (int r = 0; r < nRequests; r++) {
            System.out.println("R_" + (r + 1) + " [vCUs x CUs] " + (x[r] > 0.5 ? "(Accepted)" : "(Rejected)"));
            System.out.print(String.format("%" + (digits + 1) + "s", "\\"));
            for (int i = 0; i < nCUs; i++) {
                System.out.print(String.format("%3s", String.valueOf(instance.CUs.get(i).nodePosition)));
            }
            System.out.println();
            for (int i = 0; i < instance.requests[r].vCUs.size(); i++) {
                System.out.print(String.format("%" + (digits + 1) + "s", (i + 1) + "|"));
                for (int j = 0; j < nCUs; j++) {
                    if (S_cu[r][i][j] > 0.5) {
                        System.out.print(" On");
                        if (showResume) {
                            CUsOn[j] = 1;
                        }
                    } else {
                        System.out.print(" --");
                    }
                }
                System.out.println();
            }
        }
        System.out.println();
        //show assignaments for vDUs
        digits = String.valueOf(instance.maxVirtualDUs > instance.DUs.size() ? instance.maxVirtualDUs : instance.DUs.size()).length();
        for (int r = 0; r < nRequests; r++) {
            System.out.println(" R_" + (r + 1) + " [vDUs x DUs] " + (x[r] > 0.5 ? "(Accepted)" : "(Rejected)"));
            System.out.print(String.format("%" + (digits + 1) + "s", "\\"));
            for (int i = 0; i < nDUs; i++) {
                System.out.print(String.format("%3s", String.valueOf(instance.DUs.get(i).nodePosition)));
            }
            System.out.print(" | F Path");
            System.out.println();
            for (int i = 0; i < instance.requests[r].vDUs.size(); i++) {
                System.out.print(String.format("%" + (digits + 1) + "s", (i + 1) + "|"));
                for (int j = 0; j < nDUs; j++) {
                    if (S_du[r][i][j] > 0.5) {
                        System.out.print(" On");
                        if (showResume) {
                            DUsOn[j] = 1;
                        }
                    } else {
                        System.out.print(" --");
                    }
                }
                split = -1;
                for (int j = 0; j < nSplits; j++) {
                    if (s_split[r][i][j] > 0.5) {
                        if (split == -1) {
                            split = j + 1;
                            break;
                        }
                    }
                }
                idx_path = -1;
                for (int j = 0; j < nPaths; j++) {
                    if (vdu_path[r][i][j] > 0.5) {
                        if (idx_path == -1) {
                            idx_path = j;
                            break;
                        }
                    }
                }
                if (idx_path != -1 && split != -1) {
                    last = paths.get(idx_path).getNodesOfPath().size() - 1;
                    src = paths.get(idx_path).getNodesOfPath().get(0);
                    end = paths.get(idx_path).getNodesOfPath().get(last);
                    if (src.nodeType == 2) {
                        tmp = src;
                        src = end;
                        end = tmp;
                    }
                    if (instance.isFullyRepresentated()) {
                        idx_path = instance.mPaths[src.nodePosition][end.nodePosition].indexOf(paths.get(idx_path));
                    }
                    System.out.println(" | " + split + " [" + instance.DUs.indexOf(src) + "," + instance.CUs.indexOf(end) + "] " + idx_path);
                } else {
                    System.out.println(" | - [-,-] -");
                }
            }
        }
        System.out.println();
        if (showResume && DUsOn != null && CUsOn != null) {
            int asn_CUs = 0, DUs = 0;
            for (int i : DUsOn) {
                if (i == 1) {
                    DUs++;
                }
            }
            for (int i : CUsOn) {
                if (i == 1) {
                    asn_CUs++;
                }
            }
            System.out.println("\nRequest Accepted: " + nAccepted + " Total: " + nRequests);
            System.out.println("DUs On: " + DUs + " Total DUs: " + nDUs);
            for (int i = 0; i < nDUs; i++) {
                if (DUsOn[i] == 1) {
                    System.out.print(" [" + (instance.DUs.get(i).nodePosition) + "]");
                } else {
                    System.out.print(" -");
                }
            }
            System.out.println("\nCUs On: " + asn_CUs + " Total CUs: " + nCUs);
            for (int i = 0; i < nCUs; i++) {
                if (CUsOn[i] == 1) {
                    System.out.print(" [" + (instance.CUs.get(i).nodePosition) + "]");
                } else {
                    System.out.print(" -");
                }
            }
            System.out.println();
        }
        System.out.println("Result GUROBI");
        System.out.println(best.toString());
        System.out.println("Elapsed Time: " + solverTotalTime + " ms");
    }

    private void saveResults(String resultFile) {
        String sol;
        File solFile = new File(resultFile + ".out");
        File timeFile = new File(resultFile + "_time.out");
        String time = String.format("[%s][%s][%s] Seed: %d #Iterations: %d Elapsed Time: %f ms EFO: %d\n", runner.graphFile, runner.requestFolder, runner.pathsFile, Tools.getInstance().getSeed(), 1, solverTotalTime, 1);
        try {
            Writer bw = new FileWriter(solFile, true);
            Writer bt = new FileWriter(timeFile, true);
            if (model.get(GRB.IntAttr.SolCount) > 0) {
                sol = String.format("[%s][%s][%s] %s\n", runner.graphFile, runner.requestFolder, runner.pathsFile, best.toString());
            } else {
                sol = String.format("[%s][%s][%s] No valid solution\n", runner.graphFile, runner.requestFolder, runner.pathsFile);
            }
            bw.write(sol);
            bw.close();
            bt.write(time);
            bt.close();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }

    private void runComparations() {
        Tools.getInstance().loadBoundaries(instance);
        runGenetic();
        runGreedy();
    }

    private void runGreedy() {
        start = System.nanoTime();
        GreedyAlgorithm alg;
        if (instance.isFullyRepresentated()) {
            VirtualNodeComparator virtualNodeComparator = new VirtualNodeComparator();
            NodeFittestResourcesComparator nodeComparator = new NodeFittestResourcesComparator();
            alg = new MultiChoicesGreedyAlgorithm(new RequestComparator(), virtualNodeComparator, virtualNodeComparator, nodeComparator, new NodeDistanceComparator(), new PathComparator(), instance, Fx);
        } else {
            alg = new PathChoicesGreedyAlgorithm(new PathComparator(), new VirtualLinkComparator(), new RequestComparator(), instance, Fx);
        }
        MatrixSolution greedySolution = alg.run();
        elapsedTime = ((double) (System.nanoTime() - start + loadTime)) / 1000000.;
        System.out.println("Result Greedy");
        if (greedySolution != null) {
            System.out.println(greedySolution.toString());
        } else {
            System.out.println("No Solution found");
        }
        System.out.println("Elapsed Time: " + elapsedTime + " ms");
    }

    private void runGenetic() {
        Random rand = Tools.getInstance().getRandom();
        start = System.nanoTime();
        int popSize = 100;
        int numIterations = 300;
        Tools.cross = 0.9;
        Tools.mut = 3;
        Tools.rank = 0.475;
        if (nEdges > 100 && nEdges <= 1000) {
            Tools.mut = 2;
        } else {
            Tools.rank = 0.425;
        }
        Selection<MatrixSolution> selection = new StochasticRanking(Fx.isMaximization(), rand, Tools.rank);
        Mutation<MatrixSolution> mutation = new MultiBit(rand, instance);
        Initialization<MatrixSolution> init = (instance.isFullyRepresentated() ? new GreedyFull(instance, popSize, rand) : new GreedyPaths(instance, popSize, rand));
        GeneticAlgorithm alg = new GeneticAlgorithm(instance, Fx, mutation, new Uniform(rand, Tools.cross), selection, init, null, rand, numIterations, popSize);
        MatrixSolution geneticSolution = alg.run();
        elapsedTime = ((double) (System.nanoTime() - start + loadTime)) / 1000000.;
        System.out.println("Result Genetic");
        System.out.println(geneticSolution.toString());
        System.out.println("Elapsed Time: " + elapsedTime + " ms");
    }

    private MatrixSolution initSolution() {
        int pos;
        MatrixSolution solution;
        if (instance.isFullyRepresentated()) {
            solution = new MatrixSolution(instance.requests.length, instance.step * instance.maxVirtualDUs + instance.maxVirtualCUs);
            solution.accepted = new boolean[instance.requests.length];
            for (int k = 0; k < instance.requests.length; k++) {//para cada peticion, asignar el orden inicial y las posiciones de la matriz
                pos = 0;//cuenta las posiciones de la columna para asignarla al grafo
                for (VirtualNode vCU : instance.requests[k].vCUs) {//para cada nodo vCU, asignar la posicion a la que le corresponde en la matriz
                    vCU.indx = pos;//asignar la posicion de la columna al nodo vCU
                    pos++;//la posicion de la DU sera la inmediatamente siguiente
                    for (Integer vdu : vCU.nears) {//obtener los nodos DU a los que se conecta (cada CU se conecta por un enlace)
                        instance.requests[k].vNodes[vdu].indx = pos;//asignar posiciones relativas al nodo vCU
                        pos += instance.step;//la siguiente posicion dependera del tipo de representacion
                    }
                }
                for (int i = pos; i < solution.getM(); i++) {
                    solution.data[k][i] = -1;
                }
            }
        } else {
            solution = new MatrixSolution(instance.requests.length, instance.step * instance.maxVirtualDUs);
            solution.accepted = new boolean[instance.requests.length];
            for (int k = 0; k < instance.requests.length; k++) {//para cada peticion, asignar el orden inicial y las posiciones de la matriz
                pos = 0;//cuenta las posiciones de la columna para asignarla al grafo
                for (VirtualNode vDU : instance.requests[k].vDUs) {//obtener los nodos DU a los que se conecta (cada CU se conecta por un enlace)
                    for (Integer vcu : vDU.nears) {
                        instance.requests[k].vNodes[vcu].indx = pos;
                    }
                    vDU.indx = pos;//asignar posiciones relativas al nodo vCU
                    pos += instance.step;//la siguiente posicion dependera del tipo de representacion
                }
                for (int i = pos; i < solution.getM(); i++) {
                    solution.data[k][i] = -1;
                }
            }
        }
        return solution;
    }

    private void buildSolution() throws GRBException {
        int nAccepted = 0, idx_path, last;
        Node src, end, tmp;
        double[] x = model.get(GRB.DoubleAttr.X, b_req);
        double[][][] S_cu = model.get(GRB.DoubleAttr.X, b_req_vCU_CU);
        double[][][] S_du = model.get(GRB.DoubleAttr.X, b_req_vDU_DU);
        double[][][] s_split = model.get(GRB.DoubleAttr.X, b_req_vDU_Split);
        double[][][] vdu_path = model.get(GRB.DoubleAttr.X, b_req_vDU_Path);
        for (int r = 0; r < nRequests; r++) {
            best.accepted[r] = false;
            if (x[r] > 0.5) {
                best.accepted[r] = true;
                nAccepted++;
            }
            for (int i = 0; i < instance.requests[r].vCUs.size(); i++) {
                VirtualNode vCU = instance.requests[r].vCUs.get(i);
                for (int j = 0; j < nCUs; j++) {
                    if (S_cu[r][i][j] > 0.5) {
                        best.data[r][vCU.indx] = vCU.indxNode = instance.CUs.get(j).nodePosition;
                    }
                }
            }
            for (int i = 0; i < instance.requests[r].vDUs.size(); i++) {
                VirtualNode vDU = instance.requests[r].vDUs.get(i);
                for (int j = 0; j < nDUs; j++) {
                    if (S_du[r][i][j] > 0.5) {
                        best.data[r][vDU.indx] = vDU.indxNode = instance.DUs.get(j).nodePosition;
                    }
                }
                for (int j = 0; j < nSplits; j++) {
                    if (s_split[r][i][j] > 0.5) {
                        best.data[r][vDU.indx + instance.splitPosition] = j;
                        break;
                    }
                }
                idx_path = -1;
                for (int j = 0; j < nPaths; j++) {
                    if (vdu_path[r][i][j] > 0.5) {
                        if (idx_path == -1) {
                            idx_path = j;
                            break;
                        }
                    }
                }
                if (idx_path != -1) {
                    last = paths.get(idx_path).getNodesOfPath().size() - 1;
                    src = paths.get(idx_path).getNodesOfPath().get(0);
                    end = paths.get(idx_path).getNodesOfPath().get(last);
                    if (src.nodeType == 2) {
                        tmp = src;
                        src = end;
                        end = tmp;
                    }
                    if (instance.isFullyRepresentated()) {
                        idx_path = instance.mPaths[src.nodePosition][end.nodePosition].indexOf(paths.get(idx_path));
                    }
                    best.data[r][vDU.indx + instance.pathPosition] = idx_path;
                }
            }
        }
        best.nAccepted = nAccepted;
        best.gn = instance.validate(best);
        Fx.evaluate(best);
    }

    public void initObjectiveFunction() throws GRBException {
        GRBQuadExpr objectiveFunction = new GRBQuadExpr();
        GRBQuadExpr UoL = new GRBQuadExpr();
        GRBQuadExpr LoC = new GRBQuadExpr();
        double m = 0;
        GRBQuadExpr Ls = new GRBQuadExpr();
        for (int r = 0; r < nRequests; r++) {
            for (int i = 0; i < instance.requests[r].vDUs.size(); i++) {
                Ls.addTerm(0.7, b_req[r], b_req_vDUs_L1[r][i]);
                Ls.addTerm(0.2, b_req[r], b_req_vDUs_L2[r][i]);
                Ls.addTerm(0.1, b_req[r], b_req_vDUs_L3[r][i]);
            }
            m += instance.requests[r].vDUs.size();
        }
        LoC.multAdd(1. / m, Ls);
        GRBQuadExpr Bw = new GRBQuadExpr();
        for (int p = 0; p < nPaths; p++) {
            for (int e = 0; e < paths.get(p).getLinks().size(); e++) {
                Link l = paths.get(p).getLinks().get(e);
                for (int r = 0; r < nRequests; r++) {
                    for (int i = 0; i < instance.requests[r].vDUs.size(); i++) {
                        for (int s = 0; s < nSplits; s++) {
                            Bw.addTerm(l.linkCost * instance.splits[s].bw / l.bw, b_req_vDU_Path[r][i][p], b_req_vDU_Split[r][i][s]);
                        }
                    }
                }
            }
        }
        UoL.multAdd(-1. / (double) nEdges, Bw);
        GRBLinExpr BBUs = new GRBLinExpr();
        for (int j = 0; j < nCUs; j++) {
            BBUs.addTerm(-1., b_CUs[j]);
        }
//        GRBLinExpr numAcc = new GRBLinExpr();
//        for (int r = 0; r < nRequests; r++) {
//            numAcc.addTerm(1., b_req[r]);
//        }
        //agregar terminos de la funcion objetivo
        objectiveFunction.add(LoC);
        objectiveFunction.add(UoL);
        //objectiveFunction.add(numAcc);
        //objectiveFunction.add(BBUs);
        model.setObjective(objectiveFunction, GRB.MAXIMIZE);
    }
}
