/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tests;

import PVAI.EMRDeterministico;
import ai.asymmetric.SSS.*;
import tests.*;
import PVAI.EconomyRush;
import PVAI.EconomyMilitaryRush;
import PVAI.EconomyRushBurster;
import PVAI.HeavyDefense;
import PVAI.LightDefense;
import PVAI.WorkerRushPlusPlus;
import PVAI.WorkerDefense;
import PVAI.RangedDefense;
import Standard.StrategyTactics;
import ai.core.AI;
import ai.*;
import ai.CMAB.CmabAssymetricMCTS;
import ai.abstraction.HeavyRush;
import ai.abstraction.LightRush;
import ai.abstraction.RangedRush;
import ai.abstraction.WorkerRush;
import ai.abstraction.partialobservability.POHeavyRush;
import ai.abstraction.partialobservability.POLightRush;
import ai.abstraction.partialobservability.PORangedRush;
import ai.abstraction.partialobservability.POWorkerRush;
import ai.abstraction.pathfinding.AStarPathFinding;
import ai.abstraction.pathfinding.BFSPathFinding;
import ai.ahtn.AHTNAI;
import ai.aiSelection.AlphaBetaSearch.AlphaBetaSearch;
import ai.aiSelection.IDABCD.ABSelection;
import ai.asymmetric.GAB.GAB_oldVersion;
import ai.asymmetric.GAB.SandBox.GAB;
import ai.asymmetric.IDABCD.IDABCDAsymmetric;
import ai.asymmetric.PGS.PGSSCriptChoice;
import ai.asymmetric.PGS.PGSSelection;
import ai.asymmetric.PGS.PGSmRTS;
import ai.asymmetric.SAB.SAB;
import ai.asymmetric.SAB.SAB_oldVersion;
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
import ai.competition.capivara.Capivara;
import ai.competition.tiamat.Tiamat;
import ai.configurablescript.BasicExpandedConfigurableScript;
import ai.configurablescript.POBasicExpandedConfigurableScript;
import ai.configurablescript.POScriptsCreator;
import ai.configurablescript.ScriptsCreator;
import ai.evaluation.SimpleSqrtEvaluationFunction3;
import ai.mcts.believestatemcts.BS3_NaiveMCTS;
import ai.mcts.naivemcts.NaiveMCTS;
import ai.minimax.ABCD.IDABCD;
import ai.portfolio.PortfolioAI;
import ai.portfolio.portfoliogreedysearch.PGSAI;
import ai.puppet.BasicConfigurableScript;
import ai.puppet.PuppetSearchMCTS;
import ai.puppet.PuppetSearchMCTSBasicScripts;
import ai.scv.SCV;
import ai.scv.SCVPlus;
import ai.utalca.UTalcaBot;
import gui.PhysicalGameStatePanel;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
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
import static util.SOA.RoundRobinClusterLeve.decodeScripts;
import util.XMLWriter;

/**
 *
 * @author Rubens
 */
public class ClusterTesteLeve_GABSAB_3Scr {

