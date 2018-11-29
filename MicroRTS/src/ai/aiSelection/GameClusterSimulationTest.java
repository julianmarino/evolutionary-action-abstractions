/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.aiSelection;

import ai.core.AI;
import ai.*;
import java.util.ArrayList;
import rts.GameState;
import rts.PhysicalGameState;
import rts.PlayerAction;
import rts.units.UnitTypeTable;

/**
 *
 * @author santi
 */
public class GameClusterSimulationTest {

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
        //PhysicalGameState pgs = PhysicalGameState.load("maps/8x8/basesWorkers8x8A.xml", utt);
        //PhysicalGameState pgs = PhysicalGameState.load("maps/8x8/basesWorkersBarracks8x8.xml", utt); // Com os scripts:[4, 118]
        //PhysicalGameState pgs = PhysicalGameState.load("maps/8x8/FourBasesWorkers8x8.xml", utt); Com os scripts:[261, 3]
        //PhysicalGameState pgs = PhysicalGameState.load("maps/8x8/basesWorkers8x8Obstacle.xml", utt); //Com os scripts:[30, 101]
        //PhysicalGameState pgs = PhysicalGameState.load("maps/8x8/basesWorkers8x8A.xml", utt); // Com os scripts:[261, 92, 0]
        //PhysicalGameState pgs = PhysicalGameState.load("maps/8x8/basesWorkers8x8B.xml", utt);  // Com os scripts:[246, 0, 2]
        PhysicalGameState pgs = PhysicalGameState.load("maps/8x8/basesWorkers8x8D.xml", utt); //Com os scripts:[4, 101]
        //PhysicalGameState pgs = PhysicalGameState.load("maps/NoWhereToRun9x8.xml", utt);
        //PhysicalGameState pgs = PhysicalGameState.load("maps/16x16/basesWorkers16x16A.xml", utt);   
        

        GameState gs = new GameState(pgs, utt);

        AI ai1 = new PassiveAI(utt);
        //AI ai2 = new AISelection(utt);   
        
        AI ai2 = new AISelectionSimple(utt, iScripts);   
        
        System.out.println("---------AI's---------");
        System.out.println("AI 1 = "+ai1.toString());
        System.out.println("AI 2 = "+ai2.toString()+"\n");        
        
        
        
        PlayerAction pa2 = ai2.getAction(1, gs);
        
        System.out.println("Game Over");
    }


}
