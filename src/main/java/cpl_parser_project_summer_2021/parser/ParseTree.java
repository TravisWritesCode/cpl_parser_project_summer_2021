package cpl_parser_project_summer_2021.parser;

import java.util.ArrayList;
import java.util.List;

import hu.webarticum.treeprinter.TreeNode;
import hu.webarticum.treeprinter.SimpleTreeNode;
import jdk.jshell.spi.ExecutionControl;

import static cpl_parser_project_summer_2021.parser.Instruction.*;

public interface ParseTree {

    TreeNode makeTree();

    // Implement for lines, line, 2 kinds of supported statements, rem, all expressions
    default List<Instruction> compile() {
        throw new UnsupportedOperationException();
    }

    public interface StatementTree extends ParseTree {}

    public interface ExpressionTree extends ParseTree {}

    public interface ConstantTree extends ExpressionTree {}

    public record LinesNode(List<LineNode> lines) implements ParseTree {
        @Override
        public List<Instruction> compile() {
            var instructions = new ArrayList<Instruction>();
            for (var line : lines) {
                instructions.addAll(line.compile());
            }
            return instructions;
        }
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
        public List<Instruction> compile() {
            var instructions = new ArrayList<Instruction>();
            for (var statement : statements) {
                instructions.addAll(statement.compile());
            }
            return instructions;
        }
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
        public List<Instruction> compile() {
            var instructions = new ArrayList<Instruction>();
            instructions.add(new End());
            return instructions;
        }
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
        public List<Instruction> compile() {
            var instructions = value.compile();
            instructions.add(new SetVar(name));
            return instructions;
        }
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
        public List<Instruction> compile() {
            var instructions = new ArrayList<Instruction>();
            for (var value : values) {
                instructions.addAll(value.compile());
                instructions.add(new Print());
            }
            instructions.add(new PrintNewLine());
            return instructions;
        }
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
        public List<Instruction> compile() {
            return new ArrayList<Instruction>();
        }
        @Override
        public TreeNode makeTree() {
            var node = new SimpleTreeNode("<statement>");
            return node;
        }
    }

    public record FunctionExpressionNode(FunctionName name, ExpressionTree argument) implements ExpressionTree, StatementTree {
        @Override
        public List<Instruction> compile() {
            var instructions = argument.compile();
            instructions.add(new Function(switch(name){
                case ABS -> Instruction.FunctionName.ABS;
                case ATN -> Instruction.FunctionName.ATN;
                case COS -> Instruction.FunctionName.COS;
                case EXP -> Instruction.FunctionName.EXP;
                case INT -> Instruction.FunctionName.INT;
                case LOG -> Instruction.FunctionName.LOG;
                case RND -> Instruction.FunctionName.RND;
                case SIN -> Instruction.FunctionName.SIN;
                case SQR -> Instruction.FunctionName.SQR;
                case TAN -> Instruction.FunctionName.TAN;
            }));
            return instructions;
        }
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
        public List<Instruction> compile() {
            var instructions = left.compile();
            instructions.addAll(right.compile());
            instructions.add(switch(operation){
                case ADD -> new Addition();
                case SUBTRACT -> new Subtraction();
                case MULTIPLY -> new Multiplication();
                case DIVIDE -> new Division();
                default -> throw new UnsupportedOperationException();
            });
            return instructions;
        }
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
        public List<Instruction> compile() {
            var instructions = operand.compile();
            instructions.add(switch(operation){
                case NEGATE -> new Negation();
                default -> throw new UnsupportedOperationException();
            });
            return instructions;
        }
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
        public List<Instruction> compile() {
            var instructions = new ArrayList<Instruction>();
            instructions.add(new GetVar(name));
            return instructions;
        }
        @Override
        public TreeNode makeTree() {
            var node = new SimpleTreeNode("<expr>");
            node.addChild(new SimpleTreeNode(name));
            return node;
        }
    }

    public record NumberNode(double value) implements ConstantTree {
        @Override
        public List<Instruction> compile() {
            var instructions = new ArrayList<Instruction>();
            instructions.add(new PushConstant(value));
            return instructions;
        }
        @Override
        public TreeNode makeTree() {
            var node = new SimpleTreeNode("Number");
            node.addChild(new SimpleTreeNode(String.valueOf(value)));
            return node;
        }
    }

    public record StringNode(String value) implements ConstantTree {
        @Override
        public List<Instruction> compile() {
            var instructions = new ArrayList<Instruction>();
            instructions.add(new PushConstant(value));
            return instructions;
        }
        @Override
        public TreeNode makeTree() {
            var node = new SimpleTreeNode("String");
            node.addChild(new SimpleTreeNode(value));
            return node;
        }
    }

}
