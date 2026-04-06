package com.techdeveloper.calculator.dto;

public record FuelCalculatorResult(
        double per100km, double kmPerLiter,
        Double costPerKm, Double totalCost,
        String errorMessage) {
    public boolean isError() { return errorMessage != null; }

    public static FuelCalculatorResult success(double per100km, double kmPerLiter,
                                               Double costPerKm, Double totalCost) {
        return new FuelCalculatorResult(per100km, kmPerLiter, costPerKm, totalCost, null);
    }

    public static FuelCalculatorResult error(String message) {
        return new FuelCalculatorResult(0, 0, null, null, message);
    }
}
