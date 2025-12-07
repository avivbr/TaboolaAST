package com.example.ast.nodes;

import lombok.Getter;

/**
 * Enum representing binary operators in the AST
 */
@Getter
public enum BinaryOperator {
    // Additive operators (lowest precedence)
    ADD("+", 10),           // precedence 1 * 10 + 0
    SUBTRACT("-", 11),      // precedence 1 * 10 + 1

    // Multiplicative operators (higher precedence)
    MULTIPLY("*", 20),      // precedenc 2 * 10 + 0
    DIVIDE("/", 21),        // precedence 2 * 10 + 1
    MODULO("%", 22);        // precedence 2 * 10 + 2

    private final String symbol;
    private final int precedence;

    BinaryOperator(String symbol, int precedence) {
        this.symbol = symbol;
        this.precedence = precedence;
    }

    @Override
    public String toString() {
        return symbol;
    }
}
