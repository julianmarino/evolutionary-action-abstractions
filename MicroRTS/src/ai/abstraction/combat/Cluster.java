/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.abstraction.combat;

import ai.abstraction.*;
import ai.abstraction.pathfinding.AStarPathFinding;
import ai.core.AI;
import ai.abstraction.pathfinding.PathFinding;
import ai.core.ParameterSpecification;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import rts.GameState;
import rts.PhysicalGameState;
import rts.Player;
import rts.PlayerAction;
import rts.UnitAction;
import rts.units.*;

/**
 *
 * @author Rubens
 */
public class Cluster extends AbstractionLayerAI {

    Random r = new Random();
    protected UnitTypeTable utt;
    UnitType workerType;
    UnitType baseType;
    UnitType barracksType;
    UnitType lightType;
    int playerForComputation;
    GameState gs_to_Compute;
    PlayerAction playerActions;
    
    private int _cenX, _cenY;

    /*----------------------------------------------------------------------
 | Cluster Player
 |----------------------------------------------------------------------
 | Chooses an action with following priority:
 | 1) Move to centroid of our unit cluster
 `----------------------------------------------------------------------*/
    public Cluster(UnitTypeTable a_utt) {
        this(a_utt, new AStarPathFinding());
    }

    public Cluster(UnitTypeTable a_utt, PathFinding a_pf) {
        super(a_pf);
        reset(a_utt);
    }

    @Override
    public void reset() {
        super.reset();
    }

    @Override
    public void reset(UnitTypeTable a_utt) {
        utt = a_utt;
        workerType = utt.getUnitType("Worker");
        baseType = utt.getUnitType("Base");
        barracksType = utt.getUnitType("Barracks");
        lightType = utt.getUnitType("Light");
    }

    @Override
    public AI clone() {
        return new Cluster(utt, pf);
    }

    public PlayerAction getAction(int player, GameState gs) {

        if (!gs.canExecuteAnyAction(player)) {
            return new PlayerAction();
        }

        PhysicalGameState pgs = gs.getPhysicalGameState();
        playerForComputation = player;
        gs_to_Compute = gs;
        playerActions = new PlayerAction();
        _cenX = 0;
        _cenY = 0;

        Player p = gs.getPlayer(player);
//        System.out.println("LightRushAI for player " + player + " (cycle " + gs.getTime() + ")");

        List<Unit> combatUnits = new LinkedList<>();

        // behavior of melee units:
        for (Unit u : pgs.getUnits()) {
            if (u.getType().canAttack && !u.getType().canHarvest
                    && u.getPlayer() == player
                    && gs.getActionAssignment(u) == null) {
                combatUnits.add(u);
            }
        }

        // behavior of workers:
        for (Unit u : pgs.getUnits()) {
            if (u.getType().canHarvest
                    && gs.getActionAssignment(u) == null
                    && u.getPlayer() == player) {
                combatUnits.add(u);
            }
        }
        ClusterBehavior(combatUnits, p, pgs);
        //workersBehavior(workers, p, pgs);

        // This method simply takes all the unit actions executed so far, and packages them into a PlayerAction
        //return translateActions(player, gs);   
        playerActions.fillWithNones(gs, player, 10);
        return playerActions;
    }
    
    private void CalcCentroide(ArrayList<Unit> unidades) {
        int x = 0, y = 0;
        for (Unit un : unidades) {
            x += un.getX();
            y += un.getY();
        }
        if(unidades.size()>0){
            x = x / unidades.size();
            y = y / unidades.size();
        }
        _cenX = x;
        _cenY = y;
    }

    protected void ClusterBehavior(List<Unit> units, Player p, PhysicalGameState pgs) {
        int enemyID = getEnemy(p.getID());

        // compute the centroid of our unit cluster
        ArrayList<Unit> myUnits = new ArrayList<>();

        for (Unit uUnit : pgs.getUnits()) {
            if ((uUnit.getType() != barracksType) && (uUnit.getType() != baseType)
                    && uUnit.getPlayer() == p.getID()) {
                myUnits.add(uUnit);
            }
        }
        CalcCentroide(myUnits);
        
        for (Unit unit : units) {
            boolean foundAction = false;
            UnitAction closestMoveIndex = null;
            double actionHighestDPS = 0;
            int closestMoveDist = Integer.MAX_VALUE;
            

            Unit ourUnit = unit;
            List<UnitAction> actionsUnit = getUnitActions(ourUnit);
            for (UnitAction move : actionsUnit) {
                 if ((move.getType() == UnitAction.TYPE_MOVE)) {

                    int dist = getDistanceSqToUnit(move.getLocationX(),//+move.getDirection(), 
                                                   move.getLocationY(),//+move.getDirection(), 
                                                   _cenX, _cenY);

                    if (dist < closestMoveDist) {
                        closestMoveDist = dist;
                        closestMoveIndex = move;
                        foundAction = true;
                    }
                }
            }
            // the move we will be returning
            UnitAction bestMoveIndex = null;

            if (foundAction) {
                bestMoveIndex = closestMoveIndex;
            } else {
                if(!actionsUnit.isEmpty()){
                    bestMoveIndex = actionsUnit.get(0);
                }
            }

            if (bestMoveIndex == null) {
                bestMoveIndex = new UnitAction(UnitAction.TYPE_NONE, 10);
            }

            playerActions.addUnitAction(ourUnit, bestMoveIndex);
        }
    }

