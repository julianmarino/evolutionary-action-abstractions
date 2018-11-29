/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.asymmetric.ManagerUnits;

import java.util.ArrayList;
import java.util.HashSet;
import rts.GameState;
import rts.units.Unit;

/**
 *
 * @author rubens
 */
public class ManagerClosest extends ManagerAbstraction {

    private int _cenX, _cenY;

    public ManagerClosest(int _playerID, int numUnits) {
        super(_playerID, numUnits);
    }

    @Override
    public void controlUnitsForAB(GameState state, HashSet<Unit> unidades) {
        if(getNumUnits() == 0){
            unidades.clear();
            return ;
        }
        unitsDie(state, unidades);

        if (unidades.isEmpty()) {
            ArrayList<Unit> unT = ManagerAbstraction.getUnitsPlayer(state, getPlayerID());
            unidades.add(unT.get(rand.nextInt(unT.size())));
        }

        calcularCentroide(unidades);

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
                        calcularCentroide(unidades);
                    }
                }
                control++;
            }
        }

    }

    private void unitsDie(GameState state, HashSet<Unit> unidades) {
        //verifico se as unidades não foram mortas
        HashSet<Unit> tempUnitAbsAB = new HashSet<>();
        for (Unit un : unidades) {
            if (state.getUnit(un.getID()) != null) {
                tempUnitAbsAB.add(un);
            }
        }

        unidades.clear();
        for (Unit unit : tempUnitAbsAB) {
            unidades.add(unit);
        }
    }

    private void calcularCentroide(HashSet<Unit> unidades) {
        int x = 0, y = 0;
        for (Unit un : unidades) {
            x += un.getX();
            y += un.getY();
        }
        x = x / unidades.size();
        y = y / unidades.size();
        _cenX = x;
        _cenY = y;
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
            while ((j >= 0) && (getDistEuclidiana(_cenX, _cenY, unidades.get(j).getX(), unidades.get(j).getY())
                    > getDistEuclidiana(_cenX, _cenY, key.getX(), key.getY()))
                    && (unidades.get(j).getHitPoints() >= key.getHitPoints())) {
                unidades.set((j + 1), unidades.get(j));
                j--;
            }
            unidades.set(j + 1, key);
        }
    }

    private double getDistEuclidiana(int pXinicial, int pYinicial, int pXfinal, int pYfinal) {
        return Math.sqrt(((pXinicial - pXfinal) * (pXinicial - pXfinal)
                + (pYinicial - pYfinal) * (pYinicial - pYfinal)));
    }

    private boolean unitExistInArray(Unit unit, HashSet<Unit> unidades) {
        for (Unit un : unidades) {
            if (un.getID() == unit.getID()) {
                return true;
            }
        }
        return false;
    }

    private boolean existUnitsToAdd(GameState state, HashSet<Unit> unidades) {

        for (Unit t : ManagerAbstraction.getUnitsPlayer(state, getPlayerID())) {
            if (!unitExistInArray(t, unidades)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return "ManagerClosest";
    }
    
    

}
