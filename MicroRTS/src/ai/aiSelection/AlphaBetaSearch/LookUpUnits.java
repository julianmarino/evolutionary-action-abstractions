/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.aiSelection.AlphaBetaSearch;

import java.util.HashMap;
import rts.GameState;
import rts.PhysicalGameState;
import rts.units.Unit;

/**
 *
 * @author rubens
 */
public class LookUpUnits {
    //lookup table unitIndex -> unitID
    HashMap<Long, Integer> lookupId = new HashMap<>();
    Integer Index;

    public LookUpUnits() {
        lookupId = new HashMap<>();
        Index = 0;
    }
    
    public void clean(){
        lookupId.clear();
        Index = 0;
    }
        
    public Integer InsertUnitIndex(Long unitID){
        Integer ret = this.Index;
        this.Index++;
        this.lookupId.put(unitID, ret);        
        return ret;
    }
    
    public Integer getUnitIndex(Long unitID){
        return this.lookupId.get(unitID);
    }
    
    public boolean UnitIDInserted(Long unitID){
        return this.lookupId.containsKey(unitID);           
    }
    
    public Long getOrigIDUnit(Integer unitIndex){
        for(Long l : lookupId.keySet()){
            if(lookupId.get(l).equals(unitIndex) ){
                return l;
            }
        }
        return null;
    }
    
    public void refreshLookup(GameState state){
        PhysicalGameState pgs = state.getPhysicalGameState();
        for (Unit u : pgs.getUnits()) {
            if(u.getPlayer() == 1 || u.getPlayer() == 0){
                if(!this.UnitIDInserted(u.getID())){
                    this.InsertUnitIndex(u.getID());
                }                
            }
        }
    }
    
    
    
}
