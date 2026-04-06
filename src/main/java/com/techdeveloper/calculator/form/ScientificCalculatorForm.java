package com.techdeveloper.calculator.form;

import com.techdeveloper.calculator.constants.AngleMode;

public record ScientificCalculatorForm(
        String value,
        String operation,
        AngleMode mode
) {}
