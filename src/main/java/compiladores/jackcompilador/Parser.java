/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package compiladores.jackcompilador;

import compilador.token.Token;
import compilador.token.TokenType;
import static compilador.token.TokenType.*;

/**
 *
 * @author RAIMUNDA
 */
public class Parser {
     private Scanner sc;       //analisador léxico
    private Token currentToken;      //token atual
    private Token peekToken;     //olhar token  

    private StringBuilder xmlOutput = new StringBuilder();

    public Parser (byte[] input) {
        sc = new Scanner(input);
        nextToken();
    }
    
    private void nextToken() {
        currentToken = peekToken;
        peekToken = sc.nextToken();
    }
    
     void parser () {
        parseClass();
    }

//    classdef -> 'class' className '{' classVarDec* subroutineDec* '}'
    void parseClass() {
        printNonTerminal("class");
        expectPeek(TokenType.CLASS);
        expectPeek(TokenType.IDENT);
        expectPeek(TokenType.LBRACE);
        while (peekToken.type == FIELD || peekToken.type == STATIC) {
            parseClassVardec();
        }
        parseSubroutineDec();
        
        while (peekToken.type == CONSTRUCTOR || peekToken.type == FUNCTION || peekToken.type == METHOD){
            parseSubroutineDec();
        }
        expectPeek(TokenType.RBRACE);
        printNonTerminal("/class");
    }
    
     //( 'static' | 'field' ) type varName ( ',' varName)* ';'
    void parseClassVardec() {
        printNonTerminal("classVarDec");
        expectPeek(FIELD, STATIC);
        //'int' | 'char' | 'boolean' | className
        expectPeek(INT, CHAR, BOOLEAN,IDENT);
        expectPeek(TokenType.IDENT);
        while (peekToken.type == TokenType.COMMA) {
            expectPeek(TokenType.COMMA);
            expectPeek(TokenType.IDENT);
        }
        expectPeek(TokenType.SEMICOLON);
        printNonTerminal("/classVarDec");
    }
    
    //'var' type varName ( ',' varName)* ';'
    void parseVarDec () {
        printNonTerminal("varDec");
        expectPeek(VAR);
        // 'int' | 'char' | 'boolean' | className
        expectPeek(INT,CHAR,BOOLEAN,IDENT);
        expectPeek(IDENT);

        while (peekTokenIs(COMMA)) {
            expectPeek(COMMA);
            expectPeek(IDENT);
        }

        expectPeek(SEMICOLON);
        printNonTerminal("/varDec");
    }
    
    // letStatement -> 'let' varName  '=' term ';'
    // term -> number;
    //'let' varName ( '[' expression ']' )? '=' expression ';'
    void parseLet() {
        printNonTerminal("letStatement");
        expectPeek(TokenType.LET);
        expectPeek(TokenType.IDENT);
        if (peekTokenIs(LBRACKET)) { // array
            expectPeek(LBRACKET);
            parseExpression();
            expectPeek(RBRACKET);
        }
        expectPeek(TokenType.EQ);
        parseExpression();
        expectPeek(TokenType.SEMICOLON);
        printNonTerminal("/letStatement");

    }
    //'+' | '-' | '* | '/' | '&' | '|' | '<' | '>' | '='
    private boolean isOperator (TokenType type) {
        return type.ordinal() >= PLUS.ordinal() && type.ordinal() <= EQ.ordinal();
    }
    //term (op term)*
    void parseExpression () {
       printNonTerminal("expression");
       parseTerm();
       while (isOperator(peekToken.type)) {
            expectPeek(peekToken.type);
            parseTerm();
        }
       printNonTerminal("expression");
    }
    
   //integerConstant | stringConstant | keywordConstant | varName | varName '[' expression ']' | subroutineCall | '(' expression ')' | unaryOp term
    void parseTerm() {
        printNonTerminal("term");
        switch (peekToken.type) {
            case INT:
                expectPeek(INT);
                break;
            case STRING:
                expectPeek(STRING);             
                break;
            
            case IDENT:
                expectPeek(IDENT);
                
                if (peekTokenIs(LPAREN) || peekTokenIs(DOT)) {
                    parseSubroutineCall();
                } else { 
                    if (peekTokenIs(LBRACKET)) { 
                        expectPeek(LBRACKET);
                        parseExpression();
                        expectPeek(RBRACKET);
                               
                    } 
                }
                break;
                
            case LPAREN:
                expectPeek(LPAREN);
                parseExpression();
                expectPeek(RPAREN);
                break;
                
            case FALSE:
            case NULL:
            case TRUE:
                expectPeek(FALSE, NULL, TRUE);             
                break;
            case THIS:
                expectPeek(THIS);
                break;
                
            case MINUS:
            case NOT:
                expectPeek(MINUS, NOT);
                var op = currentToken.type;
                parseTerm();   
                break;
              
            default:
                throw new Error( "term expected");
        }
        printNonTerminal("/term");
    }



    //statement*
    void parseStatements() {
        printNonTerminal("statements");
        while (peekToken.type == WHILE ||
                peekToken.type == IF ||
                peekToken.type == LET ||
                peekToken.type == DO ||
                peekToken.type == RETURN) {
            parseStatement();
        }

        printNonTerminal("/statements");
    }
    
    
    
