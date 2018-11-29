/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.cluster;

import Standard.CombinedEvaluation;
import ai.RandomBiasedAI;
import ai.abstraction.Attack;
import ai.abstraction.pathfinding.AStarPathFinding;
import ai.abstraction.pathfinding.PathFinding;
import ai.cluster.core.hdbscanstar.HDBSCANStarObject;
import ai.core.AI;
import ai.core.AIWithComputationBudget;
import ai.core.InterruptibleAI;
import ai.core.ParameterSpecification;
import ai.evaluation.EvaluationFunction;
import static ai.evaluation.LanchesterEvaluationFunction.W_BASE;
import static ai.evaluation.LanchesterEvaluationFunction.W_HEAVY;
import static ai.evaluation.LanchesterEvaluationFunction.W_LIGHT;
import static ai.evaluation.LanchesterEvaluationFunction.W_MINERALS_CARRIED;
import static ai.evaluation.LanchesterEvaluationFunction.W_MINERALS_MINED;
import static ai.evaluation.LanchesterEvaluationFunction.W_RANGE;
import static ai.evaluation.LanchesterEvaluationFunction.W_RAX;
import static ai.evaluation.LanchesterEvaluationFunction.W_WORKER;
import ai.evaluation.SimpleSqrtEvaluationFunction3;
import ai.mcts.naivemcts.NaiveMCTS;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import rts.GameState;
import rts.PhysicalGameState;
import rts.PlayerAction;
import rts.ResourceUsage;
import rts.UnitAction;
import rts.UnitActionAssignment;
import rts.units.Unit;
import rts.units.UnitType;
import rts.units.UnitTypeTable;
import util.Pair;

/**
 * Cluster Independent Action (CIA) Use HDBScan* to choose the clusters and
 * apply in each cluster NaiveMCTS If one cluster don't have enemy reference, it
 * will search for the unit more closest to centroid of cluster.
 *
 * @author rubens
 */
public class CIA_EnemyWithTime extends AIWithComputationBudget implements InterruptibleAI {

    EvaluationFunction evaluation = null;
    UnitTypeTable utt;
    PathFinding pf;
    GameState gs_to_start_from = null;
    private int playerForThisComputation;
    ArrayList<ArrayList<Unit>> clusters;
    //AlphaBetaSearch IA1;
    NaiveMCTS IA1;
    HashMap<ArrayList<Unit>, Integer> timePerCluster;

    public CIA_EnemyWithTime(UnitTypeTable utt) {
        this(100, 200, new SimpleSqrtEvaluationFunction3(),
                utt,
                new AStarPathFinding());
    }

    public CIA_EnemyWithTime(int time, int max_playouts, EvaluationFunction e, UnitTypeTable a_utt, PathFinding a_pf) {
        super(time, max_playouts);
        evaluation = e;
        utt = a_utt;
        pf = a_pf;
        clusters = new ArrayList<>();
        //IA1 = new AlphaBetaSearch(utt);
        IA1 = new NaiveMCTS(100, -1, 100, 10, 0.3f, 0.0f, 0.4f, new RandomBiasedAI(), new CombinedEvaluation(), true);
        
        this.timePerCluster = new HashMap<>();
    }

    @Override
    public void reset() {
        clusters.clear();
    }

    @Override
    public PlayerAction getAction(int player, GameState gs) throws Exception {
        if (gs.canExecuteAnyAction(player)) {
            startNewComputation(player, gs);
            computeDuringOneGameFrame();
            return getBestActionSoFar();
        } else {
            if ((gs.getNextChangeTime() - 1) == gs.getTime()) {
                //System.out.println("Next action " + gs.getNextChangeTime() + " actual time=" + gs.getTime());
                startNewComputation(player, gs);
                //computeDuringOneGameFrame();
            }
            return new PlayerAction();
        }
    }

    @Override
    public AI clone() {
        return new CIA_EnemyWithTime(TIME_BUDGET, ITERATIONS_BUDGET, evaluation, utt, pf);
    }

