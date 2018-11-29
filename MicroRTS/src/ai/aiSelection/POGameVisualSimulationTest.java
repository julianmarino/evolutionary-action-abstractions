 /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.aiSelection;

import ai.core.AI;
import ai.*;
import ai.abstraction.LightRush;
import ai.abstraction.partialobservability.POLightRush;
import ai.abstraction.pathfinding.AStarPathFinding;
import ai.abstraction.pathfinding.BFSPathFinding;
import ai.configurablescript.BasicExpandedConfigurableScript;
import ai.configurablescript.POBasicExpandedConfigurableScript;
import ai.configurablescript.POScriptsCreator;
import ai.configurablescript.ScriptsCreator;
import gui.PhysicalGameStatePanel;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
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
public class POGameVisualSimulationTest {
    public static void main(String args[]) throws Exception {
        UnitTypeTable utt = new UnitTypeTable();
        PhysicalGameState pgs = PhysicalGameState.load("maps/24x24/basesWorkers24x24A.xml", utt);
//        PhysicalGameState pgs = MapGenerator.basesWorkers8x8Obstacle();

        GameState gs = new GameState(pgs, utt);
        int MAXCYCLES = 5000;
        int PERIOD = 20;
        boolean gameover = false;
        
//        AI ai1 = new RandomAI();
//        AI ai1 = new WorkerRush(UnitTypeTable.utt, new BFSPathFinding());
        //AI ai1 = new POLightRush(utt, new BFSPathFinding());
        AI ai1 = new POLightRush(utt, new AStarPathFinding());
//        AI ai1 = new RangedRush(UnitTypeTable.utt, new GreedyPathFinding());
//        AI ai1 = new ContinuingNaiveMC(PERIOD, 200, 0.33f, 0.2f, new RandomBiasedAI(), new SimpleEvaluationFunction());


        POScriptsCreator sc=new POScriptsCreator(utt,300);
        ArrayList<POBasicExpandedConfigurableScript> scriptsCompleteSet= sc.getScriptsMixReducedSet();
        
        AI ai2 = scriptsCompleteSet.get(102);
        //AI ai2 = scriptsCompleteSet.get(0);
        //BasicExpandedConfigurableScript(6,18,0,0,1,2,2,-1,-1,)
        //AI ai2 = new POBasicExpandedConfigurableScript(utt, (new AStarPathFinding()), 18, 0, 0, 1, 2, -1, 2, -1, 11);
        //AI ai2 = new RandomBiasedAI();
//        AI ai2 = new LightRush();
        
        //XMLWriter xml = new XMLWriter(new OutputStreamWriter(System.out));
        //pgs.toxml(xml);
        //xml.flush();

        JFrame w = PhysicalGameStatePanel.newVisualizer(gs,640,640, true);

        long nextTimeToUpdate = System.currentTimeMillis() + PERIOD;
        do{
            if (System.currentTimeMillis()>=nextTimeToUpdate) {
                PlayerAction pa1 = ai1.getAction(0, new PartiallyObservableGameState(gs,0));
                PlayerAction pa2 = ai2.getAction(1, new PartiallyObservableGameState(gs,1));
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
