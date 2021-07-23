package cpl_parser_project_summer_2021.parser;

public interface Instruction {

    //Addition, subtraction, multiplication, division, negation, assignment, print , push constant, push var value, function expression

    public record Addition() implements Instruction{

    }

    public record Subtraction() implements Instruction {

    }

    public record Multiplication() implements Instruction{

    }

    public record Division() implements Instruction{

    }

    public record Negation() implements Instruction{

    }

    public record Function(FunctionName name) implements Instruction{

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

    }

    // find what was stored and push to stack
    public record GetVar(String name) implements Instruction{

    }

    public record Print() implements Instruction{

    }

    public record PrintNewLine() implements Instruction{

    }

    public record PushConstant(Object constant) implements Instruction{

    }

    public record End() implements Instruction{

    }


}
