package com.techdeveloper.calculator.service;

import java.time.Instant;

/**
 * Immutable command record capturing a completed calculation.
 * Enables history, audit, and future undo/redo support.
 * Zero JavaFX dependencies — safe for microservice extraction.
 */
public record CalculationEvent<F, R>(
        String calculatorType,
        F form,
        R result,
        Instant timestamp
) {
    public static <F, R> CalculationEvent<F, R> of(String calculatorType, F form, R result) {
        return new CalculationEvent<>(calculatorType, form, result, Instant.now());
    }
}
