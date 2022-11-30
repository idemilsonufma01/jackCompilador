/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package compiladores.jackcompilador;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author RAIMUNDA
 */
public class SymbolTable {
    public enum Kind {
        STATIC, ARG, VAR,FIELD
    };
    
    public static record Symbol(String name, String type, Kind kind, int index){
        
    }
    
    private Map<String, Symbol> ScopeClass;
    private Map<String, Symbol> subroutineScope;
    private Map<Kind, Integer> countVars;
    
    
    public SymbolTable() {
        ScopeClass = new HashMap<>();
        subroutineScope = new HashMap<>();
        countVars = new HashMap<>();

        countVars.put(Kind.ARG, 0);
        countVars.put(Kind.VAR, 0);
        countVars.put(Kind.STATIC, 0);
        countVars.put(Kind.FIELD, 0);
        
    }
    public void startSubRoutine(){
        subroutineScope.clear();
        countVars.put(Kind.VAR,0);
        countVars.put(Kind.ARG,0);
        
    }
    
    private Map<String, Symbol> scope (Kind kind){
        if(kind == Kind.FIELD || kind == Kind.STATIC){
            return ScopeClass;
        } else {
            return subroutineScope;
        }
    }
    
    void define(String name, String type, Kind kind){
        Map<String, Symbol> scopeTable = scope(kind);
        if(scopeTable.get(name) != null) throw new RuntimeException("already have definition of variable");
        Symbol sb = new Symbol(name, type, kind,varCont(kind));
        scopeTable.put(name, sb);
        countVars.put(kind,countVars.get(kind)+1);
    }
    
    public Symbol resolve (String name){
        Symbol sb = subroutineScope.get(name);
        if (sb != null){
            return sb;
        } else {
            return ScopeClass.get(name);
        }
    }
    
    int varCont(Kind kind){
        return countVars.get(kind);
    }
    
    


}
