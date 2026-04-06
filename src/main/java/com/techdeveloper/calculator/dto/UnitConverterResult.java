package com.techdeveloper.calculator.dto;

public record UnitConverterResult(double result, String fromUnit, String toUnit, String errorMessage) {
    public boolean isError() { return errorMessage != null; }

    public static UnitConverterResult success(double result, String fromUnit, String toUnit) {
        return new UnitConverterResult(result, fromUnit, toUnit, null);
    }

    public static UnitConverterResult error(String message) {
        return new UnitConverterResult(0, null, null, message);
    }
}
