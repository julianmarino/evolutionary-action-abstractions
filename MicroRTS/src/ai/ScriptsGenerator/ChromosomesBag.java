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
import ai.ScriptsGenerator.Command.BasicBoolean.DistanceFromEnemy;
import ai.ScriptsGenerator.Command.BasicBoolean.EnemyRange;
import ai.ScriptsGenerator.Command.BasicBoolean.NAllyUnitsAttacking;
import ai.ScriptsGenerator.Command.BasicBoolean.NAllyUnitsHarvesting;
import ai.ScriptsGenerator.Command.BasicBoolean.NAllyUnitsofType;
import ai.ScriptsGenerator.Command.BasicBoolean.NEnemyUnitsofType;
import ai.ScriptsGenerator.Command.BasicAction.HarvestBasic;
import ai.ScriptsGenerator.Command.BasicAction.MoveToCoordinatesBasic;
import ai.ScriptsGenerator.Command.BasicAction.MoveToUnitBasic;
import ai.ScriptsGenerator.Command.Enumerators.EnumPlayerTarget;
import ai.ScriptsGenerator.Command.Enumerators.EnumPositionType;
import ai.ScriptsGenerator.CommandInterfaces.ICommand;
import ai.ScriptsGenerator.ParametersConcrete.ClosestEnemy;
import ai.ScriptsGenerator.ParametersConcrete.CoordinatesParam;
import ai.ScriptsGenerator.ParametersConcrete.DistanceParam;
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
public class ChromosomesBag {

    List<ICommand> commands2 = new ArrayList<>();
    List<ICommand> commandsforBoolean;
    UnitTypeTable utt;

    public ChromosomesBag(UnitTypeTable utt) {
    }

    //Done
    public List<ICommand> ChromosomesBag1(UnitTypeTable utt) {
        this.utt = utt;
        List<ICommand> commands = new ArrayList<>();
        //BASIC ACTIONS**********************************************************************

        //build action
        BuildBasic build = new BuildBasic();
        build.addParameter(TypeConcrete.getTypeBarracks()); //add unit construct type
        build.addParameter(new QuantityParam(1)); //add qtd unit
        commands.add(build);

        //train action
        TrainBasic train = new TrainBasic();
        train.addParameter(TypeConcrete.getTypeBase()); //add unit construct type
        train.addParameter(TypeConcrete.getTypeWorker()); //add unit Type
        train.addParameter(new QuantityParam(100)); //add qtd unit
        PriorityPositionParam pos = new PriorityPositionParam();
        pos.addPosition(EnumPositionType.Up);
        pos.addPosition(EnumPositionType.Left);
        //pos.addPosition(EnumPositionType.Right);
        //pos.addPosition(EnumPositionType.Down);
        train.addParameter(pos);
        commands.add(train);

        //harverst action
        HarvestBasic harverst = new HarvestBasic();
        harverst.addParameter(TypeConcrete.getTypeWorker()); //add unit type
        harverst.addParameter(new QuantityParam(1)); //add qtd unit
        commands.add(harverst);

        AttackBasic attack = new AttackBasic();
        attack.addParameter(TypeConcrete.getTypeUnits()); //add unit type
        PlayerTargetParam pt=new PlayerTargetParam();
        pt.addPlayer(EnumPlayerTarget.Enemy);
        attack.addParameter(pt);
        attack.addParameter(new ClosestEnemy()); //add behavior   
        commandsforBoolean = new ArrayList<>();
        commandsforBoolean.add(attack);

        NEnemyUnitsofType nAllyOfTypeBoolean = new NEnemyUnitsofType(commandsforBoolean);
        nAllyOfTypeBoolean.addParameter(TypeConcrete.getTypeUnits());
        nAllyOfTypeBoolean.addParameter(new QuantityParam(3));
        commands.add(nAllyOfTypeBoolean);

        return commands;
    }

    //Done
    public List<ICommand> ChromosomesBag2(UnitTypeTable utt) {

        this.utt = utt;
        List<ICommand> commands = new ArrayList<>();
        //BASIC ACTIONS**********************************************************************
        //train action
        TrainBasic train = new TrainBasic();
        train.addParameter(TypeConcrete.getTypeBase()); //add unit construct type
        train.addParameter(TypeConcrete.getTypeWorker()); //add unit Type
        train.addParameter(new QuantityParam(20)); //add qtd unit
        PriorityPositionParam pos = new PriorityPositionParam();
        pos.addPosition(EnumPositionType.Up);
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

        return commands;
    }

    //Done
    public List<ICommand> ChromosomesBag3(UnitTypeTable utt) {

        this.utt = utt;
        List<ICommand> commands = new ArrayList<>();
        //BASIC ACTIONS**********************************************************************
        //build action
        BuildBasic build = new BuildBasic();
        build.addParameter(TypeConcrete.getTypeBarracks()); //add unit construct type
        build.addParameter(new QuantityParam(1)); //add qtd unit
        commands.add(build);

        //train action
        TrainBasic train = new TrainBasic();
        train.addParameter(TypeConcrete.getTypeBase()); //add unit construct type
        train.addParameter(TypeConcrete.getTypeWorker()); //add unit Type
        train.addParameter(new QuantityParam(20)); //add qtd unit
        PriorityPositionParam pos = new PriorityPositionParam();
        //pos.addPosition(EnumPositionType.Up);
        //pos.addPosition(EnumPositionType.Left);
        //pos.addPosition(EnumPositionType.Right);
        //pos.addPosition(EnumPositionType.Down);
        train.addParameter(pos);
        commands.add(train);

        //BOOLEAN  If there is an enemy in allyRange 
        MoveToUnitBasic moveToUnit = new MoveToUnitBasic();
        moveToUnit.addParameter(TypeConcrete.getTypeUnits()); //add unit type
        PlayerTargetParam pt=new PlayerTargetParam();
        pt.addPlayer(EnumPlayerTarget.Enemy);
        moveToUnit.addParameter(pt);
        moveToUnit.addParameter(new LessHealthyEnemy()); //add behavior
        commandsforBoolean = new ArrayList<>();
        commandsforBoolean.add(moveToUnit);

        AllyRange allyRangeBoolean = new AllyRange(commandsforBoolean);
        allyRangeBoolean.addParameter(TypeConcrete.getTypeUnits());
        commands.add(allyRangeBoolean);

        return commands;
    }

    //Done
    public List<ICommand> ChromosomesBag4(UnitTypeTable utt) {

        this.utt = utt;
        List<ICommand> commands = new ArrayList<>();
        //BASIC ACTIONS**********************************************************************
        //build action
        BuildBasic build = new BuildBasic();
        build.addParameter(TypeConcrete.getTypeBarracks()); //add unit construct type
        build.addParameter(new QuantityParam(1)); //add qtd unit
        commands.add(build);

        //train action
        TrainBasic train = new TrainBasic();
        train.addParameter(TypeConcrete.getTypeBase()); //add unit construct type
        train.addParameter(TypeConcrete.getTypeWorker()); //add unit Type
        train.addParameter(new QuantityParam(20)); //add qtd unit
        PriorityPositionParam pos = new PriorityPositionParam();
        pos.addPosition(EnumPositionType.Up);
        //pos.addPosition(EnumPositionType.Left);
        pos.addPosition(EnumPositionType.Right);
        //pos.addPosition(EnumPositionType.Down);
        train.addParameter(pos);
        commands.add(train);

        //harverst action
        HarvestBasic harverst = new HarvestBasic();
        harverst.addParameter(TypeConcrete.getTypeWorker()); //add unit type
        harverst.addParameter(new QuantityParam(1)); //add qtd unit
        commands.add(harverst);

        //Move To coordinates
        MoveToCoordinatesBasic moveToCoordinates = new MoveToCoordinatesBasic();
        moveToCoordinates.addParameter(new CoordinatesParam(14, 5)); //add unit type
        moveToCoordinates.addParameter(TypeConcrete.getTypeUnits());
        commands.add(moveToCoordinates);

        //BOOLEAN  If there are X ally units of type T 
        moveToCoordinates = new MoveToCoordinatesBasic();
        moveToCoordinates.addParameter(new CoordinatesParam(14, 5)); //add unit type
        moveToCoordinates.addParameter(TypeConcrete.getTypeUnits());
        commands.add(moveToCoordinates);
        commandsforBoolean = new ArrayList<>();
        commandsforBoolean.add(moveToCoordinates);

        return commands;
    }

    //Done
    public List<ICommand> ChromosomesBag5(UnitTypeTable utt) {

        this.utt = utt;
        List<ICommand> commands = new ArrayList<>();
        //BASIC ACTIONS**********************************************************************
        //build action
        BuildBasic build = new BuildBasic();
        build.addParameter(TypeConcrete.getTypeBarracks()); //add unit construct type
        build.addParameter(new QuantityParam(3)); //add qtd unit
        commands.add(build);

        //train action
        TrainBasic train = new TrainBasic();
        train.addParameter(TypeConcrete.getTypeBase()); //add unit construct type
        train.addParameter(TypeConcrete.getTypeWorker()); //add unit Type
        train.addParameter(new QuantityParam(100)); //add qtd unit
        PriorityPositionParam pos = new PriorityPositionParam();
        //pos.addPosition(EnumPositionType.Up);
        //pos.addPosition(EnumPositionType.Left);
        //pos.addPosition(EnumPositionType.Right);
        pos.addPosition(EnumPositionType.Down);
        train.addParameter(pos);
        commands.add(train);

        //harverst action
        HarvestBasic harverst = new HarvestBasic();
        harverst.addParameter(TypeConcrete.getTypeWorker()); //add unit type
        harverst.addParameter(new QuantityParam(10)); //add qtd unit
        commands.add(harverst);

        //BOOLEANS *********************************************************************************************************************************
        //BOOLEAN  If ally Is In Distance X From Enemy 
        AttackBasic attack = new AttackBasic();
        attack.addParameter(TypeConcrete.getTypeUnits()); //add unit type
        PlayerTargetParam pt=new PlayerTargetParam();
        pt.addPlayer(EnumPlayerTarget.Enemy);
        attack.addParameter(pt);
        attack.addParameter(new ClosestEnemy()); //add behavior   
        commandsforBoolean = new ArrayList<>();
        commandsforBoolean.add(attack);

        DistanceFromEnemy distanceFromEnemyBoolean = new DistanceFromEnemy(commandsforBoolean);
        distanceFromEnemyBoolean.addParameter(TypeConcrete.getTypeUnits());
        distanceFromEnemyBoolean.addParameter(new DistanceParam(6));
        commands.add(distanceFromEnemyBoolean);

        return commands;
    }

    //Done
    public List<ICommand> ChromosomesBag6(UnitTypeTable utt) {

        this.utt = utt;
        List<ICommand> commands = new ArrayList<>();
        //BASIC ACTIONS**********************************************************************
        //train action
        TrainBasic train = new TrainBasic();
        train.addParameter(TypeConcrete.getTypeBase()); //add unit construct type
        train.addParameter(TypeConcrete.getTypeWorker()); //add unit Type
        train.addParameter(new QuantityParam(20)); //add qtd unit
        PriorityPositionParam pos = new PriorityPositionParam();
        pos.addPosition(EnumPositionType.Up);
        pos.addPosition(EnumPositionType.Left);
        //pos.addPosition(EnumPositionType.Right);
        //pos.addPosition(EnumPositionType.Down);
        train.addParameter(pos);
        commands.add(train);

        //harverst action
        HarvestBasic harverst = new HarvestBasic();
        harverst.addParameter(TypeConcrete.getTypeWorker()); //add unit type
        harverst.addParameter(new QuantityParam(1)); //add qtd unit
        commands.add(harverst);

        //BOOLEANS *********************************************************************************************************************************
        //BOOLEAN  If there is an ally in enemyRange 
        AttackBasic attack = new AttackBasic();
        attack.addParameter(TypeConcrete.getTypeUnits()); //add unit type
        PlayerTargetParam pt=new PlayerTargetParam();
        pt.addPlayer(EnumPlayerTarget.Enemy);
        attack.addParameter(pt);
        attack.addParameter(new ClosestEnemy()); //add behavior   
        commandsforBoolean = new ArrayList<>();
        commandsforBoolean.add(attack);

        EnemyRange enemyRangeBoolean = new EnemyRange(commandsforBoolean);
        enemyRangeBoolean.addParameter(TypeConcrete.getTypeUnits());
        commands.add(enemyRangeBoolean);

        //BOOLEAN  If there is an enemy in allyRange 
        attack = new AttackBasic();
        attack.addParameter(TypeConcrete.getTypeUnits()); //add unit type
        pt=new PlayerTargetParam();
        pt.addPlayer(EnumPlayerTarget.Enemy);
        attack.addParameter(pt);
        attack.addParameter(new ClosestEnemy()); //add behavior   
        commandsforBoolean = new ArrayList<>();
        commandsforBoolean.add(attack);

        AllyRange allyRangeBoolean = new AllyRange(commandsforBoolean);
        allyRangeBoolean.addParameter(TypeConcrete.getTypeUnits());
        commands.add(allyRangeBoolean);

        return commands;
    }

