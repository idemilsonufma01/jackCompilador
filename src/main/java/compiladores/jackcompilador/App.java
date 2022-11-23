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
                       // File name: projects/10/Square/Square.jack
                       
                       // (same as projects/09/Square/Square.jack)
                       
                       /** Implements a graphical square. */
                       class Square {
                       
                          field int x, y; // screen location of the square's top-left corner
                          field int size; // length of this square, in pixels
                       
                          /** Constructs a new square with a given location and size. */
                          constructor Square new(int Ax, int Ay, int Asize) {
                             let x = Ax;
                             let y = Ay;
                             let size = Asize;
                             do draw();
                             return this;
                          }
                       
                          /** Disposes this square. */
                          method void dispose() {
                             do Memory.deAlloc(this);
                             return;
                          }
                       
                          /** Draws the square on the screen. */
                          method void draw() {
                             do Screen.setColor(true);
                             do Screen.drawRectangle(x, y, x + size, y + size);
                             return;
                          }
                       
                          /** Erases the square from the screen. */
                          method void erase() {
                             do Screen.setColor(false);
                             do Screen.drawRectangle(x, y, x + size, y + size);
                             return;
                          }
                       
                           /** Increments the square size by 2 pixels. */
                          method void incSize() {
                             if (((y + size) < 254) & ((x + size) < 510)) {
                                do erase();
                                let size = size + 2;
                                do draw();
                             }
                             return;
                          }
                       
                          /** Decrements the square size by 2 pixels. */
                          method void decSize() {
                             if (size > 2) {
                                do erase();
                                let size = size - 2;
                                do draw();
                             }
                             return;
                          }
                       
                          /** Moves the square up by 2 pixels. */
                          method void moveUp() {
                             if (y > 1) {
                                do Screen.setColor(false);
                                do Screen.drawRectangle(x, (y + size) - 1, x + size, y + size);
                                let y = y - 2;
                                do Screen.setColor(true);
                                do Screen.drawRectangle(x, y, x + size, y + 1);
                             }
                             return;
                          }
                       
                          /** Moves the square down by 2 pixels. */
                          method void moveDown() {
                             if ((y + size) < 254) {
                                do Screen.setColor(false);
                                do Screen.drawRectangle(x, y, x + size, y + 1);
                                let y = y + 2;
                                do Screen.setColor(true);
                                do Screen.drawRectangle(x, (y + size) - 1, x + size, y + size);
                             }
                             return;
                          }
                       
                          /** Moves the square left by 2 pixels. */
                          method void moveLeft() {
                             if (x > 1) {
                                do Screen.setColor(false);
                                do Screen.drawRectangle((x + size) - 1, y, x + size, y + size);
                                let x = x - 2;
                                do Screen.setColor(true);
                                do Screen.drawRectangle(x, y, x + 1, y + size);
                             }
                             return;
                          }
                       
                          /** Moves the square right by 2 pixels. */
                          method void moveRight() {
                             if ((x + size) < 510) {
                                do Screen.setColor(false);
                                do Screen.drawRectangle(x, y, x + 1, y + size);
                                let x = x + 2;
                                do Screen.setColor(true);
                                do Screen.drawRectangle((x + size) - 1, y, x + size, y + size);
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
