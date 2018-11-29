/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.CMAB.ActionGenerator;

import ai.abstraction.WorkerHarvestRush;
import ai.abstraction.partialobservability.POHeavyRush;
import ai.abstraction.partialobservability.POLightRush;
import ai.abstraction.partialobservability.PORangedRush;
import ai.abstraction.partialobservability.POWorkerRush;
import ai.abstraction.pathfinding.AStarPathFinding;
import ai.configurablescript.BasicExpandedConfigurableScript;
import ai.core.AI;
import ai.puppet.BasicConfigurableScript;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import rts.GameState;
import rts.PlayerAction;
import rts.UnitAction;
import rts.units.Unit;
import rts.units.UnitTypeTable;
import util.Pair;

/**
 *
 * @author rubens
 */
public class CmabCombinatorialGenerator implements ICMAB_ActionGenerator {

    private final List<AI> scripts;
    private final GameState gs_to_start_from;
    private final int playerForThisComputation;
    private List<Pair<Unit, List<UnitAction>>> choices;
    private long size = 1;  // this will be capped at Long.MAX_VALUE;

    public CmabCombinatorialGenerator(GameState a_gs, int pID, UnitTypeTable utt) throws Exception {
        this.gs_to_start_from = a_gs;
        this.playerForThisComputation = pID;
        scripts = new ArrayList<>();
        buildPortfolio(utt);
        generateChoices();
    }

    protected final void buildPortfolio(UnitTypeTable utt) {
        //this.scripts.add(new POLightRush(utt));
        //this.scripts.add(new PORangedRush(utt)); 
        //this.scripts.add(new POHeavyRush(utt));
        //this.scripts.add(new POWorkerRush(utt));             
        //this.scripts.add(new BasicExpandedConfigurableScript(utt, new AStarPathFinding(), 18,0,0,1,2,2,-1,-1,3)); //wr
        this.scripts.add(new BasicExpandedConfigurableScript(utt, new AStarPathFinding(), 18,0,0,1,2,2,-1,-1,4)); //lr
        this.scripts.add(new BasicExpandedConfigurableScript(utt, new AStarPathFinding(), 18,0,0,1,2,2,-1,-1,5)); //HR
        this.scripts.add(new BasicExpandedConfigurableScript(utt, new AStarPathFinding(), 18,0,0,1,2,2,-1,-1,6)); //RR
        //this.scripts.add(new WorkerHarvestRush(utt));             
        
    }

    @Override
    public long getSize() {
        return size;
    }

    @Override
    public List<Pair<Unit, List<UnitAction>>> getChoices() {
        return choices;
    }

    private void generateChoices() throws Exception {
        choices = new ArrayList<>();
        ArrayList<PlayerAction> playerActions = new ArrayList<>();
        for (AI script : scripts) {
            playerActions.add(script.getAction(playerForThisComputation, gs_to_start_from));
        }
        PlayerAction pa = new PlayerAction();
        for (Unit u : gs_to_start_from.getUnits()) {
            if (u.getPlayer() == playerForThisComputation) {
                if (gs_to_start_from.getUnitActions().get(u) == null) {
                    List<UnitAction> l = getUnitActions(u, playerActions);
                    if (l.size() > 0 ) {
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
        }
        //System.out.println("Choices "+ choices.toString());
    }

    /**
     * make one collection of unitActions for the unit using all Actions
     *
     * @param u
     * @param playerActions
     * @return
     */
    private List<UnitAction> getUnitActions(Unit u, ArrayList<PlayerAction> playerActions) {
        HashSet<UnitAction> unAction = new HashSet<>();
        for (PlayerAction playerAction : playerActions) {
            unAction.add(playerAction.getAction(u));
        }
        //inserted wait action to fix move problem
        //unAction.add(new UnitAction(UnitAction.TYPE_NONE, 10));
        return new ArrayList<>(unAction);
    }

}
