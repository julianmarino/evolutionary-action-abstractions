package ai.configurablescript;

import ai.abstraction.pathfinding.AStarPathFinding;
//import ai.abstraction.pathfinding.FloodFillPathFinding;
import java.util.ArrayList;
import java.util.Collection;

import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;

import ai.abstraction.pathfinding.PathFinding;
import ai.core.ParameterSpecification;
import java.util.Map;
import rts.GameState;
import rts.PhysicalGameState;
import rts.Player;
import rts.PlayerAction;
import rts.units.Unit;
import rts.units.UnitType;
import rts.units.UnitTypeTable;

enum BasicExpandedChoicePoint{UNITTYPE, STRATEGY, NBASESEXPAND, NBARRACKSEXPAND, NUNITSATTACK, FORMATIONRADIUS, REACTIVERADIUS, DEFENSERADIOUS, NUNITSHARVEST };


public class BasicExpandedConfigurableScript extends ConfigurableScript<BasicExpandedChoicePoint> {


	Random r = new Random();
	UnitTypeTable utt;

	UnitType workerType;
	UnitType lightType;
	UnitType heavyType;
	UnitType rangedType;
	UnitType resourceType;

	UnitType baseType;
	UnitType barracksType;

	int resourcesUsed;
	int nbases;
	int nbarracks;
	int nresources;
	int ownresources;
	int abandonedbases;
	int freeresources;
	int nworkers;
	int nmelees;
	int nlight;
	int nheavy;
	int nranged;
	private static final int BASE_RESOURCE_RADIUS = 8;

	Parameters parametersValues;
	
	int strategy; 
	int nBasesExpand; 
	int nBarracksExpand; 
	int nUnitsAttack; 
	int formationRadius; 
	int defenseRadius; 
	int reactiveRadius; 
	int nUnitsHarvest;
	int nUnitType;


	public BasicExpandedConfigurableScript(UnitTypeTable a_utt) {
		this(a_utt, new AStarPathFinding());
	}


	public BasicExpandedConfigurableScript(UnitTypeTable a_utt, PathFinding a_pf) {
		super(a_pf);
		utt = a_utt;

		parametersValues=new Parameters(a_utt);
		workerType = utt.getUnitType("Worker");
		lightType = utt.getUnitType("Light");
		heavyType = utt.getUnitType("Heavy");
		rangedType = utt.getUnitType("Ranged");
		resourceType = utt.getUnitType("Resource");
		baseType = utt.getUnitType("Base");
		barracksType = utt.getUnitType("Barracks");       

		choicePoints = new EnumMap<BasicExpandedChoicePoint,Options>(BasicExpandedChoicePoint.class);
		choices = new EnumMap<BasicExpandedChoicePoint,Integer>(BasicExpandedChoicePoint.class);
		choicePointValues = BasicExpandedChoicePoint.values();
		reset();
	}

	public BasicExpandedConfigurableScript(UnitTypeTable a_utt, PathFinding a_pf, 
			int strategy, int nBasesExpand, int nBarracksExpand, int nUnitsAttack, int formationRadius, 
			int defenseRadius, int reactiveRadius, int nUnitsHarvest,int nUnitType ) {
		super(a_pf);
		utt = a_utt;
		
		this.strategy=strategy; 
		this.nBasesExpand=nBasesExpand; 
		this.nBarracksExpand=nBarracksExpand; 
		this.nUnitsAttack=nUnitsAttack; 
		this.formationRadius=formationRadius; 
		this.defenseRadius=defenseRadius; 
		this.reactiveRadius=reactiveRadius; 
		this.nUnitsHarvest=nUnitsHarvest;
		this.nUnitType=nUnitType;

		parametersValues=new Parameters(a_utt);
		workerType = utt.getUnitType("Worker");
		lightType = utt.getUnitType("Light");
		heavyType = utt.getUnitType("Heavy");
		rangedType = utt.getUnitType("Ranged");
		resourceType = utt.getUnitType("Resource");
		baseType = utt.getUnitType("Base");
		barracksType = utt.getUnitType("Barracks");       

		//choicePoints = new EnumMap<BasicExpandedChoicePoint,Options>(BasicExpandedChoicePoint.class);
		choices = new EnumMap<BasicExpandedChoicePoint,Integer>(BasicExpandedChoicePoint.class);
		//choicePointValues = BasicExpandedChoicePoint.values();
		//reset();
		choices.put(BasicExpandedChoicePoint.UNITTYPE, nUnitType);
		choices.put(BasicExpandedChoicePoint.STRATEGY, strategy);
		choices.put(BasicExpandedChoicePoint.NBASESEXPAND, nBasesExpand);
		choices.put(BasicExpandedChoicePoint.NBARRACKSEXPAND, nBarracksExpand);
		choices.put(BasicExpandedChoicePoint.NUNITSATTACK, nUnitsAttack);
		choices.put(BasicExpandedChoicePoint.FORMATIONRADIUS, formationRadius);
		choices.put(BasicExpandedChoicePoint.REACTIVERADIUS, reactiveRadius);
		choices.put(BasicExpandedChoicePoint.DEFENSERADIOUS, defenseRadius);
		choices.put(BasicExpandedChoicePoint.NUNITSHARVEST, nUnitsHarvest);
	}

