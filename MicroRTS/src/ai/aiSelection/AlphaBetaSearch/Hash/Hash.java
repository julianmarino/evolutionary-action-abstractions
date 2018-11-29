/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.aiSelection.AlphaBetaSearch.Hash;

import java.util.Random;

/**
 *
 * @author rubens
 */
public class Hash {
    // some data storage
    public final static int[][] UNIT_INDEX_HASH = new int[2][200];
    public final static HashValues[] VALUES = new HashValues[2];
    
    
    public class HashValues{
        int[] unitPositionHash = new int[2];
        int[] timeCanAttackHash = new int[2];
        int[] timeCanMoveHash = new int[2];
        int[] unitTypeHash = new int[2];
        int[] currentHPHash = new int[2];

        public HashValues(int seed) {
            Random rand = new Random(seed);
            
            for(int p = 0; p < 2; p++){
                for(int u =0; u < 200; u++){
                    Hash.UNIT_INDEX_HASH[p][u] = rand.nextInt();
                }
                unitPositionHash[p]	= rand.nextInt();
		timeCanAttackHash[p]	= rand.nextInt();
		timeCanMoveHash[p]	= rand.nextInt();
		unitTypeHash[p]		= rand.nextInt();
		currentHPHash[p]	= rand.nextInt();
            }
        }
        
        public int positionHash(int player, int x, int y){
            return hash32shift(hash32shift(unitPositionHash[player] ^ x) ^ y);
        }
        
        public int getAttackHash(int player, int value){
            return hash32shift(timeCanAttackHash[player] ^ value);
        }
        
        public int getMoveHash(int player, int value){
            return hash32shift(timeCanMoveHash[player] ^ value);
        }
        
        public int getUnitTypeHash(int player, int value){
            return hash32shift(unitTypeHash[player] ^ value);
        }
        
        public int getCurrentHPHash(int player, int value){
            return hash32shift(currentHPHash[player] ^ value);
        }
    }
    
    public int hash32shift(int key){
        key = ~key + (key << 15); // key = (key << 15) - key - 1;
	key = key ^ (key >> 12);
	key = key + (key << 2);
	key = key ^ (key >> 4);
	key = key * 2057; // key = (key + (key << 3)) + (key << 11);
	key = key ^ (key >> 16);
        
        return key;
        //String sInt = Integer.toUnsignedString(key);
	//return Integer.parseInt(sInt);
    }
    
    //Robert Jenkins' 32 bit integer hash function
    public int jenkinsHash(int a){
        a = (a+0x7ed55d16) + (a<<12);
	a = (a^0xc761c23c) ^ (a>>19);
	a = (a+0x165667b1) + (a<<5);
	a = (a+0xd3a2646c) ^ (a<<9);
	a = (a+0xfd7046c5) + (a<<3);
	a = (a^0xb55a4f09) ^ (a>>16);
	return a;
    }
    
    public void initHash(){
        Hash.VALUES[0] = new HashValues(0);
	Hash.VALUES[1] = new HashValues(1);
    }
    
    public int jenkinsHashCombine(int hash, int val){
        return hash32shift(hash ^ val);
    }
    
    public int magicHash(int hash, int player, int index){
        return hash32shift(hash ^ Hash.UNIT_INDEX_HASH[player][index]);
        //String sInt = Integer.toUnsignedString(hash32shift(hash ^ Hash.UNIT_INDEX_HASH[player][index]));
	//return Integer.parseInt(sInt);
    }
}
