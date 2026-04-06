package com.techdeveloper.calculator.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Immutable record representing a single calculation history entry.
 *
 * Fields:
 *   calculatorType — e.g. "Basic", "EMI", "BMI"
 *   inputs         — human-readable input summary, e.g. "P=10000, R=8.5, N=12"
 *   result         — the service result string
 *   timestamp      — when the calculation was performed
 *
 * toString() format: "[HH:mm] calculatorType | inputs = result"
 */
public final class HistoryEntry {

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("HH:mm");

    private final String        calculatorType;
    private final String        inputs;
    private final String        result;
    private final LocalDateTime timestamp;

    public HistoryEntry(String calculatorType, String inputs, String result) {
        this(calculatorType, inputs, result, LocalDateTime.now());
    }

    public HistoryEntry(String calculatorType, String inputs, String result, LocalDateTime timestamp) {
        this.calculatorType = calculatorType;
        this.inputs         = inputs;
        this.result         = result;
        this.timestamp      = timestamp;
    }

    public String getCalculatorType() { return calculatorType; }
    public String getInputs()         { return inputs; }
    public String getResult()         { return result; }
    public LocalDateTime getTimestamp() { return timestamp; }

    /**
     * Human-readable single-line representation for the history ListView cell.
     * Format: "[HH:mm] CalculatorType | inputs = result"
     */
    @Override
    public String toString() {
        return "[" + TIME_FMT.format(timestamp) + "] "
             + calculatorType + " | "
             + inputs + " = " + result;
    }
}
