/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util.SOA;

import Standard.StrategyTactics;
import ai.CMAB.CmabAssymetricMCTS;
import ai.RandomBiasedAI;
import ai.abstraction.partialobservability.POHeavyRush;
import ai.abstraction.partialobservability.POLightRush;
import ai.abstraction.partialobservability.PORangedRush;
import ai.abstraction.partialobservability.POWorkerRush;
import ai.abstraction.pathfinding.AStarPathFinding;
import ai.ahtn.AHTNAI;
import ai.aiSelection.AlphaBetaSearch.AlphaBetaSearch;
import ai.asymmetric.GAB.SandBox.GAB;
import ai.asymmetric.PGS.NGS;
import ai.asymmetric.PGS.NGSLimit;
import ai.asymmetric.PGS.NGSLimitRandom;
import ai.asymmetric.PGS.NGSRandom;
import ai.asymmetric.PGS.PGSIteration;
import ai.asymmetric.PGS.PGSIterationRandom;
import ai.asymmetric.PGS.PGSResponseMRTS;
import ai.asymmetric.PGS.PGSResponseMRTSRandom;
import ai.core.AI;
import ai.asymmetric.PGS.PGSSCriptChoice;
import ai.asymmetric.PGS.PGSSCriptChoiceRandom;
import ai.asymmetric.PGS.PGSmRTS;
import ai.asymmetric.SAB.SAB;
import ai.asymmetric.SSS.NSSS;
import ai.asymmetric.SSS.NSSSLimit;
import ai.asymmetric.SSS.NSSSLimitRandom;
import ai.asymmetric.SSS.NSSSRandom;
import ai.asymmetric.SSS.SSSIteration;
import ai.asymmetric.SSS.SSSIterationRandom;
import ai.asymmetric.SSS.SSSResponseMRTS;
import ai.asymmetric.SSS.SSSResponseMRTSRandom;
import ai.asymmetric.SSS.SSSmRTS;
import ai.asymmetric.SSS.SSSmRTSScriptChoice;
import ai.asymmetric.SSS.SSSmRTSScriptChoiceRandom;
import ai.cluster.CABA;
import ai.cluster.CABA_Enemy;
import ai.cluster.CABA_TDLearning;
import ai.cluster.CIA_Enemy;
import ai.cluster.CIA_PlayoutTemporal;
import ai.cluster.CIA_TDLearning;
import ai.competition.tiamat.Tiamat;
import ai.configurablescript.BasicExpandedConfigurableScript;
import ai.configurablescript.ScriptsCreator;
import ai.evaluation.SimpleSqrtEvaluationFunction3;
import ai.mcts.believestatemcts.BS3_NaiveMCTS;
import ai.mcts.naivemcts.NaiveMCTS;
import ai.puppet.PuppetSearchMCTS;
import ai.puppet.PuppetSearchMCTSBasicScripts;
import ai.scv.SCV;
import ai.scv.SCVPlus;
import gui.PhysicalGameStatePanel;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JFrame;
import rts.GameState;
import rts.PhysicalGameState;
import rts.PlayerAction;
import rts.units.UnitTYpeTableBattle;
import rts.units.UnitTypeTable;
import static tests.ClusterTesteLeve.decodeScripts;
import static tests.ClusterTesteLeve_Cluster.decodeScripts;
import static tests.ClusterTesteLeve_Combination.decodeScripts;

/**
 *
 * @author rubens Classe responsável por rodar os confrontos entre duas IA's.
 * Ambiente totalmente observável.
 */
public class RoundRobinClusterLeve {

    static String _nameStrategies = "", _enemy = "";
    static AI[] strategies = null;

