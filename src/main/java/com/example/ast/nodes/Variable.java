package com.example.ast.nodes;

import com.example.ast.ASTVisitor;
import lombok.Builder;

/**
 * Represents a variable reference in the AST
 */
@Builder
public record Variable(String name) implements Expression {

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitVariable(this);
    }

    @Override
    public String toString() {
        return String.format("%s", name);
    }
}
