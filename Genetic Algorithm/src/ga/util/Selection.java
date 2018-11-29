package ga.util;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import ga.ScriptTableGenerator.ScriptsTable;
import ga.config.ConfigurationsGA;
import ga.model.Chromosome;
import ga.model.Population;

public class Selection {

	/**
	 * Este método será responsável por controlar o processo de seleção. 
	 * Acredito que nele poderá ser feitas as chamadas para cruzamento e para mutação.
	 * @param populacaoInicial que será utilizada para aplicarmos as alterações.
	 * @return Population com os devidos novos cromossomos.
	 */
	
	static Random rand = new Random();
	
	public Population applySelection(Population populacaoInicial,ScriptsTable scrTable, String pathTableScripts){


		//System.out.println("printing the initial population");
		//printMap(populacaoInicial.getChromosomes());

		//class preselection have the methods for selecting parents according the tournament
		PreSelection ps=new PreSelection(populacaoInicial);			
		List<Map.Entry<Chromosome, BigDecimal>> parents=ps.Tournament();		
		//System.out.println("printing the parents selected for reproduction ");
		//printList(parents);

		//Class Reproduction have the methods for getting new population according the parents obtained before
		//using crossover and mutation
		Reproduction rp=new Reproduction(parents,scrTable,pathTableScripts);
		//Population newPopulation=rp.UniformCrossover();
		Population newPopulation=rp.Crossover();
		//System.out.println("printing the new population after crossover");
		//printMap(newPopulation.getChromosomes());
		newPopulation=rp.mutation(newPopulation);
		if(ConfigurationsGA.INCREASING_INDEX==true)
		{
		newPopulation=rp.IncreasePopulation(newPopulation);
		newPopulation=rp.DecreasePopulation(newPopulation);
		}
		
		//System.out.println("printing the new population after mutation");
		//printMap(newPopulation.getChromosomes());

		//in elite is saved the best guys from the last population
		HashMap<Chromosome, BigDecimal> elite=(HashMap<Chromosome, BigDecimal>)ps.sortByValue(populacaoInicial.getChromosomes());
		//System.out.println("printing elite last population");
		//printMap(elite);

		//joining elite and new sons in chromosomesNewPopulation, 
		HashMap<Chromosome, BigDecimal> chromosomesNewPopulation=new HashMap<Chromosome, BigDecimal>();
		chromosomesNewPopulation.putAll(newPopulation.getChromosomes());
		chromosomesNewPopulation.putAll(elite);
		//System.out.println("printing complete new population (elite+new population)");
		//printMap(chromosomesNewPopulation);
		newPopulation.setChromosomes(chromosomesNewPopulation);
		
		//if the number of the new pop is less than the initial pop, fill with random elements
		newPopulation=fillWithRandom(newPopulation,scrTable);
		//System.out.println("printing complete new population with new random elements If that's the case");
		//printMap(chromosomesNewPopulation);

		newPopulation=rp.RemoveCopies(newPopulation);
		
		return newPopulation;
	}

	public void printMap(HashMap<Chromosome, BigDecimal> m)
	{
		for (Chromosome ch: m.keySet()){

			String key =ch.getGenes().toString();
			String value = m.get(ch).toString();  
			System.out.println(key + " " + value);  


		} 
	}
	public void printList(List<Map.Entry<Chromosome, BigDecimal>> l)
	{
		for (Map.Entry<Chromosome, BigDecimal> it: l){

			String key =it.getKey().getGenes().toString();
			String value = it.getValue().toString(); 
			System.out.println(key + " " + value);

		} 
	}
	public Population fillWithRandom(Population p,ScriptsTable scrTable)
	{
		while(p.getChromosomes().size()<ConfigurationsGA.SIZE_POPULATION)
		{
			Chromosome tChom = new Chromosome();
			int sizeCh=rand.nextInt(ConfigurationsGA.SIZE_CHROMOSOME)+1;
			for (int j = 0; j < sizeCh; j++) {
				tChom.addGene(rand.nextInt(scrTable.getCurrentSizeTable()));
			}
			p.getChromosomes().put(tChom, BigDecimal.ZERO);			
		}
		return p;
	}

}
