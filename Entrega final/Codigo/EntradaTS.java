package code;

import java.util.ArrayList;

class EntradaTS {
	protected static Integer ENTRADA_ID = 0;
	private String tipo;
	private int desp;
	private String[] tipoParamXX;
	public ArrayList<String> llamadas;

	private String tipoRetorno;
	private int numParam;
	private int pos;
	private String lexema;

	private int index;

	public EntradaTS(String lexema, int pos){
		this.lexema = lexema;
		this.pos = pos;	
		this.tipo = "int";
		this.tipoRetorno = "void";
		this.desp = 0;
		this.index = 0;

	}

	public void setNumParam(int i ) {
		numParam = i;
		tipoParamXX = new String[numParam];
	}

	public void addParam(String p) {
		tipoParamXX[index] = p;
		index++;
	}

	public void setTipoParamXX(String[] s) {
		tipoParamXX = new String[s.length];	
		for(int i = 0 ; i < s.length ; i++) {
			tipoParamXX[i] = s[i];
		}

	}

	public int getNumParam() {
		return numParam;
	}

	public String[] getTipoParamXX() {
		return tipoParamXX;
	}

	public String getParam(int i) {
		return tipoParamXX[i];
	}

	public int getPos() {
		return pos;
	}

	public int getDesp() {
		return desp;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String t) {
		tipo = t;
	}


	public String getTipoRetorno() {
		return tipoRetorno;
	}

	public void setTipoRetorno(String t) {
		tipoRetorno = t;
	}

	public String getLexema() {
		return lexema;
	}

	public String toString(){
		return String.format("* LEXEMA: '%s'%n   ATRIBUTOS:%n", this.lexema); 
	}

}
