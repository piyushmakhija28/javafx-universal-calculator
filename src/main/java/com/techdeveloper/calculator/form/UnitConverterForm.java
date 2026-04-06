package com.techdeveloper.calculator.form;

import com.techdeveloper.calculator.constants.UnitCategory;

public record UnitConverterForm(
        double value,
        String fromUnit,
        String toUnit,
        UnitCategory category
) {}
