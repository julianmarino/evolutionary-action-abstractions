/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.ScriptsGenerator;

import ai.ScriptsGenerator.Command.BasicAction.AttackBasic;
import ai.ScriptsGenerator.Command.BasicAction.BuildBasic;
import ai.ScriptsGenerator.Command.BasicAction.TrainBasic;
import ai.ScriptsGenerator.Command.BasicBoolean.AllyRange;
import ai.ScriptsGenerator.Command.BasicAction.HarvestBasic;
import ai.ScriptsGenerator.Command.BasicAction.MoveToCoordinatesBasic;
import ai.ScriptsGenerator.Command.BasicAction.MoveToUnitBasic;
import ai.ScriptsGenerator.Command.Enumerators.EnumPositionType;
import ai.ScriptsGenerator.Command.Enumerators.EnumPlayerTarget;
import ai.ScriptsGenerator.CommandInterfaces.ICommand;
import ai.ScriptsGenerator.ParametersConcrete.ClosestEnemy;
import ai.ScriptsGenerator.ParametersConcrete.CoordinatesParam;
import ai.ScriptsGenerator.ParametersConcrete.FarthestEnemy;
import ai.ScriptsGenerator.ParametersConcrete.LessHealthyEnemy;
import ai.ScriptsGenerator.ParametersConcrete.MostHealthyEnemy;
import ai.ScriptsGenerator.ParametersConcrete.PlayerTargetParam;
import ai.ScriptsGenerator.ParametersConcrete.PriorityPositionParam;
import ai.ScriptsGenerator.ParametersConcrete.QuantityParam;
import ai.ScriptsGenerator.ParametersConcrete.StrongestEnemy;
import ai.ScriptsGenerator.ParametersConcrete.TypeConcrete;
import ai.ScriptsGenerator.ParametersConcrete.WeakestEnemy;
import ai.abstraction.pathfinding.AStarPathFinding;
import ai.abstraction.pathfinding.PathFinding;
import java.util.ArrayList;
import java.util.List;
import rts.GameState;
import rts.PlayerAction;
import rts.units.UnitTypeTable;

/**
 *
 * @author rubens
 */
public class Chromosome {

    List<ICommand> commands = new ArrayList<>();
    List<ICommand> commandsforBoolean = new ArrayList<>();
    UnitTypeTable utt;

    public Chromosome(UnitTypeTable utt) {
        this.utt = utt;

        
        //build action
        //BuildBasic build = new BuildBasic();
        //build.addParameter(TypeConcrete.getTypeBarracks()); //add unit construct type
        //build.addParameter(new QuantityParam(1)); //add qtd unit
        //commands.add(build);
        
        //train action
        TrainBasic train = new TrainBasic();
        train.addParameter(TypeConcrete.getTypeBase()); //add unit construct type
        train.addParameter(TypeConcrete.getTypeWorker()); //add unit Type
        //train.addParameter(TypeConcrete.getTypeWorker()); //add unit Type
        train.addParameter(new QuantityParam(20)); //add qtd unit
        PriorityPositionParam pos = new PriorityPositionParam();
        pos.addPosition(EnumPositionType.EnemyDirection);
        //pos.addPosition(EnumPositionType.Left);
        //pos.addPosition(EnumPositionType.Right);
        //pos.addPosition(EnumPositionType.Down);
        train.addParameter(pos);
        commands.add(train);
        //harverst action
        HarvestBasic harverst = new HarvestBasic();
        harverst.addParameter(TypeConcrete.getTypeWorker()); //add unit type
        harverst.addParameter(new QuantityParam(1)); //add qtd unit
        commands.add(harverst);
        //attack action
        AttackBasic attack = new AttackBasic();
        attack.addParameter(TypeConcrete.getTypeUnits()); //add unit type
        PlayerTargetParam pt=new PlayerTargetParam();
        pt.addPlayer(EnumPlayerTarget.Enemy);
        attack.addParameter(pt);
        attack.addParameter(new ClosestEnemy()); //add behavior
        commands.add(attack);
        //Move action
//        MoveToUnitBasic moveToUnit = new MoveToUnitBasic();
//        moveToUnit.addParameter(TypeConcrete.getTypeUnits()); //add unit type
//        moveToUnit.addParameter(new ClosestEnemy()); //add behavior
//        commands.add(moveToUnit);
        	//Move To coordinates
//        MoveToCoordinatesBasic moveToCoordinates = new MoveToCoordinatesBasic();
//        moveToCoordinates.addParameter(new CoordinatesParam(6,6)); //add unit type
//        moveToCoordinates.addParameter(TypeConcrete.getTypeUnits());
//        commands.add(moveToCoordinates);
        
        //BOOLEAN  If there is an enemy in allyRange 
        MoveToUnitBasic moveToUnit = new MoveToUnitBasic();
        moveToUnit.addParameter(TypeConcrete.getTypeUnits()); //add unit type
        pt=new PlayerTargetParam();
        pt.addPlayer(EnumPlayerTarget.Enemy);
        moveToUnit.addParameter(pt);
        moveToUnit.addParameter(new LessHealthyEnemy()); //add behavior
        commandsforBoolean = new ArrayList<>();
        commandsforBoolean.add(moveToUnit);

        AllyRange allyRangeBoolean = new AllyRange(commandsforBoolean);
        allyRangeBoolean.addParameter(TypeConcrete.getTypeUnits());
        commands.add(allyRangeBoolean);


        System.out.println("t "+allyRangeBoolean.toString());
    }

    public PlayerAction getAction(int player, GameState gs) {
        PlayerAction currentActions = new PlayerAction();
        PathFinding pf = new AStarPathFinding();

        //simulate one WR
        for (ICommand command : commands) {
            currentActions = command.getAction(gs, player, currentActions, pf, utt);
        }

        return currentActions;
    }
}