    public boolean run(String sIA1, String sIA2, String sMap, String sIte, String pathLog) throws Exception {
        int iAi1 = Integer.parseInt(sIA1);
        int iAi2 = Integer.parseInt(sIA2);
        int map = Integer.parseInt(sMap);

        ArrayList<String> log = new ArrayList<>();
        //controle de tempo
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

        //best GA PGS with PGSRandomFour  
        String GA_PGSR = "145;81;2;244;5;134;41;74;266;267";
        //best GA SSS with SSSRandomFour  
        String GA_SSSR = "33;193;18;242;179;25;202;284;46;239;";
        
        List<AI> ais = new ArrayList<>(Arrays.asList(
               new AHTNAI(utt),
               new NaiveMCTS(utt),
               new PuppetSearchMCTS(utt),
               new PuppetSearchMCTSBasicScripts(utt),
               new StrategyTactics(utt),
               new PGSSCriptChoice(utt, decodeScripts(utt, "0;1;2;3;"), "PGS"),
               new SSSmRTSScriptChoice(utt, decodeScripts(utt, "0;1;2;3;"), "SSS"),
               new BasicExpandedConfigurableScript(utt, new AStarPathFinding(), 18,0,0,1,2,2,-1,-1,4), //lr
               new BasicExpandedConfigurableScript(utt, new AStarPathFinding(), 18,0,0,1,2,2,-1,-1,5), //HR
               new BasicExpandedConfigurableScript(utt, new AStarPathFinding(), 18,0,0,1,2,2,-1,-1,6), //RR
               new BasicExpandedConfigurableScript(utt, new AStarPathFinding(), 18,0,0,1,2,2,-1,-1,3), //WR
               new SCVPlus(utt)
        ));

        //add GAB e SAB by map settings
        switch(maps.get(map)){
            case "maps/8x8/basesWorkers8x8A.xml" :
                ais.add(12, new GAB(utt, 6, 8)); //8 ManagerMoreDPS
                ais.add(13, new SAB(utt, 5, 8)); //8 ManagerMoreDPS
                break;
            case     "maps/8x8/FourBasesWorkers8x8.xml" :                
                ais.add(12, new GAB(utt, 8, 2));
                ais.add(13, new SAB(utt, 8, 2));
                break;
            case     "maps/NoWhereToRun9x8.xml" :
                ais.add(12, new GAB(utt, 8, 2));
                ais.add(13, new SAB(utt, 3, 2));
                break;
            case     "maps/16x16/basesWorkers16x16A.xml" :
                ais.add(12, new GAB(utt, 10, 2));  //2  ManagerClosestEnemy
                ais.add(13, new SAB(utt, 4, 4)); //4 ManagerFartherEnemy
                break;
            case     "maps/16x16/TwoBasesBarracks16x16.xml" :
                ais.add(12, new GAB(utt, 7, 3));
                ais.add(13, new SAB(utt, 0, 3));
                break;
            case     "maps/24x24/basesWorkers24x24A.xml" :
                ais.add(12, new GAB(utt, 9, 5)); // 5 ManagerLessLife
                ais.add(13, new SAB(utt, 1, 2)); //2  ManagerClosestEnemy
                break;
            case     "maps/32x32/basesWorkers32x32A.xml" :
                ais.add(12, new GAB(utt, 1, 2)); //2  ManagerClosestEnemy
                ais.add(13, new SAB(utt, 2, 5)); // 5 ManagerLessLife
                break;
            case     "maps/BWDistantResources32x32.xml" :
                ais.add(12, new GAB(utt, 2, 7)); // 7 ManagerLessDPS
                ais.add(13, new SAB(utt, 3, 0)); //0 - ManagerRandom
                break;
            case     "maps/BroodWar/(4)BloodBath.scmB.xml" :
                ais.add(12, new GAB(utt, 1, 7)); // 7 ManagerLessDPS
                ais.add(13, new SAB(utt, 1, 5)); // 5 ManagerLessLife
                break;
            case   "maps/BroodWar/(4)EmpireoftheSun.scmC.xml":
                ais.add(12, new GAB(utt, 2, 2));
                ais.add(13, new SAB(utt, 2, 2)); //2  ManagerClosestEnemy
                break;
            default:
                ais.add(12, new GAB(utt, 2, 2));
                ais.add(13, new SAB(utt, 2, 2));
                break;
        }
        
        ais.add(14, new CmabAssymetricMCTS(100, -1, 100, 1, 0.3f, 
                                             0.0f, 0.4f, 0, new RandomBiasedAI(utt), 
                                             new SimpleSqrtEvaluationFunction3(), true, utt, 
                                            "ManagerClosestEnemy", 1));
        ais.add(15,new ai.competition.capivara.CmabAssymetricMCTS(100, -1, 100, 1, 0.3f, 
                                             0.0f, 0.4f, 0, new RandomBiasedAI(utt), 
                                             new SimpleSqrtEvaluationFunction3(), true, utt, 
                                            "ManagerClosestEnemy", 1,decodeScripts(utt, "1;2;3;"),"A3N_3Sc_Symmetric") );
        ais.add(16,new ai.competition.capivara.CmabAssymetricMCTS(100, -1, 100, 1, 0.3f, 
                                             0.0f, 0.4f, 0, new RandomBiasedAI(utt), 
                                             new SimpleSqrtEvaluationFunction3(), true, utt, 
                                            "ManagerClosestEnemy", 1,decodeScripts(utt, "0;1;2;3;"),"A3N_4Base_Sc") );
        ais.add(17,new ai.competition.capivara.CmabAssymetricMCTS(100, -1, 100, 1, 0.3f, 
                                             0.0f, 0.4f, 0, new RandomBiasedAI(utt), 
                                             new SimpleSqrtEvaluationFunction3(), true, utt, 
                                            "ManagerClosestEnemy", 1,decodeScripts(utt, "1;2;3;299;"),"A3N_Economy") );
        ais.add(18,new ai.competition.capivara.CmabAssymetricMCTS(100, -1, 100, 1, 0.3f, 
                                             0.0f, 0.4f, 0, new RandomBiasedAI(utt), 
                                             new SimpleSqrtEvaluationFunction3(), true, utt, 
                                            "ManagerClosestEnemy", 2,decodeScripts(utt, "1;2;3;"),"A3N_2Unit_3Sc_Symmetric") );
        
        AI ai1 = ais.get(iAi1);
        AI ai2 = ais.get(iAi2);

        /*
            Variáveis para coleta de tempo
         */
        double ai1TempoMin = 9999, ai1TempoMax = -9999;
        double ai2TempoMin = 9999, ai2TempoMax = -9999;
        double sumAi1 = 0, sumAi2 = 0;
        int totalAction = 0;

        log.add("---------AIs---------");
        log.add("AI 1 = " + ai1.toString());
        log.add("AI 2 = " + ai2.toString() + "\n");

        log.add("---------Mapa---------");
        log.add("Mapa= " + maps.get(map) + "\n");

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
            //avaliacao de tempo
            duracao = Duration.between(timeInicial, Instant.now());

        } while (!gameover && (gs.getTime() < MAXCYCLES));

