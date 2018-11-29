package ai.configurablescript;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import rts.units.UnitType;
import rts.units.UnitTypeTable;

public class Parameters {

	// Parameters related to UNITTYPE choice point

	int resource;

	int worker;
	int light;
	int heavy;
	int ranged;    
	int workerLight;
	int workerHeavy;
	int workerRanged;
	int lightHeavy;
	int lightRanged;
	int heavyRanged;
	int workerLightHeavy;
	int workerLightRanged;
	int workerHeavyRanged;
	int lightHeavyRanged;
	int allTypesUnits;

	// Parameters related to EXPAND choice point 
	int nBasesExpand;
	int nBarracksExpand;

	// Parameters related to STRATEGY choice point
	int rushStrategy;
	int defenseStrategy;
	//    int economyMilitaryRushStrategy;
	//    int economyRushStrategy;
	int economyRushSimpleStrategy;

	// other Parameters open
	int nunitsAttack;   
	int formationRadius;
	int reactiveRadius;
	int defenseRadius;
	int nunitsHarvest;

	//List choices
	List <Integer> workerOpt;
	List <Integer> lightOpt;
	List <Integer> heavyOpt;
	List <Integer> rangedOpt;
	List <Integer> workerLightOpt;
	List <Integer> workerHeavyOpt;
	List <Integer> workerRangedOpt;
	List <Integer> lightHeavyOpt;
	List <Integer> lightRangedOpt;
	List <Integer> heavyRangedOpt;
	List <Integer> workerLightHeavyOpt;
	List <Integer> workerLightRangedOpt;
	List <Integer> workerHeavyRangedOpt;
	List <Integer> lightHeavyRangedOpt;
	List <Integer> allTypesUnitsOpt;

	// Map controlling the options for each id;
	HashMap<Integer, List> availableOptions;

	public Parameters(UnitTypeTable a_utt)
	{
		resource = a_utt.getUnitType("Resource").ID;
		worker=a_utt.getUnitType("Worker").ID;
		light=a_utt.getUnitType("Light").ID;;
		heavy=a_utt.getUnitType("Heavy").ID;;
		ranged=a_utt.getUnitType("Ranged").ID;
		workerLight=7;
		workerHeavy=8;
		workerRanged=9;
		lightHeavy=10;
		lightRanged=11;
		heavyRanged=12;
		workerLightHeavy=13;
		workerLightRanged=14;
		workerHeavyRanged=15;
		lightHeavyRanged=16;
		allTypesUnits=17;        
		rushStrategy=18;
		defenseStrategy=19;
		economyRushSimpleStrategy=20;
		//economyMilitaryRushStrategy=25;
		//economyRushStrategy=26;

		nBasesExpand=1;
		nBarracksExpand = 1;
		nunitsAttack=2;
		formationRadius=4;
		reactiveRadius=4;
		defenseRadius=2;
		nunitsHarvest=2;        

		workerOpt= Arrays.asList(worker);
		lightOpt= Arrays.asList(light);
		heavyOpt= Arrays.asList(heavy);
		rangedOpt= Arrays.asList(ranged);
		workerLightOpt= Arrays.asList(worker,light);
		workerHeavyOpt = Arrays.asList(worker,heavy);
		workerRangedOpt = Arrays.asList(worker, ranged);
		lightHeavyOpt = Arrays.asList(light, heavy);
		lightRangedOpt = Arrays.asList(light, ranged);
		heavyRangedOpt = Arrays.asList(heavy, ranged);
		workerLightHeavyOpt= Arrays.asList(worker, light, heavy);
		workerLightRangedOpt = Arrays.asList(worker, light, ranged);
		workerHeavyRangedOpt = Arrays.asList(worker, heavy, ranged);
		lightHeavyRangedOpt= Arrays.asList(light,heavy,ranged);
		allTypesUnitsOpt= Arrays.asList(worker,light,heavy,ranged);


		availableOptions=new HashMap<>();
		availableOptions.put(worker, workerOpt);
		availableOptions.put(light, lightOpt);
		availableOptions.put(heavy, heavyOpt);
		availableOptions.put(ranged, rangedOpt);
		availableOptions.put(workerLight, workerLightOpt);
		availableOptions.put(workerHeavy, workerHeavyOpt);
		availableOptions.put(workerRanged, workerRangedOpt);
		availableOptions.put(lightHeavy, lightHeavyOpt);
		availableOptions.put(lightRanged, lightRangedOpt);
		availableOptions.put(heavyRanged, heavyRangedOpt);
		availableOptions.put(workerLightHeavy, workerLightHeavyOpt);
		availableOptions.put(workerLightRanged, workerLightRangedOpt);
		availableOptions.put(workerHeavyRanged, workerHeavyRangedOpt);
		availableOptions.put(lightHeavyRanged, lightHeavyRangedOpt);
		availableOptions.put(allTypesUnits, allTypesUnitsOpt);

	}

	/**
	 * @return the resource
	 */
	public int getResource() {
		return resource;
	}

	/**
	 * @return the worker
	 */
	public int getWorker() {
		return worker;
	}

	/**
	 * @return the light
	 */
	public int getLight() {
		return light;
	}

	/**
	 * @return the heavy
	 */
	public int getHeavy() {
		return heavy;
	}

	/**
	 * @return the ranged
	 */
	public int getRanged() {
		return ranged;
	}

	/**
	 * @return the workerLight
	 */
	public int getWorkerLight() {
		return workerLight;
	}