    public static void main(String args[]) throws Exception {
        int iAi1 = Integer.parseInt(args[0]);
        int iAi2 = Integer.parseInt(args[1]);
        int map = Integer.parseInt(args[2]);

        Instant timeInicial = Instant.now();
        Duration duracao;

        List<String> maps = new ArrayList<>(Arrays.asList(
                "maps/8x8/basesWorkers8x8A.xml",
                "maps/NoWhereToRun9x8.xml",
                "maps/16x16/basesWorkers16x16A.xml",
                "maps/16x16/TwoBasesBarracks16x16.xml",
                "maps/24x24/basesWorkers24x24A.xml",
                "maps/DoubleGame24x24.xml",
                "maps/BWDistantResources32x32.xml",
                "maps/32x32/basesWorkers32x32A.xml",
                "maps/BroodWar/(4)BloodBath.scmB.xml",
                "maps/BroodWar/(4)BloodBath.scmD.xml",
                "maps/BroodWar/(4)EmpireoftheSun.scmC.xml",
                "maps/BroodWar/(4)CircuitBreaker.scxF.xml",
                "maps/BroodWar/(4)Fortress.scxA.xml"
        ));

        //UnitTypeTable utt = new UnitTYpeTableBattle();
        UnitTypeTable utt = new UnitTypeTable();
        PhysicalGameState pgs = PhysicalGameState.load(maps.get(map), utt);

        GameState gs = new GameState(pgs, utt);
        int MAXCYCLES = 12000;
        int PERIOD = 20;
        boolean gameover = false;

        if (pgs.getHeight() == 8) {
            MAXCYCLES = 4000;
        }
        if (pgs.getHeight() == 16) {
            MAXCYCLES = 5000;
        }
        if (pgs.getHeight() == 24) {
            MAXCYCLES = 6000;
        }
        if (pgs.getHeight() == 32) {
            MAXCYCLES = 7000;
        }
        if (pgs.getHeight() == 64) {
            MAXCYCLES = 12000;
        }

        List<AI> ais = new ArrayList<>(Arrays.asList(
               new AHTNAI(utt),
               new NaiveMCTS(utt),
               new PuppetSearchMCTS(utt),
               new PuppetSearchMCTSBasicScripts(utt),
               new StrategyTactics(utt),
               new PGSSCriptChoice(utt, decodeScripts(utt, "1;2;3;"), "PGS"),
               new SSSmRTSScriptChoice(utt, decodeScripts(utt, "1;2;3;"), "SSS"),
               new BasicExpandedConfigurableScript(utt, new AStarPathFinding(), 18,0,0,1,2,2,-1,-1,4), //lr
               new BasicExpandedConfigurableScript(utt, new AStarPathFinding(), 18,0,0,1,2,2,-1,-1,5), //HR
               new BasicExpandedConfigurableScript(utt, new AStarPathFinding(), 18,0,0,1,2,2,-1,-1,6), //RR
               new BasicExpandedConfigurableScript(utt, new AStarPathFinding(), 18,0,0,1,2,2,-1,-1,3), //WR
               new SCVPlus(utt)
        ));
        //add GAB e SAB by map settings
        switch(maps.get(map)){
            case "maps/8x8/basesWorkers8x8A.xml" :
                ais.add(12, new GAB(utt, 2, 3)); //3 ManagerFather
                ais.add(13, new SAB(utt, 1, 5)); //8 ManagerLessLife
                break;
            case     "maps/8x8/FourBasesWorkers8x8.xml" :                
                ais.add(12, new GAB(utt, 2, 3)); //3 ManagerFather
                ais.add(13, new SAB(utt, 1, 5)); //8 ManagerLessLife
                break;
            case     "maps/NoWhereToRun9x8.xml" :
                ais.add(12, new GAB(utt, 4, 4)); //ManagerFartherEnemy
                ais.add(13, new SAB(utt, 8, 7)); //ManagerLessDPS
                break;
            case     "maps/16x16/basesWorkers16x16A.xml" :
                ais.add(12, new GAB(utt, 1, 7));  //7 ManagerLessDPS
                ais.add(13, new SAB(utt, 3, 4)); //4 ManagerFartherEnemy
                break;
            case     "maps/16x16/TwoBasesBarracks16x16.xml" :
                ais.add(12, new GAB(utt, 3, 7)); //7 ManagerLessDPS
                ais.add(13, new SAB(utt, 2, 3)); //3 ManagerFather
                break;
            case     "maps/24x24/basesWorkers24x24A.xml" :
                ais.add(12, new GAB(utt, 2, 2)); // 2  ManagerClosestEnemy
                ais.add(13, new SAB(utt, 4, 3)); //3 ManagerFather
                break;
            case     "maps/DoubleGame24x24.xml" :
                ais.add(12, new GAB(utt, 2, 5)); // 5 ManagerLessLife
                ais.add(13, new SAB(utt, 2, 2)); //2  ManagerClosestEnemy
                break;
            case     "maps/32x32/basesWorkers32x32A.xml" :
                ais.add(12, new GAB(utt, 2, 2)); //2  ManagerClosestEnemy
                ais.add(13, new SAB(utt, 1, 2)); //2  ManagerClosestEnemy
                break;
            case     "maps/BWDistantResources32x32.xml" :
                ais.add(12, new GAB(utt, 3, 2)); //2  ManagerClosestEnemy
                ais.add(13, new SAB(utt, 3, 2)); //2  ManagerClosestEnemy
                break;
            case     "maps/BroodWar/(4)BloodBath.scmB.xml" :
                ais.add(12, new GAB(utt, 1, 7)); // 7 ManagerLessDPS
                ais.add(13, new SAB(utt, 1, 5)); // 5 ManagerLessLife
                break;
            default: //"maps/BroodWar/(4)EmpireoftheSun.scmC.xml"
                ais.add(12, new GAB(utt, 2, 2));
                ais.add(13, new SAB(utt, 1, 2)); //2  ManagerClosestEnemy
                break;
        }
        
        ais.add(14, new CmabAssymetricMCTS(100, -1, 100, 1, 0.3f, 
                                             0.0f, 0.4f, 0, new RandomBiasedAI(utt), 
                                             new SimpleSqrtEvaluationFunction3(), true, utt, 
                                            "ManagerClosestEnemy", 1));
        ais.add(15,new ai.competition.capivara.CmabAssymetricMCTS(100, -1, 100, 1, 0.3f, 
                                             0.0f, 0.4f, 0, new RandomBiasedAI(utt), 
                                             new SimpleSqrtEvaluationFunction3(), true, utt, 
                                            "ManagerClosestEnemy", 2,decodeScripts(utt, "1;2;3;"),"A3N_2Unit_3Sc_Symmetric") );
        
        AI ai1 = ais.get(iAi1);
        AI ai2 = ais.get(iAi2);

        ai1.preGameAnalysis(gs, 100);
        ai2.preGameAnalysis(gs, 100);
        
        

        /*
            Variáveis para coleta de tempo
         */
        double ai1TempoMin = 9999, ai1TempoMax = -9999;
        double ai2TempoMin = 9999, ai2TempoMax = -9999;
        double sumAi1 = 0, sumAi2 = 0;
        int totalAction = 0;

        System.out.println("---------AIs---------");
        System.out.println("AI 1 = " + ai1.toString());
        System.out.println("AI 2 = " + ai2.toString() + "\n");

        System.out.println("---------Mapa---------");
        System.out.println("Mapa= " + maps.get(map) + "\n");

        //método para fazer a troca dos players
        //JFrame w = PhysicalGameStatePanel.newVisualizer(gs, 840, 840, false, PhysicalGameStatePanel.COLORSCHEME_BLACK);
//        JFrame w = PhysicalGameStatePanel.newVisualizer(gs,640,640,false,PhysicalGameStatePanel.COLORSCHEME_WHITE);
        long startTime;
        long timeTemp;
        //System.out.println("Tempo de execução P2="+(startTime = System.currentTimeMillis() - startTime));
        long nextTimeToUpdate = System.currentTimeMillis() + PERIOD;
        do {
            if (System.currentTimeMillis() >= nextTimeToUpdate) {
                totalAction++;
                startTime = System.currentTimeMillis();

                PlayerAction pa1 = ai1.getAction(0, gs);
                //dados de tempo ai1
                timeTemp = (System.currentTimeMillis() - startTime);
                sumAi1 += timeTemp;
                //coleto tempo mínimo
                if (ai1TempoMin > timeTemp) {
                    ai1TempoMin = timeTemp;
                }
                //coleto tempo maximo
                if (ai1TempoMax < timeTemp) {
                    ai1TempoMax = timeTemp;
                }

                startTime = System.currentTimeMillis();
                PlayerAction pa2 = ai2.getAction(1, gs);
                //dados de tempo ai2
                timeTemp = (System.currentTimeMillis() - startTime);
                sumAi2 += timeTemp;
                //coleto tempo mínimo
                if (ai2TempoMin > timeTemp) {
                    ai2TempoMin = timeTemp;
                }
                //coleto tempo maximo
                if (ai2TempoMax < timeTemp) {
                    ai2TempoMax = timeTemp;
                }

                gs.issueSafe(pa1);
                gs.issueSafe(pa2);

                // simulate:
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

            //avaliacao de tempo
            duracao = Duration.between(timeInicial, Instant.now());

        } while (!gameover && (gs.getTime() < MAXCYCLES) && (duracao.toMinutes() < 120));
        // remover 
        //System.out.println("------------Análise de estratégias-----------------");
        //SCV_forEval sct = (SCV_forEval) ai2;
        //System.out.println(sct.getTotalStrategy());

        System.out.println("Total de actions= " + totalAction + " sumAi1= " + sumAi1 + " sumAi2= " + sumAi2 + "\n");

        System.out.println("Tempos de AI 1 = " + ai1.toString());
        System.out.println("Tempo minimo= " + ai1TempoMin + " Tempo maximo= " + ai1TempoMax + " Tempo medio= " + (sumAi1 / (long) totalAction));

        System.out.println("Tempos de AI 2 = " + ai2.toString());
        System.out.println("Tempo minimo= " + ai2TempoMin + " Tempo maximo= " + ai2TempoMax + " Tempo medio= " + (sumAi2 / (long) totalAction) + "\n");

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

        ScriptsCreator sc = new ScriptsCreator(utt, 300);
        ArrayList<BasicExpandedConfigurableScript> scriptsCompleteSet = sc.getScriptsMixReducedSet();

        iScriptsAi1.forEach((idSc) -> {
            scriptsAI.add(scriptsCompleteSet.get(idSc));
        });

        return scriptsAI;
    }

}
