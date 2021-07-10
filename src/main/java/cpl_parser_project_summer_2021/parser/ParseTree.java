package cpl_parser_project_summer_2021.parser;

import java.util.List;

import hu.webarticum.treeprinter.TreeNode;

public interface ParseTree {

    TreeNode makeTree();

    public interface StatementTree extends ParseTree {}

    public interface ExpressionTree extends ParseTree {}

    public interface ConstantTree extends ExpressionTree {}

    public record LinesNode(List<LineNode> lines) implements ParseTree {}

    public record LineNode(NumberNode number, List<StatementTree> statements) implements ParseTree {}

    public record DataNode(List<ConstantTree> data) implements StatementTree {}

    public record DimNode(String name, List<NumberNode> lengths) implements StatementTree {}

    public record EndNode() implements StatementTree {}

    public record ForNode(String var, ExpressionTree from, ExpressionTree to, ConstantTree step) implements StatementTree {}

    public record GotoNode(ExpressionTree destination) implements StatementTree {}

    public record GosubNode(ExpressionTree destination) implements StatementTree {}

    public record IfNode(ExpressionTree condition, NumberNode destination) implements StatementTree {}

    public record InputNode(String prompt, List<String> vars) implements StatementTree {}

    public record LetNode(String name, ExpressionTree value) implements StatementTree {}

    public record NextNode(List<String> vars) implements StatementTree {}

    public record PrintNode(List<ExpressionTree> values) implements StatementTree {}

    public record ReadNode(List<String> vars) implements StatementTree {}

    public record ReturnNode() implements StatementTree {}

    public record StopNode() implements StatementTree {}

    public record BlankStatementNode() implements StatementTree {}

    public record FunctionExpressionNode(FunctionName name, ExpressionTree argument) implements ExpressionTree, StatementTree {}

    public enum FunctionName {

        ABS,
        ATN,
        COS,
        EXP,
        INT,
        LOG,
        RND,
        SIN,
        SQR,
        TAN

    }

    public record BinaryExpressionNode(BinaryOperation operation, ExpressionTree left, ExpressionTree right) implements ExpressionTree {}

    public enum BinaryOperation {

        OR,
        AND,
        EQUALS,
        DIAMOND,
        LT,
        GT,
        LTE,
        GTE,
        ADD,
        SUBTRACT,
        MULTIPLY,
        DIVIDE

    }

    public record UnaryExpressionNode(UnaryOperation operation, ExpressionTree operand) implements ExpressionTree {}

    public enum UnaryOperation {

        NOT,
        NEGATE

    }

    public record VarNode(String name) implements ExpressionTree {}

    public record NumberNode(double value) implements ConstantTree {}

    public record StringNode(String value) implements ConstantTree {}

}
