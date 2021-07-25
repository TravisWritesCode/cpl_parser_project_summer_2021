/*
 * Class:       CS 4308 Section 1
 * Term:        Fall 2019
 * Name:        Nicholas Luchuk, Travis Hescox, Yash Shidhaye,
 * Instructor:   Deepa Muralidhar
 * Project:  Deliverable 2 Parser - Java
 */


package cpl_parser_project_summer_2021.parser;
import hu.webarticum.treeprinter.ListingTreePrinter;

import java.io.*;
import java.util.*;


public class App {
    public static void main( String[] args ) {
    	try {
    		var file = new FileInputStream("sample_program_2.txt");
    		Lexer lex = new Lexer(file, getKeywords());
			Parser parser = new Parser(lex);
			var parseTree = parser.parseLines();
			//System.out.println(parseTree.toString().replace("[", "[\n").replace("]", "\n]"));
			Instruction.execute(parseTree.compile());
    	} 
    	catch (Exception e) {
    	      System.out.println("An error occurred.");
    	      e.printStackTrace();
    	}
    }

	private static Set<String> getKeywords() throws IOException {
    var keywords = new HashSet<String>();
    var file = new File("keywords.txt");
		Scanner sc = new Scanner(file);
		while(sc.hasNextLine()) {
			keywords.add(sc.nextLine());
		}
		return keywords;
  }
}
