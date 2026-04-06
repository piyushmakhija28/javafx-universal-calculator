package com.techdeveloper.calculator.form;

public record TipCalculatorForm(
        double billAmount,
        double tipPercent,
        int splitBy
) {}
