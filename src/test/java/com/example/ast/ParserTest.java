package com.example.ast;

import com.example.ast.nodes.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Comprehensive test suite for the Parser class.
 * Tests various scenarios including operator precedence, parentheses, and unary operators.
 */
public class ParserTest {

    private Parser parser;

    @BeforeEach
    public void setUp() {
        parser = new Parser();
    }

    // ==================== Simple Literals and Variables ====================

    @Test
    @DisplayName("Parse simple literal assignment")
    public void testSimpleLiteral() {
        Statement stmt = parser.parse("x = 5");

        assertInstanceOf(Assignment.class, stmt);
        Assignment assignment = (Assignment) stmt;

        assertEquals("x", assignment.variable().name());
        assertEquals(AssignmentType.ASSIGN, assignment.type());
        assertInstanceOf(Literal.class, assignment.value());
        assertEquals(5.0, ((Literal) assignment.value()).value());
    }

    @Test
    @DisplayName("Parse simple variable assignment")
    public void testSimpleVariable() {
        Statement stmt = parser.parse("x = y");

        Assignment assignment = (Assignment) stmt;
        assertEquals("x", assignment.variable().name());
        assertInstanceOf(Variable.class, assignment.value());
        assertEquals("y", ((Variable) assignment.value()).name());
    }

    @Test
    @DisplayName("Parse floating point literal")
    public void testFloatingPointLiteral() {
        Statement stmt = parser.parse("x = 3.14");

        Assignment assignment = (Assignment) stmt;
        assertInstanceOf(Literal.class, assignment.value());
        assertEquals(3.14, ((Literal) assignment.value()).value());
    }

    // ==================== Binary Operations - Addition/Subtraction ====================

    @Test
    @DisplayName("Parse simple addition")
    public void testSimpleAddition() {
        Statement stmt = parser.parse("x = a + b");

        Assignment assignment = (Assignment) stmt;
        assertInstanceOf(BinaryOperation.class, assignment.value());

        BinaryOperation binOp = (BinaryOperation) assignment.value();
        assertEquals(BinaryOperator.ADD, binOp.operator());
        assertEquals("a", ((Variable) binOp.left()).name());
        assertEquals("b", ((Variable) binOp.right()).name());
    }

    @Test
    @DisplayName("Parse simple subtraction")
    public void testSimpleSubtraction() {
        Statement stmt = parser.parse("x = a - b");

        Assignment assignment = (Assignment) stmt;
        BinaryOperation binOp = (BinaryOperation) assignment.value();

        assertEquals(BinaryOperator.SUBTRACT, binOp.operator());
        assertEquals("a", ((Variable) binOp.left()).name());
        assertEquals("b", ((Variable) binOp.right()).name());
    }

    @Test
    @DisplayName("Parse chained addition (left associative)")
    public void testChainedAddition() {
        Statement stmt = parser.parse("x = a + b + c");

        Assignment assignment = (Assignment) stmt;
        BinaryOperation binOp = (BinaryOperation) assignment.value();

        // Should be parsed as (a + b) + c
        assertEquals(BinaryOperator.ADD, binOp.operator());
        assertInstanceOf(BinaryOperation.class, binOp.right());
        assertEquals("a", ((Variable) binOp.left()).name());

        BinaryOperation rightOp = (BinaryOperation) binOp.right();
        assertEquals(BinaryOperator.ADD, rightOp.operator());
        assertEquals("b", ((Variable) rightOp.left()).name());
        assertEquals("c", ((Variable) rightOp.right()).name());
    }

    // ==================== Binary Operations - Multiplication/Division ====================

    @Test
    @DisplayName("Parse simple multiplication")
    public void testSimpleMultiplication() {
        Statement stmt = parser.parse("x = a * b");

        Assignment assignment = (Assignment) stmt;
        BinaryOperation binOp = (BinaryOperation) assignment.value();

        assertEquals(BinaryOperator.MULTIPLY, binOp.operator());
        assertEquals("a", ((Variable) binOp.left()).name());
        assertEquals("b", ((Variable) binOp.right()).name());
    }

