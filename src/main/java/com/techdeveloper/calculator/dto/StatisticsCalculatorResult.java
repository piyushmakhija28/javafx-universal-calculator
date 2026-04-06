package com.techdeveloper.calculator.dto;

public record StatisticsCalculatorResult(
        double mean, double median, double stdDev,
        double min, double max, double sum, int count,
        String errorMessage) {
    public boolean isError() { return errorMessage != null; }

    public static StatisticsCalculatorResult success(double mean, double median, double stdDev,
                                                     double min, double max, double sum, int count) {
        return new StatisticsCalculatorResult(mean, median, stdDev, min, max, sum, count, null);
    }

    public static StatisticsCalculatorResult error(String message) {
        return new StatisticsCalculatorResult(0, 0, 0, 0, 0, 0, 0, message);
    }
}
