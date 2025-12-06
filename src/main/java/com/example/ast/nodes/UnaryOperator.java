package com.example.ast.nodes;

/**
 * Enum representing unary operators in the AST
 */
public enum UnaryOperator {
    INCREMENT("++"),
    DECREMENT("--"),
    UNARY_PLUS("+"),
    UNARY_MINUS("-");

    private final String symbol;

    UnaryOperator(String symbol) {
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
