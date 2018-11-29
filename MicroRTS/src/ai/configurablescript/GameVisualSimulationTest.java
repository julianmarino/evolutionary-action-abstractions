 /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.configurablescript;

import ai.core.AI;
import ai.evaluation.EvaluationFunction;
import ai.evaluation.SimpleEvaluationFunction;
import ai.*;
import ai.abstraction.WorkerRush;
import ai.abstraction.pathfinding.BFSPathFinding;
import ai.abstraction.pathfinding.FloodFillPathFinding;
import ai.abstraction.pathfinding.PathFinding;
import ai.mcts.naivemcts.NaiveMCTS;
import gui.PhysicalGameStatePanel;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import javax.swing.JFrame;
import rts.GameState;
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
        UnitTypeTable utt = new UnitTypeTable();
        PhysicalGameState pgs = PhysicalGameState.load("maps/16x16/basesWorkers16x16.xml", utt);
//        PhysicalGameState pgs = MapGenerator.basesWorkers8x8Obstacle();

        GameState gs = new GameState(pgs, utt);
        int MAXCYCLES = 5000;
        int PERIOD = 20;
        boolean gameover = false;
               
        ScriptsCreator sc=new ScriptsCreator(utt,300);
        ArrayList<BasicExpandedConfigurableScript> scriptsCompleteSet=sc.getScriptsCompleteSet();
        
        //AI ai1 = new WorkerRush(utt, new BFSPathFinding());  
//        AI ai1 = new PuppetSearchMCTS(
//                TIME, MAX_PLAYOUTS,
//                PUPPET_PLAN_TIME, PUPPET_PLAN_PLAYOUTS,
//                PLAYOUT_TIME, PLAYOUT_TIME,
//                new RandomBiasedAI(),
//                new BasicConfigurableScript(utt, getPathFinding()),
//                getEvaluationFunction());
//        AI ai2 = new RandomBiasedAI();
//        AI ai2 = new PuppetSearchMCTS(
//                TIME, MAX_PLAYOUTS,
//                PUPPET_PLAN_TIME, PUPPET_PLAN_PLAYOUTS,
//                PLAYOUT_TIME, PLAYOUT_TIME,
//                new RandomBiasedAI(),
//                new BasicExpandedConfigurableScript(utt, getPathFinding()),
//                getEvaluationFunction());
        
        AI ai1=new PassiveAI();
        AI ai2=scriptsCompleteSet.get(0);
        System.out.println("size complete set 	"+scriptsCompleteSet.size());

        JFrame w = PhysicalGameStatePanel.newVisualizer(gs,640,640,false,PhysicalGameStatePanel.COLORSCHEME_BLACK);
//        JFrame w = PhysicalGameStatePanel.newVisualizer(gs,640,640,false,PhysicalGameStatePanel.COLORSCHEME_WHITE);

        long nextTimeToUpdate = System.currentTimeMillis() + PERIOD;
        do{
            if (System.currentTimeMillis()>=nextTimeToUpdate) {
                PlayerAction pa1 = ai1.getAction(0, gs);
                PlayerAction pa2 = ai2.getAction(1, gs);
                gs.issueSafe(pa1);
                gs.issueSafe(pa2);

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