    //Done
    public List<ICommand> ChromosomesBag7(UnitTypeTable utt) {

        this.utt = utt;
        List<ICommand> commands = new ArrayList<>();
        //BASIC ACTIONS**********************************************************************
        //train action
        TrainBasic train = new TrainBasic();
        train.addParameter(TypeConcrete.getTypeBase()); //add unit construct type
        train.addParameter(TypeConcrete.getTypeWorker()); //add unit Type
        train.addParameter(new QuantityParam(20)); //add qtd unit
        PriorityPositionParam pos = new PriorityPositionParam();
        pos.addPosition(EnumPositionType.Up);
        pos.addPosition(EnumPositionType.Left);
        //pos.addPosition(EnumPositionType.Right);
        //pos.addPosition(EnumPositionType.Down);
        train.addParameter(pos);
        commands.add(train);

        //harverst action
        HarvestBasic harverst = new HarvestBasic();
        harverst.addParameter(TypeConcrete.getTypeWorker()); //add unit type
        harverst.addParameter(new QuantityParam(1)); //add qtd unit
        commands.add(harverst);

        //BOOLEANS *********************************************************************************************************************************
        //BOOLEAN  If ally Is In Distance X From Enemy 
        harverst = new HarvestBasic();
        harverst.addParameter(TypeConcrete.getTypeWorker()); //add unit type
        harverst.addParameter(new QuantityParam(1)); //add qtd unit 
        commandsforBoolean = new ArrayList<>();
        commandsforBoolean.add(harverst);

        DistanceFromEnemy distanceFromEnemyBoolean = new DistanceFromEnemy(commandsforBoolean);
        distanceFromEnemyBoolean.addParameter(TypeConcrete.getTypeUnits());
        distanceFromEnemyBoolean.addParameter(new DistanceParam(6));
        commands.add(distanceFromEnemyBoolean);

        //BOOLEAN  If there are X ally units of type T 
        MoveToUnitBasic moveToUnit = new MoveToUnitBasic();
        moveToUnit.addParameter(TypeConcrete.getTypeUnits()); //add unit type
        PlayerTargetParam pt=new PlayerTargetParam();
        pt.addPlayer(EnumPlayerTarget.Enemy);
        moveToUnit.addParameter(pt);
        moveToUnit.addParameter(new LessHealthyEnemy()); //add behavior
        commandsforBoolean = new ArrayList<>();
        commandsforBoolean.add(moveToUnit);

        NAllyUnitsofType nAllyOfTypeBoolean = new NAllyUnitsofType(commandsforBoolean);
        nAllyOfTypeBoolean.addParameter(TypeConcrete.getTypeUnits());
        nAllyOfTypeBoolean.addParameter(new QuantityParam(3));
        commands.add(nAllyOfTypeBoolean);

        return commands;
    }

    //Done
    public List<ICommand> ChromosomesBag8(UnitTypeTable utt) {

        this.utt = utt;
        List<ICommand> commands = new ArrayList<>();
        //BASIC ACTIONS**********************************************************************
        //build action
        BuildBasic build = new BuildBasic();
        build.addParameter(TypeConcrete.getTypeBarracks()); //add unit construct type
        build.addParameter(new QuantityParam(2)); //add qtd unit
        commands.add(build);

        //train action
        TrainBasic train = new TrainBasic();
        train.addParameter(TypeConcrete.getTypeBase()); //add unit construct type
        train.addParameter(TypeConcrete.getTypeWorker()); //add unit Type
        train.addParameter(new QuantityParam(20)); //add qtd unit
        PriorityPositionParam pos = new PriorityPositionParam();
        pos.addPosition(EnumPositionType.Up);
        pos.addPosition(EnumPositionType.Left);
        //pos.addPosition(EnumPositionType.Right);
        //pos.addPosition(EnumPositionType.Down);
        train.addParameter(pos);
        commands.add(train);

        //harverst action
        HarvestBasic harverst = new HarvestBasic();
        harverst.addParameter(TypeConcrete.getTypeWorker()); //add unit type
        harverst.addParameter(new QuantityParam(1)); //add qtd unit
        commands.add(harverst);

        //BOOLEANS *********************************************************************************************************************************
        //BOOLEAN  If there are X ally units of type T 
        MoveToUnitBasic moveToUnit = new MoveToUnitBasic();
        moveToUnit.addParameter(TypeConcrete.getTypeUnits()); //add unit type
        PlayerTargetParam pt=new PlayerTargetParam();
        pt.addPlayer(EnumPlayerTarget.Enemy);
        moveToUnit.addParameter(pt);
        moveToUnit.addParameter(new LessHealthyEnemy()); //add behavior
        commandsforBoolean = new ArrayList<>();
        commandsforBoolean.add(moveToUnit);

        NAllyUnitsofType nAllyOfTypeBoolean = new NAllyUnitsofType(commandsforBoolean);
        nAllyOfTypeBoolean.addParameter(TypeConcrete.getTypeUnits());
        nAllyOfTypeBoolean.addParameter(new QuantityParam(3));
        commands.add(nAllyOfTypeBoolean);

        //BOOLEAN  If there are X ally units of type T attacking
        moveToUnit = new MoveToUnitBasic();
        moveToUnit.addParameter(TypeConcrete.getTypeUnits()); //add unit type
        pt=new PlayerTargetParam();
        pt.addPlayer(EnumPlayerTarget.Enemy);
        moveToUnit.addParameter(pt);
        moveToUnit.addParameter(new LessHealthyEnemy()); //add behavior
        commandsforBoolean = new ArrayList<>();
        commandsforBoolean.add(moveToUnit);

        NAllyUnitsAttacking nAllyAttackingBoolean = new NAllyUnitsAttacking(commandsforBoolean);
        nAllyAttackingBoolean.addParameter(TypeConcrete.getTypeUnits());
        nAllyAttackingBoolean.addParameter(new QuantityParam(3));
        commands.add(nAllyAttackingBoolean);

        //attack action
        AttackBasic attack = new AttackBasic();
        attack.addParameter(TypeConcrete.getTypeUnits()); //add unit type
        pt=new PlayerTargetParam();
        pt.addPlayer(EnumPlayerTarget.Enemy);
        attack.addParameter(pt);
        attack.addParameter(new ClosestEnemy()); //add behavior
        commands.add(attack);

        //Move to unit action
        moveToUnit = new MoveToUnitBasic();
        moveToUnit.addParameter(TypeConcrete.getTypeUnits()); //add unit type
        pt=new PlayerTargetParam();
        pt.addPlayer(EnumPlayerTarget.Enemy);
        moveToUnit.addParameter(pt);
        moveToUnit.addParameter(new LessHealthyEnemy()); //add behavior
        commands.add(moveToUnit);

        //Move To coordinates
        MoveToCoordinatesBasic moveToCoordinates = new MoveToCoordinatesBasic();
        moveToCoordinates.addParameter(new CoordinatesParam(14, 5)); //add unit type
        moveToCoordinates.addParameter(TypeConcrete.getTypeUnits());
        commands.add(moveToCoordinates);

        return commands;
    }

    //done
    public List<ICommand> ChromosomesBag9(UnitTypeTable utt) {

        this.utt = utt;
        List<ICommand> commands = new ArrayList<>();
        //Move To coordinates
        MoveToCoordinatesBasic moveToCoordinates = new MoveToCoordinatesBasic();
        moveToCoordinates.addParameter(new CoordinatesParam(14, 5)); //add unit type
        moveToCoordinates.addParameter(TypeConcrete.getTypeUnits());
        commands.add(moveToCoordinates);

        return commands;
    }

    public List<ICommand> ChromosomesBag10(UnitTypeTable utt) {

        this.utt = utt;
        List<ICommand> commands = new ArrayList<>();
        //build action
        BuildBasic build = new BuildBasic();
        build.addParameter(TypeConcrete.getTypeBarracks()); //add unit construct type
        build.addParameter(new QuantityParam(1)); //add qtd unit
        commands.add(build);

        //train action
        TrainBasic train = new TrainBasic();
        train.addParameter(TypeConcrete.getTypeBarracks()); //add unit construct type
        train.addParameter(TypeConcrete.getTypeLight()); //add unit Type
        //train.addParameter(TypeConcrete.getTypeWorker()); //add unit Type
        train.addParameter(new QuantityParam(20)); //add qtd unit
        PriorityPositionParam pos = new PriorityPositionParam();
        pos.addPosition(EnumPositionType.Up);
        pos.addPosition(EnumPositionType.Left);
        //pos.addPosition(EnumPositionType.Right);
        //pos.addPosition(EnumPositionType.Down);
        train.addParameter(pos);
        commands.add(train);
        //harverst action
        HarvestBasic harverst = new HarvestBasic();
        harverst.addParameter(TypeConcrete.getTypeWorker()); //add unit type
        harverst.addParameter(new QuantityParam(2)); //add qtd unit
        commands.add(harverst);
        //attack action
        AttackBasic attack = new AttackBasic();
        attack.addParameter(TypeConcrete.getTypeUnits()); //add unit type
        PlayerTargetParam pt=new PlayerTargetParam();
        pt.addPlayer(EnumPlayerTarget.Enemy);
        attack.addParameter(pt);
        attack.addParameter(new StrongestEnemy()); //add behavior
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
        return commands;
    }

    public List<ICommand> ChromosomesBag11(UnitTypeTable utt) {
        this.utt = utt;
        List<ICommand> commands = new ArrayList<>();
        //BASIC ACTIONS**********************************************************************
        //build action
        BuildBasic build = new BuildBasic();
        build.addParameter(TypeConcrete.getTypeBarracks()); //add unit construct type
        build.addParameter(new QuantityParam(1)); //add qtd unit
        commands.add(build);

        //train action
        TrainBasic train = new TrainBasic();
        train.addParameter(TypeConcrete.getTypeBarracks()); //add unit construct type
        train.addParameter(TypeConcrete.getTypeLight()); //add unit Type
        //train.addParameter(TypeConcrete.getTypeWorker()); //add unit Type
        train.addParameter(new QuantityParam(20)); //add qtd unit
        PriorityPositionParam pos = new PriorityPositionParam();
        pos.addPosition(EnumPositionType.Up);
        pos.addPosition(EnumPositionType.Left);
        //pos.addPosition(EnumPositionType.Right);
        //pos.addPosition(EnumPositionType.Down);
        train.addParameter(pos);
        commands.add(train);

        //train action
        train = new TrainBasic();
        train.addParameter(TypeConcrete.getTypeBase()); //add unit construct type
        train.addParameter(TypeConcrete.getTypeWorker()); //add unit Type
        train.addParameter(new QuantityParam(100)); //add qtd unit
        pos = new PriorityPositionParam();
        pos.addPosition(EnumPositionType.Up);
        pos.addPosition(EnumPositionType.Left);
        //pos.addPosition(EnumPositionType.Right);
        //pos.addPosition(EnumPositionType.Down);
        train.addParameter(pos);
        commands.add(train);

        //harverst action
        HarvestBasic harverst = new HarvestBasic();
        harverst.addParameter(TypeConcrete.getTypeWorker()); //add unit type
        harverst.addParameter(new QuantityParam(1)); //add qtd unit
        commands.add(harverst);

        AttackBasic attack = new AttackBasic();
        attack.addParameter(TypeConcrete.getTypeUnits()); //add unit type
        PlayerTargetParam pt=new PlayerTargetParam();
        pt.addPlayer(EnumPlayerTarget.Enemy);
        attack.addParameter(pt);
        attack.addParameter(new ClosestEnemy()); //add behavior   
        commandsforBoolean = new ArrayList<>();
        commandsforBoolean.add(attack);

        NEnemyUnitsofType nAllyOfTypeBoolean = new NEnemyUnitsofType(commandsforBoolean);
        nAllyOfTypeBoolean.addParameter(TypeConcrete.getTypeUnits());
        nAllyOfTypeBoolean.addParameter(new QuantityParam(3));
        commands.add(nAllyOfTypeBoolean);

        return commands;
    }

