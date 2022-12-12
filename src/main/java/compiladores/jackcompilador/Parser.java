/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package compiladores.jackcompilador;

import compilador.token.Token;
import compilador.token.TokenType;
import static compilador.token.TokenType.*;
import compiladores.jackcompilador.SymbolTable.Kind;
import compiladores.jackcompilador.SymbolTable.Symbol;
import compiladores.jackcompilador.VmWriter.Command;
import compiladores.jackcompilador.VmWriter.Segment;

/**
 *
 * @author RAIMUNDA
 */
public class Parser {

    // errro

    private Scanner scan;
    private Token currentToken;
    private Token peekToken;

    private StringBuilder xmlOutput = new StringBuilder();
    // ******************************
    private SymbolTable symbolTable;
    private VmWriter vmWriter;

    private String className; // nome da classe
    private int ifLabelNum; // numero de if
    private int whileLabelNum; // numero de while
    // ***********************

    public Parser(byte[] input) {
        scan = new Scanner(input);
        // **
        symbolTable = new SymbolTable();
        vmWriter = new VmWriter();
        // **
        nextToken();
        // ***
        ifLabelNum = 0;
        whileLabelNum = 0;
        // **

    }

    private void nextToken() {
        currentToken = peekToken;
        peekToken = scan.nextToken();
    }

    void parser() {
        parseClass();
    }

    // 'class' className '{' classVarDec* subroutineDec* '}'
    void parseClass() {
        printNonTerminal("class");
        expectPeek(CLASS);
        expectPeek(IDENT);
        // ** insere o nome da classe
        className = currentToken.lexeme;
        // **
        expectPeek(LBRACE);

        while (peekToken.type == FIELD || peekToken.type == STATIC) {
            parseClassVarDec();
        }
        // parseSubroutineDec();
        while (peekTokenIs(FUNCTION) || peekTokenIs(CONSTRUCTOR) || peekTokenIs(METHOD)) {
            parseSubroutineDec();
        }

        expectPeek(RBRACE);

        printNonTerminal("/class");
    }

    // subroutineCall -> subroutineName '(' expressionList ')' | (className|varName)
    // '.' subroutineName '(' expressionList ')
    void parseSubroutineCall() {
        // **
        var numArg = 0;
        var ident = currentToken.lexeme;
        var simbol = symbolTable.resolve(ident); // CLASS OR OBJECT
        var functName = ident + ".";
        // **
        // método da propria classe
        if (peekTokenIs(LPAREN)) {
            expectPeek(LPAREN);
            vmWriter.writePush(Segment.POINTER, 0);
            numArg = parseExpressionList() + 1;
            expectPeek(RPAREN);
            functName = className + "." + ident;
        } else {
            // pode ser um metodo de um outro objeto ou uma função
            expectPeek(DOT);
            expectPeek(IDENT); // nome da função
            if (simbol != null) {
                // é o metodo
                functName = simbol.type() + "." + currentToken.lexeme;
                vmWriter.writePush(kindSegment2(simbol.kind()), simbol.index());
                numArg = 1;

            } else {
                functName += currentToken.lexeme; // se for uma função
            }
            expectPeek(LPAREN);
            numArg += parseExpressionList();

            expectPeek(RPAREN);
        }
        vmWriter.writeCall(functName, numArg);

    }

    // 'do' subroutineCall ';'
    void parseDo() {
        printNonTerminal("doStatement");
        expectPeek(DO);
        expectPeek(IDENT);
        parseSubroutineCall();
        expectPeek(SEMICOLON);
        vmWriter.writePop(Segment.TEMP, 0);
        printNonTerminal("/doStatement");
    }

    // 'var' type varName ( ',' varName)* ';'
    void parseVarDec() {
        printNonTerminal("varDec");
        expectPeek(VAR);
        SymbolTable.Kind kind = Kind.VAR;

        // 'int' | 'char' | 'boolean' | className
        expectPeek(INT, CHAR, BOOLEAN, IDENT);
        String type = currentToken.lexeme;
        expectPeek(IDENT);
        String name = currentToken.lexeme;
        symbolTable.define(name, type, kind);

        while (peekTokenIs(COMMA)) {
            expectPeek(COMMA);
            expectPeek(IDENT);
            name = currentToken.lexeme;
            symbolTable.define(name, type, kind);

        }

        expectPeek(SEMICOLON);
        printNonTerminal("/varDec");
    }