    @Test
    @DisplayName("Parse simple division")
    public void testSimpleDivision() {
        Statement stmt = parser.parse("x = a / b");

        Assignment assignment = (Assignment) stmt;
        BinaryOperation binOp = (BinaryOperation) assignment.value();

        assertEquals(BinaryOperator.DIVIDE, binOp.operator());
        assertEquals("a", ((Variable) binOp.left()).name());
        assertEquals("b", ((Variable) binOp.right()).name());
    }

    @Test
    @DisplayName("Parse modulo operation")
    public void testModulo() {
        Statement stmt = parser.parse("x = a % b");

        Assignment assignment = (Assignment) stmt;
        BinaryOperation binOp = (BinaryOperation) assignment.value();

        assertEquals(BinaryOperator.MODULO, binOp.operator());
        assertEquals("a", ((Variable) binOp.left()).name());
        assertEquals("b", ((Variable) binOp.right()).name());
    }

    // ==================== Operator Precedence ====================

    @Test
    @DisplayName("Parse multiplication before addition")
    public void testMultiplicationBeforeAddition() {
        Statement stmt = parser.parse("x = a + b * c");

        Assignment assignment = (Assignment) stmt;
        BinaryOperation binOp = (BinaryOperation) assignment.value();

        // Should be parsed as a + (b * c)
        assertEquals(BinaryOperator.ADD, binOp.operator());
        assertEquals("a", ((Variable) binOp.left()).name());

        BinaryOperation rightOp = (BinaryOperation) binOp.right();
        assertEquals(BinaryOperator.MULTIPLY, rightOp.operator());
        assertEquals("b", ((Variable) rightOp.left()).name());
        assertEquals("c", ((Variable) rightOp.right()).name());
    }

    @Test
    @DisplayName("Parse division before subtraction")
    public void testDivisionBeforeSubtraction() {
        Statement stmt = parser.parse("x = a - b / c");

        Assignment assignment = (Assignment) stmt;
        BinaryOperation binOp = (BinaryOperation) assignment.value();

        // Should be parsed as a - (b / c)
        assertEquals(BinaryOperator.SUBTRACT, binOp.operator());
        assertEquals("a", ((Variable) binOp.left()).name());

        BinaryOperation rightOp = (BinaryOperation) binOp.right();
        assertEquals(BinaryOperator.DIVIDE, rightOp.operator());
        assertEquals("b", ((Variable) rightOp.left()).name());
        assertEquals("c", ((Variable) rightOp.right()).name());
    }

    @Test
    @DisplayName("Parse complex precedence: a * b + c / d - e % f")
    public void testComplexPrecedence() {
        Statement stmt = parser.parse("x = a * b + c / d - e % f");

        Assignment assignment = (Assignment) stmt;
        BinaryOperation binOp = (BinaryOperation) assignment.value();

        // Should be parsed as (a * b + c / d) - (e % f)
        assertEquals(BinaryOperator.SUBTRACT, binOp.operator());

        // Left side: a * b + c / d
        BinaryOperation leftAdd = (BinaryOperation) binOp.left();
        assertEquals(BinaryOperator.ADD, leftAdd.operator());

        BinaryOperation mult = (BinaryOperation) leftAdd.left();
        assertEquals(BinaryOperator.MULTIPLY, mult.operator());
        assertEquals("a", ((Variable) mult.left()).name());
        assertEquals("b", ((Variable) mult.right()).name());

        BinaryOperation div = (BinaryOperation) leftAdd.right();
        assertEquals(BinaryOperator.DIVIDE, div.operator());
        assertEquals("c", ((Variable) div.left()).name());
        assertEquals("d", ((Variable) div.right()).name());

        // Right: e % f
        BinaryOperation mod = (BinaryOperation) binOp.right();
        assertEquals(BinaryOperator.MODULO, mod.operator());
        assertEquals("e", ((Variable) mod.left()).name());
        assertEquals("f", ((Variable) mod.right()).name());
    }

