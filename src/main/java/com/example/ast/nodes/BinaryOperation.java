package com.example.ast.nodes;

import com.example.ast.ASTVisitor;
import lombok.Builder;

/**
 * Represents a binary operation (e.g., +, -, *, /) in the AST
 */
@Builder
public record BinaryOperation(
        Expression left,
        BinaryOperator operator,
        Expression right) implements Expression {

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitBinaryOperation(this);
    }

    @Override
    public String toString() {
        return "(" + left + " " + operator + " " + right + ")";
    }
}
