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
import ai.ScriptsGenerator.ChromosomeAI;
import ai.ScriptsGenerator.ChromosomesBag;
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
import util.XMLWriter;

/**
 *
 * @author Rubens
 */
public class ClusterTesteLeve_ScCreator {

    public static void main(String args[]) throws Exception {
        int iAi1 = Integer.parseInt(args[0]);
        int iAi2 = Integer.parseInt(args[1]);
        int map = Integer.parseInt(args[2]);

        Instant timeInicial = Instant.now();
        Duration duracao;

        List<String> maps = new ArrayList<>(Arrays.asList(
                "maps/8x8/basesWorkers8x8A.xml",
                "maps/16x16/basesWorkers16x16A.xml",
                "maps/24x24/basesWorkers24x24A.xml"
        ));

        //UnitTypeTable utt = new UnitTYpeTableBattle();
        UnitTypeTable utt = new UnitTypeTable();
        PhysicalGameState pgs = PhysicalGameState.load(maps.get(map), utt);

        GameState gs = new GameState(pgs, utt);
        int MAXCYCLES = 20000;
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
        ChromosomesBag bag = new ChromosomesBag(utt);
        
        List<AI> ais = new ArrayList<>(Arrays.asList(
                new ChromosomeAI(utt, bag.ChromosomesBag1(utt), "C1"),
                new ChromosomeAI(utt, bag.ChromosomesBag2(utt), "C2"),
                new ChromosomeAI(utt, bag.ChromosomesBag3(utt), "C3"),
                new ChromosomeAI(utt, bag.ChromosomesBag4(utt), "C4"),
                new ChromosomeAI(utt, bag.ChromosomesBag5(utt), "C5"),
                new ChromosomeAI(utt, bag.ChromosomesBag6(utt), "C6"),
                new ChromosomeAI(utt, bag.ChromosomesBag7(utt), "C7"),
                new ChromosomeAI(utt, bag.ChromosomesBag8(utt), "C8"),
                new ChromosomeAI(utt, bag.ChromosomesBag9(utt), "C9"),
                new ChromosomeAI(utt, bag.ChromosomesBag10(utt), "C10"),
                new ChromosomeAI(utt, bag.ChromosomesBag11(utt), "C11"),
                new ChromosomeAI(utt, bag.ChromosomesBag12(utt), "C12"),
                new ChromosomeAI(utt, bag.ChromosomesBag13(utt), "C13"),
                new ChromosomeAI(utt, bag.ChromosomesBag14(utt), "C14"),
                new ChromosomeAI(utt, bag.ChromosomesBag15(utt), "C15"),
                new ChromosomeAI(utt, bag.ChromosomesBag16(utt), "C16"),
                new ChromosomeAI(utt, bag.ChromosomesBag17(utt), "C17"),
                new ChromosomeAI(utt, bag.ChromosomesBag18(utt), "C18"),
                new ChromosomeAI(utt, bag.ChromosomesBag19(utt), "C19"),
                new ChromosomeAI(utt, bag.ChromosomesBag20(utt), "C20"),
                new ChromosomeAI(utt, bag.ChromosomesBag21(utt), "C21"),
                new ChromosomeAI(utt, bag.ChromosomesBag22(utt), "C22"),
                new ChromosomeAI(utt, bag.ChromosomesBag23(utt), "C23"),
                new ChromosomeAI(utt, bag.ChromosomesBag24(utt), "C24"),
                new ChromosomeAI(utt, bag.ChromosomesBag25(utt), "C25"),
                new ChromosomeAI(utt, bag.ChromosomesBag26(utt), "C26"),
                new ChromosomeAI(utt, bag.ChromosomesBag27(utt), "C27"),
                new ChromosomeAI(utt, bag.ChromosomesBag28(utt), "C28"),
                new ChromosomeAI(utt, bag.ChromosomesBag29(utt), "C29"),
                new ChromosomeAI(utt, bag.ChromosomesBag30(utt), "C30"),
                new ChromosomeAI(utt, bag.ChromosomesBag31(utt), "C31"),
                new ChromosomeAI(utt, bag.ChromosomesBag32(utt), "C32"),
                new ChromosomeAI(utt, bag.ChromosomesBag33(utt), "C33"),
                new ChromosomeAI(utt, bag.ChromosomesBag34(utt), "C34"),
                new ChromosomeAI(utt, bag.ChromosomesBag35(utt), "C35"),
                new ChromosomeAI(utt, bag.ChromosomesBag36(utt), "C36"),
                new ChromosomeAI(utt, bag.ChromosomesBag37(utt), "C37"),
                new ChromosomeAI(utt, bag.ChromosomesBag38(utt), "C38"),
                new ChromosomeAI(utt, bag.ChromosomesBag39(utt), "C39"),
                new ChromosomeAI(utt, bag.ChromosomesBag40(utt), "C40")
        ));
        
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
