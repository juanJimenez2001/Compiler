package code;

import java.io.File;

public class principal {
    public static void main(String[] args) {
    	System.getProperty("java.classpath");
        String path = "D:\\Documentos\\Universidad\\3ºcarrera\\PDL\\java\\Procesador de Lenguajes\\src\\code\\lexer.flex";
        generarLexer(path);
    }
    public static void generarLexer(String path){
        File archivo = new File(path);
        JFlex.Main.generate(archivo);
    }
}
