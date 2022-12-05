/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */

package compiladores.jackcompilador;

//import static compilador.token.TokenType.*;
//port static compilador.token.TokenType.EOF;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 *
 * @author RAIMUNDA
 */
public class App {
    
    
    public static void saveFile(String fileName, String output) {
  
       
        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(fileName);
            byte[] strToBytes = output.getBytes();
            outputStream.write(strToBytes);
    
            outputStream.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    private static String fromFile(File file) {
        //File file = new File("Main.jack");

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
        
    /*
        String input = """";
                     
        Parser p = new Parser(input.getBytes());
        p.parser();
        System.out.println(p.XMLOutput());
        
      
    } 
    */
    if (args.length != 1) {
            System.err.println("Please provide a single file path argument.");
            System.exit(1);
        }

        File file = new File(args[0]);

        if (!file.exists()) {
            System.err.println("there is no file");
            System.exit(1);
        }

        // we need to compile every file in the directory
        if (file.isDirectory()) {
            for (File f : file.listFiles()) {
                if (f.isFile() && f.getName().endsWith(".jack")) {

                    var inputFileName = f.getAbsolutePath();
                    var pos = inputFileName.indexOf('.');
                    var outputFileName = inputFileName.substring(0, pos) + ".vm";
                    
                    System.out.println("compiling " +  inputFileName);
                    var input = fromFile(f);
                    var parser = new Parser(input.getBytes(StandardCharsets.UTF_8));
                    parser.parser();
                    var result = parser.VMOutput();
                    saveFile(outputFileName, result);
                }

            }
        // compilando arquivo
        } else if (file.isFile()) {
            if (!file.getName().endsWith(".jack"))  {
                System.err.println("provide a filename that ends with .jack");
                System.exit(1);
            } else {
                var inputFileName = file.getAbsolutePath();
                var pos = inputFileName.indexOf('.');
                var outputFileName = inputFileName.substring(0, pos) + ".vm";
                
                System.out.println("compiling " +  inputFileName);
                var input = fromFile(file);
                var parser = new Parser(input.getBytes(StandardCharsets.UTF_8));
                parser.parser();
                var result = parser.VMOutput();
                saveFile(outputFileName, result);
                
            }
        }
    }


}
