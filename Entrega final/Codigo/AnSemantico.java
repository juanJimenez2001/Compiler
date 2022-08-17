package code;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class AnSemantico {

	public static Frame marco ;
	private static String sig_tok;
	private static List<Integer> parse;
	private static boolean err;

	private static String funcionActual = "";

	private static ArrayList<String> parametrosFuncion = new ArrayList<String>();

	private static ArrayList<String> compararParametros = new ArrayList<String>();

	private static String llamadaFuncion = "";

	private static String OK = "tipo_ok";
	private static String ERROR = "tipo_error";
	private static String INT = "int";
	private static String BOOLEAN = "boolean";
	private static String STRING = "string";
	private static String LOGICO = "logico";
	private static String FUN = "funcion";
	private static String AUTODECREMENTO = "autodecremento";
	private static boolean haHechoReturn=false;

	public AnSemantico() throws FileNotFoundException {
		marco= new Frame();
		parse = new LinkedList<>();
		err=false;
	}

	public List<Integer> getLista(){
		return parse;
	}

	public static void lista() {
		/*System.out.print("lista :");
		for(String s : marco.getLexemas())
			System.out.print(" " + s + " ");
		System.out.println();*/
	}

	public static void A_Sint() throws IOException {
		sig_tok = marco.anLex();
		if(sig_tok.equals("Error")) {
			err=true; 
			sig_tok="";
		}
		P2();
		if(!sig_tok.equals("$") && !err) 
			System.err.println("ERROR: el útlimo token no es $");
	}

	private static void equipara(String token) throws IOException {
		//System.out.println("Equiparo: " + token);
		lista();
		//System.out.println("Index: " + marco.getIndex());
		if(sig_tok.equals(token)) {			
			sig_tok=marco.anLex();

			if(sig_tok.equals("Error")) {
				System.err.println("Error lexico");
				err=true; 
				sig_tok="";
			}
		}
		else {
			errorSintactico();
		}
	}

	private static boolean esOk(String s) {
		if(s.equals("tipo_ok"))
			return true;
		return false;
	}


	private static String P2() throws IOException{
		if(esOk(P())){
			return "OK";
		}
		return "ERROR";
	}

	private static String P() throws IOException {
		if(sig_tok.equals("let") || sig_tok.equals("if") || sig_tok.equals("switch") || sig_tok.equals("id") || sig_tok.equals("print") ||sig_tok.equals("input") || sig_tok.equals("return")) {
			parse.add(1);
			if(esOk(B()) && esOk(Z()))
				return OK;
			return ERROR;
		}
		else if(sig_tok.equals("function")) {
			parse.add(2);
			if(esOk(F()) && esOk(Z()))
				return OK;
			return ERROR;
		}
		else if(sig_tok.equals("$")) {
			return OK; 
		}
		else {
			errorSintactico();
		}
		return ERROR;
	}

	private static String B() throws IOException {
		if(sig_tok.equals("let")){
			parse.add(3);
			equipara("let");
			marco.setZonaDecl(true);
			String tipo = T();		
			equipara("id");
			marco.buscarPorLexema(marco.getLexema()).setTipo(tipo);
			if(tipo.equals("int"))
				marco.getTablas().get(marco.getIndex()).pos+=1;
			if(tipo.equals("boolean"))
				marco.getTablas().get(marco.getIndex()).pos+=1;
			if(tipo.equals("string"))
				marco.getTablas().get(marco.getIndex()).pos+=64;
			//System.err.println("Tipo: "+marco.getLexema()+"   "+marco.buscarPorLexema(marco.getLexema()).getTipo());
			//System.out.println("Elimino(Blet): " + marco.getLexema());
			marco.removeLexema();
			marco.setZonaDecl(false);
			equipara(";");
			return OK;
		}
		else if(sig_tok.equals("if")) {
			parse.add(4);
			equipara("if");
			equipara("(");
			String aux = sig_tok;

			if(aux.equals("id") && marco.buscarFuncion(marco.getLexema()) != null && marco.buscarPorLexema(marco.getLexema()).getTipo().equals(FUN)) {
				aux = "";
			}
			String tipoE = E();
			equipara(")");
			if(!aux.equals("!") && aux.equals("id")) {
				//System.out.println("Elimino(Bif): " + marco.getLexema());
				marco.removeLexema();
			}
			String tipoS = S();

			if((tipoE.equals(LOGICO) || tipoE.equals(BOOLEAN)) && esOk(tipoS))
				return OK;
			return ERROR;
		}
		else if(sig_tok.equals("switch")) {
			parse.add(5);
			equipara("switch");
			equipara("(");
			String tipoY = Y();
			equipara(")");
			equipara("{");
			String tipoW = W();
			equipara("}");

			if(esOk(tipoY) && esOk(tipoW))
				return OK;
			return ERROR;
		}
		else if(sig_tok.equals("id") || sig_tok.equals("return") || sig_tok.equals("print") || sig_tok.equals("input")) {
			parse.add(6);
			if(esOk(S()))
				return OK;
			return ERROR;
		}
		else {
			errorSintactico();
		}
		return ERROR;
	}

	private static String T() throws IOException {
		if(sig_tok.equals("int")) {
			parse.add(7);
			equipara("int");
			return INT;
		}
		else if(sig_tok.equals("string")) {
			parse.add(8);
			equipara("string");
			return STRING;
		}
		else if(sig_tok.equals("boolean")) {
			parse.add(9);
			equipara("boolean");
			return BOOLEAN;
		}
		else {
			errorSintactico();
		}
		return ERROR;
	}

	private static String S() throws IOException {
		lista();
		if(sig_tok.equals("id")) {
			parse.add(10);
			equipara("id");
			String tipoID = marco.buscarPorLexema(marco.getLexema()).getTipo();
			llamadaFuncion = marco.getLexema();
			if(marco.buscarFuncion(llamadaFuncion) == null) {
				//System.out.println("Elimino(S): " + marco.getLexema());
				marco.removeLexema();
			}
			String tipoS2 = S2();
			if((tipoID.equals(FUN) && tipoS2.equals("void")) || tipoID.equals(tipoS2) || (tipoID.equals(BOOLEAN) && tipoS2.equals(LOGICO)) || (tipoID.equals(LOGICO) && tipoS2.equals(BOOLEAN))) {
				return OK;
			}
			else {
				System.err.println("ERROR SEMANTICO: tipo de datos no compatible en la linea: "+ marco.nLinea
						+", tenemos: "+tipoS2+ " pero necesitamos: "+tipoID);

				return ERROR;
			}
		}
		else if(sig_tok.equals("print")) {
			parse.add(11);
			equipara("print");
			equipara("(");
			String aux = sig_tok;
			if(aux.equals("id") && marco.buscarFuncion(marco.getLexema()) != null && marco.buscarPorLexema(marco.getLexema()).getTipo().equals(FUN)) {
				aux = "";
			}
			String tipoE = E();
			if(tipoE.equals(INT) || tipoE.equals(STRING)) {
				
				if(aux.equals("id")) {
					//System.out.println("Elimino(Sprint): " + marco.getLexema());
					marco.removeLexema();
				}
				equipara(")");
				equipara(";");
	
				return OK;
			}
			else {
				System.err.println("ERROR SEMANTICO: print de un elemento que no es un entero ni una cadena en la linea: "+ marco.nLinea);
				return ERROR;
			}
		}
		else if(sig_tok.equals("input")) {
			parse.add(12);
			equipara("input");
			equipara("(");
			equipara("id");
			if(marco.buscarPorLexema(marco.getLexema()).getTipo().equals(INT) || marco.buscarPorLexema(marco.getLexema()).getTipo().equals(STRING)) {
				//System.out.println("Elimino(Sinput): " + marco.getLexema());
				marco.removeLexema();
				equipara(")");
				equipara(";");
				return OK;
			}
			else {
				System.err.println("ERROR SEMANTICO: input de un elemento que no es un entero ni una cadena en la linea: "+ marco.nLinea + " " + marco.getLexema());
				return ERROR;
			}



		}
		else if(sig_tok.equals("return") && !funcionActual.equals("")) {
			parse.add(13);
			equipara("return");
			
			String aux = sig_tok;
			if(aux.equals("id") && marco.buscarFuncion(marco.getLexema()) != null && marco.buscarPorLexema(marco.getLexema()).getTipo().equals(FUN)) {
				aux = "";
			}
			String tipoX = X();
			if(aux.equals("id")) {
				//System.out.println("Elimino(Sreturn): " + marco.getLexema());
				marco.removeLexema();
			}
			if(tipoX.equals(LOGICO))
				tipoX = BOOLEAN;
			if(tipoX.equals(marco.buscarFuncion(funcionActual).getTipoRetorno())) {
				equipara(";");
				haHechoReturn=true;
				return OK;
			}
			else {
				System.err.println("ERROR SEMANTICO: return deberia ser de un elemento de tipo "+marco.buscarFuncion(funcionActual).getTipoRetorno()+" en la linea: "+ marco.nLinea);
				return ERROR;
			}
		}
		else {
			errorSintactico();
		}
		return ERROR;
	}

	private static String S2() throws IOException {
		if(sig_tok.equals("=")) {
			//System.out.println("s2");
			parse.add(14);
			equipara("=");
			String aux = sig_tok;
			
			if(aux.equals("id") && marco.buscarFuncion(marco.getLexema()) != null && marco.buscarPorLexema(marco.getLexema()).getTipo().equals(FUN)) {
				aux = "";
			}
			String tipoE = E();
			if(aux.equals("id") ) {
				//System.out.println("Elimino(S2var): " + marco.getLexema());
				marco.removeLexema();
			}
			equipara(";");
			return tipoE;
		}
		else if(sig_tok.equals("(")) {
			parse.add(15);
			llamadaFuncion = marco.getLexema();
			String aux = marco.buscarPorLexema(marco.getLexema()).getTipoRetorno();
			//System.out.println("Elimino(S2fun): " + marco.getLexema());
			marco.removeLexema();
			equipara("(");
			String tipoL = L();
			if(esOk(tipoL)) {
				equipara(")");
				equipara(";");
				return aux;
			}
			return ERROR;


		}
		else {
			errorSintactico();
		}
		return ERROR;
	}

	private static String X() throws IOException {
		if(sig_tok.equals("!") || sig_tok.equals("id") || sig_tok.equals("(") || sig_tok.equals("entero")|| sig_tok.equals("cadena") || sig_tok.equals("--")) {
			parse.add(16);
			String tipoE = E();
			return tipoE;
		}
		else if(sig_tok.equals(";")) {
			parse.add(17);
			return OK;
		}
		else {
			errorSintactico();
		}
		return ERROR;
	}

	private static String C() throws IOException {
		if(sig_tok.equals("let") || sig_tok.equals("if") || sig_tok.equals("switch") || sig_tok.equals("id") ||
				sig_tok.equals("print") || sig_tok.equals("input") || sig_tok.equals("return")) {
			parse.add(18);
			if(esOk(B()) && esOk(C()))
				return OK;
			return ERROR;
		}
		else if(sig_tok.equals("}") || sig_tok.equals("case") || sig_tok.equals("break")) {
			parse.add(19);
			return OK;
		}
		else {
			errorSintactico();
		}
		return ERROR;
	}

	private static String L() throws IOException {
		if(sig_tok.equals("id") || sig_tok.equals("(") || sig_tok.equals("entero") || sig_tok.equals("cadena") || sig_tok.equals("!")) {
			parse.add(20);

			String aux = sig_tok;
			if(aux.equals("id") && marco.buscarFuncion(marco.getLexema()) != null && marco.buscarPorLexema(marco.getLexema()).getTipo().equals(FUN)) {
				aux = "";
			}

			String tipoE = E();

			boolean pa = true;

			if(!tipoE.equals(marco.buscarFuncion(llamadaFuncion).getParam(0)))
				pa = false;

			if(aux.equals("id")) {
				//System.out.println("Elimino(L): " + marco.getLexema());
				marco.removeLexema();
			}
			String tipoQ = Q(llamadaFuncion, 1);


			if(!tipoE.equals(ERROR) && esOk(tipoQ) && pa)
				return OK;
			return ERROR;
		}
		else if(sig_tok.equals(")")) {
			parse.add(21);
			return OK;
		}
		else {
			errorSintactico();
		}
		return ERROR;
	}

	private static String Q(String fun, int i) throws IOException {
		if(sig_tok.equals(",")) {
			parse.add(22);
			equipara(",");
			boolean pa = true;

			String aux = sig_tok;
			if(aux.equals("id") && marco.buscarFuncion(marco.getLexema()) != null && marco.buscarPorLexema(marco.getLexema()).getTipo().equals(FUN)) {
				aux = "";
			}
			String tipoE = E();

			if(!tipoE.equals(marco.buscarFuncion(fun).getParam(i))) {
				pa = false;
			}
			i++;


			if(aux.equals("id")) {
				//System.out.println("Elimino(Q): " + marco.getLexema());
				marco.removeLexema();
			}
			String tipoQ = Q(llamadaFuncion, i);
			compararParametros.add(tipoE);


			if(!tipoE.equals(ERROR) && esOk(tipoQ) && pa)
				return OK;
			return ERROR;
		}
		else if(sig_tok.equals(")")) {
			parse.add(23);
			return OK;
		}
		else {
			errorSintactico();
		}
		return ERROR;
	}

	private static String F() throws IOException {
		String res = ERROR;
		if(sig_tok.equals("function")) {
			parse.add(24);
			equipara("function");
			equipara("id");
			marco.buscarPorLexema(marco.getLexema()).setTipo(FUN);;
			marco.TS.pos -= 1;
			funcionActual = marco.getLexema();
			String tipoH = H();
			if(!esOk(tipoH)) {
				//System.out.println("Valor de retorno de " + marco.getLexema() + " es " + tipoH);
				marco.buscarPorLexema(marco.getLexema()).setTipoRetorno(tipoH);

			}
			//System.out.println("Elimino(F): " + marco.getLexema());
			marco.removeLexema();

			marco.setParametrosFuncion(true);

			marco.getTablas().add(new TablaSimbolos(marco.getContadorTablas()));	
			marco.addContadorTablas();
			marco.addIndex();
			marco.getTablas().get(marco.getIndex()).funcion = funcionActual;	
			//System.out.println("CREO TABLA: " + marco.getIndex());



			equipara("(");

			marco.setZonaDecl(true);

			String tipoA = A();

			marco.setZonaDecl(false);
			String[] aux = new String[parametrosFuncion.size()];
			marco.buscarFuncion(funcionActual).setNumParam(parametrosFuncion.size());

			for(int i = 0 ; i < aux.length ; i++) {
				aux[i] = parametrosFuncion.get(i);
			}

			marco.buscarFuncion(funcionActual).setTipoParamXX(aux);	
			parametrosFuncion.removeAll(parametrosFuncion);



			if(esOk(tipoA))
				res = OK;
			else 
				res = ERROR;
			equipara(")");
			equipara("{");

			String tipoC = C();
			if(esOk(tipoC))
				res = OK;
			else 
				res = ERROR;
			equipara("}");
			if(!marco.buscarPorLexema(funcionActual).getTipoRetorno().equals("void") && !haHechoReturn) {
				System.err.println("ERROR SEMANTICO: no se ha hecho return en la funcion: "+funcionActual);
				res=ERROR;
			}
			haHechoReturn=false;
			
			funcionActual = "";
			marco.setParametrosFuncion(false);

			marco.getEliminadas().add(marco.getTablas().remove(marco.getIndex()));
			//System.out.println("BORRO TABLA: " + marco.getIndex());
			marco.resIndex();



			return res;
		}
		else {
			errorSintactico();
		}
		return ERROR;
	}

	private static String H() throws IOException {
		if(sig_tok.equals("int") || sig_tok.equals("string") || sig_tok.equals("boolean")) {
			parse.add(25);
			String tipo = T();
			if(tipo.equals(INT) || tipo.equals(BOOLEAN) || tipo.equals(STRING)) {
				//System.out.println("H devuelve : " +  OK);
				return tipo;
			}
			return ERROR;
		}
		else if(sig_tok.equals("(")) {
			parse.add(26);
			return OK;
		}
		else {
			errorSintactico();
		}
		return ERROR;
	}

	private static String A() throws IOException {
		if(sig_tok.equals("int") || sig_tok.equals("string") || sig_tok.equals("boolean")) {
			parse.add(27);
			String res = ERROR;
			String tipoT = T();
			if((tipoT.equals(INT) || tipoT.equals(BOOLEAN) || tipoT.equals(STRING)))
				res =  OK;
			else
				res =  ERROR;
			equipara("id");
			if(tipoT.equals("int"))
				marco.getTablas().get(marco.getIndex()).pos+=1;
			if(tipoT.equals("boolean"))
				marco.getTablas().get(marco.getIndex()).pos+=1;
			if(tipoT.equals("string"))
				marco.getTablas().get(marco.getIndex()).pos+=64;
			marco.getTablas().get(marco.getIndex()).setTipo(marco.getLexema(),(tipoT));

			parametrosFuncion.add(tipoT);

			//System.out.println("Elimino(A): " + marco.getLexema());
			marco.removeLexema();
			String tipoK = K();
			if(esOk(tipoK))
				res =  OK;
			else
				res =  ERROR;
			return res;
		}
		else if(sig_tok.equals(")")) {
			parse.add(28);
			return OK;
		}
		else {
			errorSintactico();
		}
		return ERROR;
	}

	private static String K() throws IOException {
		if(sig_tok.equals(",")) {
			parse.add(29);
			String res = ERROR;
			equipara(",");
			String tipoT = T();
			if((tipoT.equals(INT) || tipoT.equals(BOOLEAN) || tipoT.equals(STRING)))
				res =  OK;
			else
				res =  ERROR;
			equipara("id");
			if(tipoT.equals("int"))
				marco.getTablas().get(marco.getIndex()).pos+=1;
			if(tipoT.equals("boolean"))
				marco.getTablas().get(marco.getIndex()).pos+=1;
			if(tipoT.equals("string"))
				marco.getTablas().get(marco.getIndex()).pos+=64;
			marco.getTablas().get(marco.getIndex()).setTipo(marco.getLexema(),(tipoT));
			parametrosFuncion.add(tipoT);

			//System.out.println("Elimino(K): " + marco.getLexema());
			marco.removeLexema();
			String tipoK = K();
			if(esOk(tipoK))
				res =  OK;
			else
				res =  ERROR;
			return res;
		}
		else if(sig_tok.equals(")")) {
			parse.add(30);
			return OK;
		}
		else {
			errorSintactico();
		}
		return ERROR;
	}

	private static String E() throws IOException {
		if(sig_tok.equals("id") || sig_tok.equals("(") || sig_tok.equals("entero") || sig_tok.equals("cadena")|| sig_tok.equals("--") || sig_tok.equals("!")) {
			parse.add(31);

			String tipoU = U();
			String tipoE2 = E2();

			if(tipoU.equals(INT) && tipoE2.equals(LOGICO))
				return LOGICO;
			if(!tipoU.equals(ERROR)) 
				return tipoU;
			return ERROR;
		}
		else {
			errorSintactico();
		}
		return ERROR;
	}

	private static String E2() throws IOException {

		if(sig_tok.equals(">")) {
			parse.add(32);
			equipara(">");
			String aux = sig_tok;
			String tipoU = U();
			if(aux.equals("id")) {
				//System.out.println("Elimino(E2): " + marco.getLexema());
				marco.removeLexema();
			}
			if(tipoU.equals(INT)) {
				E2();
				return LOGICO;
			}
			else {
				System.err.println("ERROR SEMANTICO: mayor de un elemento que no es un entero en la linea: "+ marco.nLinea);

				return ERROR;
			}
		}
		else if(sig_tok.equals(")") || sig_tok.equals(";") || sig_tok.equals(",")) {
			parse.add(33);
			return OK;
		}
		else {
			errorSintactico();
		}
		return ERROR;
	}

	private static String U() throws IOException {
		if(sig_tok.equals("id") || sig_tok.equals("(") || sig_tok.equals("entero") || sig_tok.equals("cadena")|| sig_tok.equals("--") || sig_tok.equals("!")) {
			parse.add(34);
			String tipoV = V();
			String tipoU2 = U2();
			if(tipoV.equals(INT) && tipoU2.equals(INT))
				return INT;
			if(!tipoV.equals(ERROR))
				return tipoV;

		}
		else {
			errorSintactico();
		}
		return ERROR;
	}

	private static String U2() throws IOException {
		if(sig_tok.equals("+")) {
			parse.add(35);
			equipara("+");
			String aux = sig_tok;
			String tipoV = V();
			if(aux.equals("id")) {
				//System.out.println("Elimino(U2): " + marco.getLexema());
				marco.removeLexema();
			}

			if(tipoV.equals(INT)) {
				U2();
				return INT;
			}
			else {
				System.err.println("ERROR SEMANTICO: suma de un elemento que no es un entero en la linea: "+ marco.nLinea);
				return ERROR;
			}		
		}
		else if(sig_tok.equals(">") || sig_tok.equals(")") || sig_tok.equals(";") || sig_tok.equals(",")) {
			parse.add(36);
			return OK;
		}
		else {
			errorSintactico();
		}
		return ERROR;
	}

	private static String V() throws IOException {
		if(sig_tok.equals("id") || sig_tok.equals("(")) {
			parse.add(37);
			return G();
		}
		else if(sig_tok.equals("entero")) {
			parse.add(38);
			equipara("entero");
			return INT;
		}
		else if(sig_tok.equals("cadena")) {
			parse.add(39);
			equipara("cadena");
			return STRING;
		}
		else if(sig_tok.equals("!")) {
			parse.add(40);
			equipara("!");
			String aux = sig_tok;
			if(aux.equals("id") && marco.buscarFuncion(marco.getLexema()) != null && marco.buscarPorLexema(marco.getLexema()).getTipo().equals(FUN)) {
				aux = "";
			}
			String tipoG=G();
			if(aux.equals("id")) {
				//System.out.println("Elimino(G!): " + marco.getLexema());
				marco.removeLexema();
			}
			if(tipoG.equals(LOGICO) || tipoG.equals(BOOLEAN) || (!tipoG.equals(ERROR) && marco.buscarPorLexema(marco.getLexema()).getTipo().equals(BOOLEAN))) {
				return LOGICO;
			}
			else {
				System.err.println("ERROR SEMANTICO: negación de un elemento que no es logico en la linea: "+ marco.nLinea);
				return ERROR;
			}
		}
		else {
			errorSintactico();
		}
		return ERROR;
	}

	private static String G() throws IOException{
		if(sig_tok.equals("id")) {

			parse.add(41);
			equipara("id");
			String tipoV2 = V2();
			if(esOk(tipoV2) || (tipoV2.equals(AUTODECREMENTO) && marco.buscarPorLexema(marco.getLexema()).getTipo().equals(INT))) {
				return marco.buscarPorLexema(marco.getLexema()).getTipo();
			}
			if(!tipoV2.equals(ERROR)) {
				return tipoV2;
			}
			else {
				if(tipoV2.equals(AUTODECREMENTO) && marco.buscarPorLexema(marco.getLexema()).getTipo().equals(INT)) {
					System.err.println("ERROR SEMANTICO: no se puede hacer un autodecremento de un tipo que no sea entero en la linea: "+ marco.nLinea);
					return ERROR;
				}
				else {
					System.err.println("ERROR SEMANTICO: parametro incorrecto en la llamada a una funcion en la linea: "+ marco.nLinea);
					return ERROR;
				}
			}	
		}
		else if(sig_tok.equals("(")) {
			parse.add(42);
			equipara("(");
			String aux = sig_tok;
			if(aux.equals("id") && marco.buscarFuncion(marco.getLexema()) != null && marco.buscarPorLexema(marco.getLexema()).getTipo().equals(FUN)) {
				aux = "";
			}
			String tipoE = E();

			if(aux.equals("id")) {
				//System.out.println("Elimino(G(): " + marco.getLexema());
				marco.removeLexema();

			}
			if(!tipoE.equals(ERROR)) {
				equipara(")");

				return tipoE;
			}
			return ERROR;


		}
		else {
			errorSintactico();
		}
		return ERROR;
	}

	private static String V2() throws IOException {
		if(sig_tok.equals("--")) {
			parse.add(43);
			equipara("--");
			return AUTODECREMENTO;
		}
		else if(sig_tok.equals("(")) {
			parse.add(44);
			llamadaFuncion = marco.getLexema();
			String aux = marco.buscarPorLexema(marco.getLexema()).getTipoRetorno();
			//System.out.println("Elimino(V2): " + marco.getLexema());
			marco.removeLexema();
			equipara("(");
			String tipoL = L();
			if(esOk(tipoL)) {
				equipara(")");
				return aux;
			}
			return ERROR;


		}
		else if(sig_tok.equals("+") || sig_tok.equals(">") || sig_tok.equals(")") || sig_tok.equals(";") || sig_tok.equals(",")) {
			parse.add(45);
			return OK;
		}
		else {
			errorSintactico();
		}
		return ERROR;
	}

	private static String Y() throws IOException {
		if(sig_tok.equals("id")) {
			parse.add(46);
			equipara("id");
			if(marco.buscarPorLexema(marco.getLexema()).getTipo().equals(INT)) {
				//System.out.println("Elimino(Y): " + 
				marco.removeLexema();
				return OK;
			}
			else {
				System.err.println("ERROR SEMANTICO: no se puede hacer un switch de un tipo que no sea entero en la linea: "+ marco.nLinea);
				return ERROR;
			}	
		}
		else if(sig_tok.equals("entero")) {
			parse.add(47);
			equipara("entero");
			return OK;
		}
		else {
			errorSintactico();
		}
		return ERROR;
	}

	private static String W() throws IOException {
		if(sig_tok.equals("case")) {
			parse.add(48);
			equipara("case");
			equipara("entero");
			equipara(":");
			if(esOk(C()) && esOk(W()))
				return OK;
			return ERROR;
		}
		else if(sig_tok.equals("break")) {
			parse.add(49);
			equipara("break");
			equipara(";");
			if(esOk(W()))
				return OK;
			return ERROR;

		}
		else if(sig_tok.equals("}")) {
			parse.add(50);
			return OK;
		}
		else {
			errorSintactico();
		}
		return ERROR;
	}

	private static String Z() throws IOException {
		if(sig_tok.equals("let") || sig_tok.equals("if") || sig_tok.equals("switch") || sig_tok.equals("id") || sig_tok.equals("print") || 
				sig_tok.equals("input") || sig_tok.equals("return") || sig_tok.equals("function")) {
			parse.add(51);
			if(esOk(P()))
				return OK;
			return ERROR;
		}
		else if(sig_tok.equals("$")) {
			parse.add(52);
			return OK;
		}
		else {
			errorSintactico();
		}
		return ERROR;

	}

	public static void archivoParse() {
		try {
			FileWriter file= new FileWriter("Parse.txt");	
			file.write("Descendente ");
			for(Integer s : parse) {
				file.write(s + " ");
			}
			file.close();
		} catch (IOException e) {
			System.err.println(e);
			e.printStackTrace();
		}
	}

	public static void errorSintactico() throws IOException {
		if(!err) {
			System.err.println("ERROR SINTACTICO: el token: "+ sig_tok+ " no es el esperado en la linea "+ marco.nLinea);
			err=true;
		}
	}

}
