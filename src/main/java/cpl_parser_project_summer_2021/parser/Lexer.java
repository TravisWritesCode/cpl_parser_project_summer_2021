package cpl_parser_project_summer_2021.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static cpl_parser_project_summer_2021.parser.Token.Type.*;

public class Lexer {
	
	private InputStream input;
	private boolean eof = false;
	
	public Lexer(InputStream input) {
		this.input = input;
	}
	
	private char nextChar() throws IOException {
		int c = input.read();
		if (c == -1) eof = true;
		return (char) c;
	}
	
	public Token getNextToken() throws IOException, LexerException {
		char current;
		while (eof || (current = nextChar()) == ' ' || current == '\t');
		if (eof) {
			return new Token(null, EOF);
		} if ((current >= 'A' && current <= 'Z') || (current >= 'a' && current <= 'z')) {
			var builder = new StringBuilder();
			builder.append(current);
			while (!eof && ((current = nextChar()) >= 'A' && current <= 'Z') || (current >= 'a' && current <= 'z')) {
				builder.append(current);
			}
			if (current == '$' || current == '%') {
				builder.append(current);
			}
			return new Token(builder.toString(), ID);
		} else if (current == '"') {
			var builder = new StringBuilder();
			while ((current = nextChar()) != '"') {
				if (eof) throw new LexerException("Unterminated String Literal");
				builder.append(current);
			}
			return new Token(builder.toString(), STRING);
		} else if (current >= '0' && current <= '9') {
			var builder = new StringBuilder();
			var decimal = false;
			builder.append(current);
			while (!eof && ((current = nextChar()) >= '0' && current <= '9' || current == '.' && !decimal)) {
				if (current == '.') decimal = true;
				builder.append(current);
			}
			return new Token(builder.toString(), decimal ? REAL : INTEGER);
		} else {
			throw new LexerException("Unexepcted character '" + current + "'");
		}
	}
		
}

