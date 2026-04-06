package com.techdeveloper.calculator.service;

import java.util.Map;

/**
 * Implements EMI (Equated Monthly Installment) calculator.
 * Required inputs: "principal", "annualRate" (percentage), "tenureMonths"
 * Formula: EMI = P * r * (1+r)^n / ((1+r)^n - 1)
 * where r = monthly interest rate = annualRate / 12 / 100
 * Returns a 3-line result: EMI, Total Amount, Total Interest.
 */
public class EMICalculatorService implements CalculatorService {

    @Override
    public String calculate(Map<String, String> inputs) {
        try {
            String principalStr = inputs.get("principal");
            String rateStr = inputs.get("annualRate");
            String tenureStr = inputs.get("tenureMonths");

            if (principalStr == null || principalStr.trim().isEmpty()) {
                return "Error: principal is required";
            }
            if (rateStr == null || rateStr.trim().isEmpty()) {
                return "Error: annualRate is required";
            }
            if (tenureStr == null || tenureStr.trim().isEmpty()) {
                return "Error: tenureMonths is required";
            }

            double principal = Double.parseDouble(principalStr.trim());
            double annualRate = Double.parseDouble(rateStr.trim());
            int tenureMonths = Integer.parseInt(tenureStr.trim());

            if (principal <= 0) {
                return "Error: principal must be positive";
            }
            if (annualRate < 0) {
                return "Error: annualRate cannot be negative";
            }
            if (tenureMonths <= 0) {
                return "Error: tenureMonths must be positive";
            }

            // Handle zero interest rate edge case
            if (annualRate == 0) {
                double emi = principal / tenureMonths;
                double totalAmount = principal;
                double totalInterest = 0;
                return formatEMIResult(emi, totalAmount, totalInterest);
            }

            double monthlyRate = annualRate / 12.0 / 100.0;
            double onePlusRPowN = Math.pow(1 + monthlyRate, tenureMonths);
            double emi = principal * monthlyRate * onePlusRPowN / (onePlusRPowN - 1);
            double totalAmount = emi * tenureMonths;
            double totalInterest = totalAmount - principal;

            return formatEMIResult(emi, totalAmount, totalInterest);

        } catch (NumberFormatException e) {
            return "Error: Invalid number format";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private String formatEMIResult(double emi, double totalAmount, double totalInterest) {
        return String.format("EMI: \u20B9%.2f | Total Amount: \u20B9%.2f | Total Interest: \u20B9%.2f",
                emi, totalAmount, totalInterest);
    }
}
