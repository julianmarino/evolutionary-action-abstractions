/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.aiSelection.AlphaBetaSearch;

import java.util.Objects;
import javax.swing.text.Position;
import rts.UnitAction;

/**
 *
 * @author rubens
 */
public class Action {

    private int _unit;
    private int _player;
    private int _moveIndex;
    private UnitAction unitAction;
    private Position _p;

    public Action() {
        this._unit = 255;
        this._player = 255;
        this._moveIndex = 255;
        unitAction = new UnitAction(0);
    }

    public Action(int unitIndex, int _player, int _moveIndex, UnitAction unitAction, Position dest) {
        this._unit = unitIndex;
        this._player = _player;
        this._moveIndex = _moveIndex;
        this.unitAction = unitAction;
        this._p = dest;
    }

    public Action(int unitIndex, int _player, int _moveIndex, UnitAction unitAction) {
        this._unit = unitIndex;
        this._player = _player;
        this._moveIndex = _moveIndex;
        this.unitAction = unitAction;
    }

    public int getUnit() {
        return _unit;
    }

    public void setUnit(int _unit) {
        this._unit = _unit;
    }

    public int getPlayer() {
        return _player;
    }

    public void setPlayer(int _player) {
        this._player = _player;
    }

    public int getMoveIndex() {
        return _moveIndex;
    }

    public void setMoveIndex(int _moveIndex) {
        this._moveIndex = _moveIndex;
    }

    public UnitAction getUnitAction() {
        return unitAction;
    }

    public void setUnitAction(UnitAction unitAction) {
        this.unitAction = unitAction;
    }

    public Position getP() {
        return _p;
    }

    public void setP(Position _p) {
        this._p = _p;
    }

    public int getType() {
        return unitAction.getType();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 19 * hash + this._unit;
        hash = 19 * hash + this._player;
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
        final Action other = (Action) obj;
        if (this._unit != other._unit) {
            return false;
        }
        if (this._player != other._player) {
            return false;
        }
        if (this._moveIndex != other._moveIndex) {
            return false;
        }
        if (!Objects.equals(this._p, other._p)) {
            return false;
        }
        return true;
    }

    public String moveString() {
        if (unitAction.getType() == 5) { // TYPE_ATTACK_LOCATION
            return "ATTACK";
        } else if (unitAction.getType() == 1) {// TYPE_MOVE
            return "MOVE";
        } else if (unitAction.getType() == 10) { // don't have
            return "RELOAD";
        } else if (unitAction.getType() == 11) {// don't have
            return "PASS";
        } else if (unitAction.getType() == 12) { // don't have
            return "HEAL";
        } else if (unitAction.getType() == 2) { //TYPE_HARVEST
            return "HARVEST";
        } else if (unitAction.getType() == 3) { //TYPE_RETURN
            return "RETURN";
        } else if (unitAction.getType() == 4) { //TYPE_PRODUCE
            return "PRODUCE";
        } 
        return "NONE"; // TYPE_NONE = 0
    }

    public String debugString() {
        String str = "";

        if(this._p != null){
            str = moveString()+": ("+this._unit+", "+this._player+", "+ this.unitAction.toString()+", "+this._p.toString();
        }else{
            str = moveString()+": ("+this._unit+", "+this._player+", "+ this.unitAction.toString();
        }
        
        
        return str;
    }

}
