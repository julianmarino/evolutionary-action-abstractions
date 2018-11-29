package Standard;

import ai.evaluation.EvaluationFunction;
import ai.evaluation.LanchesterEvaluationFunction;
import ai.evaluation.SimpleSqrtEvaluationFunction3;
import rts.GameState;

public class CombinedEvaluation extends EvaluationFunction  {
	EvaluationFunction lanchester = new LanchesterEvaluationFunction();
	EvaluationFunction simple = new SimpleSqrtEvaluationFunction3();
	public CombinedEvaluation() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public float evaluate(int maxplayer, int minplayer, GameState gs) {
		if(gs.getPhysicalGameState().getWidth()==8 || gs.getPhysicalGameState().getWidth()==128){
			return lanchester.evaluate(maxplayer, minplayer, gs);
		}else{
			return simple.evaluate(maxplayer, minplayer, gs);
		}
	}

	@Override
	public float upperBound(GameState gs) {
		if(gs.getPhysicalGameState().getHeight()==8 || gs.getPhysicalGameState().getHeight()==128){
			return lanchester.upperBound(gs);
		}else{
			return simple.upperBound(gs);
		}
	}

}
