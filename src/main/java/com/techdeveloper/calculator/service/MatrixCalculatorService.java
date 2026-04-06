package com.techdeveloper.calculator.service;

import java.util.Map;

/**
 * Implements Matrix Calculator for 2x2 and 3x3 matrices.
 * Required inputs: "operation", "matrixA" (row-major comma-separated values)
 * Optional inputs: "matrixB" (for ADD, SUBTRACT, MULTIPLY), "size" (2 or 3, defaults to 2)
 * Supported operations: ADD, SUBTRACT, MULTIPLY, TRANSPOSE, DETERMINANT
 * Matrix values are provided as flat comma-separated lists, row-major order.
 * Example 2x2: "1,2,3,4" = [[1,2],[3,4]]
 * Example 3x3: "1,2,3,4,5,6,7,8,9" = [[1,2,3],[4,5,6],[7,8,9]]
 */
public class MatrixCalculatorService implements CalculatorService {

    public MatrixCalculatorService() {
        // required for FXML
    }

    @Override
    public String calculate(Map<String, String> inputs) {
        try {
            String operation = inputs.get("operation");
            String matrixAStr = inputs.get("matrixA");

            if (operation == null || operation.trim().isEmpty()) {
                return "Error: operation is required (ADD, SUBTRACT, MULTIPLY, TRANSPOSE, DETERMINANT)";
            }
            if (matrixAStr == null || matrixAStr.trim().isEmpty()) {
                return "Error: matrixA is required";
            }

            operation = operation.trim().toUpperCase();

            String sizeStr = inputs.getOrDefault("size", "2").trim();
            int size = Integer.parseInt(sizeStr);
            if (size != 2 && size != 3) {
                return "Error: size must be 2 or 3";
            }

            double[][] matrixA = parseMatrix(matrixAStr, size);
            if (matrixA == null) {
                return "Error: matrixA must have exactly " + (size * size) + " values for a " + size + "x" + size + " matrix";
            }

            switch (operation) {
                case "TRANSPOSE": {
                    double[][] result = transpose(matrixA, size);
                    return "Result: " + formatMatrix(result, size);
                }
                case "DETERMINANT": {
                    if (size == 2) {
                        double det = det2x2(matrixA);
                        return "Determinant: " + formatScalar(det);
                    } else {
                        double det = det3x3(matrixA);
                        return "Determinant: " + formatScalar(det);
                    }
                }
                default:
                    break;
            }

            // Operations requiring matrixB
            String matrixBStr = inputs.get("matrixB");
            if (matrixBStr == null || matrixBStr.trim().isEmpty()) {
                return "Error: matrixB is required for " + operation;
            }
            double[][] matrixB = parseMatrix(matrixBStr, size);
            if (matrixB == null) {
                return "Error: matrixB must have exactly " + (size * size) + " values for a " + size + "x" + size + " matrix";
            }

            switch (operation) {
                case "ADD": {
                    double[][] result = addMatrices(matrixA, matrixB, size);
                    return "Result: " + formatMatrix(result, size);
                }
                case "SUBTRACT": {
                    double[][] result = subtractMatrices(matrixA, matrixB, size);
                    return "Result: " + formatMatrix(result, size);
                }
                case "MULTIPLY": {
                    double[][] result = multiplyMatrices(matrixA, matrixB, size);
                    return "Result: " + formatMatrix(result, size);
                }
                default:
                    return "Error: Unknown operation: " + operation;
            }

        } catch (NumberFormatException e) {
            return "Error: Invalid number format in matrix values";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private double[][] parseMatrix(String csv, int size) {
        String[] tokens = csv.trim().split(",");
        int expected = size * size;
        if (tokens.length != expected) {
            return null;
        }
        double[][] matrix = new double[size][size];
        int idx = 0;
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                matrix[r][c] = Double.parseDouble(tokens[idx++].trim());
            }
        }
        return matrix;
    }

    private double[][] transpose(double[][] m, int size) {
        double[][] result = new double[size][size];
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                result[c][r] = m[r][c];
            }
        }
        return result;
    }

    private double det2x2(double[][] m) {
        return m[0][0] * m[1][1] - m[0][1] * m[1][0];
    }

    private double det3x3(double[][] m) {
        // Cofactor expansion along first row
        double d1 = m[0][0] * (m[1][1] * m[2][2] - m[1][2] * m[2][1]);
        double d2 = m[0][1] * (m[1][0] * m[2][2] - m[1][2] * m[2][0]);
        double d3 = m[0][2] * (m[1][0] * m[2][1] - m[1][1] * m[2][0]);
        return d1 - d2 + d3;
    }

    private double[][] addMatrices(double[][] a, double[][] b, int size) {
        double[][] result = new double[size][size];
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                result[r][c] = a[r][c] + b[r][c];
            }
        }
        return result;
    }

    private double[][] subtractMatrices(double[][] a, double[][] b, int size) {
        double[][] result = new double[size][size];
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                result[r][c] = a[r][c] - b[r][c];
            }
        }
        return result;
    }

    private double[][] multiplyMatrices(double[][] a, double[][] b, int size) {
        double[][] result = new double[size][size];
        for (int r = 0; r < size; r++) {
            for (int c = 0; c < size; c++) {
                double sum = 0;
                for (int k = 0; k < size; k++) {
                    sum += a[r][k] * b[k][c];
                }
                result[r][c] = sum;
            }
        }
        return result;
    }

    private String formatMatrix(double[][] m, int size) {
        StringBuilder sb = new StringBuilder("[");
        for (int r = 0; r < size; r++) {
            sb.append("[");
            for (int c = 0; c < size; c++) {
                sb.append(formatScalar(m[r][c]));
                if (c < size - 1) sb.append(", ");
            }
            sb.append("]");
            if (r < size - 1) sb.append(", ");
        }
        sb.append("]");
        return sb.toString();
    }

    private String formatScalar(double value) {
        if (value == Math.floor(value) && !Double.isInfinite(value) && Math.abs(value) < 1e15) {
            return String.valueOf((long) value);
        }
        // Round to 6 decimal places to suppress floating-point noise
        double rounded = Math.round(value * 1e6) / 1e6;
        return String.valueOf(rounded);
    }
}