    @Test
    @DisplayName("Parse complex: a - b + c")
    public void testComplexMixed2() {
        Statement stmt = parser.parse("x = a - b + c");

        Assignment assignment = (Assignment) stmt;
        BinaryOperation binOp = (BinaryOperation) assignment.value();

        // Should be parsed as a - (b + c)
        assertEquals(BinaryOperator.SUBTRACT, binOp.operator());
        assertEquals("a", ((Variable) binOp.left()).name());

        // Right: b + c
        BinaryOperation rightAdd = (BinaryOperation) binOp.right();
        assertEquals(BinaryOperator.ADD, rightAdd.operator());
        assertEquals("b", ((Variable) rightAdd.left()).name());
        assertEquals("c", ((Variable) rightAdd.right()).name());
    }

    // ==================== Parentheses ====================

    @Test
    @DisplayName("Parse simple parentheses")
    public void testSimpleParentheses() {
        Statement stmt = parser.parse("x = (a + b)");

        Assignment assignment = (Assignment) stmt;
        BinaryOperation binOp = (BinaryOperation) assignment.value();

        assertEquals(BinaryOperator.ADD, binOp.operator());
        assertEquals("a", ((Variable) binOp.left()).name());
        assertEquals("b", ((Variable) binOp.right()).name());
    }

    @Test
    @DisplayName("Parse parentheses overriding precedence")
    public void testParenthesesOverridePrecedence() {
        Statement stmt = parser.parse("x = (a + b) * c");

        Assignment assignment = (Assignment) stmt;
        BinaryOperation binOp = (BinaryOperation) assignment.value();

        // Should be parsed as (a + b) * c
        assertEquals(BinaryOperator.MULTIPLY, binOp.operator());
        assertEquals("c", ((Variable) binOp.right()).name());

        BinaryOperation leftOp = (BinaryOperation) binOp.left();
        assertEquals(BinaryOperator.ADD, leftOp.operator());
        assertEquals("a", ((Variable) leftOp.left()).name());
        assertEquals("b", ((Variable) leftOp.right()).name());
    }

    @Test
    @DisplayName("Parse nested parentheses")
    public void testNestedParentheses() {
        Statement stmt = parser.parse("x = ((a + b) * c)");

        Assignment assignment = (Assignment) stmt;
        BinaryOperation binOp = (BinaryOperation) assignment.value();

        assertEquals(BinaryOperator.MULTIPLY, binOp.operator());
        assertEquals("c", ((Variable) binOp.right()).name());

        BinaryOperation leftOp = (BinaryOperation) binOp.left();
        assertEquals(BinaryOperator.ADD, leftOp.operator());
        assertEquals("a", ((Variable) leftOp.left()).name());
        assertEquals("b", ((Variable) leftOp.right()).name());
    }

    @Test
    @DisplayName("Parse complex parentheses: (a + b) * (c - d)")
    public void testComplexParentheses() {
        Statement stmt = parser.parse("x = (a + b) * (c - d)");

        Assignment assignment = (Assignment) stmt;
        BinaryOperation binOp = (BinaryOperation) assignment.value();

        assertEquals(BinaryOperator.MULTIPLY, binOp.operator());

        BinaryOperation leftOp = (BinaryOperation) binOp.left();
        assertEquals(BinaryOperator.ADD, leftOp.operator());
        assertEquals("a", ((Variable) leftOp.left()).name());
        assertEquals("b", ((Variable) leftOp.right()).name());

        BinaryOperation rightOp = (BinaryOperation) binOp.right();
        assertEquals(BinaryOperator.SUBTRACT, rightOp.operator());
        assertEquals("c", ((Variable) rightOp.left()).name());
        assertEquals("d", ((Variable) rightOp.right()).name());
    }

    // ==================== Unary Operations ====================

    @Test
    @DisplayName("Parse prefix increment")
    public void testPrefixIncrement() {
        Statement stmt = parser.parse("x = ++y");

        Assignment assignment = (Assignment) stmt;
        assertInstanceOf(UnaryOperation.class, assignment.value());

        UnaryOperation unaryOp = (UnaryOperation) assignment.value();
        assertEquals(UnaryOperator.INCREMENT, unaryOp.operator());
        assertEquals("y", unaryOp.operand().name());
        assertTrue(unaryOp.isPrefix());
    }

