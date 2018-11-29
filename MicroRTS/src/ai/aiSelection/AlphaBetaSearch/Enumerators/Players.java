/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.aiSelection.AlphaBetaSearch.Enumerators;

/**
 *
 * @author rubens
 */
public enum Players {
    Player_One(0), Player_Two(1), Player_None(2), Player_Both(3);
    
    private final int codigo;
    Players(int codigo){
        this.codigo = codigo;
    }
    
    public int codigo(){
        return this.codigo;
    }
    
    public static Players porCodigo(int codigo){
        for (Players p : Players.values()) {
            if(codigo == p.codigo()){
                return p;
            }
        }
        throw new IllegalArgumentException("codigo invalido");
    }
}
