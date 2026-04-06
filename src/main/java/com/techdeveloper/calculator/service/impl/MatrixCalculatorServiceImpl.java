package com.techdeveloper.calculator.service.impl;

import com.techdeveloper.calculator.constants.MatrixOperation;
import com.techdeveloper.calculator.dto.MatrixCalculatorResult;
import com.techdeveloper.calculator.form.MatrixCalculatorForm;
import com.techdeveloper.calculator.service.CalculatorService;

/**
 * Package-private implementation of Matrix Calculator for 2x2 and 3x3 matrices.
 * Form fields: operation (MatrixOperation), matrixA (double[][]), matrixB (double[][]), size (int).
 * Supported operations: ADD, SUBTRACT, MULTIPLY, TRANSPOSE, DETERMINANT.
 */
class MatrixCalculatorServiceImpl implements CalculatorService<MatrixCalculatorForm, MatrixCalculatorResult> {

    private MatrixCalculatorServiceImpl() {}

    static MatrixCalculatorServiceImpl newInstance() {
        return new MatrixCalculatorServiceImpl();
    }

    @Override
    public MatrixCalculatorResult calculate(MatrixCalculatorForm form) {
        try {
            MatrixOperation operation = form.operation();
            double[][]      matrixA   = form.matrixA();
            int             size      = form.size();

            if (operation == null) {
                return MatrixCalculatorResult.error("operation is required (ADD, SUBTRACT, MULTIPLY, TRANSPOSE, DETERMINANT)");
            }
            if (matrixA == null) {
                return MatrixCalculatorResult.error("matrixA is required");
            }
            if (size != 2 && size != 3) {
                return MatrixCalculatorResult.error("size must be 2 or 3");
            }

            switch (operation) {
                case TRANSPOSE: {
                    return MatrixCalculatorResult.successMatrix(transpose(matrixA, size));
                }
                case DETERMINANT: {
                    double det = (size == 2) ? det2x2(matrixA) : det3x3(matrixA);
                    return MatrixCalculatorResult.successDeterminant(det);
                }
                default:
                    break;
            }

            double[][] matrixB = form.matrixB();
            if (matrixB == null) {
                return MatrixCalculatorResult.error("matrixB is required for " + operation.name());
            }

            switch (operation) {
                case ADD:      return MatrixCalculatorResult.successMatrix(addMatrices(matrixA, matrixB, size));
                case SUBTRACT: return MatrixCalculatorResult.successMatrix(subtractMatrices(matrixA, matrixB, size));
                case MULTIPLY: return MatrixCalculatorResult.successMatrix(multiplyMatrices(matrixA, matrixB, size));
                default:
                    return MatrixCalculatorResult.error("Unknown operation: " + operation.name());
            }

        } catch (Exception e) {
            return MatrixCalculatorResult.error(e.getMessage());
        }
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
}
