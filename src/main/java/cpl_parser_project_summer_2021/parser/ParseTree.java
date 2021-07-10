package cpl_parser_project_summer_2021.parser;

import java.util.List;

import hu.webarticum.treeprinter.TreeNode;
import hu.webarticum.treeprinter.SimpleTreeNode;

public interface ParseTree {

    TreeNode makeTree();

    public interface StatementTree extends ParseTree {}

    public interface ExpressionTree extends ParseTree {}

    public interface ConstantTree extends ExpressionTree {}

    public record LinesNode(List<LineNode> lines) implements ParseTree {

        @Override
        public TreeNode makeTree() {
            var node = new SimpleTreeNode("<lines>");
            for (var line : lines) {
                node.addChild(line.makeTree());
                node.addChild(new SimpleTreeNode("(new line)"));
            }
            return node;
        }

    }

    public record LineNode(NumberNode number, List<StatementTree> statements) implements ParseTree {

        @Override
        public TreeNode makeTree() {
            var node = new SimpleTreeNode("<line>");
            node.addChild(number.makeTree());
            var first = true;
            for(var statement: statements) {
                if (first) first = false;
                else node.addChild(new SimpleTreeNode(";"));
                node.addChild(statement.makeTree());
            }
            return node;
        }
    }

    public record DataNode(List<ConstantTree> data) implements StatementTree {
        @Override
        public TreeNode makeTree() {
            var node = new SimpleTreeNode("<statement>");
            node.addChild(new SimpleTreeNode("DATA"));
            var first = true;
            for (var datum : data) {
                if (first) first = false;
                else node.addChild(new SimpleTreeNode(";"));
                node.addChild(datum.makeTree());
            }
            return node;
        }
    }

    public record DimNode(String name, List<NumberNode> lengths) implements StatementTree {

        @Override
        public TreeNode makeTree() {
            var node = new SimpleTreeNode("<statement>");
            node.addChild(new SimpleTreeNode("DIM"));
            var id = new SimpleTreeNode("ID");
            id.addChild(new SimpleTreeNode(name));
            node.addChild(id);
            var first = true;
            for (var length : lengths) {
                if (first) first = false;
                else node.addChild(new SimpleTreeNode(";"));
                node.addChild(length.makeTree());
            }
            return node;
        }

    }

    public record EndNode() implements StatementTree {
        @Override
        public TreeNode makeTree() {
            var node = new SimpleTreeNode("<statement>");
            node.addChild(new SimpleTreeNode("END"));
            return node;
        }
    }

    public record ForNode(String var, ExpressionTree from, ExpressionTree to, ConstantTree step) implements StatementTree {
        @Override
        public TreeNode makeTree() {
            var node = new SimpleTreeNode("<statement>");
            node.addChild(new SimpleTreeNode("FOR"));
            var id = new SimpleTreeNode("ID");
            id.addChild(new SimpleTreeNode(var));
            node.addChild(id);
            node.addChild(new SimpleTreeNode("="));
            node.addChild(from.makeTree());
            node.addChild(new SimpleTreeNode("TO"));
            node.addChild(to.makeTree());
            if (step != null) {
                node.addChild(new SimpleTreeNode("STEP"));
                node.addChild(step.makeTree());
            }
            return node;
        }
    }

    public record GotoNode(ExpressionTree destination) implements StatementTree {
        @Override
        public TreeNode makeTree() {
            var node = new SimpleTreeNode("<statement>");
            node.addChild(new SimpleTreeNode("GOTO"));
            node.addChild(destination.makeTree());
            return node;
        }
    }

    public record GosubNode(ExpressionTree destination) implements StatementTree {
        @Override
        public TreeNode makeTree() {
            var node = new SimpleTreeNode("<statement>");
            node.addChild(new SimpleTreeNode("GOSUB"));
            node.addChild(destination.makeTree());
            return node;
        }
    }

    public record IfNode(ExpressionTree condition, NumberNode destination) implements StatementTree {
        @Override
        public TreeNode makeTree() {
            var node = new SimpleTreeNode("<statement>");
            node.addChild(new SimpleTreeNode("IF"));
            node.addChild(condition.makeTree());
            node.addChild(new SimpleTreeNode("THEN"));
            node.addChild(destination.makeTree());
            return node;
        }
    }

    public record InputNode(String prompt, List<String> vars) implements StatementTree {
        @Override
        public TreeNode makeTree() {
            var node = new SimpleTreeNode("<statement>");
            node.addChild(new SimpleTreeNode("INPUT"));
            if (prompt != null) {
                var string = new SimpleTreeNode("String");
                string.addChild(new SimpleTreeNode(prompt));
                node.addChild(string);
                node.addChild(new SimpleTreeNode(";"));
            }
            var first = true;
            for (var var : vars) {
                if (first) first = false;
                else node.addChild(new SimpleTreeNode(";"));
                var id = new SimpleTreeNode("ID");
                id.addChild(new SimpleTreeNode(var));
                node.addChild(id);
            }
            return node;
        }
    }

