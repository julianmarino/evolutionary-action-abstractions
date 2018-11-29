package ai.configurablescript;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ai.abstraction.pathfinding.AStarPathFinding;
import ai.abstraction.pathfinding.FloodFillPathFinding;
import ai.abstraction.pathfinding.PathFinding;
import ai.evaluation.EvaluationFunction;
import ai.evaluation.SimpleEvaluationFunction;
import rts.units.UnitTypeTable;


public class ScriptsCreator {

	ArrayList<BasicExpandedConfigurableScript> scriptsCompleteSet;
	ArrayList<BasicExpandedConfigurableScript> scriptsMixSet;
	ArrayList<BasicExpandedConfigurableScript> scriptsMixReducedSet;


	int tamMixSet;

	List <Integer> unitType;
	List <Integer> strategy;
	List <Integer> nBasesExpand;
	List <Integer> nBarracksExpand;
	List <Integer> nUnitsAttack;
	List <Integer> formationRadius;
	List <Integer> reactiveRadius;
	List <Integer> defenseRadius;
	List <Integer> nUnitsHarvest;

	UnitTypeTable utt;
	Parameters p;
	int debug =0;
	int numScripts=300;

	public ScriptsCreator(UnitTypeTable utt, int tamMixSet ){

		this.utt=utt;
		scriptsCompleteSet=new ArrayList<BasicExpandedConfigurableScript>();
		scriptsMixSet=new ArrayList<BasicExpandedConfigurableScript>();
		scriptsMixReducedSet=new ArrayList<BasicExpandedConfigurableScript>();
		this.tamMixSet=tamMixSet;

		p=new Parameters(utt);

		//closed parameters:
//		unitType= Arrays.asList(p.getWorker(),p.getLight(),p.getHeavy(),p.getRanged(),p.getWorkerLight(),
//				p.getWorkerHeavy(),p.getWorkerRanged(),p.getLightHeavy(),p.getLightRanged(),p.getHeavyRanged(),
//				p.getWorkerLightHeavy(),p.getWorkerLightRanged(),p.getWorkerHeavyRanged(),p.getLightHeavyRanged(),
//				p.getAllTypesUnits());

		unitType= Arrays.asList(p.getWorker(),p.getLight(),p.getHeavy(),p.getRanged(),
				p.getLightHeavy(),p.getLightRanged(),p.getHeavyRanged(), p.getLightHeavyRanged());
		
		strategy= Arrays.asList(p.getRushStrategy(),p.getDefenseStrategy(),p.getEconomyRushSimpleStrategy());					

		rushesInitializator();
		defenseInitializator();
		economyInitializator();
	}


	private void rushesInitializator() {

		int sizeRushitems=0;
		//Open Parameters for 8x8
		
//		nBasesExpand =Arrays.asList(0,1);
//		nBarracksExpand=Arrays.asList(0,1);
//		nUnitsAttack=Arrays.asList(1,2,3,4,5);
//		formationRadius=Arrays.asList(2,3);
//		reactiveRadius=Arrays.asList(2,3);
//		defenseRadius=Arrays.asList(-1);
//		nUnitsHarvest=Arrays.asList(-1);
		
		//parameters for 24x24
		
		nBasesExpand =Arrays.asList(0);
		nBarracksExpand=Arrays.asList(0,1);
		nUnitsAttack=Arrays.asList(1,5,7);
		formationRadius=Arrays.asList(2,4);
		reactiveRadius=Arrays.asList(2,4);
		defenseRadius=Arrays.asList(-1);
		nUnitsHarvest=Arrays.asList(-1);

		int strategyItem=p.getRushStrategy();

		for (Integer defenseRadiusItem : defenseRadius) {
			for (Integer nUnitsHarvestItem : nUnitsHarvest) {
				for (Integer reactiveRadiusItem : reactiveRadius) {
					for (Integer formationRadiusItem : formationRadius) {
						for (Integer nBarracksExpandItem : nBarracksExpand) {
							for (Integer nBasesExpandItem : nBasesExpand) {
								for (Integer nUnitsAttackItem : nUnitsAttack) {
									for (Integer unitTypeItem : unitType) {

										if(debug>1)
										{
											System.out.println("configuration Rushes "+unitTypeItem+" "+nUnitsAttackItem+" "+
													formationRadiusItem+" "+reactiveRadiusItem+" "+nBasesExpandItem+" "+
													nBarracksExpandItem+" "+nUnitsHarvestItem+" "+defenseRadiusItem);
										}
										scriptsCompleteSet.add(new BasicExpandedConfigurableScript(utt, getPathFinding(),
												strategyItem,nBasesExpandItem,nBarracksExpandItem,nUnitsAttackItem,formationRadiusItem,
												defenseRadiusItem,reactiveRadiusItem,nUnitsHarvestItem,unitTypeItem));
										
//										if(scriptsMixSet.size()<(379))
//										{
//											scriptsMixSet.add(new BasicExpandedConfigurableScript(utt, getPathFinding(),
//												strategyItem,nBasesExpandItem,nBarracksExpandItem,nUnitsAttackItem,formationRadiusItem,
//												defenseRadiusItem,reactiveRadiusItem,nUnitsHarvestItem,unitTypeItem));
//										}
										
										if(scriptsMixReducedSet.size()<(tamMixSet/3))
										{
											scriptsMixReducedSet.add(new BasicExpandedConfigurableScript(utt, getPathFinding(),
												strategyItem,nBasesExpandItem,nBarracksExpandItem,nUnitsAttackItem,formationRadiusItem,
												defenseRadiusItem,reactiveRadiusItem,nUnitsHarvestItem,unitTypeItem));
										}
										sizeRushitems++;
									}  
								}  
							}  
						}   
					}   
				}
			}
			if(debug>0)
			{
				System.out.println("size rush set "+sizeRushitems);
			}
		}

	}

