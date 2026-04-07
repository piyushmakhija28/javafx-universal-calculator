package com.techdeveloper.calculator.controller.helper;

/**
 * Helper base class for CurrencyCalculatorController.
 * Contains utility methods extracted to keep the controller's initialize method ≤ 35 lines.
 * All methods are protected for use by the controller subclass.
 */
public class CurrencyCalculatorHelper {

    protected String extractCode(String item) {
        return item.substring(0, 3);
    }

    protected String formatConversionResult(double amount, String fromCode,
            double converted, String toCode, double exchangeRate) {
        return String.format("%.2f %s = %.2f %s (Rate: 1 %s = %.4f %s)",
                amount, fromCode, converted, toCode, fromCode, exchangeRate, toCode);
    }
}
