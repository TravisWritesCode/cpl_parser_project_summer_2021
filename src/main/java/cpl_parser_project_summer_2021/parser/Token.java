package cpl_parser_project_summer_2021.parser;

public record Token(String lexeme, Type type) {
  
  public enum Type {
    
    ID,
    EOF
    
  }
  
}
