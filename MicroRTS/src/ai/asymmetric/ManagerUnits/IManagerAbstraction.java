/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.asymmetric.ManagerUnits;

import java.util.HashSet;
import rts.GameState;
import rts.units.Unit;

/**
 *
 * @author rubens
 */
public interface IManagerAbstraction {
    public void controlUnitsForAB(GameState state, HashSet<Unit> unidades); 

    
}
