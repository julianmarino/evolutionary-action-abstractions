/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.asymmetric.SSS;

import java.util.ArrayList;
import java.util.Objects;
import rts.GameState;
import rts.units.Unit;
import rts.units.UnitType;
import rts.units.UnitTypeTable;

/**
 *
 * @author rubens
 */
public class AdaptableStratType {
    // 0 = ranged 1= melle 2= bases e barracas
    private int _unitType;
    private int _hpLevel;
    static private int _knob;
    static private boolean _singleType;
    static private boolean _attackType;
    static private int _counter;
    static private ArrayList<Integer> _numberTypes = new ArrayList<>();
    
    
    public AdaptableStratType(){
        this._unitType = -1;
        this._hpLevel = -1;
    
    }
   
    public AdaptableStratType(Unit unit, GameState state){
    
        if(_singleType){
            this._unitType = -1;
            this._hpLevel = -1;
        }else if(_attackType){
            //if the unit is a Ranged
            if(unit.getType().name.equals("Ranged")){
                this._unitType = 0;
            }else if(unit.getType().name.equals("Heavy") ||
                    unit.getType().name.equals("Worker") ||
                    unit.getType().name.equals("Light") ){
                this._unitType = 1;
            }else{ //if the unit is a heavy, work, light
                this._unitType = 2;
            }
            this._hpLevel = -1;
          
        }else{
            //dividindo pelo tipo de unidade
            this._unitType = unit.getType().ID;
           /* 
            if(unit.getType().name.equals("Ranged")){
                this._unitType = 0;
            }else if(unit.getType().name.equals("Heavy") ){ //if the unit is a heavy, work, light
                
                this._unitType = 1;
            }else if(unit.getType().name.equals("Light") 
                    ){
                this._unitType = 2;
            }else if(unit.getType().name.equals("Worker") 
                    ){
                this._unitType = 4;
            }
            */
            if(_knob > 0){
                this._hpLevel = unit.getHitPoints() / (unit.getMaxHitPoints() / _knob );
            }else{
                this._hpLevel = -1;
            }
        }
        
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 29 * hash + this._unitType;
        hash = 29 * hash + this._hpLevel;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AdaptableStratType other = (AdaptableStratType) obj;
        if (this._unitType != other._unitType) {
            return false;
        }
        if (this._hpLevel != other._hpLevel) {
            return false;
        }
        return true;
    }
    
    
    
    public void print(){
        System.out.println("Print Adaptable StratType "+ _unitType + "\t"+ _hpLevel);
    }
    
    public static void increase(double timePlayout, int timeLimit, int portfolioSize){
        //if the number of types is empty then the type system is at its finest form
        if(_numberTypes.isEmpty()){
            return ;
        }
        
        //estimating whether the search algorithm will be able to handle a finer type system
        if( (_numberTypes.get(_numberTypes.size()-1) * timePlayout * portfolioSize ) < timeLimit ){
            _numberTypes.remove(_numberTypes.size()-1);
            if(_singleType){
                _singleType = false;
            }else if(_attackType){
                _attackType = false;
            }else if(_knob < 3){
                _knob++;
            }
        }
    }
    
    public static void decrease(int numberTypes){
        //if hasn't reached the coarsest type system
        if(!_singleType){
            _numberTypes.add(numberTypes);
        }
        
        if(_knob > 0){
            _knob --;
        }else if(!_attackType){
            _attackType = true;
        }else{
            _singleType = true;
        }
    }
    
    public static void printType(){
        if(_singleType){
            System.out.println("Type = -1" );
        }else if(_attackType){
            System.out.println("Type = 0" );
        }else{
            System.out.println("Type = "+(_knob+1) );
        }
    }

    public boolean menor(AdaptableStratType o) {
        if(this._unitType != o._unitType){
            return this._unitType < o._unitType;
        }
        return this._hpLevel < o._hpLevel;
    }
    
    
    
}
