/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.configurablescript;

import ai.abstraction.pathfinding.PathFinding;
import rts.GameState;
import rts.PhysicalGameState;
import rts.Player;
import rts.units.UnitTypeTable;

/**
 *
 * @author rubens
 */
public class POBasicExpandedConfigurableScript extends BasicExpandedConfigurableScript {

    public POBasicExpandedConfigurableScript(UnitTypeTable a_utt) {
        super(a_utt);
    }

    public POBasicExpandedConfigurableScript(UnitTypeTable a_utt, PathFinding a_pf) {
        super(a_utt, a_pf);
    }

    public POBasicExpandedConfigurableScript(UnitTypeTable a_utt, PathFinding a_pf, int strategy, int nBasesExpand, int nBarracksExpand, int nUnitsAttack, int formationRadius, int defenseRadius, int reactiveRadius, int nUnitsHarvest, int nUnitType) {
        super(a_utt, a_pf, strategy, nBasesExpand, nBarracksExpand, nUnitsAttack, formationRadius, defenseRadius, reactiveRadius, nUnitsHarvest, nUnitType);
    }

    @Override
    public ConfigurableScript<BasicExpandedChoicePoint> clone() {
        POBasicExpandedConfigurableScript sc = new POBasicExpandedConfigurableScript(utt, pf);
        sc.choices = choices.clone();
        return sc;
    }

    @Override
    public void behaviourEconomyRushSimple(BasicExpandedConfigurableScript bec, PhysicalGameState pgs, Player p, GameState gs, int player) {
        new POBehaviorEconomyRushSimple(bec, pgs, p, gs, player);
    }

    @Override
    public void behaviourDefense(BasicExpandedConfigurableScript bec, PhysicalGameState pgs, Player p, GameState gs, int player) {
        new POBehaviorDefense(bec, pgs, p, gs, player);
    }

    @Override
    public void behaviourRush(BasicExpandedConfigurableScript bec, PhysicalGameState pgs, Player p, GameState gs, int player) {
        new POBehaviorRush(bec, pgs, p, gs, player);
    }
    
    

}
