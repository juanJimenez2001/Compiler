package code;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class principal {
	public static void main(String[] args) throws IOException {
		/*System.getProperty("java.classpath");
		String path = "C:\\Users\\jdhp2\\eclipse-workspace\\PDL\\src\\code\\lexer.flex";

		generarLexer(path);*/

		AnSemantico as = new AnSemantico();
		as.A_Sint();
		as.archivoParse();		
		System.out.println(as.marco.getTS().toString());

		for(TablaSimbolos ts : as.marco.getEliminadas()) {
			System.out.println(ts.toString());
		}

	}
	public static void generarLexer(String path){
		File archivo = new File(path);
		JFlex.Main.generate(archivo);
	}
}