    // classVarDec → ( 'static' | 'field' ) type varName ( ',' varName)* ';'
    void parseClassVarDec() {
        printNonTerminal("classVarDec");
        expectPeek(FIELD, STATIC);

        SymbolTable.Kind kind = Kind.STATIC;
        if (currentTokenIs(FIELD))
            kind = Kind.FIELD;

        // 'int' | 'char' | 'boolean' | className
        expectPeek(INT, CHAR, BOOLEAN, IDENT);
        String type = currentToken.lexeme;
        expectPeek(IDENT);
        // **
        String name = currentToken.lexeme;
        symbolTable.define(name, type, kind);
        // **
        while (peekTokenIs(COMMA)) {
            expectPeek(COMMA);
            expectPeek(IDENT);

            name = currentToken.lexeme;
            symbolTable.define(name, type, kind);

        }
        expectPeek(SEMICOLON);
        printNonTerminal("/classVarDec");
    }

    // ( 'constructor' | 'function' | 'method' ) ( 'void' | type) subroutineName
    // '(' parameterList ')' subroutineBody
    void parseSubroutineDec() {
        printNonTerminal("subroutineDec");

        ifLabelNum = 0;
        whileLabelNum = 0;
        symbolTable.startSubroutine();

        expectPeek(CONSTRUCTOR, FUNCTION, METHOD);
        var subroutineType = currentToken.type;

        if (subroutineType == METHOD) {
            symbolTable.define("this", className, Kind.ARG);
        }

        // 'int' | 'char' | 'boolean' | className
        expectPeek(VOID, INT, CHAR, BOOLEAN, IDENT);
        expectPeek(IDENT);
        // **
        var functName = className + "." + currentToken.lexeme;
        // **

        expectPeek(LPAREN);
        parseParameterList();
        expectPeek(RPAREN);
        parseSubroutineBody(functName, subroutineType);

        printNonTerminal("/subroutineDec");
    }

    // ((type varName) ( ',' type varName)*)?
    void parseParameterList() {
        printNonTerminal("parameterList");
        SymbolTable.Kind kind = Kind.ARG;

        if (!peekTokenIs(RPAREN)) // verifica se tem pelo menos uma expressao
        {
            expectPeek(INT, CHAR, BOOLEAN, IDENT);
            String type = currentToken.lexeme;
            expectPeek(IDENT);
            String name = currentToken.lexeme;
            symbolTable.define(name, type, kind);

            while (peekTokenIs(COMMA)) {
                expectPeek(COMMA);
                expectPeek(INT, CHAR, BOOLEAN, IDENT);
                type = currentToken.lexeme;

                expectPeek(IDENT);
                name = currentToken.lexeme;

                symbolTable.define(name, type, kind);

            }
        }
        printNonTerminal("/parameterList");
    }

    // '{' varDec* statements '}'
    void parseSubroutineBody(String functName, TokenType subroutineType) {

        printNonTerminal("subroutineBody");
        expectPeek(LBRACE);
        while (peekTokenIs(VAR)) {
            parseVarDec();
        }
        var numlocals = symbolTable.varCont(Kind.VAR);
        vmWriter.writeFunction(functName, numlocals);
        if (subroutineType == CONSTRUCTOR) {
            vmWriter.writePush(Segment.CONST, symbolTable.varCont(Kind.FIELD));
            vmWriter.writeCall("Memory.alloc", 1);
            vmWriter.writePop(Segment.POINTER, 0);
        }

        if (subroutineType == METHOD) {
            vmWriter.writePush(Segment.ARG, 0);
            vmWriter.writePop(Segment.POINTER, 0);
        }

        parseStatements();
        expectPeek(RBRACE);
        printNonTerminal("/subroutineBody");
    }

    // letStatement -> 'let' identifier( '[' expression ']' )? '=' expression ';'
    void parseLet() {

        printNonTerminal("letStatement");
        var isArray = false;

        expectPeek(LET);
        expectPeek(IDENT);
        // **
        var symbol = symbolTable.resolve(currentToken.lexeme);
        // **
        // array
        if (peekTokenIs(LBRACKET)) {
            expectPeek(LBRACKET);
            parseExpression();

            vmWriter.writePush(kindSegment2(symbol.kind()), symbol.index());
            vmWriter.writeArithmetic(Command.ADD);
            expectPeek(RBRACKET);

            isArray = true;
        }

        expectPeek(EQ);
        parseExpression();

        if (isArray) {

            vmWriter.writePop(Segment.TEMP, 0); // push result back onto stack
            vmWriter.writePop(Segment.POINTER, 1); // pop address pointer into pointer 1
            vmWriter.writePush(Segment.TEMP, 0); // push result back onto stack
            vmWriter.writePop(Segment.THAT, 0); // Store right hand side evaluation in THAT 0.

        } else {
            vmWriter.writePop(kindSegment2(symbol.kind()), symbol.index());
        }

        expectPeek(SEMICOLON);
        printNonTerminal("/letStatement");
    }