    @Override
    public List<ParameterSpecification> getParameters() {
        List<ParameterSpecification> parameters = new ArrayList<>();

        parameters.add(new ParameterSpecification("TimeBudget", int.class, 100));
        parameters.add(new ParameterSpecification("IterationsBudget", int.class, -1));
        parameters.add(new ParameterSpecification("PlayoutLookahead", int.class, 200));
        parameters.add(new ParameterSpecification("EvaluationFunction", EvaluationFunction.class, new SimpleSqrtEvaluationFunction3()));
        parameters.add(new ParameterSpecification("PathFinding", PathFinding.class, new AStarPathFinding()));

        return parameters;
    }

    @Override
    public void startNewComputation(int player, GameState gs) throws Exception {
        playerForThisComputation = player;
        gs_to_start_from = gs;
    }

    @Override
    public void computeDuringOneGameFrame() throws Exception {
        findBestClusters();
        filterClusters();
        removeEnemyClusters();
        //groupClustersWithBasesAndBarracks();
        //System.out.println("Total Cluster:" + this.clusters.size());
    }

    @Override
    public PlayerAction getBestActionSoFar() throws Exception {
        long start = System.currentTimeMillis();
        if (clusters.size() == 1) {
            //NaiveMCTS ns = new NaiveMCTS(100, -1, 100, 10, 0.3f, 0.0f, 0.4f, new RandomBiasedAI(), new CombinedEvaluation(), true);
            IA1.setTimeBudget(100);
            return IA1.getAction(playerForThisComputation, gs_to_start_from);
        }
        //analise clusters and set a time for each one
        calcTimePerCluster();
        //build temporary states
        HashMap<GameState, Integer> timePerState = new HashMap<>();
        ArrayList<GameState> states = new ArrayList<>();
        for (ArrayList<Unit> cluster : clusters) {
            GameState tempG = buildNewState(cluster, gs_to_start_from);
            states.add(tempG);
            timePerState.put(tempG, this.timePerCluster.get(cluster));
        }

        //calculate time for each state
        //int timeEach = TIME_BUDGET / clusters.size();
        //NaiveMCTS ns = new NaiveMCTS(timeEach, -1, 100, 10, 0.3f, 0.0f, 0.4f, new RandomBiasedAI(), new CombinedEvaluation(), true);

        //PlayerAction pateste = ns.getAction(playerForThisComputation, gs_to_start_from.clone());
        //System.out.println("actions="+ pateste.toString());
        //collect actions
        HashSet<PlayerAction> actions = new HashSet<>();
        for (GameState statePT : states) {
            int time = timePerState.get(statePT);
            IA1.setTimeBudget(timePerState.get(statePT));
            actions.add(IA1.getAction(playerForThisComputation, statePT));
        }

        //System.out.println("actions=" + paFull.toString());
        //Thread.sleep(3000);
        return joinActions(actions);

    }

    private GameState buildNewState(ArrayList<Unit> cluster, GameState rgs) {
        GameState rgsRet = rgs.clone();
        for (Unit un : rgs.getUnits()) {
            if (!cluster.contains(un) && un.getPlayer() >= 0) {
                Unit unRem = rgsRet.getUnit(un.getID());
                rgsRet.removeUnit(unRem);
            }
        }

        return rgsRet;
    }

    protected PlayerAction checkIntegrity(int player, PlayerAction pa) {
        List<Pair<Unit, UnitAction>> remActions = new ArrayList<>();

        for (Pair<Unit, UnitAction> tmp : pa.getActions()) {
            if (tmp.m_a.getPlayer() != player) {
                remActions.add(tmp);
            }
        }
        for (Pair<Unit, UnitAction> remAction : remActions) {
            pa.removeUnitAction(remAction.m_a, remAction.m_b);
        }

        return pa;
    }

    private ArrayList<Unit> getUnits(int player) {
        ArrayList<Unit> unitsPlayer = new ArrayList<>();
        for (Unit u : gs_to_start_from.getUnits()) {
            if (u.getPlayer() == player) {
                unitsPlayer.add(u);
            }
        }
        return unitsPlayer;
    }

