/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.CMAB.ActionGenerator;

import java.util.List;
import rts.UnitAction;
import rts.units.Unit;
import util.Pair;

/**
 * This interface will be used to control the selection MABg (epsilon_g).
 * @author rubens
 */
public interface ICMAB_ActionGenerator {
    
    /**
     * This function return the total of combinations possible between all choices.
     * @return 
     */
    public long getSize();
    /**
     * Return all actions possible organized in Pairs unit->actions
     * @return 
     */
    public List<Pair<Unit,List<UnitAction>>> getChoices();
    
}
