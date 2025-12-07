package com.example.ast;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Environment (symbol table) for storing variable assignments across multiple lines.
 * Acts as a cache map where assignments from line X are available for line X + 1.
 */
public class Heap {
    private final Map<String, Double> variables;

    public Heap() {
        this.variables = new LinkedHashMap<>();
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

    @Override
    public String toString() {
        if (variables.isEmpty()) {
            return "()";
        }

        String content = String.join(",",
                variables.entrySet().stream()
                        .map(entry -> {
                            double value = entry.getValue();
                            // Format the value - if it's a whole number, print without decimal
                            return String.format("%s=%s", entry.getKey(),
                                    (value == (long) value)
                                            ? Long.toString((long) value)
                                            : Double.toString(value));
                        })
                        .toList());

        return String.format("(%s)", content);
    }
}
