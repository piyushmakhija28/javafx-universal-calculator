package com.techdeveloper.calculator.service;

/**
 * Generic typed interface for all calculator service implementations.
 * F — the form (input record) type.
 * R — the result (DTO) type.
 */
public interface CalculatorService<F, R> {

    /**
     * Perform the calculation based on the given typed form.
     *
     * @param form the typed input record carrying all required fields
     * @return typed result DTO; call result.isError() to check for failures
     */
    R calculate(F form);
}