    @Override
	public ConfigurableScript<BasicExpandedChoicePoint> clone() {
		BasicExpandedConfigurableScript sc = new BasicExpandedConfigurableScript(utt, pf);
		sc.choices=choices.clone();
		//sc.choicePoints=choicePoints.clone();
		//sc.choicePointValues=choicePointValues.clone();
		return sc;
	}

	/*
        This is the main function of the AI. It is called at each game cycle with the most up to date game state and
        returns which actions the AI wants to execute in this cycle.
        The input parameters are:
        - player: the player that the AI controls (0 or 1)
        - gs: the current game state
        This method returns the actions to be sent to each of the units in the gamestate controlled by the player,
        packaged as a PlayerAction.
	 */
    @Override
	public PlayerAction getAction(int player, GameState gs) {
		PhysicalGameState pgs = gs.getPhysicalGameState();
		Player p = gs.getPlayer(player);
		resourcesUsed=gs.getResourceUsage().getResourcesUsed(player); 
		nworkers=0;
		nbases = 0;
		nbarracks = 0;
		nresources = 0;
		ownresources = 0;
		abandonedbases = 0;
		freeresources = 0;
		nmelees=0;
		nlight=0;
		nheavy=0;
		nranged=0;

		for (Unit u2 : pgs.getUnits()) {
			if (u2.getType() == workerType
					&& u2.getPlayer() == p.getID()) {
				nworkers++;
			}
		}

		for (Unit u2 : pgs.getUnits()) {
			if (u2.getType().canAttack && !u2.getType().canHarvest
					&& u2.getPlayer() == p.getID()) {
				nmelees++;
				if(u2.getType()==utt.getUnitType("Light"))
				{
					nlight++;
				}else if(u2.getType()==utt.getUnitType("Heavy"))
				{
					nheavy++;
				}else if(u2.getType()==utt.getUnitType("Ranged"))
				{
					nranged++;
				}
			}
		}

		for (Unit u2 : pgs.getUnits()) {
			if (u2.getType() == baseType
					&& u2.getPlayer() == p.getID()) {
				nbases++;
				if(!pgs.getUnitsAround(u2.getX(), u2.getY(), BASE_RESOURCE_RADIUS).stream()
						.map((a)->a.getType()==resourceType)
						.reduce((a,b)->a||b).get()){
					abandonedbases++;
				}
			}
			if (u2.getType() == barracksType
					&& u2.getPlayer() == p.getID()) {
				nbarracks++;
			}
			if(u2.getType() == resourceType){
				nresources++;
				if(pgs.getUnitsAround(u2.getX(), u2.getY(), BASE_RESOURCE_RADIUS).stream()
						.map((a)->a.getPlayer()==p.getID()&&a.getType()==baseType)
						.reduce((a,b)->a||b).get()){
					ownresources++;
				}
				if(!pgs.getUnitsAround(u2.getX(), u2.getY(), BASE_RESOURCE_RADIUS).stream()
						.map((a)->a.getPlayer()!=(1-p.getID())&&a.getType()!=baseType)
						.reduce((a,b)->a&&b).get()){
					freeresources++;
				}
			}
		}
		//        System.out.println(nbases+" "+abandonedbases+" "+ownresources);

		if ((choices.get(BasicExpandedChoicePoint.STRATEGY)==parametersValues.getRushStrategy())) 
		{
			behaviourRush(this,pgs,p,gs,player);

		}else if ((choices.get(BasicExpandedChoicePoint.STRATEGY)==parametersValues.getDefenseStrategy())) 
		{
			behaviourDefense(this,pgs,p,gs,player);

		}
		else if ((choices.get(BasicExpandedChoicePoint.STRATEGY)==parametersValues.getEconomyRushSimpleStrategy())) 
		{
			behaviourEconomyRushSimple(this,pgs,p,gs,player);

		}
		// This method simply takes all the unit actions executed so far, and packages them into a PlayerAction
		return translateActions(player, gs);
	}