	/**
	 * @return the workerHeavy
	 */
	public int getWorkerHeavy() {
		return workerHeavy;
	}

	/**
	 * @return the workerRanged
	 */
	public int getWorkerRanged() {
		return workerRanged;
	}

	/**
	 * @return the lightHeavy
	 */
	public int getLightHeavy() {
		return lightHeavy;
	}

	/**
	 * @return the lightRanged
	 */
	public int getLightRanged() {
		return lightRanged;
	}

	/**
	 * @return the heavyRanged
	 */
	public int getHeavyRanged() {
		return heavyRanged;
	}

	/**
	 * @return the workerLightHeavy
	 */
	public int getWorkerLightHeavy() {
		return workerLightHeavy;
	}

	/**
	 * @return the workerLightRanged
	 */
	public int getWorkerLightRanged() {
		return workerLightRanged;
	}

	/**
	 * @return the workerHeavyRanged
	 */
	public int getWorkerHeavyRanged() {
		return workerHeavyRanged;
	}

	/**
	 * @return the lightHeavyRanged
	 */
	public int getLightHeavyRanged() {
		return lightHeavyRanged;
	}

	/**
	 * @return the allTypesUnits
	 */
	public int getAllTypesUnits() {
		return allTypesUnits;
	}


	/**
	 * @return the nBasesExpand
	 */
	public int getnBasesExpand() {
		return nBasesExpand;
	}

	/**
	 * @return the nBarracksExpand
	 */
	public int getnBarracksExpand() {
		return nBarracksExpand;
	}

	/**
	 * @return the rushStrategy
	 */
	public int getRushStrategy() {
		return rushStrategy;
	}

	/**
	 * @return the defenseStrategy
	 */
	public int getDefenseStrategy() {
		return defenseStrategy;
	}

	//	/**
	//	 * @return the economyMilitaryRushStrategy
	//	 */
	//	public int getEconomyMilitaryRushStrategy() {
	//		return economyMilitaryRushStrategy;
	//	}
	//
	//	/**
	//	 * @return the economyRushStrategy
	//	 */
	//	public int getEconomyRushStrategy() {
	//		return economyRushStrategy;
	//	}

	/**
	 * @return the economyRushSimpleStrategy
	 */
	public int getEconomyRushSimpleStrategy() {
		return economyRushSimpleStrategy;
	}

	/**
	 * @return the nunits
	 */
	public int getNunitsAttack() {
		return nunitsAttack;
	}

	/**
	 * @return the formationRadius
	 */
	public int getFormationRadius() {
		return formationRadius;
	}

	/**
	 * @return the reactiveRadius
	 */
	public int getReactiveRadius() {
		return reactiveRadius;
	}

	/**
	 * @return the defenseRadius
	 */
	public int getDefenseRadius() {
		return defenseRadius;
	}

	/**
	 * @return the nunitsHarvest
	 */
	public int getNunitsHarvest() {
		return nunitsHarvest;
	}

	/**
	 * @return the workerOpt
	 */
	public List<Integer> getWorkerOpt() {
		return workerOpt;
	}

	/**
	 * @return the lightOpt
	 */
	public List<Integer> getLightOpt() {
		return lightOpt;
	}

	/**
	 * @return the heavyOpt
	 */
	public List<Integer> getHeavyOpt() {
		return heavyOpt;
	}

	/**
	 * @return the rangedOpt
	 */
	public List<Integer> getRangedOpt() {
		return rangedOpt;
	}

	/**
	 * @return the workerLightOpt
	 */
	public List<Integer> getWorkerLightOpt() {
		return workerLightOpt;
	}

	/**
	 * @return the workerHeavyOpt
	 */
	public List<Integer> getWorkerHeavyOpt() {
		return workerHeavyOpt;
	}

	/**
	 * @return the workerRangedOpt
	 */
	public List<Integer> getWorkerRangedOpt() {
		return workerRangedOpt;
	}

	/**
	 * @return the lightHeavyOpt
	 */
	public List<Integer> getLightHeavyOpt() {
		return lightHeavyOpt;
	}

	/**
	 * @return the lightRangedOpt
	 */
	public List<Integer> getLightRangedOpt() {
		return lightRangedOpt;
	}

	/**
	 * @return the heavyRangedOpt
	 */
	public List<Integer> getHeavyRangedOpt() {
		return heavyRangedOpt;
	}

	/**
	 * @return the workerLightHeavyOpt
	 */
	public List<Integer> getWorkerLightHeavyOpt() {
		return workerLightHeavyOpt;
	}

	/**
	 * @return the workerLightRangedOpt
	 */
	public List<Integer> getWorkerLightRangedOpt() {
		return workerLightRangedOpt;
	}

	/**
	 * @return the workerHeavyRangedOpt
	 */
	public List<Integer> getWorkerHeavyRangedOpt() {
		return workerHeavyRangedOpt;
	}

	/**
	 * @return the lightHeavyRangedOpt
	 */
	public List<Integer> getLightHeavyRangedOpt() {
		return lightHeavyRangedOpt;
	}

	/**
	 * @return the allTypesUnitsOpt
	 */
	public List<Integer> getAllTypesUnitsOpt() {
		return allTypesUnitsOpt;
	}


	/**
	 * @return the availableOptions
	 */
	public HashMap<Integer, List> getAvailableOptions() {
		return availableOptions;
	}




}
