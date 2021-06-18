package cpl_parser_project_summer_2021.parser;

import java.io.IOException;
import java.io.InputStream;

import static cpl_parser_project_summer_2021.parser.Token.Type.*;

public class Lexer {
	
	private InputStream input;
	private char current;
	private boolean eof = false;
	
	public Lexer(InputStream input) throws IOException {
		this.input = input;
		nextChar();
	}
	
	private void nextChar() throws IOException {
		var c = input.read();
		if (c == -1) eof = true;
		current = (char) c;
	}
	
	public Token getNextToken() throws IOException, LexerException {
		while (!eof && (current == ' ' || current == '\t')) nextChar();
		if (eof) {
			return new Token(null, EOF);
		} else if ((current >= 'A' && current <= 'Z') || (current >= 'a' && current <= 'z')) {
			var builder = new StringBuilder();
			while (!eof && (current >= 'A' && current <= 'Z' || current >= 'a' && current <= 'z')) {
				builder.append(current);
				nextChar();
			}
			if (!eof && (current == '$' || current == '%')) {
				builder.append(current);
				nextChar();
			}
			// TODO: check for keyword here
			return new Token(builder.toString(), ID);
		} else if (current == '"') {
			var builder = new StringBuilder();
			nextChar(); // current is '"'
			while (current != '"') {
				builder.append(current);
				nextChar();
				if (eof) throw new LexerException("Unterminated String Literal");
			}
			nextChar(); // current is '"'
			return new Token(builder.toString(), STRING);
		} else if (current >= '0' && current <= '9') {
			var builder = new StringBuilder();
			var decimal = false;
			while (!eof && (current >= '0' && current <= '9' || current == '.' && !decimal)) {
				if (current == '.') decimal = true;
				builder.append(current);
				nextChar();
			}
			return new Token(builder.toString(), decimal ? REAL : INTEGER);
		} else {
			throw new LexerException("Unexpected character '" + current + "'");
		}
	}
		
}

