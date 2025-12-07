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
    private static final Pattern ADDITIVE_PATTERN = Pattern.compile("(?<!\\+)\\+(?!\\+)|(?<!-)-(?!-)");

    // Pattern to match multiplicative operators (*, /, %)
    private static final Pattern MULTIPLICATIVE_PATTERN = Pattern.compile("[*/%]");

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

        Variable variable = Variable.builder().name(varName).build();
        AssignmentType type = parseAssignmentType(assignOp);
        Expression expression = parseExpression(exprStr)
                .orElseThrow(() -> new IllegalArgumentException("Unable to parse expression: " + exprStr));

        return Assignment.builder()
                .variable(variable)
                .value(expression)
                .type(type)
                .build();
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

    private Optional<? extends Expression> parseExpressionInternal(String expr) {
        // Step 1: Try to break into binary operation with additive operators (+, -)
        Optional<BinaryOperation> additive = tryBreakIntoBinaryOperation(expr, ADDITIVE_PATTERN);
        if (additive.isPresent()) {
            return additive;
        }

        // Step 2: Try to break into binary operation with multiplicative operators (*, /, %)
        Optional<BinaryOperation> multiplicative = tryBreakIntoBinaryOperation(expr, MULTIPLICATIVE_PATTERN);
        if (multiplicative.isPresent()) {
            return multiplicative;
        }

        // Step 3: Try to parse as atomic expression (literal, variable, or unary operation)
        // If the expression is wrapped in parentheses, recursion will handle it
        return tryParseAtomicExpression(expr);
    }

    /**
     * Try to break the expression into a binary operation using the given pattern
     * For left-associativity, use the rightmost operator match outside parentheses
     * (breaking at rightmost operator produces left-associative trees via recursion)
     */
    private Optional<BinaryOperation> tryBreakIntoBinaryOperation(String expr, Pattern operatorPattern) {
        // Replace parenthesized content with placeholders to ignore operators inside
        String maskedExpr = maskParentheses(expr);

        // Find all operator matches and use the rightmost one (for left-associativity)
        Matcher matcher = operatorPattern.matcher(maskedExpr);
        int rightmostPos = -1;
        String rightmostSymbol = null;

        while (matcher.find()) {
            rightmostPos = matcher.start();
            rightmostSymbol = matcher.group();
        }

        if (rightmostPos != -1) {
            // Find the matching BinaryOperator
            BinaryOperator matchedOp = findOperatorBySymbol(rightmostSymbol);
            if (matchedOp != null) {
                // Split the expression into three parts
                String leftStr = expr.substring(0, rightmostPos).trim();
                String rightStr = expr.substring(rightmostPos + rightmostSymbol.length()).trim();

                // Ensure both sides are non-empty
                if (!leftStr.isEmpty() && !rightStr.isEmpty()) {
                    // Recursively parse left and right sides
                    Optional<Expression> left = parseExpression(leftStr);
                    Optional<Expression> right = parseExpression(rightStr);

                    if (left.isPresent() && right.isPresent()) {
                        return Optional.of(BinaryOperation.builder()
                                .left(left.get())
                                .operator(matchedOp)
                                .right(right.get())
                                .build());
                    }
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
    private Optional<? extends Expression> tryParseAtomicExpression(String expr) {
        // If expression is wrapped in outer parentheses, strip them and recursively parse
        String stripped = Expression.stripOuterParentheses(expr);
        if (!stripped.equals(expr)) {
            return parseExpression(stripped);
        }

        // Try unary operation first
        Optional<UnaryOperation> unary = tryParseUnary(expr);
        if (unary.isPresent()) {
            return unary;
        }

        // Try literal
        Optional<Literal> literal = tryParseLiteral(expr);
        if (literal.isPresent()) {
            return literal;
        }

        // Try variable
        return tryParseVariable(expr);
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
                return UnaryOperation.builder()
                        .operator(operator)
                        .operand(var)
                        .isPrefix(true)
                        .build();
            });
        }

        // Check for postfix unary operators (x++, x--)
        if (expr.endsWith("++") || expr.endsWith("--")) {
            String op = expr.substring(expr.length() - 2);
            String varStr = expr.substring(0, expr.length() - 2).trim();
            return tryParseVariable(varStr).map(var -> {
                UnaryOperator operator = op.equals("++") ? UnaryOperator.INCREMENT : UnaryOperator.DECREMENT;
                return UnaryOperation.builder()
                        .operator(operator)
                        .operand(var)
                        .isPrefix(false)
                        .build();
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
            return Optional.of(Literal.builder().value(value).build());
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    /**
     * Try to parse as variable
     */
    private Optional<Variable> tryParseVariable(String expr) {
        if (expr.matches("[a-zA-Z_$][a-zA-Z0-9_$]*")) {
            return Optional.of(Variable.builder().name(expr).build());
        }
        return Optional.empty();
    }
}