    //Done
    public List<ICommand> ChromosomesBag12(UnitTypeTable utt) {

        this.utt = utt;
        List<ICommand> commands = new ArrayList<>();
        //BASIC ACTIONS**********************************************************************
        //train action
        TrainBasic train = new TrainBasic();
        train.addParameter(TypeConcrete.getTypeBarracks()); //add unit construct type
        train.addParameter(TypeConcrete.getTypeRanged()); //add unit Type
        //train.addParameter(TypeConcrete.getTypeWorker()); //add unit Type
        train.addParameter(new QuantityParam(20)); //add qtd unit
        PriorityPositionParam pos = new PriorityPositionParam();
        pos.addPosition(EnumPositionType.Up);
        pos.addPosition(EnumPositionType.Left);
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

        return commands;
    }

    //Done
    public List<ICommand> ChromosomesBag13(UnitTypeTable utt) {

        this.utt = utt;
        List<ICommand> commands = new ArrayList<>();
        //BASIC ACTIONS**********************************************************************
        //build action
        BuildBasic build = new BuildBasic();
        build.addParameter(TypeConcrete.getTypeBarracks()); //add unit construct type
        build.addParameter(new QuantityParam(1)); //add qtd unit
        commands.add(build);

        //train action
        TrainBasic train = new TrainBasic();
        train.addParameter(TypeConcrete.getTypeBarracks()); //add unit construct type
        train.addParameter(TypeConcrete.getTypeHeavy()); //add unit Type
        //train.addParameter(TypeConcrete.getTypeWorker()); //add unit Type
        train.addParameter(new QuantityParam(20)); //add qtd unit
        PriorityPositionParam pos = new PriorityPositionParam();
        pos.addPosition(EnumPositionType.Up);
        pos.addPosition(EnumPositionType.Left);
        //pos.addPosition(EnumPositionType.Right);
        //pos.addPosition(EnumPositionType.Down);
        train.addParameter(pos);
        commands.add(train);

        //train action
        train = new TrainBasic();
        train.addParameter(TypeConcrete.getTypeBase()); //add unit construct type
        train.addParameter(TypeConcrete.getTypeWorker()); //add unit Type
        train.addParameter(new QuantityParam(20)); //add qtd unit
        pos = new PriorityPositionParam();
        //pos.addPosition(EnumPositionType.Up);
        //pos.addPosition(EnumPositionType.Left);
        //pos.addPosition(EnumPositionType.Right);
        //pos.addPosition(EnumPositionType.Down);
        train.addParameter(pos);
        commands.add(train);

        //BOOLEAN  If there is an enemy in allyRange 
        MoveToUnitBasic moveToUnit = new MoveToUnitBasic();
        moveToUnit.addParameter(TypeConcrete.getTypeUnits()); //add unit type
        PlayerTargetParam pt=new PlayerTargetParam();
        pt.addPlayer(EnumPlayerTarget.Enemy);
        moveToUnit.addParameter(pt);
        moveToUnit.addParameter(new LessHealthyEnemy()); //add behavior
        commandsforBoolean = new ArrayList<>();
        commandsforBoolean.add(moveToUnit);

        AllyRange allyRangeBoolean = new AllyRange(commandsforBoolean);
        allyRangeBoolean.addParameter(TypeConcrete.getTypeUnits());
        commands.add(allyRangeBoolean);

        return commands;
    }

    //Done
    public List<ICommand> ChromosomesBag14(UnitTypeTable utt) {

        this.utt = utt;
        List<ICommand> commands = new ArrayList<>();
        //BASIC ACTIONS**********************************************************************
        //build action
        BuildBasic build = new BuildBasic();
        build.addParameter(TypeConcrete.getTypeBarracks()); //add unit construct type
        build.addParameter(new QuantityParam(1)); //add qtd unit
        commands.add(build);

        //train action
        //train action
        TrainBasic train = new TrainBasic();
        train.addParameter(TypeConcrete.getTypeBarracks()); //add unit construct type
        train.addParameter(TypeConcrete.getTypeLight()); //add unit Type
        //train.addParameter(TypeConcrete.getTypeWorker()); //add unit Type
        train.addParameter(new QuantityParam(20)); //add qtd unit
        PriorityPositionParam pos = new PriorityPositionParam();
        pos.addPosition(EnumPositionType.Up);
        pos.addPosition(EnumPositionType.Left);
        //pos.addPosition(EnumPositionType.Right);
        //pos.addPosition(EnumPositionType.Down);
        train.addParameter(pos);
        commands.add(train);

        //harverst action
        HarvestBasic harverst = new HarvestBasic();
        harverst.addParameter(TypeConcrete.getTypeWorker()); //add unit type
        harverst.addParameter(new QuantityParam(1)); //add qtd unit
        commands.add(harverst);

        //Move To coordinates
        MoveToCoordinatesBasic moveToCoordinates = new MoveToCoordinatesBasic();
        moveToCoordinates.addParameter(new CoordinatesParam(14, 5)); //add unit type
        moveToCoordinates.addParameter(TypeConcrete.getTypeUnits());
        commands.add(moveToCoordinates);

        //BOOLEAN  If there are X ally units of type T 
        moveToCoordinates = new MoveToCoordinatesBasic();
        moveToCoordinates.addParameter(new CoordinatesParam(14, 5)); //add unit type
        moveToCoordinates.addParameter(TypeConcrete.getTypeUnits());
        commands.add(moveToCoordinates);
        commandsforBoolean = new ArrayList<>();
        commandsforBoolean.add(moveToCoordinates);

        return commands;
    }

    //Done
    public List<ICommand> ChromosomesBag15(UnitTypeTable utt) {

        this.utt = utt;
        List<ICommand> commands = new ArrayList<>();
        //BASIC ACTIONS**********************************************************************
        //build action
        BuildBasic build = new BuildBasic();
        build.addParameter(TypeConcrete.getTypeBarracks()); //add unit construct type
        build.addParameter(new QuantityParam(3)); //add qtd unit
        commands.add(build);

        //train action
        TrainBasic train = new TrainBasic();
        train.addParameter(TypeConcrete.getTypeBase()); //add unit construct type
        train.addParameter(TypeConcrete.getTypeWorker()); //add unit Type
        train.addParameter(new QuantityParam(100)); //add qtd unit
        PriorityPositionParam pos = new PriorityPositionParam();
        //pos.addPosition(EnumPositionType.Up);
        //pos.addPosition(EnumPositionType.Left);
        //pos.addPosition(EnumPositionType.Right);
        pos.addPosition(EnumPositionType.Down);
        train.addParameter(pos);
        commands.add(train);

        //train action
        train = new TrainBasic();
        train.addParameter(TypeConcrete.getTypeBarracks()); //add unit construct type
        train.addParameter(TypeConcrete.getTypeLight()); //add unit Type
        //train.addParameter(TypeConcrete.getTypeWorker()); //add unit Type
        train.addParameter(new QuantityParam(20)); //add qtd unit
        pos = new PriorityPositionParam();
        pos.addPosition(EnumPositionType.Up);
        pos.addPosition(EnumPositionType.Left);
        //pos.addPosition(EnumPositionType.Right);
        //pos.addPosition(EnumPositionType.Down);
        train.addParameter(pos);
        commands.add(train);

        //harverst action
        HarvestBasic harverst = new HarvestBasic();
        harverst.addParameter(TypeConcrete.getTypeWorker()); //add unit type
        harverst.addParameter(new QuantityParam(10)); //add qtd unit
        commands.add(harverst);

        //BOOLEANS *********************************************************************************************************************************
        //BOOLEAN  If ally Is In Distance X From Enemy 
        AttackBasic attack = new AttackBasic();
        attack.addParameter(TypeConcrete.getTypeUnits()); //add unit type
        PlayerTargetParam pt=new PlayerTargetParam();
        pt.addPlayer(EnumPlayerTarget.Enemy);
        attack.addParameter(pt);
        attack.addParameter(new ClosestEnemy()); //add behavior   
        commandsforBoolean = new ArrayList<>();
        commandsforBoolean.add(attack);

        DistanceFromEnemy distanceFromEnemyBoolean = new DistanceFromEnemy(commandsforBoolean);
        distanceFromEnemyBoolean.addParameter(TypeConcrete.getTypeUnits());
        distanceFromEnemyBoolean.addParameter(new DistanceParam(6));
        commands.add(distanceFromEnemyBoolean);

        return commands;
    }

    //Done
    public List<ICommand> ChromosomesBag16(UnitTypeTable utt) {

        this.utt = utt;
        List<ICommand> commands = new ArrayList<>();
        //BASIC ACTIONS**********************************************************************
        //train action
        TrainBasic train = new TrainBasic();
        train.addParameter(TypeConcrete.getTypeBarracks()); //add unit construct type
        train.addParameter(TypeConcrete.getTypeLight()); //add unit Type
        //train.addParameter(TypeConcrete.getTypeWorker()); //add unit Type
        train.addParameter(new QuantityParam(20)); //add qtd unit
        PriorityPositionParam pos = new PriorityPositionParam();
        pos.addPosition(EnumPositionType.Up);
        pos.addPosition(EnumPositionType.Left);
        //pos.addPosition(EnumPositionType.Right);
        //pos.addPosition(EnumPositionType.Down);
        train.addParameter(pos);
        commands.add(train);

        //harverst action
        HarvestBasic harverst = new HarvestBasic();
        harverst.addParameter(TypeConcrete.getTypeWorker()); //add unit type
        harverst.addParameter(new QuantityParam(1)); //add qtd unit
        commands.add(harverst);

        //BOOLEANS *********************************************************************************************************************************
        //BOOLEAN  If there is an ally in enemyRange 
        AttackBasic attack = new AttackBasic();
        attack.addParameter(TypeConcrete.getTypeUnits()); //add unit type
        PlayerTargetParam pt=new PlayerTargetParam();
        pt.addPlayer(EnumPlayerTarget.Enemy);
        attack.addParameter(pt);
        attack.addParameter(new ClosestEnemy()); //add behavior   
        commandsforBoolean = new ArrayList<>();
        commandsforBoolean.add(attack);

        EnemyRange enemyRangeBoolean = new EnemyRange(commandsforBoolean);
        enemyRangeBoolean.addParameter(TypeConcrete.getTypeUnits());
        commands.add(enemyRangeBoolean);

        //BOOLEAN  If there is an enemy in allyRange 
        attack = new AttackBasic();
        attack.addParameter(TypeConcrete.getTypeUnits()); //add unit type
        pt=new PlayerTargetParam();
        pt.addPlayer(EnumPlayerTarget.Enemy);
        attack.addParameter(pt);
        attack.addParameter(new ClosestEnemy()); //add behavior   
        commandsforBoolean = new ArrayList<>();
        commandsforBoolean.add(attack);

        AllyRange allyRangeBoolean = new AllyRange(commandsforBoolean);
        allyRangeBoolean.addParameter(TypeConcrete.getTypeUnits());
        commands.add(allyRangeBoolean);

        return commands;
    }

