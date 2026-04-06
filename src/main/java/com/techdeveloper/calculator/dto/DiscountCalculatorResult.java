package com.techdeveloper.calculator.dto;

public record DiscountCalculatorResult(double discountAmount, double finalPrice, String errorMessage) {
    public boolean isError() { return errorMessage != null; }

    public static DiscountCalculatorResult success(double discountAmount, double finalPrice) {
        return new DiscountCalculatorResult(discountAmount, finalPrice, null);
    }

    public static DiscountCalculatorResult error(String message) {
        return new DiscountCalculatorResult(0, 0, message);
    }
}
