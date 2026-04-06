package com.techdeveloper.calculator.form;

public record CurrencyCalculatorForm(
        double amount,
        String fromCurrency,
        String toCurrency
) {}
