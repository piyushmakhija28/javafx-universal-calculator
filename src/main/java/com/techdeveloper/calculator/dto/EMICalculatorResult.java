package com.techdeveloper.calculator.dto;

public record EMICalculatorResult(double monthlyEMI, double totalInterest, double totalAmount, String errorMessage) {
    public boolean isError() { return errorMessage != null; }

    public static EMICalculatorResult success(double monthlyEMI, double totalInterest, double totalAmount) {
        return new EMICalculatorResult(monthlyEMI, totalInterest, totalAmount, null);
    }

    public static EMICalculatorResult error(String message) {
        return new EMICalculatorResult(0, 0, 0, message);
    }
}
