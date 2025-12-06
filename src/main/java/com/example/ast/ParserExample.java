package com.example.ast;

import com.example.ast.nodes.*;

/**
 * Example demonstrating the Parser that converts string assignments into AST.
 */
public class ParserExample {
    public static void main(String[] args) {
        Parser parser = new Parser();
        Heap heap = new Heap();
        Evaluator evaluator = new Evaluator(heap);

        System.out.println("=== Parser Example ===\n");

        // Parse and execute multiple lines
        String[] lines = {
                "a = 10",
                "b = 20",
                "c = a + b",
                "d = c * 2",
                "e = (a + b) * 3",
                "f = 100 / 4",
                "a += 5",
                "d *= 2",
                "g = a - b + c",
                "h = 50 % 7"
        };

        for (String line : lines) {
            System.out.println("Input:  " + line);
            Statement stmt = parser.parse(line);
            System.out.println("AST:    " + stmt);
            evaluator.execute(stmt);
            System.out.println();
        }

        System.out.println("=== Final State ===");
        heap.getAll().forEach((name, value) ->
                System.out.println(name + " = " + value)
        );
    }
}
