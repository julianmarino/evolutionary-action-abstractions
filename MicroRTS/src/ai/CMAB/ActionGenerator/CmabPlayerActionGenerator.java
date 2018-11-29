/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.CMAB.ActionGenerator;

import java.util.List;
import rts.GameState;
import rts.PlayerActionGenerator;
import rts.UnitAction;
import rts.units.Unit;
import rts.units.UnitTypeTable;
import util.Pair;

/**
 *
 * @author rubens
 */
public class CmabPlayerActionGenerator implements ICMAB_ActionGenerator{
    
    private final PlayerActionGenerator generator;

    public CmabPlayerActionGenerator(GameState a_gs, int pID, UnitTypeTable utt) throws Exception {
        this.generator = new PlayerActionGenerator(a_gs, pID);
    }
    
    

    @Override
    public List<Pair<Unit, List<UnitAction>>> getChoices() {
        return this.generator.getChoices();
    }

    
    @Override
    public long getSize() {
        return this.generator.getSize();
    }
    
}
