/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.ScriptsGenerator.TableGenerator;

import PVAI.util.Permutation;
import ai.ScriptsGenerator.Command.BasicAction.AttackBasic;
import ai.ScriptsGenerator.Command.BasicAction.BuildBasic;
import ai.ScriptsGenerator.Command.BasicAction.HarvestBasic;
import ai.ScriptsGenerator.Command.BasicAction.MoveToCoordinatesBasic;
import ai.ScriptsGenerator.Command.BasicAction.MoveToUnitBasic;
import ai.ScriptsGenerator.Command.BasicAction.TrainBasic;
import ai.ScriptsGenerator.Command.BasicBoolean.AllyRange;
import ai.ScriptsGenerator.Command.BasicBoolean.DistanceFromEnemy;
import ai.ScriptsGenerator.Command.BasicBoolean.EnemyRange;
import ai.ScriptsGenerator.Command.BasicBoolean.NAllyUnitsAttacking;
import ai.ScriptsGenerator.Command.BasicBoolean.NAllyUnitsHarvesting;
import ai.ScriptsGenerator.Command.BasicBoolean.NAllyUnitsofType;
import ai.ScriptsGenerator.Command.BasicBoolean.NEnemyUnitsofType;
import ai.ScriptsGenerator.Command.Enumerators.EnumPlayerTarget;
import ai.ScriptsGenerator.Command.Enumerators.EnumPositionType;
import ai.ScriptsGenerator.CommandInterfaces.ICommand;
import ai.ScriptsGenerator.IParameters.IParameters;
import ai.ScriptsGenerator.ParametersConcrete.ClosestEnemy;
import ai.ScriptsGenerator.ParametersConcrete.CoordinatesParam;
import ai.ScriptsGenerator.ParametersConcrete.DistanceParam;
import ai.ScriptsGenerator.ParametersConcrete.FarthestEnemy;
import ai.ScriptsGenerator.ParametersConcrete.LessHealthyEnemy;
import ai.ScriptsGenerator.ParametersConcrete.MostHealthyEnemy;
import ai.ScriptsGenerator.ParametersConcrete.PlayerTargetParam;
import ai.ScriptsGenerator.ParametersConcrete.PriorityPositionParam;
import ai.ScriptsGenerator.ParametersConcrete.QuantityParam;
import ai.ScriptsGenerator.ParametersConcrete.RandomEnemy;
import ai.ScriptsGenerator.ParametersConcrete.StrongestEnemy;
import ai.ScriptsGenerator.ParametersConcrete.TypeConcrete;
import ai.ScriptsGenerator.ParametersConcrete.WeakestEnemy;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import rts.units.UnitTypeTable;

/**
 *
 * @author rubens
 */
public class TableCommandsGenerator {

    UnitTypeTable utt;
    ArrayList<ICommand> commands;
    HashMap<Integer, ICommand> dicCommand;
    ArrayList<ArrayList<Integer>> bagofTypes;
    HashMap<Integer, Integer> correspondenceofTypes;
    ArrayList<String> differentTypes;
    

    //list of fixed param
    private static final int MAX_QTD_WORKERS_HARVERST = 5;
    private static final int MAX_QTD_UNITS_TO_BUILD = 3;
    private static final int MAX_QTD_UNITS_TO_TRAIN = 20;
    private static final int MAP_SIZE = 8;
    private static final int MAX_QTD_NAllyUnitsAttacking = 4;
    
//    private static final int ID_TYPE_AttackBasic= 0;
//    private static final int ID_TYPE_BuildBasic= 1;
//    private static final int ID_TYPE_HarvestBasic= 2;
//    private static final int ID_TYPE_MoveToCoordinatesBasic= 3;
//    private static final int ID_TYPE_MoveToUnitBasic= 4;
//    private static final int ID_TYPE_TrainBasic= 5;
//    private static final int ID_TYPE_AllyRange= 6;
//    private static final int ID_TYPE_DistanceFromEnemy= 7;
//    private static final int ID_TYPE_EnemyRange= 8;
//    private static final int ID_TYPE_NAllyUnitsAttacking= 9;
//    private static final int ID_TYPE_NAllyUnitsHarvesting= 10;
//    private static final int ID_TYPE_NAllyUnitsofType= 11;
//    private static final int ID_TYPE_NEnemyUnitsofType= 12;
    
    

