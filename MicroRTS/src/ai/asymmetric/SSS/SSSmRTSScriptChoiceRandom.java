/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.asymmetric.SSS;

import ai.RandomBiasedAI;
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
import util.Pair;

/**
 *
 * @author rubens
 */
public class SSSmRTSScriptChoiceRandom extends AIWithComputationBudget implements InterruptibleAI {

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
    
    //tupla scripts
    String tuplaInScripts = "";
    AI randAI = null;
    int qtdSumPlayout = 1;
    //
    HashMap<String, PlayerAction> cache;

    public SSSmRTSScriptChoiceRandom(UnitTypeTable utt) {
        this(100, -1, 200, 4, 4,
                //new CombinedEvaluation(),
                new SimpleSqrtEvaluationFunction3(),
                //new SimpleSqrtEvaluationFunction2(),
                //new LanchesterEvaluationFunction(),
                utt,
                new AStarPathFinding());
    }
    
    public SSSmRTSScriptChoiceRandom(UnitTypeTable utt, List<AI> scripts) {
        this(100, -1, 200, 4, 4,
                //new CombinedEvaluation(),
                new SimpleSqrtEvaluationFunction3(),
                //new SimpleSqrtEvaluationFunction2(),
                //new LanchesterEvaluationFunction(),
                utt,
                new AStarPathFinding());
        this.scripts = scripts;
    }
    
    public SSSmRTSScriptChoiceRandom(UnitTypeTable utt, List<AI> scripts, String tuplaIndSc) {
        this(100, -1, 200, 4, 4,
                //new CombinedEvaluation(),
                new SimpleSqrtEvaluationFunction3(),
                //new SimpleSqrtEvaluationFunction2(),
                //new LanchesterEvaluationFunction(),
                utt,
                new AStarPathFinding());
        this.scripts = scripts;
        this.tuplaInScripts = tuplaIndSc;
    }
    
    public SSSmRTSScriptChoiceRandom(int time, int max_playouts, int la, int a_I, int a_R, EvaluationFunction e, UnitTypeTable a_utt, PathFinding a_pf, List<AI> scripts) {
        super(time, max_playouts);

        LOOKAHEAD = la;
        I = a_I;
        R = a_R;
        evaluation = e;
        utt = a_utt;
        pf = a_pf;
        defaultScript = new POLightRush(a_utt);
        this.scripts = scripts;
    }
    
    public SSSmRTSScriptChoiceRandom(UnitTypeTable utt, List<AI> scripts, String tuplaIndSc, int qtdPlayout, int lookahead) {
        this(100, -1, lookahead, 4, 4,
                new SimpleSqrtEvaluationFunction3(),
                //new SimpleSqrtEvaluationFunction2(),
                //new LanchesterEvaluationFunction(),
                utt,
                new AStarPathFinding());
        this.scripts = scripts;
        this.tuplaInScripts = tuplaIndSc;
        this.qtdSumPlayout = qtdPlayout;
    }
    
    public SSSmRTSScriptChoiceRandom(int time, int max_playouts, int la, int a_I, int a_R, EvaluationFunction e, UnitTypeTable a_utt, PathFinding a_pf) {
        super(time, max_playouts);

        LOOKAHEAD = la;
        I = a_I;
        R = a_R;
        evaluation = e;
        utt = a_utt;
        pf = a_pf;
        defaultScript = new POLightRush(a_utt);
        //defaultScript = new POWorkerRush(a_utt);
        scripts = new ArrayList<>();
        randAI = new RandomBiasedAI(a_utt);
        //The next two lines is for calling the set Z of scripts created with the BasicExpandedConfigurableScript class

    }
    

    @Override
    public void reset() {

    }    
    
    private boolean portfolioHasWorkerRush() {
        for (AI script : scripts) {
            if(script.toString().contains("POWorkerRush")){
                return true;
            }
        }
        return false;
    }

    @Override
    public PlayerAction getAction(int player, GameState gs) throws Exception {
        if (gs.canExecuteAnyAction(player)) {            
            startNewComputation(player, gs);
            return getBestActionSoFar();
        } else {
            return new PlayerAction();
        }

    }

