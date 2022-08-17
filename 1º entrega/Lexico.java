package code;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Lexico {
	
    private static String analizar() {//GEN-FIRST:event_btnAnalizarActionPerformed
        // TODO add your handling code here:
		String path = "C:\\Users\\jdhp2\\eclipse-workspace\\PDL\\src\\code\\AFRICA_TPV_1.txt";	
        File archivo = new File(path);
        PrintWriter escribir;
	
           Lector lector = new Lector(path);
            String resultado = "";
            
            String firstChar ="" + lector.readChar();
            
            switch(lector.charType(firstChar)) {
            case "cadena": 
            	String word = lector.readWord(34);
            	resultado += "< cadena , " + word + " >" + "\t// Token correspondiente a: cadena. \n";
            
            }
            
            
            

                switch (lector.readWord(32)) {
                	case "boolean": 
                		resultado += "< boolean , - >" + "\t// Token correspondiente a: boolean. \n";
                		break;
                	case "break": 
                		resultado += "< break , - >" + "\t// Token correspondiente a: break. \n";
                		break;
                	case "case": 
                		resultado += "< case , - >" + "\t// Token correspondiente a: case. \n";
                		break;
//                	case function_Tipo: 
//                		resultado += "< function , - >" + "\t// Token correspondiente a: function. \n";
//                		break;
//                	case if_Tipo: 
//                		resultado += "< if , - >" + "\t// Token correspondiente a: if. \n";
//                		break;
//                	case input_Tipo: 
//                		resultado += "< input , - >" + "\t// Token correspondiente a: input. \n";
//                		break;
//                	case int_Tipo: 
//                		resultado += "< int , - >" + "\t// Token correspondiente a: int. \n";
//                		break;	
//                	case let_Tipo: 
//                		resultado += "< let , - >" + "\t// Token correspondiente a: let. \n";
//                		break;	
//                	case print_Tipo: 
//                		resultado += "< print , - >" + "\t// Token correspondiente a: print. \n";
//                		break;
//                	case return_Tipo: 
//                		resultado += "< return , - >" + "\t// Token correspondiente a: return. \n";
//                		break;
//                	case string_Tipo: 
//                		resultado += "< string , - >" + "\t// Token correspondiente a: string. \n";
//                		break;
//                	case switch_Tipo: 
//                		resultado += "< switch , - >" + "\t// Token correspondiente a: switch. \n";
//                		break;
//                	case opAutodecremento: 
//                		resultado += "< opAutodecremento , - >" + "\t// Token correspondiente a: opAutodecremento. \n";
//                		break;
////                	case cadena: 
////                   		resultado += "< cadena , " + lexer.lexema + " >" + "\t// Token correspondiente a: entero. \n";
////                		break;
////                	case entero: 
////                		resultado += "< numero , " + lexer.numero + " >" + "\t// Token correspondiente a: entero. \n";
////                		break;
//                	case coma: 
//                		resultado += "< coma , - >" + "\t// Token correspondiente a: coma. \n";
//                		break;
//                	case dosPuntos: 
//                		resultado += "< dosPuntos , - >" + "\t// Token correspondiente a: dosPuntos. \n";
//                		break;
//                	case parentesisAbierto: 
//                		resultado += "< parentesisAbierto , - >" + "\t// Token correspondiente a: parentesisAbierto. \n";
//                		break;
//                	case parentesisCerrado: 
//                		resultado += "< parentesisCerrado , - >" + "\t// Token correspondiente a: parentesisCerrado. \n";
//                		break;
//                	case llaveAbierta: 
//                		resultado += "< llaveAbierta , - >" + "\t// Token correspondiente a: llaveAbierta. \n";
//                		break;
//                	case llaveCerrada: 
//                		resultado += "< llaveCerrada , - >" + "\t// Token correspondiente a: llaveCerrada. \n";
//                		break;
                	default:
                		  resultado+="ERROR: simbolo no definido";
                		  break;                
                }
                return resultado;
    }
    
    public static void main (String[] args) {
    	System.out.println(analizar());
    }
    
}


