/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.aiSelection;


import ai.abstraction.partialobservability.POHeavyRush;
import ai.abstraction.partialobservability.POLightRush;
import ai.abstraction.partialobservability.PORangedRush;
import ai.abstraction.partialobservability.POWorkerRush;
import ai.abstraction.pathfinding.AStarPathFinding;
import ai.abstraction.pathfinding.PathFinding;
import ai.core.AI;
import ai.core.AIWithComputationBudget;
import ai.core.InterruptibleAI;
import ai.core.ParameterSpecification;
import ai.evaluation.EvaluationFunction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import rts.GameState;
import rts.PlayerAction;
import rts.units.Unit;
import rts.units.UnitTypeTable;
import PVAI.EconomyRush;
import PVAI.EconomyMilitaryRush;
import ai.abstraction.HeavyRush;
import ai.abstraction.pathfinding.FloodFillPathFinding;
import ai.aiSelection.IDABCD.ABSelection;
import ai.configurablescript.BasicExpandedConfigurableScript;
import ai.configurablescript.ScriptsCreator;
import ai.evaluation.EvalSelection;

/**
 *
 * @author rubens
 */
public class AISelection extends AIWithComputationBudget implements InterruptibleAI {

    int LOOKAHEAD = 200;
    int I = 1;  // number of iterations for improving a given player
    int R = 0;  // number of times to improve with respect to the response fo the other player
    EvaluationFunction evaluation = null;
    ArrayList<AI> scripts = null;
    //ScriptMin will be used just when we have an abstractied action space for Min player
    ArrayList<AI> scriptsMin = null;
    UnitTypeTable utt;
    PathFinding pf;
    
    ArrayList<AI> scriptsA = null;

    AI defaultScript = null;

    long start_time = 0;
    int nplayouts = 0;
    ABSelection ab = null;
    
    ScriptsCreator sc=null;
    ArrayList<BasicExpandedConfigurableScript> scriptsCompleteSet=null;
    ArrayList<BasicExpandedConfigurableScript> scriptsMixSet=null;

    GameState gs_to_start_from = null;
    int playerForThisComputation;
    

    public AISelection(UnitTypeTable utt) {
        this(100, -1, 100, 1, 1,
                new EvalSelection(),
                //new SimpleSqrtEvaluationFunction2(),
                //new LanchesterEvaluationFunction(),
                utt,
                new AStarPathFinding());
    }
    
    public AISelection(UnitTypeTable utt, ArrayList<Integer> iScripts) {
        this(100, -1, 100, 1, 1,
                new EvalSelection(),
                utt,
                new AStarPathFinding(),
                iScripts);
    }

    public AISelection(int time, int max_playouts, int la, int a_I, int a_R, EvaluationFunction e, UnitTypeTable a_utt, PathFinding a_pf) {
        super(time, max_playouts);

        LOOKAHEAD = la;
        I = a_I;
        R = a_R;
        evaluation = e;
        utt = a_utt;
        pf = a_pf;
        defaultScript = new POLightRush(a_utt);
        scripts = new ArrayList<>();
        scriptsMin = new ArrayList<>();
        
        
        //The next two lines is for calling the set Z of scripts created with the BasicExpandedConfigurableScript class
        ScriptsCreator sc=new ScriptsCreator(utt,300);
        //scriptsCompleteSet=sc.getScriptsMixSet();
        //scriptsMixSet=sc.getScriptsMixSet();
        scriptsCompleteSet = sc.getScriptsMixReducedSet();
        System.out.println("size complete set 	"+scriptsCompleteSet.size());     
        //System.out.println("size mixed set 	"+scriptsMixSet.size());  
        
        buildPortfolio();
        ab = new ABSelection(utt); 
    }
    
    public AISelection(int time, int max_playouts, int la, int a_I, int a_R, EvaluationFunction e, 
                        UnitTypeTable a_utt, PathFinding a_pf, ArrayList<Integer> iScripts) {
        super(time, max_playouts);

        LOOKAHEAD = la;
        I = a_I;
        R = a_R;
        evaluation = e;
        utt = a_utt;
        pf = a_pf;
        defaultScript = new POLightRush(a_utt);
        scripts = new ArrayList<>();
        scriptsMin = new ArrayList<>();
        
        
        //The next two lines is for calling the set Z of scripts created with the BasicExpandedConfigurableScript class
        ScriptsCreator sc=new ScriptsCreator(utt,300);
        //scriptsCompleteSet=sc.getScriptsCompleteSet();
        //scriptsCompleteSet = sc.getScriptsMixSet();
        scriptsCompleteSet = sc.getScriptsMixReducedSet();
        System.out.println("size complete set 	"+scriptsCompleteSet.size());        
        
        buildPortfolio(iScripts);
        ab = new ABSelection(utt); 
    }

