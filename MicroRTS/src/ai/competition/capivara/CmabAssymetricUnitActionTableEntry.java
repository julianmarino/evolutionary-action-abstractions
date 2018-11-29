/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.competition.capivara;

import ai.CMAB.*;
import ai.mcts.naivemcts.*;
import java.util.List;
import rts.UnitAction;
import rts.units.Unit;

/**
 *
 * @author santi
 */
public class CmabAssymetricUnitActionTableEntry {
    public Unit u;
    public int nactions = 0;
    public List<UnitAction> actions = null;
    public double[] accum_evaluation = null;
    public int[] visit_count = null;
}
