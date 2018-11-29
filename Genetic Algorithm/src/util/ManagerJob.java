package util;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Esta classe tem a função de gerenciar o arquivo aControlStartJob.sh que será utilizado 
 * nas execução do arquivo jar
 * @author rubens
 *
 *  Como usar
 *  
 *		ManagerJob man = new ManagerJob();
 *		man.configureArq("'0;1'", "'0;2'");  
 *		
 *		
 *		A tupla tem que ser passada entre aspas simples
 */

public class ManagerJob {
	private String linha1, linha2, ultimaLinha;
	
	public ManagerJob(){
		linha1 = "#!/bin/bash";
		linha2 = "cd /storage1/dados/es91661/ExecAIGASOA";
		ultimaLinha = "(qsub -l nodes=1:ppn=5,mem=5gb -v TUPIA1=$i,TUPIA2=$t,ID=$m bFixedJob.sh) &";
	}
	
	/**
	 * Função de editar o arquivo que fará o lançamento do Jobs 
	 * @param tupleIA1 Tupla com os índices para a IA 1.
	 * @param tupleIA2 Tupla com os índices para a IA 2.
	 */
	 
	public void configureArq(String tupleIA1, String tupleIA2, Integer match){
		//altero o arquivo com a nova configuração.
		String arquivo = System.getProperty("user.dir").concat("/aControlStartJob.sh");
		
		try {
			FileWriter arq = new FileWriter(arquivo, false);
			PrintWriter gravarArq = new PrintWriter(arq);
			gravarArq.println(linha1);
			gravarArq.println(linha2);
			gravarArq.println("i="+tupleIA1);
			gravarArq.println("t="+tupleIA2);
			gravarArq.println("m="+match);
			gravarArq.println(ultimaLinha);
			
			gravarArq.flush();
			gravarArq.close();
			arq.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}