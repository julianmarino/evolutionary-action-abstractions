/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package Standard;

import java.util.LinkedList;
import java.util.List;

import rts.GameState;
import rts.units.Unit;

/**
 *
 * @author santi
 */
public class ReducedGameState extends GameState{
    // creates a reduced game state, 
    public ReducedGameState(GameState gs) {
        super(gs.getPhysicalGameState().cloneKeepingUnits(), gs.getUnitTypeTable());
        //unitCancelationCounter = gs.unitCancelationCounter;
        time = gs.getTime();

        unitActions.putAll(gs.getUnitActions());
        
        List<Unit> toDelete = new LinkedList<Unit>();
        for(Unit u:pgs.getUnits()) {
                if (!observable(u.getX(),u.getY(), u.getPlayer())) {
                    toDelete.add(u);
                }
        }
        for(Unit u:toDelete){
        	removeUnit(u);
        	//included above
        	//unitActions.remove(u);
        }
    }

    public boolean observable(int x, int y, int player) {
        for(Unit u:pgs.getUnits()) {
            if (u.getPlayer() >=0 && u.getPlayer() != player) {
                double d = Math.sqrt((u.getX()-x)*(u.getX()-x) + (u.getY()-y)*(u.getY()-y));
                if (d<=u.getType().sightRadius+1) return true;
            }
        }
        
        return false;
    }
}
