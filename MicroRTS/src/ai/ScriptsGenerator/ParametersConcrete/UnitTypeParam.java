/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.ScriptsGenerator.ParametersConcrete;

import ai.ScriptsGenerator.Command.Enumerators.EnumTypeUnits;
import java.util.List;

/**
 *
 * @author rubens
 */
public class UnitTypeParam extends TypeConcrete {

    public boolean addType(EnumTypeUnits enType) {
        if (enType == EnumTypeUnits.Heavy
                || enType == EnumTypeUnits.Light
                || enType == EnumTypeUnits.Ranged
                || enType == EnumTypeUnits.Worker) {
            selectedTypes.add(enType);
            return true;
        }else{
            return false;
        }
    }

    @Override
    public List<EnumTypeUnits> getParamTypes() {
        return selectedTypes;
    }

    @Override
    public String toString() {
        return "UnitTypeParam:{selectedTypes="+selectedTypes+ '}';
    }

    
    
}
