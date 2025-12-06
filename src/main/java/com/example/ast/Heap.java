package com.example.ast;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Environment (symbol table) for storing variable assignments across multiple lines.
 * Acts as a cache map where assignments from line X are available for line X + 1.
 */
public class Heap {
    private final Map<String, Double> variables;

    public Heap() {
        this.variables = new HashMap<>();
    }

    /**
     * Store a variable assignment
     */
    public void set(String name, double value) {
        variables.put(name, value);
    }

    /**
     * Retrieve a variable's value
     */
    public Optional<Double> get(String name) {
        return Optional.ofNullable(variables.get(name));
    }

    /**
     * Check if a variable is defined
     */
    public boolean has(String name) {
        return variables.containsKey(name);
    }

    /**
     * Clear all variables
     */
    public void clear() {
        variables.clear();
    }

    /**
     * Get a copy of all variables (for debugging/inspection)
     */
    public Map<String, Double> getAll() {
        return new HashMap<>(variables);
    }
}
