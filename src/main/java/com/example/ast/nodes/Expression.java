package com.example.ast.nodes;

import com.example.ast.ASTNode;

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
}
