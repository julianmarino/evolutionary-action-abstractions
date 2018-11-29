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
public class TTBestMove {
    private AlphaBetaMove _firstMove, _secondMove;

    public TTBestMove(){
        
    }
    
    public TTBestMove(AlphaBetaMove _firstMove, AlphaBetaMove _secondMove) {
        this._firstMove = _firstMove;
        this._secondMove = _secondMove;
    }

    public AlphaBetaMove getFirstMove() {
        return _firstMove;
    }

    public void setFirstMove(AlphaBetaMove _firstMove) {
        this._firstMove = _firstMove;
    }

    public AlphaBetaMove getSecondMove() {
        return _secondMove;
    }

    public void setSecondMove(AlphaBetaMove _secondMove) {
        this._secondMove = _secondMove;
    }
    
    
    
}
