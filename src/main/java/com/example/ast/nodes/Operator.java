package com.example.ast.nodes;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

/**
 * Enum representing different operators in the AST
 */
public enum Operator {
    // Binary operators (lowest precedence)
    ADD("+", 10),           // precedence 1 * 10 + 0
    SUBTRACT("-", 11),      // precedence 1 * 10 + 1
    MULTIPLY("*", 20),      // precedence 2 * 10 + 0
    DIVIDE("/", 21),        // precedence 2 * 10 + 1
    MODULO("%", 22),        // precedence 2 * 10 + 2

    // Unary operators (highest precedence)
    INCREMENT("++", 30),    // precedence 3 * 10 + 0
    DECREMENT("--", 31),    // precedence 3 * 10 + 1
    UNARY_PLUS("+", 32),    // precedence 3 * 10 + 2
    UNARY_MINUS("-", 33);   // precedence 3 * 10 + 3

    private final String symbol;
    private final int precedence;

    // Cached sorted list of operators by precedence
    private static final List<Operator> SORTED_BY_PRECEDENCE = Stream.of(values())
                .sorted(Comparator.comparingInt(Operator::getPrecedence))
                .toList();


    Operator(String symbol, int precedence) {
        this.symbol = symbol;
        this.precedence = precedence;
    }

    public String getSymbol() {
        return symbol;
    }

    public int getPrecedence() {
        return precedence;
    }

    /**
     * Returns a stream of all operators sorted by precedence (lowest to highest)
     * Uses a cached sorted list for performance
     * @return sorted stream of operators
     */
    public static Stream<Operator> sortedByPrecedence() {
        return SORTED_BY_PRECEDENCE.stream();
    }

    /**
     * Returns a list of all operators sorted by precedence (lowest to highest)
     * Returns the cached sorted list
     * @return sorted list of operators
     */
    public static List<Operator> listSortedByPrecedence() {
        return SORTED_BY_PRECEDENCE;
    }

    @Override
    public String toString() {
        return symbol;
    }
}
