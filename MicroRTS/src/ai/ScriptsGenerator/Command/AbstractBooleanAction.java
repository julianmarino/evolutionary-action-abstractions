/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.ScriptsGenerator.Command;

import java.util.ArrayList;
import java.util.List;

import ai.ScriptsGenerator.Command.BasicAction.AttackBasic;
import ai.ScriptsGenerator.Command.BasicAction.MoveToUnitBasic;
import ai.ScriptsGenerator.Command.BasicAction.TrainBasic;
import ai.ScriptsGenerator.Command.Enumerators.EnumPositionType;
import ai.ScriptsGenerator.CommandInterfaces.ICommand;
import ai.ScriptsGenerator.IParameters.IDistance;
import ai.ScriptsGenerator.IParameters.IParameters;
import ai.ScriptsGenerator.IParameters.IQuantity;
import ai.ScriptsGenerator.ParametersConcrete.ClosestEnemy;
import ai.ScriptsGenerator.ParametersConcrete.PlayerTargetParam;
import ai.ScriptsGenerator.ParametersConcrete.LessHealthyEnemy;
import ai.ScriptsGenerator.ParametersConcrete.PriorityPositionParam;
import ai.ScriptsGenerator.ParametersConcrete.QuantityParam;
import ai.ScriptsGenerator.ParametersConcrete.TypeConcrete;
import ai.ScriptsGenerator.ParametersConcrete.UnitTypeParam;
import ai.abstraction.pathfinding.AStarPathFinding;
import ai.abstraction.pathfinding.PathFinding;
import rts.GameState;
import rts.PhysicalGameState;
import rts.PlayerAction;
import rts.ResourceUsage;
import rts.UnitAction;
import rts.units.Unit;
import rts.units.UnitTypeTable;
import util.Pair;

/**
 *
 * @author rubens Julian
 */
public abstract class AbstractBooleanAction extends AbstractCommand {
	
	 protected List<ICommand> commandsBoolean = new ArrayList<>();
	 protected UnitTypeTable utt;
	
    public PlayerAction appendCommands(int player, GameState gs, PlayerAction currentActions) {
        PathFinding pf = new AStarPathFinding();
        //simulate one WR
        for (ICommand command : commandsBoolean) {
            currentActions = command.getAction(gs, player, currentActions, pf, utt);
        }
        
        return currentActions;
    }
    
    //This method removes the default wait action to a unit, which was added just for
    //avoid apply actions to unitsthat doesnt sattisfy the boolean
    
    protected void restoreOriginalActions(GameState game, int player, ArrayList<Unit> unitstoApplyWait, PlayerAction currentPlayerAction)
    {
        for (Unit u : game.getUnits()) {
            if(unitstoApplyWait.contains(u) && u.getPlayer() == player)
            {
            	currentPlayerAction.removeUnitAction(u, currentPlayerAction.getAction(u));            	
            }
        }
    
    }
    
    //This method set a default wait action to a unit in order to avoid apply actions to units
    //that doesnt sattisfy the boolean
    protected void temporalWaitActions(GameState game, int player, ArrayList<Unit> unitstoApplyWait, PlayerAction currentPlayerAction)
    {
        for (Unit u : game.getUnits()) {
            if(unitstoApplyWait.contains(u) && u.getPlayer() == player)
            {
            	currentPlayerAction.addUnitAction(u, new UnitAction(0));
            }
        }
    
    }
}