    //Done
    public List<ICommand> ChromosomesBag17(UnitTypeTable utt) {

        this.utt = utt;
        List<ICommand> commands = new ArrayList<>();
        //BASIC ACTIONS**********************************************************************
        //train action
        TrainBasic train = new TrainBasic();
        train.addParameter(TypeConcrete.getTypeBarracks()); //add unit construct type
        train.addParameter(TypeConcrete.getTypeLight()); //add unit Type
        //train.addParameter(TypeConcrete.getTypeWorker()); //add unit Type
        train.addParameter(new QuantityParam(20)); //add qtd unit
        PriorityPositionParam pos = new PriorityPositionParam();
        pos.addPosition(EnumPositionType.Up);
        pos.addPosition(EnumPositionType.Left);
        //pos.addPosition(EnumPositionType.Right);
        //pos.addPosition(EnumPositionType.Down);
        train.addParameter(pos);
        commands.add(train);

        //build action
        BuildBasic build = new BuildBasic();
        build.addParameter(TypeConcrete.getTypeBarracks()); //add unit construct type
        build.addParameter(new QuantityParam(3)); //add qtd unit
        commands.add(build);

        //train action
        train = new TrainBasic();
        train.addParameter(TypeConcrete.getTypeBase()); //add unit construct type
        train.addParameter(TypeConcrete.getTypeWorker()); //add unit Type
        train.addParameter(new QuantityParam(20)); //add qtd unit
        pos = new PriorityPositionParam();
        pos.addPosition(EnumPositionType.Up);
        pos.addPosition(EnumPositionType.Left);
        //pos.addPosition(EnumPositionType.Right);
        //pos.addPosition(EnumPositionType.Down);
        train.addParameter(pos);
        commands.add(train);

        //harverst action
        HarvestBasic harverst = new HarvestBasic();
        harverst.addParameter(TypeConcrete.getTypeWorker()); //add unit type
        harverst.addParameter(new QuantityParam(1)); //add qtd unit
        commands.add(harverst);

        //BOOLEANS *********************************************************************************************************************************
        //BOOLEAN  If ally Is In Distance X From Enemy 
        harverst = new HarvestBasic();
        harverst.addParameter(TypeConcrete.getTypeWorker()); //add unit type
        harverst.addParameter(new QuantityParam(1)); //add qtd unit 
        commandsforBoolean = new ArrayList<>();
        commandsforBoolean.add(harverst);

        DistanceFromEnemy distanceFromEnemyBoolean = new DistanceFromEnemy(commandsforBoolean);
        distanceFromEnemyBoolean.addParameter(TypeConcrete.getTypeUnits());
        distanceFromEnemyBoolean.addParameter(new DistanceParam(6));
        commands.add(distanceFromEnemyBoolean);

        //BOOLEAN  If there are X ally units of type T 
        MoveToUnitBasic moveToUnit = new MoveToUnitBasic();
        moveToUnit.addParameter(TypeConcrete.getTypeUnits()); //add unit type
        PlayerTargetParam pt=new PlayerTargetParam();
        pt.addPlayer(EnumPlayerTarget.Enemy);
        moveToUnit.addParameter(pt);
        moveToUnit.addParameter(new LessHealthyEnemy()); //add behavior
        commandsforBoolean = new ArrayList<>();
        commandsforBoolean.add(moveToUnit);

        NAllyUnitsofType nAllyOfTypeBoolean = new NAllyUnitsofType(commandsforBoolean);
        nAllyOfTypeBoolean.addParameter(TypeConcrete.getTypeUnits());
        nAllyOfTypeBoolean.addParameter(new QuantityParam(3));
        commands.add(nAllyOfTypeBoolean);

        return commands;
    }

    //Done
    public List<ICommand> ChromosomesBag18(UnitTypeTable utt) {

        this.utt = utt;
        List<ICommand> commands = new ArrayList<>();
        //BASIC ACTIONS**********************************************************************
        //build action
        BuildBasic build = new BuildBasic();
        build.addParameter(TypeConcrete.getTypeBarracks()); //add unit construct type
        build.addParameter(new QuantityParam(2)); //add qtd unit
        commands.add(build);

        //train action
        TrainBasic train = new TrainBasic();
        train.addParameter(TypeConcrete.getTypeBarracks()); //add unit construct type
        train.addParameter(TypeConcrete.getTypeLight()); //add unit Type
        //train.addParameter(TypeConcrete.getTypeWorker()); //add unit Type
        train.addParameter(new QuantityParam(20)); //add qtd unit
        PriorityPositionParam pos = new PriorityPositionParam();
        pos.addPosition(EnumPositionType.Up);
        pos.addPosition(EnumPositionType.Left);
        //pos.addPosition(EnumPositionType.Right);
        //pos.addPosition(EnumPositionType.Down);
        train.addParameter(pos);
        commands.add(train);

        //train action
        train = new TrainBasic();
        train.addParameter(TypeConcrete.getTypeBase()); //add unit construct type
        train.addParameter(TypeConcrete.getTypeWorker()); //add unit Type
        train.addParameter(new QuantityParam(20)); //add qtd unit
        pos = new PriorityPositionParam();
        pos.addPosition(EnumPositionType.Up);
        pos.addPosition(EnumPositionType.Left);
        //pos.addPosition(EnumPositionType.Right);
        //pos.addPosition(EnumPositionType.Down);
        train.addParameter(pos);
        commands.add(train);

        //harverst action
        HarvestBasic harverst = new HarvestBasic();
        harverst.addParameter(TypeConcrete.getTypeWorker()); //add unit type
        harverst.addParameter(new QuantityParam(1)); //add qtd unit
        commands.add(harverst);

        //BOOLEANS *********************************************************************************************************************************
        //BOOLEAN  If there are X ally units of type T 
        MoveToUnitBasic moveToUnit = new MoveToUnitBasic();
        moveToUnit.addParameter(TypeConcrete.getTypeUnits()); //add unit type
        PlayerTargetParam pt=new PlayerTargetParam();
        pt.addPlayer(EnumPlayerTarget.Enemy);
        moveToUnit.addParameter(pt);
        moveToUnit.addParameter(new LessHealthyEnemy()); //add behavior
        commandsforBoolean = new ArrayList<>();
        commandsforBoolean.add(moveToUnit);

        NAllyUnitsofType nAllyOfTypeBoolean = new NAllyUnitsofType(commandsforBoolean);
        nAllyOfTypeBoolean.addParameter(TypeConcrete.getTypeUnits());
        nAllyOfTypeBoolean.addParameter(new QuantityParam(3));
        commands.add(nAllyOfTypeBoolean);

        //BOOLEAN  If there are X ally units of type T attacking
        moveToUnit = new MoveToUnitBasic();
        moveToUnit.addParameter(TypeConcrete.getTypeUnits()); //add unit type
        pt=new PlayerTargetParam();
        pt.addPlayer(EnumPlayerTarget.Enemy);
        moveToUnit.addParameter(pt);
        moveToUnit.addParameter(new LessHealthyEnemy()); //add behavior
        commandsforBoolean = new ArrayList<>();
        commandsforBoolean.add(moveToUnit);

        NAllyUnitsAttacking nAllyAttackingBoolean = new NAllyUnitsAttacking(commandsforBoolean);
        nAllyAttackingBoolean.addParameter(TypeConcrete.getTypeUnits());
        nAllyAttackingBoolean.addParameter(new QuantityParam(3));
        commands.add(nAllyAttackingBoolean);

        //attack action
        AttackBasic attack = new AttackBasic();
        attack.addParameter(TypeConcrete.getTypeUnits()); //add unit type
        pt=new PlayerTargetParam();
        pt.addPlayer(EnumPlayerTarget.Enemy);
        attack.addParameter(pt);
        attack.addParameter(new ClosestEnemy()); //add behavior
        commands.add(attack);

        //Move to unit action
        moveToUnit = new MoveToUnitBasic();
        moveToUnit.addParameter(TypeConcrete.getTypeUnits()); //add unit type
        pt=new PlayerTargetParam();
        pt.addPlayer(EnumPlayerTarget.Enemy);
        moveToUnit.addParameter(pt);
        moveToUnit.addParameter(new LessHealthyEnemy()); //add behavior
        commands.add(moveToUnit);

        //Move To coordinates
        MoveToCoordinatesBasic moveToCoordinates = new MoveToCoordinatesBasic();
        moveToCoordinates.addParameter(new CoordinatesParam(14, 5)); //add unit type
        moveToCoordinates.addParameter(TypeConcrete.getTypeUnits());
        commands.add(moveToCoordinates);

        return commands;
    }

    //done
    public List<ICommand> ChromosomesBag19(UnitTypeTable utt) {

        this.utt = utt;
        List<ICommand> commands = new ArrayList<>();
        //build action
        BuildBasic build = new BuildBasic();
        build.addParameter(TypeConcrete.getTypeBarracks()); //add unit construct type
        build.addParameter(new QuantityParam(3)); //add qtd unit
        commands.add(build);

        //train action
        TrainBasic train = new TrainBasic();
        train.addParameter(TypeConcrete.getTypeBarracks()); //add unit construct type
        train.addParameter(TypeConcrete.getTypeLight()); //add unit Type
        //train.addParameter(TypeConcrete.getTypeWorker()); //add unit Type
        train.addParameter(new QuantityParam(20)); //add qtd unit
        PriorityPositionParam pos = new PriorityPositionParam();
        pos.addPosition(EnumPositionType.Up);
        pos.addPosition(EnumPositionType.Left);
        //pos.addPosition(EnumPositionType.Right);
        //pos.addPosition(EnumPositionType.Down);
        train.addParameter(pos);
        commands.add(train);

        //Move To coordinates
        MoveToCoordinatesBasic moveToCoordinates = new MoveToCoordinatesBasic();
        moveToCoordinates.addParameter(new CoordinatesParam(14, 5)); //add unit type
        moveToCoordinates.addParameter(TypeConcrete.getTypeUnits());
        commands.add(moveToCoordinates);

        return commands;

    }

    public List<ICommand> ChromosomesBag20(UnitTypeTable utt) {

        this.utt = utt;
        List<ICommand> commands = new ArrayList<>();
        //build action
        BuildBasic build = new BuildBasic();
        build.addParameter(TypeConcrete.getTypeBarracks()); //add unit construct type
        build.addParameter(new QuantityParam(1)); //add qtd unit
        commands.add(build);

        //train action
        TrainBasic train = new TrainBasic();
        train.addParameter(TypeConcrete.getTypeBarracks()); //add unit construct type
        train.addParameter(TypeConcrete.getTypeLight()); //add unit Type
        //train.addParameter(TypeConcrete.getTypeWorker()); //add unit Type
        train.addParameter(new QuantityParam(20)); //add qtd unit
        PriorityPositionParam pos = new PriorityPositionParam();
        pos.addPosition(EnumPositionType.Up);
        pos.addPosition(EnumPositionType.Left);
        //pos.addPosition(EnumPositionType.Right);
        //pos.addPosition(EnumPositionType.Down);
        train.addParameter(pos);
        System.out.println(train.toString());
        commands.add(train);

        //train action
        train = new TrainBasic();
        train.addParameter(TypeConcrete.getTypeBarracks()); //add unit construct type
        train.addParameter(TypeConcrete.getTypeLight()); //add unit Type
        //train.addParameter(TypeConcrete.getTypeWorker()); //add unit Type
        train.addParameter(new QuantityParam(20)); //add qtd unit
        pos = new PriorityPositionParam();
        pos.addPosition(EnumPositionType.Up);
        pos.addPosition(EnumPositionType.Left);
        //pos.addPosition(EnumPositionType.Right);
        //pos.addPosition(EnumPositionType.Down);
        train.addParameter(pos);
        commands.add(train);

        //harverst action
        HarvestBasic harverst = new HarvestBasic();
        harverst.addParameter(TypeConcrete.getTypeWorker()); //add unit type
        harverst.addParameter(new QuantityParam(2)); //add qtd unit
        commands.add(harverst);
        //attack action
        AttackBasic attack = new AttackBasic();
        attack.addParameter(TypeConcrete.getTypeUnits()); //add unit type
        PlayerTargetParam pt=new PlayerTargetParam();
        pt.addPlayer(EnumPlayerTarget.Enemy);
        attack.addParameter(pt);
        attack.addParameter(new StrongestEnemy()); //add behavior
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

        return commands;
    }
    
