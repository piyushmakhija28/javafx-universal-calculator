package com.techdeveloper.calculator.form;

import com.techdeveloper.calculator.constants.BMIUnit;

public record BMICalculatorForm(
        double weight,
        double height,
        BMIUnit unit
) {}
