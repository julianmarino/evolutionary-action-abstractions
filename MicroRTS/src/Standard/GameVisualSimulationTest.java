 /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Standard;

import ai.core.AI;
import ai.core.ContinuingAI;
import ai.core.PseudoContinuingAI;
import ai.evaluation.SimpleEvaluationFunction;
import ai.evaluation.SimpleSqrtEvaluationFunction3;
import ai.*;
import ai.abstraction.HeavyRush;
import ai.abstraction.LightRush;
import ai.abstraction.RangedRush;
import ai.abstraction.WorkerRush;
import ai.abstraction.partialobservability.POHeavyRush;
import ai.abstraction.partialobservability.POLightRush;
import ai.abstraction.partialobservability.PORangedRush;
import ai.abstraction.partialobservability.POWorkerRush;
import ai.abstraction.pathfinding.BFSPathFinding;
import ai.abstraction.pathfinding.FloodFillPathFinding;
import ai.mcts.naivemcts.NaiveMCTS;
import ai.puppet.PuppetNoPlan;
import ai.puppet.PuppetSearchAB;
import ai.puppet.SingleChoiceConfigurableScript;
import gui.PhysicalGameStatePanel;
import java.io.OutputStreamWriter;
import javax.swing.JFrame;
import rts.GameState;
import rts.PartiallyObservableGameState;
import rts.PhysicalGameState;
import rts.PlayerAction;
import rts.units.UnitTypeTable;
import util.XMLWriter;

/**
 *
 * @author santi
 */
public class GameVisualSimulationTest {
    public static void main(String args[]) throws Exception {
        UnitTypeTable utt = new UnitTypeTable(
        		UnitTypeTable.VERSION_ORIGINAL_FINETUNED,
        		//UnitTypeTable.MOVE_CONFLICT_RESOLUTION_CANCEL_ALTERNATING
        		UnitTypeTable.MOVE_CONFLICT_RESOLUTION_CANCEL_BOTH
        		);
        //PhysicalGameState pgs = PhysicalGameState.load("../microrts/maps/8x8/basesWorkers8x8A.xml", utt);
        //int MAXCYCLES = 3000;
        PhysicalGameState pgs = PhysicalGameState.load("../microrts/maps/16x16/basesWorkers16x16A.xml", utt);
        int MAXCYCLES = 4000;
//        PhysicalGameState pgs = PhysicalGameState.load("../microrts/maps/BWDistantResources32x32.xml", utt);
//        int MAXCYCLES = 6000;
       // PhysicalGameState pgs = PhysicalGameState.load("../microrts/maps/BroodWar/(4)BloodBath.scmB.xml", utt);
       // int MAXCYCLES = 8000;
//        PhysicalGameState pgs = MapGenerator.basesWorkers8x8Obstacle();

        GameState gs = new GameState(pgs, utt);
        
        int PERIOD = 20;
        boolean gameover = false;
        
        AI ai1 = new POLightRush(utt, new BFSPathFinding()); 
//        AI ai2 = new WorkerRush(utt, new BFSPathFinding()); 
        //AI ai1 = new NaiveMCTS(utt);  
        AI ai2 = new ContinuingAI(new StrategyTactics(utt));
//        AI ai2 = new ContinuingAI(new PuppetNoPlan(new PuppetSearchAB(
//                100, -1,
//                -1, -1,
//                100,
//                new SingleChoiceConfigurableScript(new FloodFillPathFinding(),
//                        new AI[]{
//                                new POWorkerRush(utt, new FloodFillPathFinding()),
//                                new POLightRush(utt, new FloodFillPathFinding()),
//                                new PORangedRush(utt, new FloodFillPathFinding()),
//                                new POHeavyRush(utt, new FloodFillPathFinding()),
//                }),
//                new CombinedEvaluation())));
        JFrame w = PhysicalGameStatePanel.newVisualizer(gs,640,640,false,PhysicalGameStatePanel.COLORSCHEME_BLACK);
//        JFrame w = PhysicalGameStatePanel.newVisualizer(gs,640,640,false,PhysicalGameStatePanel.COLORSCHEME_WHITE);

        long nextTimeToUpdate = System.currentTimeMillis() + PERIOD;
        do{
            if (System.currentTimeMillis()>=nextTimeToUpdate) {
                PlayerAction pa1 = ai1.getAction(0, gs);
                PlayerAction pa2 = ai2.getAction(1, gs);
//                System.out.println(pa1);
//                System.out.println(pa2);
                gs.issueSafe(pa1);
                gs.issueSafe(pa2);
//                System.out.println(gs);
                // simulate:
                gameover = gs.cycle();
                w.repaint();
                nextTimeToUpdate+=PERIOD;
            } else {
                try {
                    Thread.sleep(1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }while(!gameover && gs.getTime()<MAXCYCLES);
        
        System.out.println("Game Over");
    }    
}
