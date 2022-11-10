/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */

package compiladores.jackcompilador;

import compilador.token.Token;
import static compilador.token.TokenType.EOF;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

/**
 *
 * @author RAIMUNDA
 */
public class App {

     private static String fromFile() {
        File file = new File("Main.jack");

        byte[] bytes;
        try {
            bytes = Files.readAllBytes(file.toPath());
            String textoDoArquivo = new String(bytes, "UTF-8");
            return textoDoArquivo;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    } 

    
    public static void main( String[] args )  {
        String input = """
               // This file is part of www.nand2tetris.org
               // and the book "The Elements of Computing Systems"
               // by Nisan and Schocken, MIT Press.
               // File name: projects/10/ExpressionLessSquare/Main.jack
               
               /** Expressionless version of projects/10/Square/Main.jack. */
               
               class Main {
                   static boolean test;    // Added for testing -- there is no static keyword
                                           // in the Square files.
               
                   function void main() {
                       var SquareGame game;
                       let game = game;
                       do game.run();
                       do game.dispose();
                       return;
                   }
               
                   
               }
               
                           
                """;
        Parser p = new Parser(input.getBytes());
        p.parser();
        System.out.println(p.XMLOutput());
        
        /*
        String input = "let rua ();";
        Scanner scan = new Scanner (input.getBytes());
        for (Token tk = scan.nextToken(); tk.type != EOF; tk = scan.nextToken()) {
            System.out.println(tk);
        }

        
        Parser p = new Parser (input.getBytes());
        p.parse();
        */


        //Parser p = new Parser (fromFile().getBytes());
        //p.parse();

        /*
        String input = "489-85+69";
        Scanner scan = new Scanner (input.getBytes());
        System.out.println(scan.nextToken());
        System.out.println(scan.nextToken());
        System.out.println(scan.nextToken());
        System.out.println(scan.nextToken());
        System.out.println(scan.nextToken());
        Token tk = new Token(NUMBER, "42");
        System.out.println(tk);
        */
    } 
}
