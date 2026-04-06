package com.techdeveloper.calculator.dto;

public record ScientificCalculatorResult(String formattedResult, String errorMessage) {
    public boolean isError() { return errorMessage != null; }

    public static ScientificCalculatorResult success(String formattedResult) {
        return new ScientificCalculatorResult(formattedResult, null);
    }

    public static ScientificCalculatorResult error(String message) {
        return new ScientificCalculatorResult(null, message);
    }
}
