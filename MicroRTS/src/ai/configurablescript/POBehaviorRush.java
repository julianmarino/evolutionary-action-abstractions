/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.configurablescript;

import rts.GameState;
import rts.PartiallyObservableGameState;
import rts.PhysicalGameState;
import rts.Player;
import rts.UnitAction;
import rts.units.Unit;

/**
 *
 * @author rubens
 */
public class POBehaviorRush extends BehaviorRush {

	public POBehaviorRush(BasicExpandedConfigurableScript bec, PhysicalGameState pgs, Player p, GameState gs, int player) {
		super(bec, pgs, p, gs, player);
	}

	@Override
	public void meleeUnitBehavior(Unit u, Player p, GameState gs) {

		PhysicalGameState pgs = gs.getPhysicalGameState();
		int ratioAttack=bec.choices.get(BasicExpandedChoicePoint.FORMATIONRADIUS);
		int ratioReactive=bec.choices.get(BasicExpandedChoicePoint.REACTIVERADIUS);
		Unit closestEnemy = null;
		int closestDistance = 0;
		Unit nearestFriend = null;
		int nearestDistance = 0;
		int mybase = 0;
		for (Unit u2 : pgs.getUnits()) {
			if (u2.getPlayer() >= 0 && u2.getPlayer() != p.getID()) {
				int d = Math.abs(u2.getX() - u.getX()) + Math.abs(u2.getY() - u.getY());
				if (closestEnemy == null || d < closestDistance) {
					closestEnemy = u2;
					closestDistance = d;
				}
			}
			else if(u2.getPlayer()==p.getID() && u2.getType() == bec.baseType)
			{
				mybase = Math.abs(u2.getX() - u.getX()) + Math.abs(u2.getY() - u.getY());
			}
		}
		for (Unit u2 : pgs.getUnits()) {
			if (u2.getPlayer() >= 0 && u2.getPlayer() == p.getID()) {
				int d = Math.abs(u2.getX() - u.getX()) + Math.abs(u2.getY() - u.getY());
				if ((nearestFriend == null || d < nearestDistance) && (validationMove(u,u2))) {
					nearestFriend = u2;
					nearestDistance = d;
				}
			}
		}
		if (closestEnemy != null) {
			bec.attack(u, closestEnemy);
		}
		else if (gs instanceof PartiallyObservableGameState) {
			PartiallyObservableGameState pogs = (PartiallyObservableGameState)gs;
			// there are no enemies, so we need to explore (find the fartest non-observable place):
			int fartest_x = 0;
			int fartest_y = 0;
			int fartestDistance = -1;
			for(int i = 0;i<pgs.getHeight();i++) {
				for(int j = 0;j<pgs.getWidth();j++) {
					if (!pogs.observable(j, i)) {
						int d = (u.getX() - j)*(u.getX() - j) + (u.getY() - i)*(u.getY() - i);
						if (fartestDistance == -1 || d>fartestDistance) {
							fartest_x = j;
							fartest_y = i;
							fartestDistance = d;
						}
					}
				}
			}
			if(bec.nmelees+bec.nworkers<bec.choices.get(BasicExpandedChoicePoint.NUNITSATTACK))
			{
				if (validationMove2(u,fartest_x,fartest_y))
				{
					if (fartestDistance!=-1 && (mybase < pgs.getHeight()/ratioAttack)) {
						bec.move(u, fartest_x, fartest_y);
					}
					else
					{
						bec.attack(u, null);
					}
				}else if(nearestFriend!=null)
				{
					//System.out.println("fartestfriend "+fartestFriend.getX()+" "+fartestFriend.getY());
					moveToNearestFriend(u, nearestFriend);
				}
				else
				{
					bec.attack(u,null);
				}
			}
			else
			{
	            // there are no enemies, so we need to explore (find the nearest non-observable place):
	            int closest_x = 0;
	            int closest_y = 0;
	            closestDistance = -1;
	            for(int i = 0;i<pgs.getHeight();i++) {
	                for(int j = 0;j<pgs.getWidth();j++) {
	                    if (!pogs.observable(j, i)) {
	                        int d = (u.getX() - j)*(u.getX() - j) + (u.getY() - i)*(u.getY() - i);
	                        if (closestDistance == -1 || d<closestDistance) {
	                            closest_x = j;
	                            closest_y = i;
	                            closestDistance = d;
	                        }
	                    }
	                }
	            }
	            if (closestDistance!=-1) {
	                bec.move(u, closest_x, closest_y);
	            }
			}
		}
	}

	public boolean validationMove2(Unit unit, int x, int y)
	{
		UnitAction move = bec.getPathFinding().findPathToPositionInRange(unit, x+y*gs.getPhysicalGameState().getWidth(), unit.getAttackRange(), gs, null);
		if (move!=null && gs.isUnitActionAllowed(unit, move)) return true;
		return false;
	}

}
