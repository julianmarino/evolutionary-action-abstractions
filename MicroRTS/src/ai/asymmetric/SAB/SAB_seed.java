/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.asymmetric.SAB;

import ai.RandomAI;
import ai.RandomBiasedAI;
import ai.asymmetric.GAB.SandBox.*;
import ai.abstraction.pathfinding.AStarPathFinding;
import ai.abstraction.pathfinding.PathFinding;
import ai.asymmetric.ManagerUnits.IManagerAbstraction;
import ai.asymmetric.ManagerUnits.ManagerClosest;
import ai.asymmetric.ManagerUnits.ManagerClosestEnemy;
import ai.asymmetric.ManagerUnits.ManagerFartherEnemy;
import ai.asymmetric.ManagerUnits.ManagerFather;
import ai.asymmetric.ManagerUnits.ManagerLessDPS;
import ai.asymmetric.ManagerUnits.ManagerLessLife;
import ai.asymmetric.ManagerUnits.ManagerMoreDPS;
import ai.asymmetric.ManagerUnits.ManagerMoreLife;
import ai.asymmetric.ManagerUnits.ManagerRandom;
import ai.asymmetric.common.UnitScriptData;
import ai.core.AI;
import ai.core.AIWithComputationBudget;
import ai.core.InterruptibleAI;
import ai.core.ParameterSpecification;
import ai.evaluation.EvaluationFunction;
import ai.evaluation.SimpleSqrtEvaluationFunction3;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import rts.GameState;
import rts.PlayerAction;
import rts.UnitAction;
import rts.units.Unit;
import rts.units.UnitTypeTable;
import util.Pair;

/**
 *
 * @author rubens
 */
public class SAB_seed extends AIWithComputationBudget implements InterruptibleAI {

    EvaluationFunction evaluation = null;
    UnitTypeTable utt;
    PathFinding pf;
    SSSLimitRandom _sss = null;
    AlphaBetaSearchAbstract _ab = null;
    GameState gs_to_start_from = null;
    private int playerForThisComputation;
    int _time;
    int _max_playouts;
    HashSet<Unit> _unitsAbsAB;
    int _numUnits;
    int _numManager;
    IManagerAbstraction manager = null;
    boolean firstTime = true;

    //tste
    UnitScriptData currentScriptData;
    RandomBiasedAI rAI;

    public SAB_seed(UnitTypeTable utt) {
        this(100, 200, new SimpleSqrtEvaluationFunction3(),
                //new SimpleSqrtEvaluationFunction2(),
                //new LanchesterEvaluationFunction(),
                utt,
                new AStarPathFinding());
    }

    public SAB_seed(UnitTypeTable utt, int numUnits, int numManager) {
        this(100, 200, new SimpleSqrtEvaluationFunction3(), utt, new AStarPathFinding(), numUnits, numManager);
    }

    public SAB_seed(int time, int max_playouts, EvaluationFunction e, UnitTypeTable a_utt, PathFinding a_pf) {
        super(time, max_playouts);

        evaluation = e;
        utt = a_utt;
        pf = a_pf;
        _sss = new SSSLimitRandom(utt);
        _ab = new AlphaBetaSearchAbstract(utt);
        _time = time;
        _max_playouts = max_playouts;
        _unitsAbsAB = new HashSet<>();
        _numUnits = 2;
        _numManager = 1;
        rAI = new RandomBiasedAI(utt);
    }

    public SAB_seed(int time, int max_playouts, EvaluationFunction e, UnitTypeTable a_utt, PathFinding a_pf, int numUnits, int numManager) {
        super(time, max_playouts);

        evaluation = e;
        utt = a_utt;
        pf = a_pf;
        _sss = new SSSLimitRandom(utt);
        _ab = new AlphaBetaSearchAbstract(utt);
        _time = time;
        _max_playouts = max_playouts;
        _unitsAbsAB = new HashSet<>();
        _numUnits = numUnits;
        _numManager = numManager;
        rAI = new RandomBiasedAI(utt);
    }

    @Override
    public void reset() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PlayerAction getAction(int player, GameState gs) throws Exception {
        if (gs.canExecuteAnyAction(player)) {
            startManager(player, _numUnits);
            startNewComputation(player, gs);
            return getBestActionSoFar();
        } else {
            if ((gs.getNextChangeTime() - 1) == gs.getTime()) {
                //System.out.println("Next action " + gs.getNextChangeTime() + " actual time=" + gs.getTime());
                startNewComputation(player, gs);
                _sss.setTimeBudget(100);
                currentScriptData = _sss.continueImproveUnitScript(player, gs, currentScriptData);
                //currentScriptData = _pgs.getUnitScript(player, gs);
            }
            return new PlayerAction();
        }
    }

