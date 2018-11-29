/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.aiSelection.AlphaBetaSearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

/**
 *
 * @author rubens
 */
public class MoveArray {
    
    

    // the array which contains all the moves
    Action[][] _moves = new Action[100][105];// otimizado
    //HashMap<Long, Action[]> _moves = new HashMap<>();

    // how many moves each unit has
    int[] _numMoves = new int[100];

    // the current move array, used for the 'iterator'
    Action[] _currentMoves = new Action[100];
    int[] _currentMovesIndex = new int[100];

    // the number of units that have moves;
    ArrayList<Integer> _numUnits;
    int _maxUnits;
    boolean _hasMoreMoves;

    public MoveArray() {
        this._maxUnits = 100;
        this._numUnits = new ArrayList<>();
        this._hasMoreMoves = true;
        for (int i = 0; i < 100; i++) {
            _numMoves[i] = 0;
            _currentMovesIndex[i] = 0;
        }
    }

    public void clear() {
        // only clear things if they need to be cleared
        if (_numUnits.size() == 0) {
            return;
        }
        _numUnits = new ArrayList<>();
        //fill
        for (int i = 0; i < 100; i++) {
            _numMoves[i] = 0;
        }
        resetMoveIterator();
    }

    public void resetMoveIterator() {
        _hasMoreMoves = true;
        //fill
        for (int i = 0; i < 100; i++) {
            _currentMovesIndex[i] = 0;
        }
        for (int u : _numUnits) {
            _currentMoves[u] = _moves[u][_currentMovesIndex[u]];
        }
    }

    // shuffle the MOVE unit actions to prevent bias in experiments
    // this function assumes that all MOVE actions are contiguous in the moves array
    // this should be the case unless you change the move generation ordering
    public void shuffleMoveActions() {
        // for each unit
        for (int u : _numUnits) {
            int moveEnd = -1;
            int moveBegin = -1;
            // reverse through the list of actions for this unit
            for (int a = (_numMoves[u] - 1); a >= 0; a--) {
                int moveType = getMove(u, a).getType();

                // mark the end of the move actions
                if (moveEnd == -1 && (moveType == 1)) //TYPE_MOVE = 1;
                {
                    moveEnd = a;
                } // mark the beginning of the MOVE unit actions
                else if ((moveEnd != -1) && (moveBegin == -1) && (moveType != 1)) {//TYPE_MOVE = 1;
                    moveBegin = a;
                } else if (moveBegin != -1) {
                    break;
                }
            }

            // if we found the end but didn't find the beginning
            if (moveEnd != -1 && moveBegin == -1) {
                // then the move actions begin at the beginning of the array
                moveBegin = 0;
            }
            // shuffle the movement actions for this unit
            if (moveEnd != -1 && moveBegin != -1 && moveEnd != moveBegin) {
                random_shuffle(u, moveBegin, moveEnd);
                resetMoveIterator();
            }

        }
    }

    public void random_shuffle(int u, int moveBegin, int moveEnd) {
        Random rgen = new Random();  // Random number generator			

        for (int i = moveBegin; i < moveEnd; i++) {
            int randomPosition = rgen.nextInt(moveEnd - moveBegin);
            Action temp = _moves[u][i];
            _moves[u][i] = _moves[u][randomPosition];
            _moves[u][randomPosition] = temp;
        }
    }

    // returns a given move from a unit
    public Action getMove(int unit, int move) {
        return _moves[unit][move];
    }

    public Action[] getMoves(int unit) {
        ArrayList<Action> mTemp = new ArrayList<>();
        Action moveTemp;

        for (int a = (_numMoves[unit] - 1); a >= 0; a--) {
            moveTemp = _moves[unit][a];
            mTemp.add(moveTemp);
        }

        return (Action[]) mTemp.toArray();
    }

    public void printCurrentMoveIndex() {
        for (int u : _numUnits) {
            System.out.print(_currentMovesIndex[u] + " ");
        }
        System.out.println(" ");
    }

    public void incrementMove(int unit) {
        // increment the index for this unit
        _currentMovesIndex[unit] = (_currentMovesIndex[unit] + 1) % _numMoves[unit];

        // if the value rolled over, we need to do the carry calculation
        if (_currentMovesIndex[unit] == 0) {
            // the next unit index
            // if we have space left to increment, do it
            try {
                int nextUnit = _numUnits.get(_numUnits.indexOf(unit)+1);
                incrementMove(nextUnit);
            } catch (Exception e) { // otherwise we have no more moves
                // stop
                _hasMoreMoves = false;
            }
        }
        _currentMoves[unit] = _moves[unit][_currentMovesIndex[unit]];
    }
    
    public boolean hasMoreMoves(){
        return _hasMoreMoves;
    }
    
    public ArrayList<Action> getNextMoveVec(){
        ArrayList<Action> tempActions = new ArrayList<>();
        
        for(int m : _numUnits){
            Action act = _currentMoves[m];
            if(act != null){
                tempActions.add(act);
            }
        }
        if(_numUnits.size() > 0){
            incrementMove(_numUnits.get(0));
        }
        return tempActions;
    }
    
    public int maxUnits(){ //verificar!
        return _maxUnits;
    }
    
    // adds a Move to the unit specified
    public void add(int unit, Action move){
        _moves[unit][_numMoves[unit]] = move;
        _numMoves[unit]++;
        _currentMovesIndex[_numUnits.size()-1]=0;
        _currentMoves[_numUnits.size()-1]= _moves[unit][0];
    }
    
    public boolean validateMoves(){
        
        for(int u : _numUnits){
            for(int m = 0; m< numMoves(u) ; m++){
                Action move = getMove(u, m);
                if(move.getUnit() > 200){
                    System.out.println("Unit Move Incorrect! Something will be wrong");
                    return false;
                }
            }
        }
        
        return true;
    }
    
    public int getUnitID(int unit){
        return getMove(unit,0).getUnit();
    }
    
    public int getPlayerID(int unit){
        return getMove(unit,0).getPlayer();
    }
    
    public void addUnit(int unit){
        _numUnits.add(unit);
    }
    
    public int numUnits(){
        return _numUnits.size();
    }
    
    public int numUnitsInTuple(){
        return numUnits();
    }
    
    public int numMoves(int unit){
        return _numMoves[unit];
    }
    
    public void replaceMovimentUnit(int unit, Action move){
        _numMoves[unit] = 0;
        _moves[unit][_numMoves[unit]]=move;
        _numMoves[unit]=1;
        
        _currentMoves[unit] = _moves[unit][0];
    }
    
    public void print(){
        for(int u = 0; u < _numMoves.length; u++){
            for(int a = (numMoves(u)-1); a >=0; a--){
                try {
                    System.out.println(_moves[u][a].debugString());
                } catch (Exception e) {
                    System.out.println(e.toString());
                }
                
            }
        }
    }

}
