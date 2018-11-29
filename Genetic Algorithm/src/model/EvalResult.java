package model;

public class EvalResult  implements Comparable<EvalResult>{
	
	private Integer evaluation;
	private String IA1, IA2;
	
	public EvalResult(){
		evaluation = 0;
		IA1="";
		IA2="";
	}
	
	public Integer getEvaluation() {
		return evaluation;
	}

	public void setEvaluation(Integer evaluation) {
		this.evaluation = evaluation;
	}

	public String getIA1() {
		return IA1;
	}

	public void setIA1(String iA1) {
		IA1 = iA1;
	}

	public String getIA2() {
		return IA2;
	}

	public void setIA2(String iA2) {
		IA2 = iA2;
	}

	public String getLine(){
		return " Evaluation= "+this.evaluation+" IA1= "+ IA1+" IA2="+IA2;
	}
	
	public void print(){
		System.out.println("AI's do confronto:");
		System.out.println("IA1= "+IA1);
		System.out.println("IA2= "+IA2);
		System.out.println(" Winner ="+ (evaluation));
		System.out.println();
	}

	@Override
	public int compareTo(EvalResult o) {
		if(this.evaluation > o.getEvaluation()){
			return -1;
		}
		if(this.evaluation < o.getEvaluation()){
			return 1;
		}
		
		return 0;
	}
	
}
