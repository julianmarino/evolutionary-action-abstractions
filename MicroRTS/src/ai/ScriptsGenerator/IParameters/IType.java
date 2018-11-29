/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.ScriptsGenerator.IParameters;

import ai.ScriptsGenerator.Command.Enumerators.EnumTypeUnits;
import java.util.List;

/**
 *
 * @author rubens
 */
public interface IType extends IParameters{
    public List<EnumTypeUnits> getParamTypes();
}