    @Test
    @DisplayName("Parse postfix increment")
    public void testPostfixIncrement() {
        Statement stmt = parser.parse("x = y++");

        Assignment assignment = (Assignment) stmt;
        UnaryOperation unaryOp = (UnaryOperation) assignment.value();

        assertEquals(UnaryOperator.INCREMENT, unaryOp.operator());
        assertEquals("y", unaryOp.operand().name());
        assertFalse(unaryOp.isPrefix());
    }

    @Test
    @DisplayName("Parse prefix decrement")
    public void testPrefixDecrement() {
        Statement stmt = parser.parse("x = --y");

        Assignment assignment = (Assignment) stmt;
        UnaryOperation unaryOp = (UnaryOperation) assignment.value();

        assertEquals(UnaryOperator.DECREMENT, unaryOp.operator());
        assertEquals("y", unaryOp.operand().name());
        assertTrue(unaryOp.isPrefix());
    }

    @Test
    @DisplayName("Parse postfix decrement")
    public void testPostfixDecrement() {
        Statement stmt = parser.parse("x = y--");

        Assignment assignment = (Assignment) stmt;
        UnaryOperation unaryOp = (UnaryOperation) assignment.value();

        assertEquals(UnaryOperator.DECREMENT, unaryOp.operator());
        assertEquals("y", unaryOp.operand().name());
        assertFalse(unaryOp.isPrefix());
    }

    // ==================== Unary with Binary Operations ====================

    @Test
    @DisplayName("Parse unary with addition: ++a + b")
    public void testUnaryWithAddition() {
        Statement stmt = parser.parse("x = ++a + b");

        Assignment assignment = (Assignment) stmt;
        BinaryOperation binOp = (BinaryOperation) assignment.value();

        assertEquals(BinaryOperator.ADD, binOp.operator());

        UnaryOperation leftOp = (UnaryOperation) binOp.left();
        assertEquals(UnaryOperator.INCREMENT, leftOp.operator());
        assertEquals("a", leftOp.operand().name());
        assertTrue(leftOp.isPrefix());

        assertEquals("b", ((Variable) binOp.right()).name());
    }

    @Test
    @DisplayName("Parse postfix unary with addition: a++ + b")
    public void testPostfixUnaryWithAddition() {
        Statement stmt = parser.parse("x = a++ + b");

        Assignment assignment = (Assignment) stmt;
        BinaryOperation binOp = (BinaryOperation) assignment.value();

        assertEquals(BinaryOperator.ADD, binOp.operator());

        UnaryOperation leftOp = (UnaryOperation) binOp.left();
        assertEquals(UnaryOperator.INCREMENT, leftOp.operator());
        assertEquals("a", leftOp.operand().name());
        assertFalse(leftOp.isPrefix());

        assertEquals("b", ((Variable) binOp.right()).name());
    }

    @Test
    @DisplayName("Parse binary with unary on right: a + ++b")
    public void testBinaryWithUnaryRight() {
        Statement stmt = parser.parse("x = a + ++b");

        Assignment assignment = (Assignment) stmt;
        BinaryOperation binOp = (BinaryOperation) assignment.value();

        assertEquals(BinaryOperator.ADD, binOp.operator());
        assertEquals("a", ((Variable) binOp.left()).name());

        UnaryOperation rightOp = (UnaryOperation) binOp.right();
        assertEquals(UnaryOperator.INCREMENT, rightOp.operator());
        assertEquals("b", rightOp.operand().name());
        assertTrue(rightOp.isPrefix());
    }

    @Test
    @DisplayName("Parse unary with multiplication: ++a * b")
    public void testUnaryWithMultiplication() {
        Statement stmt = parser.parse("x = ++a * b");

        Assignment assignment = (Assignment) stmt;
        BinaryOperation binOp = (BinaryOperation) assignment.value();

        assertEquals(BinaryOperator.MULTIPLY, binOp.operator());

        UnaryOperation leftOp = (UnaryOperation) binOp.left();
        assertEquals(UnaryOperator.INCREMENT, leftOp.operator());
        assertEquals("a", leftOp.operand().name());
    }

