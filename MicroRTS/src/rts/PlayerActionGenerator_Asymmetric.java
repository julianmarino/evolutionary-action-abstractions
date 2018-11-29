/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rts;

import ai.asymmetric.common.UnitScriptData;
import ai.core.AI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import rts.units.Unit;
import util.Pair;

/**
 *
 * @author santi e rubens esta versão do PlayerActionGenerator_Asymmetric
 * utiliza o UnitScriptData para sincronizar ações no conceito de abstrações
 * assimétricas
 */
public class PlayerActionGenerator_Asymmetric {

    static Random r = new Random();

    GameState gs;
    PhysicalGameState pgs;
    ResourceUsage base_ru;
    List<Pair<Unit, List<UnitAction>>> choices;
    PlayerAction lastAction = null;
    long size = 1;  // this will be capped at Long.MAX_VALUE;
    long generated = 0;
    int choiceSizes[] = null;
    int currentChoice[] = null;
    boolean moreActions = true;
    UnitScriptData currentScriptData = null;
    HashSet<Unit> unitsAbsAB = null;
    HashMap<String, PlayerAction> actions = new HashMap<>();

    public long getGenerated() {
        return generated;
    }

    public long getSize() {
        return size;
    }

    public PlayerAction getLastAction() {
        return lastAction;
    }

    public List<Pair<Unit, List<UnitAction>>> getChoices() {
        return choices;
    }

    public PlayerActionGenerator_Asymmetric(GameState a_gs, int pID, UnitScriptData currentScriptData, HashSet<Unit> unitsAbsAB) throws Exception {
        this.currentScriptData = currentScriptData;
        this.unitsAbsAB = unitsAbsAB;

        // Generate the reserved resources:
        base_ru = new ResourceUsage();
        gs = a_gs;
        pgs = gs.getPhysicalGameState();
        getUnitScriptDataActions(gs, pID);

        for (Unit u : pgs.getUnits()) {
            UnitActionAssignment uaa = gs.getActionAssignment(u);
            if (uaa != null) {
                ResourceUsage ru = uaa.action.resourceUsage(u, pgs);
                base_ru.merge(ru);
            }
        }

        choices = new ArrayList<>();
        for (Unit u : pgs.getUnits()) {
            if (u.getPlayer() == pID) {
                if (gs.getActionAssignment(u) == null) {
                    List<UnitAction> l = Collections.EMPTY_LIST;
                    //insero o controle da geração de ações para unidades controladas
                    if (unitIsControlled(u)) {
                        //se a unidade for controlada, não restrinjo os movimentos.
                        l = u.getUnitActions(gs);
                    } else {
                        //se a unidade existir no unitScriptData
                        if (unitExistScriptData(u)) {
                            //gero os movimentos com base nos scripts
                            //l = takeUnitScriptMove(u, gs, pID); //old version
                            l = takeActionCached(u, currentScriptData);
                        } else {
                            l = u.getUnitActions(gs);
                        }
                    }
                    choices.add(new Pair<>(u, l));
                    // make sure we don't overflow:
                    long tmp = l.size();
                    if (Long.MAX_VALUE / size <= tmp) {
                        size = Long.MAX_VALUE;
                    } else {
                        size *= (long) l.size();
                    }
//                    System.out.println("size = " + size);
                }
            }
        }
//        System.out.println("---");

        if (choices.size() == 0) {
            System.err.println("Problematic game state:");
            System.err.println(a_gs);
            throw new Exception("Move generator for player " + pID + " created with no units that can execute actions! (status: " + a_gs.canExecuteAnyAction(0) + ", " + a_gs.canExecuteAnyAction(1) + ")");
        }

        choiceSizes = new int[choices.size()];
        currentChoice = new int[choices.size()];
        int i = 0;
        for (Pair<Unit, List<UnitAction>> choice : choices) {
            choiceSizes[i] = choice.m_b.size();
            currentChoice[i] = 0;
            i++;
        }
    }

    public void randomizeOrder() {
        for (Pair<Unit, List<UnitAction>> choice : choices) {
            List<UnitAction> tmp = new LinkedList<>();
            tmp.addAll(choice.m_b);
            choice.m_b.clear();
            while (!tmp.isEmpty()) {
                choice.m_b.add(tmp.remove(r.nextInt(tmp.size())));
            }
        }
    }

    public void incrementCurrentChoice(int startPosition) {
        for (int i = 0; i < startPosition; i++) {
            currentChoice[i] = 0;
        }
        currentChoice[startPosition]++;
        if (currentChoice[startPosition] >= choiceSizes[startPosition]) {
            if (startPosition < currentChoice.length - 1) {
                incrementCurrentChoice(startPosition + 1);
            } else {
                moreActions = false;
            }
        }
    }

