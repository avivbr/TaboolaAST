package com.example.ast;

import com.example.ast.nodes.*;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Parser for converting string representations of assignments into AST nodes.
 * Parsing order follows operator precedence: parentheses -> unary -> binary (by precedence).
 */
public class Parser {

    // Pattern to match assignment statements with various operators
    private static final Pattern ASSIGNMENT_PATTERN = Pattern.compile(
            "^\\s*(\\w+)\\s*(=|\\+=|-=|\\*=|/=)\\s*(.+)\\s*$"
    );

    // Pattern to match additive operators (+, -) but not unary (++, --)
    private static final Pattern ADD_PATTERN = Pattern.compile("(?<!\\+)\\+(?!\\+)");
    private static final Pattern SUBTRACT_PATTERN = Pattern.compile("(?<!-)\\-(?!-)");

    // Pattern to match multiplicative operators (*, /, %)
    private static final Pattern MULTIPLICATIVE_PATTERN = Pattern.compile("\\*|\\/|\\%");

    /**
     * Parse a string into an Assignment statement
     */
    public Statement parse(String input) {
        Matcher matcher = ASSIGNMENT_PATTERN.matcher(input);
        if (!matcher.matches()) {
            throw new IllegalArgumentException("Invalid assignment syntax: " + input);
        }

        String varName = matcher.group(1);
        String assignOp = matcher.group(2);
        String exprStr = matcher.group(3);

        Variable variable = new Variable(varName);
        AssignmentType type = parseAssignmentType(assignOp);
        Expression expression = parseExpression(exprStr)
                .orElseThrow(() -> new IllegalArgumentException("Unable to parse expression: " + exprStr));

        return new Assignment(variable, expression, type);
    }

    /**
     * Parse the assignment operator
     */
    private AssignmentType parseAssignmentType(String op) {
        return switch (op) {
            case "=" -> AssignmentType.ASSIGN;
            case "+=" -> AssignmentType.ADD_ASSIGN;
            case "-=" -> AssignmentType.SUBTRACT_ASSIGN;
            case "*=" -> AssignmentType.MULTIPLY_ASSIGN;
            case "/=" -> AssignmentType.DIVIDE_ASSIGN;
            default -> throw new IllegalArgumentException("Unknown assignment operator: " + op);
        };
    }

    /**
     * Main expression parser with operator precedence.
     * Order: Parentheses -> Unary -> Binary (by precedence) -> Literal -> Variable
     */
    private Optional<Expression> parseExpression(String expr) {
        return Optional.ofNullable(expr)
                .filter(e -> !e.isBlank())
                .map(String::trim)
                .flatMap(this::parseExpressionInternal);
    }

    private Optional<Expression> parseExpressionInternal(String expr) {
        // Step 1: Try to break into binary operation with subtract operator (-)
        Optional<BinaryOperation> subtract = tryBreakIntoBinaryOperation(expr, SUBTRACT_PATTERN);
        if (subtract.isPresent()) {
            return subtract.map(b -> (Expression) b);
        }

        // Step 2: Try to break into binary operation with add operator (+)
        Optional<BinaryOperation> add = tryBreakIntoBinaryOperation(expr, ADD_PATTERN);
        if (add.isPresent()) {
            return add.map(b -> (Expression) b);
        }

        // Step 3: Try to break into binary operation with multiplicative operators (*, /, %)
        Optional<BinaryOperation> multiplicative = tryBreakIntoBinaryOperation(expr, MULTIPLICATIVE_PATTERN);
        if (multiplicative.isPresent()) {
            return multiplicative.map(b -> (Expression) b);
        }

        // Step 4: Try to parse as atomic expression (literal, variable, or unary operation)
        // If the expression is wrapped in parentheses, recursion will handle it
        return tryParseAtomicExpression(expr);
    }

