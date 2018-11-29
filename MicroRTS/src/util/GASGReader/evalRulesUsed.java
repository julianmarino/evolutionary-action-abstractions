/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package util.GASGReader;

import ai.ScriptsGenerator.CommandInterfaces.ICommand;
import ai.ScriptsGenerator.TableGenerator.TableCommandsGenerator;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import rts.units.UnitTypeTable;

public class evalRulesUsed {

    //k = generation    value = list of IDs
    public static HashMap<Integer, HashSet<Integer>> listOfScripts = new HashMap<>();
    public static HashMap<Integer, HashSet<String>> listOfRules = new HashMap<>();
    public static TableCommandsGenerator tcg = null;

    public static void main(String args[]) throws Exception {
        UnitTypeTable utt = new UnitTypeTable();
        List<ICommand> commandsAI1 = new ArrayList<>();
        tcg = TableCommandsGenerator.getInstance(utt);

        updateListOfScripts();
        updateListOfRules();
        printRules();
    }

    private static void updateListOfScripts() {
        File arqTour = new File("/home/rubens/MicroRTS/deepData/GA_24.o191365");

        String linha;
        try {
            FileReader arq = new FileReader(arqTour);
            BufferedReader learArq = new BufferedReader(arq);

            linha = learArq.readLine();
            HashSet<Integer> scripts = new HashSet<>();
            int generation = 0;
            while (linha != null) {
                if (linha.contains("Log - Generation =")) {
                    scripts = new HashSet<>();
                    linha = linha.replace("Log - Generation = ", "");
                    generation = Integer.parseInt(linha.trim());
                }
                if (linha.contains("Chromosome")) {
                    linha = linha.replace("Chromosome", "");
                    linha = linha.trim();
                    linha = linha.replace(" ", ";");
                    String[] itens = linha.split(";");
                    for (int i = 0; i < itens.length; i++) {
                        String iten = itens[i];
                        scripts.add(Integer.parseInt(iten.trim()));
                    }
                    listOfScripts.put(generation, scripts);
                }

                linha = learArq.readLine();
            }

            //System.out.println("close");
            arq.close();

        } catch (Exception e) {
            System.err.printf("Function: processBattles Class: Main processBattles ---> Erro na abertura do arquivo: %s.\n", e.getMessage() + " " + arqTour);
            System.out.println(e.toString());
        }
    }

    private static void updateListOfRules() {
        File arqTour = new File("/home/rubens/MicroRTS/deepData/ScriptsTable.txt");

        String linha;
        try {
            FileReader arq = new FileReader(arqTour);
            BufferedReader learArq = new BufferedReader(arq);

            linha = learArq.readLine();

            while (linha != null) {
                linha = linha.replace(" ", ";");
                String[] itens = linha.split(";");

                insertDecofiedRule(itens);

                linha = learArq.readLine();
            }
            //System.out.println("close");
            arq.close();

        } catch (Exception e) {
            System.err.printf("Function: processBattles Class: Main processBattles ---> Erro na abertura do arquivo: %s.\n", e.getMessage() + " " + arqTour);
            System.out.println(e.toString());
        }
    }

    private static void insertDecofiedRule(String[] itens) {
        //busco o id itens[0]
        Integer ID = Integer.parseInt(itens[0]);

        for (Integer idGenet : listOfScripts.keySet()) {
            HashSet<Integer> scripts = listOfScripts.get(idGenet);
            if (scripts.contains(ID)) {
                HashSet<String> rules = new HashSet<>();
                if (listOfRules.containsKey(idGenet)) {
                    rules = listOfRules.get(idGenet);
                }

                for (int i = 1; i < itens.length; i++) {
                    String iten = itens[i];
                    //rules.add(tcg.getCommandByID(Integer.parseInt(iten.trim())).toString());
                    rules.add(decodePrint(tcg.getCommandByID(Integer.parseInt(iten.trim())).toString()));
                }

                listOfRules.put(idGenet, rules);
            }

        }

    }

    private static String decodePrint(String commandString) {
        String clearCommand = "{";
        
        if (commandString.contains("NAllyUnitsAttacking")) {
            clearCommand += "NAllyUnitsAttacking;";
        }
        if (commandString.contains("NAllyUnitsHarvesting")) {
            clearCommand += "NAllyUnitsHarvesting;";
        }
        if (commandString.contains("NAllyUnitsofType")) {
            clearCommand += "NAllyUnitsofType;";
        }
        if (commandString.contains("NEnemyUnitsofType")) {
            clearCommand += "NEnemyUnitsofType;";
        }
        if (commandString.contains("MoveToCoordinatesBasic")) {
            clearCommand += "MoveToCoordinatesBasic;";
        }
        if (commandString.contains("MoveToUnitBasic")) {
            clearCommand += "MoveToUnitBasic;";
        }
        if (commandString.contains("AllyRange")) {
            clearCommand += "AllyRange;";
        }
        if (commandString.contains("DistanceFromEnemy")) {
            clearCommand += "DistanceFromEnemy;";
        }
        if (commandString.contains("EnemyRange")) {
            clearCommand += "EnemyRange;";
        }
        if (commandString.contains("HarvestBasic")) {
            clearCommand += "HarvestBasic;";
        }
        if (commandString.contains("BuildBasic")) {
            clearCommand += "BuildBasic;";
        }
        if (commandString.contains("TrainBasic")) {
            clearCommand += "TrainBasic;";
        }
        if (commandString.contains("AttackBasic")) {
            clearCommand += "AttackBasic;";
        }
        
        
        clearCommand += "}";
        return clearCommand;
    }

    private static void printRules() {
        for (Integer key : listOfRules.keySet()) {
            System.out.println("Generation "+ key);
            System.out.println("Rules "+ listOfRules.get(key));
        }
    }
}
