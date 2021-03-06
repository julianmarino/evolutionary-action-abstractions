/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.ScriptsGenerator.ParametersConcrete;

import ai.ScriptsGenerator.IParameters.IDistance;
import ai.ScriptsGenerator.IParameters.IQuantity;

/**
 *
 * @author rubens
 */
public class DistanceParam implements IDistance{

    private int value;

    public DistanceParam() {
        this.value = 0;
    }

    public DistanceParam(int value) {
        this.value = value;
    }
    
    @Override
    public int getDistance() {
        return value;
    }

    @Override
    public void setDistance(int value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "DistanceParam:{" + "value=" + value + '}';
    }
    
    
    
}