    //Done
    public List<ICommand> ChromosomesBag21(UnitTypeTable utt) {

        this.utt = utt;
        List<ICommand> commands = new ArrayList<>();
        
        //BOOLEAN  If there is an enemy in allyRange 
        AttackBasic attack = new AttackBasic();
        attack.addParameter(TypeConcrete.getTypeUnits()); //add unit type
        PlayerTargetParam pt=new PlayerTargetParam();
        pt.addPlayer(EnumPlayerTarget.Enemy);
        attack.addParameter(pt);
        attack.addParameter(new ClosestEnemy()); //add behavior   
        commandsforBoolean = new ArrayList<>();
        commandsforBoolean.add(attack);

        AllyRange allyRangeBoolean = new AllyRange(commandsforBoolean);
        allyRangeBoolean.addParameter(TypeConcrete.getTypeUnits());
        commands.add(allyRangeBoolean);
        
        //BASIC ACTIONS**********************************************************************
        //train action
        TrainBasic train = new TrainBasic();
        train.addParameter(TypeConcrete.getTypeBarracks()); //add unit construct type
        train.addParameter(TypeConcrete.getTypeLight()); //add unit Type
        //train.addParameter(TypeConcrete.getTypeWorker()); //add unit Type
        train.addParameter(new QuantityParam(20)); //add qtd unit
        PriorityPositionParam pos = new PriorityPositionParam();
        pos.addPosition(EnumPositionType.Up);
        pos.addPosition(EnumPositionType.Left);
        //pos.addPosition(EnumPositionType.Right);
        //pos.addPosition(EnumPositionType.Down);
        train.addParameter(pos);
        commands.add(train);

        //harverst action
        HarvestBasic harverst = new HarvestBasic();
        harverst.addParameter(TypeConcrete.getTypeWorker()); //add unit type
        harverst.addParameter(new QuantityParam(1)); //add qtd unit
        commands.add(harverst);

        //BOOLEANS *********************************************************************************************************************************
        //BOOLEAN  If there is an ally in enemyRange 
        attack = new AttackBasic();
        attack.addParameter(TypeConcrete.getTypeUnits()); //add unit type
        pt=new PlayerTargetParam();
        pt.addPlayer(EnumPlayerTarget.Ally);
        attack.addParameter(pt);
        attack.addParameter(new ClosestEnemy()); //add behavior   
        commandsforBoolean = new ArrayList<>();
        commandsforBoolean.add(attack);

        EnemyRange enemyRangeBoolean = new EnemyRange(commandsforBoolean);
        enemyRangeBoolean.addParameter(TypeConcrete.getTypeUnits());
        commands.add(enemyRangeBoolean);



        return commands;
    }

    //Done
    public List<ICommand> ChromosomesBag22(UnitTypeTable utt) {

        this.utt = utt;
        List<ICommand> commands = new ArrayList<>();
        //BASIC ACTIONS**********************************************************************
        
        //BOOLEAN  If there are X ally units of type T 
        MoveToUnitBasic moveToUnit = new MoveToUnitBasic();
        moveToUnit.addParameter(TypeConcrete.getTypeUnits()); //add unit type
        PlayerTargetParam pt=new PlayerTargetParam();
        pt.addPlayer(EnumPlayerTarget.Ally);
        moveToUnit.addParameter(pt);
        moveToUnit.addParameter(new LessHealthyEnemy()); //add behavior
        commandsforBoolean = new ArrayList<>();
        commandsforBoolean.add(moveToUnit);

        NAllyUnitsofType nAllyOfTypeBoolean = new NAllyUnitsofType(commandsforBoolean);
        nAllyOfTypeBoolean.addParameter(TypeConcrete.getTypeUnits());
        nAllyOfTypeBoolean.addParameter(new QuantityParam(3));
        commands.add(nAllyOfTypeBoolean);
        
        //train action
        TrainBasic train = new TrainBasic();
        train.addParameter(TypeConcrete.getTypeBarracks()); //add unit construct type
        train.addParameter(TypeConcrete.getTypeLight()); //add unit Type
        //train.addParameter(TypeConcrete.getTypeWorker()); //add unit Type
        train.addParameter(new QuantityParam(20)); //add qtd unit
        PriorityPositionParam pos = new PriorityPositionParam();
        pos.addPosition(EnumPositionType.Up);
        pos.addPosition(EnumPositionType.Left);
        //pos.addPosition(EnumPositionType.Right);
        //pos.addPosition(EnumPositionType.Down);
        train.addParameter(pos);
        commands.add(train);

        //build action
        BuildBasic build = new BuildBasic();
        build.addParameter(TypeConcrete.getTypeBarracks()); //add unit construct type
        build.addParameter(new QuantityParam(3)); //add qtd unit
        commands.add(build);

        //train action
        train = new TrainBasic();
        train.addParameter(TypeConcrete.getTypeBase()); //add unit construct type
        train.addParameter(TypeConcrete.getTypeWorker()); //add unit Type
        train.addParameter(new QuantityParam(20)); //add qtd unit
        pos = new PriorityPositionParam();
        pos.addPosition(EnumPositionType.Up);
        pos.addPosition(EnumPositionType.Left);
        //pos.addPosition(EnumPositionType.Right);
        //pos.addPosition(EnumPositionType.Down);
        train.addParameter(pos);
        commands.add(train);

        //harverst action
        HarvestBasic harverst = new HarvestBasic();
        harverst.addParameter(TypeConcrete.getTypeWorker()); //add unit type
        harverst.addParameter(new QuantityParam(1)); //add qtd unit
        commands.add(harverst);

        //BOOLEANS *********************************************************************************************************************************
        //BOOLEAN  If ally Is In Distance X From Enemy 
        harverst = new HarvestBasic();
        harverst.addParameter(TypeConcrete.getTypeWorker()); //add unit type
        harverst.addParameter(new QuantityParam(1)); //add qtd unit 
        commandsforBoolean = new ArrayList<>();
        commandsforBoolean.add(harverst);

        DistanceFromEnemy distanceFromEnemyBoolean = new DistanceFromEnemy(commandsforBoolean);
        distanceFromEnemyBoolean.addParameter(TypeConcrete.getTypeUnits());
        distanceFromEnemyBoolean.addParameter(new DistanceParam(6));
        commands.add(distanceFromEnemyBoolean);



        return commands;
    }

    //Done
    public List<ICommand> ChromosomesBag23(UnitTypeTable utt) {

        this.utt = utt;
        List<ICommand> commands = new ArrayList<>();
        
        //Move to unit action
        MoveToUnitBasic  moveToUnit = new MoveToUnitBasic();
        moveToUnit.addParameter(TypeConcrete.getTypeUnits()); //add unit type
        PlayerTargetParam pt=new PlayerTargetParam();
        pt.addPlayer(EnumPlayerTarget.Enemy);
        moveToUnit.addParameter(pt);
        moveToUnit.addParameter(new LessHealthyEnemy()); //add behavior
        commands.add(moveToUnit);

        //Move To coordinates
        MoveToCoordinatesBasic moveToCoordinates = new MoveToCoordinatesBasic();
        moveToCoordinates.addParameter(new CoordinatesParam(14, 5)); //add unit type
        moveToCoordinates.addParameter(TypeConcrete.getTypeUnits());
        commands.add(moveToCoordinates);        
        //BASIC ACTIONS**********************************************************************
        //build action
        BuildBasic build = new BuildBasic();
        build.addParameter(TypeConcrete.getTypeBarracks()); //add unit construct type
        build.addParameter(new QuantityParam(2)); //add qtd unit
        commands.add(build);

        //train action
        TrainBasic train = new TrainBasic();
        train.addParameter(TypeConcrete.getTypeBarracks()); //add unit construct type
        train.addParameter(TypeConcrete.getTypeLight()); //add unit Type
        //train.addParameter(TypeConcrete.getTypeWorker()); //add unit Type
        train.addParameter(new QuantityParam(20)); //add qtd unit
        PriorityPositionParam pos = new PriorityPositionParam();
        pos.addPosition(EnumPositionType.Up);
        pos.addPosition(EnumPositionType.Left);
        //pos.addPosition(EnumPositionType.Right);
        //pos.addPosition(EnumPositionType.Down);
        train.addParameter(pos);
        commands.add(train);

        //train action
        train = new TrainBasic();
        train.addParameter(TypeConcrete.getTypeBase()); //add unit construct type
        train.addParameter(TypeConcrete.getTypeWorker()); //add unit Type
        train.addParameter(new QuantityParam(20)); //add qtd unit
        pos = new PriorityPositionParam();
        pos.addPosition(EnumPositionType.Up);
        pos.addPosition(EnumPositionType.Left);
        //pos.addPosition(EnumPositionType.Right);
        //pos.addPosition(EnumPositionType.Down);
        train.addParameter(pos);
        commands.add(train);

        //harverst action
        HarvestBasic harverst = new HarvestBasic();
        harverst.addParameter(TypeConcrete.getTypeWorker()); //add unit type
        harverst.addParameter(new QuantityParam(1)); //add qtd unit
        commands.add(harverst);

        //BOOLEANS *********************************************************************************************************************************
        //BOOLEAN  If there are X ally units of type T 
        moveToUnit = new MoveToUnitBasic();
        moveToUnit.addParameter(TypeConcrete.getTypeUnits()); //add unit type
        pt=new PlayerTargetParam();
        pt.addPlayer(EnumPlayerTarget.Enemy);
        moveToUnit.addParameter(pt);
        moveToUnit.addParameter(new LessHealthyEnemy()); //add behavior
        commandsforBoolean = new ArrayList<>();
        commandsforBoolean.add(moveToUnit);

        NAllyUnitsofType nAllyOfTypeBoolean = new NAllyUnitsofType(commandsforBoolean);
        nAllyOfTypeBoolean.addParameter(TypeConcrete.getTypeUnits());
        nAllyOfTypeBoolean.addParameter(new QuantityParam(3));
        commands.add(nAllyOfTypeBoolean);

        //BOOLEAN  If there are X ally units of type T attacking
        moveToUnit = new MoveToUnitBasic();
        moveToUnit.addParameter(TypeConcrete.getTypeUnits()); //add unit type
        pt=new PlayerTargetParam();
        pt.addPlayer(EnumPlayerTarget.Enemy);
        moveToUnit.addParameter(pt);
        moveToUnit.addParameter(new LessHealthyEnemy()); //add behavior
        commandsforBoolean = new ArrayList<>();
        commandsforBoolean.add(moveToUnit);

        NAllyUnitsAttacking nAllyAttackingBoolean = new NAllyUnitsAttacking(commandsforBoolean);
        nAllyAttackingBoolean.addParameter(TypeConcrete.getTypeUnits());
        nAllyAttackingBoolean.addParameter(new QuantityParam(3));
        commands.add(nAllyAttackingBoolean);

        //attack action
        AttackBasic attack = new AttackBasic();
        attack.addParameter(TypeConcrete.getTypeUnits()); //add unit type
        pt=new PlayerTargetParam();
        pt.addPlayer(EnumPlayerTarget.Ally);
        attack.addParameter(pt);
        attack.addParameter(new ClosestEnemy()); //add behavior
        commands.add(attack);



        return commands;
    }

    //done
    public List<ICommand> ChromosomesBag24(UnitTypeTable utt) {

        this.utt = utt;
        List<ICommand> commands = new ArrayList<>();
        //Move To coordinates
        
        MoveToCoordinatesBasic moveToCoordinates = new MoveToCoordinatesBasic();
        moveToCoordinates.addParameter(new CoordinatesParam(14, 5)); //add unit type
        moveToCoordinates.addParameter(TypeConcrete.getTypeUnits());
        commands.add(moveToCoordinates);
        
        //build action
        BuildBasic build = new BuildBasic();
        build.addParameter(TypeConcrete.getTypeBarracks()); //add unit construct type
        build.addParameter(new QuantityParam(3)); //add qtd unit
        commands.add(build);

        //train action
        TrainBasic train = new TrainBasic();
        train.addParameter(TypeConcrete.getTypeBarracks()); //add unit construct type
        train.addParameter(TypeConcrete.getTypeLight()); //add unit Type
        //train.addParameter(TypeConcrete.getTypeWorker()); //add unit Type
        train.addParameter(new QuantityParam(20)); //add qtd unit
        PriorityPositionParam pos = new PriorityPositionParam();
        pos.addPosition(EnumPositionType.Up);
        pos.addPosition(EnumPositionType.Left);
        //pos.addPosition(EnumPositionType.Right);
        //pos.addPosition(EnumPositionType.Down);
        train.addParameter(pos);
        commands.add(train);



        return commands;

    }
    
