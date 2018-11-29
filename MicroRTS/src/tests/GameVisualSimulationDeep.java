/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tests;

import PVAI.EconomyRush;
import PVAI.EconomyRushBurster;
import PVAI.WorkerRushPlusPlus;
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
import ai.evaluation.SimpleSqrtEvaluationFunction3;
import ai.mcts.naivemcts.NaiveMCTS;
import ai.portfolio.PortfolioAI;
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
public class GameVisualSimulationDeep {

    static String _nameStrategies = "", _enemy = "";
    static AI[] strategies = null;

    public static void main(String args[]) throws Exception {
        UnitTypeTable utt = new UnitTypeTable();
//        PhysicalGameState pgs = PhysicalGameState.load("maps/16x16/basesWorkers16x16.xml", utt);
        //PhysicalGameState pgs = PhysicalGameState.load("maps/8x8/basesWorkers8x8A.xml", utt);
        //PhysicalGameState pgs = PhysicalGameState.load("maps/16x16/basesWorkers16x16A.xml", utt);        
        //PhysicalGameState pgs = PhysicalGameState.load("maps/BWDistantResources32x32.xml", utt);
        //PhysicalGameState pgs = PhysicalGameState.load("maps/BroodWar/(4)BloodBath.scmB.xml", utt);
        //PhysicalGameState pgs = MapGenerator.basesWorkers8x8Obstacle();
        //PhysicalGameState pgs = PhysicalGameState.load("maps/8x8/FourBasesWorkers8x8.xml", utt);
        //PhysicalGameState pgs = PhysicalGameState.load("maps/16x16/TwoBasesBarracks16x16.xml", utt);
        //PhysicalGameState pgs = PhysicalGameState.load("maps/NoWhereToRun9x8.xml", utt);
        //PhysicalGameState pgs = PhysicalGameState.load("maps/DoubleGame24x24.xml", utt);
        PhysicalGameState pgs = PhysicalGameState.load("maps/NoWhereToRun9x8.xml", utt);

        GameState gs = new GameState(pgs, utt);
        int MAXCYCLES = 8000;
        int PERIOD = 20;
        boolean gameover = false;

        //AI ai1 = new RangedRush(utt);
        //AI ai1 = new PORangedRush(utt);
        //AI ai1 = new WorkerRush(utt);
        //AI ai1 = new LightRush(utt);
        //AI ai1 = new HeavyRush(utt);
        //AI ai1 = new POLightRush(utt);
        //AI ai1 = new EconomyRush(utt);        
        //AI ai1 = new EconomyRushBurster(utt);        
        //AI ai2 = new PassiveAI(utt);
        AI ai1 = new StrategyTactics(utt);
        //AI ai1 = new PortfolioAI(utt);
        //AI ai1 = new PVAI(utt);
        //AI ai1 = new WorkerRushPlusPlus(utt);
        //AI ai1 = new RandomBiasedAI(utt);
        AI ai2 = new PuppetSearchMCTS(utt);

   

        //método para fazer a troca dos players
        JFrame w = PhysicalGameStatePanel.newVisualizer(gs, 640, 640, false, PhysicalGameStatePanel.COLORSCHEME_BLACK);
//        JFrame w = PhysicalGameStatePanel.newVisualizer(gs,640,640,false,PhysicalGameStatePanel.COLORSCHEME_WHITE);

        long nextTimeToUpdate = System.currentTimeMillis() + PERIOD;
        do {
            if (System.currentTimeMillis() >= nextTimeToUpdate) {
                PlayerAction pa1 = ai1.getAction(0, gs);
                PlayerAction pa2 = ai2.getAction(1, gs);
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
        } while (!gameover && gs.getTime() < MAXCYCLES);

        System.out.println("Game Over");
    }


}
