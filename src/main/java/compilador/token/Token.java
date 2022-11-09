/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package compilador.token;

/**
 *
 * @author RAIMUNDA
 */
import java.util.HashMap;
import java.util.Map;

public class Token {
    
    
         private static final Map<String, TokenType> keywords;
 

    static {
        keywords = new HashMap<>();
        keywords.put("class", TokenType.CLASS);
        keywords.put("constructor", TokenType.CONSTRUCTOR);
        keywords.put("function", TokenType.FUNCTION);
        keywords.put("method",    TokenType.METHOD);
        keywords.put("field",    TokenType.FIELD);
        keywords.put("static",    TokenType.STATIC);
        keywords.put("var",    TokenType.VAR);
        keywords.put("int",    TokenType.INT);
        keywords.put("char",    TokenType.CHAR);
        keywords.put("boolean",    TokenType.BOOLEAN);
        keywords.put("void",    TokenType.VOID);
        keywords.put("true",    TokenType.TRUE);
        keywords.put("false",    TokenType.FALSE);
        keywords.put("null",    TokenType.NULL);
        keywords.put("this",    TokenType.THIS);
        keywords.put("let",    TokenType.LET);
        keywords.put("do",    TokenType.DO);
        keywords.put("if",   TokenType.IF);
        keywords.put("else",    TokenType.ELSE);
        keywords.put("while",  TokenType.WHILE);
        keywords.put("return",  TokenType.RETURN);
        
        
    }

        public final TokenType type;
        public final String lexeme;

        public Token (TokenType type, String lexeme) {
            this.type = type;
            this.lexeme = lexeme;
        }
        
        public String value (){
            return type.value;
        }
       

        
        static public boolean isSymbol (String c) {
            String symbols = "{}()[].,;+-*/&|<>=~";
            return symbols.indexOf(c) > -1;
        }
        
        static public TokenType keyword (String id) {
            return keywords.get(id);
        }


        public String toString() {
            String type = this.type.toString().toLowerCase();
            String valor = lexeme;
            
            if(isSymbol(lexeme)){
                type = "Symbol";
                //Os símbolos <, >, ", e & são impressos como &lt;  &gt;  &quot; e &amp; Para não conflitar com o significado destes símbolos no XML
                if (valor == ">") {
                    valor = "&gt;" ;
                } else if (valor == "<") {
                    valor = "&lt;" ;
                } else if (valor == "\"") {
                    valor = "&quot;" ;
                } else if (valor == "&") {
                    valor = "&amp;" ;
                }

            }
            
            else if (type.equals("NUMBER"))
                type =  "intConst";

            else if (type.equals("STRING"))
                type =  "stringConst";

            else if (Token.keyword(lexeme)!=null )
                type = "keyword";
        

            return "<"+ type +">" + valor + "</"+ type + ">";
        }
    
}

