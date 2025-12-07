package com.example.ast;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Comprehensive test suite for the Evaluator class.
 * Tests various scenarios by evaluating expressions and comparing the heap's toString output.
 */
public class EvaluatorTest {

    /**
     * Test case record containing expression string(s) and expected heap state
     */
    record TestCase(String[] expressions, String expectedHeap) {
        // Constructor for single expression
        TestCase(String expression, String expectedHeap) {
            this(new String[]{expression}, expectedHeap);
        }

        @Override
        public String toString() {
            return String.join("; ", expressions);
        }
    }

    /**
     * Provides test cases for evaluator validation.
     */
    static Stream<TestCase> provideTestCases() {
        return Stream.of(
                // ==================== Simple Assignments ====================
                new TestCase("x = 5", "(x=5)"),
                new TestCase("x = 3.14", "(x=3.14)"),
                new TestCase("x = 10", "(x=10)"),

                // ==================== Multiple Variables ====================
                new TestCase(
                        new String[]{"x = 5", "y = 10"},
                        "(x=5,y=10)"
                ),
                new TestCase(
                        new String[]{"a = 1", "b = 2", "c = 3"},
                        "(a=1,b=2,c=3)"
                ),
                new TestCase(
                        new String[]{"i = 82", "j = 1", "x = 6", "y = 80"},
                        "(i=82,j=1,x=6,y=80)"
                ),

                // ==================== Binary Operations - Addition/Subtraction ====================
                new TestCase("x = 5 + 3", "(x=8)"),
                new TestCase("x = 10 - 4", "(x=6)"),
                new TestCase("x = 1 + 2 + 3", "(x=6)"),
                new TestCase("x = 10 - 3 - 2", "(x=5)"),  // Left-associative: (10 - 3) - 2
                new TestCase("x = 5 + 3 - 2", "(x=6)"),

                // ==================== Binary Operations - Multiplication/Division/Modulo ====================
                new TestCase("x = 5 * 3", "(x=15)"),
                new TestCase("x = 20 / 4", "(x=5)"),
                new TestCase("x = 10 % 3", "(x=1)"),
                new TestCase("x = 2 * 3 * 4", "(x=24)"),

                // ==================== Operator Precedence ====================
                new TestCase("x = 2 + 3 * 4", "(x=14)"),
                new TestCase("x = 10 - 6 / 2", "(x=7)"),
                new TestCase("x = 2 * 3 + 4 * 5", "(x=26)"),
                new TestCase("x = 100 / 10 + 5 * 2", "(x=20)"),
                new TestCase("x = 5 + 10 % 3", "(x=6)"),

                // ==================== Parentheses ====================
                new TestCase("x = (5 + 3)", "(x=8)"),
                new TestCase("x = (2 + 3) * 4", "(x=20)"),
                new TestCase("x = 2 * (3 + 4)", "(x=14)"),
                new TestCase("x = (10 - 2) / (3 - 1)", "(x=4)"),
                new TestCase("x = ((5 + 3) * 2) - 1", "(x=15)"),

                // ==================== Variable References ====================
                new TestCase(
                        new String[]{"x = 5", "y = x"},
                        "(x=5,y=5)"
                ),
                new TestCase(
                        new String[]{"x = 10", "y = x + 5"},
                        "(x=10,y=15)"
                ),
                new TestCase(
                        new String[]{"a = 2", "b = 3", "c = a + b"},
                        "(a=2,b=3,c=5)"
                ),
                new TestCase(
                        new String[]{"x = 5", "y = 10", "z = x * y"},
                        "(x=5,y=10,z=50)"
                ),
                new TestCase(
                        new String[]{"a = 10", "b = a / 2", "c = b * 3"},
                        "(a=10,b=5,c=15)"
                ),

                // ==================== Compound Assignments ====================
                new TestCase(
                        new String[]{"x = 10", "x += 5"},
                        "(x=15)"
                ),
                new TestCase(
                        new String[]{"x = 20", "x -= 7"},
                        "(x=13)"
                ),
                new TestCase(
                        new String[]{"x = 5", "x *= 3"},
                        "(x=15)"
                ),
                new TestCase(
                        new String[]{"x = 20", "x /= 4"},
                        "(x=5)"
                ),
                new TestCase(
                        new String[]{"x = 10", "x += 5", "x *= 2"},
                        "(x=30)"
                ),

                // ==================== Unary Operations - Increment/Decrement ====================
                new TestCase(
                        new String[]{"x = 5", "y = ++x"},
                        "(x=6,y=6)"
                ),
                new TestCase(
                        new String[]{"x = 5", "y = x++"},
                        "(x=6,y=5)"
                ),
                new TestCase(
                        new String[]{"x = 10", "y = --x"},
                        "(x=9,y=9)"
                ),
                new TestCase(
                        new String[]{"x = 10", "y = x--"},
                        "(x=9,y=10)"
                ),
                new TestCase(
                        new String[]{"x = 5", "y = ++x + 10"},
                        "(x=6,y=16)"
                ),
                new TestCase(
                        new String[]{"x = 5", "y = x++ + 10"},
                        "(x=6,y=15)"
                ),

                // ==================== Complex Expressions ====================
                new TestCase(
                        new String[]{"a = 2", "b = 3", "c = 4", "x = a + b * c"},
                        "(a=2,b=3,c=4,x=14)"
                ),
                new TestCase(
                        new String[]{"a = 10", "b = 20", "c = a + b", "d = c * 2"},
                        "(a=10,b=20,c=30,d=60)"
                ),
                new TestCase(
                        new String[]{"x = 5", "x += 3", "y = x * 2", "z = y - 4"},
                        "(x=8,y=16,z=12)"
                ),
                new TestCase(
                        new String[]{"a = 100", "b = a / 10", "c = b + 5", "d = c * 2 - 3"},
                        "(a=100,b=10,c=15,d=27)"
                ),

                // ==================== Decimal Results ====================
                new TestCase("x = 10 / 4", "(x=2.5)"),
                new TestCase("x = 7 / 2", "(x=3.5)"),
                new TestCase(
                        new String[]{"a = 5", "b = 2", "c = a / b"},
                        "(a=5,b=2,c=2.5)"
                ),

                // ==================== Variable Reassignment ====================
                new TestCase(
                        new String[]{"x = 5", "x = 10"},
                        "(x=10)"
                ),
                new TestCase(
                        new String[]{"x = 5", "y = x", "x = 20"},
                        "(x=20,y=5)"
                ),
                new TestCase(
                        new String[]{"x = 1", "x = x + 1", "x = x + 1"},
                        "(x=3)"
                ),
                new TestCase(
                        new String[]{"i = 0", "j = ++i", "x = i++ + 5", "y = (5 + 3) * 10", "i += y"},
                        "(i=82,j=1,x=6,y=80)"
                )
        );
    }

    @ParameterizedTest
    @MethodSource("provideTestCases")
    @DisplayName("Evaluate expressions and compare heap state")
    void testEvaluateExpression(TestCase testCase) {
        // Create a new heap and evaluator for each test
        Heap heap = new Heap();
        Parser parser = new Parser();
        Evaluator evaluator = new Evaluator(heap);

        // Execute all expressions in sequence
        for (String expression : testCase.expressions) {
            var statement = parser.parse(expression);
            evaluator.execute(statement);
        }

        // Compare the heap's toString output
        assertEquals(testCase.expectedHeap, heap.toString(),
                "Heap state mismatch for expressions: " + String.join("; ", testCase.expressions));
    }
}
