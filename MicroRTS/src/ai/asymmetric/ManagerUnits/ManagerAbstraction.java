/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.asymmetric.ManagerUnits;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import rts.GameState;
import rts.units.Unit;

/**
 *
 * @author rubens
 */
public abstract class ManagerAbstraction implements IManagerAbstraction{

    private int _playerID;
    private int numUnits;
    
    protected Random rand = new Random();

    public int getPlayerID() {
        return _playerID;
    }

    public void setPlayerID(int _playerID) {
        this._playerID = _playerID;
    }

    public int getNumUnits() {
        return numUnits;
    }

    public void setNumUnits(int numUnits) {
        this.numUnits = numUnits;
    }
    
    public ManagerAbstraction(int _playerID, int numUnits) {
        this._playerID = _playerID;
        this.numUnits = numUnits;
    }
    
    public static int numUnitsPlayer(GameState state, int playerID){
        int total = 0;
        
        for(Unit u : state.getUnits()){
            if(u.getPlayer() == playerID){
                total++;
            }
        }
        
        return total;
    }
    
    public static void printUnits(HashSet<Unit> unidades){
        System.out.println("INICIO ----------------------------- Unidades controladas: ");
        for (Unit unidade : unidades) {
            System.out.println(unidade.toString());
        }
        System.out.println("FIM ----------------------------- Unidades controladas ");
    }
 
    public static ArrayList<Unit> getUnitsPlayer(GameState state, int playerID){
        ArrayList<Unit> unRet = new ArrayList<>();
        
        for(Unit u : state.getUnits()){
                if(u.getPlayer() == playerID){
                    unRet.add(u);
                }
        }
        
        return unRet;
    }
    
}