    // 'while' '(' expression ')' '{' statements '}'
    void parseWhile() {
        printNonTerminal("whileStatement");
        // **
        var labelTrue = "WHILE_EXP" + whileLabelNum;
        var labelFalse = "WHILE_END" + whileLabelNum;
        whileLabelNum++;

        vmWriter.writeLabel(labelTrue);
        // **

        expectPeek(WHILE);
        expectPeek(LPAREN);
        parseExpression();
        // **
        vmWriter.writeArithmetic(Command.NOT);
        vmWriter.writeIf(labelFalse);

        expectPeek(RPAREN);
        expectPeek(LBRACE);
        parseStatements();
        // **
        vmWriter.writeGoto(labelTrue); // Go back to labelTrue and check condition
        vmWriter.writeLabel(labelFalse); // Breaks out of while loop because ~(condition) is true

        expectPeek(RBRACE);
        printNonTerminal("/whileStatement");
    }

    // 'if' '(' expression ')' '{' statements '}' ( 'else' '{' statements '}' )?
    void parseIf() {
        printNonTerminal("ifStatement");

        var labelTrue = "IF_TRUE" + ifLabelNum;
        var labelFalse = "IF_FALSE" + ifLabelNum;
        var labelEnd = "IF_END" + ifLabelNum;

        ifLabelNum++;

        expectPeek(IF);
        expectPeek(LPAREN);
        parseExpression();
        expectPeek(RPAREN);
        // **
        vmWriter.writeIf(labelTrue);
        vmWriter.writeGoto(labelFalse);
        vmWriter.writeLabel(labelTrue);

        // **

        expectPeek(LBRACE);
        parseStatements();
        expectPeek(RBRACE);

        if (peekTokenIs(ELSE)) {
            vmWriter.writeGoto(labelEnd);
        }

        vmWriter.writeLabel(labelFalse);

        if (peekTokenIs(ELSE)) {
            expectPeek(ELSE);
            expectPeek(LBRACE);
            parseStatements();
            expectPeek(RBRACE);
            vmWriter.writeLabel(labelEnd);
        }

        printNonTerminal("/ifStatement");
    }

    // statement*
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

