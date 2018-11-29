/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.asymmetric.common;

import ai.core.AI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import rts.GameState;
import rts.PlayerAction;
import rts.units.Unit;

/**
 *
 * @author rubens
 */
public class UnitScriptData {

    //vai contar o id da unidade
    private HashMap<Unit, AI> _unitScriptMap = new HashMap<Unit, AI>();
    private int playerForThisComputation;
    private AI seed = null;

    public UnitScriptData(int player) {
        playerForThisComputation = player;
    }

    public void setSeedUnits(AI script) {
        this.seed = script.clone();
    }
    
    public List<Unit> getUnits(){
        ArrayList<Unit> units = new ArrayList<>();
        
        for(Unit u : _unitScriptMap.keySet()){
            units.add(u);
        }
        return units;
    }

    public void reset() {
        _unitScriptMap.clear();
        if (seed != null) {
            seed = seed.clone();
        }
    }

    public void setUnitScript(Unit unit, AI ai) {
        this._unitScriptMap.put(unit, ai.clone());
    }

    public PlayerAction getAction(int player, GameState gs2) throws Exception {
        HashMap<AI, PlayerAction> actions = new HashMap<>();
        PlayerAction pAction = new PlayerAction();

        for (AI ai : _unitScriptMap.values()) {
            actions.put(ai, ai.getAction(player, gs2));
        }
        //compõe o vetor de ações baseado nos scripts
        PlayerAction temp;
        for (Unit u : gs2.getUnits()) {
            if (u.getPlayer() == player) {
                temp = actions.get(getAIUnit(u));
                
                if (temp == null) {
                    AI ai = seed.clone();
                    temp = ai.getAction(player, gs2);
                }
                if (temp.getAction(u) != null) {
                    pAction.addUnitAction(u, temp.getAction(u));
                }

            }
        }

        return pAction;
    }
    

    public AI getAIUnit(Unit un) {
        for (Unit u : _unitScriptMap.keySet()) {
            if (u.getID() == un.getID()) {
                return _unitScriptMap.get(u);
            }
        }

        return seed;
    }
    
    public UnitScriptData clone(){
        UnitScriptData uR = new UnitScriptData(playerForThisComputation);
        uR.setSeedUnits(seed);
        for(Unit u : this._unitScriptMap.keySet()){
            uR.setUnitScript(u, this._unitScriptMap.get(u));
        }
        return uR;
    }
    
    public void print(){
        System.out.println("Player for computation= "+this.playerForThisComputation);
        System.out.println("Unit   ---->  Script ");
        for (Map.Entry<Unit, AI> entry : _unitScriptMap.entrySet()) {
            Unit key = entry.getKey();
            AI value = entry.getValue();
            System.out.println(key.toString()+"   "+value.toString());            
        }
    }
    
    public HashSet<AI> getUniqueAI(){
        return new HashSet<>(_unitScriptMap.values());
    }

}
