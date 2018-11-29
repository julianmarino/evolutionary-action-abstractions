/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.CMAB;

import Standard.CombinedEvaluation;
import ai.RandomBiasedAI;
import ai.abstraction.partialobservability.POLightRush;
import ai.asymmetric.ManagerUnits.IManagerAbstraction;
import ai.asymmetric.ManagerUnits.ManagerClosest;
import ai.core.AI;
import ai.core.AIWithComputationBudget;
import ai.core.InterruptibleAI;
import ai.core.ParameterSpecification;
import ai.evaluation.EvaluationFunction;
import ai.evaluation.SimpleSqrtEvaluationFunction3;
import java.util.ArrayList;
import java.util.List;
import rts.GameState;
import rts.PlayerAction;
import rts.units.UnitTypeTable;

/**
 *
 * @author rubens
 */
public class CMABBuilder extends AIWithComputationBudget implements InterruptibleAI {

    private AI CMABAI;
    private String moveString;
    private String behavior;
    private int qtdUnits;
    private int minSize;
    private int minPoint;

    public CMABBuilder(UnitTypeTable utt) {
        //this(100, -1, 100, 10, 0, new RandomBiasedAI(), new SimpleSqrtEvaluationFunction3(), 0, utt, new ArrayList<AI>(),"CmabPlayerActionGenerator");
        //this(100, -1, 100, 10, 0, new RandomBiasedAI(utt), new SimpleSqrtEvaluationFunction3(), 0, utt, new ArrayList<AI>(), "CmabCombinatorialGenerator");
        //this(100, -1, 100, 10, 0, new RandomBiasedAI(), new SimpleSqrtEvaluationFunction3(), 0, utt, new ArrayList<AI>(),"CmabHillClimbingGenerator");
        //this(100, -1, 100, 2, 0, new RandomBiasedAI(utt), new SimpleSqrtEvaluationFunction3(), 0, utt, new ArrayList<AI>(), "CmabAsyReduzedGenerator");

        //assymetric
        this(100, -1, 100, 10, 0, new RandomBiasedAI(utt), new CombinedEvaluation(), 0, utt, 
                new ArrayList<AI>(), "CmabCombinatorialGenerator", "ManagerClosestEnemy", 2);
        
        //assymetric Cluster  
        //CmabClusterEuDistGenerator
        //CmabClusterPlayoutGenerator
        //CmabClusterGammaGenerator
        
        //Asymmetric cluster
        //this(100, -1, 100, 10, 0, new RandomBiasedAI(utt), new SimpleSqrtEvaluationFunction3(), 0, utt, 
        //        new ArrayList<AI>(), "CmabClusterEuDistGenerator", 2, 2);
    }
    
    //used to build the NaiveMCTS Assymetric Cluster
    public CMABBuilder(int available_time, int max_playouts, int lookahead, int max_depth, int police_Exp,
            AI policyPlayout, EvaluationFunction a_ef, int global_strategy,
            UnitTypeTable utt, List<AI> abstraction, String generatorMoves, int minSize, int minPoint) {
        super(available_time, max_playouts);
        this.moveString = generatorMoves;
        this.CMABAI = new CmabAsymClusterMCTS(available_time, max_playouts, lookahead, max_depth, 0.3f, 
                                             0.0f, 0.4f, global_strategy, policyPlayout, 
                                             a_ef, true, utt, generatorMoves, minSize, minPoint);
        this.minSize = minSize;
        this.minPoint = minPoint;
    }
    
    
    //used to build the NaiveMCTS Assymetric    
    public CMABBuilder(int available_time, int max_playouts, int lookahead, int max_depth, int police_Exp,
            AI policyPlayout, EvaluationFunction a_ef, int global_strategy,
            UnitTypeTable utt, List<AI> abstraction, String generatorMoves, String behavior, int qtdUnits) {
        super(available_time, max_playouts);
        this.moveString = generatorMoves;
        this.behavior = behavior;
        this.qtdUnits = qtdUnits;
        //this.CMABAI = new CmabAssymetricMCTS(utt);
        this.CMABAI = new CmabAssymetricMCTS(available_time, max_playouts, lookahead, max_depth, 0.3f, 
                                             0.0f, 0.4f, global_strategy, policyPlayout, 
                                             a_ef, true, utt, behavior, qtdUnits);
    }

