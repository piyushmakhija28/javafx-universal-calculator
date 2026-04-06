package com.techdeveloper.calculator.dto;

import com.techdeveloper.calculator.constants.NumberBase;

public record ProgrammerCalculatorResult(String value, NumberBase base, String errorMessage) {
    public boolean isError() { return errorMessage != null; }

    public static ProgrammerCalculatorResult success(String value, NumberBase base) {
        return new ProgrammerCalculatorResult(value, base, null);
    }

    public static ProgrammerCalculatorResult error(String message) {
        return new ProgrammerCalculatorResult(null, null, message);
    }
}