    /**
     * This function need to be improved.
     *
     * @param ourUnit
     * @return
     */
    private List<UnitAction> getUnitActions(Unit ourUnit) {
        ArrayList<UnitAction> ActionsT = new ArrayList<>();
        
        UnitAction t2 = new Move(ourUnit,_cenX,_cenY, pf).execute(gs_to_Compute);
        if(t2!= null){
            ActionsT.add(t2);
        }
        t2 = new Move(ourUnit, ourUnit.getX()+1,ourUnit.getY(), pf).execute(gs_to_Compute);
        if(t2!= null){
            ActionsT.add(t2);
        }
        t2 = new Move(ourUnit, ourUnit.getX(),ourUnit.getY()+1, pf).execute(gs_to_Compute);
        if(t2!= null){
            ActionsT.add(t2);
        }
        t2 = new Move(ourUnit, ourUnit.getX()+1,ourUnit.getY()+1, pf).execute(gs_to_Compute);
        if(t2!= null){
            ActionsT.add(t2);
        }
        t2 = new Move(ourUnit, ourUnit.getX()-1,ourUnit.getY(), pf).execute(gs_to_Compute);
        if(t2!= null){
            ActionsT.add(t2);
        }
        t2 = new Move(ourUnit, ourUnit.getX(),ourUnit.getY()-1, pf).execute(gs_to_Compute);
        if(t2!= null){
            ActionsT.add(t2);
        }
        t2 = new Move(ourUnit, ourUnit.getX()-1,ourUnit.getY()-1, pf).execute(gs_to_Compute);
        if(t2!= null){
            ActionsT.add(t2);
        }
        
        return ActionsT;
    }

    private Unit getClosestUnit(Unit allyUnit, PhysicalGameState pgs) {
        Unit closestAlly = null;
        int closestDistance = 0;
        for (Unit u2 : pgs.getUnits()) {
            if (u2.getPlayer() >= 0 && u2.getPlayer() == playerForComputation && allyUnit != u2) {
                int d = Math.abs(u2.getX() - allyUnit.getX()) + Math.abs(u2.getY() - allyUnit.getY());
                if (closestAlly == null || d < closestDistance) {
                    closestAlly = u2;
                    closestDistance = d;
                }
            }
        }
        return closestAlly;
    }

    protected int getEnemy(int playerID) {
        return (playerID + 1) % 2;
    }

    public void baseBehavior(Unit u, Player p, PhysicalGameState pgs) {
        int nworkers = 0;
        for (Unit u2 : pgs.getUnits()) {
            if (u2.getType() == workerType
                    && u2.getPlayer() == p.getID()) {
                nworkers++;
            }
        }
        if (nworkers < 1 && p.getResources() >= workerType.cost) {
            train(u, workerType);
        }
    }

    public void barracksBehavior(Unit u, Player p, PhysicalGameState pgs) {
        if (p.getResources() >= lightType.cost) {
            train(u, lightType);
        }
    }

    public void meleeUnitBehavior(Unit u, Player p, GameState gs) {
        PhysicalGameState pgs = gs.getPhysicalGameState();
        Unit closestEnemy = null;
        int closestDistance = 0;
        for (Unit u2 : pgs.getUnits()) {
            if (u2.getPlayer() >= 0 && u2.getPlayer() != p.getID()) {
                int d = Math.abs(u2.getX() - u.getX()) + Math.abs(u2.getY() - u.getY());
                if (closestEnemy == null || d < closestDistance) {
                    closestEnemy = u2;
                    closestDistance = d;
                }
            }
        }
        if (closestEnemy != null) {
//            System.out.println("LightRushAI.meleeUnitBehavior: " + u + " attacks " + closestEnemy);
            attack(u, closestEnemy);
        }
    }

