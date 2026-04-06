package com.techdeveloper.calculator.dto;

public record TipCalculatorResult(double tipAmount, double total, double perPerson, String errorMessage) {
    public boolean isError() { return errorMessage != null; }

    public static TipCalculatorResult success(double tipAmount, double total, double perPerson) {
        return new TipCalculatorResult(tipAmount, total, perPerson, null);
    }

    public static TipCalculatorResult error(String message) {
        return new TipCalculatorResult(0, 0, 0, message);
    }
}
