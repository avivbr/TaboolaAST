package com.example.ast.nodes;

import com.example.ast.ASTNode;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Interface for all expression nodes that can be evaluated to a value
 */
public interface Expression extends ASTNode {

    /**
     * Removes outermost matching parentheses and returns the inner expression string
     * @param str the string to process
     * @return the string without outermost parentheses, or the original if no matching outer parens
     */
    static String stripOuterParentheses(String str) {
        if (str == null || str.isBlank()) {
            return str;
        }
        String trimmed = str.trim();

        // Check if starts and ends with parentheses
        if (trimmed.startsWith("(") && trimmed.endsWith(")")) {
            // Verify they are matching pairs
            int depth = 0;
            for (int i = 0; i < trimmed.length(); i++) {
                char c = trimmed.charAt(i);
                if (c == '(') {
                    depth++;
                } else if (c == ')') {
                    depth--;
                }
                // If depth reaches 0 before the end, outer parens don't match
                if (depth == 0 && i < trimmed.length() - 1) {
                    return trimmed;
                }
            }
            // Outer parentheses match, strip them
            return trimmed.substring(1, trimmed.length() - 1).trim();
        }
        return trimmed;
    }

    /**
     * Removes all parenthesized expressions from the string, replacing them with placeholders
     * This allows operator scanning without worrying about parenthesis depth
     * @param str the string to process
     * @return the string with parenthesized expressions removed
     */
    static String removeParenthesizedSegments(String str) {
        if (str == null || str.isBlank()) {
            return str;
        }

        StringBuilder result = new StringBuilder();
        int depth = 0;

        for (char c : str.toCharArray()) {
            if (c == '(') {
                depth++;
                result.append(' '); // Replace with space
            } else if (c == ')') {
                depth--;
                result.append(' '); // Replace with space
            } else if (depth == 0) {
                result.append(c);
            } else {
                result.append(' '); // Replace content inside parens with space
            }
        }

        return result.toString();
    }

    /**
     * Recursively strips all layers of outer parentheses
     * @param str the string to process
     * @param parser the parser function to apply after stripping parentheses
     * @return the parsed expression
     */
    static Expression parseWithParentheses(String str, Function<String, Expression> parser) {
        if (str == null || str.isBlank()) {
            return null;
        }

        String stripped = stripOuterParentheses(str);
        // Keep stripping until no more outer parentheses
        while (!stripped.equals(str)) {
            str = stripped;
            stripped = stripOuterParentheses(str);
        }

        return parser.apply(str);
    }
}