    //'while' '(' expression ')' '{' statements '}'
    void parseWhile(){
        printNonTerminal("whileStatement");
        expectPeek(WHILE);
        expectPeek(LPAREN);
        parseExpression();
        expectPeek(RPAREN);
        expectPeek(LBRACE);
        parseStatements();
        expectPeek(RBRACE);
        printNonTerminal("/whileStatement");
    }
    //'if' '(' expression ')' '{' statements '}' ( 'else' '{' statements '}' )?
    void parseIf(){
        printNonTerminal("ifStatement");
        expectPeek(IF);
        expectPeek(LPAREN);
        parseExpression();
        expectPeek(RPAREN);
        expectPeek(LBRACE);
        parseStatements();
        expectPeek(RBRACE);
        printNonTerminal("/ifStatement");
    }
    
    //ReturnStatement -> 'return' expression? ';'
    void parseReturn(){

        printNonTerminal("returnStatement");
        expectPeek(RETURN);
        if (!peekTokenIs(SEMICOLON)) {
            parseExpression();
        }
        expectPeek(SEMICOLON);

        printNonTerminal("/returnStatement");
    }
    
    //'do' subroutineCall ';'
    void parseDo(){
        printNonTerminal("doStatement");
        expectPeek(DO);
        expectPeek(IDENT);
        parseSubroutineCall();
        expectPeek(SEMICOLON);
        printNonTerminal("/doStatement");
    }
//letStatement | ifStatement | whileStatement | doStatement | returnStatement
    void parseStatement() {
        switch (peekToken.type) {
            case LET:
                parseLet();
                break;
            case WHILE:
                parseWhile();
                break;
            case IF:
                parseIf();
                break;
            case DO:
                parseDo();
                break;
            case RETURN:
                parseReturn();
                break;
            default:
                throw new Error("Syntax error - Expected a statement");
        }
    printNonTerminal("/statements");
    }
    
    //subroutineName '(' expressionList ')' | (className|varName) '.' subroutineName '(' expressionList ')'
    void parseSubroutineCall () {
        
        if (peekTokenIs (LPAREN)) {
            expectPeek(LPAREN);
            parseExpression();
            expectPeek(RPAREN);
        } else {
            // pode ser um metodo de um outro objeto ou uma função
            expectPeek(DOT);
            expectPeek(IDENT);
            expectPeek(LPAREN);
            parseExpression();
            expectPeek(RPAREN);
        }
    }
    //( 'constructor' | 'function' | 'method' ) ( 'void' | type) subroutineName '(' parameterList ')' subroutineBody
    void parseSubroutineDec () {
        printNonTerminal("classVarDec");
        expectPeek(CONSTRUCTOR, FUNCTION, METHOD);
        // 'int' | 'char' | 'boolean' | className
        expectPeek(VOID, INT,CHAR,BOOLEAN,IDENT);
        //subroutineName
        expectPeek(IDENT);

        expectPeek(LPAREN);
        parseParameterList();
        expectPeek(RPAREN);
        parseSubroutineBody();

        printNonTerminal("/classVarDec");
    }
    //'{' varDec* statements '}'
    void parseSubroutineBody () {
        printNonTerminal("subroutineBody");
        expectPeek(LBRACE);
        while (peekTokenIs(VAR)) {
            parseVarDec();
        }
        parseStatements();
        expectPeek(RBRACE);
        printNonTerminal("/subroutineBody");
    }
    
    //parameterList ->((type varName) ( ',' type varName)*)?
      void parseParameterList() {
            printNonTerminal("parameterList");

            if (!peekTokenIs(RPAREN)) // verifica se tem pelo menos uma expressao
                {
                    expectPeek(INT, CHAR, BOOLEAN, IDENT);
            

                     expectPeek(IDENT);
            

                    while (peekTokenIs(COMMA)) {
                        expectPeek(COMMA);
                        expectPeek(INT, CHAR, BOOLEAN, IDENT);
                        expectPeek(IDENT);
             
                    }

                }

            printNonTerminal("/parameterList");
    }

    
    boolean currentTokenIs (TokenType type) {
        return currentToken.type == type;
    }
    
    boolean peekTokenIs (TokenType type) {
        return peekToken.type == type;
    }
    
    public String XMLOutput() {
        return xmlOutput.toString();
    }

  
     private void printNonTerminal(String nterminal) {
        xmlOutput.append(String.format("<%s>\r\n", nterminal));
    }
     
     private void expectPeek (TokenType type) {
        if (peekToken.type == type ) {
            nextToken();
            xmlOutput.append(String.format("%s\r\n", currentToken.toString()));
        } else {
            throw new Error("Syntax error - expected "+type+" found " + peekToken.lexeme);
        }
    }
     
    private void expectPeek(TokenType... types) {
        
        for (TokenType type : types) {
            if (peekToken.type == type) {
                expectPeek(type);
                return;
            }
        }
        
        throw new Error("Syntax error ");

    }

}
