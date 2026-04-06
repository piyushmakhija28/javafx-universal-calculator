package com.techdeveloper.calculator.form;

import com.techdeveloper.calculator.constants.MatrixOperation;

public record MatrixCalculatorForm(
        MatrixOperation operation,
        double[][] matrixA,
        double[][] matrixB,
        int size
) {}
