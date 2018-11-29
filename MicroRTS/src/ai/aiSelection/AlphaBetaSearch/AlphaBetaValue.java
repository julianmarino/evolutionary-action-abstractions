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
public class AlphaBetaValue {
    private StateEvalScore _score;
    private AlphaBetaMove  _move;

    public AlphaBetaValue(){
        
    }
    public AlphaBetaValue(StateEvalScore _score, AlphaBetaMove _move) {
        this._score = _score;
        this._move = _move;
    }

    public StateEvalScore getScore() {
        return _score;
    }

    public void setScore(StateEvalScore _score) {
        this._score = _score;
    }

    public AlphaBetaMove getMove() {
        return _move;
    }

    public void setMove(AlphaBetaMove _move) {
        this._move = _move;
    }
    
    
}
