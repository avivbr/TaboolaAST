package com.example.ast;

import com.example.ast.nodes.*;

/**
 * Example demonstrating multi-line execution where assignments from line X
 * are available for line X + 1 using the Environment cache map.
 */
public class MultiLineExample {
    public static void main(String[] args) {
        // Create environment to store variables across lines
        Heap env = new Heap();
        Evaluator evaluator = new Evaluator(env);

        System.out.println("=== Multi-Line Execution Example ===\n");

        // Line 1: a = 10
        Statement line1 = new Assignment(
                new Variable("a"),
                new Literal(10),
                AssignmentType.ASSIGN
        );
        System.out.println("Line 1: " + line1);
        evaluator.execute(line1);
        System.out.println("Result: a = " + env.get("a").orElse(null) + "\n");

        // Line 2: b = 20
        Statement line2 = new Assignment(
                new Variable("b"),
                new Literal(20),
                AssignmentType.ASSIGN
        );
        System.out.println("Line 2: " + line2);
        evaluator.execute(line2);
        System.out.println("Result: b = " + env.get("b").orElse(null) + "\n");

        // Line 3: c = a + b (uses variables from previous lines)
        Statement line3 = new Assignment(
                new Variable("c"),
                new BinaryOperation(
                        new Variable("a"),
                        BinaryOperator.ADD,
                        new Variable("b")
                ),
                AssignmentType.ASSIGN
        );
        System.out.println("Line 3: " + line3);
        evaluator.execute(line3);
        System.out.println("Result: c = " + env.get("c").orElse(null) + "\n");

        // Line 4: d = c * 2 (uses c from previous line)
        Statement line4 = new Assignment(
                new Variable("d"),
                new BinaryOperation(
                        new Variable("c"),
                        BinaryOperator.MULTIPLY,
                        new Literal(2)
                ),
                AssignmentType.ASSIGN
        );
        System.out.println("Line 4: " + line4);
        evaluator.execute(line4);
        System.out.println("Result: d = " + env.get("d").orElse(null) + "\n");

        // Line 5: e = d / a (uses d and a from previous lines)
        Statement line5 = new Assignment(
                new Variable("e"),
                new BinaryOperation(
                        new Variable("d"),
                        BinaryOperator.DIVIDE,
                        new Variable("a")
                ),
                AssignmentType.ASSIGN
        );
        System.out.println("Line 5: " + line5);
        evaluator.execute(line5);
        System.out.println("Result: e = " + env.get("e").orElse(null) + "\n");

        // Line 6: a += 5 (compound assignment)
        Statement line6 = new Assignment(
                new Variable("a"),
                new Literal(5),
                AssignmentType.ADD_ASSIGN
        );
        System.out.println("Line 6: " + line6);
        evaluator.execute(line6);
        System.out.println("Result: a = " + env.get("a").orElse(null) + "\n");

        // Line 7: d *= 3 (compound assignment)
        Statement line7 = new Assignment(
                new Variable("d"),
                new Literal(3),
                AssignmentType.MULTIPLY_ASSIGN
        );
        System.out.println("Line 7: " + line7);
        evaluator.execute(line7);
        System.out.println("Result: d = " + env.get("d").orElse(null) + "\n");

        // Show all variables in the environment
        System.out.println("=== Final Environment State ===");
        env.getAll().forEach((name, value) ->
                System.out.println(name + " = " + value)
        );
    }
}