    /**
     * Try to break the expression into a binary operation using the given pattern
     * Uses regex to find the first operator outside parentheses
     */
    private Optional<BinaryOperation> tryBreakIntoBinaryOperation(String expr, Pattern operatorPattern) {
        // Replace parenthesized content with placeholders to ignore operators inside
        String maskedExpr = maskParentheses(expr);

        // Find the first operator match outside parentheses
        Matcher matcher = operatorPattern.matcher(maskedExpr);

        while (matcher.find()) {
            int operatorPos = matcher.start();
            String operatorSymbol = matcher.group();

            // Find the matching BinaryOperator
            BinaryOperator matchedOp = findOperatorBySymbol(operatorSymbol);
            if (matchedOp == null) {
                continue;
            }

            // Split the expression into three parts
            String leftStr = expr.substring(0, operatorPos).trim();
            String rightStr = expr.substring(operatorPos + operatorSymbol.length()).trim();

            // Ensure both sides are non-empty
            if (!leftStr.isEmpty() && !rightStr.isEmpty()) {
                // Recursively parse left and right sides
                Optional<Expression> left = parseExpression(leftStr);
                Optional<Expression> right = parseExpression(rightStr);

                if (left.isPresent() && right.isPresent()) {
                    return Optional.of(new BinaryOperation(left.get(), matchedOp, right.get()));
                }
            }
        }

        return Optional.empty();
    }

    /**
     * Mask parenthesized content with spaces to ignore operators inside
     */
    private String maskParentheses(String expr) {
        StringBuilder masked = new StringBuilder(expr);
        int depth = 0;

        for (int i = 0; i < masked.length(); i++) {
            char c = masked.charAt(i);

            if (c == '(') {
                depth++;
            } else if (c == ')') {
                depth--;
            } else if (depth > 0) {
                // Replace content inside parentheses with spaces
                masked.setCharAt(i, ' ');
            }
        }

        return masked.toString();
    }

    /**
     * Find the BinaryOperator that matches the given symbol
     */
    private BinaryOperator findOperatorBySymbol(String symbol) {
        return switch (symbol) {
            case "+" -> BinaryOperator.ADD;
            case "-" -> BinaryOperator.SUBTRACT;
            case "*" -> BinaryOperator.MULTIPLY;
            case "/" -> BinaryOperator.DIVIDE;
            case "%" -> BinaryOperator.MODULO;
            default -> null;
        };
    }

    /**
     * Try to parse as an atomic expression (literal, variable, or unary operation)
     * If wrapped in parentheses, strip them and recursively parse
     */
    private Optional<Expression> tryParseAtomicExpression(String expr) {
        // If expression is wrapped in outer parentheses, strip them and recursively parse
        String stripped = Expression.stripOuterParentheses(expr);
        if (!stripped.equals(expr)) {
            return parseExpression(stripped);
        }

        // Try unary operation first
        Optional<UnaryOperation> unary = tryParseUnary(expr);
        if (unary.isPresent()) {
            return unary.map(u -> (Expression) u);
        }

        // Try literal
        Optional<Literal> literal = tryParseLiteral(expr);
        if (literal.isPresent()) {
            return literal.map(l -> (Expression) l);
        }

        // Try variable
        return tryParseVariable(expr).map(v -> (Expression) v);
    }

    /**
     * Try to parse as unary operation
     */
    private Optional<UnaryOperation> tryParseUnary(String expr) {
        // Check for prefix unary operators (++x, --x)
        if (expr.startsWith("++") || expr.startsWith("--")) {
            String op = expr.substring(0, 2);
            String varStr = expr.substring(2).trim();
            return tryParseVariable(varStr).map(var -> {
                UnaryOperator operator = op.equals("++") ? UnaryOperator.INCREMENT : UnaryOperator.DECREMENT;
                return new UnaryOperation(operator, var, true);
            });
        }

        // Check for postfix unary operators (x++, x--)
        if (expr.endsWith("++") || expr.endsWith("--")) {
            String op = expr.substring(expr.length() - 2);
            String varStr = expr.substring(0, expr.length() - 2).trim();
            return tryParseVariable(varStr).map(var -> {
                UnaryOperator operator = op.equals("++") ? UnaryOperator.INCREMENT : UnaryOperator.DECREMENT;
                return new UnaryOperation(operator, var, false);
            });
        }

        return Optional.empty();
    }

    /**
     * Try to parse as literal
     */
    private Optional<Literal> tryParseLiteral(String expr) {
        try {
            double value = Double.parseDouble(expr);
            return Optional.of(new Literal(value));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    /**
     * Try to parse as variable
     */
    private Optional<Variable> tryParseVariable(String expr) {
        if (expr.matches("[a-zA-Z_$][a-zA-Z0-9_$]*")) {
            return Optional.of(new Variable(expr));
        }
        return Optional.empty();
    }
}
