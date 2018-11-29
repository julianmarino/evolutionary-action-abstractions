/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tests;

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
import ai.abstraction.partialobservability.POWorkerRush;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
public class torneioLeve {

    static String _nameStrategies = "", _enemy = "";
    static AI[] strategies = null;

    public static void main(String args[]) throws Exception {
        int iAi1 = Integer.parseInt(args[0]);
        int iAi2 = Integer.parseInt(args[1]);
        int map = Integer.parseInt(args[2]);
       //int iAi1 = 0;
       //int iAi2 = 3;
       //int map = 0;
        List<String> maps = new ArrayList<>(Arrays.asList(
                "maps/BroodWar/(4)CircuitBreaker.scxB.xml",
                "maps/BroodWar/(4)BloodBath.scmB.xml"
        ));
        List<Integer> lCycles = new ArrayList<Integer>();

        int[] cycles = {12000, 12000, 12000, 12000, 12000};

        for (int cycle : cycles) {
            lCycles.add(cycle);
        }

        UnitTypeTable utt = new UnitTypeTable();
        int PERIOD = 20;
        boolean gameover = false;

        List<AI> ais = new ArrayList<>(Arrays.asList(                
                new PuppetSearchMCTS(utt)
        ));

        PhysicalGameState pgs = PhysicalGameState.load(maps.get(map), utt);
        int MAXCYCLES = lCycles.get(map);
        GameState gs = new GameState(pgs, utt);
        AI ai1 = ais.get(iAi1);
        AI ai2 = ais.get(iAi2);

        //troca para teste
        
        if (Integer.decode(args[3]) == 1) {
            AI ai3 = ai2;
            ai2 = ai1;
            ai1 = ai3;
        }
        
        //JFrame w = PhysicalGameStatePanel.newVisualizer(gs, 640, 640, false, PhysicalGameStatePanel.COLORSCHEME_BLACK);

        long startTime = System.currentTimeMillis();
        long nextTimeToUpdate = System.currentTimeMillis() + PERIOD;
        do {
            if (System.currentTimeMillis() >= nextTimeToUpdate) {
                PlayerAction pa1 = ai1.getAction(0, gs);
                PlayerAction pa2 = ai2.getAction(1, gs);
                gs.issueSafe(pa1);
                gs.issueSafe(pa2);

                gameover = gs.cycle();
                //w.repaint();
                nextTimeToUpdate += PERIOD;
            } else {
                try {
                    Thread.sleep(1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } while (!gameover && gs.getTime() < MAXCYCLES);

        try {
            FileOutputStream bytes = new FileOutputStream("results/match_"+ai1.toString()+"_"+ai2.toString()+"_"+System.currentTimeMillis() , true);
            OutputStreamWriter chars = new OutputStreamWriter(bytes);
            BufferedWriter strings = new BufferedWriter(chars);
            try {
                strings.write("");
                strings.flush();
                strings.write(ai1.toString());
                strings.newLine();
                strings.write(ai2.toString());
                strings.newLine();
                strings.write(Integer.toString(gs.winner()));
                
                
                strings.newLine();
                strings.flush();
                
            } catch (IOException e) {
                e.printStackTrace();
            }
            strings.close();
            chars.close();
            bytes.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        System.out.println("Game Over");
        System.exit(0);
    }

}
