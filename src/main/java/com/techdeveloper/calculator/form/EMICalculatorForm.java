package com.techdeveloper.calculator.form;

public record EMICalculatorForm(
        double principal,
        double annualRate,
        int tenureMonths
) {}
