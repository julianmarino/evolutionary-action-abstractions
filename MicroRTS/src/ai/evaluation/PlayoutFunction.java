/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.evaluation;

import ai.core.AI;
import java.util.logging.Level;
import java.util.logging.Logger;
import rts.GameState;
import rts.PhysicalGameState;
import rts.units.*;

/**
 *
 * @author rubens
 *
 * This function uses the same base evaluation as SparCraft LTD2
 */
public class PlayoutFunction extends EvaluationFunction {

    public static float RESOURCE = 20;
    public static float RESOURCE_IN_WORKER = 10;
    public static float UNIT_BONUS_MULTIPLIER = 40.0f;
    public AI p1;
    public AI p2;
    public EvaluationFunction eval;

    public PlayoutFunction(AI p1, AI p2, EvaluationFunction eval) {
        this.p1 = p1;
        this.p2 = p2;
        this.eval = eval;
    }

    @Override
    public float evaluate(int maxplayer, int minplayer, GameState gs) {

        GameState gs2 = gs.clone();
        boolean gameover = false;
        int time = gs2.getTime() + 100;
        do {
            if (gs2.isComplete()) {
                gameover = gs2.cycle();
            } else {
                try {
                    gs2.issue(p1.getAction(0, gs2));
                    gs2.issue(p2.getAction(1, gs2));
                } catch (Exception ex) {
                    //Logger.getLogger(PlayoutFunction.class.getName()).log(Level.SEVERE, null, ex);
                    return eval.evaluate(maxplayer, minplayer, gs2);
                }
            }
        } while (!gameover && gs2.getTime() < time);

        return eval.evaluate(maxplayer, minplayer, gs2);

    }

    protected float LTD2_score(int player, GameState gs) {
        if (numUnits(player, gs) == 0) {
            return 0.0f;
        }

        float sum = 0.0f;
        float totalSQRT = 0.0f;

        for (Unit unit : gs.getUnits()) {
            if (unit.getPlayer() == player) {
                totalSQRT += Math.sqrt(unit.getMaxHitPoints()) * dpf(unit);
                sum += Math.sqrt(unit.getHitPoints()) * dpf(unit);
            }
        }
        float ret = (1000 * sum / totalSQRT);

        return ret;
    }

    private float dpf(Unit target) {
        return Float.max(target.getMinDamage(), (float) (((float) target.getMaxDamage()) / ((float) target.getAttackTime() + 1.0)));
    }

    public float base_score(int player, GameState gs) {
        PhysicalGameState pgs = gs.getPhysicalGameState();
        float score = gs.getPlayer(player).getResources() * RESOURCE;
        boolean anyunit = false;
        for (Unit u : pgs.getUnits()) {
            if (u.getPlayer() == player) {
                anyunit = true;
                score += u.getResources() * RESOURCE_IN_WORKER;
                score += UNIT_BONUS_MULTIPLIER * u.getCost() * Math.sqrt(u.getHitPoints() / u.getMaxHitPoints());
            }
        }
        if (!anyunit) {
            return 0;
        }
        return score;
    }

    @Override
    public float upperBound(GameState gs) {
        return 1.0f;
    }

    private int numUnits(int player, GameState gs) {
        int qtdUnits = 0;
        for (Unit unit : gs.getUnits()) {
            if (unit.getPlayer() == player) {
                qtdUnits++;
            }
        }
        return qtdUnits;
    }

    @Override
    public String toString() {
        return "PlayoutFunction{" + "p1=" + p1 + ", p2=" + p2 + ", eval=" + eval + '}';
    }

}
