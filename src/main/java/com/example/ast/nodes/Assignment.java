package com.example.ast.nodes;

import com.example.ast.ASTVisitor;
import lombok.Builder;

/**
 * Represents a variable assignment statement in the AST
 */
@Builder
public record Assignment(Variable variable, Expression value, AssignmentType type) implements Statement {

    @Override
    public <T> T accept(ASTVisitor<T> visitor) {
        return visitor.visitAssignment(this);
    }

    @Override
    public String toString() {
        return String.format("%s %s %s", variable, type, value);
    }
}
