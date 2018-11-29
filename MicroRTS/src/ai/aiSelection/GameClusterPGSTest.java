/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.aiSelection;

import ai.core.AI;
import ai.*;
import ai.evaluation.EvalSelection;
import java.util.ArrayList;
import rts.GameState;
import rts.PhysicalGameState;
import rts.PlayerAction;
import rts.units.UnitTypeTable;

/**
 *
 * @author santi
 */
public class GameClusterPGSTest {

    static String _nameStrategies = "", _enemy = "";
    static AI[] strategies = null;

    public static void main(String args[]) throws Exception {
        
        String tuple = args[0];
        
        System.out.println("Tupla= "+tuple);
        
        //decompõe a tupla
        ArrayList<Integer> iScripts = new ArrayList<>();
        String[] itens = tuple.split(";");
        
        for (String element : itens) {
            System.out.println("Itens tupla= "+ element);
            iScripts.add(Integer.decode(element));
        }
        
        UnitTypeTable utt = new UnitTypeTable();
        PhysicalGameState pgs = PhysicalGameState.load("maps/8x8/basesWorkers8x8A.xml", utt);
        //PhysicalGameState pgs = PhysicalGameState.load("maps/NoWhereToRun9x8.xml", utt);
        //PhysicalGameState pgs = PhysicalGameState.load("maps/16x16/basesWorkers16x16A.xml", utt);   
        

        GameState gs = new GameState(pgs, utt);
        int MAXCYCLES = 8000;
        int PERIOD = 20;
        boolean gameover = false;
        

        AI ai1 = new PassiveAI(utt);
        //AI ai2 = new AISelection(utt);   
        
        AI ai2 = new AISelectionSimple(utt, iScripts);   
        
        System.out.println("---------AI's---------");
        System.out.println("AI 1 = "+ai1.toString());
        System.out.println("AI 2 = "+ai2.toString()+"\n");        
        
        
        
        //PlayerAction pa2 = ai2.getAction(1, gs);
        long startTime = System.currentTimeMillis();
        long nextTimeToUpdate = System.currentTimeMillis() + PERIOD;
        do {
            if (System.currentTimeMillis() >= nextTimeToUpdate) {
                startTime = System.currentTimeMillis();
                PlayerAction pa1 = ai1.getAction(0, gs);  
                //System.out.println("Tempo de execução P1="+(startTime = System.currentTimeMillis() - startTime));
                
                startTime = System.currentTimeMillis();
                PlayerAction pa2 = ai2.getAction(1, gs);
                //System.out.println("Tempo de execução P2="+(startTime = System.currentTimeMillis() - startTime));
                
                gs.issueSafe(pa1);
                gs.issueSafe(pa2);

                // simulate:
                gameover = gs.cycle();
                nextTimeToUpdate += PERIOD;
                
                //teste ltd2
                EvalSelection e = new EvalSelection();
                //System.out.println("EvalSelection ="+ e.evaluate(0, 1, gs));
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
