package ga.ScriptTableGenerator;

import java.util.ArrayList;

/**
 * 
 * @author rubens julian

 */
public class ChromosomeScript {
	private ArrayList<Integer> Genes;

	public ChromosomeScript() {
		this.Genes = new ArrayList<>();
	}

	public ArrayList<Integer> getGenes() {
		return Genes;
	}

	public void setGenes(ArrayList<Integer> genes) {
		Genes = genes;
	}
	
	public void addGene(Integer gene){
		this.Genes.add(gene);
	}
	
	public String print(){

		String crom="";
		for (Integer gene : Genes) {
			//System.out.print(gene+" ");
			crom+=" "+gene;
		}
		return crom;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((Genes == null) ? 0 : Genes.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ChromosomeScript other = (ChromosomeScript) obj;
		if (Genes == null) {
			if (other.Genes != null)
				return false;
		} else if (!Genes.equals(other.Genes))
			return false;
		return true;
	}
	
	
	
}
