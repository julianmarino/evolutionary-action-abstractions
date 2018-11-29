/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.ScriptsGenerator.Command;

import ai.asymmetric.PGS.*;
import tests.*;
import PVAI.EconomyRush;
import PVAI.EconomyRushBurster;
import PVAI.WorkerDefense;
import PVAI.RangedDefense;
import Standard.CombinedEvaluation;
import Standard.StrategyTactics;
import ai.core.AI;
import ai.*;
import ai.CMAB.CMABBuilder;
import ai.CMAB.CmabNaiveMCTS;
import ai.ScriptsGenerator.Chromosome;
import ai.ScriptsGenerator.ChromosomeAI;
import ai.ScriptsGenerator.ChromosomesBag;
import ai.ScriptsGenerator.Command.BasicAction.AttackBasic;
import ai.ScriptsGenerator.CommandInterfaces.ICommand;
import ai.ScriptsGenerator.TableGenerator.TableCommandsGenerator;
import ai.abstraction.HeavyRush;
import ai.abstraction.LightRush;
import ai.abstraction.RangedRush;
import ai.abstraction.WorkerRush;
import ai.abstraction.combat.Cluster;
import ai.abstraction.combat.KitterDPS;
import ai.abstraction.combat.NOKDPS;
import ai.abstraction.partialobservability.POLightRush;
import ai.abstraction.partialobservability.PORangedRush;
import ai.abstraction.pathfinding.AStarPathFinding;
import ai.abstraction.pathfinding.BFSPathFinding;
import ai.aiSelection.AlphaBetaSearch.AlphaBetaSearch;
import ai.asymmetric.GAB.GAB_oldVersion;
import ai.asymmetric.GAB.SandBox.AlphaBetaSearchAbstract;
import ai.asymmetric.GAB.SandBox.GAB;
import ai.asymmetric.GAB.SandBox.GAB_SandBox_Parcial_State;
import ai.asymmetric.PGS.SandBox.PGSmRTS_Paralel_JulianTest;
import ai.asymmetric.PGS.SandBox.PGSmRTS_Paralel_SandBox;
import ai.asymmetric.PGS.SandBox.PGSmRTS_SandBox;
import ai.asymmetric.SAB.SAB;
import ai.asymmetric.SAB.SAB_seed;
import ai.asymmetric.SSS.NSSS;
import ai.asymmetric.SSS.NSSSLimit;
import ai.asymmetric.SSS.NSSSRandom;
import ai.asymmetric.SSS.SSSIterationRandom;
import ai.asymmetric.SSS.SSSResponseMRTS;
import ai.asymmetric.SSS.SSSResponseMRTSRandom;
import ai.asymmetric.SSS.SSSmRTS;
import ai.asymmetric.SSS.SSSmRTSScriptChoice;
import ai.asymmetric.SSS.SSSmRTSScriptChoiceRandom;
import ai.cluster.CABA;
import ai.cluster.CABA_Enemy;
import ai.cluster.CIA;
import ai.cluster.CIA_Enemy;
import ai.cluster.CIA_EnemyEuclidieanInfluence;
import ai.cluster.CIA_EnemyWithTime;
import ai.cluster.CIA_PlayoutCluster;
import ai.cluster.CIA_PlayoutPower;
import ai.cluster.CIA_PlayoutTemporal;
import ai.cluster.CIA_TDLearning;
import ai.competition.capivara.Capivara;
import ai.competition.capivara.CmabAssymetricMCTS;
import ai.competition.tiamat.Tiamat;
import ai.configurablescript.BasicExpandedConfigurableScript;
import ai.configurablescript.ScriptsCreator;
import ai.evaluation.EvaluationFunctionForwarding;
import ai.evaluation.LTD2;
import ai.evaluation.LanchesterEvaluationFunction;
import ai.evaluation.PlayoutFunction;
import ai.evaluation.SimpleSqrtEvaluationFunction3;
import ai.mcts.naivemcts.NaiveMCTS;
import ai.minimax.ABCD.IDABCD;
import ai.portfolio.PortfolioAI;
import ai.puppet.PuppetSearchMCTS;
import ai.scv.SCVPlus;
import gui.PhysicalGameStatePanel;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import javax.swing.JFrame;
import rts.GameState;
import rts.PhysicalGameState;
import rts.Player;
import rts.PlayerAction;
import rts.units.Unit;
import rts.units.UnitTYpeTableBattle;
import rts.units.UnitTypeTable;
import static tests.ClusterTesteLeve.decodeScripts;
import static util.SOA.RoundRobinClusterLeve.decodeScripts;
import util.XMLWriter;

