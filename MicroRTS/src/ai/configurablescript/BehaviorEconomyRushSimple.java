/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.configurablescript;

import ai.abstraction.AbstractAction;
import ai.abstraction.AbstractionLayerAI;
import ai.abstraction.Harvest;
import ai.abstraction.pathfinding.AStarPathFinding;
import ai.core.AI;
import ai.abstraction.pathfinding.PathFinding;
import ai.core.ParameterSpecification;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;

import rts.GameState;
import rts.PhysicalGameState;
import rts.Player;
import rts.PlayerAction;
import rts.UnitAction;
import rts.units.*;

/**
 *
 * @author julian
 */
public class BehaviorEconomyRushSimple {

	BasicExpandedConfigurableScript bec; 
	PhysicalGameState pgs; 
	Player p; 
	GameState gs; 
	int player;

	List <Integer> unitTypeL;

	public BehaviorEconomyRushSimple(BasicExpandedConfigurableScript bec, PhysicalGameState pgs, Player p, GameState gs, int player)
	{
		this.bec=bec;
		this.pgs=pgs;
		this.p=p;
		this.gs=gs;
		this.player=player;

		unitTypeL=bec.parametersValues.getAvailableOptions().get(bec.choices.get(BasicExpandedChoicePoint.UNITTYPE));

		doEconomyRushSimple();
	}
	public void doEconomyRushSimple()
	{
		// behavior of bases:
		for (Unit u : pgs.getUnits()) {
			if (u.getType() == bec.baseType
					&& u.getPlayer() == player
					&& gs.getActionAssignment(u) == null) {
				baseBehavior(u, p, pgs);
			}
		}

		// behavior of barracks:
		for (Unit u : pgs.getUnits()) {
			if (u.getType() == bec.barracksType
					&& u.getPlayer() == player
					&& gs.getActionAssignment(u) == null) {
				barracksBehavior(u, p, pgs);
			}
		}

		// behavior of workers:
		List<Unit> workers = new ArrayList<Unit>();
		for (Unit u : pgs.getUnits()) {
			if (u.getType().canHarvest
					&& u.getPlayer() == player
					&& u.getType() == bec.workerType) {
				workers.add(u);
			}
		}
		workersBehavior(workers, p, pgs);

		// behavior of melee units:
		for (Unit u : pgs.getUnits()) {
			if (u.getType().canAttack && !u.getType().canHarvest
					&& u.getPlayer() == player
					&& gs.getActionAssignment(u) == null) {
				meleeUnitBehavior(u, p, gs);
			}
		}
	}

	public void baseBehavior(Unit u, Player p, PhysicalGameState pgs) {
//		int nworkers = 0;
//		for (Unit u2 : pgs.getUnits()) {
//			if (u2.getType() == bec.workerType
//					&& u2.getPlayer() == p.getID()) {
//				nworkers++;
//			}
//		}
//		//calculo numero de bases
//		int nBases = 0;
//		for (Unit u2 : pgs.getUnits()) {
//			if (u2.getType() == bec.baseType
//					&& u2.getPlayer() == p.getID()) {
//				nBases++;
//			}
//		}
		int qtdWorkLim = bec.choices.get(BasicExpandedChoicePoint.NUNITSHARVEST);

		if (bec.nworkers < qtdWorkLim && p.getResources() >= bec.workerType.cost) {

				bec.train(u, bec.workerType);
			
		}
	}