    public void workersBehavior(List<Unit> workers, Player p, PhysicalGameState pgs) {
        int nbases = 0;
        int nbarracks = 0;

        int resourcesUsed = 0;
        List<Unit> freeWorkers = new LinkedList<Unit>();
        freeWorkers.addAll(workers);

        if (workers.isEmpty()) {
            return;
        }

        for (Unit u2 : pgs.getUnits()) {
            if (u2.getType() == baseType
                    && u2.getPlayer() == p.getID()) {
                nbases++;
            }
            if (u2.getType() == barracksType
                    && u2.getPlayer() == p.getID()) {
                nbarracks++;
            }
        }

        List<Integer> reservedPositions = new LinkedList<Integer>();
        if (nbases == 0 && !freeWorkers.isEmpty()) {
            // build a base:
            if (p.getResources() >= baseType.cost + resourcesUsed) {
                Unit u = freeWorkers.remove(0);
                buildIfNotAlreadyBuilding(u, baseType, u.getX(), u.getY(), reservedPositions, p, pgs);
                resourcesUsed += baseType.cost;
            }
        }

        if (nbarracks == 0) {
            // build a barracks:
            if (p.getResources() >= barracksType.cost + resourcesUsed && !freeWorkers.isEmpty()) {
                Unit u = freeWorkers.remove(0);
                buildIfNotAlreadyBuilding(u, barracksType, u.getX(), u.getY(), reservedPositions, p, pgs);
                resourcesUsed += barracksType.cost;
            }
        }

        // harvest with all the free workers:
        for (Unit u : freeWorkers) {
            Unit closestBase = null;
            Unit closestResource = null;
            int closestDistance = 0;
            for (Unit u2 : pgs.getUnits()) {
                if (u2.getType().isResource) {
                    int d = Math.abs(u2.getX() - u.getX()) + Math.abs(u2.getY() - u.getY());
                    if (closestResource == null || d < closestDistance) {
                        closestResource = u2;
                        closestDistance = d;
                    }
                }
            }
            closestDistance = 0;
            for (Unit u2 : pgs.getUnits()) {
                if (u2.getType().isStockpile && u2.getPlayer() == p.getID()) {
                    int d = Math.abs(u2.getX() - u.getX()) + Math.abs(u2.getY() - u.getY());
                    if (closestBase == null || d < closestDistance) {
                        closestBase = u2;
                        closestDistance = d;
                    }
                }
            }
            if (closestResource != null && closestBase != null) {
                AbstractAction aa = getAbstractAction(u);
                if (aa instanceof Harvest) {
                    Harvest h_aa = (Harvest) aa;
                    if (h_aa.getTarget() != closestResource || h_aa.getBase() != closestBase) {
                        harvest(u, closestResource, closestBase);
                    }
                } else {
                    harvest(u, closestResource, closestBase);
                }
            }
        }
    }

    @Override
    public List<ParameterSpecification> getParameters() {
        List<ParameterSpecification> parameters = new ArrayList<>();

        parameters.add(new ParameterSpecification("PathFinding", PathFinding.class, new AStarPathFinding()));

        return parameters;
    }

    private int getHPRemaining(ArrayList<Unit> units, UnitAction move) {
        for (Unit un : units) {
            if (un.getX() == move.getLocationX() && un.getY() == move.getLocationY()) {
                return un.getHitPoints();
            }
        }

        return 0;
    }

    private Unit getEnemyByPosition(ArrayList<Unit> units, UnitAction move) {
        for (Unit un : units) {
            if (un.getX() == move.getLocationX() && un.getY() == move.getLocationY()) {
                return un;
            }
        }

        return null;
    }

    private double dpf(Unit target) {
        return Double.max(target.getMinDamage(), (((double) target.getMaxDamage()) / ((double) target.getAttackTime() + 1.0)));
    }

    private int getDistanceSqToUnit(int pXinicial, int pYinicial, int pXfinal, int pYfinal) {
        return (int) Math.sqrt( ((pXinicial - pXfinal) * (pXinicial - pXfinal))
                + ((pYinicial - pYfinal) * (pYinicial - pYfinal)) );
    }

    private Unit getClosestEnemyUnit(Unit allyUnit, PhysicalGameState pgs) {
        Unit closestEnemy = null;
        int closestDistance = 0;
        for (Unit u2 : pgs.getUnits()) {
            if (u2.getPlayer() >= 0 && u2.getPlayer() != playerForComputation) {
                int d = Math.abs(u2.getX() - allyUnit.getX()) + Math.abs(u2.getY() - allyUnit.getY());
                if (closestEnemy == null || d < closestDistance) {
                    closestEnemy = u2;
                    closestDistance = d;
                }
            }
        }
        return closestEnemy;
    }

    private boolean CanAttackTarget(Unit ourUnit, Unit closestUnit) {
        int dx = closestUnit.getX() - ourUnit.getX();
        int dy = closestUnit.getY() - ourUnit.getY();
        double d = Math.sqrt(dx * dx + dy * dy);
        return d <= ourUnit.getAttackRange();
    }

}
