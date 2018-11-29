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
public class ManagerLessDPS extends ManagerAbstraction {

    private int _cenX, _cenY;

    public ManagerLessDPS(int _playerID, int numUnits) {
        super(_playerID, numUnits);
    }

    @Override
    public void controlUnitsForAB(GameState state, HashSet<Unit> unidades) {
        unidades.clear();
        if(getNumUnits() == 0){
            return ;
        }
        if (unidades.isEmpty()) {
            Unit u = state.getUnit(getIDUnitLessDPS(state));
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

    private long getIDUnitLessDPS(GameState state) {
        Unit un = ManagerAbstraction.getUnitsPlayer(state, getPlayerID()).get(0);
        double DPS = 1000000;

        for (Unit t : ManagerAbstraction.getUnitsPlayer(state, getPlayerID())) {

            double DPSunit = ((double) t.getMaxDamage() / (double) t.getAttackTime()) / t.getHitPoints();

            if (DPS > DPSunit) {
                DPS = DPSunit;
                un = t;
            }
        }

        return un.getID();
    }

    private boolean hasMoreDPS(Unit u1, Unit u2) {
        double u1Threat = ((double) u1.getMaxDamage() / (double) u1.getAttackTime()) / u1.getHitPoints();
        double u2Threat = ((double) u2.getMaxDamage() / (double) u2.getAttackTime()) / u2.getHitPoints();

        return u1Threat > u2Threat;
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
            while ((j >= 0)
                    && ( hasMoreDPS(unidades.get(j), key))
                    ) {

                unidades.set((j + 1), unidades.get(j));
                j--;
            }
            unidades.set(j + 1, key);
        }
    }

    @Override
    public String toString() {
        return "ManagerLessDPS";
    }
    
    

}
