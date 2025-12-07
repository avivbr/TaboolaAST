package com.example.ast.nodes;

import lombok.Getter;

/**
 * Enum representing unary operators in the AST
 */
@Getter
public enum UnaryOperator {
    INCREMENT("++"),
    DECREMENT("--");

    private final String symbol;

    UnaryOperator(String symbol) {
        this.symbol = symbol;
    }

    @Override
    public String toString() {
        return symbol;
    }
}
