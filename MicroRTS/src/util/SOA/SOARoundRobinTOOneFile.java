/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util.SOA;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rubens Classe utilizada para gerir o serviço SOA para testes
 * totalmente observáveis.
 */
public class SOARoundRobinTOOneFile {

    public static void main(String args[]) throws Exception {
        String pathSOA = args[0];
        String pathLog = args[1];
        
        int qtdMapas = 1;
        //String pathSOA = "/home/rubens/cluster/USP/Test_map8_10script_USP/batchsSOA/SOA1.txt";
        //String pathLog = "/home/rubens/cluster/USP/Test_map8_10script_USP/logs/";
        String SOANumber = pathSOA.substring(pathSOA.lastIndexOf("/")+1, pathSOA.lastIndexOf("."));
        File SOA = new File(pathSOA);

        System.out.println(pathSOA);
        System.out.println(pathLog);

        //ler todas as linhas.
        ArrayList<String> matchs = processFile(SOA);

        //processar todas as linhas.
        for (String arquivo : matchs) {
            System.out.println("Processando arquivo " + arquivo);
            try {
                for (int map = 0; map < qtdMapas; map++) {
                    if (processarMatch(pathLog, arquivo, map, SOANumber)) {
                    }
                    System.gc();
                }
                //salva o match processado
                recordMatch(pathSOA, arquivo, SOANumber);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
    }

    /**
     * Irá chamar a classe que faz processará o arquivo do confronto.
     *
     * @param pathLog Stringo do caminho onde será salvo o resultado
     * @param arquivo String com o nome do arquivo que contém a configuração
     * @return True se processado corretamente
     */
    private static boolean processarMatch(String pathLog, String arquivo, int map, String SOANumber) {
        //ler o arquivo e pegar a linha com dados
        String config = arquivo;
        String[] itens = config.split("#");

        RoundRobinTOWRDominance control = new RoundRobinTOWRDominance();
        try {
            return control.run(itens[0].trim(),
                    itens[1].trim(),
                    Integer.decode(itens[2]),
                    Integer.decode(itens[3]), pathLog, map, SOANumber);
        } catch (Exception ex) {
            Logger.getLogger(SOARoundRobinTOOneFile.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    /**
     * Retorna todos os arquivos da pasta SOA para serem processados
     *
     * @param pathSOA String com o caminho da pasta gerenciada
     * @return Lista de caminhos para os arquivos que serão processados.
     */
    private static ArrayList<String> buscarAquivos(String pathSOA) {
        ArrayList<String> arquivos = new ArrayList<>();
        File diretorio = new File(pathSOA);
        buscarParcial(diretorio, ".txt", arquivos);

        return arquivos;
    }

    /**
     * Irá verificar se existe o arquivo com o nome exit na pasta SOA.
     *
     * @param pathSOA Caminho da pasta de controle de serviços.
     * @return true se encontrar o arquivo exit
     */
    private static boolean finalizarSOA(String pathSOA) {
        ArrayList<String> arquivos = new ArrayList<>();
        File diretorio = new File(pathSOA);
        buscar(diretorio, "exit", arquivos);

        if (arquivos.size() > 0) {
            return true;
        }

        return false;
    }

    /**
     * Realiza a busca (recursiva) de todos os arquivos com o nome informado
     *
     * @param arquivo = File contendo o caminho que se deseja procurar.
     * @param palavra = String com o nome que se deseja buscar
     * @param lista = ArrayList<String> que retornará os arquivos encontrados
     * @return Lista de Strings com todos os caminhos absolutos dos arquivos com
     * o nome encontrado
     */
    public static ArrayList<String> buscar(File arquivo, String palavra, ArrayList<String> lista) {
        if (arquivo.isDirectory()) {
            File[] subPastas = arquivo.listFiles();
            for (int i = 0; i < subPastas.length; i++) {
                lista = buscar(subPastas[i], palavra, lista);
                if (arquivo.getName().equalsIgnoreCase(palavra)) {
                    lista.add(arquivo.getAbsolutePath());
                } else if (arquivo.getName().indexOf(palavra) > -1) {
                    lista.add(arquivo.getAbsolutePath());
                }
            }
        } else if (arquivo.getName().equalsIgnoreCase(palavra)) {
            lista.add(arquivo.getAbsolutePath());
        }
        //else if (arquivo.getName().indexOf(palavra) > -1) lista.add(arquivo.getAbsolutePath());
        return lista;
    }

    public static ArrayList<String> buscarParcial(File arquivo, String palavra, ArrayList<String> lista) {
        if (arquivo.isDirectory()) {
            File[] subPastas = arquivo.listFiles();
            for (int i = 0; i < subPastas.length; i++) {
                lista = buscarParcial(subPastas[i], palavra, lista);
                if (arquivo.getName().equalsIgnoreCase(palavra)) {
                    lista.add(arquivo.getAbsolutePath());
                } else if (arquivo.getName().contains(palavra)) {
                    lista.add(arquivo.getAbsolutePath());
                }
            }
        } else if (arquivo.getName().equalsIgnoreCase(palavra)) {
            lista.add(arquivo.getAbsolutePath());
        } else if (arquivo.getName().contains(palavra)) {
            lista.add(arquivo.getAbsolutePath());
        }
        return lista;
    }

    private static String getLinha(String arquivo) {
        File file = new File(arquivo);
        String linha = "";
        try {
            FileReader arq = new FileReader(file);
            java.io.BufferedReader learArq = new BufferedReader(arq);
            linha = learArq.readLine();

            arq.close();
        } catch (Exception e) {
            System.err.printf("Erro na leitura da linha de configuração");
            System.out.println(e.toString());
        }
        return linha;
    }

    private static ArrayList<String> processFile(File SOA) {
        ArrayList<String> temp = new ArrayList<>();
        String linha = "";
        try {
            FileReader arq = new FileReader(SOA);
            BufferedReader learArq = new BufferedReader(arq);
            linha = learArq.readLine();
            while (linha != null) {
                temp.add(linha);
                linha = learArq.readLine();
            }
            arq.close();

        } catch (Exception e) {
            System.err.printf("Fail during read process %s.\n", e.getMessage());
            System.out.println(e.toString());
        }

        return temp;
    }

    private static void recordMatch(String pathSOA, String arquivo, String SOANumber) {
        String pathbackup = pathSOA.replace(".txt", "");
        pathbackup += SOANumber+"_backup.txt";
        try {
            FileWriter arq = new FileWriter(pathbackup, true);
            PrintWriter gravarArq = new PrintWriter(arq);

            gravarArq.println(arquivo);

            gravarArq.flush();
            gravarArq.close();
            arq.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
