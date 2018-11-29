/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.aiSelection.AlphaBetaSearch;

import ai.aiSelection.AlphaBetaSearch.Enumerators.MoveOrderMethod;
import ai.aiSelection.AlphaBetaSearch.Enumerators.PlayerToMove;
import ai.aiSelection.AlphaBetaSearch.Enumerators.Players;
import ai.aiSelection.AlphaBetaSearch.Enumerators.SearchMethods;
import ai.core.AI;
import ai.evaluation.EvaluationFunction;
import ai.evaluation.SimpleSqrtEvaluationFunction3;
import java.util.ArrayList;

/**
 *
 * @author rubens
 */
public class AlphaBetaSearchParameters {
                                                // DEFAULT				DESCRIPTION
    private SearchMethods _searchMethod;       // ID-AB                The Method to use for AB Search
    private Players _maxPlayer;                   // Player_One			The player who will make maximizing moves
    private short _maxDepth;                    // Max_Depth            Maximum depth of AB search to allow
    
    private int _timeLimit;                     // 0					Search time limit. 0 means no time limit
    private int _maxChildren;                   // 10                   Max children at each node
    
    private MoveOrderMethod _moveOrdering;                // ScriptFirst          Move ordering method for child generation
    private EvaluationFunction  _evalMethod;    // LTD3				Evaluation function type
    private AI[] _simScripts = new AI[2];       // POLightRush               Policy to use for playouts
    private PlayerToMove _playerToMoveMethod;          // Alternate			The player to move policy
    private AI[] _playerModel = new AI[2];// None                 Player model to use for each player
    
    private int _beamSize;
    private boolean _learning;
    private String _policyFilename;
    
    private String _graphVizFilename;           // ""                   File name to output graph viz file
    
    private ArrayList<AI> _orderedMoveScripts; // 
    
    private ArrayList<ArrayList<String>> _desc = new ArrayList<>();

    public AlphaBetaSearchParameters() {
        this._searchMethod = SearchMethods.IDAlphaBeta;
        this._maxPlayer    = Players.Player_Two;
        this._maxDepth     = 50; // Constants::Max_Search_Depth in SparCraft
        this._timeLimit    = 0;
        this._maxChildren  = 10;
        this._moveOrdering = MoveOrderMethod.ScriptFirst;
        this._evalMethod   = new SimpleSqrtEvaluationFunction3();
        this._playerToMoveMethod = PlayerToMove.Alternate;
        this._beamSize = 20;
        this._policyFilename = "policy_filename";
        this._learning = false;
        this._orderedMoveScripts = new ArrayList<>();
        setPlayerModel(0,null);
        setPlayerModel(1, null);
        setSimScripts(null, null);
    }

    public SearchMethods getSearchMethod() {
        return _searchMethod;
    }

    public void setSearchMethod(SearchMethods _searchMethod) {
        this._searchMethod = _searchMethod;
    }

    public Players getMaxPlayer() {
        return _maxPlayer;
    }

    public void setMaxPlayer(Players _maxPlayer) {
        this._maxPlayer = _maxPlayer;
    }

    public short getMaxDepth() {
        return _maxDepth;
    }

    public void setMaxDepth(short _maxDepth) {
        this._maxDepth = _maxDepth;
    }

    public int getTimeLimit() {
        return _timeLimit;
    }

    public void setTimeLimit(int _timeLimit) {
        this._timeLimit = _timeLimit;
    }

    public int getMaxChildren() {
        return _maxChildren;
    }

    public void setMaxChildren(int _maxChildren) {
        this._maxChildren = _maxChildren;
    }

    public MoveOrderMethod getMoveOrdering() {
        return _moveOrdering;
    }

    public void setMoveOrdering(MoveOrderMethod _moveOrdering) {
        this._moveOrdering = _moveOrdering;
    }

    public EvaluationFunction getEvalMethod() {
        return _evalMethod;
    }

    public void setEvalMethod(EvaluationFunction _evalMethod) {
        this._evalMethod = _evalMethod;
    }

    public AI[] getSimScripts() {
        return _simScripts;
    }

    public void setSimScripts(AI p1, AI p2) {
        this._simScripts = new AI[2];
        _simScripts[0]=p1;
        _simScripts[1]=p2;
    }
    
    public void setSimScripts(int player, AI ai){
        _simScripts[player] = ai;
    }

    public PlayerToMove getPlayerToMoveMethod() {
        return _playerToMoveMethod;
    }

    public void setPlayerToMoveMethod(PlayerToMove _playerToMoveMethod) {
        this._playerToMoveMethod = _playerToMoveMethod;
    }

    public AI[] getPlayerModel() {
        return _playerModel;
    }
    
    public AI getPlayerModel(int player) {
        return _playerModel[player];
    }

    public void setAllPlayerModel(AI[] _playerModel) {
        this._playerModel = _playerModel;
    }
    
    public void setPlayerModel(int player, AI model){
        this._playerModel[player] = model;
    }

    public int getBeamSize() {
        return _beamSize;
    }

    public void setBeamSize(int _beamSize) {
        this._beamSize = _beamSize;
    }

    public boolean isLearning() {
        return _learning;
    }

    public void setLearning(boolean _learning) {
        this._learning = _learning;
    }

    public String getPolicyFilename() {
        return _policyFilename;
    }

    public void setPolicyFilename(String _policyFilename) {
        this._policyFilename = _policyFilename;
    }

    public String getGraphVizFilename() {
        return _graphVizFilename;
    }

    public void setGraphVizFilename(String _graphVizFilename) {
        this._graphVizFilename = _graphVizFilename;
    }

    public ArrayList<AI> getOrderedMoveScripts() {
        return _orderedMoveScripts;
    }

    public void setOrderedMoveScripts(ArrayList<AI> _orderedMoveScripts) {
        this._orderedMoveScripts = _orderedMoveScripts;
    }
    
    public void addOrderedMoveScript(AI script){
        this._orderedMoveScripts.add(script);
    }

    public ArrayList<ArrayList<String>> getDesc() {
        return _desc;
    }

    public void setDesc(ArrayList<ArrayList<String>> _desc) {
        this._desc = _desc;
    }
    
    public ArrayList<ArrayList<String>> getDescription(){
        
        if(_desc.size() == 0){
            _desc.add(new ArrayList<>());
            _desc.add(new ArrayList<>());
            
            _desc.get(0).add("Player Type:");
            _desc.get(0).add("Time Limit:");
            _desc.get(0).add("Max Children:");
            _desc.get(0).add("Move Ordering:");
            _desc.get(0).add("Player To Move:");
            _desc.get(0).add("Opponent Model:");
            
            
            _desc.get(1).add("AlphaBeta");
            _desc.get(1).add(String.valueOf(getTimeLimit())+"ms");
            _desc.get(1).add(String.valueOf(getMaxChildren()));        
            _desc.get(1).add(this.getMoveOrdering().name());
            _desc.get(1).add(getPlayerToMoveMethod().name());
            if(this._playerModel[0] != null){
            _desc.get(1).add(this._playerModel[0].toString());
            }else{
                _desc.get(1).add("-");
            }
            if(this._playerModel[1] != null){
            _desc.get(1).add(this._playerModel[1].toString());
            }else{
                _desc.get(1).add("-");
            }
            
        }
        
        return _desc;
    }
    
    
    
}
