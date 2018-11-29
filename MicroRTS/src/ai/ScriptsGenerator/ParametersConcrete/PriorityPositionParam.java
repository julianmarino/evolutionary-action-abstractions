/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.ScriptsGenerator.ParametersConcrete;

import ai.ScriptsGenerator.Command.Enumerators.EnumPositionType;
import ai.ScriptsGenerator.IParameters.IPriorityPosition;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author rubens
 */
public class PriorityPositionParam implements IPriorityPosition{
    private List<EnumPositionType> selectedPosition;

    public PriorityPositionParam() {
        this.selectedPosition = new ArrayList<>();
    }

    public List<EnumPositionType> getSelectedPosition() {
        return selectedPosition;
    }

    public void setSelectedPosition(List<EnumPositionType> selectedPosition) {
        this.selectedPosition = selectedPosition;
    }
    
    public void addPosition(EnumPositionType position){
        if(!selectedPosition.contains(position)){
            this.selectedPosition.add(position);
        }
    }

    @Override
    public String toString() {
        return "PriorityPositionParam:{" + "selectedPosition=" + selectedPosition + '}';
    }
    
    
    
}