    public List<ICommand> ChromosomesBag25(UnitTypeTable utt) {

        this.utt = utt;
        List<ICommand> commands = new ArrayList<>();


        //BOOLEANS *********************************************************************************************************************************
        //BOOLEAN  If ally Is In Distance X From Enemy 
        HarvestBasic harverst = new HarvestBasic();
        harverst.addParameter(TypeConcrete.getTypeWorker()); //add unit type
        harverst.addParameter(new QuantityParam(1)); //add qtd unit 
        commandsforBoolean = new ArrayList<>();
        commandsforBoolean.add(harverst);

        DistanceFromEnemy distanceFromEnemyBoolean = new DistanceFromEnemy(commandsforBoolean);
        distanceFromEnemyBoolean.addParameter(TypeConcrete.getTypeUnits());
        distanceFromEnemyBoolean.addParameter(new DistanceParam(6));
        commands.add(distanceFromEnemyBoolean);



        return commands;
    }
    
    public List<ICommand> ChromosomesBag26(UnitTypeTable utt) {
        this.utt = utt;
        List<ICommand> commands = new ArrayList<>();
        //BASIC ACTIONS**********************************************************************
        
        AttackBasic attack = new AttackBasic();
        attack.addParameter(TypeConcrete.getTypeUnits()); //add unit type
        PlayerTargetParam pt=new PlayerTargetParam();
        pt.addPlayer(EnumPlayerTarget.Enemy);
        attack.addParameter(pt);
        attack.addParameter(new ClosestEnemy()); //add behavior   
        commandsforBoolean = new ArrayList<>();
        commandsforBoolean.add(attack);

        NEnemyUnitsofType nAllyOfTypeBoolean = new NEnemyUnitsofType(commandsforBoolean);
        nAllyOfTypeBoolean.addParameter(TypeConcrete.getTypeUnits());
        nAllyOfTypeBoolean.addParameter(new QuantityParam(3));
        commands.add(nAllyOfTypeBoolean);

        //build action
        BuildBasic build = new BuildBasic();
        build.addParameter(TypeConcrete.getTypeBarracks()); //add unit construct type
        build.addParameter(new QuantityParam(1)); //add qtd unit
        commands.add(build);

        //train action
        TrainBasic train = new TrainBasic();
        train.addParameter(TypeConcrete.getTypeBase()); //add unit construct type
        train.addParameter(TypeConcrete.getTypeWorker()); //add unit Type
        train.addParameter(new QuantityParam(100)); //add qtd unit
        PriorityPositionParam pos = new PriorityPositionParam();
        pos.addPosition(EnumPositionType.Up);
        pos.addPosition(EnumPositionType.Left);
        //pos.addPosition(EnumPositionType.Right);
        //pos.addPosition(EnumPositionType.Down);
        train.addParameter(pos);
        commands.add(train);

        //harverst action
        HarvestBasic harverst = new HarvestBasic();
        harverst.addParameter(TypeConcrete.getTypeWorker()); //add unit type
        harverst.addParameter(new QuantityParam(1)); //add qtd unit
        commands.add(harverst);



        return commands;
    }

    //Done
    public List<ICommand> ChromosomesBag27(UnitTypeTable utt) {

        this.utt = utt;
        List<ICommand> commands = new ArrayList<>();
        //BASIC ACTIONS**********************************************************************
        
        //attack action
        AttackBasic attack = new AttackBasic();
        attack.addParameter(TypeConcrete.getTypeUnits()); //add unit type
        PlayerTargetParam pt=new PlayerTargetParam();
        pt.addPlayer(EnumPlayerTarget.Enemy);
        attack.addParameter(pt);
        attack.addParameter(new ClosestEnemy()); //add behavior
        commands.add(attack);
        
        //train action
        TrainBasic train = new TrainBasic();
        train.addParameter(TypeConcrete.getTypeBase()); //add unit construct type
        train.addParameter(TypeConcrete.getTypeWorker()); //add unit Type
        train.addParameter(new QuantityParam(20)); //add qtd unit
        PriorityPositionParam pos = new PriorityPositionParam();
        pos.addPosition(EnumPositionType.Up);
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



        return commands;
    }

    //Done
    public List<ICommand> ChromosomesBag28(UnitTypeTable utt) {

        this.utt = utt;
        List<ICommand> commands = new ArrayList<>();
        //BASIC ACTIONS**********************************************************************
        
        //BOOLEAN  If there is an enemy in allyRange 
        MoveToUnitBasic moveToUnit = new MoveToUnitBasic();
        moveToUnit.addParameter(TypeConcrete.getTypeUnits()); //add unit type
        PlayerTargetParam pt=new PlayerTargetParam();
        pt.addPlayer(EnumPlayerTarget.Enemy);
        moveToUnit.addParameter(pt);
        moveToUnit.addParameter(new LessHealthyEnemy()); //add behavior
        commandsforBoolean = new ArrayList<>();
        commandsforBoolean.add(moveToUnit);

        AllyRange allyRangeBoolean = new AllyRange(commandsforBoolean);
        allyRangeBoolean.addParameter(TypeConcrete.getTypeUnits());
        commands.add(allyRangeBoolean);        
        
        //build action
        BuildBasic build = new BuildBasic();
        build.addParameter(TypeConcrete.getTypeBarracks()); //add unit construct type
        build.addParameter(new QuantityParam(1)); //add qtd unit
        commands.add(build);

        //train action
        TrainBasic train = new TrainBasic();
        train.addParameter(TypeConcrete.getTypeBase()); //add unit construct type
        train.addParameter(TypeConcrete.getTypeWorker()); //add unit Type
        train.addParameter(new QuantityParam(20)); //add qtd unit
        PriorityPositionParam pos = new PriorityPositionParam();
        //pos.addPosition(EnumPositionType.Up);
        //pos.addPosition(EnumPositionType.Left);
        //pos.addPosition(EnumPositionType.Right);
        //pos.addPosition(EnumPositionType.Down);
        train.addParameter(pos);
        commands.add(train);



        return commands;
    }

    //Done
    public List<ICommand> ChromosomesBag29(UnitTypeTable utt) {

        this.utt = utt;
        List<ICommand> commands = new ArrayList<>();
        //BASIC ACTIONS**********************************************************************
        
        //BOOLEAN  If there are X ally units of type T 
        MoveToCoordinatesBasic moveToCoordinates = new MoveToCoordinatesBasic();
        moveToCoordinates.addParameter(new CoordinatesParam(14, 5)); //add unit type
        moveToCoordinates.addParameter(TypeConcrete.getTypeUnits());
        commands.add(moveToCoordinates);
        commandsforBoolean = new ArrayList<>();
        commandsforBoolean.add(moveToCoordinates);
        
        //build action
        BuildBasic build = new BuildBasic();
        build.addParameter(TypeConcrete.getTypeBarracks()); //add unit construct type
        build.addParameter(new QuantityParam(1)); //add qtd unit
        commands.add(build);

        //train action
        TrainBasic train = new TrainBasic();
        train.addParameter(TypeConcrete.getTypeBase()); //add unit construct type
        train.addParameter(TypeConcrete.getTypeWorker()); //add unit Type
        train.addParameter(new QuantityParam(20)); //add qtd unit
        PriorityPositionParam pos = new PriorityPositionParam();
        pos.addPosition(EnumPositionType.Up);
        //pos.addPosition(EnumPositionType.Left);
        pos.addPosition(EnumPositionType.Right);
        //pos.addPosition(EnumPositionType.Down);
        train.addParameter(pos);
        commands.add(train);

        //harverst action
        HarvestBasic harverst = new HarvestBasic();
        harverst.addParameter(TypeConcrete.getTypeWorker()); //add unit type
        harverst.addParameter(new QuantityParam(1)); //add qtd unit
        commands.add(harverst);

        //Move To coordinates
        moveToCoordinates = new MoveToCoordinatesBasic();
        moveToCoordinates.addParameter(new CoordinatesParam(14, 5)); //add unit type
        moveToCoordinates.addParameter(TypeConcrete.getTypeUnits());
        commands.add(moveToCoordinates);



        return commands;
    }

    //Done
    public List<ICommand> ChromosomesBag30(UnitTypeTable utt) {

        this.utt = utt;
        List<ICommand> commands = new ArrayList<>();
        //BASIC ACTIONS**********************************************************************
        //BOOLEAN  If ally Is In Distance X From Enemy 
        AttackBasic attack = new AttackBasic();
        attack.addParameter(TypeConcrete.getTypeUnits()); //add unit type
        PlayerTargetParam pt=new PlayerTargetParam();
        pt.addPlayer(EnumPlayerTarget.Ally);
        attack.addParameter(pt);
        attack.addParameter(new ClosestEnemy()); //add behavior   
        commandsforBoolean = new ArrayList<>();
        commandsforBoolean.add(attack);

        DistanceFromEnemy distanceFromEnemyBoolean = new DistanceFromEnemy(commandsforBoolean);
        distanceFromEnemyBoolean.addParameter(TypeConcrete.getTypeUnits());
        distanceFromEnemyBoolean.addParameter(new DistanceParam(6));
        commands.add(distanceFromEnemyBoolean);
        
        //build action
        BuildBasic build = new BuildBasic();
        build.addParameter(TypeConcrete.getTypeBarracks()); //add unit construct type
        build.addParameter(new QuantityParam(3)); //add qtd unit
        commands.add(build);

        //train action
        TrainBasic train = new TrainBasic();
        train.addParameter(TypeConcrete.getTypeBase()); //add unit construct type
        train.addParameter(TypeConcrete.getTypeWorker()); //add unit Type
        train.addParameter(new QuantityParam(100)); //add qtd unit
        PriorityPositionParam pos = new PriorityPositionParam();
        //pos.addPosition(EnumPositionType.Up);
        //pos.addPosition(EnumPositionType.Left);
        //pos.addPosition(EnumPositionType.Right);
        pos.addPosition(EnumPositionType.Down);
        train.addParameter(pos);
        commands.add(train);

        //harverst action
        HarvestBasic harverst = new HarvestBasic();
        harverst.addParameter(TypeConcrete.getTypeWorker()); //add unit type
        harverst.addParameter(new QuantityParam(10)); //add qtd unit
        commands.add(harverst);

        //BOOLEANS *********************************************************************************************************************************


        return commands;
    }

    //Done
    public List<ICommand> ChromosomesBag31(UnitTypeTable utt) {

        this.utt = utt;
        List<ICommand> commands = new ArrayList<>();
        //BASIC ACTIONS**********************************************************************
        
        //BOOLEAN  If there is an enemy in allyRange 
        AttackBasic attack = new AttackBasic();
        attack.addParameter(TypeConcrete.getTypeUnits()); //add unit type
        PlayerTargetParam pt=new PlayerTargetParam();
        pt.addPlayer(EnumPlayerTarget.Enemy);
        attack.addParameter(pt);
        attack.addParameter(new ClosestEnemy()); //add behavior   
        commandsforBoolean = new ArrayList<>();
        commandsforBoolean.add(attack);

        AllyRange allyRangeBoolean = new AllyRange(commandsforBoolean);
        allyRangeBoolean.addParameter(TypeConcrete.getTypeUnits());
        commands.add(allyRangeBoolean);
        
        //train action
        TrainBasic train = new TrainBasic();
        train.addParameter(TypeConcrete.getTypeBase()); //add unit construct type
        train.addParameter(TypeConcrete.getTypeWorker()); //add unit Type
        train.addParameter(new QuantityParam(20)); //add qtd unit
        PriorityPositionParam pos = new PriorityPositionParam();
        pos.addPosition(EnumPositionType.Up);
        pos.addPosition(EnumPositionType.Left);
        //pos.addPosition(EnumPositionType.Right);
        //pos.addPosition(EnumPositionType.Down);
        train.addParameter(pos);
        commands.add(train);

        //harverst action
        HarvestBasic harverst = new HarvestBasic();
        harverst.addParameter(TypeConcrete.getTypeWorker()); //add unit type
        harverst.addParameter(new QuantityParam(1)); //add qtd unit
        commands.add(harverst);

        //BOOLEANS *********************************************************************************************************************************
        //BOOLEAN  If there is an ally in enemyRange 
        attack = new AttackBasic();
        attack.addParameter(TypeConcrete.getTypeUnits()); //add unit type
        pt=new PlayerTargetParam();
        pt.addPlayer(EnumPlayerTarget.Enemy);
        attack.addParameter(pt);
        attack.addParameter(new ClosestEnemy()); //add behavior   
        commandsforBoolean = new ArrayList<>();
        commandsforBoolean.add(attack);

        EnemyRange enemyRangeBoolean = new EnemyRange(commandsforBoolean);
        enemyRangeBoolean.addParameter(TypeConcrete.getTypeUnits());
        commands.add(enemyRangeBoolean);



        return commands;
    }