	private void defenseInitializator() {

		int sizeDefenseitems=0;
		//Open Parameters 8x8
//		nBasesExpand =Arrays.asList(0,1);
//		nBarracksExpand=Arrays.asList(0,1);
//		nUnitsAttack=Arrays.asList(-1);
//		formationRadius=Arrays.asList(-1);
//		reactiveRadius=Arrays.asList(2,3);
//		defenseRadius=Arrays.asList(2,3,4);
//		nUnitsHarvest=Arrays.asList(-1);	

		//parameters for 24x24
		
		nBasesExpand =Arrays.asList(0,1);
		nBarracksExpand=Arrays.asList(0,1);
		nUnitsAttack=Arrays.asList(-1);
		formationRadius=Arrays.asList(-1);
		reactiveRadius=Arrays.asList(2,4);
		defenseRadius=Arrays.asList(2,4);
		nUnitsHarvest=Arrays.asList(-1);
		
		int strategyItem=p.getDefenseStrategy();

		for (Integer nUnitsHarvestItem : nUnitsHarvest) {
			for (Integer formationRadiusItem : formationRadius) {
				for (Integer nUnitsAttackItem : nUnitsAttack) {
					for (Integer reactiveRadiusItem : reactiveRadius) {
						for (Integer nBarracksExpandItem : nBarracksExpand) {
							for (Integer nBasesExpandItem : nBasesExpand) {
								for (Integer defenseRadiusItem : defenseRadius) {
									for (Integer unitTypeItem : unitType) { 

										if(debug>1)
										{
											System.out.println("configuration Defenses "+unitTypeItem+" "+nUnitsAttackItem+" "+
													formationRadiusItem+" "+reactiveRadiusItem+" "+nBasesExpandItem+" "+
													nBarracksExpandItem+" "+nUnitsHarvestItem+" "+defenseRadiusItem);
										}
										scriptsCompleteSet.add(new BasicExpandedConfigurableScript(utt, getPathFinding(),
												strategyItem,nBasesExpandItem,nBarracksExpandItem,nUnitsAttackItem,formationRadiusItem,
												defenseRadiusItem,reactiveRadiusItem,nUnitsHarvestItem,unitTypeItem));
//										
//										if(scriptsMixSet.size()<(tamMixSet/3)*2)
//										{
//											scriptsMixSet.add(new BasicExpandedConfigurableScript(utt, getPathFinding(),
//												strategyItem,nBasesExpandItem,nBarracksExpandItem,nUnitsAttackItem,formationRadiusItem,
//												defenseRadiusItem,reactiveRadiusItem,nUnitsHarvestItem,unitTypeItem));
//										}
										
										if(scriptsMixReducedSet.size()<(tamMixSet/3)*2)
										{
											scriptsMixReducedSet.add(new BasicExpandedConfigurableScript(utt, getPathFinding(),
												strategyItem,nBasesExpandItem,nBarracksExpandItem,nUnitsAttackItem,formationRadiusItem,
												defenseRadiusItem,reactiveRadiusItem,nUnitsHarvestItem,unitTypeItem));
										}
										
										sizeDefenseitems++;
									}  
								}  
							}  
						}   
					}   
				}
			}
			if(debug>0)
			{
				System.out.println("size defense set "+sizeDefenseitems);
			}
		}

	}