	public void barracksBehavior(Unit u, Player p, PhysicalGameState pgs) {
//		int nLight = 0;
//		int nRanged = 0;
//		int nHeavy = 0;
//		for (Unit u2 : pgs.getUnits()) {
//			if (u2.getType() == bec.lightType
//					&& u.getPlayer() == p.getID()) {
//				nLight++;
//			}
//			if (u2.getType() == bec.rangedType
//					&& u.getPlayer() == p.getID()) {
//				nRanged++;
//			}
//			if (u2.getType() == bec.heavyType
//					&& u.getPlayer() == p.getID()) {
//				nHeavy++;
//			}
//		}
		//System.out.println("PVAI.EconomyRush.barracksBehavior() "+nLight + " "+nRanged+ " "+nHeavy);

		//conferir se eu já tenho algum light, se não tiver, crie.
		if (bec.nlight == 0 && p.getResources() >= bec.lightType.cost) {
			if(unitTypeL.contains(bec.parametersValues.getLight()))
			{
				bec.train(u, bec.lightType);
				return;
			}
		} else if (bec.nranged == 0 && p.getResources() >= bec.rangedType.cost) {
			//conferir se eu já tenho algum Ranged, se não tiver, crie.
			if(unitTypeL.contains(bec.parametersValues.getRanged()))
			{
				bec.train(u, bec.rangedType);
				return;
			}

		} else if (bec.nheavy == 0 && p.getResources() >= bec.heavyType.cost) {
			//conferir se eu já tenho algum Heavy, se não tiver, crie.
			if(unitTypeL.contains(bec.parametersValues.getHeavy()))
			{
				bec.train(u, bec.heavyType);
				return;
			}
		}

		//caso já possua uma unidade de cada, selecione randomicamente qualquer uma à gerar
		//if (bec.nlight != 0 && bec.nranged != 0 && bec.nheavy != 0) {
		if(unitTypeL.contains(bec.parametersValues.getLight()) || unitTypeL.contains(bec.parametersValues.getRanged()) || unitTypeL.contains(bec.parametersValues.getHeavy()) )
			{
			int uSelected=selectLessPresent();
			//int uSelected=unitTypeL.get(bec.r.nextInt(unitTypeL.size()));
			UnitType toBuild=bec.utt.getUnitType(uSelected);
			if (p.getResources() >= (toBuild.cost)) {
				bec.train(u, toBuild);
			}
			}
		//}
	}

	public void meleeUnitBehavior(Unit u, Player p, GameState gs) {
		PhysicalGameState pgs = gs.getPhysicalGameState();
		int ratioAttack=bec.choices.get(BasicExpandedChoicePoint.FORMATIONRADIUS);
		int ratioReactive=bec.choices.get(BasicExpandedChoicePoint.REACTIVERADIUS);
		Unit closestEnemy = null;
		int closestDistance = 0;
		Unit fartestFriend = null;
		int fartestDistance = 0;
		int mybase = 0;
		for (Unit u2 : pgs.getUnits()) {
			if (u2.getPlayer() >= 0 && u2.getPlayer() != p.getID()) {
				int d = Math.abs(u2.getX() - u.getX()) + Math.abs(u2.getY() - u.getY());
				if (closestEnemy == null || d < closestDistance) {
					closestEnemy = u2;
					closestDistance = d;
				}
			} else if(u2.getPlayer()==p.getID() && u2.getType() == bec.baseType)
			{
				mybase = Math.abs(u2.getX() - u.getX()) + Math.abs(u2.getY() - u.getY());
			}
		}
		for (Unit u2 : pgs.getUnits()) {
			if (u2.getPlayer() >= 0 && u2.getPlayer() == p.getID()) {
				int d = Math.abs(u2.getX() - u.getX()) + Math.abs(u2.getY() - u.getY());
				if ((fartestFriend == null || d > fartestDistance) && (validationMove(u,u2))) {
					fartestFriend = u2;
					fartestDistance = d;
				}
			}
		}
		if(bec.nmelees+bec.nworkers<bec.choices.get(BasicExpandedChoicePoint.NUNITSATTACK))
		{
			if (validationMove(u,closestEnemy))
			{
				if ((closestEnemy!=null && (closestDistance < pgs.getHeight()/ratioReactive || mybase < pgs.getHeight()/ratioAttack)) ) {
					bec.attack(u,closestEnemy);
				}
				else {
					bec.attack(u,null);
				}
			}
			else if(fartestFriend!=null)
			{
				bec.move(u, fartestFriend.getX(), fartestFriend.getY());
			}
			else
			{
				bec.attack(u,null);
			}	
		}
		else
		{
			if (closestEnemy != null) {
				//            System.out.println("EconomyRush.meleeUnitBehavior: " + u + " attacks " + closestEnemy);
				bec.attack(u, closestEnemy);
			}
		}
	}

