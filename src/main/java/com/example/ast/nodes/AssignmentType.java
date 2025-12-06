package com.example.ast.nodes;

/**
 * Enum representing different assignment operators
 */
public enum AssignmentType {
    ASSIGN("="),
    ADD_ASSIGN("+="),
    SUBTRACT_ASSIGN("-="),
    MULTIPLY_ASSIGN("*="),
    DIVIDE_ASSIGN("/=");

    private final String symbol;

    AssignmentType(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    @Override
    public String toString() {
        return symbol;
    }
}
