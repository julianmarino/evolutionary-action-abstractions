/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package util.SOA;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author rubens Classe utilizada para gerir o serviço SOA para testes
 * totalmente observáveis.
 */
public class SOARoundRobinTOScale_Chromo_Scripts {

    public static void main(String args[]) throws Exception {
        String pathSOA = args[0];
        String pathLog = args[1];
        int qtdMapas = 1;
        //String pathSOA = "/home/rubens/cluster/USP/Test_map8_10script_USP/configSOA/SOA1/";
        //String pathLog = "/home/rubens/cluster/USP/Test_map8_10script_USP/logs/";
        File SOA = new File(pathSOA);
        if (!SOA.exists()) {
            SOA.mkdir();
        }
        System.out.println(pathSOA);
        System.out.println(pathLog);

        while (!finalizarSOA(pathSOA)) {
            //procuro a existencia dos arquivos a serem processados.
            ArrayList<String> mathSOA = buscarAquivos(pathSOA);
            for (String arquivo : mathSOA) {
                System.out.println("Processando arquivo " + arquivo);
                try {
                    for (int map = 0; map < qtdMapas; map++) {
                        if (processarMatch(pathLog, arquivo, map)) {
                        }
                        System.gc();
                    }
                    //remove o arquivo da pasta 
                    File remove = new File(arquivo);
                    remove.delete();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            try {
                System.out.println("Waiting...");
                Thread.sleep(10000);
            } catch (InterruptedException e) {
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
    private static boolean processarMatch(String pathLog, String arquivo, int map) {
        //ler o arquivo e pegar a linha com dados
        String config = getLinha(arquivo);
        String[] itens = config.split("#");

        RoundRobinTOScaleChromoScripts control = new RoundRobinTOScaleChromoScripts();
        try {
            return control.run(itens[0].trim(),
                    itens[1].trim(),
                    Integer.decode(itens[2]),
                    Integer.decode(itens[3]), pathLog, map);
        } catch (Exception ex) {
            Logger.getLogger(SOARoundRobinTOScale_Chromo_Scripts.class.getName()).log(Level.SEVERE, null, ex);
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

}
