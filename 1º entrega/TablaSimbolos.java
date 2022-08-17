package code;

import java.util.HashMap;
import java.util.Map;

class TablaSimbolos  {
	
	private Map<String, EntradaTS> tabla;
	private int pos;										

	public TablaSimbolos(){
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
			pos++;
		}
		return aux;
	}
	
	public String toString(){
		String table_string = String.format("CONTENIDOS DE LA TABLA #0 : \n");
		for (EntradaTS entrada : tabla.values()) {
			table_string += "\n* lexema: '"+entrada.getLexema()+"'";
			table_string += "\n  Atributos:";
			table_string += "\n--------- ----------";
		}
		return table_string;
	}
	
}