package cpl_parser_project_summer_2021.parser;
import java.io.*;
import java.util.*;

public class App {
    public static void main( String[] args ) throws Exception {
    	try {
    		var file = new FileInputStream("sample_program.txt");
    		Lexer lex = new Lexer(file);
    		Token token = null;
    		while (lex.hasNext()) {
    			token = lex.getNextToken();
    			System.out.println(token);
    		}
    	} 
    	catch (Exception e) {
    	      System.out.println("An error occurred.");
    	      e.printStackTrace();
    	}
    }
}