    private static TableCommandsGenerator uniqueInstance;
    
    public static synchronized TableCommandsGenerator getInstance(UnitTypeTable utt){
        if(uniqueInstance == null){
            uniqueInstance = new TableCommandsGenerator(utt);
        }
        return uniqueInstance;
    }
    
    private TableCommandsGenerator(UnitTypeTable utt) {
        this.utt = utt;
        commands = new ArrayList<>();
        dicCommand = new HashMap<>();
        generateTable();
        generateDic();
    }

    public ICommand getCommandByID(int ID) {
        return dicCommand.get(ID);
    }

    public int getNumberOfCommands() {
        return dicCommand.keySet().size();
    }

    @Override
    public String toString() {
        /* String text = "";
        for (ICommand cmd : commands) {
            text += cmd.toString() + "\n";
        }
        return "TableCommandsGenerator " + text;
         */

        String text = "";

        for (Integer key : dicCommand.keySet()) {
            text += key + "-" + dicCommand.get(key).toString() + "\n";
        }

        return "TableCommandsGenerator " + text;
    }

    private void generateTable() {
        commands.clear();
        //harvest
        commands.addAll(getHarvestCommands());
        //build
        commands.addAll(getBuildCommands());
        //Train
        commands.addAll(getTrainCommands());
        //attack
        commands.addAll(getAttackCommands());
        //MoveToUnit
        commands.addAll(getMoveToUnit());
        //MoveToCoordenates
        commands.addAll(getMoveToCoordenates());
        //------------- booleans 
        ArrayList<ICommand> commandsBasic = new ArrayList<>(commands);
        //AllyRange
        commands.addAll(getAllyRangeCommands(commandsBasic));
        //DistanceFromEnemy
        commands.addAll(getDistanceFromEnemyCommands(commandsBasic));
        //EnemyRange
        commands.addAll(getEnemyRangeCommands(commandsBasic));
        //NAllyUnitsAttacking
        commands.addAll(getNAllyUnitsAttackingCommands(commandsBasic));
        //NAllyUnitsHarvesting
        commands.addAll(getNAllyUnitsHarvestingCommands(commandsBasic));
        //NAllyUnitsofType
        commands.addAll(getNAllyUnitsofTypeCommands(commandsBasic));
        //NEnemyUnitsofType
        commands.addAll(getNEnemyUnitsofTypeCommands(commandsBasic));
    }

    private Collection<? extends ICommand> getHarvestCommands() {
        ArrayList<ICommand> tCommandHarvest = new ArrayList<>();

        for (int i = 1; i <= MAX_QTD_WORKERS_HARVERST; i++) {
            HarvestBasic harverst = new HarvestBasic();
            harverst.addParameter(TypeConcrete.getTypeWorker()); //add unit type
            harverst.addParameter(new QuantityParam(i)); //add qtd unit
            tCommandHarvest.add(harverst);
        }

        return tCommandHarvest;
    }

    private Collection<? extends ICommand> getBuildCommands() {
        ArrayList<ICommand> tCommandBuild = new ArrayList<>();

        //build barracks
        for (int i = 0; i <= MAX_QTD_UNITS_TO_BUILD; i++) {
            BuildBasic build = new BuildBasic();
            build.addParameter(TypeConcrete.getTypeBarracks()); //add unit construct type
            build.addParameter(new QuantityParam(i)); //add qtd unit
            tCommandBuild.add(build);
        }

        //build bases
        for (int i = 0; i <= MAX_QTD_UNITS_TO_BUILD; i++) {
            BuildBasic build = new BuildBasic();
            build.addParameter(TypeConcrete.getTypeBase()); //add unit construct type
            build.addParameter(new QuantityParam(i)); //add qtd unit
            tCommandBuild.add(build);
        }
        return tCommandBuild;
    }