    public record LetNode(String name, ExpressionTree value) implements StatementTree {
        @Override
        public TreeNode makeTree() {
            var node = new SimpleTreeNode("<statement>");
            node.addChild(new SimpleTreeNode("LET"));
            var id = new SimpleTreeNode("ID");
            id.addChild(new SimpleTreeNode(name));
            node.addChild(id);
            node.addChild(new SimpleTreeNode("="));
            node.addChild(value.makeTree());
            return node;
        }
    }

    public record NextNode(List<String> vars) implements StatementTree {
        @Override
        public TreeNode makeTree() {
            var node = new SimpleTreeNode("<statement>");
            node.addChild(new SimpleTreeNode("NEXT"));
            var first = true;
            for (var var : vars) {
                if (first) first = false;
                else node.addChild(new SimpleTreeNode(";"));
                var id = new SimpleTreeNode("ID");
                id.addChild(new SimpleTreeNode(var));
                node.addChild(id);
            }
            return node;
        }
    }

    public record PrintNode(List<ExpressionTree> values) implements StatementTree {
        @Override
        public TreeNode makeTree() {
            var node = new SimpleTreeNode("<statement>");
            node.addChild(new SimpleTreeNode("PRINT"));
            var first = true;
            for (var value : values) {
                if (first) first = false;
                else node.addChild(new SimpleTreeNode(";"));
                node.addChild(value.makeTree());
            }
            return node;
        }
    }

    public record ReadNode(List<String> vars) implements StatementTree {
        @Override
        public TreeNode makeTree() {
            var node = new SimpleTreeNode("<statement>");
            node.addChild(new SimpleTreeNode("READ"));
            var first = true;
            for (var var : vars) {
                if (first) first = false;
                else node.addChild(new SimpleTreeNode(";"));
                var id = new SimpleTreeNode("ID");
                id.addChild(new SimpleTreeNode(var));
                node.addChild(id);
            }
            return node;
        }
    }

    public record ReturnNode() implements StatementTree {
        @Override
        public TreeNode makeTree() {
            var node = new SimpleTreeNode("<statement>");
            node.addChild(new SimpleTreeNode("RETURN"));
            return node;
        }
    }

    public record StopNode() implements StatementTree {
        @Override
        public TreeNode makeTree() {
            var node = new SimpleTreeNode("<statement>");
            node.addChild(new SimpleTreeNode("STOP"));
            return node;
        }
    }

    public record BlankStatementNode() implements StatementTree {
        @Override
        public TreeNode makeTree() {
            var node = new SimpleTreeNode("<statement>");
            return node;
        }
    }

    public record FunctionExpressionNode(FunctionName name, ExpressionTree argument) implements ExpressionTree, StatementTree {
        @Override
        public TreeNode makeTree() {
            var node = new SimpleTreeNode("<expr>");
            node.addChild(new SimpleTreeNode(name.name()));
            node.addChild(new SimpleTreeNode("("));
            node.addChild(argument.makeTree());
            node.addChild(new SimpleTreeNode(")"));
            return node;
        }
    }

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

    public record BinaryExpressionNode(BinaryOperation operation, ExpressionTree left, ExpressionTree right) implements ExpressionTree {
        @Override
        public TreeNode makeTree() {
            var node = new SimpleTreeNode("<expr>");
            node.addChild(left.makeTree());
            node.addChild(new SimpleTreeNode(switch (operation) {
                case OR -> "OR";
                case AND -> "AND";
                case EQUALS -> "=";
                case DIAMOND -> "<>";
                case LT -> "<";
                case GT -> ">";
                case LTE -> "<=";
                case GTE -> ">=";
                case ADD -> "+";
                case SUBTRACT -> "-";
                case MULTIPLY -> "*";
                case DIVIDE -> "/";
            }));
            node.addChild(right.makeTree());
            return node;
        }
    }

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

    public record UnaryExpressionNode(UnaryOperation operation, ExpressionTree operand) implements ExpressionTree {
        @Override
        public TreeNode makeTree() {
            var node = new SimpleTreeNode("<expr>");
            node.addChild(new SimpleTreeNode(switch(operation) {
                case NOT -> "!";
                case NEGATE -> "-";
            }));
            node.addChild(operand.makeTree());
            return node;
        }
    }

    public enum UnaryOperation {

        NOT,
        NEGATE

    }

    public record VarNode(String name) implements ExpressionTree {
        @Override
        public TreeNode makeTree() {
            var node = new SimpleTreeNode("<expr>");
            node.addChild(new SimpleTreeNode(name));
            return node;
        }
    }

    public record NumberNode(double value) implements ConstantTree {
        @Override
        public TreeNode makeTree() {
            var node = new SimpleTreeNode("Number");
            node.addChild(new SimpleTreeNode(String.valueOf(value)));
            return node;
        }
    }

    public record StringNode(String value) implements ConstantTree {
        @Override
        public TreeNode makeTree() {
            var node = new SimpleTreeNode("String");
            node.addChild(new SimpleTreeNode(value));
            return node;
        }
    }

}
