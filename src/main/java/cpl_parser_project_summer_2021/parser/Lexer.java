package cpl_parser_project_summer_2021.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static cpl_parser_project_summer_2021.parser.Token.Type.*;
import static java.lang.Character.toUpperCase;

public class Lexer {
	
	private static Map<String, Token> keywords = Map.ofEntries(
		keyword(CLOSE),
		keyword(DATA),
		keyword(DIM),
		keyword(END),
		keyword(FOR),
		keyword(GOTO),
		keyword(GOSUB),
		keyword(IF),
		keyword(ELSE),
		keyword(INPUT),
		keyword(LET),
		keyword(NEXT),
		keyword(OPEN),
		keyword(POKE),
		keyword(PRINT),
		keyword(READ),
		keyword(RETURN),
		keyword(RESTORE),
		keyword(RUN),
		keyword(STOP),
		keyword(SYS),
		keyword(WAIT),
		keyword(OUTPUT),
		keyword(TO),
		keyword(STEP),
		keyword(THEN),
		keyword(AS),
		keyword(AND),
		keyword(OR),
		keyword(NOT)
	);
	
	private static Map.Entry<String, Token> keyword(Token.Type type) {
		return Map.entry(type.name(), new Token(type.name(), type));
	}
	
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
				builder.append(toUpperCase(current));
				nextChar();
			}
			if (!eof && (current == '$' || current == '%')) {
				builder.append(current);
				nextChar();
			}
			var value = builder.toString();
			var keyword = keywords.get(value);
			if (keyword != null) {
				return keyword;
			} else if (REM.name().equals(value)) {
				while (!eof && current != '\r' && current != '\n') nextChar();
				return new Token(null, REM);
			} else {
				return new Token(value, ID);
			}
		} else if (current == '"') {
			var builder = new StringBuilder();
			nextChar(); // current is '"'
			while (current != '"') {
				builder.append(current);
				nextChar();
				if (eof || current == '\r' || current == '\n') throw new LexerException("Unterminated String Literal");
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
		} else if (current == '\r' || current == '\n') {
			var first = current;
			nextChar();
			if (!eof && first == '\r' && current == '\n') {
				nextChar();
				return new Token("\r\n", NEWLINE);
			} else {
				return new Token(String.valueOf(first), NEWLINE);
			}
		} else {
			var advance = true;
			var token = switch (current) {
				case ':' -> new Token(":", COLON);
				case '#' -> new Token("#", HASH);
				case '(' -> new Token("(", LPAREN);
				case ')' -> new Token(")", RPAREN);
				case ',' -> new Token(",", COMMA);
				case ';' -> new Token(";", SEMI);
				case '=' -> new Token("=", EQUALS);
				case '<' -> {
					nextChar();
					if (!eof) {
						if (current == '=') {
							yield new Token("<=", LTE);
						} else if (current == '>') {
							yield new Token("<>", DIAMOND);
						}
					}
					advance = false;
					yield new Token("<", LT);
				}
				case '>' -> {
					nextChar();
					if (!eof && current == '=') {
						yield new Token(">=", GTE);
					}
					advance = false;
					yield new Token(">", GT);
				}
				case '+' -> new Token("+", PLUS);
				case '-' -> new Token("-", MINUS);
				case '*' -> new Token("*", TIMES);
				case '/' -> new Token("/", DIVIDE);
				case '^' -> new Token("^", POWER);
				default -> throw new LexerException("Unexpected character '" + current + "'");
			};
			if (advance) nextChar();
			return token;
		}
	}
		
}

