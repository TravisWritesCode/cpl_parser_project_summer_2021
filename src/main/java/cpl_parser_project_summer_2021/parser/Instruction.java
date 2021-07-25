package cpl_parser_project_summer_2021.parser;

import java.util.*;

public interface Instruction {

    //Addition, subtraction, multiplication, division, negation, assignment, print , push constant, push var value, function expression

    boolean run(Stack<Object> stack, HashMap<String, Object> vars);

    public static void execute(List<Instruction> instructions) {
        var stack = new Stack<Object>();
        var vars = new HashMap<String, Object>();
        for (var instruction: instructions){
            if (!instruction.run(stack, vars)) break;
        }
    }

    public record Addition() implements Instruction{
        public boolean run(Stack<Object> stack, HashMap<String, Object> vars) {
            var rhs = (Double)stack.pop();
            var lhs = (Double)stack.pop();
            stack.push(lhs + rhs);
            return true;
        }
    }

    public record Subtraction() implements Instruction {
        public boolean run(Stack<Object> stack, HashMap<String, Object> vars) {
            var rhs = (Double) stack.pop();
            var lhs = (Double) stack.pop();
            stack.push(lhs - rhs);
            return true;
        }
    }

    public record Multiplication() implements Instruction{
        public boolean run(Stack<Object> stack, HashMap<String, Object> vars) {
            var rhs = (Double)stack.pop();
            var lhs = (Double)stack.pop();
            stack.push(lhs * rhs);
            return true;
        }
    }

    public record Division() implements Instruction{
        public boolean run(Stack<Object> stack, HashMap<String, Object> vars) {
            var rhs = (Double) stack.pop();
            var lhs = (Double) stack.pop();
            stack.push(lhs / rhs);
            return true;
        }
    }

    public record Negation() implements Instruction{
        public boolean run(Stack<Object> stack, HashMap<String, Object> vars) {
            var operand = (Double)stack.pop();
            stack.push(-operand);
            return true;
        }
    }

    public record Function(FunctionName name) implements Instruction{
        public boolean run(Stack<Object> stack, HashMap<String, Object> vars) {
            var argument = (Double)stack.pop();
            stack.push(
            switch(name){
                case ABS -> Math.abs(argument);
                case ATN -> Math.atan(argument);
                case COS -> Math.cos(argument);
                case EXP -> Math.exp(argument);
                case INT -> Math.rint(argument);
                case LOG -> Math.log(argument);
                case RND -> new Random().nextInt((int)(double)argument);
                case SIN -> Math.sin(argument);
                case SQR -> Math.sqrt(argument);
                case TAN -> Math.tan(argument);
            });
            return true;
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

    // pop value and store somewhere with name of variable
    public record SetVar(String name) implements Instruction{
        public boolean run(Stack<Object> stack, HashMap<String, Object> vars) {
            vars.put(name, stack.pop());
            return true;
        }
    }

    // find what was stored and push to stack
    public record GetVar(String name) implements Instruction{
        public boolean run(Stack<Object> stack, HashMap<String, Object> vars) {
            stack.push(vars.get(name));
            return true;
        }
    }

    public record Print() implements Instruction{
        public boolean run(Stack<Object> stack, HashMap<String, Object> vars) {
            System.out.print(stack.pop() + " ");
            return true;
        }
    }

    public record PrintNewLine() implements Instruction{
        public boolean run(Stack<Object> stack, HashMap<String, Object> vars) {
            System.out.println();
            return true;
        }
    }

    public record PushConstant(Object constant) implements Instruction{
        public boolean run(Stack<Object> stack, HashMap<String, Object> vars) {
            stack.push(constant);
            return true;
        }
    }

    public record End() implements Instruction{
        public boolean run(Stack<Object> stack, HashMap<String, Object> vars) {
            return false;
        }
    }


}
