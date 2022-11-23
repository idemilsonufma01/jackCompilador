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

    private Scanner scan;
    private Token currentToken;
    private Token peekToken;
    
    private StringBuilder xmlOutput = new StringBuilder();
    


    public Parser(byte[] input) {
        scan = new Scanner(input);
   
        nextToken();


    }

    private void nextToken() {
        currentToken = peekToken;
        peekToken = scan.nextToken();
    }

    void parser() {
        parseClass();
    }
//'class' className '{' classVarDec* subroutineDec* '}'
    void parseClass() {
        printNonTerminal("class");
        expectPeek(CLASS);
        expectPeek(IDENT);
        expectPeek(LBRACE);

        while (peekToken.type==FIELD || peekToken.type == STATIC) {
            parseClassVarDec();
        }
        //parseSubroutineDec();
        while (peekTokenIs(FUNCTION )|| peekTokenIs(CONSTRUCTOR)|| peekTokenIs(METHOD)) {
            parseSubroutineDec();
        }

        expectPeek(RBRACE);

        printNonTerminal("/class");
    }

    // subroutineCall -> subroutineName '(' expressionList ')' | (className|varName)
    // '.' subroutineName '(' expressionList ')
    void parseSubroutineCall() {
        if (peekTokenIs(LPAREN)) { // método da propria classe
            expectPeek(LPAREN);
            parseExpressionList();
            expectPeek(RPAREN);
        } else {
            // pode ser um metodo de um outro objeto ou uma função
            expectPeek(DOT);
            expectPeek(IDENT); // nome da função
            expectPeek(LPAREN);
            parseExpressionList();
            expectPeek(RPAREN);
        }

        
    }
//'do' subroutineCall ';'
    void parseDo() {
        printNonTerminal("doStatement");
        expectPeek(DO);
        expectPeek(IDENT);
        parseSubroutineCall();
        expectPeek(SEMICOLON);
        printNonTerminal("/doStatement");
    }

    // 'var' type varName ( ',' varName)* ';'
    void parseVarDec() {
        printNonTerminal("varDec");
        expectPeek(VAR);

        // 'int' | 'char' | 'boolean' | className
        expectPeek(INT, CHAR, BOOLEAN, IDENT);
        expectPeek(IDENT);
       
        while (peekTokenIs(COMMA)) {
            expectPeek(COMMA);
            expectPeek(IDENT);


        }

        expectPeek(SEMICOLON);
        printNonTerminal("/varDec");
    }

    // classVarDec → ( 'static' | 'field' ) type varName ( ',' varName)* ';'
    void parseClassVarDec() {
        printNonTerminal("classVarDec");
        expectPeek(FIELD, STATIC);
        // 'int' | 'char' | 'boolean' | className
        expectPeek(INT, CHAR, BOOLEAN, IDENT);
        expectPeek(IDENT);
        while (peekTokenIs(COMMA)) {
            expectPeek(COMMA);
            expectPeek(IDENT);
        }
        expectPeek(SEMICOLON);
        printNonTerminal("/classVarDec");
    }
//( 'constructor' | 'function' | 'method' ) ( 'void' | type) subroutineName '(' parameterList ')' subroutineBody
    void parseSubroutineDec() {
        printNonTerminal("subroutineDec");
        expectPeek(CONSTRUCTOR, FUNCTION, METHOD);
        // 'int' | 'char' | 'boolean' | className
        expectPeek(VOID, INT, CHAR, BOOLEAN, IDENT);
        expectPeek(IDENT);
        expectPeek(LPAREN);
        parseParameterList();
        expectPeek(RPAREN);
        parseSubroutineBody();

        printNonTerminal("/subroutineDec");
    }
    //((type varName) ( ',' type varName)*)?
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
//'{' varDec* statements '}'
    void parseSubroutineBody() {

        printNonTerminal("subroutineBody");
        expectPeek(LBRACE);
        while (peekTokenIs(VAR)) {
            parseVarDec();
        }
     
        parseStatements();
        expectPeek(RBRACE);
        printNonTerminal("/subroutineBody");
    }

    // letStatement -> 'let' identifier( '[' expression ']' )? '=' expression ';'
    void parseLet() {

        printNonTerminal("letStatement");
        expectPeek(LET);
        expectPeek(IDENT);
        
        if (peekTokenIs(LBRACKET)) { // array
            expectPeek(LBRACKET);
            parseExpression();
            expectPeek(RBRACKET);
        }
       
        expectPeek(EQ);
        parseExpression();
        expectPeek(SEMICOLON);
        printNonTerminal("/letStatement");
    }

    // 'while' '(' expression ')' '{' statements '}'
    void parseWhile() {
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
    void parseIf() {
        printNonTerminal("ifStatement");
        expectPeek(IF);
        expectPeek(LPAREN);
        parseExpression();
        expectPeek(RPAREN);
        expectPeek(LBRACE);
        parseStatements();
        expectPeek(RBRACE);
        
        if (peekTokenIs(ELSE))
        {
            expectPeek(ELSE);
            expectPeek(LBRACE);
            parseStatements();
            expectPeek(RBRACE);
        }
        
        printNonTerminal("/ifStatement");
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
            case RETURN:
                parseReturn();
                break;
            case DO:
                parseDo();
                break;
            default:
                throw new Error("Expected a statement");
        }
    }

    // ReturnStatement -> 'return' expression? ';'
    void parseReturn() {
        printNonTerminal("returnStatement");
        expectPeek(RETURN);
        if (!peekTokenIs(SEMICOLON)) {
            parseExpression();
        } 
        expectPeek(SEMICOLON);
        

        printNonTerminal("/returnStatement");
    }
