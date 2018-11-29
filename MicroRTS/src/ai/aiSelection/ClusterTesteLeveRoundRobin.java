/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.aiSelection;

import ai.core.AI;
import ai.asymmetric.PGS.PGSSCriptChoice;
import ai.configurablescript.BasicExpandedConfigurableScript;
import ai.configurablescript.ScriptsCreator;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import rts.GameState;
import rts.PhysicalGameState;
import rts.PlayerAction;
import rts.units.UnitTypeTable;

/**
 *
 * @author rubens
 */
public class ClusterTesteLeveRoundRobin {

    static String _nameStrategies = "", _enemy = "";
    static AI[] strategies = null;

    public static void main(String args[]) throws Exception { 
        //controle de tempo
        Instant timeInicial = Instant.now();
        Duration duracao;
        
        String tupleAi1 = args[0];
        System.out.println("Tupla A1 = "+tupleAi1);
        
        String tupleAi2 = args[1];
        System.out.println("Tupla A2 = "+tupleAi2);
        
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

        System.out.println("---------AIs---------");
        System.out.println("AI 1 = " + ai1.toString());
        System.out.println("AI 2 = " + ai2.toString() + "\n");

        System.out.println("---------Mapa---------");
        System.out.println("Mapa= " + map + "\n");

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
            
        } while (!gameover && (gs.getTime() < MAXCYCLES) && (duracao.toMinutes() < 7));
        
        System.out.println("Total de actions= " + totalAction + " sumAi1= " + sumAi1 + " sumAi2= " + sumAi2 + "\n");

        System.out.println("Tempos de AI 1 = " + ai1.toString());
        System.out.println("Tempo minimo= " + ai1TempoMin + " Tempo maximo= " + ai1TempoMax + " Tempo medio= " + (sumAi1 / (long) totalAction));

        System.out.println("Tempos de AI 2 = " + ai2.toString());
        System.out.println("Tempo minimo= " + ai2TempoMin + " Tempo maximo= " + ai2TempoMax + " Tempo medio= " + (sumAi2 / (long) totalAction) + "\n");

        System.out.println("Winner " + Integer.toString(gs.winner()));
        System.out.println("Game Over");
        System.exit(0);
    }

    
    public static List<AI> decodeScripts(UnitTypeTable utt, ArrayList<Integer> iScripts){
        List<AI> scriptsAI = new ArrayList<>();
        
        ScriptsCreator sc = new ScriptsCreator(utt,300);
        ArrayList<BasicExpandedConfigurableScript> scriptsCompleteSet = sc.getScriptsMixReducedSet();
        
        for (Integer idSc : iScripts) {
            scriptsAI.add( scriptsCompleteSet.get(idSc) );
        }
        
        return scriptsAI;
    }
}
