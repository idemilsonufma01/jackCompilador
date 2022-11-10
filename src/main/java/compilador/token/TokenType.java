/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package compilador.token;

/**
 *
 * @author RAIMUNDA
 */
import java.util.Arrays;

public enum TokenType {

    //simbolos
    PLUS,
    MINUS,
    EQ,
    AST,
    SLASH,
    AND,
    OR,
    NOT,
    GT,
    LT,
    DOT,
    COMMA,
    SEMICOLON,
    LPAREN,
    RPAREN,
    LBRACE,
    RBRACE,
    LBRACKET,
    RBRACKET,




     // Literals.
     NUMBER,
     STRING,
     IDENT,

 
     // keywords
     CLASS,
     CONSTRUCTOR,
     FUNCTION,
     METHOD,
     FIELD,
     STATIC,
     VAR,
     INT,
     CHAR,
     BOOLEAN,
     VOID,
     TRUE,
     FALSE,
     NULL,
     THIS,
     LET,
     DO,
     IF,
     ELSE,
     WHILE,
     RETURN,


     
     EOF,

     ILLEGAL;
     
     private TokenType(){
         
     }
     
     private TokenType(String value){
         this.value = value;
     }
     
     public String value;
     
     public static TokenType Value(String value){
         return Arrays.stream(TokenType.values())
                 .filter(symbolType -> symbolType.value != null && symbolType.value.equals(value))
                 .findFirst()
                 .orElse(null);
     }
}
