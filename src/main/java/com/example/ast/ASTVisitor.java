package com.example.ast;

import com.example.ast.nodes.*;

/**
 * Visitor interface for traversing and processing AST nodes
 */
public interface ASTVisitor<T> {
    T visitVariable(Variable variable);
    T visitLiteral(Literal literal);
    T visitBinaryOperation(BinaryOperation operation);
    T visitUnaryOperation(UnaryOperation operation);
    T visitAssignment(Assignment assignment);
}
