package com.example.ast.nodes;

import com.example.ast.ASTVisitor;
import lombok.Builder;

/**
 * Represents a numeric literal in the AST
 */
@Builder
public record Literal(double value) implements Expression {

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitLiteral(this);
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