	@Override
	public Collection<Options> getApplicableChoicePoints(int player, GameState gs) {

		List<Options> choices=new ArrayList<Options>();


		//		choices.add(new Options(BasicExpandedChoicePoint.UNITTYPE.ordinal(),new int[]{
		//				parametersValues.getWorker(),
		//				parametersValues.getLight(),
		//				parametersValues.getRanged(),
		//				parametersValues.getHeavy(),
		//				parametersValues.getWorkerLight(),
		//				parametersValues.getWorkerHeavy(),
		//				parametersValues.getWorkerRanged(),
		//				parametersValues.getLightHeavy(),
		//				parametersValues.getLightRanged(),
		//				parametersValues.getHeavyRanged(),
		//				parametersValues.getWorkerLightHeavy(),
		//				parametersValues.getWorkerLightRanged(),
		//				parametersValues.getWorkerHeavyRanged(),
		//				parametersValues.getLightHeavyRanged(),
		//				parametersValues.getAllTypesUnits(),
		//		}));
		//
		//		choices.add(new Options(BasicExpandedChoicePoint.NBASESEXPAND.ordinal(),new int[]{
		//				parametersValues.getnBasesExpand()}));
		//		
		//		choices.add(new Options(BasicExpandedChoicePoint.NBARRACKSEXPAND.ordinal(),new int[]{
		//				parametersValues.getnBarracksExpand()}));
		//		
		//		choices.add(new Options(BasicExpandedChoicePoint.STRATEGY.ordinal(),new int[]{
		//				parametersValues.getRushStrategy(),
		//				parametersValues.getDefenseStrategy(),
		//				parametersValues.getEconomyRushSimpleStrategy()}));
		//		
		//		choices.add(new Options(BasicExpandedChoicePoint.NUNITSATTACK.ordinal(),new int[]{
		//				parametersValues.getNunitsAttack()}));
		//		
		//		choices.add(new Options(BasicExpandedChoicePoint.FORMATIONRADIUS.ordinal(),new int[]{
		//				parametersValues.getFormationRadius()}));
		//		
		//		choices.add(new Options(BasicExpandedChoicePoint.REACTIVERADIUS.ordinal(),new int[]{
		//				parametersValues.getReactiveRadius()}));
		//		
		//		choices.add(new Options(BasicExpandedChoicePoint.DEFENSERADIOUS.ordinal(),new int[]{
		//				parametersValues.getDefenseRadius()}));
		//		
		//		choices.add(new Options(BasicExpandedChoicePoint.NUNITSHARVEST.ordinal(),new int[]{
		//				parametersValues.getNunitsHarvest()}));

		return choices;
	}

