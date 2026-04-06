package com.techdeveloper.calculator.dto;

import com.techdeveloper.calculator.constants.SolveFor;

public record SpeedCalculatorResult(SolveFor solvedVariable, double value, String errorMessage) {
    public boolean isError() { return errorMessage != null; }

    public static SpeedCalculatorResult success(SolveFor solvedVariable, double value) {
        return new SpeedCalculatorResult(solvedVariable, value, null);
    }

    public static SpeedCalculatorResult error(String message) {
        return new SpeedCalculatorResult(null, 0, message);
    }
}
