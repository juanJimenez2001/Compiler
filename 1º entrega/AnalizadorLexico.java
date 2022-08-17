package code;

import java.util.HashMap;
import java.util.Map;

import Objetos.Reader;
import Objetos.Token;
import Objetos.Writter;

public class AnalizadorLexico {
	private char caracterActual;
	private String stringActual;
	private int numeroActual;
	private Reader fileReader;
	private Writter tokenWritter;
	private RegistraErrores errorLex;
	private Map<String, Integer> palabrasReservadas;
	private Map<String, Integer> simbolos;
	private TablaSimbolosControl control;

	public AnalizadorLexico(Reader file_reader, Writter token_Writter, TablaSimbolosControl stHandler,
			RegistraErrores errControl) {
		this.inicializaTabla();
		this.setFileReader(file_reader);
		this.tokenWritter = token_Writter;
		this.control = stHandler;
		this.errorLex = errControl;
		this.readChar();
	}

	public Token getToken() {
		// REVISADO
		Token token = new Token("NULL");
		this.stringActual = "";
		this.numeroActual = 0;
		if (!this.esDelimitador(this.caracterActual)) {
			// COMENTARIO "//" HASTA FINAL DE LINEA O "/*" HASTA "*/"
			if (this.caracterActual == '/') {
				this.readChar();
				if (this.caracterActual == '/') {
					this.readChar();
					while (this.caracterActual != '\n') {
						this.readChar();
					}
					return this.getToken();
				}
				if (this.caracterActual == '*') {
					while (this.caracterActual != '/') {
						while (this.caracterActual != '*') {
							this.readChar();
						}
						this.readChar();
					}
					this.readChar();
					return this.getToken();
				}
				this.errorLex.write(String.format("Linea %d: Analizador Lexico - Caracter(es) \"%s\" no esperado(s)",
						getFileReader().getCurrentLine(), this.caracterActual));
			}
			// NUMERO
			else if (this.esNumero(this.caracterActual)) {
				this.calculaValor();
				this.readChar();
				while (this.esNumero(this.caracterActual) && this.numeroActual <= 32767) {
					this.calculaValor();
					this.readChar();
				}
				if (this.numeroActual <= 32767) {
					return this.generaToken("NUM", this.numeroActual);
				}
				this.errorLex.write(String.format("Linea %d: Analizador Lexico - NUM ( %d ) fuera de rango",
						getFileReader().getCurrentLine(), this.numeroActual));
			}
			// CADENA
			else if (this.caracterActual == '\"') {
				this.readChar();
				while (this.caracterActual != '\"' && this.caracterActual != '\n' && this.caracterActual != '\0') {
					this.concatenaValor();
					this.readChar();
				}
				if (this.caracterActual == '\"') {
					this.readChar();
					return this.generaToken("CAD", this.stringActual);
				}
				this.errorLex.write(
						String.format("Linea %d: Analizador Lexico - CAD no valida", getFileReader().getCurrentLine()));
			}
			// VARIABLE
			else if (this.esLetra(this.caracterActual)) {
				this.concatenaValor();
				this.readChar();
				while (this.esLetra(this.caracterActual) || this.esNumero(this.caracterActual)
						|| this.caracterActual == '_') {
					this.concatenaValor();
					this.readChar();
				}
				Integer idPalRes = this.palabrasReservadas.get(this.stringActual);
				if (idPalRes != null) {
					return this.generaToken("PAL_RES", idPalRes);
				} else {
					EntradaTS entrada = this.control.buscarEntradaPorLexema(this.stringActual);

					if (entrada == null) {
						entrada = this.control.insertarEntrada(this.stringActual);
					}
					return this.generaToken("ID", entrada.getId());
				}
			}
			// OPERADORES
			else {
				if (this.caracterActual == '>') {
					this.concatenaValor();
					this.readChar();
					return this.generaToken("OPERADOR", this.simbolos.get(this.stringActual));
				}
				if (this.caracterActual == '=') {
					this.concatenaValor();
					this.readChar();
					return this.generaToken("OPERADOR", this.simbolos.get(this.stringActual));
				}
				if (this.caracterActual == '|') {
					this.concatenaValor();
					this.readChar();
					if (this.caracterActual == '|') {
						this.concatenaValor();
						this.readChar();
						return this.generaToken("OPERADOR", this.simbolos.get(this.stringActual));
					}
					this.errorLex.write(
							String.format("Linea %d: Analizador Lexico - Formato del operador OR_asignacion incorrecto",
									getFileReader().getCurrentLine()));
				}
				if (this.caracterActual == '%') {
					this.concatenaValor();
					this.readChar();
					if (this.caracterActual == '=') {
						this.concatenaValor();
						this.readChar();
						return this.generaToken("OPERADOR", this.simbolos.get(this.stringActual));
					} else {
						return this.generaToken("OPERADOR", this.simbolos.get(this.stringActual));
					}
				} else {
					if (this.esSimbolo(this.caracterActual)) {
						this.concatenaValor();
						this.readChar();
						return this.generaToken("CARACTER", this.stringActual);
					}
					this.errorLex.write(String.format("Linea %d: Analizador Lexico - Caracter ( %s ) no soportado",
							getFileReader().getCurrentLine(), this.caracterActual));
					this.readChar();
				}
			}
			return token;
		}
		if (this.caracterActual == '\t') {
			this.readChar();
		}
		if (this.caracterActual == '\n') {
			this.readChar();
			return this.generaToken("EOL", null);
		}
		if (this.caracterActual == '\0') {
			token = this.generaToken("EOF", null);
			this.getFileReader().close();
			return token;
		}
		this.readChar();
		return this.getToken();
	}