	@Override
	public void initializeChoices() {
		//		for(BasicExpandedChoicePoint c:choicePointValues){
		//			switch(c){
		//			case UNITTYPE:
		//				choicePoints.put(c, new Options(c.ordinal(),new int[]{
		//						parametersValues.getWorker(),
		//						parametersValues.getLight(),
		//						parametersValues.getRanged(),
		//						parametersValues.getHeavy(),
		//						parametersValues.getWorkerLight(),
		//						parametersValues.getWorkerHeavy(),
		//						parametersValues.getWorkerRanged(),
		//						parametersValues.getLightHeavy(),
		//						parametersValues.getLightRanged(),
		//						parametersValues.getHeavyRanged(),
		//						parametersValues.getWorkerLightHeavy(),
		//						parametersValues.getWorkerLightRanged(),
		//						parametersValues.getWorkerHeavyRanged(),
		//						parametersValues.getLightHeavyRanged(),
		//						parametersValues.getAllTypesUnits()}));
		//				break;
		//			case NBASESEXPAND:
		//				choicePoints.put(c, new Options(c.ordinal(),new int[]{
		//						parametersValues.getnBasesExpand()}));
		//				break;
		//			case NBARRACKSEXPAND:
		//				choicePoints.put(c, new Options(c.ordinal(),new int[]{
		//						parametersValues.getnBarracksExpand()}));
		//				break;
		//			case STRATEGY:
		//				choicePoints.put(c, new Options(c.ordinal(),new int[]{
		//						parametersValues.getRushStrategy(),
		//						parametersValues.getDefenseStrategy(),
		//						parametersValues.getEconomyRushSimpleStrategy()}));
		//				break;
		//			case NUNITSATTACK:
		//				choicePoints.put(c, new Options(c.ordinal(),new int[]{
		//						parametersValues.getNunitsAttack()}));
		//				break;
		//				
		//			case FORMATIONRADIUS:
		//				choicePoints.put(c, new Options(c.ordinal(),new int[]{
		//						parametersValues.getFormationRadius()}));
		//				break;
		//				
		//			case REACTIVERADIUS:
		//				choicePoints.put(c, new Options(c.ordinal(),new int[]{
		//						parametersValues.getReactiveRadius()}));
		//				break;
		//				
		//			case DEFENSERADIOUS:
		//				choicePoints.put(c, new Options(c.ordinal(),new int[]{
		//						parametersValues.getDefenseRadius()}));
		//				break;
		//				
		//			case NUNITSHARVEST:
		//				choicePoints.put(c, new Options(c.ordinal(),new int[]{
		//						parametersValues.getNunitsHarvest()}));
		//				break;
		//			}
		//		}

	}

    @Override
	public String toString(){
		/*String str = getClass().getSimpleName() + "(";
		for(BasicExpandedChoicePoint c:BasicExpandedChoicePoint.values()){
			str+=c.toString()+",";
		}
		return str+")";                */
                
                
            String str = getClass().getSimpleName() + "(";
                
            EnumMap<BasicExpandedChoicePoint,Integer> choicesTemp =  choices;
            for (Map.Entry<BasicExpandedChoicePoint, Integer> entry : choicesTemp.entrySet()) {
                BasicExpandedChoicePoint key = entry.getKey();
                Integer value = entry.getValue();
                str+=value+",";
                //str+=key+" "+value+",";
            }
                
                
                return str+")";
	}


	@Override
	public List<ParameterSpecification> getParameters() {
		List<ParameterSpecification> parameters = new ArrayList<>();

		parameters.add(new ParameterSpecification("PathFinding", PathFinding.class, new AStarPathFinding()));

		return parameters;
	}       
    
	public void behaviourRush(BasicExpandedConfigurableScript bec,PhysicalGameState pgs, Player p, GameState gs, int player)
	{
		new BehaviorRush(bec, pgs, p, gs, player);
	}
    
	public void behaviourDefense(BasicExpandedConfigurableScript bec,PhysicalGameState pgs, Player p, GameState gs, int player)
	{
		new BehaviorDefense(bec, pgs, p, gs, player);
	}
    
	public void behaviourEconomyRushSimple(BasicExpandedConfigurableScript bec,PhysicalGameState pgs, Player p, GameState gs, int player)
	{
		new BehaviorEconomyRushSimple(bec, pgs, p, gs, player);
	}
    
	public String getParametersScript()
	{
		return 	"strategy "+strategy+" nBasesExpand "+nBasesExpand+" nBarracksExpand "+nBarracksExpand+" nUnitsAttack "+nUnitsAttack+" formationRadius "+formationRadius+" defenseRadius "+defenseRadius+" reactiveRadius "+reactiveRadius+" nUnitsHarvest "+nUnitsHarvest+" nUnitType "+nUnitType;
	}
    
	public int getUnitType()
	{
		return nUnitType;
	}
}
