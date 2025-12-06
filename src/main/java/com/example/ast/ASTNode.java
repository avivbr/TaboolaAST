package com.example.ast;

/**
 * Base interface for all AST nodes
 */
public interface ASTNode {
    /**
     * Accept a visitor for traversing the AST
     */
    <T> T accept(ASTVisitor<T> visitor);
}
