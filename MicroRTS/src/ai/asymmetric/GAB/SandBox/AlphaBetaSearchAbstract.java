/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.asymmetric.GAB.SandBox;

import PVAI.EconomyRush;
import ai.abstraction.combat.KitterDPS;
import ai.abstraction.combat.NOKDPS;
import ai.aiSelection.AlphaBetaSearch.*;
import ai.abstraction.partialobservability.POLightRush;
import ai.aiSelection.AlphaBetaSearch.Enumerators.MoveOrderMethod;
import ai.aiSelection.AlphaBetaSearch.Enumerators.PlayerToMove;
import ai.aiSelection.AlphaBetaSearch.Enumerators.Players;
import ai.aiSelection.AlphaBetaSearch.Enumerators.SearchMethods;
import ai.aiSelection.AlphaBetaSearch.Enumerators.TTEntryEnum;
import ai.aiSelection.AlphaBetaSearch.Hash.Hash;
import ai.asymmetric.common.UnitScriptData;
import ai.configurablescript.BasicExpandedConfigurableScript;
import ai.configurablescript.ScriptsCreator;
import ai.core.AI;
import ai.core.AIWithComputationBudget;
import ai.core.InterruptibleAI;
import ai.core.ParameterSpecification;
import ai.evaluation.EvaluationFunction;
import ai.evaluation.SimpleSqrtEvaluationFunction3;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import rts.GameState;
import rts.PlayerAction;
import rts.PlayerActionGenerator_Asymmetric;
import rts.UnitAction;
import rts.units.Unit;
import rts.units.UnitTypeTable;
import util.Pair;

/**
 *
 * @author rubens
 */
public class AlphaBetaSearchAbstract extends AIWithComputationBudget implements InterruptibleAI {

    private AlphaBetaSearchParameters _params;
    private AlphaBetaSearchResults _results;
    private Instant _searchTimer;
    private int _currentRootDepth;
    private ArrayList<MoveArrayAbstract> _allMoves;
    private ArrayList[][] _orderedMoves = new ArrayList[50][10];
    private ArrayList[] _allScripts = new ArrayList[2];
    private AI[] _playerModels = new AI[2];
    private TranspositionTable _TT;
    private int playerToGame;
    EvaluationFunction evaluation = null;

    GameState gs_to_start_from = null;
    int playerForThisComputation;

    UnitScriptData currentScriptData = null;
    HashSet<Unit> _unitsAbsAB = null;

    //class to control a relation between unit ID and Unit Index (limited to 100).
    LookUpUnits lKp = new LookUpUnits();
    public boolean scriptedMove;

    public AlphaBetaSearchAbstract(UnitTypeTable utt) {
        this(100, 100, new AlphaBetaSearchParameters(), new TranspositionTable(), utt);
    }

    public AlphaBetaSearchAbstract(int time, int max_playouts, AlphaBetaSearchParameters _params, TranspositionTable _TT, UnitTypeTable utt) {
        super(time, max_playouts);
        _params.setTimeLimit(time);
        _params.setPlayerModel(Players.Player_One.codigo(), new POLightRush(utt));
        _params.setPlayerModel(Players.Player_Two.codigo(), new POLightRush(utt));
        _params.setSimScripts(new POLightRush(utt), new POLightRush(utt));

        ScriptsCreator sc = new ScriptsCreator(utt, 300);
        ArrayList<BasicExpandedConfigurableScript> scriptsCompleteSet = sc.getScriptsMixReducedSet();

        _params.setOrderedMoveScripts(new ArrayList<AI>() {
            {
                //add(0, new EconomyRush(utt));
                // add(1, new POWorkerRush(utt));
                //add(1, scriptsCompleteSet.get(0));
                add(0, scriptsCompleteSet.get(1));
                //add(1, scriptsCompleteSet.get(2));
                //add(3, scriptsCompleteSet.get(3));
                add(1, new NOKDPS(utt));
                add(2, new KitterDPS(utt));

            }
        });

        StartAlphaBetaSearch(_params, _TT);
        evaluation = new SimpleSqrtEvaluationFunction3();
    }

