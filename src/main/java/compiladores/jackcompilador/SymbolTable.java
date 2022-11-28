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
    
    private Map<String, Symbol> classScope;
    private Map<String, Symbol> subroutineScope;
    private Map<Kind, Integer> countVars;
    
    
    public SymbolTable() {
        classScope = new HashMap<>();
        subroutineScope = new HashMap<>();
        countVars = new HashMap<>();

        countVars.put(Kind.ARG, 0);
        countVars.put(Kind.VAR, 0);
        countVars.put(Kind.STATIC, 0);
        countVars.put(Kind.FIELD, 0);
        
    }


}
