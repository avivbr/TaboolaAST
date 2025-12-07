package com.example.ast.nodes;

import lombok.Getter;

/**
 * Enum representing binary operators in the AST
 */
@Getter
public enum BinaryOperator {
    ADD("+"),
    SUBTRACT("-"),

    // Multiplicative operators (higher precedence)
    MULTIPLY("*"),
    DIVIDE("/"),
    MODULO("%");

    private final String symbol;

    BinaryOperator(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return String.format("%s", symbol);
    }
}
