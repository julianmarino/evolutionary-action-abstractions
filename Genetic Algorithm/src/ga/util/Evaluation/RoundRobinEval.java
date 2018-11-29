package ga.util.Evaluation;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import ga.model.Chromosome;
import ga.model.Population;
import model.EvalResult;
import util.LeitorLog;

public class RoundRobinEval implements RatePopulation {
	// CONSTANTES
	private static final int TOTAL_PARTIDAS_ROUND = 1;
	private static final int BATCH_SIZE = 2;

	// private static final String pathSOA =
	// "/home/rubens/cluster/ExecAIGASOA/configSOA/";
	private static final String pathSOA = System.getProperty("user.dir").concat("/configSOA/");

	// private static final String pathCentral =
	// "/home/rubens/cluster/ExecAIGASOA/centralSOA";
	private static final String pathCentral = System.getProperty("user.dir").concat("/centralSOA");
	

	// Classes de informação
	private int atualGeneration = 0;

	// Atributos locais
	ArrayList<String> SOA_Folders = new ArrayList<>();
	ArrayList<String> SOA_arqs = new ArrayList<>();

	public RoundRobinEval() {
		super();
	}

	@Override
	public Population evalPopulation(Population population, int generation) {
		this.atualGeneration = generation;
		SOA_Folders.clear();
		// limpa os valores existentes na population
		population.clearValueChromosomes();

		// executa os confrontos
		runBattles(population);

		// Só permite continuar a execução após terminar os JOBS.
		controllExecute();

		// remove qualquer aquivo que não possua um vencedor
		removeLogsEmpty();

		// ler resultados
		ArrayList<EvalResult> resultados = lerResultados();
		// atualizar valores das populacoes
		updatePopulationValue(resultados, population);

		return population;
	}

	private void removeLogsEmpty() {
		LeitorLog log = new LeitorLog();
		log.removeNoResults();
	}

	public Population updatePopulationValue(ArrayList<EvalResult> results, Population pop) {
		ArrayList<EvalResult> resultsNoDraw = removeDraw(results);

		/*
		 * System.out.println("Avaliações sem Draw"); for (EvalResult evalResult
		 * : resultsNoDraw) { evalResult.print(); }
		 */

		for (EvalResult evalResult : resultsNoDraw) {
			updateChomoPopulation(evalResult, pop);
		}

		return pop;
	}

	private void updateChomoPopulation(EvalResult evalResult, Population pop) {

		// identicar qual IA foi a vencedora
		String IAWinner = "";
		if (evalResult.getEvaluation() == 0) {
			IAWinner = evalResult.getIA1();
		} else {
			IAWinner = evalResult.getIA2();
		}

		// buscar na população a IA compatível.
		Chromosome chrUpdate = null;
		for (Chromosome ch : pop.getChromosomes().keySet()) {
			if (convertBasicTuple(ch).equals(IAWinner)) {
				chrUpdate = ch;
			}
		}
		// atualizar valores.
		BigDecimal toUpdate = pop.getChromosomes().get(chrUpdate);
		if (toUpdate != null) {
			toUpdate = toUpdate.add(BigDecimal.ONE);
			HashMap<Chromosome, BigDecimal> chrTemp = pop.getChromosomes();
			chrTemp.put(chrUpdate, toUpdate);
		} else {
			System.out.println("Problem to find " + chrUpdate.toString());
		}
	}

	private ArrayList<EvalResult> removeDraw(ArrayList<EvalResult> results) {
		ArrayList<EvalResult> rTemp = new ArrayList<>();

		for (EvalResult evalResult : results) {
			if (evalResult.getEvaluation() != -1) {
				rTemp.add(evalResult);
			}
		}

		return rTemp;
	}

	public ArrayList<EvalResult> lerResultados() {
		LeitorLog leitor = new LeitorLog();
		ArrayList<EvalResult> resultados = leitor.processar();
		/*
		 * for (EvalResult evalResult : resultados) { evalResult.print(); }
		 */
		return resultados;
	}

