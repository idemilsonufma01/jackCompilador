/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */

package compiladores.jackcompilador;

//import static compilador.token.TokenType.*;
//port static compilador.token.TokenType.EOF;
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
                       // File name: projects/10/ExpressionLessSquare/SquareGame.jack
                       
                       /** Expressionless version of projects/10/Square/SquareGame.jack. */
                       
                       class SquareGame {
                          field Square square; 
                          field int direction; 
                       
                          constructor SquareGame new() {
                             let square = square;
                             let direction = direction;
                             return square;
                          }
                       
                          method void dispose() {
                             do square.dispose();
                             do Memory.deAlloc(square);
                             return;
                          }
                       
                          method void moveSquare() {
                             if (direction) { do square.moveUp(); }
                             if (direction) { do square.moveDown(); }
                             if (direction) { do square.moveLeft(); }
                             if (direction) { do square.moveRight(); }
                             do Sys.wait(direction);
                             return;
                          }
                       
                          method void run() {
                             var char key;
                             var boolean exit;
                             
                             let exit = key;
                             while (exit) {
                                while (key) {
                                   let key = key;
                                   do moveSquare();
                                }
                       
                                if (key) { let exit = exit; }
                                if (key) { do square.decSize(); }
                                if (key) { do square.incSize(); }
                                if (key) { let direction = exit; }
                                if (key) { let direction = key; }
                                if (key) { let direction = square; }
                                if (key) { let direction = direction; }
                       
                                while (key) {
                                   let key = key;
                                   do moveSquare();
                                }
                             }
                             return;
                           }
                       }
                       
                       
                           
                         
                """;
        Parser p = new Parser(input.getBytes());
        p.parser();
        System.out.println(p.XMLOutput());
        
      
    } 
}