    public AlphaBetaSearchAbstract(int time, int max_playouts, AlphaBetaSearchParameters _params, TranspositionTable _TT) {
        super(time, max_playouts);
        this._params = _params;
        this._TT = _TT;
        this._currentRootDepth = 0;
        this._allMoves = new ArrayList<>();
        for (int i = 0; i <= 50; i++) {
            this._allMoves.add(new MoveArrayAbstract());
        }
        this._results = new AlphaBetaSearchResults();
        for (int p = 0; p < 2; ++p) {
            // set ordered move script player objects
            for (int s = 0; s < _params.getOrderedMoveScripts().size(); s++) {
                if (_allScripts[p] == null) {
                    _allScripts[p] = new ArrayList();
                }
                _allScripts[p].add(_params.getOrderedMoveScripts().get(s));
            }
            // set player model objects
            if (_params.getPlayerModel(p) != null) {
                _playerModels[p] = _params.getPlayerModel(p);
            }
        }
    }

    public void StartAlphaBetaSearch(AlphaBetaSearchParameters _params, TranspositionTable _TT) {
        this._results = new AlphaBetaSearchResults();
        this._params = _params;
        this._TT = _TT;
        this._currentRootDepth = 0;
        this._allMoves = new ArrayList<>();
        for (int i = 0; i <= 50; i++) {
            this._allMoves.add(new MoveArrayAbstract());
        }
        for (int p = 0; p < 2; ++p) {
            // set ordered move script player objects
            for (int s = 0; s < _params.getOrderedMoveScripts().size(); s++) {
                if (_allScripts[p] == null) {
                    _allScripts[p] = new ArrayList();
                }
                _allScripts[p].add(_params.getOrderedMoveScripts().get(s));
            }
            // set player model objects
            if (_params.getPlayerModel(p) != null) {
                _playerModels[p] = _params.getPlayerModel(p);
            }
        }
    }

    public void doSearch(GameState initialState, int player) throws Exception {
        this._searchTimer = Instant.now();
        this.playerToGame = player;
        _params.setMaxPlayer(Players.porCodigo(player));
        _params.setPlayerModel(player, null);

        StateEvalScore alpha = new StateEvalScore(-10000000, 1000000);
        StateEvalScore beta = new StateEvalScore(10000000, 1000000);
        AlphaBetaValue val;
        if (_params.getSearchMethod() == SearchMethods.AlphaBeta) {
            val = alphaBeta(initialState, _params.getMaxDepth(), Players.Player_None, null, alpha, beta);
        } else if (_params.getSearchMethod() == SearchMethods.IDAlphaBeta) {
            val = IDAlphaBeta(initialState, _params.getMaxDepth());
        }

        _results.setTimeElapsed(Duration.between(_searchTimer, Instant.now()).toMillis());
    }

