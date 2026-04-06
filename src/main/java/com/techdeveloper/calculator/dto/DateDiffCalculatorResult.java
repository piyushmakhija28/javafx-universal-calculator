package com.techdeveloper.calculator.dto;

public record DateDiffCalculatorResult(long totalDays, int years, int months, int days, String errorMessage) {
    public boolean isError() { return errorMessage != null; }

    public static DateDiffCalculatorResult success(long totalDays, int years, int months, int days) {
        return new DateDiffCalculatorResult(totalDays, years, months, days, null);
    }

    public static DateDiffCalculatorResult error(String message) {
        return new DateDiffCalculatorResult(0, 0, 0, 0, message);
    }
}