        log.add("Total de actions= " + totalAction + " sumAi1= " + sumAi1 + " sumAi2= " + sumAi2 + "\n");

        log.add("Tempos de AI 1 = " + ai1.toString());
        log.add("Tempo minimo= " + ai1TempoMin + " Tempo maximo= " + ai1TempoMax + " Tempo medio= " + (sumAi1 / (long) totalAction));

        log.add("Tempos de AI 2 = " + ai2.toString());
        log.add("Tempo minimo= " + ai2TempoMin + " Tempo maximo= " + ai2TempoMax + " Tempo medio= " + (sumAi2 / (long) totalAction) + "\n");

        log.add("Winner " + Integer.toString(gs.winner()));
        log.add("Game Over");

        if (gs.winner() == -1) {
            System.out.println("Empate!" + ai1.toString() + " vs " + ai2.toString() + " Max Cycles =" + MAXCYCLES + " Time:" + duracao.toMinutes());
        }

        gravarLog(log, sIA1, sIA2, sMap, sIte, pathLog);
        //System.exit(0);
        return true;
    }

    private void gravarLog(ArrayList<String> log, String sIA1, String sIA2, String sMap, String sIte, String pathLog) throws IOException {
        if (!pathLog.endsWith("/")) {
            pathLog += "/";
        }
        String nameArquivo = pathLog + "match_" + sIA1 + "_" + sIA2 + "_" + sMap + "_" + sIte + ".scv";
        File arqLog = new File(nameArquivo);
        if (!arqLog.exists()) {
            arqLog.createNewFile();
        }
        //abre o arquivo e grava o log
        try {
            FileWriter arq = new FileWriter(arqLog, false);
            PrintWriter gravarArq = new PrintWriter(arq);
            for (String l : log) {
                gravarArq.println(l);
            }

            gravarArq.flush();
            gravarArq.close();
            arq.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
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
