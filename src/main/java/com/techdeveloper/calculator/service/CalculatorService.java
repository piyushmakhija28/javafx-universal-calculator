package com.techdeveloper.calculator.service;

import java.util.Map;

/**
 * Universal interface for all calculator service implementations.
 * Every calculator type implements this interface.
 * The calculate() method accepts named inputs and returns a formatted result string.
 * On any error (divide-by-zero, empty input, invalid format), returns "Error: <message>"
 * and NEVER throws an exception to the calling Controller.
 */
public interface CalculatorService {

    /**
     * Perform the calculation based on the given named inputs.
     *
     * @param inputs Map of input field name to string value (e.g., "principal" to "10000")
     * @return Formatted result string, or "Error: <specific reason>" on failure
     */
    String calculate(Map<String, String> inputs);
}
