/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util.SOA;

import ai.core.AI;
import ai.asymmetric.PGS.PGSSCriptChoice;
import ai.configurablescript.POBasicExpandedConfigurableScript;
import ai.configurablescript.POScriptsCreator;
import gui.PhysicalGameStatePanel;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;
import rts.GameState;
import rts.PartiallyObservableGameState;
import rts.PhysicalGameState;
import rts.PlayerAction;
import rts.units.UnitTypeTable;

/**
 *
 * @author rubens Classe responsável por rodar os confrontos entre duas IA's.
 * Ambiente Parcialmente observável.
 */
public class RoundRobinPOMatch {

    public boolean run(String tupleAi1, String tupleAi2, Integer IDMatch, Integer Generation, String pathLog) throws Exception {
        ArrayList<String> log = new ArrayList<>();
        //controle de tempo
        Instant timeInicial = Instant.now();
        Duration duracao;

        log.add("Tupla A1 = " + tupleAi1);
        log.add("Tupla A2 = " + tupleAi2);

        String map = "maps/24x24/basesWorkers24x24A.xml";

        UnitTypeTable utt = new UnitTypeTable();
        PhysicalGameState pgs = PhysicalGameState.load(map, utt);

        GameState gs = new GameState(pgs, utt);
        int MAXCYCLES = 6000;
        int PERIOD = 20;
        boolean gameover = false;

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

        AI ai1 = new PGSSCriptChoice(utt, decodeScripts(utt, iScriptsAi1), tupleAi1);
        AI ai2 = new PGSSCriptChoice(utt, decodeScripts(utt, iScriptsAi2), tupleAi2);

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
        log.add("Mapa= " + map + "\n");

        //método para fazer a troca dos players
        //JFrame w = PhysicalGameStatePanel.newVisualizer(gs,640,640, true);
        long startTime;
        long timeTemp;
        //System.out.println("Tempo de execução P2="+(startTime = System.currentTimeMillis() - startTime));
        long nextTimeToUpdate = System.currentTimeMillis() + PERIOD;
        do {
            if (System.currentTimeMillis() >= nextTimeToUpdate) {
                totalAction++;
                startTime = System.currentTimeMillis();

                //PlayerAction pa1 = ai1.getAction(0, gs);
                PlayerAction pa1 = ai1.getAction(0, new PartiallyObservableGameState(gs,0));
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
                //PlayerAction pa2 = ai2.getAction(1, gs);
                PlayerAction pa2 = ai2.getAction(1, new PartiallyObservableGameState(gs,1));
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

        } while (!gameover && (gs.getTime() < MAXCYCLES) && (duracao.toMinutes() < 7));

        log.add("Total de actions= " + totalAction + " sumAi1= " + sumAi1 + " sumAi2= " + sumAi2 + "\n");

        log.add("Tempos de AI 1 = " + ai1.toString());
        log.add("Tempo minimo= " + ai1TempoMin + " Tempo maximo= " + ai1TempoMax + " Tempo medio= " + (sumAi1 / (long) totalAction));

        log.add("Tempos de AI 2 = " + ai2.toString());
        log.add("Tempo minimo= " + ai2TempoMin + " Tempo maximo= " + ai2TempoMax + " Tempo medio= " + (sumAi2 / (long) totalAction) + "\n");

        log.add("Winner " + Integer.toString(gs.winner()));
        log.add("Game Over");

        gravarLog(log, tupleAi1, tupleAi2, IDMatch, Generation, pathLog);
        //System.exit(0);
        return true;
    }

    public static List<AI> decodeScripts(UnitTypeTable utt, ArrayList<Integer> iScripts) {
        List<AI> scriptsAI = new ArrayList<>();

        POScriptsCreator sc = new POScriptsCreator(utt,300);
        ArrayList<POBasicExpandedConfigurableScript> scriptsCompleteSet = sc.getScriptsMixReducedSet();

        for (Integer idSc : iScripts) {
            scriptsAI.add(scriptsCompleteSet.get(idSc));
        }

        return scriptsAI;
    }

    private void gravarLog(ArrayList<String> log, String tupleAi1, String tupleAi2, Integer IDMatch, Integer Generation, String pathLog) throws IOException {
        if(!pathLog.endsWith("/")){
            pathLog +="/";
        }
        String nameArquivo = pathLog + "Eval_" + tupleAi1 + "_" + tupleAi2 + "_" + IDMatch + "_" + Generation + ".txt";
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
