package cpl_parser_project_summer_2021.parser;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;


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
	
	public boolean hasNext() {
		throw new RuntimeException();
	}
	
	public Token getNextToken() {
		throw new RuntimeException();
	}
		
}