    //Done
    public List<ICommand> ChromosomesBag32(UnitTypeTable utt) {

        this.utt = utt;
        List<ICommand> commands = new ArrayList<>();
        //BASIC ACTIONS**********************************************************************
        
        //BOOLEAN  If ally Is In Distance X From Enemy 
        HarvestBasic harverst = new HarvestBasic();
        harverst.addParameter(TypeConcrete.getTypeWorker()); //add unit type
        harverst.addParameter(new QuantityParam(1)); //add qtd unit 
        commandsforBoolean = new ArrayList<>();
        commandsforBoolean.add(harverst);

        DistanceFromEnemy distanceFromEnemyBoolean = new DistanceFromEnemy(commandsforBoolean);
        distanceFromEnemyBoolean.addParameter(TypeConcrete.getTypeUnits());
        distanceFromEnemyBoolean.addParameter(new DistanceParam(6));
        commands.add(distanceFromEnemyBoolean);

        //BOOLEAN  If there are X ally units of type T 
        MoveToUnitBasic moveToUnit = new MoveToUnitBasic();
        moveToUnit.addParameter(TypeConcrete.getTypeUnits()); //add unit type
        PlayerTargetParam pt=new PlayerTargetParam();
        pt.addPlayer(EnumPlayerTarget.Enemy);
        moveToUnit.addParameter(pt);
        moveToUnit.addParameter(new LessHealthyEnemy()); //add behavior
        commandsforBoolean = new ArrayList<>();
        commandsforBoolean.add(moveToUnit);

        NAllyUnitsofType nAllyOfTypeBoolean = new NAllyUnitsofType(commandsforBoolean);
        nAllyOfTypeBoolean.addParameter(TypeConcrete.getTypeUnits());
        nAllyOfTypeBoolean.addParameter(new QuantityParam(3));
        commands.add(nAllyOfTypeBoolean);
        
        //train action
        TrainBasic train = new TrainBasic();
        train.addParameter(TypeConcrete.getTypeBase()); //add unit construct type
        train.addParameter(TypeConcrete.getTypeWorker()); //add unit Type
        train.addParameter(new QuantityParam(20)); //add qtd unit
        PriorityPositionParam pos = new PriorityPositionParam();
        pos.addPosition(EnumPositionType.Up);
        pos.addPosition(EnumPositionType.Left);
        //pos.addPosition(EnumPositionType.Right);
        //pos.addPosition(EnumPositionType.Down);
        train.addParameter(pos);
        commands.add(train);

        //harverst action
        harverst = new HarvestBasic();
        harverst.addParameter(TypeConcrete.getTypeWorker()); //add unit type
        harverst.addParameter(new QuantityParam(1)); //add qtd unit
        commands.add(harverst);

        //BOOLEANS *********************************************************************************************************************************


        return commands;
    }

    //Done
    public List<ICommand> ChromosomesBag33(UnitTypeTable utt) {

        this.utt = utt;
        List<ICommand> commands = new ArrayList<>();
        //BASIC ACTIONS**********************************************************************
        
        //Move to unit action
        MoveToUnitBasic moveToUnit = new MoveToUnitBasic();
        moveToUnit.addParameter(TypeConcrete.getTypeUnits()); //add unit type
        PlayerTargetParam pt=new PlayerTargetParam();
        pt.addPlayer(EnumPlayerTarget.Enemy);
        moveToUnit.addParameter(pt);
        moveToUnit.addParameter(new LessHealthyEnemy()); //add behavior
        commands.add(moveToUnit);

        //Move To coordinates
        MoveToCoordinatesBasic moveToCoordinates = new MoveToCoordinatesBasic();
        moveToCoordinates.addParameter(new CoordinatesParam(14, 5)); //add unit type
        moveToCoordinates.addParameter(TypeConcrete.getTypeUnits());
        commands.add(moveToCoordinates);
        
        //build action
        BuildBasic build = new BuildBasic();
        build.addParameter(TypeConcrete.getTypeBarracks()); //add unit construct type
        build.addParameter(new QuantityParam(2)); //add qtd unit
        commands.add(build);

        //train action
        TrainBasic train = new TrainBasic();
        train.addParameter(TypeConcrete.getTypeBase()); //add unit construct type
        train.addParameter(TypeConcrete.getTypeWorker()); //add unit Type
        train.addParameter(new QuantityParam(20)); //add qtd unit
        PriorityPositionParam pos = new PriorityPositionParam();
        pos.addPosition(EnumPositionType.Up);
        pos.addPosition(EnumPositionType.Left);
        //pos.addPosition(EnumPositionType.Right);
        //pos.addPosition(EnumPositionType.Down);
        train.addParameter(pos);
        commands.add(train);

        //harverst action
        HarvestBasic harverst = new HarvestBasic();
        harverst.addParameter(TypeConcrete.getTypeWorker()); //add unit type
        harverst.addParameter(new QuantityParam(1)); //add qtd unit
        commands.add(harverst);

        //BOOLEANS *********************************************************************************************************************************
        //BOOLEAN  If there are X ally units of type T 
        moveToUnit = new MoveToUnitBasic();
        moveToUnit.addParameter(TypeConcrete.getTypeUnits()); //add unit type
        pt=new PlayerTargetParam();
        pt.addPlayer(EnumPlayerTarget.Enemy);
        moveToUnit.addParameter(pt);
        moveToUnit.addParameter(new LessHealthyEnemy()); //add behavior
        commandsforBoolean = new ArrayList<>();
        commandsforBoolean.add(moveToUnit);

        NAllyUnitsofType nAllyOfTypeBoolean = new NAllyUnitsofType(commandsforBoolean);
        nAllyOfTypeBoolean.addParameter(TypeConcrete.getTypeUnits());
        nAllyOfTypeBoolean.addParameter(new QuantityParam(3));
        commands.add(nAllyOfTypeBoolean);

        //BOOLEAN  If there are X ally units of type T attacking
        moveToUnit = new MoveToUnitBasic();
        moveToUnit.addParameter(TypeConcrete.getTypeUnits()); //add unit type
        pt=new PlayerTargetParam();
        pt.addPlayer(EnumPlayerTarget.Enemy);
        moveToUnit.addParameter(pt);
        moveToUnit.addParameter(new LessHealthyEnemy()); //add behavior
        commandsforBoolean = new ArrayList<>();
        commandsforBoolean.add(moveToUnit);

        NAllyUnitsAttacking nAllyAttackingBoolean = new NAllyUnitsAttacking(commandsforBoolean);
        nAllyAttackingBoolean.addParameter(TypeConcrete.getTypeUnits());
        nAllyAttackingBoolean.addParameter(new QuantityParam(3));
        commands.add(nAllyAttackingBoolean);

        //attack action
        AttackBasic attack = new AttackBasic();
        attack.addParameter(TypeConcrete.getTypeUnits()); //add unit type
        pt=new PlayerTargetParam();
        pt.addPlayer(EnumPlayerTarget.Enemy);
        attack.addParameter(pt);
        attack.addParameter(new ClosestEnemy()); //add behavior
        commands.add(attack);

        return commands;
    }

    //done
    public List<ICommand> ChromosomesBag34(UnitTypeTable utt) {

        this.utt = utt;
        List<ICommand> commands = new ArrayList<>();
        //Move To coordinates
        MoveToCoordinatesBasic moveToCoordinates = new MoveToCoordinatesBasic();
        moveToCoordinates.addParameter(new CoordinatesParam(14, 5)); //add unit type
        moveToCoordinates.addParameter(TypeConcrete.getTypeUnits());
        commands.add(moveToCoordinates);
        
        //Move To coordinates
        moveToCoordinates = new MoveToCoordinatesBasic();
        moveToCoordinates.addParameter(new CoordinatesParam(14, 5)); //add unit type
        moveToCoordinates.addParameter(TypeConcrete.getTypeUnits());
        commands.add(moveToCoordinates);

        return commands;
    }

    public List<ICommand> ChromosomesBag35(UnitTypeTable utt) {

        this.utt = utt;
        List<ICommand> commands = new ArrayList<>();
        //build action
        BuildBasic build = new BuildBasic();
        build.addParameter(TypeConcrete.getTypeBarracks()); //add unit construct type
        build.addParameter(new QuantityParam(1)); //add qtd unit
        commands.add(build);
        
        //build action
        build = new BuildBasic();
        build.addParameter(TypeConcrete.getTypeBarracks()); //add unit construct type
        build.addParameter(new QuantityParam(1)); //add qtd unit
        commands.add(build);
        
        //build action
        build = new BuildBasic();
        build.addParameter(TypeConcrete.getTypeBarracks()); //add unit construct type
        build.addParameter(new QuantityParam(1)); //add qtd unit
        commands.add(build);

        //train action
        TrainBasic train = new TrainBasic();
        train.addParameter(TypeConcrete.getTypeBarracks()); //add unit construct type
        train.addParameter(TypeConcrete.getTypeLight()); //add unit Type
        //train.addParameter(TypeConcrete.getTypeWorker()); //add unit Type
        train.addParameter(new QuantityParam(20)); //add qtd unit
        PriorityPositionParam pos = new PriorityPositionParam();
        pos.addPosition(EnumPositionType.Up);
        pos.addPosition(EnumPositionType.Left);
        //pos.addPosition(EnumPositionType.Right);
        //pos.addPosition(EnumPositionType.Down);
        train.addParameter(pos);
        commands.add(train);
        //harverst action
        HarvestBasic harverst = new HarvestBasic();
        harverst.addParameter(TypeConcrete.getTypeWorker()); //add unit type
        harverst.addParameter(new QuantityParam(2)); //add qtd unit
        commands.add(harverst);
        //attack action
        AttackBasic attack = new AttackBasic();
        attack.addParameter(TypeConcrete.getTypeUnits()); //add unit type
        PlayerTargetParam pt=new PlayerTargetParam();
        pt.addPlayer(EnumPlayerTarget.Enemy);
        attack.addParameter(pt);
        attack.addParameter(new StrongestEnemy()); //add behavior
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
        return commands;
    }