    @Override
    public PlayerAction getBestActionSoFar() throws Exception {
        //coleto o cache dos scripts
        getCache();
        //pego o melhor script do portfolio para ser a semente
        AI seedPlayer = getSeedPlayer(playerForThisComputation);
        AI seedEnemy = getSeedPlayer(1 - playerForThisComputation);

        defaultScript = seedPlayer;
        // set up the root script data
        UnitScriptData currentScriptData = new UnitScriptData(playerForThisComputation);
        currentScriptData.setSeedUnits(seedPlayer);
        setAllScripts(playerForThisComputation, currentScriptData, seedPlayer);

        // do the initial root portfolio search for our player
        numberTypes = 0;
        timePlayout = 0.0;

        if(doStratifiedSearch(playerForThisComputation, currentScriptData, seedEnemy)){
            /*
            System.out.print("Time Playout ");
            AdaptableStratType.printType();
            System.out.println("Types: "+ numberTypes + " Time: "+ timePlayout);
            */
            AdaptableStratType.increase(timePlayout,TIME_BUDGET, scripts.size());
        }else{
            /*
            System.out.print("Time Playout ");
            AdaptableStratType.printType();
            System.out.println("Types: "+ numberTypes + " Time: "+ timePlayout);
            */
            AdaptableStratType.decrease(numberTypes);
        }

        return getFinalAction(currentScriptData);
    }

    
    protected AI getSeedPlayer(int player) throws Exception {
        AI seed = null;
        double bestEval = -9999;
        //AI enemyAI = new POLightRush(utt);
        AI enemyAI = defaultScript.clone();
        //vou iterar para todos os scripts do portfolio
        for (AI script : scripts) {
            double tEval = eval(player, gs_to_start_from, script, enemyAI);
            if (tEval > bestEval) {
                bestEval = tEval;
                seed = script;
            }
            if ((System.currentTimeMillis() - start_time) > (TIME_BUDGET-5)) {
                return seed;
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
    public double eval(int player, GameState gs, UnitScriptData uScriptPlayer, AI aiEnemy) throws Exception {
        //AI ai1 = defaultScript.clone();
        AI ai2 = aiEnemy.clone();
        ai2.reset();
        GameState gs2 = gs.clone();
        //actions default
        //gs2.issue(uScriptPlayer.getAction(player, gs2));
        //gs2.issue(ai2.getAction(1 - player, gs2));
        gs2.issue(getActionsUScript(player, uScriptPlayer, gs2));
        gs2.issue(ai2.getAction(1 - player, gs2));
        int timeLimit = gs2.getTime() + LOOKAHEAD;
        boolean gameover = false;
        while (!gameover && gs2.getTime() < timeLimit) {
            if (gs2.isComplete()) {
                gameover = gs2.cycle();
            } else {
                gs2.issue(randAI.getAction(player, gs2));
                gs2.issue(randAI.getAction(1 - player, gs2));
            }
        }

        return evaluation.evaluate(player, 1 - player, gs2);
    }

    @Override
    public AI clone() {
        return new SSSmRTSScriptChoiceRandom(TIME_BUDGET, ITERATIONS_BUDGET, LOOKAHEAD, I, R, evaluation, utt, pf);
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
        //return getClass().getSimpleName() + "(" + TIME_BUDGET + ", " + ITERATIONS_BUDGET + ", " + LOOKAHEAD + ", " + I + ", " + R + ", " + evaluation + ", " + pf + ")";
        //return getClass().getSimpleName()+"_"+tuplaInScripts+"_"+this.scripts.toString();
        return getClass().getSimpleName() + "_" + tuplaInScripts + "_" + qtdSumPlayout + "_" + LOOKAHEAD;
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
        this.cache = new HashMap<>();
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
    private boolean doStratifiedSearch(int player, UnitScriptData currentScriptData, AI seedEnemy) throws Exception {
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

        while (System.currentTimeMillis() < (start_time + (TIME_BUDGET - 4))) {

            // set up data for best scripts
            AI bestScriptVec[] = new AI[typeUnits.size()];
            double bestScoreVec[] = new double[typeUnits.size()];

            for (int typeIndex = 0; typeIndex < typeUnits.size(); typeIndex++) {
                for (int sIndex = 0; sIndex < scripts.size(); sIndex++) {
                    AI ai = scripts.get(sIndex);
                    for (Unit un : typeUnits.get(ordemAdapt.get(typeIndex))) {
                        currentScriptData.setUnitScript(un, ai);
                    }

                    double sum = 0.0;
                    for (int i = 0; i < qtdSumPlayout; i++) {
                        sum += eval(player, gs_to_start_from, currentScriptData, seedEnemy);
                    }
                    double score = sum / qtdSumPlayout;
                    
                    numberEvals++;
                    if ((sIndex == 0) || (score > bestScoreVec[typeIndex])) {
                        bestScriptVec[typeIndex] = scripts.get(sIndex);
                        bestScoreVec[typeIndex]  = score;
                    }

                }
                for (Unit un : typeUnits.get(ordemAdapt.get(typeIndex))) {
                    currentScriptData.setUnitScript(un, bestScriptVec[typeIndex]);
                }
                
                if( System.currentTimeMillis() > (start_time + (TIME_BUDGET - 0)) ){
                    timePlayout =  (double)(System.currentTimeMillis() - start_time) /(numberEvals);
                    return hasFinishedIteration;
                }
                
            }
            
            hasFinishedIteration = true;

        }
        /*
        System.out.println("---Tempo Inicial: "+ start_time);
        System.out.println("---CurrentTime: "+ System.currentTimeMillis());
        System.out.println("---Tempo calculado: "+ (System.currentTimeMillis() - start_time));
        System.out.println("---numberEvals: "+ numberEvals);
        */
        if(numberEvals==0){
            timePlayout = (double)((System.currentTimeMillis() - start_time) /(1));    
        }else{
            timePlayout = (double)((System.currentTimeMillis() - start_time) /(numberEvals));
        }
        
        //System.out.println("---Calculado: "+ timePlayout);
        return hasFinishedIteration;
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
    
    private void getCache() throws Exception {
        for (AI script : scripts) {
            cache.put(script.toString(), script.getAction(playerForThisComputation, gs_to_start_from));
        }
    }
    
    private PlayerAction getActionsUScript(int player, UnitScriptData uScriptPlayer, GameState gs2) {
        PlayerAction temp = new PlayerAction();
        for (Unit u : gs2.getUnits()) {
            if (u.getPlayer() == player) {
                String sAI = uScriptPlayer.getAIUnit(u).toString();

                UnitAction uAt = getUnitAction(u, cache.get(sAI));
                if(uAt != null){
                    temp.addUnitAction(u, uAt);
                }
            }
        }

        return temp;
    }
    
    private UnitAction getUnitAction(Unit u, PlayerAction get) {
        for (Pair<Unit, UnitAction> tmp : get.getActions()) {
            if (tmp.m_a.getID() == u.getID()) {
                return tmp.m_b;
            }
        }
        return null;
    }

}
