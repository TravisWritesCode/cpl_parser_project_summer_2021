package cpl_parser_project_summer_2021.parser;

import java.io.IOException;
import java.security.Key;

import static cpl_parser_project_summer_2021.parser.Token.Type.*;

public class Parser {

    private Lexer lexer;
    private Token current;

    public Parser(Lexer lexer) throws IOException, LexerException {
        this.lexer = lexer;
        nextToken();
    }

    private void nextToken() throws IOException, LexerException {
        current = lexer.getNextToken();
    }

    private Token eat(Token.Type type) throws IOException, LexerException, ParserException {
        if (current.type() != type) throw new ParserException("Expected " + type);
        var token = current;
        nextToken();
        return token;
    }

    private Token eatKeyword(String word) throws IOException, LexerException, ParserException {
        if (current.type() != KEYWORD && !current.lexeme().equals(word)) throw new ParserException("Expected " + word);
        var token = current;
        nextToken();
        return token;
    }

    public void parseStatement() throws IOException, LexerException, ParserException {
        if (current.type() == KEYWORD) {
            switch (current.lexeme()) {
                case "DATA" -> {
                    nextToken();
                    parseConstantList();
                }
                // TODO: DEF
                case "DIM" -> {
                    nextToken();
                    eat(ID);
                    eat(LPAREN);
                    parseNumberList();
                    eat(RPAREN);
                }
                case "END" -> {
                    nextToken();
                }
                case "FOR" -> {
                    nextToken();
                    eat(ID);
                    eat(EQUALS);
                    parseExpression();
                    eatKeyword("TO");
                    parseExpression();
                    if (current.type() == KEYWORD && "STEP".equals(current.lexeme())) {
                        nextToken();
                        parseConstant();
                    }
                }
                case "GOTO" -> {
                    nextToken();
                    parseExpression();
                }
                case "GOSUB" -> {
                    nextToken();
                    parseExpression();
                }
                case "IF" -> {
                    nextToken();
                    parseExpression();
                    eatKeyword("THEN");
                    parseStatement();
                    eat(INTEGER);
                }
                // TODO: INPUT
                case "LET" -> {
                    nextToken();
                    eat(ID);
                    eat(EQUALS);
                    parseExpression();
                }
                case "NEXT" -> {
                    nextToken();
                    parseIdentifierList();
                }
                case "PRINT" -> {
                    nextToken();
                    parsePrintList();
                }
                case "READ" -> {
                    nextToken();
                    parseIdentifierList();
                }
                case "RETURN" -> {
                    nextToken();
                }
                case "STOP" -> {
                    nextToken();
                }
                default -> parseFunctionExpression();
            }
        } else {
            if (current.type() == REM) nextToken();
        }
    }

    private void parseFunctionExpression() {
    }

    private void parseIdentifierList() {
    }

    private void parseConstantList() {
    }

    private void parseNumberList() {
    }

    private void parsePrintList() {
    }

    private void parseExpression() throws IOException, LexerException, ParserException {
        parseAndExpression();
        if (current.type() == KEYWORD && "OR".equals(current.lexeme())) {
            nextToken();
            parseExpression();
        }
    }

    private void parseAndExpression() throws IOException, LexerException, ParserException {
        parseNotExpression();
        if (current.type() == KEYWORD && "AND".equals(current.lexeme())) {
            nextToken();
            parseAndExpression();
        }
    }

    private void parseNotExpression() throws IOException, LexerException, ParserException {
        if (current.type() == KEYWORD && "NOT".equals(current.lexeme())) {
            nextToken();
            parseNotExpression();
        } else {
            parseCompareExpression();
        }
    }

    private void parseCompareExpression() throws IOException, LexerException, ParserException {
        parseAdditionExpression();
        switch (current.type()) {
            case EQUALS -> {}
            case DIAMOND -> {}
            case LT -> {}
            case GT -> {}
            case LTE -> {}
            case GTE -> {}
            default -> {
                return;
            }
        }
        nextToken();
        parseCompareExpression();
    }

    private void parseAdditionExpression() throws IOException, LexerException, ParserException {
        parseMultiplicationExpression();
        switch (current.type()) {
            case PLUS -> {}
            case MINUS -> {}
            default -> {
                return;
            }
        }
        nextToken();
        parseAdditionExpression();
    }

    private void parseMultiplicationExpression() throws IOException, LexerException, ParserException {
        parseNegateExpression();
        switch (current.type()) {
            case TIMES -> {}
            case DIVIDE -> {}
            default -> {
                return;
            }
        }
        nextToken();
        parseMultiplicationExpression();
    }

    private void parseNegateExpression() throws IOException, LexerException, ParserException {
        if (current.type() == MINUS) {
            nextToken();
            parseNegateExpression();
        } else {
            parseValue();
        }
    }

    private void parseValue() throws IOException, LexerException, ParserException {
        switch (current.type()) {
            case LPAREN -> {
                nextToken();
                parseExpression();
                eat(RPAREN);
            }
            case ID -> {
                nextToken();
            }
            case STRING, INTEGER, REAL -> {
                parseConstant();
            }
            default -> {
                parseFunctionExpression();
            }
        }
    }

    private void parseConstant() {

    }

}
