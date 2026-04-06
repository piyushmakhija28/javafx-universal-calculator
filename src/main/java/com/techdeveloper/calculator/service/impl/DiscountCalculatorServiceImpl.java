package com.techdeveloper.calculator.service.impl;

import com.techdeveloper.calculator.dto.DiscountCalculatorResult;
import com.techdeveloper.calculator.form.DiscountCalculatorForm;
import com.techdeveloper.calculator.service.CalculatorService;

/**
 * Package-private implementation of Discount Calculator.
 * Form fields: originalPrice (double), discountPercent (double).
 * Computes discountAmount and finalPrice from originalPrice and discountPercent.
 */
class DiscountCalculatorServiceImpl implements CalculatorService<DiscountCalculatorForm, DiscountCalculatorResult> {

    private DiscountCalculatorServiceImpl() {}

    static DiscountCalculatorServiceImpl newInstance() {
        return new DiscountCalculatorServiceImpl();
    }

    @Override
    public DiscountCalculatorResult calculate(DiscountCalculatorForm form) {
        try {
            double originalPrice   = form.originalPrice();
            double discountPercent = form.discountPercent();

            if (originalPrice <= 0) {
                return DiscountCalculatorResult.error("originalPrice must be positive");
            }
            if (discountPercent < 0 || discountPercent > 100) {
                return DiscountCalculatorResult.error("discountPercent must be between 0 and 100");
            }

            double amountSaved = originalPrice * discountPercent / 100.0;
            double finalPrice  = originalPrice - amountSaved;

            return DiscountCalculatorResult.success(amountSaved, finalPrice);

        } catch (Exception e) {
            return DiscountCalculatorResult.error(e.getMessage());
        }
    }
}