	private void economyInitializator() {

		int sizeEconomyitems=0;
		//Open Parameters
//		nBasesExpand =Arrays.asList(0,1);
//		nBarracksExpand=Arrays.asList(0,1);
//		nUnitsAttack=Arrays.asList(1,2,3,4,5);
//		formationRadius=Arrays.asList(2,3);
//		reactiveRadius=Arrays.asList(2,3);
//		defenseRadius=Arrays.asList(-1);
//		nUnitsHarvest=Arrays.asList(1,2,3,4,5);		
		
		//parameters for 24x24
		
		nBasesExpand =Arrays.asList(0);
		nBarracksExpand=Arrays.asList(0,1);
		nUnitsAttack=Arrays.asList(1,5);
		formationRadius=Arrays.asList(4);
		reactiveRadius=Arrays.asList(4);
		defenseRadius=Arrays.asList(-1);
		nUnitsHarvest=Arrays.asList(2,3,4,5);

		int strategyItem=p.getEconomyRushSimpleStrategy();

		for (Integer defenseRadiusItem : defenseRadius) {
			for (Integer reactiveRadiusItem : reactiveRadius) {
				for (Integer formationRadiusItem : formationRadius) {
					for (Integer nBarracksExpandItem : nBarracksExpand) {
						for (Integer nBasesExpandItem : nBasesExpand) {
							for (Integer nUnitsAttackItem : nUnitsAttack) {
								for (Integer nUnitsHarvestItem : nUnitsHarvest) {
									for (Integer unitTypeItem : unitType) {

										if(debug>1)
										{
											System.out.println("configuration Economys "+unitTypeItem+" "+nUnitsAttackItem+" "+
													formationRadiusItem+" "+reactiveRadiusItem+" "+nBasesExpandItem+" "+
													nBarracksExpandItem+" "+nUnitsHarvestItem+" "+defenseRadiusItem);
										}
										scriptsCompleteSet.add(new BasicExpandedConfigurableScript(utt, getPathFinding(),
												strategyItem,nBasesExpandItem,nBarracksExpandItem,nUnitsAttackItem,formationRadiusItem,
												defenseRadiusItem,reactiveRadiusItem,nUnitsHarvestItem,unitTypeItem));
//										
//										if(scriptsMixSet.size()<tamMixSet)
//										{
//											scriptsMixSet.add(new BasicExpandedConfigurableScript(utt, getPathFinding(),
//												strategyItem,nBasesExpandItem,nBarracksExpandItem,nUnitsAttackItem,formationRadiusItem,
//												defenseRadiusItem,reactiveRadiusItem,nUnitsHarvestItem,unitTypeItem));
//										}
										
										if(scriptsMixReducedSet.size()<tamMixSet)
										{
											scriptsMixReducedSet.add(new BasicExpandedConfigurableScript(utt, getPathFinding(),
												strategyItem,nBasesExpandItem,nBarracksExpandItem,nUnitsAttackItem,formationRadiusItem,
												defenseRadiusItem,reactiveRadiusItem,nUnitsHarvestItem,unitTypeItem));
										}
										
										sizeEconomyitems++;
									}  
								}  
							}  
						}   
					}   
				}
			}
			if(debug>0)
			{
				System.out.println("size economy set "+sizeEconomyitems);
			}
		}		
	}

	public static PathFinding getPathFinding() {
		//		return new BFSPathFinding();
				return new AStarPathFinding();
		//      return new FloodFillPathFinding();
	}

	public static EvaluationFunction getEvaluationFunction() {
		return new SimpleEvaluationFunction();
		//		return new SimpleOptEvaluationFunction();
		//		return new LanchesterEvaluationFunction();
	}


	/**
	 * @return the scriptsCompleteSet
	 */
	public ArrayList<BasicExpandedConfigurableScript> getScriptsCompleteSet() {
		return scriptsCompleteSet;
	}


//	/**
//	 * @return the scriptsMixSet
//	 */
//	public ArrayList<BasicExpandedConfigurableScript> getScriptsMixSet() {
//		return scriptsMixSet;
//	}
	
	/**
	 * @return the scriptsMixReducedSet
	 */
	public ArrayList<BasicExpandedConfigurableScript> getScriptsMixReducedSet() {
		return scriptsMixReducedSet;
	}


	/**
	 * @return the tamMixSet
	 */
	public int getTamMixSet() {
		return tamMixSet;
	}


	/**
	 * @param tamMixSet the tamMixSet to set
	 */
	public void setTamMixSet(int tamMixSet) {
		this.tamMixSet = tamMixSet;
	}

}
