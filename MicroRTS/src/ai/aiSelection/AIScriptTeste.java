/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.aiSelection;


import ai.abstraction.partialobservability.POHeavyRush;
import ai.abstraction.partialobservability.POLightRush;
import ai.abstraction.pathfinding.AStarPathFinding;
import ai.abstraction.pathfinding.PathFinding;
import ai.core.AI;
import ai.core.AIWithComputationBudget;
import ai.core.InterruptibleAI;
import ai.core.ParameterSpecification;
import ai.evaluation.EvaluationFunction;
import java.util.ArrayList;
import java.util.List;
import rts.GameState;
import rts.PlayerAction;
import rts.units.Unit;
import rts.units.UnitTypeTable;
import ai.abstraction.pathfinding.FloodFillPathFinding;
import ai.evaluation.SimpleSqrtEvaluationFunction3;

/**
 *
 * @author rubens
 */
public class AIScriptTeste extends AIWithComputationBudget implements InterruptibleAI {

    int LOOKAHEAD = 200;
    int I = 1;  // number of iterations for improving a given player
    int R = 0;  // number of times to improve with respect to the response fo the other player
    EvaluationFunction evaluation = null;
    UnitTypeTable utt;
    PathFinding pf;
    

    AI defaultScript = null;

    long start_time = 0;
    int nplayouts = 0;

    GameState gs_to_start_from = null;
    int playerForThisComputation;
    

    public AIScriptTeste(UnitTypeTable utt) {
        this(100, -1, 100, 1, 1,
                new SimpleSqrtEvaluationFunction3(),
                //new LanchesterEvaluationFunction(),
                utt,
                new AStarPathFinding());
    }

    public AIScriptTeste(int time, int max_playouts, int la, int a_I, int a_R, EvaluationFunction e, UnitTypeTable a_utt, PathFinding a_pf) {
        super(time, max_playouts);

        LOOKAHEAD = la;
        I = a_I;
        R = a_R;
        evaluation = e;
        utt = a_utt;
        pf = a_pf;
        defaultScript = new POLightRush(a_utt);
                   
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
       
        
        //the behavior in this line is normal
        //AI ait = defaultScript.clone();        
        //return ait.getAction(playerForThisComputation, gs_to_start_from);
        //return defaultScript.getAction(playerForThisComputation, gs_to_start_from);
        

        //the behavior with state's clone is diferent
        return defaultScript.getAction(playerForThisComputation, gs_to_start_from.clone());
        
    }
    

    @Override
    public AI clone() {
        return new AIScriptTeste(TIME_BUDGET, ITERATIONS_BUDGET, LOOKAHEAD, I, R, evaluation, utt, pf);
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
        start_time = System.currentTimeMillis();
    }

    @Override
    public void computeDuringOneGameFrame() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
