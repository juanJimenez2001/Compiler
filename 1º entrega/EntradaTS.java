package code;

class EntradaTS {
	protected static Integer ENTRADA_ID = 0;

	private int pos;
	private String lexema;


	public EntradaTS(String lexema, int pos){
		this.lexema = lexema;
		this.pos = pos;	
	}

	public int getPos() {
		return pos;
	}
	
	public String getLexema() {
		return lexema;
	}

	public String toString(){
		return String.format("* LEXEMA: '%s'%n   ATRIBUTOS:%n", this.lexema); 
	}
	
}
