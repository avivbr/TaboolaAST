package com.example.ast.nodes;

import com.example.ast.ASTVisitor;
import lombok.Builder;

/**
 * Represents a unary operation (e.g., ++, --) in the AST
 */
@Builder
public record UnaryOperation(
        UnaryOperator operator,
        Variable operand,
        boolean isPrefix
) implements Expression {

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitUnaryOperation(this);
    }

    @Override
    public String toString() {
        return isPrefix ? operator + operand.toString() : operand.toString() + operator;
    }
}