    private AlphaBetaValue alphaBeta(GameState state, int depth, Players lastPlayerToMove, ArrayList<Action> prevSimMove, StateEvalScore alpha, StateEvalScore beta) throws Exception {
        // update statistics
        _results.setNodesExpanded(_results.getNodesExpanded() + 1);
        if (searchTimeOut()) {
            //System.out.println("Estou dando searchTimeOut");
            throw new Exception("Timeout during the search! " + depth);
        }
        if (terminalState(state, depth)) {
            //System.out.println("Estado é terminal!");
            // return the value, but the move will not be valid since none was performed
            StateEvalScore evalScore = eval(_params.getMaxPlayer(), _params.getEvalMethod(),
                    _params.getSimScripts()[Players.Player_One.codigo()],
                    _params.getSimScripts()[Players.Player_Two.codigo()], state);
            return new AlphaBetaValue(new StateEvalScore(evalScore.getVal(), evalScore.getNumMoves()), new AlphaBetaMove());
        }

        // figure out which player is to move
        Players playerToMove = getPlayerToMove(state, depth, lastPlayerToMove, (prevSimMove == null));

        // is the player to move the max player?
        boolean maxPlayer = (playerToMove.equals(_params.getMaxPlayer()));

        // Transposition Table Logic
        TTLookupValue TTval = new TTLookupValue();

        if (isTranspositionLookupState(state, prevSimMove)) {
            TTval = TTlookup(state, alpha, beta, depth);

            // if this is a TT cut, return the proper value
            if (TTval.isCut()) {
                return new AlphaBetaValue(TTval.getEntry().getScore(), getAlphaBetaMove(TTval, playerToMove));
            }
        }

        boolean bestMoveSet = false;

        // move generation
        MoveArrayAbstract moves = _allMoves.get(depth);
        if (state.canExecuteAnyAction(playerToMove.codigo())) {
            moves = generateMoves(moves, playerToMove, state);
        }
        moves.shuffleMoveActions();
        stopSearch();
        generateOrderedMoves(state, moves, TTval, playerToMove, depth);

        // while we have more simultaneous moves
        AlphaBetaMove bestMove = new AlphaBetaMove();
        AlphaBetaMove bestSimResponse = new AlphaBetaMove();

        int moveNumber = 0;
        ArrayList<Action> moveVec = new ArrayList<>();

        // for each child
        while (getNextMoveVec(playerToMove, moves, moveNumber, TTval, depth, moveVec, state)) {
            //printMoveVec(moveVec);
            stopSearch();
            // the value of the recursive AB we will call
            AlphaBetaValue val = new AlphaBetaValue();

            // generate the child state
            GameState child;
            try {
                child = state.clone();
            } catch (Exception e) {
                throw new Exception("clone line 233");
            } catch (Error e2) {                
                throw new Exception("clone line 236");
            }

            boolean firstMove = true;
            // if this is the first player in a simultaneous move state
            if (bothCanMove() && (prevSimMove == null) && (depth != 1)) {
                firstMove = true;
                // don't generate a child yet, just pass on the move we are investigating
                stopSearch();
                val = alphaBeta(state, (depth - 1), playerToMove, moveVec, alpha, beta);
            } else {
                firstMove = false;
                // if this is the 2nd move of a simultaneous move state
                if (prevSimMove != null) {
                    // do the previous move selected by the first player to move during this state
                    applyActionState(child, prevSimMove, moves);
                }
                // do the moves of the current player
                applyActionState(child, moveVec, moves);
                //child.forceExecuteAllActions(); //check if this is good for our problem
                //apply a simulation node.
                stopSearch();
                while (child.winner() == -1
                        && !child.gameover()
                        && !child.canExecuteAnyAction(0)
                        && !child.canExecuteAnyAction(1)) {
                    child.cycle();
                    stopSearch();
                }
                stopSearch();
                // get the alpha beta value
                val = alphaBeta(child, (depth - 1), playerToMove, null, alpha, beta);
            }

            // set alpha or beta based on maxplayer
            if (maxPlayer && (val.getScore().maior(alpha))) {
                alpha = val.getScore();
                bestMove = new AlphaBetaMove(makePlayerAction(state, moveVec, moves), true);
                bestMoveSet = true;
                if (bothCanMove() && (prevSimMove == null)) {
                    bestSimResponse = val.getMove();
                }
                // if this is depth 1 of the first try at depth 1, store the best in results
            } else if (!maxPlayer && (val.getScore().menor(beta))) {
                beta = val.getScore();
                bestMove = new AlphaBetaMove(makePlayerAction(state, moveVec, moves), true);
                bestMoveSet = true;
                if (bothCanMove() && prevSimMove == null) {
                    bestSimResponse = val.getMove();
                }
            }
            if (alpha.getVal() == -10000000 && beta.getVal() == 10000000) {
                System.out.println("ALPHA BETA ERROR, NO VALUE SET");
            }
            // alpha-beta cut
            if (alpha.maiorIgual(beta)) {
                break;
            }

            moveNumber++;
        }
        if (isTranspositionLookupState(state, prevSimMove)) {

            TTSave(state, (maxPlayer ? alpha : beta), alpha, beta, depth, playerToMove, bestMove, bestSimResponse);
        }

        //
        return maxPlayer ? new AlphaBetaValue(alpha, bestMove) : new AlphaBetaValue(beta, bestMove);
    }

