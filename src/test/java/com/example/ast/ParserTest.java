package com.example.ast;

import com.example.ast.nodes.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test suite for the Parser class.
 * Tests various scenarios using builders to construct expected AST nodes.
 */
public class ParserTest {

    private Parser parser;

    @BeforeEach
    public void setUp() {
        parser = new Parser();
    }

    /**
     * Test case record containing expression string and expected AST node
     */
    public record TestCase(String expression, Statement expected) {
        @Override
        public String toString() {
            return expression;
        }
    }

    /**
     * Produces test cases for parser validation.
     * Each test case contains an expression string and the expected AST node built using builders.
     */
    static Stream<TestCase> provideTestCases() {
        return Stream.of(
                // ==================== Simple Literals and Variables ====================
                new TestCase(
                        "x = 5",
                        Assignment.builder()
                                .variable(Variable.builder().name("x").build())
                                .value(Literal.builder().value(5.0).build())
                                .type(AssignmentType.ASSIGN)
                                .build()
                ),
                new TestCase(
                        "x = y",
                        Assignment.builder()
                                .variable(Variable.builder().name("x").build())
                                .value(Variable.builder().name("y").build())
                                .type(AssignmentType.ASSIGN)
                                .build()
                ),
                new TestCase(
                        "x = 3.14",
                        Assignment.builder()
                                .variable(Variable.builder().name("x").build())
                                .value(Literal.builder().value(3.14).build())
                                .type(AssignmentType.ASSIGN)
                                .build()
                ),

                // ==================== Binary Operations - Addition/Subtraction ====================
                new TestCase(
                        "x = a + b",
                        Assignment.builder()
                                .variable(Variable.builder().name("x").build())
                                .value(BinaryOperation.builder()
                                        .left(Variable.builder().name("a").build())
                                        .operator(BinaryOperator.ADD)
                                        .right(Variable.builder().name("b").build())
                                        .build())
                                .type(AssignmentType.ASSIGN)
                                .build()
                ),
                new TestCase(
                        "x = a - b",
                        Assignment.builder()
                                .variable(Variable.builder().name("x").build())
                                .value(BinaryOperation.builder()
                                        .left(Variable.builder().name("a").build())
                                        .operator(BinaryOperator.SUBTRACT)
                                        .right(Variable.builder().name("b").build())
                                        .build())
                                .type(AssignmentType.ASSIGN)
                                .build()
                ),
                new TestCase(
                        "x = a + b + c",
                        Assignment.builder()
                                .variable(Variable.builder().name("x").build())
                                .value(BinaryOperation.builder()
                                        .left(BinaryOperation.builder()
                                                .left(Variable.builder().name("a").build())
                                                .operator(BinaryOperator.ADD)
                                                .right(Variable.builder().name("b").build())
                                                .build())
                                        .operator(BinaryOperator.ADD)
                                        .right(Variable.builder().name("c").build())
                                        .build())
                                .type(AssignmentType.ASSIGN)
                                .build()
                ),

                // ==================== Binary Operations - Multiplication/Division ====================
                new TestCase(
                        "x = a * b",
                        Assignment.builder()
                                .variable(Variable.builder().name("x").build())
                                .value(BinaryOperation.builder()
                                        .left(Variable.builder().name("a").build())
                                        .operator(BinaryOperator.MULTIPLY)
                                        .right(Variable.builder().name("b").build())
                                        .build())
                                .type(AssignmentType.ASSIGN)
                                .build()
                ),
                new TestCase(
                        "x = a / b",
                        Assignment.builder()
                                .variable(Variable.builder().name("x").build())
                                .value(BinaryOperation.builder()
                                        .left(Variable.builder().name("a").build())
                                        .operator(BinaryOperator.DIVIDE)
                                        .right(Variable.builder().name("b").build())
                                        .build())
                                .type(AssignmentType.ASSIGN)
                                .build()
                ),
                new TestCase(
                        "x = a % b",
                        Assignment.builder()
                                .variable(Variable.builder().name("x").build())
                                .value(BinaryOperation.builder()
                                        .left(Variable.builder().name("a").build())
                                        .operator(BinaryOperator.MODULO)
                                        .right(Variable.builder().name("b").build())
                                        .build())
                                .type(AssignmentType.ASSIGN)
                                .build()
                ),

                // ==================== Operator Precedence ====================
                new TestCase(
                        "x = a + b * c",
                        Assignment.builder()
                                .variable(Variable.builder().name("x").build())
                                .value(BinaryOperation.builder()
                                        .left(Variable.builder().name("a").build())
                                        .operator(BinaryOperator.ADD)
                                        .right(BinaryOperation.builder()
                                                .left(Variable.builder().name("b").build())
                                                .operator(BinaryOperator.MULTIPLY)
                                                .right(Variable.builder().name("c").build())
                                                .build())
                                        .build())
                                .type(AssignmentType.ASSIGN)
                                .build()
                ),
                new TestCase(
                        "x = a - b / c",
                        Assignment.builder()
                                .variable(Variable.builder().name("x").build())
                                .value(BinaryOperation.builder()
                                        .left(Variable.builder().name("a").build())
                                        .operator(BinaryOperator.SUBTRACT)
                                        .right(BinaryOperation.builder()
                                                .left(Variable.builder().name("b").build())
                                                .operator(BinaryOperator.DIVIDE)
                                                .right(Variable.builder().name("c").build())
                                                .build())
                                        .build())
                                .type(AssignmentType.ASSIGN)
                                .build()
                ),
                new TestCase(
                        "x = a * b + c / d - e % f",
                        Assignment.builder()
                                .variable(Variable.builder().name("x").build())
                                .value(BinaryOperation.builder()
                                        .left(BinaryOperation.builder()
                                                .left(BinaryOperation.builder()
                                                        .left(Variable.builder().name("a").build())
                                                        .operator(BinaryOperator.MULTIPLY)
                                                        .right(Variable.builder().name("b").build())
                                                        .build())
                                                .operator(BinaryOperator.ADD)
                                                .right(BinaryOperation.builder()
                                                        .left(Variable.builder().name("c").build())
                                                        .operator(BinaryOperator.DIVIDE)
                                                        .right(Variable.builder().name("d").build())
                                                        .build())
                                                .build())
                                        .operator(BinaryOperator.SUBTRACT)
                                        .right(BinaryOperation.builder()
                                                .left(Variable.builder().name("e").build())
                                                .operator(BinaryOperator.MODULO)
                                                .right(Variable.builder().name("f").build())
                                                .build())
                                        .build())
                                .type(AssignmentType.ASSIGN)
                                .build()
                ),
                new TestCase(
                        "x = a - b + c",
                        Assignment.builder()
                                .variable(Variable.builder().name("x").build())
                                .value(BinaryOperation.builder()
                                        .left(BinaryOperation.builder()
                                                .left(Variable.builder().name("a").build())
                                                .operator(BinaryOperator.SUBTRACT)
                                                .right(Variable.builder().name("b").build())
                                                .build())
                                        .operator(BinaryOperator.ADD)
                                        .right(Variable.builder().name("c").build())
                                        .build())
                                .type(AssignmentType.ASSIGN)
                                .build()
                ),

                // ==================== Parentheses ====================
                new TestCase(
                        "x = (a + b)",
                        Assignment.builder()
                                .variable(Variable.builder().name("x").build())
                                .value(BinaryOperation.builder()
                                        .left(Variable.builder().name("a").build())
                                        .operator(BinaryOperator.ADD)
                                        .right(Variable.builder().name("b").build())
                                        .build())
                                .type(AssignmentType.ASSIGN)
                                .build()
                ),
                new TestCase(
                        "x = (a + b) * c",
                        Assignment.builder()
                                .variable(Variable.builder().name("x").build())
                                .value(BinaryOperation.builder()
                                        .left(BinaryOperation.builder()
                                                .left(Variable.builder().name("a").build())
                                                .operator(BinaryOperator.ADD)
                                                .right(Variable.builder().name("b").build())
                                                .build())
                                        .operator(BinaryOperator.MULTIPLY)
                                        .right(Variable.builder().name("c").build())
                                        .build())
                                .type(AssignmentType.ASSIGN)
                                .build()
                ),
                new TestCase(
                        "x = ((a + b) * c)",
                        Assignment.builder()
                                .variable(Variable.builder().name("x").build())
                                .value(BinaryOperation.builder()
                                        .left(BinaryOperation.builder()
                                                .left(Variable.builder().name("a").build())
                                                .operator(BinaryOperator.ADD)
                                                .right(Variable.builder().name("b").build())
                                                .build())
                                        .operator(BinaryOperator.MULTIPLY)
                                        .right(Variable.builder().name("c").build())
                                        .build())
                                .type(AssignmentType.ASSIGN)
                                .build()
                ),
                new TestCase(
                        "x = (a + b) * (c - d)",
                        Assignment.builder()
                                .variable(Variable.builder().name("x").build())
                                .value(BinaryOperation.builder()
                                        .left(BinaryOperation.builder()
                                                .left(Variable.builder().name("a").build())
                                                .operator(BinaryOperator.ADD)
                                                .right(Variable.builder().name("b").build())
                                                .build())
                                        .operator(BinaryOperator.MULTIPLY)
                                        .right(BinaryOperation.builder()
                                                .left(Variable.builder().name("c").build())
                                                .operator(BinaryOperator.SUBTRACT)
                                                .right(Variable.builder().name("d").build())
                                                .build())
                                        .build())
                                .type(AssignmentType.ASSIGN)
                                .build()
                ),

                // ==================== Unary Operations ====================
                new TestCase(
                        "x = ++y",
                        Assignment.builder()
                                .variable(Variable.builder().name("x").build())
                                .value(UnaryOperation.builder()
                                        .operator(UnaryOperator.INCREMENT)
                                        .operand(Variable.builder().name("y").build())
                                        .isPrefix(true)
                                        .build())
                                .type(AssignmentType.ASSIGN)
                                .build()
                ),
                new TestCase(
                        "x = y++",
                        Assignment.builder()
                                .variable(Variable.builder().name("x").build())
                                .value(UnaryOperation.builder()
                                        .operator(UnaryOperator.INCREMENT)
                                        .operand(Variable.builder().name("y").build())
                                        .isPrefix(false)
                                        .build())
                                .type(AssignmentType.ASSIGN)
                                .build()
                ),
                new TestCase(
                        "x = --y",
                        Assignment.builder()
                                .variable(Variable.builder().name("x").build())
                                .value(UnaryOperation.builder()
                                        .operator(UnaryOperator.DECREMENT)
                                        .operand(Variable.builder().name("y").build())
                                        .isPrefix(true)
                                        .build())
                                .type(AssignmentType.ASSIGN)
                                .build()
                ),
                new TestCase(
                        "x = y--",
                        Assignment.builder()
                                .variable(Variable.builder().name("x").build())
                                .value(UnaryOperation.builder()
                                        .operator(UnaryOperator.DECREMENT)
                                        .operand(Variable.builder().name("y").build())
                                        .isPrefix(false)
                                        .build())
                                .type(AssignmentType.ASSIGN)
                                .build()
                ),

                // ==================== Unary with Binary Operations ====================
                new TestCase(
                        "x = ++a + b",
                        Assignment.builder()
                                .variable(Variable.builder().name("x").build())
                                .value(BinaryOperation.builder()
                                        .left(UnaryOperation.builder()
                                                .operator(UnaryOperator.INCREMENT)
                                                .operand(Variable.builder().name("a").build())
                                                .isPrefix(true)
                                                .build())
                                        .operator(BinaryOperator.ADD)
                                        .right(Variable.builder().name("b").build())
                                        .build())
                                .type(AssignmentType.ASSIGN)
                                .build()
                ),
                new TestCase(
                        "x = a++ + b",
                        Assignment.builder()
                                .variable(Variable.builder().name("x").build())
                                .value(BinaryOperation.builder()
                                        .left(UnaryOperation.builder()
                                                .operator(UnaryOperator.INCREMENT)
                                                .operand(Variable.builder().name("a").build())
                                                .isPrefix(false)
                                                .build())
                                        .operator(BinaryOperator.ADD)
                                        .right(Variable.builder().name("b").build())
                                        .build())
                                .type(AssignmentType.ASSIGN)
                                .build()
                ),
                new TestCase(
                        "x = a + ++b",
                        Assignment.builder()
                                .variable(Variable.builder().name("x").build())
                                .value(BinaryOperation.builder()
                                        .left(Variable.builder().name("a").build())
                                        .operator(BinaryOperator.ADD)
                                        .right(UnaryOperation.builder()
                                                .operator(UnaryOperator.INCREMENT)
                                                .operand(Variable.builder().name("b").build())
                                                .isPrefix(true)
                                                .build())
                                        .build())
                                .type(AssignmentType.ASSIGN)
                                .build()
                ),
                new TestCase(
                        "x = ++a * b",
                        Assignment.builder()
                                .variable(Variable.builder().name("x").build())
                                .value(BinaryOperation.builder()
                                        .left(UnaryOperation.builder()
                                                .operator(UnaryOperator.INCREMENT)
                                                .operand(Variable.builder().name("a").build())
                                                .isPrefix(true)
                                                .build())
                                        .operator(BinaryOperator.MULTIPLY)
                                        .right(Variable.builder().name("b").build())
                                        .build())
                                .type(AssignmentType.ASSIGN)
                                .build()
                ),

                // ==================== Complex Mixed Expressions ====================
                new TestCase(
                        "x = (++a + b) * c - d++",
                        Assignment.builder()
                                .variable(Variable.builder().name("x").build())
                                .value(BinaryOperation.builder()
                                        .left(BinaryOperation.builder()
                                                .left(BinaryOperation.builder()
                                                        .left(UnaryOperation.builder()
                                                                .operator(UnaryOperator.INCREMENT)
                                                                .operand(Variable.builder().name("a").build())
                                                                .isPrefix(true)
                                                                .build())
                                                        .operator(BinaryOperator.ADD)
                                                        .right(Variable.builder().name("b").build())
                                                        .build())
                                                .operator(BinaryOperator.MULTIPLY)
                                                .right(Variable.builder().name("c").build())
                                                .build())
                                        .operator(BinaryOperator.SUBTRACT)
                                        .right(UnaryOperation.builder()
                                                .operator(UnaryOperator.INCREMENT)
                                                .operand(Variable.builder().name("d").build())
                                                .isPrefix(false)
                                                .build())
                                        .build())
                                .type(AssignmentType.ASSIGN)
                                .build()
                ),
                new TestCase(
                        "x = ((a + (b * c)) - d)",
                        Assignment.builder()
                                .variable(Variable.builder().name("x").build())
                                .value(BinaryOperation.builder()
                                        .left(BinaryOperation.builder()
                                                .left(Variable.builder().name("a").build())
                                                .operator(BinaryOperator.ADD)
                                                .right(BinaryOperation.builder()
                                                        .left(Variable.builder().name("b").build())
                                                        .operator(BinaryOperator.MULTIPLY)
                                                        .right(Variable.builder().name("c").build())
                                                        .build())
                                                .build())
                                        .operator(BinaryOperator.SUBTRACT)
                                        .right(Variable.builder().name("d").build())
                                        .build())
                                .type(AssignmentType.ASSIGN)
                                .build()
                ),

                // ==================== Assignment Types ====================
                new TestCase(
                        "x += 5",
                        Assignment.builder()
                                .variable(Variable.builder().name("x").build())
                                .value(Literal.builder().value(5.0).build())
                                .type(AssignmentType.ADD_ASSIGN)
                                .build()
                ),
                new TestCase(
                        "x -= 5",
                        Assignment.builder()
                                .variable(Variable.builder().name("x").build())
                                .value(Literal.builder().value(5.0).build())
                                .type(AssignmentType.SUBTRACT_ASSIGN)
                                .build()
                ),
                new TestCase(
                        "x *= 5",
                        Assignment.builder()
                                .variable(Variable.builder().name("x").build())
                                .value(Literal.builder().value(5.0).build())
                                .type(AssignmentType.MULTIPLY_ASSIGN)
                                .build()
                ),
                new TestCase(
                        "x /= 5",
                        Assignment.builder()
                                .variable(Variable.builder().name("x").build())
                                .value(Literal.builder().value(5.0).build())
                                .type(AssignmentType.DIVIDE_ASSIGN)
                                .build()
                )
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideTestCases")
    @DisplayName("Parse expression and compare to expected AST")
    public void testParseExpression(TestCase testCase) {
        System.out.println("Testing expression: " + testCase.expression);
        System.out.flush();
        Statement actual = parser.parse(testCase.expression);
        Assignment actualAssignment = (Assignment) actual;
        System.out.println("Parsed result: " + actualAssignment.value().toString());
        System.out.flush();
        System.err.println("Testing expression: " + testCase.expression);
        System.err.println("Parsed result: " + actualAssignment.value());
        System.err.flush();
        assertStatementsEqual(testCase.expected, actual);
    }

    /**
     * Recursively compares two statements for equality
     */
    private void assertStatementsEqual(Statement expected, Statement actual) {
        assertInstanceOf(Assignment.class, actual, "Expected Assignment statement");
        Assignment expectedAssignment = (Assignment) expected;
        Assignment actualAssignment = (Assignment) actual;

        assertEquals(expectedAssignment.variable().name(), actualAssignment.variable().name(),
                "Variable names should match");
        assertEquals(expectedAssignment.type(), actualAssignment.type(),
                "Assignment types should match");
        assertExpressionsEqual(expectedAssignment.value(), actualAssignment.value());
    }

    /**
     * Recursively compares two expressions for equality
     */
    private void assertExpressionsEqual(Expression expected, Expression actual) {
        switch (expected) {
            case Literal expectedLiteral -> {
                assertInstanceOf(Literal.class, actual, "Expected Literal");
                Literal actualLiteral = (Literal) actual;
                assertEquals(expectedLiteral.value(), actualLiteral.value(),
                        "Literal values should match");
            }
            case Variable expectedVar -> {
                assertInstanceOf(Variable.class, actual, "Expected Variable");
                Variable actualVar = (Variable) actual;
                assertEquals(expectedVar.name(), actualVar.name(),
                        "Variable names should match");
            }
            case BinaryOperation expectedBinOp -> {
                assertInstanceOf(BinaryOperation.class, actual, "Expected BinaryOperation");
                BinaryOperation actualBinOp = (BinaryOperation) actual;
                assertEquals(expectedBinOp.operator(), actualBinOp.operator(),
                        "Binary operators should match");
                assertExpressionsEqual(expectedBinOp.left(), actualBinOp.left());
                assertExpressionsEqual(expectedBinOp.right(), actualBinOp.right());
            }
            case UnaryOperation expectedUnaryOp -> {
                assertInstanceOf(UnaryOperation.class, actual, "Expected UnaryOperation");
                UnaryOperation actualUnaryOp = (UnaryOperation) actual;
                assertEquals(expectedUnaryOp.operator(), actualUnaryOp.operator(),
                        "Unary operators should match");
                assertEquals(expectedUnaryOp.isPrefix(), actualUnaryOp.isPrefix(),
                        "Unary operation prefix/postfix should match");
                assertEquals(expectedUnaryOp.operand().name(), actualUnaryOp.operand().name(),
                        "Unary operation operand names should match");
            }
            default -> fail("Unexpected expression type: " + expected.getClass().getSimpleName());
        }
    }

    // ==================== Negative Test Cases ====================

    /**
     * Test case for invalid expressions that should throw exceptions
     */
    public record NegativeTestCase(String expression, Class<? extends Exception> expectedException, String description) {
        @Override
        public String toString() {
            return description;
        }
    }

    /**
     * Provides negative test cases for parser validation
     */
    static Stream<NegativeTestCase> provideNegativeTestCases() {
        return Stream.of(
                // ==================== Invalid Syntax ====================
                new NegativeTestCase(
                        "",
                        IllegalArgumentException.class,
                        "Empty string"
                ),
                new NegativeTestCase(
                        "   ",
                        IllegalArgumentException.class,
                        "Whitespace only"
                ),
                new NegativeTestCase(
                        "x",
                        IllegalArgumentException.class,
                        "Missing assignment operator"
                ),
                new NegativeTestCase(
                        "= 5",
                        IllegalArgumentException.class,
                        "Missing variable name"
                ),
                new NegativeTestCase(
                        "x =",
                        IllegalArgumentException.class,
                        "Missing right-hand side"
                ),
                new NegativeTestCase(
                        "x = ",
                        IllegalArgumentException.class,
                        "Missing expression (whitespace only)"
                ),
                // Note: "5 = x" is not caught by parser regex - would require semantic validation

                // ==================== Incomplete Expressions ====================
                new NegativeTestCase(
                        "x = +",
                        IllegalArgumentException.class,
                        "Operator without operands"
                ),
                new NegativeTestCase(
                        "x = 5 +",
                        IllegalArgumentException.class,
                        "Missing right operand"
                ),
                new NegativeTestCase(
                        "x = + 5",
                        IllegalArgumentException.class,
                        "Missing left operand"
                ),
                new NegativeTestCase(
                        "x = 5 + + 3",
                        IllegalArgumentException.class,
                        "Consecutive operators"
                ),
                new NegativeTestCase(
                        "x = * 5",
                        IllegalArgumentException.class,
                        "Operator at start of expression"
                ),
                new NegativeTestCase(
                        "x = 5 * * 3",
                        IllegalArgumentException.class,
                        "Double multiplication operator"
                ),

                // ==================== Unmatched Parentheses ====================
                new NegativeTestCase(
                        "x = (5 + 3",
                        IllegalArgumentException.class,
                        "Unmatched opening parenthesis"
                ),
                new NegativeTestCase(
                        "x = 5 + 3)",
                        IllegalArgumentException.class,
                        "Unmatched closing parenthesis"
                ),
                new NegativeTestCase(
                        "x = ((5 + 3)",
                        IllegalArgumentException.class,
                        "Multiple unmatched opening parentheses"
                ),
                new NegativeTestCase(
                        "x = (5 + 3))",
                        IllegalArgumentException.class,
                        "Multiple unmatched closing parentheses"
                ),
                new NegativeTestCase(
                        "x = ()",
                        IllegalArgumentException.class,
                        "Empty parentheses"
                ),

                // ==================== Invalid Variable Names ====================
                // Note: "123 = 5" is not caught by parser regex - would require semantic validation
                new NegativeTestCase(
                        "x-y = 5",
                        IllegalArgumentException.class,
                        "Variable name with hyphen"
                ),
                new NegativeTestCase(
                        "x.y = 5",
                        IllegalArgumentException.class,
                        "Variable name with dot"
                ),
                new NegativeTestCase(
                        "x y = 5",
                        IllegalArgumentException.class,
                        "Variable name with space"
                ),

                // ==================== Invalid Literals ====================
                new NegativeTestCase(
                        "x = 5.3.2",
                        IllegalArgumentException.class,
                        "Invalid number format (multiple dots)"
                ),
                new NegativeTestCase(
                        "x = 5a",
                        IllegalArgumentException.class,
                        "Invalid number format (letter suffix)"
                ),

                // ==================== Invalid Unary Operations ====================
                new NegativeTestCase(
                        "x = ++",
                        IllegalArgumentException.class,
                        "Increment operator without operand"
                ),
                new NegativeTestCase(
                        "x = --",
                        IllegalArgumentException.class,
                        "Decrement operator without operand"
                ),
                new NegativeTestCase(
                        "x = ++5",
                        IllegalArgumentException.class,
                        "Increment on literal"
                ),
                new NegativeTestCase(
                        "x = 5++",
                        IllegalArgumentException.class,
                        "Postfix increment on literal"
                ),
                new NegativeTestCase(
                        "x = ++++y",
                        IllegalArgumentException.class,
                        "Multiple consecutive increment operators"
                ),

                // ==================== Invalid Assignment Operators ====================
                new NegativeTestCase(
                        "x == 5",
                        IllegalArgumentException.class,
                        "Comparison operator instead of assignment"
                ),
                new NegativeTestCase(
                        "x := 5",
                        IllegalArgumentException.class,
                        "Invalid assignment operator"
                ),
                new NegativeTestCase(
                        "x =+ 5",
                        IllegalArgumentException.class,
                        "Reversed compound assignment operator"
                ),

                // ==================== Multiple Statements ====================
                new NegativeTestCase(
                        "x = 5; y = 10",
                        IllegalArgumentException.class,
                        "Multiple statements separated by semicolon"
                ),
                new NegativeTestCase(
                        "x = 5\ny = 10",
                        IllegalArgumentException.class,
                        "Multiple statements on separate lines"
                ),

                // ==================== Special Characters ====================
                new NegativeTestCase(
                        "x = 5 & 3",
                        IllegalArgumentException.class,
                        "Unsupported operator &"
                ),
                new NegativeTestCase(
                        "x = 5 | 3",
                        IllegalArgumentException.class,
                        "Unsupported operator |"
                ),
                new NegativeTestCase(
                        "x = 5 ^ 3",
                        IllegalArgumentException.class,
                        "Unsupported operator ^"
                ),
                new NegativeTestCase(
                        "x = 5 < 3",
                        IllegalArgumentException.class,
                        "Unsupported comparison operator <"
                ),
                new NegativeTestCase(
                        "x = 5 > 3",
                        IllegalArgumentException.class,
                        "Unsupported comparison operator >"
                ),
                new NegativeTestCase(
                        "x = !y",
                        IllegalArgumentException.class,
                        "Unsupported logical NOT operator"
                ),
                new NegativeTestCase(
                        "x = ~y",
                        IllegalArgumentException.class,
                        "Unsupported bitwise NOT operator"
                ),

                // ==================== Edge Cases ====================
                new NegativeTestCase(
                        "x = y =",
                        IllegalArgumentException.class,
                        "Chained assignment with missing value"
                ),
                new NegativeTestCase(
                        "x = (a + b",
                        IllegalArgumentException.class,
                        "Missing closing parenthesis with expression"
                ),
                new NegativeTestCase(
                        "x = a + (b * c",
                        IllegalArgumentException.class,
                        "Nested parentheses missing closing"
                ),
                new NegativeTestCase(
                        "x = / 5",
                        IllegalArgumentException.class,
                        "Division operator at start"
                ),
                new NegativeTestCase(
                        "x = 5 /",
                        IllegalArgumentException.class,
                        "Division operator at end"
                ),
                new NegativeTestCase(
                        "x = % 5",
                        IllegalArgumentException.class,
                        "Modulo operator at start"
                )
        );
    }

    @ParameterizedTest(name = "{0}")
    @MethodSource("provideNegativeTestCases")
    @DisplayName("Parse invalid expression and expect exception")
    public void testParseInvalidExpression(NegativeTestCase testCase) {
        Exception exception = assertThrows(
                testCase.expectedException,
                () -> parser.parse(testCase.expression), "Expected " + testCase.expectedException.getSimpleName() + " for: " + testCase.description);

        // Optionally verify the exception message contains useful information
        assertNotNull(exception.getMessage(), "Exception should have a message");
        assertFalse(exception.getMessage().isEmpty(), "Exception message should not be empty");
    }
}
