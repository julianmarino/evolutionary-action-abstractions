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
public class ManagerRandom extends ManagerAbstraction{

    public ManagerRandom(int _playerID, int numUnits) {
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
            if(state.getUnit(unidade.getID()) != null){
                unTemp.add(unidade);
            }
        }
        unidades.clear();
        for (Unit unit : unTemp) {
            unidades.add(unit);
        }
        
        
        if(ManagerAbstraction.numUnitsPlayer(state, this.getPlayerID()) <= getNumUnits()){
            unidades.clear();
            //adiciono todas as unidades para serem controladas pelo AB
            for(Unit u : state.getUnits()){
                if(u.getPlayer() == getPlayerID()){
                    unidades.add(u);
                }
            }
        }else if(unidades.size() < getNumUnits()){
            ArrayList<Unit> unitRandom = new ArrayList<>();
            for(Unit u : state.getUnits()){
                if(u.getPlayer() == getPlayerID()){
                    unitRandom.add(u);
                }
            }
            //seleciono aleatoriamente
            int control = 0;
            while(unidades.size() < getNumUnits() && control < 20){
                Unit ut = unitRandom.get(rand.nextInt(unitRandom.size()));
                if(ut != null){
                    unidades.add(ut);
                }
                control++;
            }
        }
        
    }

    @Override
    public String toString() {
        return "ManagerRandom";
    }
    
    
    
}
