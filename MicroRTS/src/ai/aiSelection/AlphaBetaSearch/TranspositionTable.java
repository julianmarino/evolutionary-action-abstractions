/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.aiSelection.AlphaBetaSearch;

import ai.aiSelection.AlphaBetaSearch.Enumerators.TTEntryEnum;
import java.util.ArrayList;

/**
 *
 * @author rubens
 */
public class TranspositionTable {

    private ArrayList<TTEntry> TT;
    private int size;
    private int minIndex;
    private int maxIndex;

    public int getIndex(int hash1) {
        return hash1 % size;
    }

    private int collisions,
            lookups,
            found,
            notFound,
            saves,
            saveOverwriteSelf,
            saveOverwriteOther,
            saveEmpty;

    public TranspositionTable() {
        this.TT = new ArrayList<>();
        //this is necessary em Java?
        for (int i = 0; i < 100000; i++) {
            //this.TT.add(new TTEntry());
            this.TT.add(i, new TTEntry());
        }
        this.size = 100000;
        this.collisions = 0;
        this.lookups = 0;
        this.found = 0;
        this.notFound = 0;
        this.saves = 0;
        this.minIndex = 0;
        this.maxIndex = 0;
        this.saveOverwriteSelf = 0;
        this.saveOverwriteOther = 0;
        this.saveEmpty = 0;
    }

    public ArrayList<TTEntry> getTT() {
        return TT;
    }

    public void setTT(ArrayList<TTEntry> TT) {
        this.TT = TT;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getMinIndex() {
        return minIndex;
    }

    public void setMinIndex(int minIndex) {
        this.minIndex = minIndex;
    }

    public int getMaxIndex() {
        return maxIndex;
    }

    public void setMaxIndex(int maxIndex) {
        this.maxIndex = maxIndex;
    }

    public int getCollisions() {
        return collisions;
    }

    public void setCollisions(int collisions) {
        this.collisions = collisions;
    }

    public int getLookups() {
        return lookups;
    }

    public void setLookups(int lookups) {
        this.lookups = lookups;
    }

    public int getFound() {
        return found;
    }

    public void setFound(int found) {
        this.found = found;
    }

    public int getNotFound() {
        return notFound;
    }

    public void setNotFound(int notFound) {
        this.notFound = notFound;
    }

    public int getSaves() {
        return saves;
    }

    public void setSaves(int saves) {
        this.saves = saves;
    }

    public int getSaveOverwriteSelf() {
        return saveOverwriteSelf;
    }

    public void setSaveOverwriteSelf(int saveOverwriteSelf) {
        this.saveOverwriteSelf = saveOverwriteSelf;
    }

    public int getSaveOverwriteOther() {
        return saveOverwriteOther;
    }

    public void setSaveOverwriteOther(int saveOverwriteOther) {
        this.saveOverwriteOther = saveOverwriteOther;
    }

    public int getSaveEmpty() {
        return saveEmpty;
    }

    public void setSaveEmpty(int saveEmpty) {
        this.saveEmpty = saveEmpty;
    }
    
    public TTEntry get(int hash){
        return TT.get(getIndex(hash));
    }
    
    public int getSaveIndex(int index, int hash2, int depht){
        int worstDepth = 1000;
        int worstDepthIndex = index;
        
        // scan ahead to find the best spot to store this entryw
        for (int i = index; (i<(index+10)) && (i <TT.size()); i++) {
            TTEntry tempTT = get(i);  //verificar!
            // if this index holds worse data about the current state, use it to overwrite
            if(tempTT.getHash2()== hash2 && tempTT.getDepth() < depht){
                return i;
            }
            else if(tempTT.getHash2() == hash2 && tempTT.getDepth() > depht){
                System.out.println("We shouldn't get here");
            }
            // otherwise if this entry is empty, use it
            else if(!tempTT.isValid()){
                return i;
            }
            // otherwise if the hashes don't match, check to see how old the data is
            else if(tempTT.getHash2() != hash2){
                if(tempTT.getDepth() < worstDepth){
                    worstDepth = tempTT.getDepth();
                    worstDepthIndex = i;
                }
            }
        }
        
        
        return worstDepthIndex;
    }
    
    public void save(int hash1, int hash2, StateEvalScore value, int depth, TTEntryEnum type, 
                    int firstPlayer, AlphaBetaMove bestFirstMove, AlphaBetaMove bestSecondMove){
        
        int indexToSave = getSaveIndex(getIndex(hash1), hash2, depth);
        TTEntry existing = this.TT.get(indexToSave); //verificar!
        
        if (existing.isValid())
	{
		if (existing.getHash2() == hash2)
		{
			saveOverwriteSelf++;
		}
		else
		{
			saveOverwriteOther++;
		}
	}
	else
	{
		saveEmpty++;
	}

	saves++;
        
        TT.set(indexToSave, (new TTEntry(hash2, value, depth, type, firstPlayer, bestFirstMove, bestSecondMove)) );
    }
    
    public TTEntry lookupScan(int hash1, int hash2){
        lookups++;
        int index = getIndex(hash1);
        if(index < 0){
            System.err.println("Indice menor que zero");
        }
        
        // scan to see if this exists anywhere in the next few entries
        for (int i = index; (i<(index+10)) && (i <TT.size()); i++) {
            TTEntry entry = lookup(i, hash2);
            // if it does, return it
		if(entry != null)
		{
			found++;
			return entry;
		}
        }
        
        // otherwise return null
	notFound++;
        return null;
    }
    
    
    public TTEntry lookup(int index, int hash2){
        if (index < minIndex) minIndex = index;
	if (index > maxIndex) maxIndex = index;
        
        TTEntry tte = TT.get(index);
        
        
        // if there is a valid entry at that location
        if(tte.isValid()){
            // test for matching secondary hash
            if(tte.hashMathes(hash2)){
                found++;
                return tte;
            }else{ // if no match it is a collision
                return null;
            }
        }else{
            return null;
        }
    }
    
    public int getUsage(){
        int sum = 0;
        for (TTEntry tTEntry : TT) {
            if(tTEntry.isValid()){
                sum++;
            }
        }
        return sum;
    }
    
    public void print(){
        System.out.println("TT stats: "+lookups+" lookups, "+found+" found, "+ notFound+" not found, "+ collisions+
                " collision, "+ minIndex+" min, "+ maxIndex+" max.");
    }
}
