package com.techdeveloper.calculator.dto;

public record BMICalculatorResult(double bmi, String category, String errorMessage) {
    public boolean isError() { return errorMessage != null; }

    public static BMICalculatorResult success(double bmi, String category) {
        return new BMICalculatorResult(bmi, category, null);
    }

    public static BMICalculatorResult error(String message) {
        return new BMICalculatorResult(0, null, message);
    }
}
