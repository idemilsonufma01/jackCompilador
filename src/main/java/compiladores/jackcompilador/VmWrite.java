/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package compiladores.jackcompilador;

/**
 *
 * @author RAIMUNDA
 */
public class VmWrite {
    private StringBuilder vmOutput = new StringBuilder();
    
    //segmenetos
     enum Segment {
        CONST("constant"),
        ARG("argument"),
        LOCAL("local"),
        STATIC("static"),
        THIS("this"),
        THAT("that"),
        POINTER("pointer"),
        TEMP("temp");
        
        private Segment(String value) {
            this.value = value;
        }

        public String value;

     }
     //comandos logicos e aritmeticos
     enum Command {
        ADD,
        SUB,
        NEG,
        EQ,
        GT,
        LT,
        AND,
        OR,
        NOT
    };

     
     
     


}
