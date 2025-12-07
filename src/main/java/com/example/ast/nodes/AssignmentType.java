package com.example.ast.nodes;

import lombok.Getter;

/**
 * Enum representing different assignment operators
 */
@Getter
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

    @Override
    public String toString() {
        return String.format("%s", symbol);
    }
}