    protected void buildPortfolio(ArrayList<Integer> iScripts) {
    	
    	//including the first script from the Z set
        for (Integer indice : iScripts) {
            this.scripts.add(scriptsCompleteSet.get(indice));                
        }
        
        //System.out.println("Lista scripts ="+ scripts);
    	
    	//The next line is for adding scripts for Min
    	this.scriptsMin.add(scriptsCompleteSet.get(30));

    }
    
    
    protected void buildPortfolio() {
        
        //this.scripts.add(new POWorkerRush(utt));
        //this.scripts.add(new POHeavyRush(utt));
        //this.scripts.add(new POLightRush(utt));
        //this.scripts.add(new PORangedRush(utt));
        //this.scripts.add(new EconomyMilitaryRush(utt));
        
        //this.scripts.add(new POHeavyRush(utt, new FloodFillPathFinding()));
        //this.scripts.add(new POLightRush(utt, new FloodFillPathFinding()));
        //this.scripts.add(new PORangedRush(utt, new FloodFillPathFinding()));
    	
    	//including the first script from the Z set
        for (int i = 0; i < 20; i++) {
            this.scripts.add(scriptsCompleteSet.get(i));    
        }
    	
    	//The next line is for adding scripts for Min
    	this.scriptsMin.add(scriptsCompleteSet.get(30));

    }

    @Override
    public void reset() {

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
       
//        //rodar um ID-ABCD com o script controlando nosso jogador e ver o quanto iremos achar de score.
//        //System.out.println("Eval Selection do estado atual:"+ evaluation.evaluate(playerForThisComputation, (1-playerForThisComputation), gs_to_start_from));
//        System.out.println("--------------------------------");
//        //System.out.println("Action padrão = "+ defaultScript.getAction(playerForThisComputation, gs_to_start_from).toString());
//        
//        
//        ab.setScripts(scripts);
//        ab.setScriptsMin(scriptsMin);
//        PlayerAction abAct = ab.getAction(playerForThisComputation, gs_to_start_from);
//        System.out.println("Action AB = "+abAct.toString());
//        System.out.println("Action AB = "+ab.getBestMoveValue());
//        //System.out.println(ab.statisticsString());
//        System.out.println("--------------------------------");
//        return abAct;
        
        
        //return scripts.get(0).getAction(playerForThisComputation, gs_to_start_from.clone());
        
        
        //ab.setScriptsMin(scriptsMin);
    	ArrayList<AI> scriptsUniverse = new ArrayList<>(scripts);;
        PlayerAction abAct;
        scriptsA = new ArrayList<>();
        float bestMoveValue=Float.NEGATIVE_INFINITY;
        float lastBestMoveValue=Float.NEGATIVE_INFINITY;
        AI bestCandidate=null;
        int indexBestScript=-1;
        int tamUniverseScripts=scriptsUniverse.size();
        
        while(scriptsA.size()!=tamUniverseScripts)
        {
        	for(int i=0; i<scriptsUniverse.size();i++)
        	{
        		ab = new ABSelection(utt); 
        		AI candidate=scriptsUniverse.get(i);
        		scriptsA.add(candidate);
        		ab.setScripts(scriptsA);
        		abAct = ab.getAction(playerForThisComputation, gs_to_start_from);
        		if(ab.getBestMoveValue()>bestMoveValue)
        		{
        			bestMoveValue=ab.getBestMoveValue();
        			bestCandidate=candidate;
        			indexBestScript=i;
        		}
        		scriptsA.remove(scriptsA.size()-1);
        	}
        	if(lastBestMoveValue>=bestMoveValue)
        	{
        		break;
        	}
        	else
        	{
        		scriptsA.add(bestCandidate);
        		scriptsUniverse.remove(indexBestScript);
        		lastBestMoveValue=bestMoveValue;
        		bestMoveValue=Float.NEGATIVE_INFINITY;
        	}
        	System.out.println("lastBestMoveValue "+lastBestMoveValue);
        	
        }
        
        //printing the selected set
        for (AI obj: scriptsA){
        	BasicExpandedConfigurableScript aiSelected=(BasicExpandedConfigurableScript)obj;
        	System.out.println(aiSelected.getParametersScript());
        }
        //System.out.println(Arrays.toString(scriptsA.toArray()));
        System.out.println("End AB");
        
        //This is just for returning something.  
        return scripts.get(0).getAction(playerForThisComputation, gs_to_start_from);
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

    @Override
    public AI clone() {
        return new AISelection(TIME_BUDGET, ITERATIONS_BUDGET, LOOKAHEAD, I, R, evaluation, utt, pf);
    }

    @Override
    public List<ParameterSpecification> getParameters() {
        List<ParameterSpecification> parameters = new ArrayList<>();

        parameters.add(new ParameterSpecification("TimeBudget", int.class, 100));
        parameters.add(new ParameterSpecification("IterationsBudget", int.class, -1));
        parameters.add(new ParameterSpecification("PlayoutLookahead", int.class, 100));
        parameters.add(new ParameterSpecification("I", int.class, 1));
        parameters.add(new ParameterSpecification("R", int.class, 1));
        parameters.add(new ParameterSpecification("EvaluationFunction", EvaluationFunction.class, new EvalSelection()));
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
        start_time = System.currentTimeMillis();
    }

    @Override
    public void computeDuringOneGameFrame() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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

}
