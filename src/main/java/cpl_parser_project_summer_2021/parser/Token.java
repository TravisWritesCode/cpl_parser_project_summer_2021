/*
 * Class:       CS 4308 Section 1
 * Term:        Fall 2019
 * Name:        Nicholas Luchuk, Travis Hescox, Yash Shidhaye,
 * Instructor:   Deepa Muralidhar
 * Project:  Deliverable 1 Scanner - Java
 */


package cpl_parser_project_summer_2021.parser;

public record Token(String lexeme, Type type) {
  
  public enum Type {
    
    ID,
    STRING,
    REAL,
    INTEGER,
    
    REM,
    NEWLINE,
    
    // keywords
    CLOSE,
    DATA,
    DIM,
    END,
    FOR,
    GOTO,
    GOSUB,
    IF,
    ELSE,
    INPUT,
    LET,
    NEXT,
    OPEN,
    POKE,
    PRINT,
    READ,
    RETURN,
    RESTORE,
    RUN,
    STOP,
    SYS,
    WAIT,
    OUTPUT,
    TO,
    STEP,
    THEN,
    AS,
    AND,
    OR,
    NOT,
    
    COLON,
    HASH,
    LPAREN,
    RPAREN,
    EQUALS,
    COMMA,
    SEMI,
    DIAMOND,
    GT,
    GTE,
    LT,
    LTE,
    PLUS,
    MINUS,
    TIMES,
    DIVIDE,
    POWER,
    
    EOF
    
  }
  
}
