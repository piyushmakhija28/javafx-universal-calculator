package com.techdeveloper.calculator.service.impl;

import com.techdeveloper.calculator.dto.CurrencyCalculatorResult;
import com.techdeveloper.calculator.form.CurrencyCalculatorForm;
import com.techdeveloper.calculator.service.CalculatorService;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Package-private implementation of Currency Converter (static rates fallback).
 * Form fields: amount (double), fromCurrency (String), toCurrency (String).
 * Supports 20 currencies. Rates are static approximations vs USD.
 */
class CurrencyCalculatorServiceImpl implements CalculatorService<CurrencyCalculatorForm, CurrencyCalculatorResult> {

    private CurrencyCalculatorServiceImpl() {}

    static CurrencyCalculatorServiceImpl newInstance() {
        return new CurrencyCalculatorServiceImpl();
    }

    private static final Map<String, Double> RATES_VS_USD;

    static {
        Map<String, Double> rates = new HashMap<>();
        rates.put("USD", 1.0);
        rates.put("EUR", 0.92);
        rates.put("GBP", 0.79);
        rates.put("INR", 83.50);
        rates.put("JPY", 149.50);
        rates.put("CAD", 1.36);
        rates.put("AUD", 1.53);
        rates.put("CHF", 0.90);
        rates.put("CNY", 7.24);
        rates.put("HKD", 7.82);
        rates.put("SGD", 1.34);
        rates.put("NZD", 1.63);
        rates.put("SEK", 10.42);
        rates.put("NOK", 10.57);
        rates.put("DKK", 6.87);
        rates.put("MXN", 17.15);
        rates.put("BRL", 4.97);
        rates.put("ZAR", 18.63);
        rates.put("AED", 3.67);
        rates.put("SAR", 3.75);
        RATES_VS_USD = Collections.unmodifiableMap(rates);
    }

    @Override
    public CurrencyCalculatorResult calculate(CurrencyCalculatorForm form) {
        try {
            double amount = form.amount();
            String from   = form.fromCurrency() != null ? form.fromCurrency().trim().toUpperCase() : null;
            String to     = form.toCurrency()   != null ? form.toCurrency().trim().toUpperCase()   : null;

            if (from == null || from.isEmpty()) {
                return CurrencyCalculatorResult.error("fromCurrency is required");
            }
            if (to == null || to.isEmpty()) {
                return CurrencyCalculatorResult.error("toCurrency is required");
            }
            if (amount < 0) {
                return CurrencyCalculatorResult.error("amount cannot be negative");
            }

            if (!RATES_VS_USD.containsKey(from)) {
                return CurrencyCalculatorResult.error("Unsupported currency: " + from + ". Supported: " + getSupportedList());
            }
            if (!RATES_VS_USD.containsKey(to)) {
                return CurrencyCalculatorResult.error("Unsupported currency: " + to + ". Supported: " + getSupportedList());
            }

            double amountInUSD      = amount / RATES_VS_USD.get(from);
            double convertedAmount  = amountInUSD * RATES_VS_USD.get(to);
            double exchangeRate     = RATES_VS_USD.get(to) / RATES_VS_USD.get(from);

            return CurrencyCalculatorResult.success(convertedAmount, exchangeRate, from, to);

        } catch (Exception e) {
            return CurrencyCalculatorResult.error(e.getMessage());
        }
    }

    private String getSupportedList() {
        return String.join(", ", RATES_VS_USD.keySet().stream().sorted().toArray(String[]::new));
    }
}
