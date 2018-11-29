package ai.cluster.core.distance;

import java.util.ArrayList;
import java.util.List;
import rts.GameState;
import rts.units.Unit;

/**
 * Computes the euclidean distance between two points, d = sqrt((x1-y1)^2 +
 * (x2-y2)^2 + ... + (xn-yn)^2).
 *
 * @author zjullion
 */
public class EuclideanDistanceByInfluence implements DistanceCalculator {

    // ------------------------------ PRIVATE VARIABLES ------------------------------
    private GameState gsLocal;
    private int player;

    // ------------------------------ CONSTANTS ------------------------------
    // ------------------------------ CONSTRUCTORS ------------------------------
    public EuclideanDistanceByInfluence(GameState gs, int player) {
        this.gsLocal = gs;
        this.player = player;
    }

    // ------------------------------ PUBLIC METHODS ------------------------------
    @Override
    public double computeDistance(double[] attributesOne, double[] attributesTwo) {
        double distance = 0;

        for (int i = 0; i < attributesOne.length && i < attributesTwo.length; i++) {
            distance += ((attributesOne[i] - attributesTwo[i]) * (attributesOne[i] - attributesTwo[i]));
        }
        Unit un1 = getUnitByPos(attributesOne, gsLocal.getUnits());
        Unit un2 = getUnitByPos(attributesTwo, gsLocal.getUnits());

        //verify if bouth unit are observable 
        if (observable(un1, un2)) {
            double value = Math.sqrt(distance);
            //System.out.println("Value "+ value + " influence "+ (value - (value*0.1)));
            //remove 20% of value to improve by influence
            return value - (value*0.2);
        } else {
            return Math.sqrt(distance);
        }
    }

    public String getName() {
        return "euclideanInfluence";
    }

    // ------------------------------ PRIVATE METHODS ------------------------------
    private boolean observable(Unit un1, Unit un2) {

        double d = Math.sqrt((un2.getX() - un1.getX()) * (un2.getX() - un1.getX()) + (un2.getY() - un1.getY()) * (un2.getY() - un1.getY()));
        if (d <= un2.getType().sightRadius + 1) {
            return true;
        }

        return false;
    }

    private Unit getUnitByPos(double[] tPos, List<Unit> unitsCl) {
        for (Unit unit : unitsCl) {
            if (unit.getX() == tPos[0] && unit.getY() == tPos[1]) {
                return unit;
            }
        }
        return null;
    }

    // ------------------------------ GETTERS & SETTERS ------------------------------
}
