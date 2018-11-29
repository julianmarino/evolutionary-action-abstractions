/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.asymmetric.SSS;

import ai.abstraction.combat.Cluster;
import ai.abstraction.combat.KitterDPS;
import ai.abstraction.combat.NOKDPS;
import ai.abstraction.partialobservability.POHeavyRush;
import ai.abstraction.partialobservability.POLightRush;
import ai.abstraction.partialobservability.PORangedRush;
import ai.abstraction.partialobservability.POWorkerRush;
import ai.abstraction.pathfinding.AStarPathFinding;
import ai.abstraction.pathfinding.PathFinding;
import ai.asymmetric.common.UnitScriptData;
import ai.core.AI;
import ai.core.AIWithComputationBudget;
import ai.core.InterruptibleAI;
import ai.core.ParameterSpecification;
import ai.evaluation.EvaluationFunction;
import ai.evaluation.SimpleSqrtEvaluationFunction3;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import rts.GameState;
import rts.PlayerAction;
import rts.UnitAction;
import rts.units.Unit;
import rts.units.UnitTypeTable;

/**
 *
 * @author rubens
 */
public class NSSS extends AIWithComputationBudget implements InterruptibleAI {

    int LOOKAHEAD = 200;
    int I = 1;  // number of iterations for improving a given player
    int R = 0;  // number of times to improve with respect to the response fo the other player
    EvaluationFunction evaluation = null;
    List<AI> scripts = null;
    UnitTypeTable utt;
    PathFinding pf;
    int _startTime;

    AI defaultScript = null;

    long start_time = 0;
    int nplayouts = 0;

    GameState gs_to_start_from = null;
    int playerForThisComputation;

    private Integer numberTypes;
    private Double timePlayout;

    public NSSS(UnitTypeTable utt) {
        this(100, -1, 200, 1, 1,
                //new CombinedEvaluation(),
                new SimpleSqrtEvaluationFunction3(),
                //new SimpleSqrtEvaluationFunction2(),
                //new LanchesterEvaluationFunction(),
                utt,
                new AStarPathFinding());
    }

    public NSSS(int time, int max_playouts, int la, int a_I, int a_R, EvaluationFunction e, UnitTypeTable a_utt, PathFinding a_pf) {
        super(time, max_playouts);

        LOOKAHEAD = la;
        I = a_I;
        R = a_R;
        evaluation = e;
        utt = a_utt;
        pf = a_pf;
        defaultScript = new POLightRush(a_utt);
        scripts = new ArrayList<>();
        buildPortfolio();
    }

    protected void buildPortfolio() {
        this.scripts.add(new POWorkerRush(utt));
        this.scripts.add(new POLightRush(utt));
        this.scripts.add(new POHeavyRush(utt));
        this.scripts.add(new PORangedRush(utt));
        this.scripts.add(new NOKDPS(utt));
        this.scripts.add(new KitterDPS(utt));
        this.scripts.add(new Cluster(utt));

        //this.scripts.add(new POHeavyRush(utt, new FloodFillPathFinding()));
        //this.scripts.add(new POLightRush(utt, new FloodFillPathFinding()));
        //this.scripts.add(new PORangedRush(utt, new FloodFillPathFinding()));
    }

    @Override
    public void reset() {

    }

    protected void evalPortfolio(int heightMap) {
        if (heightMap <= 16 && !portfolioHasWorkerRush()) {
            //this.scripts.add(new POWorkerRush(utt));
        }
    }

    private boolean portfolioHasWorkerRush() {
        for (AI script : scripts) {
            if (script.toString().contains("POWorkerRush")) {
                return true;
            }
        }
        return false;
    }

    @Override
    public PlayerAction getAction(int player, GameState gs) throws Exception {
        if (gs.canExecuteAnyAction(player)) {
            //evalPortfolio(gs.getPhysicalGameState().getHeight());
            startNewComputation(player, gs);
            return getBestActionSoFar();
        } else {
            return new PlayerAction();
        }

    }

