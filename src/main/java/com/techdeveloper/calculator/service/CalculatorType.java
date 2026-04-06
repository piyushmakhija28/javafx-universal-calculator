package com.techdeveloper.calculator.service;

/**
 * Enum representing every supported calculator type.
 * Used by ServiceFactory to return the correct service instance.
 */
public enum CalculatorType {
    BASIC,
    SCIENTIFIC,
    PROGRAMMER,
    EMI,
    BMI,
    AGE,
    DATE_DIFF,
    CURRENCY,
    UNIT_CONVERTER,
    TIP,
    DISCOUNT,
    MATRIX,
    STATISTICS,
    SPEED,
    FUEL
}
