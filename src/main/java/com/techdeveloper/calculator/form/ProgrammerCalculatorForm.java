package com.techdeveloper.calculator.form;

import com.techdeveloper.calculator.constants.NumberBase;

public record ProgrammerCalculatorForm(
        String value,
        String operation,
        String operand2,
        NumberBase inputBase,
        NumberBase outputBase
) {}