	public void workersBehavior(List<Unit> workers, Player p, PhysicalGameState pgs) {
//		int nbases = 0;
//		int nbarracks = 0;
//
//		int resourcesUsed = 0;
		Unit harvestWorker = null;
		List<Unit> freeWorkers = new ArrayList<Unit>();
		freeWorkers.addAll(workers);

		if (workers.isEmpty()) {
			return;
		}

//		for (Unit u2 : pgs.getUnits()) {
//			if (u2.getType() == bec.baseType
//					&& u2.getPlayer() == p.getID()) {
//				nbases++;
//			}
//			if (u2.getType() == bec.barracksType
//					&& u2.getPlayer() == p.getID()) {
//				nbarracks++;
//			}
//		}
		List<Unit> bases = new LinkedList<Unit>();
		for (Unit u2 : pgs.getUnits()) {
			if (u2.getType() == bec.baseType
					&& u2.getPlayer() == p.getID()) {
				bases.add(u2);
			}
		}

		List<Integer> reservedPositions = new ArrayList<Integer>();
		if (bec.nbases == 0 && !freeWorkers.isEmpty()) {
			// build a base:
			if (p.getResources() >= bec.baseType.cost + bec.resourcesUsed ) {
				//System.out.println("expanding");
				Unit u = freeWorkers.remove(0);
				List<Unit> resources=new LinkedList<Unit>();
				for (Unit u2 : pgs.getUnits()) {
					if(u2.getType() == bec.resourceType){
						resources.add(u2);
					}
				}

				//get closest resource that hasn't got bases around, or enemy units
				Unit closestFreeResource=findClosest(u, 
						(Unit unit) -> {
							return unit.getType() == bec.resourceType && 
									pgs.getUnitsAround(unit.getX(), unit.getY(), 10).stream()
									.map((a)->a.getPlayer()!=(1-p.getID())&&a.getType()!=bec.baseType)
									.reduce((a,b)->a&&b).get();
						}, 
						pgs);
				if(closestFreeResource!=null){
					bec.buildIfNotAlreadyBuilding(u,bec.baseType,closestFreeResource.getX(),closestFreeResource.getY(),reservedPositions,p,pgs);
				}
				bec.resourcesUsed += bec.baseType.cost;
			}else{
				//System.out.println("reserving");
				bec.resourcesUsed+=  bec.baseType.cost;
			}
		}

		if (bec.nbarracks == 0 && !freeWorkers.isEmpty() && bases.size()>0) {
			// build a barracks:
			if (p.getResources() >= bec.barracksType.cost + bec.resourcesUsed) {
				Unit u = freeWorkers.remove(0);
				Unit b = bases.get(bec.nbarracks);
				int xCoord=b.getX();
				int yCoord=b.getY();
				
				if(player==0)
				{
					xCoord=b.getX()+2;
					yCoord=b.getY()+2;
				}
				bec.buildIfNotAlreadyBuilding(u,bec.barracksType,xCoord,yCoord,reservedPositions,p,pgs);
				bec.resourcesUsed += bec.barracksType.cost;
			}
		}

		//expand

		//Expanding bases
		if(     bec.choices.get(BasicExpandedChoicePoint.NBASESEXPAND)>0
				&& bec.nbarracks >= 1 
				&& ((bec.nbases - bec.abandonedbases) <= bec.choices.get(BasicExpandedChoicePoint.NBASESEXPAND)) 
				&& bec.freeresources > 0  
				&& !freeWorkers.isEmpty()) {
			//        	System.out.println("should expand");
			// build a base:
			if (p.getResources() >= bec.baseType.cost + bec.resourcesUsed ) {
				//System.out.println("expanding");
				Unit u = freeWorkers.remove(0);
				List<Unit> resources=new LinkedList<Unit>();
				for (Unit u2 : pgs.getUnits()) {
					if(u2.getType() == bec.resourceType){
						resources.add(u2);
					}
				}

				//get closest resource that hasn't got bases around, or enemy units
				Unit closestFreeResource=findClosest(u, 
						(Unit unit) -> {
							return unit.getType() == bec.resourceType && 
									pgs.getUnitsAround(unit.getX(), unit.getY(), 10).stream()
									.map((a)->a.getPlayer()!=(1-p.getID())&&a.getType()!=bec.baseType)
									.reduce((a,b)->a&&b).get();
						}, 
						pgs);
				if(closestFreeResource!=null){
					bec.buildIfNotAlreadyBuilding(u,bec.baseType,closestFreeResource.getX(),closestFreeResource.getY(),reservedPositions,p,pgs);
				}
				bec.resourcesUsed += bec.baseType.cost;
			}else{
				//System.out.println("reserving");
				bec.resourcesUsed+=  bec.baseType.cost;
			}
		}
		//Expanding barracks
		if(		bec.choices.get(BasicExpandedChoicePoint.NBARRACKSEXPAND)>0
				&& (bec.nbarracks <= bec.choices.get(BasicExpandedChoicePoint.NBARRACKSEXPAND)) 
				&& !(bec.choices.get(BasicExpandedChoicePoint.UNITTYPE)==bec.parametersValues.getWorker()) 
				&& !freeWorkers.isEmpty()) {
			// build a barracks:
			if (bec.nbarracks > 0 && !freeWorkers.isEmpty() && bases.size()>0){
				// build a new barracks:
				if (p.getResources() >= bec.barracksType.cost + bec.resourcesUsed) {
					Unit u = freeWorkers.remove(0);
					Unit b =bases.get(0);
					if(bases.size()>=bec.nbarracks+1)
						b = bases.get(bec.nbarracks);
					int xCoord=b.getX();
					int yCoord=b.getY();
					
					if(player==0)
					{
						xCoord=b.getX()+2;
						yCoord=b.getY()+2;
					}
					bec.buildIfNotAlreadyBuilding(u,bec.barracksType,xCoord,yCoord,reservedPositions,p,pgs);
					bec.resourcesUsed += bec.barracksType.cost;
				}
			}
		}

		if(!unitTypeL.contains(bec.parametersValues.getWorker()))
		{
		// harvest with all the free workers:
        for (Unit u : freeWorkers) {
            Unit closestBase = null;
            Unit closestResource = null;
            int closestDistance = 0;
            for (Unit u2 : pgs.getUnits()) {
                if (u2.getType().isResource) {
                    int d = Math.abs(u2.getX() - u.getX()) + Math.abs(u2.getY() - u.getY());
                    if (closestResource == null || d < closestDistance) {
                        closestResource = u2;
                        closestDistance = d;
                    }
                }
            }
            closestDistance = 0;
            for (Unit u2 : pgs.getUnits()) {
                if (u2.getType().isStockpile && u2.getPlayer()==p.getID()) {
                    int d = Math.abs(u2.getX() - u.getX()) + Math.abs(u2.getY() - u.getY());
                    if (closestBase == null || d < closestDistance) {
                        closestBase = u2;
                        closestDistance = d;
                    }
                }
            }
            if (closestResource != null && closestBase != null) {
                AbstractAction aa = bec.getAbstractAction(u);
                if (aa instanceof Harvest) {
                    Harvest h_aa = (Harvest)aa;
                    if (h_aa.getTarget() != closestResource || h_aa.getBase()!=closestBase) bec.harvest(u, closestResource, closestBase);
                } else {
                    bec.harvest(u, closestResource, closestBase);
                }
            }
        }
		}
		else if(unitTypeL.contains(bec.parametersValues.getWorker()))
		{
			if (freeWorkers.size()>0) harvestWorker = freeWorkers.remove(0);
	        // harvest with the harvest worker:
	        if (harvestWorker!=null) {
	            Unit closestBase = null;
	            Unit closestResource = null;
	            int closestDistance = 0;
	            for(Unit u2:pgs.getUnits()) {
	                if (u2.getType().isResource) { 
	                    int d = Math.abs(u2.getX() - harvestWorker.getX()) + Math.abs(u2.getY() - harvestWorker.getY());
	                    if (closestResource==null || d<closestDistance) {
	                        closestResource = u2;
	                        closestDistance = d;
	                    }
	                }
	            }
	            closestDistance = 0;
	            for(Unit u2:pgs.getUnits()) {
	                if (u2.getType().isStockpile && u2.getPlayer()==p.getID()) { 
	                    int d = Math.abs(u2.getX() - harvestWorker.getX()) + Math.abs(u2.getY() - harvestWorker.getY());
	                    if (closestBase==null || d<closestDistance) {
	                        closestBase = u2;
	                        closestDistance = d;
	                    }
	                }
	            }
	            if (closestResource!=null && closestBase!=null) {
	                AbstractAction aa = bec.getAbstractAction(harvestWorker);
	                if (aa instanceof Harvest) {
	                    Harvest h_aa = (Harvest)aa;
	                    if (h_aa.getTarget() != closestResource || h_aa.getBase()!=closestBase) bec.harvest(harvestWorker, closestResource, closestBase);
	                } else {
	                    bec.harvest(harvestWorker, closestResource, closestBase);
	                }
	            }
	        }
	        
	        for(Unit u:freeWorkers) meleeUnitBehavior(u, p, gs);
		}
	}

