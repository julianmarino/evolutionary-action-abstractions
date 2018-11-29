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
public class ManagerUnitsMelee extends ManagerAbstraction {

    public ManagerUnitsMelee(int _playerID, int numUnits) {
        super(_playerID, numUnits);
    }

    @Override
    public void controlUnitsForAB(GameState state, HashSet<Unit> unidades) {
        if(getNumUnits() == 0){
            unidades.clear();
            return ;
        }
        //verifico se as unidades não foram mortas
        HashSet<Unit> unTemp = new HashSet<>();
        for (Unit unidade : unidades) {
            if (state.getUnit(unidade.getID()) != null) {
                unTemp.add(unidade);
            }
        }
        unidades.clear();
        for (Unit unit : unTemp) {
            unidades.add(unit);
        }

        if (ManagerAbstraction.numUnitsPlayer(state, this.getPlayerID()) <= getNumUnits()) {
            unidades.clear();
            //adiciono todas as unidades para serem controladas pelo AB
            /*
            for (Unit u : state.getUnits()) {
                if (u.getPlayer() == getPlayerID()
                        && (u.getType().name.equals("Light")
                        || u.getType().name.equals("Heavy")
                        || u.getType().name.equals("Ranged"))) {
                    unidades.add(u);
                }
            }
            */
        } else if (unidades.size() < getNumUnits()) {
            ArrayList<Unit> unitRandom = new ArrayList<>();
            for (Unit u : state.getUnits()) {
                if (u.getPlayer() == getPlayerID()
                        && (u.getType().name.equals("Light")
                        || u.getType().name.equals("Heavy")
                        || u.getType().name.equals("Ranged"))) {
                    unitRandom.add(u);
                }
            }
            if (unitRandom.size()>0) {
                //seleciono aleatoriamente
                int control = 0;
                while (unidades.size() < getNumUnits() && control < 20) {
                    Unit ut = unitRandom.get(rand.nextInt(unitRandom.size()));
                    if (ut != null) {
                        //valido se a unidade é próxima de 6 do inimigo mais próximo
                        Unit enemyClosest = getClosestEnemyUnit(ut, state);
                        if (40 >= getDistanceSqToUnit(ut.getX(), ut.getY(), enemyClosest.getX(), enemyClosest.getY())) {
                            unidades.add(ut);
                        }
                    }
                    control++;
                }
            }
        }

    }

    private double getDistanceSqToUnit(int pXinicial, int pYinicial, int pXfinal, int pYfinal) {
        return ((pXinicial - pXfinal) * (pXinicial - pXfinal))
                + ((pYinicial - pYfinal) * (pYinicial - pYfinal));
    }

    @Override
    public String toString() {
        return "ManagerUnitsMelee";
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

}
