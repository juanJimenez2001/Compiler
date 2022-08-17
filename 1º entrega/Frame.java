package code;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.text.Position;


public class Frame extends javax.swing.JFrame {


    private static ArrayList<TablaSimbolos> tablas = new ArrayList<TablaSimbolos>();
    private static TablaSimbolos TS = new TablaSimbolos();
    
    public Frame() {    	
        this.setLocationRelativeTo(null);
        tablas.add(TS);
    }



    private static void AnalizameEsta() {
        File FileIn = new File("hola.txt");
        File FileOut = new File("analisis.txt");  
        try {
            Reader lector = new BufferedReader(new FileReader(FileIn));
            PrintWriter escribir=new PrintWriter(FileOut);
            Lexemas lexer = new Lexemas(lector);
            String resultado = "";
            while (true) {
                Tokens tokens = lexer.yylex();
                if (tokens == null) {
                    System.out.println(resultado);
                    escribir.print(resultado);
                    escribir.close();
                    return;
                }
                switch (tokens) {
                    case ERROR:
                        resultado+="ERROR: Simbolo no definido\n";
                        break;
                    case cadena: 
                    	resultado += "<"+tokens+" , "+"\""+lexer.lexema+"\""+">\n";
                        break;
                    case entero: 
                    	resultado += "<"+tokens+" , "+lexer.lexema+">\n";
                        break;
                    case Pal_Res:
                        resultado += "<"+lexer.lexema+" , >\n";
                        break;
                    case id:
//                    	if(!TS.contieneEntrada(lexer.lexema)) {
//                    		EntradaTS posTS = TS.insertar(lexer.lexema);
//                    		resultado += "<"+tokens+" , "+posTS.getDesp()+">" + "\t// Token correspondiente al identificador: "+lexer.lexema+"\n";
//                    	}
//                    	else
//                    	{
//                    		resultado += "<"+tokens+" , "+ TS.buscarPorLexema(lexer.lexema).getDesp() +">" + "\t// Token correspondiente al identificador: "+lexer.lexema+"\n";
//                    	}
                		resultado += "<"+tokens+" , "+ TS.crearEntrada(lexer.lexema)+">\n";

                        break;
                    case suma:
                        resultado += "<"+tokens+" , >\n";
                        break;
                    case negacion:
                        resultado += "<"+tokens+" , >\n";
                        break;
                    case opAsignacion:
                    	resultado += "<"+tokens+" , >\n";
                        break;
                    case opAutodecremento:
                    	resultado += "<"+tokens+" , >\n";
                        break;
                    case puntoComa:
                        resultado += "<"+tokens+" , >\n";
                        break;
                    case coma:
                        resultado += "<"+tokens+" , >\n";
                        break;
                    case dosPuntos:
                        resultado += "<"+tokens+" , >\n";
                        break;
                    case parentesisAbierto:
                        resultado += "<"+tokens+" , >\n";
                        break;
                    case parentesisCerrado:
                        resultado += "<"+tokens+" , >\n";
                        break;
                    case llaveAbierta:
                        resultado += "<"+tokens+" , >\n";
                        break;
                    case llaveCerrada:
                        resultado += "<"+tokens+" , >\n";
                        break;
                    case mayor:
                        resultado += "<"+tokens+" , >\n";
                        break;
                    default:
              		  resultado+="ERROR: simbolo no definido\n";
              		  break; 
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Frame.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    }

    
    public String OpenFile(javax.swing.JFileChooser fc) {
    	String path=null;
    	fc.setCurrentDirectory(new File(System.getProperty("user.home")));
    	int response=fc.showOpenDialog(this);
    	if (response == fc.APPROVE_OPTION) {
    	    File selectedFile = fc.getSelectedFile();
    	    path= selectedFile.getAbsolutePath();
    	}
    	return path;
    }
    public static void main(String args[]) {

    	AnalizameEsta();
    	System.out.println(TS.toString());
    	try {
            FileWriter file= new FileWriter("D:\\Documentos\\Universidad\\3ºcarrera\\PDL\\java\\Procesador de Lenguajes\\TS.txt");
            char [] charsPath = TS.toString().toCharArray();
            for(int i=0; i<charsPath.length; i++) {
                file.write(charsPath[i]);
            }
            file.close();
        } catch (IOException e) {
            System.err.println(e);
            e.printStackTrace();
        }
    	
    }




}
