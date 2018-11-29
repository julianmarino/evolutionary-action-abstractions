/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.asymmetric.ManagerUnits;

import java.util.ArrayList;
import java.util.HashSet;
import rts.GameState;
import rts.PhysicalGameState;
import rts.units.Unit;

/**
 *
 * @author rubens
 */
public class ManagerClosestEnemy extends ManagerAbstraction {

    private int _cenX, _cenY;

    public ManagerClosestEnemy(int _playerID, int numUnits) {
        super(_playerID, numUnits);
    }

    @Override
    public void controlUnitsForAB(GameState state, HashSet<Unit> unidades) {
        unidades.clear();
        if(getNumUnits() == 0){
            return ;
        }
        if (unidades.isEmpty()) {
            Unit u = state.getUnit(getIDUnitMoreClosest(state));
            unidades.add(u);
        }

        if (ManagerAbstraction.numUnitsPlayer(state, this.getPlayerID()) <= getNumUnits()) {
            unidades.clear();
            //adiciono todas as unidades para serem controladas pelo AB
            for (Unit u : state.getUnits()) {
                if (u.getPlayer() == getPlayerID()) {
                    unidades.add(u);
                }
            }
        } else if (unidades.size() < getNumUnits()) {
            int control = 0;
            while (unidades.size() < getNumUnits() && control < 20) {
                if (existUnitsToAdd(state, unidades)) {
                    Unit ut = state.getUnit(getIDUnitAdd(state, unidades));
                    if (ut != null) {
                        unidades.add(ut);
                    }
                }
                control++;
            }
        }
    }

    private long getIDUnitMoreClosest(GameState state) {
        Unit un = ManagerAbstraction.getUnitsPlayer(state, getPlayerID()).get(0);
        double minDist = 1000000;

        for (Unit t : ManagerAbstraction.getUnitsPlayer(state, getPlayerID())) {
            Unit enemy = getClosestEnemyUnit(t, state);
            double distSq = getDistanceSqToUnit(t.getX(), t.getY(), enemy.getX(), enemy.getY());
            if (distSq < minDist) {
                minDist = distSq;
                un = t;
            }
        }

        return un.getID();
    }

    private boolean existUnitsToAdd(GameState state, HashSet<Unit> unidades) {

        for (Unit t : ManagerAbstraction.getUnitsPlayer(state, getPlayerID())) {
            if (!unitExistInArray(t, unidades)) {
                return true;
            }
        }
        return false;
    }

    private boolean unitExistInArray(Unit unit, HashSet<Unit> unidades) {
        for (Unit un : unidades) {
            if (un.getID() == unit.getID()) {
                return true;
            }
        }
        return false;
    }

    private long getIDUnitAdd(GameState state, HashSet<Unit> unidades) {
        ArrayList<Unit> unitOrdenar = new ArrayList<>();

        for (Unit t : ManagerAbstraction.getUnitsPlayer(state, getPlayerID())) {
            if (!unitExistInArray(t, unidades)) {
                unitOrdenar.add(t);
            }
        }
        sortUnits(unitOrdenar, state);

        return unitOrdenar.get(0).getID();
    }

    private void sortUnits(ArrayList<Unit> unidades, GameState state) {
        for (int i = 1; i < unidades.size(); i++) {
            Unit key = unidades.get(i);
            int j = i - 1;
            Unit enemy = getClosestEnemyUnit(unidades.get(j), state);
            Unit enKey = getClosestEnemyUnit(key, state);
            while ((j >= 0)
                    && (getDistanceSqToUnit(unidades.get(j).getX(), unidades.get(j).getY(), enemy.getX(), enemy.getY())
                    >= getDistanceSqToUnit(key.getX(), key.getY(), enKey.getX(), enKey.getY()))) {

                unidades.set((j + 1), unidades.get(j));
                j--;
                if (j >= 0) {
                    enemy = getClosestEnemyUnit(unidades.get(j), state);
                }
            }
            unidades.set(j + 1, key);
        }
    }

    private Unit getClosestEnemyUnit(Unit allyUnit, GameState state) {
        PhysicalGameState pgs = state.getPhysicalGameState();
        Unit closestEnemy = null;
        int closestDistance = 0;
        for (Unit u2 : pgs.getUnits()) {
            if (u2.getPlayer() >= 0 && u2.getPlayer() != getPlayerID()) {
                int d = Math.abs(u2.getX() - allyUnit.getX()) + Math.abs(u2.getY() - allyUnit.getY());
                if (closestEnemy == null || d < closestDistance) {
                    closestEnemy = u2;
                    closestDistance = d;
                }
            }
        }
        return closestEnemy;
    }

    private double getDistanceSqToUnit(int pXinicial, int pYinicial, int pXfinal, int pYfinal) {
        return ((pXinicial - pXfinal) * (pXinicial - pXfinal))
                + ((pYinicial - pYfinal) * (pYinicial - pYfinal));
    }

    @Override
    public String toString() {
        return "ManagerClosestEnemy";
    }
    
    
}