//(expression ( ',' expression)* )?
    void parseExpressionList() {
        printNonTerminal("expressionList");

        if (!peekTokenIs(RPAREN)) // verifica se tem pelo menos uma expressao
        {
            parseExpression();
        }

        // procurando as demais
        while (peekTokenIs(COMMA)) {
            expectPeek(COMMA);
            parseExpression();
        }

        printNonTerminal("/expressionList");
        
    }
    
    private boolean isOperator (TokenType type) {
        return type.ordinal() >= PLUS.ordinal() && type.ordinal() <= EQ.ordinal();
    }

    // expression -> term (op term)*
    void parseExpression() {
        printNonTerminal("expression");
        parseTerm();
        while (isOperator(peekToken.type)) {
            expectPeek(peekToken.type);
            parseTerm();
        }
        printNonTerminal("/expression");
    }

   

    // term -> number | identifier | stringConstant | keywordConstant
    void parseTerm() {
        printNonTerminal("term");
        switch (peekToken.type) {
            case NUMBER:
                expectPeek(NUMBER);
                break;
                
            case STRING:
                expectPeek(STRING);
                break;
                
            case IDENT:
                expectPeek(IDENT);
                
                if (peekTokenIs(LPAREN) || peekTokenIs(DOT)) {
                    parseSubroutineCall();
                } else { // variavel comum ou array
                    if (peekTokenIs(LBRACKET)) { // array
                        expectPeek(LBRACKET);
                        parseExpression();
                        expectPeek(RBRACKET);
                    } 
                }
                break;  
            case FALSE:
            case NULL:
            case TRUE:
                expectPeek(FALSE, NULL, TRUE);
                break;
            case THIS:
                expectPeek(THIS);
                break;
            
            case LPAREN:
                expectPeek(LPAREN);
                parseExpression();
                expectPeek(RPAREN);
                break;
            case MINUS:
            case NOT:
              expectPeek(MINUS, NOT);
              parseTerm();
              break;
            default:
                throw new Error("term expected");
        }
        printNonTerminal("/term");
    }

   
    boolean peekTokenIs(TokenType type) {
        return peekToken.type == type;
    }

    boolean currentTokenIs(TokenType type) {
        return currentToken.type == type;
    }

     private void expectPeek(TokenType type) {
        if (peekToken.type == type) {
            nextToken();
            xmlOutput.append(String.format("%s\r\n", currentToken.toString()));
        } else {
             throw new Error("Syntax error - expected " + type + " found " + peekToken.lexeme);
            //throw new Error("Expected " + type.value);
        }
    }
     
    private void expectPeek(TokenType... types) {
        for (TokenType type : types) {
            if (peekToken.type == type) {
                expectPeek(type);
                return;
            }
        }

        throw new Error("Syntax error");
        //throw new Error( "Expected a statement");

    }

    // funções auxiliares
    public String XMLOutput() {
        return xmlOutput.toString();
    }


    private void printNonTerminal(String nterminal) {
        xmlOutput.append(String.format("<%s>\r\n", nterminal));
    }


  
   

   

   

}
