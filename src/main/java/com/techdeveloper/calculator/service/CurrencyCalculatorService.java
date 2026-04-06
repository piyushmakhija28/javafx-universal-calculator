package com.techdeveloper.calculator.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Implements Currency Converter.
 * Required inputs: "amount", "fromCurrency", "toCurrency"
 * Supports 20 currencies. Rates are static approximations vs USD.
 * Returns: formatted conversion result string.
 */
public class CurrencyCalculatorService implements CalculatorService {

    // Static exchange rates relative to 1 USD (approximate reference rates)
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
    public String calculate(Map<String, String> inputs) {
        try {
            String amountStr = inputs.get("amount");
            String fromCurrency = inputs.get("fromCurrency");
            String toCurrency = inputs.get("toCurrency");

            if (amountStr == null || amountStr.trim().isEmpty()) {
                return "Error: amount is required";
            }
            if (fromCurrency == null || fromCurrency.trim().isEmpty()) {
                return "Error: fromCurrency is required";
            }
            if (toCurrency == null || toCurrency.trim().isEmpty()) {
                return "Error: toCurrency is required";
            }

            double amount = Double.parseDouble(amountStr.trim());
            if (amount < 0) {
                return "Error: amount cannot be negative";
            }

            String from = fromCurrency.trim().toUpperCase();
            String to = toCurrency.trim().toUpperCase();

            if (!RATES_VS_USD.containsKey(from)) {
                return "Error: Unsupported currency: " + from + ". Supported: " + getSupportedList();
            }
            if (!RATES_VS_USD.containsKey(to)) {
                return "Error: Unsupported currency: " + to + ". Supported: " + getSupportedList();
            }

            // Convert: from -> USD -> to
            double amountInUSD = amount / RATES_VS_USD.get(from);
            double convertedAmount = amountInUSD * RATES_VS_USD.get(to);

            double exchangeRate = RATES_VS_USD.get(to) / RATES_VS_USD.get(from);

            return String.format("%.2f %s = %.2f %s (Rate: 1 %s = %.4f %s)",
                    amount, from, convertedAmount, to, from, exchangeRate, to);

        } catch (NumberFormatException e) {
            return "Error: Invalid number format for amount";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private String getSupportedList() {
        return String.join(", ", RATES_VS_USD.keySet().stream().sorted().toArray(String[]::new));
    }
}