	// REVISADO
	private void readChar() {
		this.caracterActual = this.getFileReader().read();
	}

	// REVISADO
	private void concatenaValor() {
		this.stringActual = String.valueOf(this.stringActual) + this.caracterActual;
	}

	// REVISADO
	private void calculaValor() {
		this.concatenaValor();
		this.numeroActual = this.numeroActual * 10 + (this.caracterActual - '0');
	}

	// REVISADO
	private Token generaToken(String id, Object attr) {
		Token token = new Token(id, attr);
		if (id.equals("CAD")) {
			token.setValue(this.stringActual);
		}
		if (id.equals("NUM")) {
			token.setValue(this.numeroActual);
		}
		this.tokenWritter.write(token.toString());
		return token;
	}

	// REVISADO
	private boolean esDelimitador(char c) {
		if (c == ' ' || c == '\r' || c == '\n' || c == '\0') {
			return true;
		}
		return false;
	}

	// REVISADO
	private boolean esNumero(char c) {
		if (c >= '0' && c <= '9') {
			return true;
		}
		return false;
	}

	// REVISADO
	private boolean esLetra(char c) {
		if ((c >= 'A' && c <= 'Z') || (c >= 'a' && c <= 'z')) {
			return true;
		}
		return false;
	}

	// REVISADO
	private boolean esSimbolo(char c) {
		if (c == '(' || c == ')' || c == ',' || c == '.' || c == '{' || c == '}') {
			return true;
		}
		return false;
	}

	// REVISADO
	private void inicializaTabla() {
		this.palabrasReservadas = new HashMap<String, Integer>();
		this.palabrasReservadas.put("var", 0);
		this.palabrasReservadas.put("int", 1);
		this.palabrasReservadas.put("chars", 2);
		this.palabrasReservadas.put("bool", 3);
		this.palabrasReservadas.put("while", 4);
		this.palabrasReservadas.put("function", 5);
		this.palabrasReservadas.put("return", 6);
		this.palabrasReservadas.put("true", 7);
		this.palabrasReservadas.put("false", 8);
		this.palabrasReservadas.put("write", 9);
		this.palabrasReservadas.put("prompt", 10);
		this.simbolos = new HashMap<String, Integer>();
		this.simbolos.put("%", 0);
		this.simbolos.put("=", 1);
		this.simbolos.put("%=", 2);
		this.simbolos.put(">", 3);
		this.simbolos.put("||", 4);
	}

	// REVISADO
	public Reader getFileReader() {
		return fileReader;
	}

	// REVISADO
	public void setFileReader(Reader fileReader) {
		this.fileReader = fileReader;
	}

	// REVISADO
	public void write(String s) {
		this.tokenWritter.write(s);
	}
}
