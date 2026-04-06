package com.techdeveloper.calculator.service;

import java.util.Map;

/**
 * Implements Tip Calculator.
 * Required inputs: "billAmount", "tipPercent"
 * Optional input: "splitBy" (number of people, defaults to 1)
 * Returns: tip amount, total bill, and per-person share.
 */
public class TipCalculatorService implements CalculatorService {

    @Override
    public String calculate(Map<String, String> inputs) {
        try {
            String billStr = inputs.get("billAmount");
            String tipStr = inputs.get("tipPercent");

            if (billStr == null || billStr.trim().isEmpty()) {
                return "Error: billAmount is required";
            }
            if (tipStr == null || tipStr.trim().isEmpty()) {
                return "Error: tipPercent is required";
            }

            double billAmount = Double.parseDouble(billStr.trim());
            double tipPercent = Double.parseDouble(tipStr.trim());

            if (billAmount < 0) {
                return "Error: billAmount cannot be negative";
            }
            if (tipPercent < 0) {
                return "Error: tipPercent cannot be negative";
            }

            String splitStr = inputs.getOrDefault("splitBy", "1").trim();
            int splitBy = Integer.parseInt(splitStr);
            if (splitBy <= 0) {
                return "Error: splitBy must be at least 1";
            }

            double tipAmount = billAmount * tipPercent / 100.0;
            double totalAmount = billAmount + tipAmount;
            double perPerson = totalAmount / splitBy;

            if (splitBy == 1) {
                return String.format("Tip: %.2f | Total: %.2f", tipAmount, totalAmount);
            }
            return String.format("Tip: %.2f | Total: %.2f | Per Person (%d): %.2f",
                    tipAmount, totalAmount, splitBy, perPerson);

        } catch (NumberFormatException e) {
            return "Error: Invalid number format";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}