    private AlphaBetaValue IDAlphaBeta(GameState initialState, short maxDepth) throws Exception {
        AlphaBetaValue val = new AlphaBetaValue();
        scriptedMove = false;
        _results.setNodesExpanded(0);
        _results.setMaxDepthReached(0);
        for (int d = 1; d < maxDepth; d++) {
            StateEvalScore alpha = new StateEvalScore(-10000000, 999999);
            StateEvalScore beta = new StateEvalScore(10000000, 999999);
            _results.setMaxDepthReached(d);
            _currentRootDepth = d;
            // perform ID-AB until time-out
            try {
                lKp.clean();
                lKp.refreshLookup(initialState);
                val = alphaBeta(initialState, d, Players.Player_None, null, alpha, beta);
                _results.setBestMoves(val.getMove().getMove());
                _results.setAbValue(val.getScore().getVal());
            } // if we do time-out
            catch (Exception e) {
                // if we didn't finish the first depth, set the move to the best script move
                if (d == 1) {
                    //System.out.println("Selecting best scripted move for AB " + e.toString());
                    scriptedMove = true;
                    _results.setBestMoves(_params.getSimScripts()[playerToGame].getAction(playerToGame, initialState));

                }
                //System.out.println("Erro during process " + e.toString() + " " + e.getStackTrace().toString());
                //System.err.println(e.getMessage());
                break;
            }

        }
        //_results.print(); // remover
        return val;
    }

    public boolean isTranspositionLookupState(GameState state, ArrayList<Action> firstSimMove) {
        return !bothCanMove() || (bothCanMove() && (firstSimMove == null));
    }

    public Players getPlayerToMove(GameState state, int depth, Players lastPlayerToMove, boolean isFirstSimMove) {
        Random rand = new Random();
        Players whoCanMove = whoCanMove();
        // if both players can move
        if (whoCanMove == Players.Player_Both) {
            // no matter what happens, the 2nd player to move is always the enemy of the first
            if (!isFirstSimMove) {
                return Players.porCodigo(getEnemy(lastPlayerToMove));
            }
            // pick the first move based on our policy
            PlayerToMove policy = _params.getPlayerToMoveMethod();
            Players maxPlayer = _params.getMaxPlayer();
            if (policy == PlayerToMove.Alternate) {
                return isRoot(depth) ? maxPlayer : Players.porCodigo(getEnemy(lastPlayerToMove));
            } else if (policy == PlayerToMove.Not_Alternate) {
                return isRoot(depth) ? maxPlayer : lastPlayerToMove;
            } else if (policy == PlayerToMove.Random) {
                return isRoot(depth) ? maxPlayer : Players.porCodigo(rand.nextInt(2));
            }

            // we should never get to this state
            System.out.println("AlphaBeta Error: Nobody can move for some reason");
            return Players.Player_None;
        } else {
            return whoCanMove;
        }
    }

    public boolean isRoot(int depht) {
        return depht == _currentRootDepth;
    }

    public int getEnemy(Players player) {
        return (player.codigo() + 1) % 2;
    }

    private boolean searchTimeOut() {
        if (Duration.between(_searchTimer, Instant.now()).toMillis() >= (_params.getTimeLimit() - 2)) {
            return true;
        }
        return ((_results.getNodesExpanded() % 100 == 0)
                && (Duration.between(_searchTimer, Instant.now()).toMillis() >= _params.getTimeLimit()));
    }

    private boolean terminalState(GameState state, int depth) {
        return (depth <= 0 || state.gameover());
    }

    private StateEvalScore eval(Players maxPlayer, EvaluationFunction evalMethod, AI simScript, AI simScript0, GameState state) {
        StateEvalScore score = new StateEvalScore();

        score.setVal(evalMethod.evaluate(maxPlayer.codigo(), (1 - maxPlayer.codigo()), state));
        score.setNumMoves(0);
        return score;
    }

    //function in GameState Sparcraft
    //----------------------------------------------------------------------------------------------------
    public Players whoCanMove() {
        boolean p1 = this.gs_to_start_from.canExecuteAnyAction(0); // player 1
        boolean p2 = this.gs_to_start_from.canExecuteAnyAction(1); // player 2

        if (p1 && p2) {
            return Players.Player_Both;
        } else if (p1) {
            return Players.Player_One;
        } else if (p2) {
            return Players.Player_Two;
        }
        return Players.Player_None;
    }

    public boolean bothCanMove() {
        boolean p1 = this.gs_to_start_from.canExecuteAnyAction(0); // player 1
        boolean p2 = this.gs_to_start_from.canExecuteAnyAction(1); // player 2

        return p1 == p2;
    }

