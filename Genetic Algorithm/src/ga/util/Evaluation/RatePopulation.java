package ga.util.Evaluation;

import ga.model.Population;

public interface RatePopulation {
	/* A função de avaliação irá controlar as chamadas no cluster, ou fazer os cálculos das simulações e entregar
	*  uma população devidamente avaliada.
	*  Lembrar que na população os cromossomos estão em um MAP onde 
	*  KEYS   = Cromossomo
	*  VALUES = Valor da avaliação 
	*/
	public Population evalPopulation(Population population, int generation);
	public void finishProcess();
}
