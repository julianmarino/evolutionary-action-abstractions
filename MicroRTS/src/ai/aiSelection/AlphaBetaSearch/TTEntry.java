/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.aiSelection.AlphaBetaSearch;

import ai.aiSelection.AlphaBetaSearch.Enumerators.TTEntryEnum;
import java.util.ArrayList;

/**
 *
 * @author rubens
 */
public class TTEntry {

    private int _hash2;
    private StateEvalScore _score;
    private int _depth;
    //private TTBestMove[] _bestMoves = new TTBestMove[2]; otimizado
    private ArrayList<TTBestMove> _bestMoves = new ArrayList<>();
    private TTEntryEnum _type;

    public TTEntry() {
        this._hash2 = 0;
        this._depth = 0;
        this._type = TTEntryEnum.NONE;
        this._bestMoves.add(new TTBestMove());
        this._bestMoves.add(new TTBestMove());
    }

    public TTEntry(int _hash2, StateEvalScore _score, int _depth, TTEntryEnum _type, 
                    int firstPlayer, AlphaBetaMove bestFirstMove, AlphaBetaMove bestSecondMove) {
        this._hash2 = _hash2;
        this._score = _score;
        this._depth = _depth;
        this._type = _type;
        this._bestMoves.add(new TTBestMove());
        this._bestMoves.add(new TTBestMove());
        //_bestMoves[firstPlayer] = new TTBestMove(bestFirstMove, bestSecondMove);
        _bestMoves.set(firstPlayer, new TTBestMove(bestFirstMove, bestSecondMove));
    }
    
    public boolean hashMathes(int hash2){
        return hash2 == _hash2;
    }
    
    public boolean isValid(){
        return _type != TTEntryEnum.NONE;
    }

    public int getHash2() {
        return _hash2;
    }

    public void setHash2(int _hash2) {
        this._hash2 = _hash2;
    }

    public StateEvalScore getScore() {
        return _score;
    }

    public void setScore(StateEvalScore _score) {
        this._score = _score;
    }

    public int getDepth() {
        return _depth;
    }

    public void setDepth(int _depth) {
        this._depth = _depth;
    }

    public ArrayList<TTBestMove> getBestMoves() {
        return _bestMoves;
    }

    public void setBestMoves(ArrayList<TTBestMove> _bestMoves) {
        this._bestMoves = _bestMoves;
    }

    public TTEntryEnum getType() {
        return _type;
    }

    public void setType(TTEntryEnum _type) {
        this._type = _type;
    }
    
    public TTBestMove getBestMove(int player){
        return _bestMoves.get(player);
    }
    
    public void setBestMove(int firstPlayer, AlphaBetaMove bestFirstMove, AlphaBetaMove bestSecondMove){
        //_bestMoves[firstPlayer] = new TTBestMove(bestFirstMove, bestSecondMove);
        _bestMoves.add(firstPlayer, new TTBestMove(bestFirstMove, bestSecondMove));
    }
    
    public void print(){
        System.out.println(_hash2+", "+_score.getVal()+", "+_depth+", "+_type.name());
    }
    
    
}
