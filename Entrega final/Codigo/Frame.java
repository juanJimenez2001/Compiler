package code;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.util.ArrayList;


public class Frame extends javax.swing.JFrame {

	private ArrayList<TablaSimbolos> tablas = new ArrayList<TablaSimbolos>();
	private ArrayList<TablaSimbolos> eliminadas = new ArrayList<TablaSimbolos>();
	public TablaSimbolos TS;
	private static ArrayList<String> lexemas = new ArrayList<String>();

	private static boolean parametrosFuncion = false;
	private static boolean zonaDecl = false;

	public static int contadorTablas = 1;

	private static int index = 0;

	private static Reader lector;
	private static PrintWriter escribir;
	private static String resultado;
	private static Lexemas lexer;
	public int nLinea;

	public Frame() throws FileNotFoundException { 
		TS = new TablaSimbolos(contadorTablas);
		contadorTablas++;
		this.setLocationRelativeTo(null);

		tablas.add(TS);
		File FileIn = new File("hola.txt");
		File FileOut = new File("analisis.txt");  
		lector = new BufferedReader(new FileReader(FileIn));
		escribir=new PrintWriter(FileOut);
		lexer = new Lexemas(lector);
		resultado = "";
		nLinea=0;

	}

	public ArrayList<String> getLexemas(){
		return lexemas;
	}

	public ArrayList<TablaSimbolos> getTablas(){
		return tablas;
	}

	public ArrayList<TablaSimbolos> getEliminadas(){
		return eliminadas;
	}

	public void addEliminada(TablaSimbolos ts) {
		eliminadas.add(ts);
	}

	public String removeLexema() {
		return lexemas.remove(0);
	}

	public TablaSimbolos getTS() {
		return TS;
	}

	public String getLexema() {
		return lexemas.get(0);
	}

	public int getIndex() {
		return index;
	}

	public void addIndex() {
		index++;
	}

	public int getContadorTablas() {
		return contadorTablas;
	}

	public void addContadorTablas() {
		contadorTablas++;
	}

	public void resIndex() {
		index--;
	}

	public void setZonaDecl(boolean b) {
		zonaDecl = b;
	}

	public void setParametrosFuncion(boolean b) {
		parametrosFuncion = b;
	}


	public EntradaTS buscarPorLexema(String lexema) {
		for(int i = tablas.size() - 1 ; i >= 0 ; i--) {
			EntradaTS res =  tablas.get(i).buscarPorLexema(lexema);
			if(res != null) {
				//System.out.println("Consigo " + lexema + " en tabla: " + i + " (Index: " + index + ") "+ " de tipo " + tablas.get(i).buscarPorLexema(lexema).getTipo());
				return res;
			}
		}
		return null;
	}

	public EntradaTS buscarFuncion(String lexema) {
		for(int i = tablas.size() - 1 ; i >= 0 ; i--) {
			EntradaTS res =  tablas.get(i).buscarPorLexema(lexema);
			if(res != null && res.getTipo().equals("funcion")) {
				//System.out.println("Consigo " + lexema + " en tabla: " + i + " (Index: " + index + ") "+ " de tipo " + tablas.get(i).buscarPorLexema(lexema).getTipo());
				return res;
			}
		}
		return null;
	}

	public static void lista() {
//		System.out.print("lista :");
//		for(String s : lexemas)
//			System.out.print(" " + s + " ");
//		System.out.println();
	}



	public String anLex() throws IOException{
		String sintactico="";
		Tokens tokens = lexer.yylex();
		if (tokens == null) {
			escribir.print(resultado);
			escribir.close();
			return"$";
		}
		nLinea=lexer.yylineno+1;
		//System.err.println(tokens+" nº linea: "+nLinea + " " + lexer.lexema);
		switch (tokens) {
		case ERROR:
			resultado+="ERROR LEXICO: Simbolo " +lexer.lexema+ " no definido en linea: "+nLinea+"\n";
			System.err.println("ERROR LEXICO: Simbolo " +lexer.lexema+ " no definido en linea: "+nLinea);
			sintactico="Error";
			break;
		case cadena:
			if(lexer.lexema.length()<=64) {
				resultado += "<"+tokens+" , "+lexer.lexema+">\n";
				sintactico="cadena";
			}
			else {
				resultado+="ERROR LEXICO: Cadena " +lexer.lexema+ " esta fuera de rango en linea "+nLinea+"\n";
				System.err.println("ERROR LEXICO: Cadena " +lexer.lexema+ " esta fuera de rango en linea "+nLinea);
				sintactico="Error";
			}
			break;
		case entero: 
			if(Integer.parseInt(lexer.lexema)<=32767) {
				resultado += "<"+tokens+" , "+lexer.lexema+">\n";
				sintactico="entero";
			}
			else {
				resultado+="ERROR LEXICO: Entero " +lexer.lexema+ " esta fuera de rango en linea "+nLinea+"\n";
				System.err.println("ERROR LEXICO: Entero " +lexer.lexema+ " esta fuera de rango en linea "+nLinea);
				sintactico="Error";
			}
			break;
		case Pal_Res:
			resultado += "<"+lexer.lexema+" , >\n";
			sintactico=lexer.lexema;
			break;
		case id:
			String lex = lexer.lexema;

			if(buscarPorLexema(lex) == null && !zonaDecl) {
				//System.out.println("                    Variable GLOBAL: " + lexer.lexema);
				//System.out.println("Inserto en tabla: " + 0 + " el lexema " + lex);
				resultado += "<"+tokens+" , "+ tablas.get(0).crearEntrada(lex)+">\n";
				TS.pos += 1;
			}
			else if(zonaDecl){
				//System.out.println("Inserto en tabla: " + index + " el lexema " + lex);
				resultado += "<"+tokens+" , "+ tablas.get(index).crearEntrada(lex)+">\n";
			}
			else {
				resultado += "<"+tokens+" , "+ buscarPorLexema(lex).getPos()+">\n";
			}
			//System.out.println("Meto en lexemas: " + lex + " " + buscarPorLexema(lexer.lexema).getTipo());

			lexemas.add(lex);
			lista();
			sintactico="id";
			break;
		case suma:
			resultado += "<"+tokens+" , >\n";
			sintactico="+";
			break;
		case negacion:
			resultado += "<"+tokens+" , >\n";
			sintactico="!";
			break;
		case opAsignacion:
			resultado += "<"+tokens+" , >\n";
			sintactico="=";
			break;
		case opAutodecremento:
			resultado += "<"+tokens+" , >\n";
			sintactico="--";
			break;
		case puntoComa:
			resultado += "<"+tokens+" , >\n";
			sintactico=";";
			break;
		case coma:
			resultado += "<"+tokens+" , >\n";
			sintactico=",";
			break;
		case dosPuntos:
			resultado += "<"+tokens+" , >\n";
			sintactico=":";
			break;
		case parentesisAbierto:
			resultado += "<"+tokens+" , >\n";
			sintactico="(";
			break;
		case parentesisCerrado:
			resultado += "<"+tokens+" , >\n";
			sintactico=")";
			break;
		case llaveAbierta:
			resultado += "<"+tokens+" , >\n";
			sintactico="{";
			break;
		case llaveCerrada:
			resultado += "<"+tokens+" , >\n";
			sintactico="}";
			break;
		case mayor:
			resultado += "<"+tokens+" , >\n";
			sintactico=">";
			break;
		default:
			resultado+="ERROR LEXICO: simbolo no definido en linea "+nLinea+"\n";
			System.err.println("ERROR LEXICO: simbolo no definido en linea "+nLinea);
			sintactico="Error";
			break; 
		}
		return sintactico;
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

}
