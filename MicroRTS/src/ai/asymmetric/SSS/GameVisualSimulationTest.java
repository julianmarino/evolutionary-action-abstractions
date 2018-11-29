/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.asymmetric.SSS;

import tests.*;
import PVAI.EconomyRush;
import PVAI.EconomyRushBurster;
import PVAI.WorkerRushPlusPlus;
import PVAI.WorkerDefense;
import PVAI.RangedDefense;
import Standard.StrategyTactics;
import ai.core.AI;
import ai.*;
import ai.abstraction.HeavyRush;
import ai.abstraction.LightRush;
import ai.abstraction.RangedRush;
import ai.abstraction.WorkerRush;
import ai.abstraction.partialobservability.POLightRush;
import ai.abstraction.partialobservability.PORangedRush;
import ai.abstraction.pathfinding.BFSPathFinding;
import ai.asymmetric.GAB.GAB_oldVersion;
import ai.asymmetric.IDABCD.IDABCDAsymmetric;
import ai.asymmetric.PGS.PGSmRTS;
import ai.evaluation.SimpleSqrtEvaluationFunction3;
import ai.mcts.naivemcts.NaiveMCTS;
import ai.minimax.ABCD.IDABCD;
import ai.portfolio.PortfolioAI;
import ai.portfolio.portfoliogreedysearch.PGSAI;
import ai.puppet.PuppetSearchMCTS;
import gui.PhysicalGameStatePanel;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import javax.swing.JFrame;
import rts.GameState;
import rts.PhysicalGameState;
import rts.Player;
import rts.PlayerAction;
import rts.units.Unit;
import rts.units.UnitTypeTable;
import util.XMLWriter;

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
        PhysicalGameState pgs = PhysicalGameState.load("maps/8x8/basesWorkers8x8A.xml", utt);
        //PhysicalGameState pgs = PhysicalGameState.load("maps/16x16/basesWorkers16x16A.xml", utt);        
        //PhysicalGameState pgs = PhysicalGameState.load("maps/BWDistantResources32x32.xml", utt);
        //PhysicalGameState pgs = PhysicalGameState.load("maps/24x24/basesWorkers24x24A.xml", utt);
        //PhysicalGameState pgs = PhysicalGameState.load("maps/BroodWar/(4)BloodBath.scmB.xml", utt);
        //PhysicalGameState pgs = PhysicalGameState.load("maps/8x8/FourBasesWorkers8x8.xml", utt);
       //PhysicalGameState pgs = PhysicalGameState.load("maps/16x16/TwoBasesBarracks16x16.xml", utt);
       //PhysicalGameState pgs = PhysicalGameState.load("maps/NoWhereToRun9x8.xml", utt);
       //PhysicalGameState pgs = PhysicalGameState.load("maps/DoubleGame24x24.xml", utt);
        //PhysicalGameState pgs = MapGenerator.basesWorkers8x8Obstacle();
        //PhysicalGameState pgs = PhysicalGameState.load("maps/BroodWar/(4)Python.scxA.xml", utt);

        GameState gs = new GameState(pgs, utt);
        int MAXCYCLES = 20000;
        int PERIOD = 20;
        boolean gameover = false;

        //AI ai1 = new StrategyTactics(utt);
        //AI ai1 = new RangedRush(utt);
        //AI ai1 = new WorkerRush(utt);
        AI ai1 = new LightRush(utt);
        //AI ai1 = new HeavyRush(utt);
        //AI ai1 = new POLightRush(utt);
        //AI ai1 = new EconomyRush(utt);        
        //AI ai1 = new RangedDefense(utt);
        //AI ai1 = new EconomyRushBurster(utt);        
        //AI ai1 = new PassiveAI(utt);
        //AI ai1 = new NaiveMCTS(utt);
        //AI ai1 = new PortfolioAI(utt);
        //AI ai1 = new PVAI(utt);
        //AI ai1 = new WorkerRushPlusPlus(utt);
        //AI ai1 = new RandomBiasedAI(utt);
        //AI ai1 = new PuppetSearchMCTS(utt);
        //AI ai1 = new PortfolioAI(utt);
        //AI ai1 = new PGSmRTS(utt);
        //AI ai1 = new PGSAI(utt);
        //AI ai1 = new IDABCD(utt);
        //AI ai1 = new PVAIML_SL_ONE_STRATEGY(utt);
        
        
        //AI ai2 = new SCV(utt);
        //AI ai2 = new EconomyRush(utt);      
        //AI ai2 = new LightRush(utt);
        //AI ai2 = new RangedDefense(utt);
        //AI ai2 = new PVAI(utt);
        //AI ai2 = new PVAIML_onlyEnemy(utt);
        //AI ai2 = new PVAIML_SLMS(utt);
        //AI ai2 = new PVAIML_NaiveMS(utt);
        //AI ai2 = new PVAIML_ED(utt);
        //AI ai2 = new PVAIML_SLFW(utt);
        //AI ai2 = new PVAIML_SL(utt);
        //AI ai2 = new PVAIML_Naive(utt);
        //AI ai2 = new PVAIML_NaiveFW(utt);
        //AI ai2 = new PVAIML_FW(utt);
        //AI ai2 = new PVAIML_EDP(utt);
        //AI ai2 = new PVAIML_SLFWMS(utt);
        //AI ai2 = new PVAICluster(4, utt, "EconomyRush(AStarPathFinding)");
        AI ai2 = new SSSmRTS(utt);
        //AI ai2 = new PGSmRTS(utt);
        //AI ai2 = new ABCD(utt);
        //AI ai2 = new PassiveAI(utt);
        //AI ai2 = new GAB_ABActionGeneration(utt);
        //AI ai2 = new PVAICluster(2, utt, ai1.toString(), ai1.clone());
        
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
                if((System.currentTimeMillis() - startTime )>100){
                   //System.out.println("Tempo de execução P1="+(startTime = System.currentTimeMillis() - startTime));
                }
                
                startTime = System.currentTimeMillis();
                PlayerAction pa2 = ai2.getAction(1, gs);
                if((System.currentTimeMillis() - startTime )>120){
                    System.out.println("Tempo de execução P2="+(startTime = System.currentTimeMillis() - startTime));
                }
                
                gs.issueSafe(pa1);
                gs.issueSafe(pa2);

                // simulate:
                gameover = gs.cycle();
                w.repaint();
                nextTimeToUpdate += PERIOD;
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