	public Unit findClosest(Unit from, Predicate<Unit> predicate, PhysicalGameState pgs){
		Unit closestUnit = null;
		int closestDistance = 0;
		for (Unit u2 : pgs.getUnits()) {
			if (predicate.test(u2)) {
				int d = Math.abs(u2.getX() - from.getX()) + Math.abs(u2.getY() - from.getY());
				if (closestUnit == null || d < closestDistance) {
					closestUnit = u2;
					closestDistance = d;
				}
			}
		}
		return closestUnit;
	}
	public boolean validationMove(Unit unit, Unit target)
	{
		UnitAction move = bec.getPathFinding().findPathToPositionInRange(unit, target.getX()+target.getY()*gs.getPhysicalGameState().getWidth(), unit.getAttackRange(), gs, null);
		if (move!=null && gs.isUnitActionAllowed(unit, move)) return true;
		return false;
	}
	//This metods return the id of the typeunit that is present in the ahorter quantity. If there is draw, is selected light, after heacy, after ranged
	public int selectLessPresent()
	{		
		int [] quantityEachUnit=new int[3];
		quantityEachUnit[0]=bec.nlight;
		quantityEachUnit[1]=bec.nheavy;
		quantityEachUnit[2]=bec.nranged;
		
		Arrays.sort(quantityEachUnit);
		
		for(int i=0;i<quantityEachUnit.length;i++)
		{
			if(quantityEachUnit[i]==bec.nlight && unitTypeL.contains(bec.utt.getUnitType("Light").ID))
			{
				return bec.utt.getUnitType("Light").ID;
			}
			else if(quantityEachUnit[i]==bec.nheavy && unitTypeL.contains(bec.utt.getUnitType("Heavy").ID))
			{
				return bec.utt.getUnitType("Heavy").ID;
			}
			else if(quantityEachUnit[i]==bec.nranged  && unitTypeL.contains(bec.utt.getUnitType("Ranged").ID))
			{
				return bec.utt.getUnitType("Ranged").ID;
			}
		}

			
		return -1;
	}
	public void moveToNearestFriend(Unit u, Unit closestEnemy) {


		int xDiff=u.getX()-closestEnemy.getX();//>0 enemy LEFT
		int yDiff=u.getY()-closestEnemy.getY();//>0 enemy UP
		int targetX=u.getX();
		int targetY=u.getY();
		if (Math.abs(xDiff)> Math.abs(yDiff)){//run horizontally
			if(xDiff>0 && targetX<gs.getPhysicalGameState().getWidth()-1)targetX=u.getX()+1;
			else if(xDiff<0 && targetX>0) targetX=u.getX()-1;
		}else{
			if(yDiff>0 && targetY<gs.getPhysicalGameState().getHeight()-1)targetY=u.getY()+1;
			else if (yDiff<0 && targetY>0) targetY=u.getY()-1;
		}
		if(gs.free(targetX,targetY)){
			bec.move(u,targetX,targetY);
		}else{
			bec.attack(u, closestEnemy);
		}

	}
}
