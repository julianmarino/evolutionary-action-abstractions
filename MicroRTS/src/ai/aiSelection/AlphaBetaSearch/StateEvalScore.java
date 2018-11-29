/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.aiSelection.AlphaBetaSearch;

/**
 *
 * @author rubens
 */
public class StateEvalScore {
    private float _val;
    private int _numMoves;

    public StateEvalScore(){
        this._val = 0;
        this._numMoves = 0;
    }
    public StateEvalScore(float _val, int _numMoves) {
        this._val = _val;
        this._numMoves = _numMoves;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Float.floatToIntBits(this._val);
        hash = 79 * hash + this._numMoves;
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
        final StateEvalScore other = (StateEvalScore) obj;
        if (Float.floatToIntBits(this._val) != Float.floatToIntBits(other._val)) {
            return false;
        }
        if (this._numMoves != other._numMoves) {
            return false;
        }
        return true;
    }
    
    public boolean menor(StateEvalScore rhs){
        if (_val < rhs._val)
            {
                return true;
            }
            else if (_val == rhs._val)
            {
                return _numMoves > rhs._numMoves;
            }
            else
            {
                return false;
            }
    }
    public boolean maior(StateEvalScore rhs){
        if (_val > rhs._val)
            {
                return true;
            }
            else if (_val == rhs._val)
            {
                return _numMoves < rhs._numMoves;
            }
            else
            {
                return false;
            }
    }
    
    public boolean menorIgual(StateEvalScore rhs){
        if (_val > rhs._val)
            {
                return true;
            }
            else if (_val == rhs._val)
            {
                return _numMoves >= rhs._numMoves;
            }
            else
            {
                return false;
            }
    
    }
    
    public boolean maiorIgual(StateEvalScore rhs){
        if (_val > rhs._val)
            {
                return true;
            }
            else if (_val == rhs._val)
            {
                return _numMoves <= rhs._numMoves;
            }
            else
            {
                return false;
            }
    }

    public float getVal() {
        return _val;
    }

    public void setVal(float _val) {
        this._val = _val;
    }

    public int getNumMoves() {
        return _numMoves;
    }

    public void setNumMoves(int _numMoves) {
        this._numMoves = _numMoves;
    }
    
    
    
}
