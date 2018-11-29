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
public class KitterDPS extends AbstractionLayerAI {

    Random r = new Random();
    protected UnitTypeTable utt;
    UnitType workerType;
    UnitType baseType;
    UnitType barracksType;
    UnitType lightType;
    int playerForComputation;
    GameState gs_to_Compute;
    PlayerAction playerActions;

    /*----------------------------------------------------------------------
 | Kiter DPS Player
 |----------------------------------------------------------------------
 | Chooses an action with following priority:
 | 1) If it can attack, ATTACK highest DPS/HP enemy unit in range
 | 2) If it cannot attack:
 |    a) If it is in range to attack an enemy, move away from closest one
 |    b) If it is not in range of enemy, MOVE towards closest one
 `----------------------------------------------------------------------*/
    public KitterDPS(UnitTypeTable a_utt) {
        this(a_utt, new AStarPathFinding());
    }

    public KitterDPS(UnitTypeTable a_utt, PathFinding a_pf) {
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
        return new KitterDPS(utt, pf);
    }

    public PlayerAction getAction(int player, GameState gs) {

        if (!gs.canExecuteAnyAction(player)) {
            return new PlayerAction();
        }

        PhysicalGameState pgs = gs.getPhysicalGameState();
        playerForComputation = player;
        gs_to_Compute = gs;
        playerActions = new PlayerAction();

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
                    && u.getPlayer() == player) {
                combatUnits.add(u);
            }
        }
        KiterKDPSBehavior(combatUnits, p, pgs);
        //workersBehavior(workers, p, pgs);

        // This method simply takes all the unit actions executed so far, and packages them into a PlayerAction
        //return translateActions(player, gs);
        playerActions.fillWithNones(gs, player, 10);
        return playerActions;
    }

    protected void KiterKDPSBehavior(List<Unit> units, Player p, PhysicalGameState pgs) {
        int enemyID = getEnemy(p.getID());
        ArrayList<Unit> enemyUnits = new ArrayList<>();

        for (Unit uEnemy : pgs.getUnits()) {
            if ((uEnemy.getType() != barracksType) && (uEnemy.getType() != baseType)
                    && uEnemy.getPlayer() == enemyID) {
                enemyUnits.add(uEnemy);
            }
        }

        for (Unit unit : units) {
            boolean foundAction = false;
            UnitAction actionMoveIndex = null;
            UnitAction closestMoveIndex = null;
            UnitAction furthestMoveIndex = null;
            double actionHighestDPS = 0;
            int closestMoveDist = Integer.MAX_VALUE;
            int furthestMoveDist = 0;

            Unit ourUnit = unit;
            Unit closestUnit = getClosestEnemyUnit(ourUnit, pgs);
            // the move we will be returning
            UnitAction bestMoveIndex = null;
            if (closestUnit != null) {

                for (UnitAction move : getUnitActions(ourUnit)) {
                    if ((move.getType() == UnitAction.TYPE_ATTACK_LOCATION)) {
                        Unit target = getEnemyByPosition(enemyUnits, move);
                        double dpsHPValue = -9999;
                        if(target != null){
                            dpsHPValue = (dpf(target) / target.getHitPoints());
                        }
                        if (dpsHPValue > actionHighestDPS) {
                            actionHighestDPS = dpsHPValue;
                            actionMoveIndex = move;
                            foundAction = true;
                        }
                    } else if ((move.getType() == UnitAction.TYPE_MOVE)) {
                        int destX = move.getLocationX(); //+ move.getDirection();
                        int destY = move.getLocationY(); //+ move.getDirection();

                        int dist = getDistanceSqToUnit(destX, destY, closestUnit.getX(), closestUnit.getY());
                        if (dist > furthestMoveDist) {
                            furthestMoveDist = dist;
                            furthestMoveIndex = move;
                        }
                        if (dist < closestMoveDist) {
                            closestMoveDist = dist;
                            closestMoveIndex = move;
                        }
                    }
                }
                

                // if we have an attack move we will use that one
                if (foundAction) {
                    bestMoveIndex = actionMoveIndex;
                } else {// otherwise use the closest move to the opponent
                    // if we are in attack range of the unit, back up
                    if (CanAttackTarget(closestUnit, ourUnit)) {
                        bestMoveIndex = furthestMoveIndex;
                    } else {// otherwise get back into the fight
                        bestMoveIndex = closestMoveIndex;
                    }
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
        //attack action : attack unit more close
        Unit enemy = getClosestEnemyUnit(ourUnit, gs_to_Compute.getPhysicalGameState());
        if (enemy != null) {
            UnitAction t = new Attack(ourUnit, enemy, pf).execute(gs_to_Compute);
            if (t != null) {
                ActionsT.add(t);
            } else {
                t = new Move(ourUnit, enemy.getX(), enemy.getY(), pf).execute(gs_to_Compute);
                if (t != null) {
                    ActionsT.add(t);
                }
            }

            //add all news attack actions
            for (Unit unitEn : gs_to_Compute.getUnits()) {
                if (unitEn.getPlayer() == (1 - playerForComputation)) {
                    //try attack that unit
                    t = new Attack(ourUnit, unitEn, pf).execute(gs_to_Compute);
                    if (t != null) {
                        if (t.getType() == UnitAction.TYPE_ATTACK_LOCATION) {
                            ActionsT.add(t);
                        }

                    }
                }
            }

            //action return back
            if (!ActionsT.isEmpty()) {
                UnitAction t4 = new Move(ourUnit, ourUnit.getX() - 1, ourUnit.getY() - 1, pf).execute(gs_to_Compute);
                if (t4 != null) {
                    ActionsT.add(t4);
                }
                t4 = new Move(ourUnit, ourUnit.getX() - 1, ourUnit.getY(), pf).execute(gs_to_Compute);
                if (t4 != null) {
                    ActionsT.add(t4);
                }
                t4 = new Move(ourUnit, ourUnit.getX(), ourUnit.getY() - 1, pf).execute(gs_to_Compute);
                if (t4 != null) {
                    ActionsT.add(t4);
                }
                UnitAction t5 = new Move(ourUnit, ourUnit.getX() + 1, ourUnit.getY() + 1, pf).execute(gs_to_Compute);
                if (t5 != null) {
                    ActionsT.add(t5);
                }
                t5 = new Move(ourUnit, ourUnit.getX() + 1, ourUnit.getY(), pf).execute(gs_to_Compute);
                if (t5 != null) {
                    ActionsT.add(t5);
                }
                t5 = new Move(ourUnit, ourUnit.getX(), ourUnit.getY() + 1, pf).execute(gs_to_Compute);
                if (t5 != null) {
                    ActionsT.add(t5);
                }
            }
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
        return (int) Math.sqrt(((pXinicial - pXfinal) * (pXinicial - pXfinal))
                + ((pYinicial - pYfinal) * (pYinicial - pYfinal)));
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
