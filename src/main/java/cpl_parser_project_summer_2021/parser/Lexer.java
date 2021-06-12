package cpl_parser_project_summer_2021.parser;
import java.util.*;


public class Lexer{
	private Scanner sc = null;
	
	public Lexer(Scanner input) throws Exception {
		try {
			sc = input;
			sc.useDelimiter(" ");
		} catch (Exception e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}
	}
	
	public boolean hasNext() {
		return sc.hasNext();
	}
	
	public String getNextToken() {
		return sc.next();
	}
		
}