    protected void startManager(int playerID, int numUnits) {
        if (manager == null) {
            switch (_numManager) {
                case 0:
                    manager = new ManagerRandom(playerID, _numUnits);
                    break;
                case 1:
                    manager = new ManagerClosest(playerID, numUnits);
                    break;
                case 2:
                    manager = new ManagerClosestEnemy(playerID, numUnits);
                    break;
                case 3:
                    manager = new ManagerFather(playerID, numUnits);
                    break;
                case 4:
                    manager = new ManagerFartherEnemy(playerID, numUnits);
                    break;
                case 5:
                    manager = new ManagerLessLife(playerID, numUnits);
                    break;
                case 6:
                    manager = new ManagerMoreLife(playerID, numUnits);
                    break;
                case 7:
                    manager = new ManagerLessDPS(playerID, numUnits);
                case 8:
                    manager = new ManagerMoreDPS(playerID, numUnits);
                    break;
                default:
                    System.out.println("ai.asymmetric.SAB.SAB.startManager() Erro na escolha!");
            }
        }
    }

    @Override
    public AI clone() {
        return new SAB_seed(_time, _max_playouts, evaluation, utt, pf);
    }

    @Override
    public List<ParameterSpecification> getParameters() {
        List<ParameterSpecification> parameters = new ArrayList<>();

        parameters.add(new ParameterSpecification("TimeBudget", int.class, 100));
        parameters.add(new ParameterSpecification("IterationsBudget", int.class, -1));
        parameters.add(new ParameterSpecification("PlayoutLookahead", int.class, 200));
        parameters.add(new ParameterSpecification("EvaluationFunction", EvaluationFunction.class, new SimpleSqrtEvaluationFunction3()));
        parameters.add(new ParameterSpecification("PathFinding", PathFinding.class, new AStarPathFinding()));

        return parameters;
    }

    @Override
    public void startNewComputation(int player, GameState gs) throws Exception {
        playerForThisComputation = player;
        gs_to_start_from = gs;
    }