    public List<ICommand> ChromosomesBag36(UnitTypeTable utt) {
        this.utt = utt;
        List<ICommand> commands = new ArrayList<>();
        //BASIC ACTIONS**********************************************************************
        AttackBasic attack = new AttackBasic();
        attack.addParameter(TypeConcrete.getTypeUnits()); //add unit type
        PlayerTargetParam pt=new PlayerTargetParam();
        pt.addPlayer(EnumPlayerTarget.Ally);
        attack.addParameter(pt);
        attack.addParameter(new ClosestEnemy()); //add behavior   
        commandsforBoolean = new ArrayList<>();
        commandsforBoolean.add(attack);

        NEnemyUnitsofType nAllyOfTypeBoolean = new NEnemyUnitsofType(commandsforBoolean);
        nAllyOfTypeBoolean.addParameter(TypeConcrete.getTypeUnits());
        nAllyOfTypeBoolean.addParameter(new QuantityParam(3));
        commands.add(nAllyOfTypeBoolean);
        
        //build action
        BuildBasic build = new BuildBasic();
        build.addParameter(TypeConcrete.getTypeBarracks()); //add unit construct type
        build.addParameter(new QuantityParam(1)); //add qtd unit
        commands.add(build);

        //train action
        TrainBasic train = new TrainBasic();
        train.addParameter(TypeConcrete.getTypeBarracks()); //add unit construct type
        train.addParameter(TypeConcrete.getTypeLight()); //add unit Type
        //train.addParameter(TypeConcrete.getTypeWorker()); //add unit Type
        train.addParameter(new QuantityParam(20)); //add qtd unit
        PriorityPositionParam pos = new PriorityPositionParam();
        pos.addPosition(EnumPositionType.Up);
        pos.addPosition(EnumPositionType.Left);
        //pos.addPosition(EnumPositionType.Right);
        //pos.addPosition(EnumPositionType.Down);
        train.addParameter(pos);
        commands.add(train);

        //train action
        train = new TrainBasic();
        train.addParameter(TypeConcrete.getTypeBase()); //add unit construct type
        train.addParameter(TypeConcrete.getTypeWorker()); //add unit Type
        train.addParameter(new QuantityParam(100)); //add qtd unit
        pos = new PriorityPositionParam();
        pos.addPosition(EnumPositionType.Up);
        pos.addPosition(EnumPositionType.Left);
        //pos.addPosition(EnumPositionType.Right);
        //pos.addPosition(EnumPositionType.Down);
        train.addParameter(pos);
        commands.add(train);

        //harverst action
        HarvestBasic harverst = new HarvestBasic();
        harverst.addParameter(TypeConcrete.getTypeWorker()); //add unit type
        harverst.addParameter(new QuantityParam(1)); //add qtd unit
        commands.add(harverst);



        return commands;
    }

    //Done
    public List<ICommand> ChromosomesBag37(UnitTypeTable utt) {

        this.utt = utt;
        List<ICommand> commands = new ArrayList<>();
        //BASIC ACTIONS**********************************************************************
        
        //attack action
        AttackBasic attack = new AttackBasic();
        attack.addParameter(TypeConcrete.getTypeUnits()); //add unit type
        PlayerTargetParam pt=new PlayerTargetParam();
        pt.addPlayer(EnumPlayerTarget.Ally);
        attack.addParameter(pt);
        attack.addParameter(new ClosestEnemy()); //add behavior
        commands.add(attack);
        
        //train action
        TrainBasic train = new TrainBasic();
        train.addParameter(TypeConcrete.getTypeBarracks()); //add unit construct type
        train.addParameter(TypeConcrete.getTypeRanged()); //add unit Type
        //train.addParameter(TypeConcrete.getTypeWorker()); //add unit Type
        train.addParameter(new QuantityParam(20)); //add qtd unit
        PriorityPositionParam pos = new PriorityPositionParam();
        pos.addPosition(EnumPositionType.Up);
        pos.addPosition(EnumPositionType.Left);
        //pos.addPosition(EnumPositionType.Right);
        //pos.addPosition(EnumPositionType.Down);
        train.addParameter(pos);
        commands.add(train);

        //harverst action
        HarvestBasic harverst = new HarvestBasic();
        harverst.addParameter(TypeConcrete.getTypeWorker()); //add unit type
        harverst.addParameter(new QuantityParam(1)); //add qtd unit
        commands.add(harverst);



        return commands;
    }

    //Done
    public List<ICommand> ChromosomesBag38(UnitTypeTable utt) {

        this.utt = utt;
        List<ICommand> commands = new ArrayList<>();
        //BASIC ACTIONS**********************************************************************
        //build action
        //build action
        BuildBasic build = new BuildBasic();
        build.addParameter(TypeConcrete.getTypeBarracks()); //add unit construct type
        build.addParameter(new QuantityParam(1)); //add qtd unit
        commands.add(build);
        
        //train action
        TrainBasic train = new TrainBasic();
        train.addParameter(TypeConcrete.getTypeBarracks()); //add unit construct type
        train.addParameter(TypeConcrete.getTypeLight()); //add unit Type
        train.addParameter(TypeConcrete.getTypeRanged()); //add unit Type
        //train.addParameter(TypeConcrete.getTypeWorker()); //add unit Type
        train.addParameter(new QuantityParam(20)); //add qtd unit
        PriorityPositionParam pos = new PriorityPositionParam();
        pos.addPosition(EnumPositionType.Up);
        pos.addPosition(EnumPositionType.Left);
        //pos.addPosition(EnumPositionType.Right);
        //pos.addPosition(EnumPositionType.Down);
        train.addParameter(pos);
        commands.add(train);
        //harverst action
        HarvestBasic harverst = new HarvestBasic();
        harverst.addParameter(TypeConcrete.getTypeWorker()); //add unit type
        harverst.addParameter(new QuantityParam(2)); //add qtd unit
        commands.add(harverst);
        //attack action
        AttackBasic attack = new AttackBasic();
        attack.addParameter(TypeConcrete.getTypeUnits()); //add unit type
        PlayerTargetParam pt=new PlayerTargetParam();
        pt.addPlayer(EnumPlayerTarget.Ally);
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

        return commands;
    }

    //Done
    public List<ICommand> ChromosomesBag39(UnitTypeTable utt) {

        this.utt = utt;
        List<ICommand> commands = new ArrayList<>();
        //BASIC ACTIONS**********************************************************************
        //build action
        BuildBasic build = new BuildBasic();
        build.addParameter(TypeConcrete.getTypeBarracks()); //add unit construct type
        build.addParameter(new QuantityParam(1)); //add qtd unit
        commands.add(build);

        //train action
        //train action
        TrainBasic train = new TrainBasic();
        train.addParameter(TypeConcrete.getTypeBarracks()); //add unit construct type
        train.addParameter(TypeConcrete.getTypeLight()); //add unit Type
        train.addParameter(TypeConcrete.getTypeRanged()); //add unit Type
        //train.addParameter(TypeConcrete.getTypeWorker()); //add unit Type
        train.addParameter(new QuantityParam(20)); //add qtd unit
        PriorityPositionParam pos = new PriorityPositionParam();
        pos.addPosition(EnumPositionType.Up);
        pos.addPosition(EnumPositionType.Left);
        //pos.addPosition(EnumPositionType.Right);
        //pos.addPosition(EnumPositionType.Down);
        train.addParameter(pos);
        commands.add(train);

        //harverst action
        HarvestBasic harverst = new HarvestBasic();
        harverst.addParameter(TypeConcrete.getTypeWorker()); //add unit type
        harverst.addParameter(new QuantityParam(1)); //add qtd unit
        commands.add(harverst);

        //Move To coordinates
        MoveToCoordinatesBasic moveToCoordinates = new MoveToCoordinatesBasic();
        moveToCoordinates.addParameter(new CoordinatesParam(14, 5)); //add unit type
        moveToCoordinates.addParameter(TypeConcrete.getTypeUnits());
        commands.add(moveToCoordinates);

        //BOOLEAN  If there are X ally units of type T 
        moveToCoordinates = new MoveToCoordinatesBasic();
        moveToCoordinates.addParameter(new CoordinatesParam(14, 5)); //add unit type
        moveToCoordinates.addParameter(TypeConcrete.getTypeUnits());
        commands.add(moveToCoordinates);
        commandsforBoolean = new ArrayList<>();
        commandsforBoolean.add(moveToCoordinates);

        return commands;
    }

    //Done
    public List<ICommand> ChromosomesBag40(UnitTypeTable utt) {

        this.utt = utt;
        List<ICommand> commands = new ArrayList<>();
        //BASIC ACTIONS**********************************************************************
        
        //BOOLEAN  If ally Is In Distance X From Enemy 
        AttackBasic attack = new AttackBasic();
        attack.addParameter(TypeConcrete.getTypeUnits()); //add unit type
        PlayerTargetParam pt=new PlayerTargetParam();
        pt.addPlayer(EnumPlayerTarget.Enemy);
        attack.addParameter(pt);
        attack.addParameter(new ClosestEnemy()); //add behavior   
        commandsforBoolean = new ArrayList<>();
        commandsforBoolean.add(attack);

        DistanceFromEnemy distanceFromEnemyBoolean = new DistanceFromEnemy(commandsforBoolean);
        distanceFromEnemyBoolean.addParameter(TypeConcrete.getTypeUnits());
        distanceFromEnemyBoolean.addParameter(new DistanceParam(6));
        commands.add(distanceFromEnemyBoolean);
        
        //build action
        BuildBasic build = new BuildBasic();
        build.addParameter(TypeConcrete.getTypeBarracks()); //add unit construct type
        build.addParameter(new QuantityParam(3)); //add qtd unit
        commands.add(build);

        //train action
        TrainBasic train = new TrainBasic();
        train.addParameter(TypeConcrete.getTypeBase()); //add unit construct type
        train.addParameter(TypeConcrete.getTypeWorker()); //add unit Type
        train.addParameter(new QuantityParam(100)); //add qtd unit
        PriorityPositionParam pos = new PriorityPositionParam();
        //pos.addPosition(EnumPositionType.Up);
        //pos.addPosition(EnumPositionType.Left);
        //pos.addPosition(EnumPositionType.Right);
        pos.addPosition(EnumPositionType.Down);
        train.addParameter(pos);
        commands.add(train);

        //train action
        train = new TrainBasic();
        train.addParameter(TypeConcrete.getTypeBarracks()); //add unit construct type
        train.addParameter(TypeConcrete.getTypeLight()); //add unit Type
        //train.addParameter(TypeConcrete.getTypeWorker()); //add unit Type
        train.addParameter(new QuantityParam(20)); //add qtd unit
        pos = new PriorityPositionParam();
        pos.addPosition(EnumPositionType.Up);
        pos.addPosition(EnumPositionType.Left);
        //pos.addPosition(EnumPositionType.Right);
        //pos.addPosition(EnumPositionType.Down);
        train.addParameter(pos);
        commands.add(train);

        //harverst action
        HarvestBasic harverst = new HarvestBasic();
        harverst.addParameter(TypeConcrete.getTypeWorker()); //add unit type
        harverst.addParameter(new QuantityParam(10)); //add qtd unit
        commands.add(harverst);

        //BOOLEANS *********************************************************************************************************************************


        return commands;
    }
    
    public List<ICommand> ChromosomesBag41(UnitTypeTable utt) {

        this.utt = utt;
        List<ICommand> commands = new ArrayList<>();
        //BASIC ACTIONS**********************************************************************
        
        HarvestBasic harverst = new HarvestBasic();
        harverst.addParameter(TypeConcrete.getTypeWorker()); //add unit type
        harverst.addParameter(new QuantityParam(1)); //add qtd unit
        commands.add(harverst);
        
        TrainBasic train = new TrainBasic();
        train.addParameter(TypeConcrete.getTypeBase()); //add unit construct type
        train.addParameter(TypeConcrete.getTypeWorker()); //add unit Type
        //train.addParameter(TypeConcrete.getTypeWorker()); //add unit Type
        train.addParameter(new QuantityParam(20)); //add qtd unit
        PriorityPositionParam pos = new PriorityPositionParam();
        pos.addPosition(EnumPositionType.Up);
        pos.addPosition(EnumPositionType.Left);
        pos.addPosition(EnumPositionType.Right);
        pos.addPosition(EnumPositionType.Down);  
        train.addParameter(pos);
        commands.add(train);
        
        AttackBasic attack = new AttackBasic();
        attack.addParameter(TypeConcrete.getTypeWorker()); //add unit type
        PlayerTargetParam pt = new PlayerTargetParam();
        pt.addPlayer(EnumPlayerTarget.Enemy);
        attack.addParameter(pt);
        attack.addParameter(new ClosestEnemy()); //add behavior
        commands.add(attack);

        return commands;
    }

    public PlayerAction getAction(int player, GameState gs) {
        PlayerAction currentActions = new PlayerAction();
        PathFinding pf = new AStarPathFinding();

        //simulate one WR
        for (ICommand command : commands2) {
            currentActions = command.getAction(gs, player, currentActions, pf, utt);
        }
        System.out.println("currentActions " + currentActions.toString());
        return currentActions;
    }
}
