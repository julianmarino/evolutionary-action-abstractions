/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.aiSelection;

import ai.asymmetric.PGS.*;
import ai.core.AI;
import ai.abstraction.LightRush;
import ai.configurablescript.BasicExpandedConfigurableScript;
import ai.configurablescript.ScriptsCreator;
import ai.evaluation.EvalSelection;
import gui.PhysicalGameStatePanel;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import rts.GameState;
import rts.PhysicalGameState;
import rts.PlayerAction;
import rts.units.UnitTypeTable;

/**
 *
 * @author santi
 */
public class GameVisualSimulationTest {

    static String _nameStrategies = "", _enemy = "";
    static AI[] strategies = null;

    public static void main(String args[]) throws Exception {
        UnitTypeTable utt = new UnitTypeTable();
        //PhysicalGameState pgs = PhysicalGameState.load("maps/16x16/basesWorkers16x16.xml", utt);
        //PhysicalGameState pgs =  PhysicalGameState.load("maps/64x64/SimplePathExplore64x64.xml", utt);
        //PhysicalGameState pgs = PhysicalGameState.load("maps/16x16/basesWorkers16x16A.xml", utt);        
        //PhysicalGameState pgs = PhysicalGameState.load("maps/BWDistantResources32x32.xml", utt);
        PhysicalGameState pgs = PhysicalGameState.load("maps/24x24/basesWorkers24x24A.xml", utt);
        //PhysicalGameState pgs = PhysicalGameState.load("maps/BroodWar/(4)BloodBath.scmB.xml", utt);
        //PhysicalGameState pgs = PhysicalGameState.load("maps/8x8/FourBasesWorkers8x8.xml", utt);
       //PhysicalGameState pgs = PhysicalGameState.load("maps/16x16/TwoBasesBarracks16x16.xml", utt);
       //PhysicalGameState pgs = PhysicalGameState.load("maps/NoWhereToRun9x8.xml", utt);
       //PhysicalGameState pgs = PhysicalGameState.load("maps/DoubleGame24x24.xml", utt);
        //PhysicalGameState pgs = MapGenerator.basesWorkers8x8Obstacle();
        //PhysicalGameState pgs = PhysicalGameState.load("maps/BroodWar/(4)Python.scxF.xml", utt);
      //PhysicalGameState pgs = PhysicalGameState.load("maps/24x24/basesWorkers24x24A_Barrack.xml", utt);
        

        GameState gs = new GameState(pgs, utt);
        int MAXCYCLES = 8000;
        int PERIOD = 20;
        boolean gameover = false;

        //monta os scripts
        ScriptsCreator sc=new ScriptsCreator(utt,300);
        ArrayList<BasicExpandedConfigurableScript> scriptsCompleteSet= sc.getScriptsMixReducedSet();
        //scripts ai1
        List<AI> scripts1 = new ArrayList<>();
        scripts1.add(scriptsCompleteSet.get(0));
        scripts1.add(scriptsCompleteSet.get(1));
        scripts1.add(scriptsCompleteSet.get(2));
        scripts1.add(scriptsCompleteSet.get(3));
        //scripts ai2
        List<AI> scripts2 = new ArrayList<>();
        
        scripts2.add(scriptsCompleteSet.get(201));
        scripts2.add(scriptsCompleteSet.get(232));
        scripts2.add(scriptsCompleteSet.get(188));
        scripts2.add(scriptsCompleteSet.get(151));
        scripts2.add(scriptsCompleteSet.get(26));
        //scripts2.add(new WorkerRush(utt));
        //scripts2.add(new RangedRush(utt));
        //scripts2.add(new LightRush(utt));
        //scripts2.add(new HeavyRush(utt));
        
        
        
        //AI ai1 = new PassiveAI();
        AI ai2 = new PGSSCriptChoice(utt, scripts1);
        //AI ai1 = new WorkerRush(utt);
        //AI ai1 = new LightRush(utt);
        //AI ai1 = new StrategyTactics(utt);
        //AI ai1 = new PuppetSearchMCTS(utt);
        
        AI ai1 = new PGSSCriptChoice(utt, scripts2);
        
        
        System.out.println("---------AI's---------");
        System.out.println("AI 1 = "+ai1.toString());
        System.out.println("AI 2 = "+ai2.toString()+"\n");        
        
        //método para fazer a troca dos players
        JFrame w = PhysicalGameStatePanel.newVisualizer(gs, 640, 640, false, PhysicalGameStatePanel.COLORSCHEME_BLACK);
//        JFrame w = PhysicalGameStatePanel.newVisualizer(gs,640,640,false,PhysicalGameStatePanel.COLORSCHEME_WHITE);
        long startTime = System.currentTimeMillis();
        long nextTimeToUpdate = System.currentTimeMillis() + PERIOD;
        do {
            if (System.currentTimeMillis() >= nextTimeToUpdate) {
                startTime = System.currentTimeMillis();
                PlayerAction pa1 = ai1.getAction(0, gs);  
                //System.out.println("Tempo de execução P1="+(startTime = System.currentTimeMillis() - startTime));
                
                startTime = System.currentTimeMillis();
                PlayerAction pa2 = ai2.getAction(1, gs);
                //System.out.println("Tempo de execução P2="+(startTime = System.currentTimeMillis() - startTime));
                
                gs.issueSafe(pa1);
                gs.issueSafe(pa2);

                // simulate:
                gameover = gs.cycle();
                w.repaint();
                nextTimeToUpdate += PERIOD;
                
                //teste ltd2
                EvalSelection e = new EvalSelection();
                //System.out.println("EvalSelection ="+ e.evaluate(0, 1, gs));
            } else {
                try {
                    Thread.sleep(1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
           /* PhysicalGameState physical = gs.getPhysicalGameState();
            System.out.println("---------TIME---------");
            System.out.println(gs.getTime());
            for (Unit u : physical.getUnits()) {
                if (u.getPlayer() == 1) {
                    System.out.println("Player 1: Unity - " + u.toString());
                }
                else if (u.getPlayer() == 0) {
                     System.out.println("Player 0: Unity - " + u.toString());
                } 
            }
            */
        } while (!gameover && gs.getTime() < MAXCYCLES);

        System.out.println("Game Over");
    }


}
