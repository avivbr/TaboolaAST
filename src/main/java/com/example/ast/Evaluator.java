package com.example.ast;

import com.example.ast.nodes.*;

/**
 * Evaluator that executes AST nodes using an Environment to track variable assignments.
 * This allows assignments from previous lines to be available in subsequent lines.
 */
public class Evaluator implements ASTVisitor<Double> {

    private final Heap heap;

    public Evaluator(Heap heap) {
        this.heap = heap;
    }

    /**
     * Execute a statement (typically an assignment)
     */
    public void execute(Statement statement) {
        statement.accept(this);
    }

    /**
     * Evaluate an expression to a numeric value
     */
    public double evaluate(Expression expression) {
        return expression.accept(this);
    }

    @Override
    public Double visitVariable(Variable variable) {
        return heap.get(variable.name())
                .orElseThrow(() -> new RuntimeException("Undefined variable: " + variable.name()));
    }

    @Override
    public Double visitLiteral(Literal literal) {
        return literal.value();
    }

    @Override
    public Double visitBinaryOperation(BinaryOperation operation) {
        double left = operation.left().accept(this);
        double right = operation.right().accept(this);

        return switch (operation.operator()) {
            case ADD -> left + right;
            case SUBTRACT -> left - right;
            case MULTIPLY -> left * right;
            case DIVIDE -> {
                if (right == 0) {
                    throw new ArithmeticException("Division by zero");
                }
                yield left / right;
            }
            case MODULO -> left % right;
            default -> throw new UnsupportedOperationException("Unsupported binary operator: " + operation.operator());
        };
    }

    @Override
    public Double visitUnaryOperation(UnaryOperation operation) {
        String varName = operation.operand().name();
        double value = heap.get(varName)
                .orElseThrow(() -> new RuntimeException("Undefined variable: " + varName));

        return switch (operation.operator()) {
            case INCREMENT -> {
                double newValue = value + 1;
                heap.set(varName, newValue);
                yield operation.isPrefix() ? newValue : value;
            }
            case DECREMENT -> {
                double newValue = value - 1;
                heap.set(varName, newValue);
                yield operation.isPrefix() ? newValue : value;
            }
            case UNARY_PLUS -> value;
            case UNARY_MINUS -> -value;
            default -> throw new UnsupportedOperationException("Unsupported unary operator: " + operation.operator());
        };
    }

    @Override
    public Double visitAssignment(Assignment assignment) {
        String varName = assignment.variable().name();
        double rightValue = assignment.value().accept(this);
        double finalValue;

        switch (assignment.type()) {
            case ASSIGN -> finalValue = rightValue;
            case ADD_ASSIGN -> {
                double currentValue = heap.get(varName)
                        .orElseThrow(() -> new RuntimeException("Undefined variable: " + varName));
                finalValue = currentValue + rightValue;
            }
            case SUBTRACT_ASSIGN -> {
                double currentValue = heap.get(varName)
                        .orElseThrow(() -> new RuntimeException("Undefined variable: " + varName));
                finalValue = currentValue - rightValue;
            }
            case MULTIPLY_ASSIGN -> {
                double currentValue = heap.get(varName)
                        .orElseThrow(() -> new RuntimeException("Undefined variable: " + varName));
                finalValue = currentValue * rightValue;
            }
            case DIVIDE_ASSIGN -> {
                double currentValue = heap.get(varName)
                        .orElseThrow(() -> new RuntimeException("Undefined variable: " + varName));
                if (rightValue == 0) {
                    throw new ArithmeticException("Division by zero");
                }
                finalValue = currentValue / rightValue;
            }
            default -> throw new UnsupportedOperationException("Unsupported assignment type: " + assignment.type());
        }

        heap.set(varName, finalValue);
        return finalValue;
    }
}