    @Override
    public PlayerAction getBestActionSoFar() throws Exception {

        //pego o melhor script do portfolio para ser a semente
        AI seedPlayer = getSeedPlayer(playerForThisComputation);
        AI seedEnemy = getSeedPlayer(1 - playerForThisComputation);

        defaultScript = seedPlayer;
        // set up the root script data
        UnitScriptData currentScriptData = new UnitScriptData(playerForThisComputation);
        UnitScriptData enemyScriptData = new UnitScriptData(1 - playerForThisComputation);

        currentScriptData.setSeedUnits(seedPlayer);
        enemyScriptData.setSeedUnits(seedEnemy);

        setAllScripts(playerForThisComputation, currentScriptData, seedPlayer);
        setAllScripts(1 - playerForThisComputation, enemyScriptData, seedEnemy);

        // do the initial root portfolio search for our player
        numberTypes = 0;
        timePlayout = 0.0;

        if (doStratifiedSearch(playerForThisComputation, currentScriptData, enemyScriptData)) {
            AdaptableStratType.increase(timePlayout, TIME_BUDGET, scripts.size());
        } else {
            AdaptableStratType.decrease(numberTypes);
        }

        //System.out.println("Result final");
        //currentScriptData.print();
        return getFinalAction(currentScriptData);
    }

    protected AI getSeedPlayer(int player) throws Exception {
        AI seed = null;
        double bestEval = -9999;
        AI enemyAI = new POLightRush(utt);
        //vou iterar para todos os scripts do portfolio
        for (AI script : scripts) {
            double tEval = eval(player, gs_to_start_from, script, enemyAI);
            if (tEval > bestEval) {
                bestEval = tEval;
                seed = script;
            }
        }

        return seed;
    }

    /*
    * Executa um playout de tamanho igual ao @LOOKAHEAD e retorna o valor
     */
    public double eval(int player, GameState gs, AI aiPlayer, AI aiEnemy) throws Exception {
        AI ai1 = aiPlayer.clone();
        AI ai2 = aiEnemy.clone();

        GameState gs2 = gs.clone();
        ai1.reset();
        ai2.reset();
        int timeLimit = gs2.getTime() + LOOKAHEAD;
        boolean gameover = false;
        while (!gameover && gs2.getTime() < timeLimit) {
            if (gs2.isComplete()) {
                gameover = gs2.cycle();
            } else {
                gs2.issue(ai1.getAction(player, gs2));
                gs2.issue(ai2.getAction(1 - player, gs2));
            }
        }
        double e = evaluation.evaluate(player, 1 - player, gs2);

        return e;
    }

    /**
     * Realiza um playout (Dave playout) para calcular o improve baseado nos
     * scripts existentes.
     *
     * @param player
     * @param gs
     * @param uScriptPlayer
     * @param aiEnemy
     * @return a avaliação para ser utilizada como base.
     * @throws Exception
     */
    public double eval(int player, GameState gs, UnitScriptData uScriptPlayer, UnitScriptData aiEnemy) throws Exception {
        //AI ai1 = defaultScript.clone();
        //AI ai2 = aiEnemy.clone();

        GameState gs2 = gs.clone();
        //ai1.reset();
        //ai2.reset();
        int timeLimit = gs2.getTime() + LOOKAHEAD;
        boolean gameover = false;
        while (!gameover && gs2.getTime() < timeLimit) {
            if (gs2.isComplete()) {
                gameover = gs2.cycle();
            } else {
                gs2.issue(uScriptPlayer.getAction(player, gs2));
                //
                gs2.issue(aiEnemy.getAction(1 - player, gs2));
            }
        }

        return evaluation.evaluate(player, 1 - player, gs2);
    }

    @Override
    public AI clone() {
        return new NSSS(TIME_BUDGET, ITERATIONS_BUDGET, LOOKAHEAD, I, R, evaluation, utt, pf);
    }