    private Collection<? extends ICommand> getTrainCommands() {
        ArrayList<ICommand> tCommandTrain = new ArrayList<>();
        //generate position permutation
        char v[] = {'0', '1', '2', '3'};
        //Permutation.clear();
        //Permutation.createPermutation(v, 4);
        //Permutation.imprime();
        //train using base
        for (int i = 1; i <= MAX_QTD_UNITS_TO_TRAIN; i++) {
            //for (int j = 0; j < Permutation.totalItens(); j++) {
                TrainBasic train = new TrainBasic();
                train.addParameter(TypeConcrete.getTypeBase()); //add unit construct type
                train.addParameter(TypeConcrete.getTypeWorker()); //add unit Type
                //train.addParameter(TypeConcrete.getTypeWorker()); //add unit Type
                train.addParameter(new QuantityParam(i)); //add qtd unit
                PriorityPositionParam pos = new PriorityPositionParam();
                //for (Integer position : Permutation.getPermutation(j)) {
                    pos.addPosition(EnumPositionType.byCode(4));
                //}

                train.addParameter(pos);
                tCommandTrain.add(train);
            //}
        }

        //train using barracks
        //light
        for (int i = 1; i <= MAX_QTD_UNITS_TO_TRAIN; i++) {
            //for (int j = 0; j < Permutation.totalItens(); j++) {
                TrainBasic train = new TrainBasic();
                train.addParameter(TypeConcrete.getTypeBarracks()); //add unit construct type
                train.addParameter(TypeConcrete.getTypeLight()); //add unit Type
                //train.addParameter(TypeConcrete.getTypeWorker()); //add unit Type
                train.addParameter(new QuantityParam(i)); //add qtd unit
                PriorityPositionParam pos = new PriorityPositionParam();
                //for (Integer position : Permutation.getPermutation(j)) {
                    pos.addPosition(EnumPositionType.byCode(4));
                //}

                train.addParameter(pos);
                tCommandTrain.add(train);
            //}
        }
        //ranged
        for (int i = 1; i <= MAX_QTD_UNITS_TO_TRAIN; i++) {
            //for (int j = 0; j < Permutation.totalItens(); j++) {
                TrainBasic train = new TrainBasic();
                train.addParameter(TypeConcrete.getTypeBarracks()); //add unit construct type
                train.addParameter(TypeConcrete.getTypeRanged()); //add unit Type
                //train.addParameter(TypeConcrete.getTypeWorker()); //add unit Type
                train.addParameter(new QuantityParam(i)); //add qtd unit
                PriorityPositionParam pos = new PriorityPositionParam();
                //for (Integer position : Permutation.getPermutation(j)) {
                    pos.addPosition(EnumPositionType.byCode(4));
                //}

                train.addParameter(pos);
                tCommandTrain.add(train);
            //}
        }
        //heavy
        for (int i = 1; i <= MAX_QTD_UNITS_TO_TRAIN; i++) {
            //for (int j = 0; j < Permutation.totalItens(); j++) {
                TrainBasic train = new TrainBasic();
                train.addParameter(TypeConcrete.getTypeBarracks()); //add unit construct type
                train.addParameter(TypeConcrete.getTypeRanged()); //add unit Type
                //train.addParameter(TypeConcrete.getTypeWorker()); //add unit Type
                train.addParameter(new QuantityParam(i)); //add qtd unit
                PriorityPositionParam pos = new PriorityPositionParam();
                //for (Integer position : Permutation.getPermutation(j)) {
                    pos.addPosition(EnumPositionType.byCode(4));
                //}

                train.addParameter(pos);
                tCommandTrain.add(train);
            //}
        }

        return tCommandTrain;
    }

