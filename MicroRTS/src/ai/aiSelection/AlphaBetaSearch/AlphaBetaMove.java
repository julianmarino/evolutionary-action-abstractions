/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.aiSelection.AlphaBetaSearch;

import rts.PlayerAction;

/**
 *
 * @author rubens
 */
public class AlphaBetaMove {
    private PlayerAction _move;
    private boolean _isValid;

    public AlphaBetaMove(){
        
    }
    public AlphaBetaMove(PlayerAction _move, boolean _isValid) {
        this._move = _move;
        this._isValid = _isValid;
    }

    public PlayerAction getMove() {
        return _move;
    }

    public void setMove(PlayerAction _move) {
        this._move = _move;
    }

    public boolean isIsValid() {
        return _isValid;
    }

    public void setIsValid(boolean _isValid) {
        this._isValid = _isValid;
    }
    
    
    
    
}
