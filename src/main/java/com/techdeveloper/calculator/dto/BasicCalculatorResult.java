package com.techdeveloper.calculator.dto;

public record BasicCalculatorResult(String formattedResult, String errorMessage) {
    public boolean isError() { return errorMessage != null; }

    public static BasicCalculatorResult success(String formattedResult) {
        return new BasicCalculatorResult(formattedResult, null);
    }

    public static BasicCalculatorResult error(String message) {
        return new BasicCalculatorResult(null, message);
    }
}
