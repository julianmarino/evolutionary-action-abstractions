package ai.utalca;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import ai.abstraction.EMRDeterministico;
import ai.abstraction.HeavyRush;
import ai.abstraction.LightRush;
import ai.abstraction.SimpleEconomyRush;
import ai.abstraction.WorkerRush;
import ai.abstraction.WorkerRushPlusPlus;
import ai.abstraction.cRush.CRush_V1;
import ai.core.AI;
import ai.core.AIWithComputationBudget;
import ai.core.ParameterSpecification;
import rts.GameState;
import rts.PhysicalGameState;
import rts.PlayerAction;
import rts.units.UnitTypeTable;

public class UTalcaBot extends AIWithComputationBudget {

	AI ai=null;
	UnitTypeTable m_utt;
	
	public UTalcaBot(UnitTypeTable utt) {
        super(-1,-1);
        m_utt = utt;
	}

	@Override
	public void reset() {
		ai=null;

	}

	@Override
	public PlayerAction getAction(int player, GameState gs) throws Exception {
		return ai.getAction(player, gs);
	}

	@Override
	public void preGameAnalysis(GameState gs, long milliseconds) throws Exception
	{
		PhysicalGameState pgs=gs.getPhysicalGameState();
		Random rand=new Random();
		if(pgs.getUnits().stream().anyMatch(u -> u.getType()==m_utt.getUnitType("Barracks"))) {//starting barracks
			ai=new HeavyRush(m_utt);
			return;
		}
		if(pgs.getHeight()*pgs.getWidth()<=64) {//8x8 or smaller
			if(rand.nextBoolean()) {
				ai=new WorkerRushPlusPlus(m_utt);
			}else {
				ai=new CRush_V1(m_utt);
			}
			return;
		}
		if(pgs.getHeight()==8 && pgs.getWidth()==9) {//NoWhereToRun9x8
			ai=new SimpleEconomyRush(m_utt);
			return;
		}
		if(pgs.getHeight()*pgs.getWidth()<=256) {//16x16 or smaller
			ai=new CRush_V1(m_utt);
			return;
		}
		if(pgs.getHeight()==24 && pgs.getWidth()==24 &&
				pgs.getTerrain(0, 11)==pgs.TERRAIN_WALL &&
				pgs.getTerrain(0, 12)==pgs.TERRAIN_WALL&&
				pgs.getTerrain(1, 11)==pgs.TERRAIN_WALL&&
				pgs.getTerrain(1, 12)==pgs.TERRAIN_WALL&&
				pgs.getTerrain(2, 11)==pgs.TERRAIN_WALL &&
				pgs.getTerrain(2, 12)==pgs.TERRAIN_WALL&&
				pgs.getTerrain(3, 11)==pgs.TERRAIN_WALL&&
				pgs.getTerrain(3, 12)==pgs.TERRAIN_WALL) {//DoubleGame24x24
			if(rand.nextBoolean()) {
				ai=new WorkerRushPlusPlus(m_utt);
			}else {
				ai=new WorkerRush(m_utt);
			}
			return;
		}
		if(pgs.getHeight()*pgs.getWidth()<=1024) {//32x32 or smaller
			ai=new LightRush(m_utt);
			return;
		}
		ai=new EMRDeterministico(m_utt);//for bigger maps
	}
	 
	@Override
	public AI clone() {
		UTalcaBot bot=new UTalcaBot(m_utt);
		bot.ai=ai;
		return bot;
	}

	@Override
	public List<ParameterSpecification> getParameters() {
		 return new ArrayList<>();
	}

}
