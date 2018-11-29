/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rts;

import ai.abstraction.LightRush;
import ai.core.AI;
import java.util.ArrayList;
import java.util.List;
import rts.units.Unit;
import rts.units.UnitTypeTable;
import util.Pair;

/**
 *
 * @author rubens
 */
public class PlayerActionScriptGeneration extends PlayerActionGenerator {

    ArrayList<AI> scripts = new ArrayList<>();

    public PlayerActionScriptGeneration(GameState a_gs, int pID, ArrayList<AI> baseScripts) throws Exception {
        super(a_gs, pID);
        scripts = baseScripts;
        // Generate the reserved resources:
        base_ru = new ResourceUsage();
        physicalGameState = gameState.getPhysicalGameState();

        for (Unit u : physicalGameState.getUnits()) {
            UnitActionAssignment uaa = gameState.unitActions.get(u);
            if (uaa != null) {
                ResourceUsage ru = uaa.action.resourceUsage(u, physicalGameState);
                base_ru.merge(ru);
            }
        }

        choices = new ArrayList<>();
        for (Unit u : physicalGameState.getUnits()) {
            if (u.getPlayer() == pID) {
                if (gameState.unitActions.get(u) == null) {
                    //List<UnitAction> l = u.getUnitActions(gs);
                    List<UnitAction> l = takeUnitScriptMove(u,  pID);
                    choices.add(new Pair<>(u, l));
                    // make sure we don't overflow:
                    long tmp = l.size();
                    if (Long.MAX_VALUE / size <= tmp) {
                        size = Long.MAX_VALUE;
                    } else {
                        size *= (long) l.size();
                    }
//                    System.out.println("size = " + size);
                }
            }
        }
//        System.out.println("---");

        if (choices.size() == 0) {
            System.err.println("Problematic game state:");
            System.err.println(a_gs);
            throw new Exception("Move generator for player " + pID + " created with no units that can execute actions! (status: " + a_gs.canExecuteAnyAction(0) + ", " + a_gs.canExecuteAnyAction(1) + ")");
        }

        choiceSizes = new int[choices.size()];
        currentChoice = new int[choices.size()];
        int i = 0;
        for (Pair<Unit, List<UnitAction>> choice : choices) {
            choiceSizes[i] = choice.m_b.size();
            currentChoice[i] = 0;
            i++;
        }

    }

    private List<UnitAction> takeUnitScriptMove(Unit u, int player) throws Exception {
        List<UnitAction> actionUnit = new ArrayList<>();        
        for (AI ai : this.scripts) {
        	AI newAi=ai.clone();
        	newAi.reset();
            PlayerAction acScript = newAi.getAction(player, gameState);
            //System.out.println("Action= "+acScript.toString());
            UnitAction uAcTemp = acScript.getAction(gameState.getUnit(u.getID()));
            if (uAcTemp != null) {
            	boolean exist=false;
                for (UnitAction element : actionUnit) {
                    if(uAcTemp.equals(element))
                    {
                    	exist=true;
                    	break;
                    }                    
                }
                if(!exist)
                {
                	actionUnit.add(uAcTemp);
                }
            }
        }
        //verifica se já tem ação NONE
        if( !contemNone(actionUnit)){
            //actionUnit.add(new UnitAction(UnitAction.TYPE_NONE, 10));
        }      
        
        //imprimir lista de movimentos
        /*
        System.out.println("Unit: "+ u.toString());
        for (UnitAction unitAction : actionUnit) {
            System.out.println(unitAction.toString());
        }
        */
        return actionUnit;

    }

    private boolean contemNone(List<UnitAction> actionUnit) {
        
        for (UnitAction unitAction : actionUnit) {
            if(unitAction.getType() == UnitAction.TYPE_NONE ){
                return true;
            }
        }
        
        return false;
    }

}