	/**
	 * Verifica se os jobs já foram encerrados no cluster.
	 */
	private void controllExecute() {

		// look for clients and share the data.
		while (hasSOACentralFile()) {
			// update the quantity of SOA Clients.
			updateSOAClients();
			// update the file to process
			updateFiles();
			// share the files between SOA Clients
			shareFiles();

			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		while (hasSOAArq()) {
			try {
				Thread.sleep(50000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	private void shareFiles() {
		for (String folder : this.SOA_Folders) {

			for (int i = 0; i < BATCH_SIZE; i++) {

				if (SOA_arqs.size() == 0) {
					return;
				}
				String nFile = SOA_arqs.get(0);
				File f = new File(nFile);
				try {
					copyFileUsingStream(f, new File(folder + "/" + f.getName()));
					SOA_arqs.remove(nFile);
					f.delete();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

	}

	private void updateFiles() {
		this.SOA_arqs.clear();
		File CentralFolder = new File(pathCentral + "/");
		for (File file : CentralFolder.listFiles()) {
			SOA_arqs.add(file.getAbsolutePath());
		}
	}

	private void updateSOAClients() {
		this.SOA_Folders.clear();
		File configSOAFolder = new File(pathSOA);
		if (configSOAFolder != null) {
			for (File folder : configSOAFolder.listFiles()) {
				if (folder.listFiles().length == 0) {
					SOA_Folders.add(folder.getAbsolutePath());
				}
			}
		}

	}

	/**
	 * irá verificar se todas as pastas SOA estão vazias
	 * 
	 * @return True se estiver vazias
	 */
	private boolean hasSOAArq() {
		updateSOACLientFull();
		for (String soaFolder : this.SOA_Folders) {
			String strConfig = soaFolder;
			File f = new File(strConfig);
			String[] children = f.list();
			if (children.length > 0) {
				return true;
			}

		}

		return false;
	}

	private void updateSOACLientFull() {
		this.SOA_Folders.clear();
		File configSOAFolder = new File(pathSOA);
		for (File folder : configSOAFolder.listFiles()) {
			SOA_Folders.add(folder.getAbsolutePath());
		}

	}

	/**
	 * Irá verificar a pasta central não tem mais arquivos.
	 * 
	 * @return
	 */
	private boolean hasSOACentralFile() {
		File centralF = new File(pathCentral);
		if (centralF.list().length > 0) {
			return true;
		}
		return false;
	}

	/**
	 * Metódo para enviar todas as batalhas ao cluster.
	 * 
	 * @param population
	 *            Que contém as configuracoes para a IA
	 */
	private void runBattles(Population population) {
		int numberSOA = 1;
		// montar a lista de batalhas que irão ocorrer

		for (int i = 0; i < TOTAL_PARTIDAS_ROUND; i++) {

			for (Chromosome cIA1 : population.getChromosomes().keySet()) {

				for (Chromosome cIA2 : population.getChromosomes().keySet()) {
					if (!cIA1.equals(cIA2)) {
						// System.out.println("IA1 = "+ convertTuple(cIA1)+ "
						// IA2 = "+ convertTuple(cIA2));

						// salvar arquivo na pasta log
						String strConfig = pathCentral + "/" + convertBasicTuple(cIA1) + "#" + convertBasicTuple(cIA2)
								+ "#" + i + "#" + atualGeneration + ".txt";
						File arqConfig = new File(strConfig);
						if (!arqConfig.exists()) {
							try {
								arqConfig.createNewFile();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						// escreve a configuração de teste
						try {
							FileWriter arq = new FileWriter(arqConfig, false);
							PrintWriter gravarArq = new PrintWriter(arq);

							gravarArq.println(convertBasicTuple(cIA1) + "#" + convertBasicTuple(cIA2) + "#" + i + "#"
									+ atualGeneration);

							gravarArq.flush();
							gravarArq.close();
							arq.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						/*
						 * try { Thread.sleep(300); } catch
						 * (InterruptedException e) { e.printStackTrace(); }
						 */
					}

				}
			}
		}
	}

	private String convertTuple(Chromosome cromo) {
		String tuple = "'";

		for (Integer integer : cromo.getGenes()) {
			tuple += integer + ";";
		}

		return tuple += "'";
	}

	private String convertBasicTuple(Chromosome cromo) {
		String tuple = "";

		for (Integer integer : cromo.getGenes()) {
			tuple += integer + ";";
		}

		return tuple;
	}

	private void copyFileUsingStream(File source, File dest) throws IOException {
		InputStream is = null;
		OutputStream os = null;
		try {
			is = new FileInputStream(source);
			os = new FileOutputStream(dest);
			byte[] buffer = new byte[1024];
			int length;
			while ((length = is.read(buffer)) > 0) {
				os.write(buffer, 0, length);
			}
		} finally {
			is.close();
			os.close();
		}
	}

	/**
	 * Envia o sinal de exit para todos os SOA clientes
	 */
	@Override
	public void finishProcess() {
		for (String soaFolder : this.SOA_Folders) {
			String strConfig = soaFolder;
			File f = new File(strConfig+"/exit");
			try {
				f.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

}