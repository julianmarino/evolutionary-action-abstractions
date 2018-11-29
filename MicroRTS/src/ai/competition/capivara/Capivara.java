/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.competition.capivara;

import ai.RandomBiasedAI;
import ai.abstraction.AbstractionLayerAI;
import ai.abstraction.pathfinding.AStarPathFinding;
import ai.abstraction.pathfinding.PathFinding;
import ai.configurablescript.BasicExpandedConfigurableScript;
import ai.configurablescript.ScriptsCreator;
import ai.core.AI;
import ai.core.ParameterSpecification;
import ai.evaluation.EvaluationFunction;
import ai.evaluation.SimpleSqrtEvaluationFunction3;
import java.util.ArrayList;
import java.util.List;
import rts.GameState;
import rts.PhysicalGameState;
import rts.PlayerAction;
import rts.units.UnitTypeTable;

/**
 *
 * @author Rubens, Julian and Levi
 */
public class Capivara extends AbstractionLayerAI {
    
    GameState gs_to_start_from = null;
    int playerForThisComputation;
    long start_time = 0;
    boolean started = false;
    AI baseAI = null;
    UnitTypeTable utt;
    ScriptsCreator sc;
    ArrayList<BasicExpandedConfigurableScript> scriptsCompleteSet;

    public Capivara(UnitTypeTable utt){
        super(new AStarPathFinding(),100, 200);
        this.utt = utt;
        sc = new ScriptsCreator(utt,300);
        scriptsCompleteSet = sc.getScriptsMixReducedSet();
    }
    
    public Capivara(int time, int max_playouts) {
        super(new AStarPathFinding(),time, max_playouts);
        started = false;
    }

    @Override
    public void reset() {
        this.started = false;
    }

    @Override
    public PlayerAction getAction(int player, GameState gs) throws Exception {
        if (gs.canExecuteAnyAction(player)) {
            if(!started){
                inicializeIA(player, gs);
                started = true;
            }
            startNewComputation(player, gs);
            return getBestActionSoFar();
        } else {
            return new PlayerAction();
        }
    }

    @Override
    public AI clone() {
        return new Capivara(utt);
    }

    @Override
    public List<ParameterSpecification> getParameters() {
        List<ParameterSpecification> parameters = new ArrayList<>();

        parameters.add(new ParameterSpecification("TimeBudget", int.class, this.TIME_BUDGET));
        parameters.add(new ParameterSpecification("IterationsBudget", int.class, -1));
        parameters.add(new ParameterSpecification("PlayoutLookahead", int.class, this.ITERATIONS_BUDGET));        
        parameters.add(new ParameterSpecification("EvaluationFunction", EvaluationFunction.class, new SimpleSqrtEvaluationFunction3()));
        parameters.add(new ParameterSpecification("PathFinding", PathFinding.class, new AStarPathFinding()));

        return parameters;
    }

    public void startNewComputation(int player, GameState gs) throws Exception {
        playerForThisComputation = player;
        gs_to_start_from = gs;
        start_time = System.currentTimeMillis();
    }

    public void computeDuringOneGameFrame() throws Exception {
        
    }

    public PlayerAction getBestActionSoFar() throws Exception {
        return baseAI.getAction(playerForThisComputation, gs_to_start_from);
    }

    private void inicializeIA(int player, GameState gs) {
        PhysicalGameState pgs = gs.getPhysicalGameState();
        if(pgs.getHeight() == 8 && pgs.getWidth() == 8){
            this.baseAI = new CmabAssymetricMCTS(100, -1, 100, 1, 0.3f, 
                                             0.0f, 0.4f, 0, new RandomBiasedAI(utt), 
                                             new SimpleSqrtEvaluationFunction3(), true, utt, 
                                            "ManagerClosestEnemy", 1,decodeScripts(utt, "48;0;"));
        } else if(pgs.getHeight() == 8 && pgs.getWidth() == 9){
            this.baseAI = new CmabAssymetricMCTS(100, -1, 100, 1, 0.3f, 
                                             0.0f, 0.4f, 0, new RandomBiasedAI(utt), 
                                             new SimpleSqrtEvaluationFunction3(), true, utt, 
                                            "ManagerClosestEnemy", 1, decodeScripts(utt, "203;3;"));
            
        } else if( (pgs.getHeight() >= 9 && pgs.getHeight() <= 16) && (pgs.getWidth() >= 9 && pgs.getWidth() <= 16) ){
            this.baseAI = new CmabAssymetricMCTS(100, -1, 100, 1, 0.3f, 
                                             0.0f, 0.4f, 0, new RandomBiasedAI(utt), 
                                             new SimpleSqrtEvaluationFunction3(), true, utt, 
                                            "ManagerClosestEnemy", 1, decodeScripts(utt, "1;270;"));
            
        } else if( (pgs.getHeight() > 16 && pgs.getHeight() <= 24) && (pgs.getWidth() > 16 && pgs.getWidth() <= 24) ){
            this.baseAI = new CmabAssymetricMCTS(100, -1, 100, 1, 0.3f, 
                                             0.0f, 0.4f, 0, new RandomBiasedAI(utt), 
                                             new SimpleSqrtEvaluationFunction3(), true, utt, 
                                            "ManagerClosestEnemy", 1, decodeScripts(utt, "93;45;211;"));
        }else if( (pgs.getHeight() > 24 && pgs.getHeight() <= 32) && (pgs.getWidth() > 24 && pgs.getWidth() <= 32) ){
            this.baseAI = new CmabAssymetricMCTS(100, -1, 100, 1, 0.3f, 
                                             0.0f, 0.4f, 0, new RandomBiasedAI(utt), 
                                             new SimpleSqrtEvaluationFunction3(), true, utt, 
                                            "ManagerClosestEnemy", 1, decodeScripts(utt, "1;285;244;"));
        }else{
            this.baseAI = new CmabAssymetricMCTS(100, -1, 100, 1, 0.3f, 
                                             0.0f, 0.4f, 0, new RandomBiasedAI(utt), 
                                             new SimpleSqrtEvaluationFunction3(), true, utt, 
                                            "ManagerClosestEnemy", 1, decodeScripts(utt, "297;158;256;")); //
        }
    }
    
    
    private List<AI> decodeScripts(UnitTypeTable utt, String sScripts) {
        ArrayList<Integer> iScriptsAi1 = new ArrayList<>();
        String[] itens = sScripts.split(";");
        for (String element : itens) {
            iScriptsAi1.add(Integer.decode(element));
        }
        List<AI> scriptsAI = new ArrayList<>();
        iScriptsAi1.forEach((idSc) -> {
            scriptsAI.add(scriptsCompleteSet.get(idSc));
        });
        return scriptsAI;
    }
}