    @Override
    public List<ParameterSpecification> getParameters() {
        List<ParameterSpecification> parameters = new ArrayList<>();

        parameters.add(new ParameterSpecification("TimeBudget", int.class, 100));
        parameters.add(new ParameterSpecification("IterationsBudget", int.class, -1));
        parameters.add(new ParameterSpecification("PlayoutLookahead", int.class, 100));
        parameters.add(new ParameterSpecification("I", int.class, 1));
        parameters.add(new ParameterSpecification("R", int.class, 1));
        parameters.add(new ParameterSpecification("EvaluationFunction", EvaluationFunction.class, new SimpleSqrtEvaluationFunction3()));
        parameters.add(new ParameterSpecification("PathFinding", PathFinding.class, new AStarPathFinding()));

        return parameters;
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(" + TIME_BUDGET + ", " + ITERATIONS_BUDGET + ", " + LOOKAHEAD + ", " + I + ", " + R + ", " + evaluation + ", " + pf + ")";
    }

    public int getPlayoutLookahead() {
        return LOOKAHEAD;
    }

    public void setPlayoutLookahead(int a_pola) {
        LOOKAHEAD = a_pola;
    }

    public int getI() {
        return I;
    }

    public void setI(int a) {
        I = a;
    }

    public int getR() {
        return R;
    }

    public void setR(int a) {
        R = a;
    }

    public EvaluationFunction getEvaluationFunction() {
        return evaluation;
    }

    public void setEvaluationFunction(EvaluationFunction a_ef) {
        evaluation = a_ef;
    }

    public PathFinding getPathFinding() {
        return pf;
    }

    public void setPathFinding(PathFinding a_pf) {
        pf = a_pf;
    }

    @Override
    public void startNewComputation(int player, GameState gs) throws Exception {
        playerForThisComputation = player;
        gs_to_start_from = gs;
        nplayouts = 0;
        _startTime = gs.getTime();
        start_time = System.currentTimeMillis();
    }

    @Override
    public void computeDuringOneGameFrame() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private void setAllScripts(int playerForThisComputation, UnitScriptData currentScriptData, AI seedPlayer) {
        currentScriptData.reset();
        for (Unit u : gs_to_start_from.getUnits()) {
            if (u.getPlayer() == playerForThisComputation) {
                currentScriptData.setUnitScript(u, seedPlayer);
            }
        }
    }

    //private boolean doStratifiedSearch(int player, UnitScriptData currentScriptData, AI seedEnemy, Integer numberTypes, Double timePlayouts) throws Exception {
    private boolean doStratifiedSearch(int player, UnitScriptData currentScriptData, UnitScriptData seedEnemy) throws Exception {
        int enemy = 1 - player;
        int numberEvals = 0;

        //compute the set of type for each unit that can move
        HashMap<AdaptableStratType, ArrayList<Unit>> typeUnits = new HashMap<>();
        ArrayList<AdaptableStratType> ordemAdapt = new ArrayList<>();
        for (Unit un : gs_to_start_from.getUnits()) {
            if (un.getPlayer() == player) {
                AdaptableStratType t = new AdaptableStratType(un, gs_to_start_from);
                if (!typeUnits.containsKey(t)) {
                    ArrayList<Unit> v = new ArrayList<>();
                    typeUnits.put(t, v);
                    ordemAdapt.add(t);
                }
                typeUnits.get(t).add(un);
            }
        }
        //number of types to be returned through parameter numberTypes
        numberTypes = typeUnits.size();

        boolean hasFinishedIteration = false;
        for (int i = 0; i < I; i++) {
        //while (System.currentTimeMillis() < (start_time + (TIME_BUDGET - 10))) {

            // set up data for best scripts
            AI bestScriptVec[] = new AI[typeUnits.size()];
            double bestScoreVec[] = new double[typeUnits.size()];

            for (int typeIndex = 0; typeIndex < typeUnits.size(); typeIndex++) {
                for (int sIndex = 0; sIndex < scripts.size(); sIndex++) {
                    AI ai = scripts.get(sIndex);
                    for (Unit un : typeUnits.get(ordemAdapt.get(typeIndex))) {
                        currentScriptData.setUnitScript(un, ai);
                    }

                    double score = SHillEvaluation(1-player, seedEnemy, currentScriptData );//eval(player, gs_to_start_from, currentScriptData, seedEnemy);
                    numberEvals++;
                    if ((sIndex == 0) || (score < bestScoreVec[typeIndex])) {
                        bestScriptVec[typeIndex] = scripts.get(sIndex);
                        bestScoreVec[typeIndex] = score;
                    }

                }
                for (Unit un : typeUnits.get(ordemAdapt.get(typeIndex))) {
                    currentScriptData.setUnitScript(un, bestScriptVec[typeIndex]);
                }
                //System.out.println("Analisando....");
                //currentScriptData.print();

                //if (System.currentTimeMillis() > (start_time + (TIME_BUDGET - 0))) {
                //    timePlayout = (double) (System.currentTimeMillis() - start_time) / (numberEvals);
                //    return hasFinishedIteration;
                //}

            }

            hasFinishedIteration = true;

        }
        /*
        System.out.println("---Tempo Inicial: "+ start_time);
        System.out.println("---CurrentTime: "+ System.currentTimeMillis());
        System.out.println("---Tempo calculado: "+ (System.currentTimeMillis() - start_time));
        System.out.println("---numberEvals: "+ numberEvals);
         */
        if (numberEvals == 0) {
            timePlayout = (double) ((System.currentTimeMillis() - start_time) / (1));
        } else {
            timePlayout = (double) ((System.currentTimeMillis() - start_time) / (numberEvals));
        }

        //System.out.println("---Calculado: "+ timePlayout);
        return hasFinishedIteration;
    }
    
