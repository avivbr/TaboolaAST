package com.example.ast;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test suite for error handling in the Evaluator class.
 * Tests various runtime error scenarios that should throw exceptions.
 */
public class EvaluatorErrorTest {

    /**
     * Test case record containing expression string(s), expected exception type, and description
     */
    public record ErrorTestCase(String[] expressions, Class<? extends Exception> expectedException, String description) {
        // Constructor for single expression
        ErrorTestCase(String expression, Class<? extends Exception> expectedException, String description) {
            this(new String[]{expression}, expectedException, description);
        }

        @Override
        public String toString() {
            return description;
        }
    }

    /**
     * Provides error test cases for evaluator validation.
     */
    static Stream<ErrorTestCase> provideErrorTestCases() {
        return Stream.of(
                // ==================== Undefined Variable - Simple Assignment ====================
                new ErrorTestCase(
                        "x = y",
                        RuntimeException.class,
                        "Undefined variable in simple assignment"
                ),
                new ErrorTestCase(
                        "x = undefinedVar",
                        RuntimeException.class,
                        "Undefined variable with descriptive name"
                ),

                // ==================== Undefined Variable - Binary Operations ====================
                new ErrorTestCase(
                        "x = a + 5",
                        RuntimeException.class,
                        "Undefined variable on left side of addition"
                ),
                new ErrorTestCase(
                        "x = 5 + b",
                        RuntimeException.class,
                        "Undefined variable on right side of addition"
                ),
                new ErrorTestCase(
                        "x = a + b",
                        RuntimeException.class,
                        "Both operands undefined in addition"
                ),
                new ErrorTestCase(
                        "x = a - 5",
                        RuntimeException.class,
                        "Undefined variable in subtraction"
                ),
                new ErrorTestCase(
                        "x = a * 5",
                        RuntimeException.class,
                        "Undefined variable in multiplication"
                ),
                new ErrorTestCase(
                        "x = a / 5",
                        RuntimeException.class,
                        "Undefined variable in division"
                ),
                new ErrorTestCase(
                        "x = a % 5",
                        RuntimeException.class,
                        "Undefined variable in modulo"
                ),

                // ==================== Undefined Variable - Nested Expressions ====================
                new ErrorTestCase(
                        "x = (a + 5) * 3",
                        RuntimeException.class,
                        "Undefined variable in parenthesized expression"
                ),
                new ErrorTestCase(
                        "x = (5 + a) * b",
                        RuntimeException.class,
                        "Multiple undefined variables in nested expression"
                ),
                new ErrorTestCase(
                        "x = a + b * c",
                        RuntimeException.class,
                        "Undefined variables in complex expression"
                ),
                new ErrorTestCase(
                        new String[]{"x = 5", "y = x + undefined"},
                        RuntimeException.class,
                        "Undefined variable in expression after valid assignment"
                ),

                // ==================== Undefined Variable - Variable Reference Chain ====================
                new ErrorTestCase(
                        new String[]{"x = y", "y = z"},
                        RuntimeException.class,
                        "Chain of undefined variables"
                ),
                new ErrorTestCase(
                        new String[]{"x = a + b", "a = 5"},
                        RuntimeException.class,
                        "Undefined variable used before definition"
                ),

                // ==================== Division by Zero - Direct Literal ====================
                new ErrorTestCase(
                        "x = 5 / 0",
                        ArithmeticException.class,
                        "Division by zero literal"
                ),
                new ErrorTestCase(
                        "x = 10 / 0",
                        ArithmeticException.class,
                        "Division by zero with larger numerator"
                ),
                new ErrorTestCase(
                        "x = 0 / 0",
                        ArithmeticException.class,
                        "Zero divided by zero"
                ),
                new ErrorTestCase(
                        "x = -5 / 0",
                        ArithmeticException.class,
                        "Negative number divided by zero"
                ),

                // ==================== Division by Zero - Variable ====================
                new ErrorTestCase(
                        new String[]{"y = 0", "x = 5 / y"},
                        ArithmeticException.class,
                        "Division by zero variable"
                ),
                new ErrorTestCase(
                        new String[]{"y = 0", "x = 10 / y"},
                        ArithmeticException.class,
                        "Division by zero variable with larger numerator"
                ),
                new ErrorTestCase(
                        new String[]{"a = 5", "b = 0", "x = a / b"},
                        ArithmeticException.class,
                        "Division by zero in multi-variable expression"
                ),

                // ==================== Division by Zero - Nested Expressions ====================
                new ErrorTestCase(
                        new String[]{"y = 0", "x = (5 + 3) / y"},
                        ArithmeticException.class,
                        "Division by zero in parenthesized expression"
                ),
                new ErrorTestCase(
                        new String[]{"y = 0", "x = 10 / (y + 0)"},
                        ArithmeticException.class,
                        "Division by zero in nested parentheses"
                ),
                new ErrorTestCase(
                        new String[]{"a = 5", "b = 0", "c = 3", "x = (a + c) / b"},
                        ArithmeticException.class,
                        "Division by zero in complex nested expression"
                ),

                // ==================== Division by Zero - Compound Assignment ====================
                new ErrorTestCase(
                        new String[]{"x = 10", "x /= 0"},
                        ArithmeticException.class,
                        "Division by zero in compound assignment"
                ),
                new ErrorTestCase(
                        new String[]{"x = 20", "y = 0", "x /= y"},
                        ArithmeticException.class,
                        "Division by zero in compound assignment with variable"
                ),

                // ==================== Compound Assignment - Undefined Variable ====================
                new ErrorTestCase(
                        "x += 5",
                        RuntimeException.class,
                        "Add assignment on undefined variable"
                ),
                new ErrorTestCase(
                        "x -= 5",
                        RuntimeException.class,
                        "Subtract assignment on undefined variable"
                ),
                new ErrorTestCase(
                        "x *= 5",
                        RuntimeException.class,
                        "Multiply assignment on undefined variable"
                ),
                new ErrorTestCase(
                        "x /= 5",
                        RuntimeException.class,
                        "Divide assignment on undefined variable"
                ),
                new ErrorTestCase(
                        new String[]{"x = 5", "y += 10"},
                        RuntimeException.class,
                        "Add assignment on undefined variable after valid assignment"
                ),

                // ==================== Unary Operations - Undefined Variable ====================
                new ErrorTestCase(
                        "x = ++y",
                        RuntimeException.class,
                        "Prefix increment on undefined variable"
                ),
                new ErrorTestCase(
                        "x = y++",
                        RuntimeException.class,
                        "Postfix increment on undefined variable"
                ),
                new ErrorTestCase(
                        "x = --y",
                        RuntimeException.class,
                        "Prefix decrement on undefined variable"
                ),
                new ErrorTestCase(
                        "x = y--",
                        RuntimeException.class,
                        "Postfix decrement on undefined variable"
                ),
                new ErrorTestCase(
                        new String[]{"x = 5", "y = ++z"},
                        RuntimeException.class,
                        "Prefix increment on undefined variable after valid assignment"
                ),
                new ErrorTestCase(
                        new String[]{"x = 5", "y = z++"},
                        RuntimeException.class,
                        "Postfix increment on undefined variable after valid assignment"
                ),

                // ==================== Unary Operations in Expressions - Undefined Variable ====================
                new ErrorTestCase(
                        "x = ++y + 5",
                        RuntimeException.class,
                        "Prefix increment on undefined variable in expression"
                ),
                new ErrorTestCase(
                        "x = 5 + ++y",
                        RuntimeException.class,
                        "Prefix increment on undefined variable as right operand"
                ),
                new ErrorTestCase(
                        "x = y++ + 5",
                        RuntimeException.class,
                        "Postfix increment on undefined variable in expression"
                ),
                new ErrorTestCase(
                        "x = (++y) * 2",
                        RuntimeException.class,
                        "Prefix increment on undefined variable in parentheses"
                ),

                // ==================== Complex Scenarios ====================
                new ErrorTestCase(
                        new String[]{"a = 5", "b = 0", "x = a / b + c"},
                        RuntimeException.class,
                        "Undefined variable after division by zero would occur"
                ),
                new ErrorTestCase(
                        new String[]{"x = a + b", "a = 5"},
                        RuntimeException.class,
                        "Undefined variable evaluated before later definition"
                ),
                new ErrorTestCase(
                        new String[]{"x = 10", "x /= 0", "y = x"},
                        ArithmeticException.class,
                        "Division by zero prevents subsequent assignment"
                )
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideErrorTestCases")
    @DisplayName("Evaluate expression and expect exception")
    void testEvaluateError(ErrorTestCase testCase) {
        // Create a new heap and evaluator for each test
        Heap heap = new Heap();
        Parser parser = new Parser();
        Evaluator evaluator = new Evaluator(heap);

        // Execute expressions and verify exception is thrown
        Exception exception = assertThrows(
                testCase.expectedException,
                () -> {
                    for (String expression : testCase.expressions) {
                        var statement = parser.parse(expression);
                        evaluator.execute(statement);
                    }
                },
                "Expected " + testCase.expectedException.getSimpleName() +
                        " for: " + testCase.description
        );

        // Verify the exception message contains useful information
        assertNotNull(exception.getMessage(), "Exception should have a message");
        assertFalse(exception.getMessage().isEmpty(), "Exception message should not be empty");
    }
}