    private Collection<? extends ICommand> getAttackCommands() {
        ArrayList<ICommand> tCommandAttack = new ArrayList<>();
        //attack just with workers
        for (int i = 0; i < 7; i++) {
            for (int j = 0; j < 5; j++) {
                AttackBasic attack = new AttackBasic();
                attack.addParameter(getTypeUnitByNumber(j)); //add unit type
                PlayerTargetParam pt = new PlayerTargetParam();
                pt.addPlayer(EnumPlayerTarget.Enemy);
                attack.addParameter(pt);
                attack.addParameter(getBehaviorByNumber(i)); //add behavior
                tCommandAttack.add(attack);
            }
        }

        return tCommandAttack;
    }

    private IParameters getBehaviorByNumber(int i) {
        switch (i) {
            case 0:
                return new ClosestEnemy();
            case 1:
                return new FarthestEnemy();
            case 2:
                return new LessHealthyEnemy();
            case 3:
                return new MostHealthyEnemy();
            case 4:
                return new StrongestEnemy();
            case 5:
                return new WeakestEnemy();
            default:
                return new RandomEnemy();

        }
    }

    private IParameters getTypeUnitByNumber(int j) {
        switch (j) {
            case 0:
                return TypeConcrete.getTypeWorker();
            case 1:
                return TypeConcrete.getTypeLight();
            case 2:
                return TypeConcrete.getTypeRanged();
            case 3:
                return TypeConcrete.getTypeHeavy();
            default:
                return TypeConcrete.getTypeUnits();

        }
    }

    private Collection<? extends ICommand> getMoveToCoordenates() {
        ArrayList<ICommand> tCommandMove = new ArrayList<>();
        for (int x = 0; x < MAP_SIZE; x++) {
            for (int y = 0; y < MAP_SIZE; y++) {
                for (int u = 0; u < 5; u++) {
                    MoveToCoordinatesBasic moveToCoordinates = new MoveToCoordinatesBasic();
                    moveToCoordinates.addParameter(new CoordinatesParam(x, y)); //add unit type
                    moveToCoordinates.addParameter(getTypeUnitByNumber(u));
                    tCommandMove.add(moveToCoordinates);
                }
            }

        }

        return tCommandMove;
    }

    private Collection<? extends ICommand> getMoveToUnit() {
        ArrayList<ICommand> tCommandMove = new ArrayList<>();
        for (int u = 0; u < 5; u++) {
            for (int p = 0; p < 2; p++) {
                for (int i = 0; i < 7; i++) {
                    //
                    MoveToUnitBasic moveToUnit = new MoveToUnitBasic();
                    moveToUnit.addParameter(getTypeUnitByNumber(u)); //add unit type
                    PlayerTargetParam pt = new PlayerTargetParam();
                    pt.addPlayer(getPlayerTargetByNumber(p));
                    moveToUnit.addParameter(pt);
                    moveToUnit.addParameter(getBehaviorByNumber(i)); //add behavior
                    //
                    tCommandMove.add(moveToUnit);
                }
            }
        }

        return tCommandMove;
    }

    private EnumPlayerTarget getPlayerTargetByNumber(int p) {
        if (p == 0) {
            return EnumPlayerTarget.Ally;
        }
        return EnumPlayerTarget.Enemy;
    }

    private void generateDic() {
    	bagofTypes= new ArrayList<ArrayList<Integer>>();
    	correspondenceofTypes=new HashMap<>();
    	separateTypes();
    	initializeBagOfTypes();    	
        int cont = 0;
        for (ICommand command : commands) {
            dicCommand.put(cont, command);
            addIdToBagofTypes(cont, command);
            cont++;
        }
        commands.clear();
    }

