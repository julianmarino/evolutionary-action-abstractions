/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.aiSelection.AlphaBetaSearch;

import java.util.ArrayList;
import rts.PlayerAction;

/**
 * Classe baseada na classe AlphaBetaSearchResults do Sparcraft.
 *
 * @author rubens
 */
public class AlphaBetaSearchResults {

    private boolean solved;         // whether ot not a solution was found
    private boolean timedOut;       // did the search time-out?
    private long nodesExpanded;     // number of nodes expanded in the search
    private double timeElapsed;     // time elapsed in milliseconds
    private double avgBranch;       // avg branching factor
    
    private PlayerAction bestMoves;
    private float abValue;          // change to float
    private long ttcuts;
    private int maxDepthReached; //size_t changed to int
    private int ttMoveOrders;
    private int ttFoundButNoMove;
    private int ttFoundNoCut;
    private int ttFoundCheck;
    private int ttFoundLessDepth;
    private int ttSaveAttempts;
    
    private ArrayList<ArrayList<String>> _desc = new ArrayList<>();

    public AlphaBetaSearchResults() {
        this.solved = false;
        this.timedOut = false;
        this.nodesExpanded = 0;
        this.timeElapsed = 0;
        this.avgBranch = 0;
        this.abValue = 0;
        this.ttcuts = 0;
        this.maxDepthReached = 0;
        this.ttMoveOrders = 0;
        this.ttFoundButNoMove = 0;
        this.ttFoundNoCut = 0;
        this.ttFoundCheck = 0;
        this.ttFoundLessDepth = 0;
        this.ttSaveAttempts = 0;
        
    }
    
    public void print(){
        System.out.println(this.toString());
    }

    @Override
    public String toString() {
        return "AlphaBetaSearchResults{" + "solved=" + solved + ", timedOut=" + timedOut + ", nodesExpanded=" + nodesExpanded + ", timeElapsed=" + timeElapsed + ", avgBranch=" + avgBranch + ", bestMoves=" + bestMoves + ", abValue=" + abValue + ", maxDepthReached=" + maxDepthReached + '}';
    }
    
    

    public boolean isSolved() {
        return solved;
    }

    public void setSolved(boolean solved) {
        this.solved = solved;
    }

    public boolean isTimedOut() {
        return timedOut;
    }

    public void setTimedOut(boolean timedOut) {
        this.timedOut = timedOut;
    }

    public long getNodesExpanded() {
        return nodesExpanded;
    }

    public void setNodesExpanded(long nodesExpanded) {
        this.nodesExpanded = nodesExpanded;
    }

    public double getTimeElapsed() {
        return timeElapsed;
    }

    public void setTimeElapsed(double timeElapsed) {
        this.timeElapsed = timeElapsed;
    }

    public double getAvgBranch() {
        return avgBranch;
    }

    public void setAvgBranch(double avgBranch) {
        this.avgBranch = avgBranch;
    }

    public PlayerAction getBestMoves() {
        return bestMoves;
    }

    public void setBestMoves(PlayerAction bestMoves) {
        this.bestMoves = bestMoves;
    }

    public float getAbValue() {
        return abValue;
    }

    public void setAbValue(float abValue) {
        this.abValue = abValue;
    }

    public long getTtcuts() {
        return ttcuts;
    }

    public void setTtcuts(long ttcuts) {
        this.ttcuts = ttcuts;
    }

    public int getMaxDepthReached() {
        return maxDepthReached;
    }

    public void setMaxDepthReached(int maxDepthReached) {
        this.maxDepthReached = maxDepthReached;
    }

    public int getTtMoveOrders() {
        return ttMoveOrders;
    }

    public void setTtMoveOrders(int ttMoveOrders) {
        this.ttMoveOrders = ttMoveOrders;
    }

    public int getTtFoundButNoMove() {
        return ttFoundButNoMove;
    }

    public void setTtFoundButNoMove(int ttFoundButNoMove) {
        this.ttFoundButNoMove = ttFoundButNoMove;
    }

    public int getTtFoundNoCut() {
        return ttFoundNoCut;
    }

    public void setTtFoundNoCut(int ttFoundNoCut) {
        this.ttFoundNoCut = ttFoundNoCut;
    }

    public int getTtFoundCheck() {
        return ttFoundCheck;
    }

    public void setTtFoundCheck(int ttFoundCheck) {
        this.ttFoundCheck = ttFoundCheck;
    }

    public int getTtFoundLessDepth() {
        return ttFoundLessDepth;
    }

    public void setTtFoundLessDepth(int ttFoundLessDepth) {
        this.ttFoundLessDepth = ttFoundLessDepth;
    }

    public int getTtSaveAttempts() {
        return ttSaveAttempts;
    }

    public void setTtSaveAttempts(int ttSaveAttempts) {
        this.ttSaveAttempts = ttSaveAttempts;
    }

    public ArrayList<ArrayList<String>> getDesc() {
        return _desc;
    }

    public void setDesc(ArrayList<ArrayList<String>> _desc) {
        this._desc = _desc;
    }
    
    
    
    public ArrayList<ArrayList<String>> getDescription(){
        _desc.clear();
        _desc.add(new ArrayList<>());
        _desc.add(new ArrayList<>());
        
        _desc.get(0).add("Nodes Searched: ");
        _desc.get(0).add("AB Value: ");
        _desc.get(0).add("Max Depth: ");
        
        _desc.get(1).add(String.valueOf(nodesExpanded));
        _desc.get(1).add(String.valueOf(abValue));
        _desc.get(1).add(String.valueOf(maxDepthReached));
        
        return _desc;
    }
    
}