    /**
     *
     * @param available_time 100 ms
     * @param max_playouts -1
     * @param lookahead 100 times
     * @param max_depth 10 (default) - 2 = 1-ply
     * @param police_Exp 0 = E-greedy and 1 = Alternate
     * @param policyPlayout = IA to playout simulation
     * @param a_ef = Evaluation function
     * @param global_strategy E_GREEDY = 0, UCB1 = 1, HC = 2, HC_ST = 3, CLUSTER
     * = 4, GA = 5, COMB = 6
     * @param utt Unit Type Table
     * @param abstraction size == 0 (none), size > 0 == abstraction.
     * @param generatorMoves
     */
    public CMABBuilder(int available_time, int max_playouts, int lookahead, int max_depth, int police_Exp,
            AI policyPlayout, EvaluationFunction a_ef, int global_strategy,
            UnitTypeTable utt, List<AI> abstraction, String generatorMoves) {
        super(available_time, max_playouts);
        this.moveString = generatorMoves;
        //eval process to build the AI
        if (police_Exp == 0) { //E-Greedy exploration
            switch (global_strategy) {
                case 0:
                    CMABAI = new CmabNaiveMCTS(available_time, max_playouts, lookahead, max_depth,
                            0.3f, 0.0f, 0.4f, global_strategy, policyPlayout, a_ef,
                            true, generatorMoves, utt);
                    System.out.println("NaiveMCTS E-Greedy " + generatorMoves);
                    break; //NaiveMCTS E- Greedy
                case 1:
                    CMABAI = new CmabNaiveMCTS(available_time, max_playouts, lookahead, max_depth,
                            0.3f, 0.0f, 0.4f, global_strategy, policyPlayout, a_ef,
                            true, generatorMoves, utt);
                    System.out.println("NaiveMCTS UCB1 " + generatorMoves);
                    break; //NaiveMCTS UCB1
                default:
                    CMABAI = new CmabNaiveMCTS(utt);
                    System.out.println("NaiveMCTS E-Greedy " + generatorMoves);
                    break;
            }

        } else { //alternate exploration
            switch (global_strategy) {
                case 0:
                    CMABAI = new CmabNaiveMCTS(available_time, max_playouts, lookahead, max_depth,
                            1.0f, 0.0f, 0.5f, global_strategy, policyPlayout, a_ef,
                            true, generatorMoves, utt);
                    System.out.println("NaiveMCTS E-Greedy " + generatorMoves);
                    break; //NaiveMCTS E- Greedy
                case 1:
                    CMABAI = new CmabNaiveMCTS(available_time, max_playouts, lookahead, max_depth,
                            1.0f, 0.0f, 0.5f, global_strategy, policyPlayout, a_ef,
                            true, generatorMoves, utt);
                    System.out.println("NaiveMCTS UCB1 " + generatorMoves);
                    break; //NaiveMCTS UCB1
                case 2:
                    CMABAI = new CmabAlternateMCTS(available_time, max_playouts, lookahead, max_depth,
                            1.0f, 0.0f, 0.5f, global_strategy, policyPlayout, a_ef,
                            true, generatorMoves, utt);
                    System.out.println("MCTS Alternate HC " + generatorMoves);
                    break; //MCTS Alternate HC
                default:
                    CMABAI = new CmabNaiveMCTS(utt);
                    System.out.println("NaiveMCTS E-Greedy " + generatorMoves);
                    break;
            }
        }

    }

    @Override
    public void reset() {
        CMABAI.reset();
    }

    @Override
    public PlayerAction getAction(int player, GameState gs) throws Exception {
        return CMABAI.getAction(player, gs);
    }

    @Override
    public AI clone() {
        return CMABAI.clone();
    }

    @Override
    public List<ParameterSpecification> getParameters() {
        return CMABAI.getParameters();
    }

    @Override
    public void startNewComputation(int player, GameState gs) throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void computeDuringOneGameFrame() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PlayerAction getBestActionSoFar() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String toString() {
        if(behavior != null){
            return CMABAI.toString() + "_"+behavior+"_"+qtdUnits;
        }else{
            return CMABAI.toString() + "_"+moveString+"_"+minSize+"_"+minPoint;
        }
    }

}
