/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.asymmetric.PGS;

import tests.*;
import PVAI.EconomyRush;
import PVAI.EconomyRushBurster;
import PVAI.WorkerDefense;
import PVAI.RangedDefense;
import Standard.CombinedEvaluation;
import Standard.StrategyTactics;
import ai.core.AI;
import ai.*;
import ai.abstraction.HeavyRush;
import ai.abstraction.LightRush;
import ai.abstraction.RangedRush;
import ai.abstraction.WorkerRush;
import ai.abstraction.combat.Cluster;
import ai.abstraction.combat.KitterDPS;
import ai.abstraction.combat.NOKDPS;
import ai.abstraction.partialobservability.POLightRush;
import ai.abstraction.partialobservability.PORangedRush;
import ai.abstraction.pathfinding.AStarPathFinding;
import ai.abstraction.pathfinding.BFSPathFinding;
import ai.aiSelection.AlphaBetaSearch.AlphaBetaSearch;
import ai.asymmetric.GAB.GAB_oldVersion;
import ai.asymmetric.GAB.SandBox.AlphaBetaSearchAbstract;
import ai.asymmetric.GAB.SandBox.GAB;
import ai.asymmetric.GAB.SandBox.GAB_SandBox_Parcial_State;
import ai.asymmetric.PGS.SandBox.PGSmRTS_SandBox;
import ai.asymmetric.SAB.SAB;
import ai.asymmetric.SAB.SAB_seed;
import ai.asymmetric.SSS.SSSmRTS;
import ai.asymmetric.SSS.SSSmRTSScriptChoice;
import ai.cluster.CABA;
import ai.cluster.CABA_Enemy;
import ai.cluster.CIA;
import ai.cluster.CIA_Enemy;
import ai.cluster.CIA_EnemyEuclidieanInfluence;
import ai.cluster.CIA_EnemyWithTime;
import ai.cluster.CIA_PlayoutCluster;
import ai.cluster.CIA_PlayoutPower;
import ai.cluster.CIA_PlayoutTemporal;
import ai.cluster.CIA_TDLearning;
import ai.configurablescript.BasicExpandedConfigurableScript;
import ai.configurablescript.ScriptsCreator;
import ai.evaluation.EvaluationFunctionForwarding;
import ai.evaluation.LanchesterEvaluationFunction;
import ai.evaluation.SimpleSqrtEvaluationFunction3;
import ai.mcts.naivemcts.NaiveMCTS;
import ai.minimax.ABCD.IDABCD;
import ai.portfolio.PortfolioAI;
import ai.puppet.PuppetSearchMCTS;
import gui.PhysicalGameStatePanel;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import rts.GameState;
import rts.PhysicalGameState;
import rts.Player;
import rts.PlayerAction;
import rts.units.Unit;
import rts.units.UnitTYpeTableBattle;
import rts.units.UnitTypeTable;
import static tests.ClusterTesteLeve.decodeScripts;
import util.XMLWriter;

/**
 *
 * @author santi
 */
public class GameVisualSimulationSetCover {

    static String _nameStrategies = "", _enemy = "";
    static AI[] strategies = null;

