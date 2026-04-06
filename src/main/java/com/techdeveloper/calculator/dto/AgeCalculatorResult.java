package com.techdeveloper.calculator.dto;

public record AgeCalculatorResult(int years, int months, int days, String errorMessage) {
    public boolean isError() { return errorMessage != null; }

    public static AgeCalculatorResult success(int years, int months, int days) {
        return new AgeCalculatorResult(years, months, days, null);
    }

    public static AgeCalculatorResult error(String message) {
        return new AgeCalculatorResult(0, 0, 0, message);
    }
}