    @Override
    public void computeDuringOneGameFrame() throws Exception {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PlayerAction getBestActionSoFar() throws Exception {
        long start = System.currentTimeMillis();

        if (this.firstTime) {
            currentScriptData = _sss.getUnitScript(playerForThisComputation, gs_to_start_from);
            this.firstTime = false;
        } else if (hasNewUnitToImprove()) {
            //currentScriptData = _sss.getUnitScript(playerForThisComputation, gs_to_start_from);
            currentScriptData = _sss.continueImproveUnitScript(playerForThisComputation, gs_to_start_from, currentScriptData);
        }
        PlayerAction paSSS = _sss.getFinalAction(currentScriptData);
        if (_numUnits == 0) {
            return paSSS;
        }

        if ((System.currentTimeMillis() - start) < 90) {
            //System.out.println("Sobrou tempo para o AB:"+ (System.currentTimeMillis() - start));
            //aplico o AB
            manager.controlUnitsForAB(gs_to_start_from, _unitsAbsAB);

            _ab.setPlayoutAI(_sss.getDefaultScript().clone());
            _ab.setPlayoutAIEnemy(_sss.getEnemyScript().clone());
            _ab.setPlayerModel((1 - playerForThisComputation), _sss.getEnemyScript().clone());

            int timeUsed = (int) (System.currentTimeMillis() - start);
            if (timeUsed < 80) {
                _ab.setTimeBudget(100 - timeUsed);
            } else {
                _ab.setTimeBudget(20);
            }
            //System.out.println("----------------------------------------" + _unitsAbsAB);
            PlayerAction paAB = _ab.getActionForAssymetric(playerForThisComputation, gs_to_start_from, currentScriptData, _unitsAbsAB);
            //System.out.println("Results AB= "+ _ab.statisticsString());
            //System.out.println("Results AB= "+ paAB.toString());
            //if(_ab.getBestScore() > _pgs.getBestScore()){
            //if(playoutAnalise(paAB)> playoutAnalise(paSSS)){
            if (playoutAnaliseAVG(paAB) > playoutAnaliseAVG(paSSS)) { // playout with avg between playouts
                //if (playoutAnalise(paAB) > _pgs.getBestScore()) {
                //System.out.println("Escolhido paAB");
                //currentScriptData = new UnitScriptData(playerForThisComputation);
                return paAB;
            }
        }

        //System.out.println("Escolhido paPGS");
        //currentScriptData = new UnitScriptData(playerForThisComputation);
        return paSSS;

    }

    /**
     * Função que executa um playout de 1 ação e o restante de passos com o
     * script default escolhido pelo pgs.
     *
     * @param pa
     * @return
     */
    protected double playoutAnalise(PlayerAction pa) throws Exception {

        AI ai1 = _sss.getDefaultScript();
        AI ai2 = _sss.getEnemyScript();

        //AI ai1 = rAI;
        //AI ai2 = rAI;
        //boolean paUsed = false;
        //System.out.println(pa.toString());
        GameState gs2 = gs_to_start_from.clone();

        pa = changePlayer(playerForThisComputation, pa, gs2);
        gs2.issue(pa);

        ai1.reset();
        ai2.reset();
        //int timeLimit = gs2.getTime() + _max_playouts;
        int timeLimit = gs2.getTime() + 100;
        boolean gameover = false;
        while (!gameover && gs2.getTime() < timeLimit) {
            if (gs2.isComplete()) {
                gameover = gs2.cycle();
            } else {

                PlayerAction pa1 = ai1.getAction(playerForThisComputation, gs2);
                PlayerAction pa2 = ai2.getAction(1 - playerForThisComputation, gs2);
                gs2.issue(pa1);
                gs2.issue(pa2);
            }
        }
        double e = evaluation.evaluate(playerForThisComputation, 1 - playerForThisComputation, gs2);

        return e;
    }

    protected double playoutAnaliseAVG(PlayerAction pa) throws Exception {

        AI ai1 = rAI.clone();
        AI ai2 = rAI.clone();
        ai1.reset();
        ai2.reset();
        double e = 0.0000;
        //AI ai1 = rAI;
        //AI ai2 = rAI;

        //boolean paUsed = false;
        //System.out.println(pa.toString());
        for (int i = 0; i < 4; i++) {
            GameState gs2 = gs_to_start_from.clone();

            pa = changePlayer(playerForThisComputation, pa, gs2);
            gs2.issue(pa);

            ai1.reset();
            ai2.reset();
            //int timeLimit = gs2.getTime() + _max_playouts;
            int timeLimit = gs2.getTime() + 50;
            boolean gameover = false;
            while (!gameover && gs2.getTime() < timeLimit) {
                if (gs2.isComplete()) {
                    gameover = gs2.cycle();
                } else {

                    PlayerAction pa1 = ai1.getAction(playerForThisComputation, gs2);
                    PlayerAction pa2 = ai2.getAction(1 - playerForThisComputation, gs2);
                    gs2.issue(pa1);
                    gs2.issue(pa2);
                }
            }
            e += evaluation.evaluate(playerForThisComputation, 1 - playerForThisComputation, gs2);
        }
        //System.out.println("Eval ="+ (e/4.0) );
        return e/4.0;
    }

    protected PlayerAction changePlayer(int player, PlayerAction pa, GameState gs) {
        PlayerAction paR = new PlayerAction();

        for (Pair<Unit, UnitAction> tmp : pa.getActions()) {
            paR.addUnitAction(gs.getUnit(tmp.m_a.getID()), tmp.m_b);
        }

        return paR;
    }

    protected PlayerAction checkIntegrity(int player, PlayerAction pa) {
        List<Pair<Unit, UnitAction>> remActions = new ArrayList<>();

        for (Pair<Unit, UnitAction> tmp : pa.getActions()) {
            if (tmp.m_a.getPlayer() != player) {
                remActions.add(tmp);
            }
        }
        for (Pair<Unit, UnitAction> remAction : remActions) {
            pa.removeUnitAction(remAction.m_a, remAction.m_b);
        }

        return pa;
    }

    //verifica se o UnitScriptData contem todas as unidades.
    private boolean hasNewUnitToImprove() {
        ArrayList<Unit> unitsPlayer = getUnits(playerForThisComputation);
        List<Unit> unitsComputed = currentScriptData.getUnits();

        for (Unit unit : unitsPlayer) {
            if (!unitsComputed.contains(unit)) {
                return true;
            }
        }

        return false;
    }

    private ArrayList<Unit> getUnits(int player) {
        ArrayList<Unit> unitsPlayer = new ArrayList<>();
        for (Unit u : gs_to_start_from.getUnits()) {
            if (u.getPlayer() == player) {
                unitsPlayer.add(u);
            }
        }
        return unitsPlayer;
    }

    private void updateCurrentScriptData() {
        ArrayList<Unit> unitsPlayer = getUnits(playerForThisComputation);
        List<Unit> unitsComputed = currentScriptData.getUnits();

        for (Unit unit : unitsPlayer) {
            if (!unitsComputed.contains(unit)) {
                currentScriptData.setUnitScript(unit, _sss.getDefaultScript());
            }
        }
    }

    @Override
    public String toString() {
        //return "GAB{" + "_numUnits=" + _numUnits + ", numManager=" + _numManager + '}';
        return "SAB_Seed_" + _numUnits + "_" + _numManager;
    }

}