    public PlayerAction getNextAction(long cutOffTime) throws Exception {
        int count = 0;
        while (moreActions) {
            boolean consistent = true;
            PlayerAction pa = new PlayerAction();
            pa.setResourceUsage(base_ru.clone());
            int i = choices.size();
            if (i == 0) {
                throw new Exception("Move generator created with no units that can execute actions!");
            }
            while (i > 0) {
                i--;
                Pair<Unit, List<UnitAction>> unitChoices = choices.get(i);
                int choice = currentChoice[i];
                Unit u = unitChoices.m_a;
                UnitAction ua = unitChoices.m_b.get(choice);

                ResourceUsage r2 = ua.resourceUsage(u, pgs);

                if (pa.getResourceUsage().consistentWith(r2, gs)) {
                    pa.getResourceUsage().merge(r2);
                    pa.addUnitAction(u, ua);
                } else {
                    consistent = false;
                    break;
                }
            }

            incrementCurrentChoice(i);
            if (consistent) {
                lastAction = pa;
                generated++;
                return pa;
            }

            // check if we are over time (only check once every 1000 actions, since currenttimeMillis is a slow call):
            if (cutOffTime > 0 && (count % 1000 == 0) && System.currentTimeMillis() > cutOffTime) {
                lastAction = null;
                return null;
            }
            count++;
        }
        lastAction = null;
        return null;
    }

    public PlayerAction getRandom() {
        Random r = new Random();
        PlayerAction pa = new PlayerAction();
        pa.setResourceUsage(base_ru.clone());
        for (Pair<Unit, List<UnitAction>> unitChoices : choices) {
            List<UnitAction> l = new LinkedList<>();
            l.addAll(unitChoices.m_b);
            Unit u = unitChoices.m_a;

            boolean consistent = false;
            do {
                UnitAction ua = l.remove(r.nextInt(l.size()));
                ResourceUsage r2 = ua.resourceUsage(u, pgs);

                if (pa.getResourceUsage().consistentWith(r2, gs)) {
                    pa.getResourceUsage().merge(r2);
                    pa.addUnitAction(u, ua);
                    consistent = true;
                }
            } while (!consistent);
        }
        return pa;
    }

    public long getActionIndex(PlayerAction a) {
        int choice[] = new int[choices.size()];
        for (Pair<Unit, UnitAction> ua : a.actions) {
            int idx = 0;
            Pair<Unit, List<UnitAction>> ua_choice = null;
            for (Pair<Unit, List<UnitAction>> c : choices) {
                if (ua.m_a == c.m_a) {
                    ua_choice = c;
                    break;
                }
                idx++;
            }
            if (ua_choice == null) {
                return -1;
            }
            choice[idx] = ua_choice.m_b.indexOf(ua.m_b);

        }
        long index = 0;
        long multiplier = 1;
        for (int i = 0; i < choice.length; i++) {
            index += choice[i] * multiplier;
            multiplier *= choiceSizes[i];
        }
        return index;
    }

    public String toString() {
        String ret = "PlayerActionGenerator:\n";
        for (Pair<Unit, List<UnitAction>> choice : choices) {
            ret = ret + "  (" + choice.m_a + "," + choice.m_b.size() + ")\n";
        }
        ret += "currentChoice: ";
        for (int i = 0; i < currentChoice.length; i++) {
            ret += currentChoice[i] + " ";
        }
        ret += "\nactions generated so far: " + generated;
        return ret;
    }

    protected boolean unitIsControlled(Unit un) {
        for (Unit u : this.unitsAbsAB) {
            if (u.getID() == un.getID()) {
                return true;
            }
        }
        return false;
    }

    private List<UnitAction> takeUnitScriptMove(Unit u, GameState gs, int player) throws Exception {
        System.err.println("Mudar metodo takeUnitScriptMove... Acelerar!");
        List<UnitAction> actionUnit = new ArrayList<>();

        AI ai = currentScriptData.getAIUnit(u);
        ai.reset();
        PlayerAction acScript = ai.getAction(player, gs);
        UnitAction uAcTemp = acScript.getAction(gs.getUnit(u.getID()));
        if (uAcTemp != null) {
            actionUnit.add(uAcTemp);
        }

        return actionUnit;

    }

    private boolean unitExistScriptData(Unit u) {

        for (Unit us : currentScriptData.getUnits()) {
            if (us.getID() == u.getID()) {
                return true;
            }
        }

        return false;
    }

    private void getUnitScriptDataActions(GameState a_gs, int pID) {
        actions.clear();
        currentScriptData.getUniqueAI().forEach((_item) -> {
            try {
                _item.reset();
                actions.put(_item.toString(), _item.getAction(pID, a_gs));
            } catch (Exception ex) {
                //Logger.getLogger(PlayerActionGenerator_Asymmetric.class.getName()).log(Level.SEVERE, null, ex);
                System.err.println("Error getUnitScriptDataActions");
            }
        });
    }

    private List<UnitAction> takeActionCached(Unit u, UnitScriptData currentScriptData) {
        AI ai = currentScriptData.getAIUnit(u);
        List<UnitAction> actionUnit = new ArrayList<>();

        UnitAction unt = actions.get(ai.toString()).getAction(u);
        if (unt != null) {
            actionUnit.add(unt);
        }

        return actionUnit;
    }

}