    private Collection<? extends ICommand> getAllyRangeCommands(ArrayList<ICommand> commandsBasic) {
        ArrayList<ICommand> tCommandAllyRange = new ArrayList<>();

        for (ICommand iCommand : commandsBasic) {
            for (int u = 0; u < 5; u++) { //type units
                ArrayList<ICommand> commandsforBoolean = new ArrayList<>();
                commandsforBoolean.add(iCommand);
                AllyRange allyRangeBoolean = new AllyRange(commandsforBoolean);
                allyRangeBoolean.addParameter(getTypeUnitByNumber(u));

                tCommandAllyRange.add(allyRangeBoolean);
            }

        }
        return tCommandAllyRange;
    }

    private Collection<? extends ICommand> getDistanceFromEnemyCommands(ArrayList<ICommand> commandsBasic) {
        ArrayList<ICommand> tCommandDistance = new ArrayList<>();
        for (ICommand iCommand : commandsBasic) {
            for (int u = 0; u < 5; u++) { //type units
                for (int x = 0; x < MAP_SIZE; x++) {
                    ArrayList<ICommand> commandsforBoolean = new ArrayList<>();
                    commandsforBoolean.add(iCommand);
                    DistanceFromEnemy distanceFromEnemyBoolean = new DistanceFromEnemy(commandsforBoolean);
                    distanceFromEnemyBoolean.addParameter(getTypeUnitByNumber(u));
                    distanceFromEnemyBoolean.addParameter(new DistanceParam(x));

                    tCommandDistance.add(distanceFromEnemyBoolean);
                }
            }

        }

        return tCommandDistance;
    }

    private Collection<? extends ICommand> getEnemyRangeCommands(ArrayList<ICommand> commandsBasic) {
        ArrayList<ICommand> tCommandEnRange = new ArrayList<>();
        for (ICommand iCommand : commandsBasic) {
            for (int u = 0; u < 5; u++) { //type units
                ArrayList<ICommand> commandsforBoolean = new ArrayList<>();
                commandsforBoolean.add(iCommand);
                EnemyRange enemyRangeBoolean = new EnemyRange(commandsforBoolean);
                enemyRangeBoolean.addParameter(getTypeUnitByNumber(u));

                tCommandEnRange.add(enemyRangeBoolean);
            }

        }

        return tCommandEnRange;
    }

    private Collection<? extends ICommand> getNAllyUnitsAttackingCommands(ArrayList<ICommand> commandsBasic) {
        ArrayList<ICommand> tCommandNaAlly = new ArrayList<>();
        for (ICommand iCommand : commandsBasic) {
            for (int u = 0; u < 5; u++) { //type units
                for (int q = 0; q < MAX_QTD_NAllyUnitsAttacking; q++) { //type units

                    ArrayList<ICommand> commandsforBoolean = new ArrayList<>();
                    commandsforBoolean.add(iCommand);

                    NAllyUnitsAttacking nAllyAttackingBoolean = new NAllyUnitsAttacking(commandsforBoolean);
                    nAllyAttackingBoolean.addParameter(getTypeUnitByNumber(u));
                    nAllyAttackingBoolean.addParameter(new QuantityParam(q));

                    tCommandNaAlly.add(nAllyAttackingBoolean);
                }
            }

        }

        return tCommandNaAlly;
    }

    private Collection<? extends ICommand> getNAllyUnitsHarvestingCommands(ArrayList<ICommand> commandsBasic) {
        //NAllyUnitsHarvesting
        ArrayList<ICommand> tCommandNaHarvest = new ArrayList<>();
        for (ICommand iCommand : commandsBasic) {
            for (int q = 0; q < MAX_QTD_WORKERS_HARVERST; q++) { //type units
                ArrayList<ICommand> commandsforBoolean = new ArrayList<>();
                commandsforBoolean.add(iCommand);

                NAllyUnitsHarvesting nAllyUnitsHarvesting = new NAllyUnitsHarvesting(commandsforBoolean);
                nAllyUnitsHarvesting.addParameter(new QuantityParam(q));

                tCommandNaHarvest.add(nAllyUnitsHarvesting);
            }
        }

        return tCommandNaHarvest;
    }

