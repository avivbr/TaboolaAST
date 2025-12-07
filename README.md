# TaboolaAST

A Java-based Abstract Syntax Tree (AST) parser and evaluator for assignment statements with support for arithmetic operations, compound assignments, and unary operators.

## Features

- **Assignment Statements**: Parse and evaluate variable assignments
  - Simple assignment: `x = 5`
  - Compound assignments: `+=`, `-=`, `*=`, `/=`
  
- **Arithmetic Operations**: Support for binary operations with proper operator precedence
  - Addition (`+`), Subtraction (`-`)
  - Multiplication (`*`), Division (`/`), Modulo (`%`)
  
- **Unary Operations**: Prefix and postfix increment/decrement
  - Prefix: `++x`, `--x`
  - Postfix: `x++`, `x--`
  
- **Expression Grouping**: Parentheses for explicit precedence control
  
- **Variable State Management**: Heap-based storage that maintains variable values across multiple statements

- **Operator Precedence**: Correct parsing order following standard mathematical rules
  - Parentheses → Unary → Binary (multiplicative before additive)

## Requirements

- Java 21 or higher
- Gradle 7.x or higher

## Building the Project

```bash
# Build the project
./gradlew build

# Run tests
./gradlew test

# Clean build artifacts
./gradlew clean
```

## Usage

### Basic Example

```java
import com.example.ast.*;

// Create a heap to store variables
Heap heap = new Heap();

// Create parser and evaluator
Parser parser = new Parser();
Evaluator evaluator = new Evaluator(heap);

// Parse and execute statements
Statement stmt1 = parser.parse("x = 5");
evaluator.execute(stmt1);

Statement stmt2 = parser.parse("y = x + 10");
evaluator.execute(stmt2);

// Access variable values
double xValue = heap.get("x").orElse(0.0);
double yValue = heap.get("y").orElse(0.0);
```

### Supported Syntax Examples

```java
// Simple assignments
"x = 5"
"y = 3.14"
"z = x"

// Binary operations
"x = a + b"
"x = a - b * c"
"x = (a + b) * c"

// Compound assignments
"x += 5"
"x -= 10"
"x *= 2"
"x /= 3"

// Unary operations
"x = ++y"
"x = y++"
"x = --z"
"x = z--"

// Complex expressions
"result = (a + b) * (c - d) / 2"
"x = a + b * c - d / e"
```

## Project Structure

```
src/
├── main/java/com/example/ast/
│   ├── Parser.java          # Parses string expressions into AST nodes
│   ├── Evaluator.java       # Evaluates AST nodes and executes statements
│   ├── Heap.java            # Variable storage and state management
│   ├── ASTNode.java         # Base interface for AST nodes
│   ├── ASTVisitor.java      # Visitor pattern interface
│   └── nodes/
│       ├── Assignment.java      # Assignment statement node
│       ├── AssignmentType.java  # Enum for assignment types
│       ├── BinaryOperation.java # Binary operation node
│       ├── BinaryOperator.java  # Enum for binary operators
│       ├── Expression.java      # Base interface for expressions
│       ├── Literal.java         # Numeric literal node
│       ├── Statement.java      # Base interface for statements
│       ├── UnaryOperation.java # Unary operation node
│       ├── UnaryOperator.java  # Enum for unary operators
│       └── Variable.java       # Variable reference node
└── test/java/com/example/ast/
    ├── ParserTest.java      # Comprehensive parser tests
    └── EvaluatorTest.java   # Comprehensive evaluator tests
```

## Testing

The project includes comprehensive test suites:

- **ParserTest**: Tests parsing of various expression types and edge cases
- **EvaluatorTest**: Tests evaluation logic, operator precedence, and state management

Run all tests:
```bash
./gradlew test
```

View test reports:
```bash
# HTML reports are generated in build/reports/tests/test/index.html
open build/reports/tests/test/index.html
```

## Architecture

The project follows a visitor pattern architecture:

1. **Parser**: Converts string input into an AST representation
2. **AST Nodes**: Immutable data structures representing the parsed code
3. **Evaluator**: Implements the visitor pattern to traverse and evaluate AST nodes
4. **Heap**: Maintains variable state across multiple statement executions

## Error Handling

The parser and evaluator throw appropriate exceptions:

- `IllegalArgumentException`: Invalid syntax or parsing errors
- `RuntimeException`: Undefined variable access
- `ArithmeticException`: Division by zero

## License

This project is provided as-is for educational and demonstration purposes.