    @Override
    public String toString() {
        return "CIA_EnemyWithTime";
    }

    private void findBestClusters() {
        //[total units] [2] (x,y)
        ArrayList<Unit> unitsCl = getUnits(playerForThisComputation);
        unitsCl.addAll(getUnits(1 - playerForThisComputation));
        double[][] dataSet = new double[unitsCl.size()][2];
        int idx = 0;
        for (Unit unit : unitsCl) {
            double[] tempPosition = new double[2];
            tempPosition[0] = unit.getX();
            tempPosition[1] = unit.getY();
            //System.out.println(unit.getX()+","+unit.getY());
            dataSet[idx] = tempPosition;
            idx++;
        }

        try {
            int[] clusterInt = HDBSCANStarObject.runHDBSCAN(dataSet, 2, 2, true);
            //System.out.println(Arrays.toString(clusterInt));
            buildClusters(dataSet, clusterInt, unitsCl);
        } catch (IOException ex) {
            Logger.getLogger(CIA_EnemyWithTime.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private void buildClusters(double[][] dataSet, int[] clusterInt, ArrayList<Unit> unitsCl) {
        this.clusters.clear();
        HashSet<Integer> labels = new HashSet<>();
        for (int i = 0; i < clusterInt.length; i++) {
            labels.add(clusterInt[i]);
        }

        for (Integer label : labels) {
            ArrayList<Unit> cluster = new ArrayList<>();

            for (int i = 0; i < clusterInt.length; i++) {
                if (clusterInt[i] == label) {
                    double[] tPos = dataSet[i];
                    Unit untC = getUnitByPos(tPos, unitsCl);
                    cluster.add(untC);
                }
            }

            this.clusters.add(cluster);
        }
    }

    private Unit getUnitByPos(double[] tPos, ArrayList<Unit> unitsCl) {
        for (Unit unit : unitsCl) {
            if (unit.getX() == tPos[0] && unit.getY() == tPos[1]) {
                return unit;
            }
        }
        return null;
    }

    /**
     * Follow these steps: 1 - Join clusters where you don't have an enemy
     * present with a enemy cluster. 2 - Remove cluster that haven't our units.
     */
    private void filterClusters() {
        ArrayList<ArrayList<Unit>> newClusters = new ArrayList<>();
        for (ArrayList<Unit> cluster : clusters) {
            if (playerCluster(cluster, playerForThisComputation)) {
                //join with the enemy cluster more closest
                ArrayList<Unit> newCluster;
                newCluster = new ArrayList<>();
                newCluster.addAll(cluster);
                //get unit more closest 
                newCluster.add(getEnemyClosestByCentroid(cluster));

                newClusters.add(newCluster);
            } else {
                //keep this cluster
                newClusters.add(cluster);
            }
        }
        this.clusters = newClusters;
    }

    private boolean playerCluster(ArrayList<Unit> cluster, int playerEv) {
        for (Unit unit : cluster) {
            if (unit.getPlayer() != playerEv) {
                return false;
            }
        }
        return true;
    }

    /**
     * Retorna o cluster com distancia euclidiana mais próxima
     *
     * @param cluster
     * @return
     */
    private ArrayList<Unit> getEnemyClusterClosest(ArrayList<Unit> cluster) {
        Unit Enbase = getClosestEnemyUnit(cluster.get(0), gs_to_start_from, cluster.get(0).getPlayer());
        return getClusterWithUnit(Enbase);

    }

    private Unit getEnemyClosestByCentroid(ArrayList<Unit> cluster) {
        ArrayList<Unit> unidades = new ArrayList<>();
        for (Unit unit : cluster) {
            if (unit.getPlayer() == playerForThisComputation) {
                unidades.add(unit);
            }
        }
        //find the centroid
        int x = 0, y = 0;
        for (Unit un : unidades) {
            x += un.getX();
            y += un.getY();
        }
        x = x / unidades.size();
        y = y / unidades.size();
        return getEnemyClosest(x, y);
    }

    private Unit getEnemyClosest(int xCentroid, int yCentroid) {
        Unit Enbase = getClosestEnemyUnit(xCentroid, yCentroid, gs_to_start_from, playerForThisComputation);
        return Enbase;

    }

    private Unit getClosestEnemyUnit(int xCent, int yCent, GameState state, int player) {
        PhysicalGameState pgs = state.getPhysicalGameState();
        Unit closestEnemy = null;
        int closestDistance = 0;
        for (Unit u2 : pgs.getUnits()) {
            if (u2.getPlayer() >= 0 && u2.getPlayer() != player) {
                int d = Math.abs(u2.getX() - xCent) + Math.abs(u2.getY() - yCent);
                if (closestEnemy == null || d < closestDistance) {
                    closestEnemy = u2;
                    closestDistance = d;
                }
            }
        }
        return closestEnemy;
    }

    private Unit getClosestEnemyUnit(Unit allyUnit, GameState state, int player) {
        PhysicalGameState pgs = state.getPhysicalGameState();
        Unit closestEnemy = null;
        int closestDistance = 0;
        for (Unit u2 : pgs.getUnits()) {
            if (u2.getPlayer() >= 0 && u2.getPlayer() != player) {
                int d = Math.abs(u2.getX() - allyUnit.getX()) + Math.abs(u2.getY() - allyUnit.getY());
                if (closestEnemy == null || d < closestDistance) {
                    closestEnemy = u2;
                    closestDistance = d;
                }
            }
        }
        return closestEnemy;
    }

    private ArrayList<Unit> getClusterWithUnit(Unit Enbase) {
        for (ArrayList<Unit> cluster : clusters) {
            for (Unit unit : cluster) {
                if (unit.getID() == Enbase.getID()) {
                    return cluster;
                }
            }
        }

        return null;
    }

    /**
     * Remove clusters just with enemy units
     */
    private void removeEnemyClusters() {
        ArrayList<ArrayList<Unit>> remCluster = new ArrayList<>();
        for (ArrayList<Unit> cluster : clusters) {
            if (playerCluster(cluster, (1 - playerForThisComputation))) {
                remCluster.add(cluster);
            }
        }
        for (ArrayList<Unit> enC : remCluster) {
            this.clusters.remove(enC);
        }

    }

    private void groupClustersWithBasesAndBarracks() {
        ArrayList<ArrayList<Unit>> clusterJoin = new ArrayList<>();

        for (ArrayList<Unit> cluster : clusters) {
            if (existBaseBarrack(cluster, playerForThisComputation)) {
                clusterJoin.add(cluster);
            }
        }
        //remove from clusters' variable and make the new cluster
        ArrayList<Unit> newCluster = new ArrayList<>();
        for (ArrayList<Unit> rem : clusterJoin) {
            this.clusters.remove(rem);
            newCluster.addAll(rem);
        }

        //add new cluster in clusters' variable
        this.clusters.add(newCluster);
    }

    /**
     * Analisa se existe bases e barracas referentes ao playerForThisComputation
     * no cluster
     *
     * @param cluster
     * @param playerForThisComputation
     * @return
     */
    private boolean existBaseBarrack(ArrayList<Unit> cluster, int player) {
        UnitType baseType = utt.getUnitType("Base");
        UnitType barracksType = utt.getUnitType("Barracks");
        for (Unit unit : cluster) {

            if ((unit.getPlayer() == player) && (unit.getType() == baseType || unit.getType() == barracksType)) {
                return true;
            }

        }
        return false;
    }

    private PlayerAction joinActions(HashSet<PlayerAction> actions) {
        ResourceUsage base_ru = new ResourceUsage();
        GameState gs = gs_to_start_from;
        PhysicalGameState pgs = gs_to_start_from.getPhysicalGameState();
        //sum the base_ru used
        for (Unit u : pgs.getUnits()) {
            UnitActionAssignment uaa = gs.getUnitActions().get(u);
            if (uaa != null) {
                ResourceUsage ru = uaa.action.resourceUsage(u, pgs);
                base_ru.merge(ru);
            }
        }

        //join action
        PlayerAction paFull = new PlayerAction();
        for (PlayerAction action : actions) {
            //System.out.println("actions="+ actions.toString());
            for (Pair<Unit, UnitAction> ua : action.getActions()) {
                // check to see if the action is legal!
                ResourceUsage r = ua.m_b.resourceUsage(ua.m_a, pgs);
                boolean targetOccupied = false;
                for (int position : r.getPositionsUsed()) {
                    int y = position / pgs.getWidth();
                    int x = position % pgs.getWidth();
                    if (pgs.getTerrain(x, y) != PhysicalGameState.TERRAIN_NONE
                            || pgs.getUnitAt(x, y) != null) {
                        targetOccupied = true;
                        break;
                    }
                }
                if (!targetOccupied && r.consistentWith(paFull.getResourceUsage(), gs_to_start_from)) {
                    if (base_ru.consistentWith(r, gs)) {
                        paFull.addUnitAction(gs_to_start_from.getUnit(ua.m_a.getID()), ua.m_b);
                        paFull.getResourceUsage().merge(r);
                        base_ru.merge(r);
                    } else {
                        Unit t = gs_to_start_from.getUnit(ua.m_a.getID());
                        UnitAction unt = new Attack(t, getClosestEnemyUnit(t, gs, playerForThisComputation), pf).execute(gs, base_ru);
                        if (unt != null) {
                            paFull.addUnitAction(t, unt);
                            ResourceUsage r2 = unt.resourceUsage(t, pgs);
                            paFull.getResourceUsage().merge(r2);
                            base_ru.merge(r2);
                        }
                        //System.out.println("new attack move action");
                    }
                    //System.out.println("Frame: " + gs_to_start_from.getTime() + ", extra action: " + ua);

                } else {

                    //System.out.println("inconsistent"+ ua);
                }
            }
        }
        return paFull;
    }

    private void calcTimePerCluster() {
        HashMap< ArrayList<Unit>, Float> baseValue = new HashMap<>();
        Float total= 0.0f;
        
        for (ArrayList<Unit> cluster : clusters) {
            Float tValue = calcuValueCluster(cluster);
            baseValue.put(cluster, tValue);
            total += tValue;
        }
        timePerCluster.clear();
        for (ArrayList<Unit> cluster : clusters) {
            //Float tPerc = ((100.0f * baseValue.get(cluster))/total);
            int time = (int) ((100.0f * baseValue.get(cluster))/total);
            timePerCluster.put(cluster, time);
        }
    }

    /**
     * Values based in LanchesterEvaluationFunction
     * @param cluster
     * @return 
     */
    private Float calcuValueCluster(ArrayList<Unit> cluster) {
        float order = 1.7f;
        float score = 0.0f;
        float score_buildings = 0.0f;
        float nr_units = 0.0f;
        float res_carried = 0.0f;

        for (Unit u : cluster) {
            res_carried += u.getResources();
            //UNITS
            if (u.getType() == utt.getUnitType("Base")) {
                score_buildings += W_BASE[0] * u.getHitPoints();
            } else if (u.getType() == utt.getUnitType("Barracks")) {
                score_buildings += W_RAX[0] * u.getHitPoints();

            } else if (u.getType() == utt.getUnitType("Worker")) {
                nr_units += 1;
                score += W_WORKER[0] * u.getHitPoints();
            } else if (u.getType() == utt.getUnitType("Light")) {
                nr_units += 1;
                score += W_LIGHT[0] * u.getHitPoints() / (float) u.getMaxHitPoints();
            } else if (u.getType() == utt.getUnitType("Ranged")) {
                nr_units += 1;
                score += W_RANGE[0] * u.getHitPoints();
            } else if (u.getType() == utt.getUnitType("Heavy")) {
                nr_units += 1;
                score += W_HEAVY[0] * u.getHitPoints() / (float) u.getMaxHitPoints();
            }

        }

        score = (float) (score * Math.pow(nr_units, order - 1));

        score += score_buildings + res_carried * W_MINERALS_CARRIED[0] + 
        		gs_to_start_from.getPlayer(playerForThisComputation).getResources() * W_MINERALS_MINED[0];

        return score;
    }

}
