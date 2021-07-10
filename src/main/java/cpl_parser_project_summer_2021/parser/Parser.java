package cpl_parser_project_summer_2021.parser;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import static cpl_parser_project_summer_2021.parser.ParseTree.*;
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

    public LinesNode parseLines() throws IOException, LexerException, ParserException {
        var firstNumber = new NumberNode(Double.parseDouble(eat(NUMBER).lexeme()));
        var firstStatements = parseStatements();
        eat(NEWLINE);
        var first = new LineNode(firstNumber, firstStatements);
        if (current.type() != EOF) {
            var list = new ArrayList<LineNode>();
            list.add(first);
            var rest = parseLines();
            list.addAll(rest.lines());
            return new LinesNode(list);
        }
        return new LinesNode(List.of(first));
    }

    public List<StatementTree> parseStatements() throws IOException, LexerException, ParserException {
        var first = parseStatement();
        switch (current.type()){
            case COMMA, SEMI, COLON -> {
                parseDivider();
                var list = new ArrayList<StatementTree>();
                list.add(first);
                var rest = parseStatements();
                list.addAll(rest);
                return list;
            }
            default -> {
                return List.of(first);
            }
        }
    }

    public StatementTree parseStatement() throws IOException, LexerException, ParserException {
        if (current.type() == KEYWORD) {
            switch (current.lexeme()) {
                case "DATA" -> {
                    nextToken();
                    var data = parseConstantList();
                    return new DataNode(data);
                }
                case "DIM" -> {
                    nextToken();
                    var name = eat(ID).lexeme();
                    eat(LPAREN);
                    var lengths = parseNumberList();
                    eat(RPAREN);
                    return new DimNode(name, lengths);
                }
                case "END" -> {
                    nextToken();
                    return new EndNode();
                }
                case "FOR" -> {
                    nextToken();
                    var var = eat(ID).lexeme();
                    eat(EQUALS);
                    var from = parseExpression();
                    eatKeyword("TO");
                    var to = parseExpression();
                    ConstantTree step = null;
                    if (current.type() == KEYWORD && "STEP".equals(current.lexeme())) {
                        nextToken();
                        step = parseConstant();
                    }
                    return new ForNode(var, from, to, step);
                }
                case "GOTO" -> {
                    nextToken();
                    var destination = parseExpression();
                    return new GotoNode(destination);
                }
                case "GOSUB" -> {
                    nextToken();
                    var destination = parseExpression();
                    return new GosubNode(destination);
                }
                case "IF" -> {
                    nextToken();
                    var condition = parseExpression();
                    eatKeyword("THEN");
                    var destination = new NumberNode(Double.parseDouble(eat(NUMBER).lexeme()));
                    return new IfNode(condition, destination);
                }
                case "INPUT" -> {
                    nextToken();
                    String prompt = null;
                    if (current.type() == STRING) {
                        prompt = current.lexeme();
                        nextToken();
                        parseDivider();
                    }
                    var vars = parseIdentifierList();
                    return new InputNode(prompt, vars);
                }
                case "LET" -> {
                    nextToken();
                    var name = eat(ID).lexeme();
                    eat(EQUALS);
                    var value = parseExpression();
                    return new LetNode(name, value);
                }
                case "NEXT" -> {
                    nextToken();
                    var vars = parseIdentifierList();
                    return new NextNode(vars);
                }
                case "PRINT" -> {
                    nextToken();
                    var values = parsePrintList();
                    return new PrintNode(values);
                }
                case "READ" -> {
                    nextToken();
                    var vars = parseIdentifierList();
                    return new ReadNode(vars);
                }
                case "RETURN" -> {
                    nextToken();
                    return new ReturnNode();
                }
                case "STOP" -> {
                    nextToken();
                    return new StopNode();
                }
                default -> {
                    return parseFunctionExpression();
                }
            }
        } else {
            if (current.type() == REM) nextToken();
            return new BlankStatementNode();
        }
    }

    private FunctionExpressionNode parseFunctionExpression() throws IOException, LexerException, ParserException {
        if(current.type() == KEYWORD) {
            switch (current.lexeme()) {
                case "ABS" -> {
                    nextToken();
                    eat(LPAREN);
                    var argument = parseAdditionExpression();
                    eat(RPAREN);
                    return new FunctionExpressionNode(FunctionName.ABS, argument);
                }
                case "ATN" -> {
                    nextToken();
                    eat(LPAREN);
                    var argument = parseAdditionExpression();
                    eat(RPAREN);
                    return new FunctionExpressionNode(FunctionName.ATN, argument);
                }
                case "COS" -> {
                    nextToken();
                    eat(LPAREN);
                    var argument = parseAdditionExpression();
                    eat(RPAREN);
                    return new FunctionExpressionNode(FunctionName.COS, argument);
                }
                case "EXP" -> {
                    nextToken();
                    eat(LPAREN);
                    var argument = parseAdditionExpression();
                    eat(RPAREN);
                    return new FunctionExpressionNode(FunctionName.EXP, argument);
                }
                case "INT" -> {
                    nextToken();
                    eat(LPAREN);
                    var argument = parseAdditionExpression();
                    eat(RPAREN);
                    return new FunctionExpressionNode(FunctionName.INT, argument);
                }
                case "LOG" -> {
                    nextToken();
                    eat(LPAREN);
                    var argument = parseAdditionExpression();
                    eat(RPAREN);
                    return new FunctionExpressionNode(FunctionName.LOG, argument);
                }
                case "RND" -> {
                    nextToken();
                    eat(LPAREN);
                    var argument = new NumberNode(Double.parseDouble(eat(NUMBER).lexeme()));
                    eat(RPAREN);
                    return new FunctionExpressionNode(FunctionName.RND, argument);
                }
                case "SIN" -> {
                    nextToken();
                    eat(LPAREN);
                    var argument = parseAdditionExpression();
                    eat(RPAREN);
                    return new FunctionExpressionNode(FunctionName.SIN, argument);
                }
                case "SQR" -> {
                    nextToken();
                    eat(LPAREN);
                    var argument = parseAdditionExpression();
                    eat(RPAREN);
                    return new FunctionExpressionNode(FunctionName.SQR, argument);
                }
                case "TAN" -> {
                    nextToken();
                    eat(LPAREN);
                    var argument = parseAdditionExpression();
                    eat(RPAREN);
                    return new FunctionExpressionNode(FunctionName.TAN, argument);
                }
            }
        }
        throw new ParserException("Expected identifier");
    }

    private List<String> parseIdentifierList() throws IOException, LexerException, ParserException {
        var first = eat(ID).lexeme();
        switch (current.type()){
            case COMMA, SEMI, COLON -> {
                parseDivider();
                var list = new ArrayList<String>();
                list.add(first);
                var rest = parseIdentifierList();
                list.addAll(rest);
                return list;
            }
            default -> {
                return List.of(first);
            }
        }
    }

    private List<ExpressionTree> parseValueList() throws IOException, LexerException, ParserException {
        var first = parseValue();
        switch (current.type()){
            case COMMA, SEMI, COLON -> {
                parseDivider();
                var list = new ArrayList<ExpressionTree>();
                list.add(first);
                var rest = parseValueList();
                list.addAll(rest);
                return list;
            }
            default -> {
                return List.of(first);
            }
        }
    }

    private List<ConstantTree> parseConstantList() throws IOException, LexerException, ParserException {
        var first = parseConstant();
        switch (current.type()){
            case COMMA, SEMI, COLON -> {
                parseDivider();
                var list = new ArrayList<ConstantTree>();
                list.add(first);
                var rest = parseConstantList();
                list.addAll(rest);
                return list;
            }
            default -> {
                return List.of(first);
            }
        }
    }

    private List<NumberNode> parseNumberList() throws IOException, LexerException, ParserException {
        var first = new NumberNode(Double.parseDouble(eat(NUMBER).lexeme()));
        switch (current.type()){
            case COMMA, SEMI, COLON -> {
                parseDivider();
                var list = new ArrayList<NumberNode>();
                list.add(first);
                var rest = parseNumberList();
                list.addAll(rest);
                return list;
            }
            default -> {
                return List.of(first);
            }
        }
    }

    private List<ExpressionTree> parsePrintList() throws IOException, LexerException, ParserException{
        switch (current.type()) {
            case COMMA, SEMI, COLON, NEWLINE -> {
                return List.of();
            }
        }
        var first = parseExpression();
        switch (current.type()){
            case COMMA, SEMI, COLON -> {
                parseDivider();
                var list = new ArrayList<ExpressionTree>();
                list.add(first);
                var rest = parsePrintList();
                list.addAll(rest);
                return list;
            }
            default -> {
                return List.of(first);
            }
        }
    }

    private ExpressionTree parseExpression() throws IOException, LexerException, ParserException {
        var left = parseAndExpression();
        if (current.type() == KEYWORD && "OR".equals(current.lexeme())) {
            nextToken();
            var right = parseExpression();
            return new BinaryExpressionNode(BinaryOperation.OR, left, right);
        }
        return left;
    }

    private ExpressionTree parseAndExpression() throws IOException, LexerException, ParserException {
        var left = parseNotExpression();
        if (current.type() == KEYWORD && "AND".equals(current.lexeme())) {
            nextToken();
            var right = parseAndExpression();
            return new BinaryExpressionNode(BinaryOperation.AND, left, right);
        }
        return left;
    }

    private ExpressionTree parseNotExpression() throws IOException, LexerException, ParserException {
        if (current.type() == KEYWORD && "NOT".equals(current.lexeme())) {
            nextToken();
            var operand = parseNotExpression();
            return new UnaryExpressionNode(UnaryOperation.NOT, operand);
        } else {
            return parseCompareExpression();
        }
    }

    private ExpressionTree parseCompareExpression() throws IOException, LexerException, ParserException {
        var left = parseAdditionExpression();
        var operation = switch (current.type()) {
            case EQUALS -> BinaryOperation.EQUALS;
            case DIAMOND -> BinaryOperation.DIAMOND;
            case LT -> BinaryOperation.LT;
            case GT -> BinaryOperation.GT;
            case LTE -> BinaryOperation.LTE;
            case GTE -> BinaryOperation.GTE;
            default -> null;
        };
        if (operation == null) return left;
        nextToken();
        var right = parseCompareExpression();
        return new BinaryExpressionNode(operation, left, right);
    }

    private ExpressionTree parseAdditionExpression() throws IOException, LexerException, ParserException {
        var left = parseMultiplicationExpression();
        var operation = switch (current.type()) {
            case PLUS -> BinaryOperation.ADD;
            case MINUS -> BinaryOperation.SUBTRACT;
            default -> null;
        };
        if (operation == null) return left;
        nextToken();
        var right = parseAdditionExpression();
        return new BinaryExpressionNode(operation, left, right);
    }

    private ExpressionTree parseMultiplicationExpression() throws IOException, LexerException, ParserException {
        var left = parseNegateExpression();
        var operation = switch (current.type()) {
            case TIMES -> BinaryOperation.MULTIPLY;
            case DIVIDE -> BinaryOperation.DIVIDE;
            default -> null;
        };
        if (operation == null) return left;
        nextToken();
        var right = parseMultiplicationExpression();
        return new BinaryExpressionNode(operation, left, right);
    }

    private ExpressionTree parseNegateExpression() throws IOException, LexerException, ParserException {
        if (current.type() == MINUS) {
            nextToken();
            var operand = parseNegateExpression();
            return new UnaryExpressionNode(UnaryOperation.NEGATE, operand);
        } else {
            return parseValue();
        }
    }

    private ExpressionTree parseValue() throws IOException, LexerException, ParserException {
        switch (current.type()) {
            case LPAREN -> {
                nextToken();
                var node = parseExpression();
                eat(RPAREN);
                return node;
            }
            case ID -> {
                var name = current.lexeme();
                nextToken();
                return new VarNode(name);
            }
            case STRING, NUMBER -> {
                return parseConstant();
            }
            default -> {
                return parseFunctionExpression();
            }
        }
    }

    private ConstantTree parseConstant() throws IOException, LexerException, ParserException {
        if(current.type() == NUMBER) {
            var value = Double.parseDouble(current.lexeme());
            nextToken();
            return new NumberNode(value);
        } else if (current.type() == STRING) {
            var value = current.lexeme();
            nextToken();
            return new StringNode(value);
        } else {
            throw new ParserException("Expected constant");
        }
    }

    private void parseDivider() throws IOException, LexerException, ParserException{
        switch (current.type()){
            case COMMA, SEMI, COLON -> nextToken();
            default -> throw new ParserException("Expected divider");
        }

    }

}
