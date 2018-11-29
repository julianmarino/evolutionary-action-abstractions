/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util.SOA;

import ai.core.AI;
import ai.asymmetric.PGS.PGSSCriptChoice;
import ai.asymmetric.PGS.PGSSCriptChoiceRandom;
import ai.asymmetric.SSS.SSSmRTSScriptChoice;
import ai.asymmetric.SSS.SSSmRTSScriptChoiceRandom;
import ai.configurablescript.BasicExpandedConfigurableScript;
import ai.configurablescript.ScriptsCreator;
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
import rts.units.UnitTypeTable;

/**
 *
 * @author rubens Classe responsável por rodar os confrontos entre duas IA's.
 * Ambiente totalmente observável.
 */
public class RoundRobinTOScaleNash {

    static String _nameStrategies = "", _enemy = "";
    static AI[] strategies = null;

    public boolean run(String tupleAi1, String tupleAi2, Integer IDMatch, Integer Generation, String pathLog, int iMap,
                       String idAi1, String idAi2) throws Exception {
        ArrayList<String> log = new ArrayList<>();
        //controle de tempo
        Instant timeInicial = Instant.now();
        Duration duracao;

        log.add("Tupla A1 = " + tupleAi1);
        log.add("Tupla A2 = " + tupleAi2);

        List<String> maps = new ArrayList<>(Arrays.asList(
                "maps/24x24/basesWorkers24x24A.xml"
                //"maps/32x32/basesWorkers32x32A.xml"
                //"maps/8x8/basesWorkers8x8A.xml" 
        ));

        UnitTypeTable utt = new UnitTypeTable();
        PhysicalGameState pgs = PhysicalGameState.load(maps.get(iMap), utt);

        GameState gs = new GameState(pgs, utt);
        int MAXCYCLES = 20000;
        int PERIOD = 20;
        boolean gameover = false;
        
        if (pgs.getHeight() == 8) {
            MAXCYCLES = 4000;
        }
        if (pgs.getHeight() == 9) {
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

        //decompõe a tupla
        ArrayList<Integer> iScriptsAi1 = new ArrayList<>();
        String[] itens = tupleAi1.split(";");

        for (String element : itens) {
            iScriptsAi1.add(Integer.decode(element));
        }

        ArrayList<Integer> iScriptsAi2 = new ArrayList<>();
        itens = tupleAi2.split(";");

        for (String element : itens) {
            iScriptsAi2.add(Integer.decode(element));
        }

        //pgs 
        //AI ai1 = new PGSSCriptChoiceRandom(utt, decodeScripts(utt, iScriptsAi1), "PGSR", 2, 200);
        //AI ai2 = new PGSSCriptChoiceRandom(utt, decodeScripts(utt, iScriptsAi2), "PGSR", 2, 200);
        
        AI ai1 = new SSSmRTSScriptChoiceRandom(utt, decodeScripts(utt, iScriptsAi1), "SSSR", 2, 200);
        AI ai2 = new SSSmRTSScriptChoiceRandom(utt, decodeScripts(utt, iScriptsAi2), "SSSR", 2, 200);

        /*
            Variáveis para coleta de tempo
         */
        double ai1TempoMin = 9999, ai1TempoMax = -9999;
        double ai2TempoMin = 9999, ai2TempoMax = -9999;
        double sumAi1 = 0, sumAi2 = 0;
        int totalAction = 0;

        log.add("---------AIs---------");
        log.add("AI 1 = " + ai1.toString());
        log.add("ID A1 = " + idAi1);
        log.add("AI 2 = " + ai2.toString() );
        log.add("ID A2 = " + idAi2 + "\n");

        log.add("---------Mapa---------");
        log.add("Mapa= " + maps.get(iMap) + "\n");

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

        } while (!gameover && (gs.getTime() < MAXCYCLES) && (duracao.toMinutes() < 20));

        log.add("Total de actions= " + totalAction + " sumAi1= " + sumAi1 + " sumAi2= " + sumAi2 + "\n");

        log.add("Tempos de AI 1 = " + ai1.toString());
        log.add("Tempo minimo= " + ai1TempoMin + " Tempo maximo= " + ai1TempoMax + " Tempo medio= " + (sumAi1 / (long) totalAction));

        log.add("Tempos de AI 2 = " + ai2.toString());
        log.add("Tempo minimo= " + ai2TempoMin + " Tempo maximo= " + ai2TempoMax + " Tempo medio= " + (sumAi2 / (long) totalAction) + "\n");

        log.add("Winner " + Integer.toString(gs.winner()));
        log.add("Game Over");
        
        if(gs.winner() == -1){
            System.out.println("Empate!"+ai1.toString()+" vs "+ai2.toString()+" Max Cycles ="+MAXCYCLES+" Time:"+duracao.toMinutes());
        }
        String stMatch = Integer.toString(IDMatch)+""+Integer.toString(iMap);
        gravarLog(log, tupleAi1, tupleAi2, stMatch, Generation, pathLog, idAi1, idAi2);
        //System.exit(0);
        return true;
    }

    public static List<AI> decodeScripts(UnitTypeTable utt, ArrayList<Integer> iScripts) {
        List<AI> scriptsAI = new ArrayList<>();

        ScriptsCreator sc = new ScriptsCreator(utt,300);
        ArrayList<BasicExpandedConfigurableScript> scriptsCompleteSet = sc.getScriptsMixReducedSet();



        //take the setCover

        
        AI[] AIs = new AI[20];
        AIs[0] = scriptsCompleteSet.get(0);
        AIs[1] = scriptsCompleteSet.get(1);
        AIs[2] = scriptsCompleteSet.get(2);
        AIs[3] = scriptsCompleteSet.get(3);
        AIs[4] = scriptsCompleteSet.get(4);
        AIs[5] = scriptsCompleteSet.get(7);
        AIs[6] = scriptsCompleteSet.get(8);
        AIs[7] = scriptsCompleteSet.get(100);
        AIs[8] = scriptsCompleteSet.get(101);
        AIs[9] = scriptsCompleteSet.get(102);
        AIs[10] = scriptsCompleteSet.get(103);
        AIs[11] = scriptsCompleteSet.get(107);
        AIs[12] = scriptsCompleteSet.get(108);
        AIs[13] = scriptsCompleteSet.get(201);
        AIs[14] = scriptsCompleteSet.get(202);
        AIs[15] = scriptsCompleteSet.get(203);
        AIs[16] = scriptsCompleteSet.get(204);
        AIs[17] = scriptsCompleteSet.get(207);
        AIs[18] = scriptsCompleteSet.get(208);
        AIs[19] = scriptsCompleteSet.get(209);
        
        for (Integer idSc : iScripts) {
            scriptsAI.add(AIs[idSc]);
        }

        return scriptsAI;
    }

    private void gravarLog(ArrayList<String> log, String tupleAi1, String tupleAi2, String IDMatch, Integer Generation, 
                           String pathLog, String idAi1, String idAi2) throws IOException {
        if(!pathLog.endsWith("/")){
            pathLog +="/";
        }
        String nameArquivo = pathLog + "Eval_" + tupleAi1 + "_" + tupleAi2 + "_" + IDMatch + "_" + 
                             Generation + "_" + idAi1 +"_" + idAi2 + ".txt";
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
}