/**
 *
 * @author santi
 */
public class GameVisualSimulationTest {

    static String _nameStrategies = "", _enemy = "";
    static AI[] strategies = null;
    private static HashMap<BigDecimal, ArrayList<Integer>> scriptsTable;

    public static void main(String args[]) throws Exception {
        UnitTypeTable utt = new UnitTypeTable();
        //UnitTypeTable utt = new UnitTYpeTableBattle();
        //PhysicalGameState pgs = PhysicalGameState.load("maps/16x16/basesWorkers16x16.xml", utt);
        PhysicalGameState pgs = PhysicalGameState.load("maps/8x8/basesWorkers8x8A.xml", utt);
        //PhysicalGameState pgs = PhysicalGameState.load("maps/16x16/basesWorkers16x16A.xml", utt);        
        //PhysicalGameState pgs = PhysicalGameState.load("maps/BWDistantResources32x32.xml", utt);
        //PhysicalGameState pgs = PhysicalGameState.load("maps/32x32/basesWorkers32x32A.xml", utt);
        //PhysicalGameState pgs = PhysicalGameState.load("maps/24x24/basesWorkers24x24A.xml", utt);
        //PhysicalGameState pgs = PhysicalGameState.load("maps/BroodWar/(4)BloodBath.scmB.xml", utt);
        //PhysicalGameState pgs = PhysicalGameState.load("maps/8x8/FourBasesWorkers8x8.xml", utt);
        //PhysicalGameState pgs = PhysicalGameState.load("maps/16x16/TwoBasesBarracks16x16.xml", utt);
        //PhysicalGameState pgs = PhysicalGameState.load("maps/NoWhereToRun9x8.xml", utt);
        //PhysicalGameState pgs = PhysicalGameState.load("maps/DoubleGame24x24.xml", utt);
        //PhysicalGameState pgs = MapGenerator.basesWorkers8x8Obstacle();
        //testes 
        //PhysicalGameState pgs = PhysicalGameState.load("maps/24x24/basesWorkers24x24A.xml", utt);
        
        GameState gs = new GameState(pgs, utt);
        int MAXCYCLES = 8000;
        int PERIOD = 20;
        boolean gameover = false;

        Chromosome chrom = new Chromosome(utt);
        
        
        //AI ai1 = new RangedRush(utt);
        //AI ai1 = new WorkerRush(utt);
        //AI ai1 = new LightRush(utt);
        //AI ai1 = new HeavyRush(utt);
        //AI ai1 = new PassiveAI();
        //AI ai1 = new POLightRush(utt);
        //AI ai1 = new EconomyRush(utt);        
        //AI ai1 = new RangedDefense(utt);
        //AI ai1 = new EconomyRushBurster(utt);        
        //AI ai1 = new PassiveAI(utt);
        //AI ai1 = new NaiveMCTS(utt);
        //AI ai1 = new PortfolioAI(utt);
        //AI ai1 = new PVAI(utt);
        //AI ai1 = new WorkerRushPlusPlus(utt);
        //AI ai1 = new RandomBiasedAI(utt);
        //AI ai1 = new PuppetSearchMCTS(utt);
        //AI ai1 = new PortfolioAI(utt);
        //AI ai1 = new POLightRush(utt);
        //AI ai1 = new WorkerRush(utt);
        //AI ai1 = new PGSmRTS_SandBox(utt);
        //AI ai1 = new PGSmRTS(utt); 
        //AI ai1 = new GAB(utt);
        //AI ai1 = new SAB(utt);
        //AI ai1 = new IDABCD(utt);
        //AI ai1 = new StrategyTactics(utt);
        //AI ai1 = new PGSSCriptChoice(utt, decodeScripts(utt, "65;184;217;"), "bGA");
        //AI ai1 = new SSSmRTS(utt);
        //AI ai1 = new AlphaBetaSearch(utt, new LTD2(), "LTD2");
        //AI ai1 = new AlphaBetaSearch(utt, new PlayoutFunction(new RandomBiasedAI(utt), new RandomBiasedAI(utt), new LTD2()), "Play_Rand_LTD2");
        //AI ai1 = new AlphaBetaSearch(utt, new PlayoutFunction(new KitterDPS(utt), new KitterDPS(utt), new LTD2()), "Play_KitterDPS_LTD2");
        //AI ai1 = new AlphaBetaSearch(utt, new PlayoutFunction(new NOKDPS(utt), new NOKDPS(utt), new LTD2()), "Play_NOKDPS_LTD2");
        //AI ai1 = new AlphaBetaSearch(utt, new PlayoutFunction(new POLightRush(utt), new POLightRush(utt), new LTD2()), "Play_POLightRush_LTD2");        
        //AI ai1 = new CmabNaiveMCTS(utt);
        //AI ai1 = new PGSmRTS_Paralel_JulianTest(utt);
        //AI ai1 = new PGSmRTSRandom(utt, 4, 200);
        //AI ai2 = new PGSSCriptChoiceRandom(utt, decodeScripts(utt, "23;73;208;208;193;"), "PGSRSym",4,200);
        //AI ai1 = new PGSResponseMRTS(utt);
        //AI ai1 = new NGSRandom(utt);
        //AI ai1 = new NSSSRandom(utt);
        //AI ai1 = new NGS(utt);
        //AI ai1 = new NGSLimit(utt);
        //AI ai1 = new NSSS(utt);
        //AI ai1 = new NSSSLimit(utt);
        //AI ai1 = new SSSmRTSScriptChoiceRandom(utt, decodeScripts(utt, "46;141;273;195;"), "GA_PGSRLim",4,200);
        //AI ai1 = new StrategyTactics(utt);
        
        //AI ai1 = new WorkerRush(utt);
        //AI ai2 = new CmabAssymetricMCTS(100, -1, 100, 1, 0.3f, 
        //                                     0.0f, 0.4f, 0, new RandomBiasedAI(utt), 
        //                                     new SimpleSqrtEvaluationFunction3(), true, utt, 
        //                                    "ManagerClosestEnemy", 1,decodeScripts(utt, "48;0;")); //A3N
        //AI ai2 = new GAB(utt);
        //AI ai1 = new Tiamat(utt);
        //AI ai2 = new Capivara(utt);
        //AI ai2 = new SCVPlus(utt);
        //AI ai2 = new SCVPlus(utt, pgs.getHeight(), pgs.getWidth());
        //AI ai2 = new CMABBuilder(100, -1, 100, 1, 0, new RandomBiasedAI(utt), new SimpleSqrtEvaluationFunction3(), 0, utt, new ArrayList<>(), "CmabCombinatorialGenerator", "ManagerClosestEnemy", 1);
        //AI ai1 = new StrategyTactics(utt);
        //AI ai2 = new SSSmRTS(utt);
        //AI ai2 = new PGSIterationRandom(utt); 	
        //AI ai2 = new SSSIterationRandom(utt); 
        //AI ai1 = new SSSResponseMRTS(utt);
        //AI ai2 = new NaiveMCTS(utt);
        //AI ai2 = new CIA_TDLearning(utt);
        //AI ai2 = new CIA_PlayoutTemporal(utt);
        //AI ai2 = new CIA_PlayoutPower(utt);
        //AI ai2 = new CIA_PlayoutCluster(utt);
        //AI ai1 = new AlphaBetaSearch(utt);
        //AI ai2 = new CABA(utt);
        //AI ai2 = new CABA_Enemy(utt);
        //AI ai2 = new CIA_Enemy(utt);
        //AI ai2 = new CIA(utt);
        //AI ai2 = new CIA_EnemyEuclidieanInfluence(utt);
        //AI ai2 = new CIA_EnemyWithTime(utt);
        //AI ai1 = new PassiveAI();
        //AI ai2 = new SAB(utt);
        //AI ai2 = new SAB_seed(utt);
        //AI ai2 = new Cluster(utt);
        //AI ai2 = new KitterDPS(utt);
        //AI ai1 = new NOKDPS(utt);
        //AI ai2 = new GAB(utt);
        //AI ai2 = new GAB(utt);
        //AI ai2 = new AlphaBetaSearchAbstract(utt);
        //AI ai2 = new GAB_SandBox_Parcial_State(utt);
        //AI ai2 = new GAB(utt);
        AI ai2 = new WorkerRush(utt);
        //AI ai2 = new PuppetSearchMCTS(utt);
        //AI ai2 = new POLightRush(utt);
        //AI ai2 = new RangedDefense(utt);
        //AI ai2 = new PVAI(utt);
        //AI ai2 = new PVAIML_onlyEnemy(utt);
        //AI ai2 = new PVAIML_SLMS(utt);
        //AI ai2 = new PVAIML_NaiveMS(utt);
        //AI ai2 = new PVAIML_ED(utt);
        //AI ai2 = new PVAIML_SLFW(utt);
        //AI ai2 = new PVAIML_SL(utt);
        //AI ai2 = new PVAIML_Naive(utt);
        //AI ai2 = new PVAIML_NaiveFW(utt);
        //AI ai2 = new PVAIML_FW(utt);
        //AI ai2 = new PVAIML_EDP(utt);
        //AI ai2 = new PVAIML_SLFWMS(utt);
        //AI ai2 = new PVAICluster(4, utt, "EconomyRush(AStarPathFinding)");
        
//        AI ai2 = new PassiveAI(utt);
//        ChromosomesBag bag = new ChromosomesBag(utt);
//        AI ai1 = new ChromosomeAI(utt, bag.ChromosomesBag41(utt), "");
//          AI ai2 = new PassiveAI(utt);
        
        
        List<ICommand> commandsAI1=new ArrayList<>(); 
        TableCommandsGenerator tcg=TableCommandsGenerator.getInstance(utt);
        
        //System.out.println("Table "+(tcg.getCommandByID(5138).toString()));
        commandsAI1.add(tcg.getCommandByID(5138));;
//        commandsAI1.add(tcg.getCommandByID(5147));;
//        commandsAI1.add(tcg.getCommandByID(2101));;
//        commandsAI1.add(tcg.getCommandByID(1364));;
//        commandsAI1.add(tcg.getCommandByID(1267));;
//        commandsAI1.add(tcg.getCommandByID(3591));;
//        commandsAI1.add(tcg.getCommandByID(7840));;
//        commandsAI1.add(tcg.getCommandByID(4778));;
//        commandsAI1.add(tcg.getCommandByID(1050));;
//        commandsAI1.add(tcg.getCommandByID(171));;
//        commandsAI1.add(tcg.getCommandByID(1361));;
//        commandsAI1.add(tcg.getCommandByID(7989));;
//        commandsAI1.add(tcg.getCommandByID(5442));;
//        commandsAI1.add(tcg.getCommandByID(4073));;
//        commandsAI1.add(tcg.getCommandByID(2905));;
//        AI ai1 = new ChromosomeAI(utt, commandsAI1, "");
//        
//        List<ICommand> commandsAI2=new ArrayList<>(); 
//        tcg=new TableCommandsGenerator(utt);
//        commandsAI2.add(tcg.getCommandByID(2818));;
//        commandsAI2.add(tcg.getCommandByID(5147));;
//        commandsAI2.add(tcg.getCommandByID(2101));;
//        commandsAI2.add(tcg.getCommandByID(2924));;
//        commandsAI2.add(tcg.getCommandByID(4915));;
//        commandsAI2.add(tcg.getCommandByID(3748));;
//        AI ai2 = new ChromosomeAI(utt, commandsAI2, "");
       // AI ai2 = new PassiveAI(utt);        
          
        buildScriptsTable();
        
        ArrayList<Integer> iScriptsAi1 = new ArrayList<>();
        ArrayList<Integer> iScriptsAi2 = new ArrayList<>();
        
        //iScriptsAi1.add(758);
        
        iScriptsAi1.add(2383);
        iScriptsAi1.add(1262);
        iScriptsAi1.add(1323);
        iScriptsAi1.add(2716);
        iScriptsAi1.add(2772);
//        iScriptsAi1.add(1094);
//        iScriptsAi1.add(1505);
//        iScriptsAi1.add(1105);
        
//        System.out.println(decodeScripts2(utt, iScriptsAi1).get(0).toString());
//        System.out.println(decodeScripts2(utt, iScriptsAi1).get(1).toString());
//        System.out.println(decodeScripts2(utt, iScriptsAi1).get(2).toString());
//        System.out.println(decodeScripts2(utt, iScriptsAi1).get(3).toString());
//        System.out.println(decodeScripts2(utt, iScriptsAi1).get(4).toString());
//        System.out.println(decodeScripts2(utt, iScriptsAi1).get(5).toString());
//        System.out.println(decodeScripts2(utt, iScriptsAi1).get(6).toString());
//        iScriptsAi1.add(404);
//        iScriptsAi1.add(402);
        
        
        iScriptsAi2.add(1512);
        iScriptsAi2.add(1594);
        iScriptsAi2.add(1544);
//        iScriptsAi2.add(142);
//        iScriptsAi2.add(267);
//        iScriptsAi2.add(640);
//        iScriptsAi2.add(716);
//        iScriptsAi2.add(404);
//        iScriptsAi2.add(188);
        
        AI ai1 = new PGSSCriptChoiceRandom(utt, decodeScripts2(utt, iScriptsAi1), "PGSR", 2, 200);
        //AI ai2 = new PGSSCriptChoiceRandom(utt, decodeScripts2(utt, iScriptsAi2), "PGSR", 2, 200);
        
        //AI ai1= decodeScripts2(utt, iScriptsAi1).get(0);
        
        System.out.println("---------AI's---------");
        System.out.println("AI 1 = "+ai1.toString());
        System.out.println("AI 2 = "+ai2.toString()+"\n");        
        
        //método para fazer a troca dos players
        JFrame w = PhysicalGameStatePanel.newVisualizer(gs, 720, 720, false, PhysicalGameStatePanel.COLORSCHEME_BLACK);
        //JFrame w = PhysicalGameStatePanel.newVisualizer(gs, 720, 720, false, PhysicalGameStatePanel.COLORSCHEME_WHITE); 
        long startTime = System.currentTimeMillis();
        long nextTimeToUpdate = System.currentTimeMillis() + PERIOD;
        do {
            if (System.currentTimeMillis() >= nextTimeToUpdate) {
                startTime = System.currentTimeMillis();
                
                PlayerAction pa1 = ai1.getAction(0, gs);  
                if( (System.currentTimeMillis() - startTime) >0){
                System.out.println("Tempo de execução P1="+(startTime = System.currentTimeMillis() - startTime));
                }
                //System.out.println("Action A1 ="+ pa1.toString());
                
                startTime = System.currentTimeMillis();
                PlayerAction pa2 = ai2.getAction(1, gs);
                
                if( (System.currentTimeMillis() - startTime) >0){
                   System.out.println("Tempo de execução P2="+(startTime = System.currentTimeMillis() - startTime));
                }
                //System.out.println("Action A2 ="+ pa2.toString());
                
                gs.issueSafe(pa1);
                gs.issueSafe(pa2);
                
                // simulate:
                gameover = gs.cycle();
                w.repaint();
                nextTimeToUpdate += PERIOD;
            } else {
                try {
                    Thread.sleep(1);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
           /* PhysicalGameState physical = gs.getPhysicalGameState();
            System.out.println("---------TIME---------");
            System.out.println(gs.getTime());
            for (Unit u : physical.getUnits()) {
                if (u.getPlayer() == 1) {
                    System.out.println("Player 1: Unity - " + u.toString());
                }
                else if (u.getPlayer() == 0) {
                     System.out.println("Player 0: Unity - " + u.toString());
                } 
            }
            */
        } while (!gameover && gs.getTime() < MAXCYCLES);
        System.out.println("Winner " + Integer.toString(gs.winner()));
        System.out.println("Game Over");
    }
    
    public static List<AI> decodeScripts(UnitTypeTable utt, String sScripts) {
        
        //decompõe a tupla
        ArrayList<Integer> iScriptsAi1 = new ArrayList<>();
        String[] itens = sScripts.split(";");

        for (String element : itens) {
            iScriptsAi1.add(Integer.decode(element));
        }
        
        List<AI> scriptsAI = new ArrayList<>();

        ScriptsCreator sc = new ScriptsCreator(utt,300);
        ArrayList<BasicExpandedConfigurableScript> scriptsCompleteSet = sc.getScriptsMixReducedSet();

        iScriptsAi1.forEach((idSc) -> {
            scriptsAI.add(scriptsCompleteSet.get(idSc));
        });

        return scriptsAI;
    }
    
    public static List<AI> decodeScripts2(UnitTypeTable utt, ArrayList<Integer> iScripts) {
        List<AI> scriptsAI = new ArrayList<>();

        
        for (Integer idSc : iScripts) {
        	System.out.println("tam tab"+scriptsTable.size());
        	System.out.println("id "+idSc+" Elems "+scriptsTable.get(BigDecimal.valueOf(idSc)));
            scriptsAI.add(buildScript(utt,scriptsTable.get(BigDecimal.valueOf(idSc))));
            //System.out.println("kamelotagem ");
        }

        return scriptsAI;
    }
    
    public static AI buildScript(UnitTypeTable utt, ArrayList<Integer> iRules) {
    	//System.out.println("laut");
    	TableCommandsGenerator tcg=TableCommandsGenerator.getInstance(utt);
    	List<ICommand> commands=new ArrayList<>();
    	System.out.println("sizeeiRules "+iRules.size());
        for (Integer idSc : iRules) {
        	System.out.print("idSc "+idSc+" ");
        	commands.add(tcg.getCommandByID(idSc));;
        	System.out.println(tcg.getCommandByID(idSc).toString());
        }   	
    	AI aiscript = new ChromosomeAI(utt,commands , "P1");

        return aiscript;
    }
    
    public static HashMap<BigDecimal, ArrayList<Integer>> buildScriptsTable(){
    	
    	scriptsTable=new HashMap<BigDecimal, ArrayList<Integer>>();
    	ArrayList<Integer> idsRulesList;
    	try (BufferedReader br = new BufferedReader(new FileReader("ScriptsTable.txt"))) {
    	    String line;
    	    while ((line = br.readLine()) != null) {
    	    	idsRulesList=new ArrayList<>();
    	    	String[] strArray = line.split(" ");
    	    	int[] intArray = new int[strArray.length];
    	    	for(int i = 0; i < strArray.length; i++) {
    	    	    intArray[i] = Integer.parseInt(strArray[i]);
    	    	}
    	    	int idScript=intArray[0];
    	    	int[] rules = Arrays.copyOfRange(intArray, 1, intArray.length);
    	    	
    	    	for (int i : rules)
    	    	{
    	    		idsRulesList.add(i);
    	    	}
    	    	
    	    	scriptsTable.put( BigDecimal.valueOf(idScript),idsRulesList);
    	    }
    	} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	
    return scriptsTable;	
    }


}
