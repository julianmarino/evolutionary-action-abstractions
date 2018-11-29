/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.asymmetric.PGS.SandBox;

import ai.core.AI;
import ai.evaluation.EvaluationFunction;
import java.util.logging.Level;
import java.util.logging.Logger;
import rts.GameState;

/**
 *
 * @author rubens
 */
public class seedPlayerThread implements Runnable{
    
    int player;
    int playerEnemy;
    AI seed;
    AI enemyAi;
    GameState gs;
    double eval= -9999;
    int lookahead;
    EvaluationFunction evaluation = null;

    public seedPlayerThread() {
    }
    
    public void initialInf(int player, AI seed, AI enemy, GameState gsOriginal, int lookahead, EvaluationFunction ev){
        this.seed = seed;
        this.enemyAi = enemy;
        this.gs = gsOriginal;
        this.lookahead = lookahead;
        this.evaluation = ev;
    }
    
    public double getEval(){
        return eval;
    }
    
    @Override
    public void run() {
        seed.reset();
        enemyAi.reset();
        int timeLimit = gs.getTime() + lookahead;
        boolean gameover = false;
        while (!gameover && gs.getTime() < timeLimit) {
                try {
                    gs.issue(seed.getAction(player, gs));
                    gs.issue(enemyAi.getAction(1 - player, gs));
                } catch (Exception ex) {
                    System.out.println("Erro playout thread"+ex);
                    Logger.getLogger(seedPlayerThread.class.getName()).log(Level.SEVERE, null, ex);
                }
                gameover = gs.cycle();
        }
        eval = evaluation.evaluate(player, 1 - player, gs);
    }
    
}