    public int calculateHash(int hashNum, GameState currentGS) {
        Hash hashC = new Hash();
        lKp.refreshLookup(currentGS);
        hashC.initHash();
        Long hash = 0L;
        for (Unit u : currentGS.getUnits()) {
            if (!u.getType().isResource) {
                long tHash = hashC.magicHash(calculateHashUnit(hashNum, currentGS.getTime(), u), u.getPlayer(), lKp.getUnitIndex(u.getID()));
                hash += tHash;
            }
        }
        int ret = (int) (hash % 100000);
        return ((int) (hash % 100000));
    }

    // calculates the hash of this unit based on a given game time
    public int calculateHashUnit(int hashNum, int gameTime, Unit unit) {
        int x = unit.getX();
        int y = unit.getY();
        return Hash.VALUES[hashNum].positionHash(unit.getPlayer(), x, y)
                ^ Hash.VALUES[hashNum].getAttackHash(unit.getPlayer(), unit.getAttackTime() + gameTime)
                ^ Hash.VALUES[hashNum].getMoveHash(unit.getPlayer(), unit.getMoveTime() + gameTime)
                ^ Hash.VALUES[hashNum].getCurrentHPHash(unit.getPlayer(), unit.getHitPoints())
                ^ Hash.VALUES[hashNum].getUnitTypeHash(unit.getPlayer(), unit.getType().ID);
    }

    //----------------------------------------------------------------------------------------------------
    // Transposition Table look up + alpha/beta update
    private TTLookupValue TTlookup(GameState state, StateEvalScore alpha, StateEvalScore beta, int depth) {
        TTEntry entry = _TT.lookupScan(calculateHash(0, state), calculateHash(1, state));
        if ((entry != null) && (entry.getDepth() == depth)) {
            // get the value and type of the entry
            StateEvalScore TTvalue = entry.getScore();

            // set alpha and beta depending on the type of entry in the TT
            if (entry.getType() == TTEntryEnum.LOWER) {
                if (TTvalue.maior(alpha)) {
                    alpha = TTvalue;
                }
            } else if (entry.getType() == TTEntryEnum.UPPER) {
                if (TTvalue.menor(beta)) {
                    beta = TTvalue;
                }
            } else {
                System.out.println("ai.aiSelection.AlphaBetaSearch.AlphaBetaSearch.TTlookup()   LOL");
                alpha = TTvalue;
                beta = TTvalue;
            }

            if (alpha.maiorIgual(beta)) {
                // this will be a cut
                long ttCuts = _results.getTtcuts();
                _results.setTtcuts(ttCuts++);
                return new TTLookupValue(true, true, entry);
            } else {
                // found but no cut
                int ttFoundNoCut = _results.getTtFoundNoCut();
                _results.setTtFoundNoCut(ttFoundNoCut++);
                return new TTLookupValue(true, false, entry);
            }
        } else if (entry != null) {
            int ttFoundLessDepth = _results.getTtFoundLessDepth();
            _results.setTtFoundLessDepth(ttFoundLessDepth++);
            return new TTLookupValue(true, false, entry);
        }

        return new TTLookupValue(false, false, entry);
    }

    private AlphaBetaMove getAlphaBetaMove(TTLookupValue TTval, Players playerToMove) {
        int enemy = getEnemy(playerToMove);

        // if we have a valid first move for this player, use it
        if (TTval.getEntry().getBestMove(playerToMove.codigo()).getFirstMove().isIsValid()) {
            return TTval.getEntry().getBestMove(playerToMove.codigo()).getFirstMove();
        } // otherwise return the response to an opponent move, if it doesn't exist it will just be invalid
        else {
            return TTval.getEntry().getBestMove(playerToMove.codigo()).getSecondMove();
        }
    }

