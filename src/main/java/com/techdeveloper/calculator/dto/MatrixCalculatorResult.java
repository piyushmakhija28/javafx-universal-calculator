package com.techdeveloper.calculator.dto;

public record MatrixCalculatorResult(double[][] resultMatrix, Double determinant, String errorMessage) {
    public boolean isError() { return errorMessage != null; }

    public static MatrixCalculatorResult successMatrix(double[][] resultMatrix) {
        return new MatrixCalculatorResult(resultMatrix, null, null);
    }

    public static MatrixCalculatorResult successDeterminant(double determinant) {
        return new MatrixCalculatorResult(null, determinant, null);
    }

    public static MatrixCalculatorResult error(String message) {
        return new MatrixCalculatorResult(null, null, message);
    }
}