    private double SHillEvaluation(int player, UnitScriptData currentScriptData, UnitScriptData seedEnemy) throws Exception {
        int enemy = 1 - player;

        UnitScriptData bestScriptData = currentScriptData.clone();
        double bestScore = eval(player, gs_to_start_from, bestScriptData, seedEnemy);
        ArrayList<Unit> unitsPlayer = getUnitsPlayer(player);
        //controle pelo número de iterações
        for (int i = 0; i < I; i++) {
        //while (System.currentTimeMillis() < (start_time + (TIME_BUDGET - 2))) {
            //fazer o improve de cada unidade
            for (Unit unit : unitsPlayer) {
                //iterar sobre cada script do portfolio
                for (AI ai : scripts) {
                    currentScriptData.setUnitScript(unit, ai);
                    double scoreTemp = eval(player, gs_to_start_from, currentScriptData, seedEnemy);

                    if (scoreTemp > bestScore) {
                        bestScriptData = currentScriptData.clone();
                        bestScore = scoreTemp;
                    }
                    //if ((System.currentTimeMillis() - start_time) > (TIME_BUDGET - 1)) {
                    //    return bestScore;
                   // }
                }
                //seto o melhor vetor para ser usado em futuras simulações
                currentScriptData = bestScriptData.clone();
            }
        }
        return bestScore;
    }

    private ArrayList<Unit> getUnitsPlayer(int player) {
        ArrayList<Unit> unitsPlayer = new ArrayList<>();
        for (Unit u : gs_to_start_from.getUnits()) {
            if (u.getPlayer() == player) {
                unitsPlayer.add(u);
            }
        }
        return unitsPlayer;
    }

    private PlayerAction getFinalAction(UnitScriptData currentScriptData) throws Exception {
        PlayerAction pAction = new PlayerAction();
        HashMap<String, PlayerAction> actions = new HashMap<>();
        for (AI script : scripts) {
            actions.put(script.toString(), script.getAction(playerForThisComputation, gs_to_start_from));
        }
        for (Unit u : currentScriptData.getUnits()) {
            AI ai = currentScriptData.getAIUnit(u);

            UnitAction unt = actions.get(ai.toString()).getAction(u);
            if (unt != null) {
                pAction.addUnitAction(u, unt);
            }
        }

        return pAction;
    }

}
