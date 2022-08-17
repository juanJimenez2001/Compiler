package code;

import java.util.HashMap;
import java.util.Map;

class TablaSimbolos  {

	private Map<String, EntradaTS> tabla;
	public int pos;	
	public String funcion = "";	
	private int index;


	public TablaSimbolos(int index){
		this.index=index;
		this.tabla = new HashMap<String, EntradaTS>();
		this.pos=0;
	}

	public int crearEntrada(String lexema) {
		int aux;
		if(tabla.containsKey(lexema)) {
			aux=tabla.get(lexema).getPos();
		}
		else {
			aux=pos;
			EntradaTS entrada=new EntradaTS(lexema, pos);
			tabla.put(lexema, entrada);
		}
		return aux;
	}

	public void setEntrada(String lex, EntradaTS e) {
		tabla.put(lex, e);
	}
	
	public void setPos(int i) {
		pos = i;
	}
	public int getPos() {
		return pos;
	}



	public void setTipo(String lexema, String tipo) {
		EntradaTS entrada = buscarPorLexema(lexema);
		entrada.setTipo(tipo);
		setEntrada(lexema, entrada);
	}

	public void setValorRetorno(String lexema, String valor) {
		EntradaTS entrada = buscarPorLexema(lexema);
		entrada.setTipo(valor);
		setEntrada(lexema,entrada);
	}

	public EntradaTS buscarPorLexema(String lexema){
		return tabla.get(lexema);
	}

	public String toString(){
		String table_string = "";
		if(index == 1) {
			table_string += String.format("TABLA PRINCIPAL #"+ index +":\n");
		}
		else {
			table_string+="\n";
			table_string += String.format("TABLA de la FUNCION " + funcion + " #"+index+":\n");
		}

		int cont = 1;	
		
		for (EntradaTS entrada : tabla.values()) {
			table_string += "\n* LEXEMA: '"+entrada.getLexema()+"'";

			String tipo = entrada.getTipo();
			table_string += "\n  +Tipo: '" + tipo+"'";

			if(tipo.equals("funcion")) {
				int numParam = entrada.getNumParam();
				table_string += "\n  	+numParam: " + numParam;

				for(int i = 0 ; i < numParam ; i++) {
					table_string += "\n  	  +TipoParam" + "0" + (i + 1) + ": '" + entrada.getParam(i)+"'";
				}
				table_string += "\n  	+TipoRetorno: '" + entrada.getTipoRetorno()+"'";
				table_string += "\n  +EtiqFuncion: 'Et" + entrada.getLexema() +"0" + cont +"'"; 
			cont++;
			}
			else {
				table_string += "\n  +Despl: " + entrada.getPos();
			}

		}
		return table_string;
	}

}