    private MoveArrayAbstract generateMoves(MoveArrayAbstract moves, Players playerToMove, GameState state) throws Exception {

        if (moves == null) {
            moves = new MoveArrayAbstract();
        } else {
            moves.clear();
        }

        lKp.refreshLookup(state);
        stopSearch();
        PlayerActionGenerator_Asymmetric AllMoves = null;
        try {
            AllMoves = new PlayerActionGenerator_Asymmetric(state, playerToMove.codigo(), this.currentScriptData, _unitsAbsAB);
        } catch (Exception ex) {
            Logger.getLogger(AlphaBetaSearchAbstract.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("Problem line 523 AlphaBetaSearchAbstract!");
        }
        List<Pair<Unit, List<UnitAction>>> choices = AllMoves.getChoices();
        for (Pair<Unit, List<UnitAction>> choice : choices) {
            Integer idIndex = lKp.getUnitIndex(choice.m_a.getID());
            moves.addUnit(idIndex);
            for (UnitAction uAc : choice.m_b) {
                Action act = new Action(idIndex, playerToMove.codigo(), uAc.getType(), uAc);
                moves.add(idIndex, act);
            }
        }

        return moves;
    }

    private void generateOrderedMoves(GameState state, MoveArrayAbstract moves, TTLookupValue TTval, Players playerToMove, int depth) throws Exception {
        // get the array where we will store the moves and clear it
        ArrayList[] orderedMoves = _orderedMoves[depth];
        cleanMoves(orderedMoves);
        //orderedMoves = new ArrayList[10]; // clear

        // if we are using opponent modeling, get the move and then return, we don't want to put any more moves in
        if (_params.getPlayerModel(playerToMove.codigo()) != null) {
            // put the vector into the ordered moves array
            orderedMoves[0] = new ArrayList();
            // generate the moves into that vector
            PlayerAction pTemp = _params.getPlayerModel(playerToMove.codigo()).getAction(playerToMove.codigo(), state);
            orderedMoves[0] = getActions(pTemp, moves, playerToMove, state);
            return;
        }

        if (depth == 2) {
            int a = 6;
        }

        // if we are using script move ordering, insert the script moves we want
        if (_params.getMoveOrdering() == MoveOrderMethod.ScriptFirst) {
            for (int s = 0; s < _params.getOrderedMoveScripts().size(); s++) {
                AI aiT = (AI) _allScripts[playerToMove.codigo()].get(s);
                //int indexOrd = orderedMoves.length;
                int indexOrd = countElemArray(orderedMoves);
                orderedMoves[indexOrd] = new ArrayList();
                PlayerAction pTemp = aiT.getAction(playerToMove.codigo(), state);
                orderedMoves[indexOrd] = getActions(pTemp, moves, playerToMove, state);
                stopSearch();
            }

            if (orderedMoves.length < 2) {
                int a = 6;
            }
        }

    }

    private boolean getNextMoveVec(Players playerToMove, MoveArrayAbstract moves, int moveNumber, TTLookupValue TTval, int depth, ArrayList<Action> moveVec, GameState stateTemp) throws Exception {

        if ((moveNumber >= _params.getMaxChildren())) {
            return false;
        }
        // if this move is beyond the first, check to see if we are only using a single move
        if (moveNumber == 1) {
            // if we are player modeling, we should have only generated the first move
            if (_params.getPlayerModel(playerToMove.codigo()) != null) {
                // so return false
                return false;
            }
            // if there is a transposition table entry for this state
            if (TTval.isFound()) {
                // if there was a valid move found with higher depth, just do that one
                AlphaBetaMove abMove = getAlphaBetaMove(TTval, playerToMove);
                if (TTval.getEntry().getDepth() >= depth && abMove.isIsValid()) {
                    // so return false
                    return false;
                }
            }
        }
        ArrayList[] orderedMoves = _orderedMoves[depth];
        moveVec.clear();
        // if this move should be from the ordered list, return it from the list
        //if (moveNumber < orderedMoves.length) {
        if (moveNumber < getRealSize(orderedMoves)) {
            moveVec.addAll(orderedMoves[moveNumber]);
            return true;
        } // otherwise return the next move vector starting from the beginning
        else {
            if (moves.hasMoreMoves()) {

                for (Action a : moves.getNextValidMoveVec(stateTemp, playerToMove.codigo(), lKp, _searchTimer)) {
                    moveVec.add(a);
                }

                return true;
            } else {
                return false;
            }
        }
    }

    /**
     * Fuction used to count the real elements in the orderedMoves
     *
     * @return
     */
    private int getRealSize(ArrayList[] orderedMoves) {
        int qtdMoves = 0;

        for (ArrayList orderedMove : orderedMoves) {
            if (orderedMove != null) {
                qtdMoves++;
            }
        }

        return qtdMoves;
    }

    private ArrayList getActions(PlayerAction pTemp, MoveArrayAbstract moves, Players playerToMove, GameState state) {
        HashMap<String, PlayerAction> actions = new HashMap<>();
        ArrayList<Action> acts = new ArrayList<>();
        for (Pair<Unit, UnitAction> choice : pTemp.getActions()) {
            Integer idIndex;
            if (lKp.UnitIDInserted(choice.m_a.getID())) {
                idIndex = lKp.getUnitIndex(choice.m_a.getID());
            } else {
                idIndex = lKp.InsertUnitIndex(choice.m_a.getID());
            }
            Action act;
            if (!_unitsAbsAB.contains(choice.m_a)) {
                act = new Action(idIndex, playerToMove.codigo(), choice.m_b.getType(), choice.m_b);
            } else {
                //take unit script action
                PlayerAction pAIUnit = null;
                AI ai = currentScriptData.getAIUnit(choice.m_a);
                if (actions.containsKey(ai.toString())) {
                    pAIUnit = actions.get(ai.toString());
                } else {
                    try {
                        pAIUnit = ai.getAction(playerToMove.codigo(), state);
                    } catch (Exception ex) {
                        System.out.println("Problem line 653 AlphaBetaSearchAbstract!");
                        Logger.getLogger(AlphaBetaSearchAbstract.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    actions.put(ai.toString(), pAIUnit);
                }
                UnitAction actAbst = pAIUnit.getAction(choice.m_a);
                act = new Action(idIndex, playerToMove.codigo(), actAbst.getType(), actAbst);
            }
            //moves.add(idIndex, act);
            acts.add(act);
        }

        return acts;
    }

    private void applyActionState(GameState child, ArrayList<Action> movesToAplly, MoveArrayAbstract moves) {
        lKp.refreshLookup(child);
        PlayerAction act = new PlayerAction();
        for (Action action : movesToAplly) {
            if (lKp.getOrigIDUnit(action.getUnit()) != null) {
                Unit unt = child.getUnit(lKp.getOrigIDUnit(action.getUnit()));
                if (unt != null) {
                    act.addUnitAction(unt, action.getUnitAction());
                }
            }
        }
        try {
        child.issue(act);
        } catch (Exception e) {
            System.out.println("Erro applyActionState line 674 " + e.toString());
         }

    }

    private PlayerAction makePlayerAction(GameState state, ArrayList<Action> movesToAplly, MoveArrayAbstract moves) {
        lKp.refreshLookup(state);
        PlayerAction act = new PlayerAction();
        for (Action action : movesToAplly) {
            if (lKp.getOrigIDUnit(action.getUnit()) != null) {
                act.addUnitAction(state.getUnit(lKp.getOrigIDUnit(action.getUnit())), action.getUnitAction());
            } else {
                System.out.println("ai.aiSelection.AlphaBetaSearch.AlphaBetaSearch.makePlayerAction() Erro ao encontrar unidade line 694!");
            }
        }
        return act;
    }

    // Transposition Table save 
    private void TTSave(GameState state, StateEvalScore value, StateEvalScore alpha, StateEvalScore beta, int depth,
            Players firstPlayer, AlphaBetaMove bestFirstMove, AlphaBetaMove bestSecondMove) {

        // IF THE DEPTH OF THE ENTRY IS BIGGER THAN CURRENT DEPTH, DO NOTHING
        TTEntry entry = _TT.lookupScan(calculateHash(0, state), calculateHash(1, state));
        boolean valid = entry != null && entry.isValid();
        int edepth = entry != null ? entry.getDepth() : 0;

        _results.setTtSaveAttempts(_results.getTtSaveAttempts() + 1);

        if (valid && (edepth > depth)) {
            return;
        }

        TTEntryEnum type = TTEntryEnum.NONE;

        if (value.menorIgual(alpha)) {
            type = TTEntryEnum.UPPER;
        } else if (value.maiorIgual(beta)) {
            type = TTEntryEnum.LOWER;
        } else {
            type = TTEntryEnum.ACCURATE;
        }
        // SAVE A NEW ENTRY IN THE TRANSPOSITION TABLE
        _TT.save(calculateHash(0, state), calculateHash(1, state), value, depth, type, firstPlayer.codigo(), bestFirstMove, bestSecondMove);
    }

    @Override
    public void reset() {
        this._TT = new TranspositionTable();
        this._results = new AlphaBetaSearchResults();
        this._allMoves.clear();
        this._currentRootDepth = 0;
    }

    @Override
    public PlayerAction getAction(int player, GameState gs) throws Exception {
        if (gs.canExecuteAnyAction(player)) {
            startNewComputation(player, gs);
            computeDuringOneGameFrame();
            return getBestActionSoFar();
        } else {
            return new PlayerAction();
        }
    }

    @Override
    public AI clone() {
        return new AlphaBetaSearchAbstract(TIME_BUDGET, ITERATIONS_BUDGET, _params, _TT);
    }

    @Override
    public List<ParameterSpecification> getParameters() {
        List<ParameterSpecification> parameters = new ArrayList<>();
        parameters.add(new ParameterSpecification("TimeBudget", int.class, 100));
        parameters.add(new ParameterSpecification("IterationsBudget", int.class, -1));
        parameters.add(new ParameterSpecification("PlayoutLookahead", int.class, 100));
        parameters.add(new ParameterSpecification("EvaluationFunction", EvaluationFunction.class, new SimpleSqrtEvaluationFunction3()));
        parameters.add(new ParameterSpecification("AlphaBetaSearchParameters", AlphaBetaSearchParameters.class, new AlphaBetaSearchParameters()));
        parameters.add(new ParameterSpecification("TranspositionTable", TranspositionTable.class, new TranspositionTable()));
        return parameters;
    }

    @Override
    public void startNewComputation(int player, GameState gs) throws Exception {
        playerForThisComputation = player;
        gs_to_start_from = gs;
        playerToGame = player;
        this._results = new AlphaBetaSearchResults();
    }

    @Override
    public void computeDuringOneGameFrame() throws Exception {
        doSearch(gs_to_start_from, playerToGame);
    }

    @Override
    public PlayerAction getBestActionSoFar() throws Exception {
        return _results.getBestMoves();
    }

    private void printMoveVec(ArrayList<Action> moveVec) {
        for (Action action : moveVec) {
            System.out.println(action.debugString());
        }
    }

    @Override
    public String toString() {
        return "AlphaBetaSearch{" + "_params=" + _params.getDescription() + '}';
    }

    protected void stopSearch() throws Exception {
        if (Duration.between(_searchTimer, Instant.now()).toMillis() >= (_params.getTimeLimit() - 1)) {
            //System.out.println("Stop the search!");
            throw new Exception("Time limit reached!");
        }
    }

    private int countElemArray(ArrayList[] orderedMoves) {
        int count = 0;
        for (ArrayList orderedMove : orderedMoves) {
            if (orderedMove != null) {
                count++;
            }
        }

        return count;
    }

    private void cleanMoves(ArrayList[] orderedMoves) {
        for (int i = 0; i < orderedMoves.length; i++) {
            orderedMoves[i] = null;

        }
    }

    public AI getPlayoutAI() {
        return _params.getSimScripts()[playerToGame];
    }

    public void setPlayoutAI(AI a_dp) {
        _params.setSimScripts(playerToGame, a_dp);
    }

    public AI getPlayoutAIEnemy() {
        return _params.getSimScripts()[getEnemy(Players.porCodigo(playerToGame))];
    }

    public void setPlayoutAIEnemy(AI playoutAIEnemy) {
        _params.setSimScripts(getEnemy(Players.porCodigo(playerToGame)), playoutAIEnemy);
    }

    @Override
    public void setTimeBudget(int a_tb) {
        TIME_BUDGET = a_tb;
        _params.setTimeLimit(a_tb);
    }

    public final PlayerAction getActionForAssymetric(int player, GameState gs, UnitScriptData currentScriptData, HashSet<Unit> unitsAbsAB) throws Exception {
        this.currentScriptData = currentScriptData;
        this._unitsAbsAB = unitsAbsAB;

        startNewComputation(player, gs);
        computeDuringOneGameFrame();
        return getBestActionSoFar();
    }

    public void setPlayerModel(int player, AI ai) {
        _params.setPlayerModel(player, ai);
    }

}