    public static void main(String args[]) throws Exception {
        UnitTypeTable utt = new UnitTypeTable();
        //UnitTypeTable utt = new UnitTYpeTableBattle();
        //PhysicalGameState pgs = PhysicalGameState.load("maps/16x16/basesWorkers16x16.xml", utt);
        //PhysicalGameState pgs = PhysicalGameState.load("maps/8x8/basesWorkers8x8A.xml", utt);
        PhysicalGameState pgs = PhysicalGameState.load("maps/16x16/basesWorkers16x16A.xml", utt);        
        //PhysicalGameState pgs = PhysicalGameState.load("maps/BWDistantResources32x32.xml", utt);
        //PhysicalGameState pgs = PhysicalGameState.load("maps/32x32/basesWorkers32x32A.xml", utt);
        //PhysicalGameState pgs = PhysicalGameState.load("maps/24x24/basesWorkers24x24A.xml", utt);
        //PhysicalGameState pgs = PhysicalGameState.load("maps/BroodWar/(4)BloodBath.scmB.xml", utt);
        //PhysicalGameState pgs = PhysicalGameState.load("maps/8x8/FourBasesWorkers8x8.xml", utt);
      //PhysicalGameState pgs = PhysicalGameState.load("maps/16x16/TwoBasesBarracks16x16.xml", utt);
       //PhysicalGameState pgs = PhysicalGameState.load("maps/NoWhereToRun9x8.xml", utt);
       //PhysicalGameState pgs = PhysicalGameState.load("maps/DoubleGame24x24.xml", utt);
        //PhysicalGameState pgs = MapGenerator.basesWorkers8x8Obstacle();
        //testes GAB
        //PhysicalGameState pgs = PhysicalGameState.load("maps/8x8/basesWorkers8x8A.xml", utt);
        //PhysicalGameState pgs = PhysicalGameState.load("maps/16x16/basesWorkers16x16A.xml", utt);  
       //PhysicalGameState pgs = PhysicalGameState.load("maps/BWDistantResources32x32.xml", utt);  
       //PhysicalGameState pgs = PhysicalGameState.load("maps/NoWhereToRun9x8.xml", utt);
       //PhysicalGameState pgs = PhysicalGameState.load("maps/DoubleGame24x24.xml", utt);
       //combate tests 
       //PhysicalGameState pgs = PhysicalGameState.load("maps/melee14x12Mixed18.xml", utt);
       //PhysicalGameState pgs = PhysicalGameState.load("maps/battleMaps/melee2x2Mixed_map8x8.xml", utt);
       //PhysicalGameState pgs = PhysicalGameState.load("maps/melee4x4light2.xml", utt);
       //PhysicalGameState pgs = PhysicalGameState.load("maps/battleMaps/ClusterTestMap_map8x8.xml", utt);
       //combat tests CIA
       //PhysicalGameState pgs = PhysicalGameState.load("maps/battleMaps/8x8/4x4Mixed_combatRangedProtection_map8x8.xml", utt);
       //PhysicalGameState pgs = PhysicalGameState.load("maps/battleMaps/8x8/4x4Mixed_crazyPosition_map8x8.xml", utt);
       //PhysicalGameState pgs = PhysicalGameState.load("maps/battleMaps/8x8/4x4Mixed_map8x8.xml", utt);
       //PhysicalGameState pgs = PhysicalGameState.load("maps/battleMaps/8x8/four_goups_Battle_8x8.xml", utt);
       //PhysicalGameState pgs = PhysicalGameState.load("maps/battleMaps/8x8/lineBattle8x8.xml", utt);
       //PhysicalGameState pgs = PhysicalGameState.load("maps/battleMaps/8x8/melee2x2Mixed_map8x8.xml", utt);
       //PhysicalGameState pgs = PhysicalGameState.load("maps/battleMaps/16x16/melee16x16Mixed8.xml", utt);
        //PhysicalGameState pgs = PhysicalGameState.load("maps/battleMaps/16x16/fourGroupsWithBlocks16x16.xml", utt);
        //PhysicalGameState pgs = PhysicalGameState.load("maps/battleMaps/8x8/1x1.xml", utt);
        //PhysicalGameState pgs = PhysicalGameState.load("maps/battleMaps/8x8/mapTeste.xml", utt);
        //PhysicalGameState pgs = PhysicalGameState.fromJSON(readFile("/home/rubens/SetCoverProblemforMRTS/SetCoverProblemRTS/state_0.txt"), utt);
        GameState gs = new GameState(pgs, utt);
        //GameState gs = GameState.fromJSON(readFile("/home/rubens/SetCoverProblemforMRTS/state_34.txt"), utt);
        
        int MAXCYCLES = 8000;
        int PERIOD = 20;
        boolean gameover = false;

        
        ScriptsCreator sc = new ScriptsCreator(utt,300);
        ArrayList<BasicExpandedConfigurableScript> scriptsCompleteSet = sc.getScriptsMixReducedSet();
        
        AI ai1 = scriptsCompleteSet.get(93);
        
        
        AI ai2 = scriptsCompleteSet.get(246);
        
        
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
                System.out.println("Action A1 ="+ pa1.toString());
                
                startTime = System.currentTimeMillis();
                PlayerAction pa2 = ai2.getAction(1, gs);
                if( (System.currentTimeMillis() - startTime) >0){
                   System.out.println("Tempo de execução P2="+(startTime = System.currentTimeMillis() - startTime));
                }
                System.out.println("Action A2 ="+ pa2.toString());
                
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
        System.out.println("Winner " + Integer.toString(gs.winner()));
        System.out.println("Game Over");
    }
    
    public static List<AI> decodeScripts(UnitTypeTable utt, String sScripts) {
        
        //decompõe a tupla
        ArrayList<Integer> iScriptsAi1 = new ArrayList<>();
        String[] itens = sScripts.split(";");

        for (String element : itens) {
            iScriptsAi1.add(Integer.decode(element));
        }
        
        List<AI> scriptsAI = new ArrayList<>();

        ScriptsCreator sc = new ScriptsCreator(utt,300);
        ArrayList<BasicExpandedConfigurableScript> scriptsCompleteSet = sc.getScriptsMixReducedSet();

        iScriptsAi1.forEach((idSc) -> {
            scriptsAI.add(scriptsCompleteSet.get(idSc));
        });

        return scriptsAI;
    }
    
    public static String readFile(String fileName) throws IOException {
	    BufferedReader br = new BufferedReader(new FileReader(fileName));
	    try {
	        StringBuilder sb = new StringBuilder();
	        String line = br.readLine();

	        while (line != null) {
	            sb.append(line);
	            sb.append("\n");
	            line = br.readLine();
	        }
	        return sb.toString();
	    } finally {
	        br.close();
	    }
	}


}