    private Collection<? extends ICommand> getNAllyUnitsofTypeCommands(ArrayList<ICommand> commandsBasic) {
        ArrayList<ICommand> tCommandNAllyUnitsofType = new ArrayList<>();
        for (ICommand iCommand : commandsBasic) {
            for (int u = 0; u < 5; u++) { //type units
                for (int q = 0; q < MAX_QTD_NAllyUnitsAttacking; q++) { //type units

                    ArrayList<ICommand> commandsforBoolean = new ArrayList<>();
                    commandsforBoolean.add(iCommand);

                    NAllyUnitsofType nAllyUnitsofType = new NAllyUnitsofType(commandsforBoolean);
                    nAllyUnitsofType.addParameter(getTypeUnitByNumber(u));
                    nAllyUnitsofType.addParameter(new QuantityParam(q));

                    tCommandNAllyUnitsofType.add(nAllyUnitsofType);
                }
            }

        }

        return tCommandNAllyUnitsofType;
    }

    private Collection<? extends ICommand> getNEnemyUnitsofTypeCommands(ArrayList<ICommand> commandsBasic) {
        ArrayList<ICommand> tCommandNEnemyUnitsofType = new ArrayList<>();
        for (ICommand iCommand : commandsBasic) {
            for (int u = 0; u < 5; u++) { //type units
                for (int q = 0; q < MAX_QTD_NAllyUnitsAttacking; q++) { //type units

                    ArrayList<ICommand> commandsforBoolean = new ArrayList<>();
                    commandsforBoolean.add(iCommand);

                    NEnemyUnitsofType nEnemyUnitsofType = new NEnemyUnitsofType(commandsforBoolean);
                    nEnemyUnitsofType.addParameter(getTypeUnitByNumber(u));
                    nEnemyUnitsofType.addParameter(new QuantityParam(q));

                    tCommandNEnemyUnitsofType.add(nEnemyUnitsofType);
                }
            }

        }

        return tCommandNEnemyUnitsofType;
    }
    
    /**
	 * @return the numberTypes
	 */
	public int getNumberTypes() {
		return differentTypes.size();
	}

	/**
	 * @return the bagofTypes
	 */
	public ArrayList<ArrayList<Integer>> getBagofTypes() {
		return bagofTypes;
	}

	/**
	 * @return the correspondenceofTypes
	 */
	public HashMap<Integer, Integer> getCorrespondenceofTypes() {
		return correspondenceofTypes;
	}

	private void initializeBagOfTypes()
    {
    	for(int i=0; i< differentTypes.size(); i++)
    	{
    		bagofTypes.add(new ArrayList<Integer>());
    	}
    }
	
	private void separateTypes()
	{
		differentTypes=new ArrayList<String>();
        for (ICommand command : commands) {
        	String s = command.toString();        	
        	s = s.substring(s.indexOf("{") + 1);
        	s = s.substring(0, s.indexOf(":"));
        	if(!differentTypes.contains(s))
        		differentTypes.add(s);
        }
	}
    
    private void addIdToBagofTypes(int idCommand, ICommand com) {  	
    	
    	String s = com.toString();
    	
    	s = s.substring(s.indexOf("{") + 1);
    	s = s.substring(0, s.indexOf(":"));
    	
    	for(int i=0;i<differentTypes.size();i++)
    	{
    		if(differentTypes.get(i).equals(s))
    		{
    			bagofTypes.get(i).add(idCommand);
    			correspondenceofTypes.put(idCommand, i);
    			break;
    		}
    			
    	}
    	
    	//System.out.println("String s "+correspondenceofTypes.size());
    	
    }
}
