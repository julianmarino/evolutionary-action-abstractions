/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.ScriptsGenerator.ParametersConcrete;

import java.util.ArrayList;
import java.util.List;

import ai.ScriptsGenerator.Command.Enumerators.EnumPlayerTarget;
import ai.ScriptsGenerator.Command.Enumerators.EnumPositionType;
import ai.ScriptsGenerator.Command.Enumerators.EnumTypeUnits;
import ai.ScriptsGenerator.IParameters.IPlayerTarget;
import ai.ScriptsGenerator.IParameters.IQuantity;

/**
*
* @author rubens
*/
public class PlayerTargetParam implements IPlayerTarget {

    private List<EnumPlayerTarget> selectedPlayerTarget;

    public PlayerTargetParam() {
        this.selectedPlayerTarget = new ArrayList<>();
    }

    public List<EnumPlayerTarget> getSelectedPlayerTarget() {
        return selectedPlayerTarget;
    }

    public void setSelectedPosition(List<EnumPlayerTarget> selectedPlayerTarget) {
        this.selectedPlayerTarget = selectedPlayerTarget;
    }
    
    public void addPlayer(EnumPlayerTarget player){
        if(!selectedPlayerTarget.contains(player)){
            this.selectedPlayerTarget.add(player);
        }
    }

    @Override
    public String toString() {
        return "PlayerTargetParam:{" + "selectedPlayerTarget=" + selectedPlayerTarget + '}';
    }

    
}