    // ==================== Complex Mixed Expressions ====================

    @Test
    @DisplayName("Parse complex: (++a + b) * c - d++")
    public void testComplexMixed1() {
        Statement stmt = parser.parse("x = (++a + b) * c - d++");

        Assignment assignment = (Assignment) stmt;
        BinaryOperation binOp = (BinaryOperation) assignment.value();

        // Top level: ... - d++
        assertEquals(BinaryOperator.SUBTRACT, binOp.operator());

        UnaryOperation rightUnary = (UnaryOperation) binOp.right();
        assertEquals(UnaryOperator.INCREMENT, rightUnary.operator());
        assertEquals("d", rightUnary.operand().name());
        assertFalse(rightUnary.isPrefix());

        // Left: (++a + b) * c
        BinaryOperation leftMult = (BinaryOperation) binOp.left();
        assertEquals(BinaryOperator.MULTIPLY, leftMult.operator());
        assertEquals("c", ((Variable) leftMult.right()).name());

        // Inner: ++a + b
        BinaryOperation innerAdd = (BinaryOperation) leftMult.left();
        assertEquals(BinaryOperator.ADD, innerAdd.operator());

        UnaryOperation prefixUnary = (UnaryOperation) innerAdd.left();
        assertEquals(UnaryOperator.INCREMENT, prefixUnary.operator());
        assertEquals("a", prefixUnary.operand().name());
        assertTrue(prefixUnary.isPrefix());

        assertEquals("b", ((Variable) innerAdd.right()).name());
    }

    @Test
    @DisplayName("Parse deeply nested parentheses: ((a + (b * c)) - d)")
    public void testDeeplyNestedParentheses() {
        Statement stmt = parser.parse("x = ((a + (b * c)) - d)");

        Assignment assignment = (Assignment) stmt;
        BinaryOperation binOp = (BinaryOperation) assignment.value();

        assertEquals(BinaryOperator.SUBTRACT, binOp.operator());
        assertEquals("d", ((Variable) binOp.right()).name());

        BinaryOperation leftAdd = (BinaryOperation) binOp.left();
        assertEquals(BinaryOperator.ADD, leftAdd.operator());
        assertEquals("a", ((Variable) leftAdd.left()).name());

        BinaryOperation mult = (BinaryOperation) leftAdd.right();
        assertEquals(BinaryOperator.MULTIPLY, mult.operator());
        assertEquals("b", ((Variable) mult.left()).name());
        assertEquals("c", ((Variable) mult.right()).name());
    }

    // ==================== Assignment Types ====================

    @Test
    @DisplayName("Parse add-assign")
    public void testAddAssign() {
        Statement stmt = parser.parse("x += 5");

        Assignment assignment = (Assignment) stmt;
        assertEquals(AssignmentType.ADD_ASSIGN, assignment.type());
        assertEquals(5.0, ((Literal) assignment.value()).value());
    }

    @Test
    @DisplayName("Parse subtract-assign")
    public void testSubtractAssign() {
        Statement stmt = parser.parse("x -= 5");

        Assignment assignment = (Assignment) stmt;
        assertEquals(AssignmentType.SUBTRACT_ASSIGN, assignment.type());
    }

    @Test
    @DisplayName("Parse multiply-assign")
    public void testMultiplyAssign() {
        Statement stmt = parser.parse("x *= 5");

        Assignment assignment = (Assignment) stmt;
        assertEquals(AssignmentType.MULTIPLY_ASSIGN, assignment.type());
    }

    @Test
    @DisplayName("Parse divide-assign")
    public void testDivideAssign() {
        Statement stmt = parser.parse("x /= 5");

        Assignment assignment = (Assignment) stmt;
        assertEquals(AssignmentType.DIVIDE_ASSIGN, assignment.type());
    }
}
