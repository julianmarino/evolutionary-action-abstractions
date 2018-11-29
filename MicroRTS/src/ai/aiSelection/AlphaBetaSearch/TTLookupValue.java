/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ai.aiSelection.AlphaBetaSearch;

/**
 *
 * @author rubens
 */
public class TTLookupValue {
    private boolean _found;
    private boolean _cut;
    private TTEntry _entry;

    
    public TTLookupValue(){
        this._found = false;
        this._cut = false;
        this._entry = null;
    }
    
    public TTLookupValue(boolean _found, boolean _cut, TTEntry _entry) {
        this._found = _found;
        this._cut = _cut;
        this._entry = _entry;
    }

    public boolean isFound() {
        return _found;
    }

    public void setFound(boolean _found) {
        this._found = _found;
    }

    public boolean isCut() {
        return _cut;
    }

    public void setCut(boolean _cut) {
        this._cut = _cut;
    }

    public TTEntry getEntry() {
        return _entry;
    }

    public void setEntry(TTEntry _entry) {
        this._entry = _entry;
    }
    
    
    
}
