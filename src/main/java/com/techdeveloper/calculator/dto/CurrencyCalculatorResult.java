package com.techdeveloper.calculator.dto;

public record CurrencyCalculatorResult(double convertedAmount, double rate, String fromCode, String toCode, String errorMessage) {
    public boolean isError() { return errorMessage != null; }

    public static CurrencyCalculatorResult success(double convertedAmount, double rate, String fromCode, String toCode) {
        return new CurrencyCalculatorResult(convertedAmount, rate, fromCode, toCode, null);
    }

    public static CurrencyCalculatorResult error(String message) {
        return new CurrencyCalculatorResult(0, 0, null, null, message);
    }
}
