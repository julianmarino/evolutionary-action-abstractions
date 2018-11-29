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
import ai.abstraction.HeavyRush;
import ai.abstraction.LightRush;
import ai.abstraction.RangedRush;
import ai.abstraction.WorkerRush;
import ai.abstraction.partialobservability.POHeavyRush;
import ai.abstraction.partialobservability.POLightRush;
import ai.abstraction.partialobservability.PORangedRush;
import ai.abstraction.partialobservability.POWorkerRush;
import ai.abstraction.pathfinding.BFSPathFinding;
import ai.ahtn.AHTNAI;
import ai.aiSelection.IDABCD.ABSelection;
import ai.asymmetric.GAB.GAB_oldVersion;
import ai.asymmetric.GAB.SandBox.GAB;
import ai.asymmetric.IDABCD.IDABCDAsymmetric;
import ai.asymmetric.PGS.PGSSCriptChoice;
import ai.asymmetric.PGS.PGSSCriptChoiceRandom;
import ai.asymmetric.PGS.PGSSelection;
import ai.asymmetric.PGS.PGSmRTS;
import ai.asymmetric.PGS.PGSmRTSRandom;
import ai.asymmetric.SAB.SAB_oldVersion;
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
import rts.units.UnitTypeTable;
import static tests.ClusterTesteLeve.decodeScripts;
import util.XMLWriter;

/**
 *
 * @author Rubens
 */
public class ClusterTesteLeve_Combination {

