/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package compiladores.jackcompilador;

import compilador.token.Token;
import compilador.token.TokenType;
import static compilador.token.TokenType.NUMBER;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author RAIMUNDA
 */
public class Scanner {
    private byte[] input;
    private int current;
    private int start;

   
    public Scanner (byte[ ] input) {
        this.input = input;
        current = 0;
        start = 0;
    }

        
//verificando Tokens
    public Token nextToken () {

        skipWhitespace();

        start = current;
        char ch = peek();

        if (Character.isDigit(ch)) {
             return number();
        }

        if (Character.isLetter(ch)) {
            return identifier();
        }

        switch (ch) {


            case '+':
                advance();
                return new Token (TokenType.PLUS,"+");
            case '-':
                advance();
                return new Token (TokenType.MINUS,"-");

            case '=':
                advance();
                return new Token(TokenType.EQ, "=");
            case '*':
                advance();
                return new Token(TokenType.AST, "*");

            case '/':
                if (peekNext() == '/') {
                    skipLineComments();
                    return nextToken();
                } else if (peekNext() == '*') {
                    skipBlockComments();
                    return nextToken();
                }
                else {
                    advance();
                    return new Token (TokenType.SLASH,"/");
                }

            case '&':
                advance();
                return new Token(TokenType.AND, "&");

            case '|':
                advance();
                return new Token(TokenType.OR, "|");

            case '~':
                advance();
                return new Token(TokenType.NOT, "~");

            case '<':
                advance();
                return new Token(TokenType.LT, "<");

            case '>':
                advance();
                return new Token(TokenType.GT, ">");
            
            case '.':
                advance();
                return new Token(TokenType.DO, ".");

            case ',':
                advance();
                return new Token(TokenType.COMMA, ",");

            case ';':
                advance();
                return new Token(TokenType.SEMICOLON, ";");

            case '(':
                advance();
                return new Token(TokenType.LPAREN, "(");

            case ')':
                advance();
                return new Token(TokenType.RPAREN, ")");

            case '{':
                advance();
                return new Token(TokenType.LBRACE, "{");
            
            case '}':
                advance();
                return new Token(TokenType.RBRACE, "}");

            case '[':
                advance();
                return new Token(TokenType.LBRACKET, "[");
            case ']':
                advance();
                return new Token(TokenType.RBRACKET, "]");
            
            case '"':
                return string();
            
            case 0:
                return new Token (TokenType.EOF,"EOF");
            default:
                advance();
                return new Token(TokenType.ILLEGAL, Character.toString(ch));
        }
    }

//ignorar blocos de comentários
    private void skipBlockComments() {
        boolean endComment = false;
        advance();

        while (!endComment) {
            advance();
            char ch = peek();

            if ( ch == 0) { // eof
                System.exit(1);
            }
    
         
            if (ch == '*') {

               for (ch = peek(); ch == '*';  advance(), ch = peek());

             
                if (ch == '/') {
                    endComment = true;
                    advance();
                }
            }

        }

    }
    //pulando espaço
    private void skipWhitespace() {
        char ch = peek();
        while (ch == ' ' || ch == '\r' || ch == '\t' || ch == '\n') {
            advance();
            ch = peek();
        }
    }

//pulando linha
    private void skipLineComments() {
  
        for (char ch = peek(); ch != '\n' && ch != 0;  advance(), ch = peek()) ;
    }
//verificando se é identificador
    private Token identifier() {
        while (isAlphaNumeric(peek())) advance();

        String id = new String(input, start, current-start, StandardCharsets.UTF_8)  ;
        TokenType type = Token.keyword(id);
        if (type == null) type = TokenType.IDENT;
            return new Token(type, id);
    }
//verificando se é numero
    private Token number() {
        while (Character.isDigit(peek())) {
            advance();
        }
            String num = new String(input, start, current-start, StandardCharsets.UTF_8)  ;
            return new Token(NUMBER, num);
    }
//verificando se é string
    private Token string () {
        advance();
        start = current;
        while (peek() != '"' && peek() != 0) {
            advance();
        }
        String s = new String(input, start, current-start, StandardCharsets.UTF_8);
        Token token = new Token (TokenType.STRING,s);
        advance();
        return token;
    }
    //avançando
    private void advance()  {
        char ch = peek();
        if (ch != 0) {
            current++;
        }
    }

    //verificando se é letra ou digitos
    private boolean isAlphaNumeric(char c) {
            return Character.isLetter(c) || Character.isDigit((c));
      }
    
//verificando atual
    private char peek() {
         if (current < input.length)
                return (char)input[current];
         return 0;
    }
//verificando o proximo
    private char peekNext () {
        int next = current + 1;
        if ( next  < input.length) {
            return (char)input[next];
        } else {
            return 0;
        }
   }

}