    // letStatement | ifStatement | whileStatement | doStatement | returnStatement
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
        } else {
            vmWriter.writePush(Segment.CONST, 0);
        }
        expectPeek(SEMICOLON);

        vmWriter.writeReturn();
        printNonTerminal("/returnStatement");
    }

    // (expression ( ',' expression)* )?
    int parseExpressionList() {
        printNonTerminal("expressionList");
        var numArg = 0;

        if (!peekTokenIs(RPAREN)) // verifica se tem pelo menos uma expressao
        {
            parseExpression();
            numArg = 1;
        }

        // procurando as outras
        while (peekTokenIs(COMMA)) {
            expectPeek(COMMA);
            parseExpression();
            numArg++;
        }

        printNonTerminal("/expressionList");
        return numArg;
    }

    private boolean isOperator(TokenType type) {
        return type.ordinal() >= PLUS.ordinal() && type.ordinal() <= EQ.ordinal();
    }

    // expression -> term (op term)*
    void parseExpression() {
        printNonTerminal("expression");
        parseTerm();
        while (isOperator(peekToken.type)) {
            var ope = peekToken.type;
            expectPeek(peekToken.type);
            parseTerm();
            compileOperators(ope);
        }
        printNonTerminal("/expression");
    }

    // term -> number | identifier | stringConstant | keywordConstant
    void parseTerm() {
        printNonTerminal("term");
        switch (peekToken.type) {
            case NUMBER:
                expectPeek(NUMBER);
                System.out.println(currentToken);
                vmWriter.writePush(Segment.CONST, Integer.parseInt(currentToken.lexeme));
                break;

            case STRING:
                expectPeek(STRING);
                var strValue = currentToken.lexeme;
                vmWriter.writePush(Segment.CONST, strValue.length());
                vmWriter.writeCall("String.new", 1);
                for (int i = 0; i < strValue.length(); i++) {
                    vmWriter.writePush(Segment.CONST, strValue.charAt(i));
                    vmWriter.writeCall("String.appendChar", 2);
                }
                break;

            case IDENT:
                expectPeek(IDENT);
                Symbol s = symbolTable.resolve(currentToken.lexeme);
                if (peekTokenIs(LPAREN) || peekTokenIs(DOT)) {
                    parseSubroutineCall();
                } else { // variavel comum ou array
                    if (peekTokenIs(LBRACKET)) { // array
                        expectPeek(LBRACKET);
                        parseExpression();
                        vmWriter.writePush(kindSegment2(s.kind()), s.index());
                        vmWriter.writeArithmetic(Command.ADD);
                        expectPeek(RBRACKET);
                        vmWriter.writePop(Segment.POINTER, 1); // pop address pointer into pointer 1
                        vmWriter.writePush(Segment.THAT, 0); // push the value of the address pointer back onto stack

                    } else {
                        vmWriter.writePush(kindSegment2(s.kind()), s.index());
                    }

                }
                break;
            case FALSE:
            case NULL:
            case TRUE:
                expectPeek(FALSE, NULL, TRUE);
                vmWriter.writePush(Segment.CONST, 0);
                if (currentToken.type == TRUE)
                    vmWriter.writeArithmetic(Command.NOT);
                break;
            case THIS:
                expectPeek(THIS);
                vmWriter.writePush(Segment.POINTER, 0);
                break;

            case LPAREN:
                expectPeek(LPAREN);
                parseExpression();
                expectPeek(RPAREN);
                break;
            case MINUS:
            case NOT:
                expectPeek(MINUS, NOT);
                var op = currentToken.type;
                parseTerm();
                if (op == MINUS) {
                    vmWriter.writeArithmetic(Command.NEG);
                } else {
                    vmWriter.writeArithmetic(Command.NOT);
                }
                break;
            default:
                throw new Error("term expected");
        }
        printNonTerminal("/term");
    }

    private void expectPeek(TokenType type) {
        if (peekToken.type == type) {
            nextToken();
            xmlOutput.append(String.format("%s\r\n", currentToken.toString()));
        } else {
            // throw new Error("Syntax error - expected " + type + " found " +
            // peekToken.lexeme);
            throw new Error("Expected " + type.value);
        }
    }

    void compileOperators(TokenType type) {
        System.out.println(type);
        if (type == AST) {
            vmWriter.writeCall("Math.multiply", 2);
        } else if (type == SLASH) {
            vmWriter.writeCall("Math.divide", 2);
        } else if (type == PLUS) {
            vmWriter.writeArithmetic(Command.ADD);
        } else {

            vmWriter.writeArithmetic(typeOperator(type));
        }

    }

    // funções auxiliares
    public String XMLOutput() {
        return xmlOutput.toString();
    }

    public String VMOutput() {
        return vmWriter.vmOutput();
    }

    private void printNonTerminal(String nterminal) {
        xmlOutput.append(String.format("<%s>\r\n", nterminal));
    }

    boolean peekTokenIs(TokenType type) {
        return peekToken.type == type;
    }

    boolean currentTokenIs(TokenType type) {
        return currentToken.type == type;
    }

    private void expectPeek(TokenType... types) {
        for (TokenType type : types) {
            if (peekToken.type == type) {
                expectPeek(type);
                return;
            }
        }

        throw new Error("Syntax error");
        // throw new Error( "Expected a statement");

    }

    private Segment kindSegment2(Kind kind) {
        if (kind == Kind.STATIC)
            return Segment.STATIC;
        if (kind == Kind.FIELD)
            return Segment.THIS;
        if (kind == Kind.VAR)
            return Segment.LOCAL;
        if (kind == Kind.ARG)
            return Segment.ARG;
        return null;

    }

    private Command typeOperator(TokenType type) {
        if (type == PLUS)
            return Command.ADD;
        if (type == MINUS)
            return Command.SUB;
        if (type == LT)
            return Command.LT;
        if (type == GT)
            return Command.GT;
        if (type == EQ)
            return Command.EQ;
        if (type == AND)
            return Command.AND;
        if (type == OR)
            return Command.OR;
        return null;

    }

}
