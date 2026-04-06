package com.techdeveloper.calculator.service;

import java.util.Map;

/**
 * Implements Discount Calculator.
 * Mode 1 — compute final price: inputs "originalPrice" + "discountPercent"
 * Mode 2 — compute discount %:  inputs "originalPrice" + "finalPrice"
 * Returns: final price, amount saved, discount percentage.
 */
public class DiscountCalculatorService implements CalculatorService {

    public DiscountCalculatorService() {
        // required for FXML
    }

    @Override
    public String calculate(Map<String, String> inputs) {
        try {
            String originalPriceStr = inputs.get("originalPrice");
            if (originalPriceStr == null || originalPriceStr.trim().isEmpty()) {
                return "Error: originalPrice is required";
            }

            double originalPrice = Double.parseDouble(originalPriceStr.trim());
            if (originalPrice <= 0) {
                return "Error: originalPrice must be positive";
            }

            // Mode 2: compute discount % from original and final price
            String finalPriceStr = inputs.get("finalPrice");
            if (finalPriceStr != null && !finalPriceStr.trim().isEmpty()) {
                double finalPrice = Double.parseDouble(finalPriceStr.trim());
                if (finalPrice < 0) {
                    return "Error: finalPrice cannot be negative";
                }
                if (finalPrice > originalPrice) {
                    return "Error: finalPrice cannot be greater than originalPrice";
                }
                double amountSaved = originalPrice - finalPrice;
                double discountPercent = (amountSaved / originalPrice) * 100.0;
                return String.format("Discount: %.2f%% | Amount Saved: %.2f | Final Price: %.2f",
                        discountPercent, amountSaved, finalPrice);
            }

            // Mode 1: compute final price from original and discount %
            String discountStr = inputs.get("discountPercent");
            if (discountStr == null || discountStr.trim().isEmpty()) {
                return "Error: discountPercent (or finalPrice) is required";
            }

            double discountPercent = Double.parseDouble(discountStr.trim());
            if (discountPercent < 0 || discountPercent > 100) {
                return "Error: discountPercent must be between 0 and 100";
            }

            double amountSaved = originalPrice * discountPercent / 100.0;
            double finalPrice = originalPrice - amountSaved;
            return String.format("Discount: %.2f%% | Amount Saved: %.2f | Final Price: %.2f",
                    discountPercent, amountSaved, finalPrice);

        } catch (NumberFormatException e) {
            return "Error: Invalid number format";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}