    public static void main(String args[]) throws Exception {
        int iAi1 = Integer.parseInt(args[0]);
        int iAi2 = Integer.parseInt(args[1]);
        int map = Integer.parseInt(args[2]);
        
        
        Instant timeInicial = Instant.now();
        Duration duracao;
        
        /*
        
       
        
        "maps/BroodWar/(3)Aztec.scxA.xml", 
            "maps/BroodWar/(3)Aztec.scxC.xml", 
            "maps/BroodWar/(4)Andromeda.scxB.xml",
            "maps/BroodWar/(4)Andromeda.scxF.xml",
            "maps/BroodWar/(4)Fortress.scxD.xml", 
            "maps/BroodWar/(4)Fortress.scxA.xml", 
            "maps/BroodWar/(4)EmpireoftheSun.scmC.xml",
            "maps/BroodWar/(4)EmpireoftheSun.scmF.xml"
        
            "maps/BroodWar/(3)TauCross.scxA.xml",
                "maps/BroodWar/(3)TauCross.scxB.xml",
                "maps/BroodWar/(3)TauCross.scxC.xml",
                "maps/BroodWar/(4)CircuitBreaker.scxB.xml",
                "maps/BroodWar/(4)CircuitBreaker.scxD.xml",
                "maps/BroodWar/(4)CircuitBreaker.scxF.xml",
                "maps/BroodWar/(4)Python.scxA.xml",
                "maps/BroodWar/(4)Python.scxC.xml",
                "maps/BroodWar/(4)Python.scxF.xml"
        
             
             
            "maps/8x8/basesWorkers8x8A.xml" 
             "maps/16x16/basesWorkers16x16A.xml" 
        "maps/NoWhereToRun9x8.xml"     
            
        "maps/8x8/basesWorkers8x8A.xml", 
                "maps/8x8/basesWorkers8x8C.xml",
                "maps/8x8/basesWorkers8x8F.xml",
                "maps/8x8/basesWorkers8x8E.xml",
                "maps/8x8/basesWorkers8x8H.xml",
                "maps/8x8/basesWorkersBarracks8x8.xml",
                "maps/8x8/basesWorkers8x8Obstacle.xml",
                "maps/8x8/FourBasesWorkers8x8.xml",
                "maps/8x8/TwoBasesWorkers8x8.xml"
        
                
        
                //novos mapas de treino
         "maps/8x8/basesWorkers8x8Obstacle.xml",
                "maps/8x8/TwoBasesWorkers8x8.xml",
                "maps/8x8/basesWorkersBarracks8x8.xml",
                "maps/9x8/BlockWall9x8.xml",
                "maps/9x8/BlockDiagonal9x8.xml",
                "maps/9x8/BlockTwoResources9x8.xml",
                "maps/9x8/BlockTwoResourcesWithBarracks9x8.xml",
                "maps/16x16/EightBasesWorkers16x16.xml",
                "maps/16x16/BasesWithWalls16x16.xml",
                "maps/16x16/BasesTwoBarracksWithWalls16x16.xml",
                "maps/16x16/NoWhereWithBlocks16x16.xml",
                "maps/24x24/basesWorkers24x24A.xml",
                "maps/24x24/DoubleMapaWithBlock24x24.xml",
                "maps/24x24/DoubleMapaWithBlockTwoBarracks24x24.xml",
                "maps/24x24/DoubleMapaWithBlockTwoBases24x24.xml",
                "maps/32x32/ComplexPathToFight32x32.xml",
                "maps/32x32/DiagonalRuntoGold32x32.xml",
                "maps/32x32/RuntoGoldWithBlocksBarracks32x32.xml",
                "maps/32x32/SimplePathToFight32x32.xml",
                "maps/64x64/ComplexPathToFight64x64.xml",
                "maps/64x64/SimplePathExplore64x64.xml",
                "maps/64x64/SimplePathToFight64x64.xml"
                
        mapas scv teste
                "maps/8x8/basesWorkers8x8A.xml",
                "maps/8x8/FourBasesWorkers8x8.xml",
                "maps/NoWhereToRun9x8.xml",
                "maps/16x16/basesWorkers16x16A.xml",
                "maps/16x16/TwoBasesBarracks16x16.xml",
                "maps/DoubleGame24x24.xml",
                "maps/BWDistantResources32x32.xml",
                "maps/BroodWar/(4)BloodBath.scmB.xml",
                "maps/8x8/basesWorkers8x8Obstacle.xml",
                "maps/8x8/TwoBasesWorkers8x8.xml", 
                "maps/8x8/basesWorkersBarracks8x8.xml",
                "maps/9x8/BlockWall9x8.xml"
        
        mapas scv totais de teste SCV
                //5 mapas 8x8
                "maps/8x8/basesWorkers8x8A.xml",
                "maps/8x8/FourBasesWorkers8x8.xml",
                "maps/8x8/basesWorkers8x8Obstacle.xml",
                "maps/8x8/TwoBasesWorkers8x8.xml", 
                "maps/8x8/basesWorkersBarracks8x8.xml",
                //5 mapas 16x16
                "maps/16x16/basesWorkers16x16C.xml",
                "maps/16x16/basesWorkers16x16G.xml",
                "maps/16x16/basesWorkers16x16K.xml",
                "maps/16x16/basesWorkers16x16A.xml",
                "maps/16x16/TwoBasesBarracks16x16.xml",
                //5 mapas 24x24
                "maps/24x24/basesWorkers24x24C.xml",
                "maps/24x24/basesWorkers24x24E.xml",
                "maps/24x24/basesWorkers24x24F.xml",
                "maps/24x24/basesWorkers24x24I.xml",
                "maps/DoubleGame24x24.xml",
                //5 mapas 32x32
                "maps/32x32/basesWorkers32x32A.xml",
                "maps/32x32/basesWorkersBarracks32x32.xml",
                "maps/32x32/RunToFight32x32A.xml",
                "maps/32x32/centerResources32x32.xml",
                "maps/BWDistantResources32x32.xml",
                //5 mapas 64x46
                "maps/BroodWar/(4)BloodBath.scmA.xml",
                "maps/BroodWar/(4)BloodBath.scmC.xml",
                "maps/BroodWar/(4)BloodBath.scmD.xml",
                "maps/BroodWar/(4)BloodBath.scmB.xml",
                "maps/BroodWar/(4)BloodBath.scmF.xml"
                //5 mapas 128x128
                (3)TauCross.scxA.xml
                (3)TauCross.scxC.xml
                (4)CircuitBreaker.scxB.xml
                (4)Python.scxA.xml
                (4)Python.scxF.xml

        */
        
        List<String> maps = new ArrayList<>(Arrays.asList(
                "maps/8x8/basesWorkers8x8A.xml",
                "maps/16x16/basesWorkers16x16A.xml",
                "maps/24x24/basesWorkers24x24A.xml",
                //"maps/DoubleGame24x24.xml"
                "maps/32x32/basesWorkers32x32A.xml",
                "maps/BWDistantResources32x32.xml"
                //"maps/BroodWar/(4)BloodBath.scmB.xml"
                
                
        ));
        
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

        /*
            new NaiveMCTS(utt),
                new PuppetSearchMCTS(utt),
                new StrategyTactics(utt),
                new BS3_NaiveMCTS(utt),
                new AHTNAI(utt),
        
                new LightRush(utt),
                new WorkerRush(utt),
                new HeavyRush(utt),
                new RangedRush(utt),
                new EconomyRush(utt),
                new SCV(utt),
                new SCV_GAB(utt),
                new PVAIML_SL_ONE_STRATEGY(utt),
                new PVAI(utt)
        
                new WorkerRush(utt),
                new POLightRush(utt),
                new RandomBiasedAI(),
                new POHeavyRush(utt),
                new PORangedRush(utt),
                new LightDefense(utt),
                new RangedDefense(utt),
                new WorkerDefense(utt),
                new EconomyMilitaryRush(utt),
            
        
        //IAs GA 
                new AHTNAI(utt),
               new NaiveMCTS(utt),
               new BS3_NaiveMCTS(utt),
               new PuppetSearchMCTS(utt),
               new StrategyTactics(utt),
               new PGSmRTS(utt),
               new SSSmRTS(utt),
               new GAB(utt),
               new GAB_ABActionGeneration(utt),
               new SAB_oldVersion(utt),
               new PGSSCriptChoice(utt, decodeScripts(utt, best), "BR"), //PGS com o best response do GA
               new GAB_ScriptC(utt, decodeScripts(utt, best), "BR"), //GAB com o best response do GA
               new PGSSCriptChoice(utt, decodeScripts(utt, sc2Nash), "Nash"), //PGS com o best response do Nash
               new GAB_ScriptC(utt, decodeScripts(utt, sc2Nash), "Nash"),  //GAB com o best response do Nash
               new PGSSCriptChoice(utt, decodeScripts(utt, bestGA), "bGA"), //PGS com o melhor GA
               new GAB_ScriptC(utt, decodeScripts(utt, bestGA), "bGA")  //GAB com o melhor GA
        
        
        
        //IAS SCV
                //IA's SCV usadas
                //new WorkerDefense(utt),
                //new WorkerRush(utt),
                //new RangedDefense(utt),
                //new RangedRush(utt),
                //new LightRush(utt),
                //new LightDefense(utt),
                //new HeavyDefense(utt),
                //new HeavyRush(utt),
                //new EMRDeterministico(utt),
                new PVAIML_SL_ONE_STRATEGY(utt),  //SBS
                new AHTNAI(utt),
                new NaiveMCTS(utt),
                new BS3_NaiveMCTS(utt),
                new PuppetSearchMCTS(utt),
                new StrategyTactics(utt),
                new SCV_Full(utt, pgs.getHeight(), pgs.getWidth()),
                new SCV_GABFull(utt, pgs.getHeight(), pgs.getWidth())
         */            
        
        //best response GA PGS
        String GA_PGS = "32;285;107;267;225;"; 
        //best response GA SSS
        String GA_SSS = "233;97;93;246;117;"; 
        
        List<AI> ais = new ArrayList<>(Arrays.asList(
                new PGSSCriptChoice(utt, decodeScripts(utt, "0;1;2;3;"), "PGSSym"), 
                new PGSSCriptChoiceRandom(utt, decodeScripts(utt, "0;1;2;3;"), "PGSRSym",1,100),
                new PGSSCriptChoiceRandom(utt, decodeScripts(utt, "0;1;2;3;"), "PGSRSym",2,100),
                new PGSSCriptChoiceRandom(utt, decodeScripts(utt, "0;1;2;3;"), "PGSRSym",4,100),
                new PGSSCriptChoiceRandom(utt, decodeScripts(utt, "0;1;2;3;"), "PGSRSym",1,200),
                new PGSSCriptChoiceRandom(utt, decodeScripts(utt, "0;1;2;3;"), "PGSRSym",2,200),
                new PGSSCriptChoiceRandom(utt, decodeScripts(utt, "0;1;2;3;"), "PGSRSym",4,200),
                new PGSSCriptChoice(utt, decodeScripts(utt, "189;225;101;26;"), "PGSSymHeavy"),
                new PGSSCriptChoiceRandom(utt, decodeScripts(utt, "189;225;101;26;"), "PGSRSymHeavy",1,100),
                new PGSSCriptChoiceRandom(utt, decodeScripts(utt, "189;225;101;26;"), "PGSRSymHeavy",2,100),
                new PGSSCriptChoiceRandom(utt, decodeScripts(utt, "189;225;101;26;"), "PGSRSymHeavy",4,100),
                new PGSSCriptChoiceRandom(utt, decodeScripts(utt, "189;225;101;26;"), "PGSRSymHeavy",1,200),
                new PGSSCriptChoiceRandom(utt, decodeScripts(utt, "189;225;101;26;"), "PGSRSymHeavy",2,200),
                new PGSSCriptChoiceRandom(utt, decodeScripts(utt, "189;225;101;26;"), "PGSRSymHeavy",4,200)
        ));

        AI ai1 = ais.get(iAi1);
        AI ai2 = ais.get(iAi2);

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
            
        } while (!gameover && (gs.getTime() < MAXCYCLES) && (duracao.toMinutes() < 40));
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

        ScriptsCreator sc = new ScriptsCreator(utt,300);
        ArrayList<BasicExpandedConfigurableScript> scriptsCompleteSet = sc.getScriptsMixReducedSet();

        iScriptsAi1.forEach((idSc) -> {
            scriptsAI.add(scriptsCompleteSet.get(idSc));
        });

        return scriptsAI;
    }

}
