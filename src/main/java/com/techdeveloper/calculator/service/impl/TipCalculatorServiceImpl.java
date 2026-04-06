package com.techdeveloper.calculator.service.impl;

import com.techdeveloper.calculator.dto.TipCalculatorResult;
import com.techdeveloper.calculator.form.TipCalculatorForm;
import com.techdeveloper.calculator.service.CalculatorService;

/**
 * Package-private implementation of Tip Calculator.
 * Form fields: billAmount (double), tipPercent (double), splitBy (int, defaults to 1).
 */
class TipCalculatorServiceImpl implements CalculatorService<TipCalculatorForm, TipCalculatorResult> {

    private TipCalculatorServiceImpl() {}

    static TipCalculatorServiceImpl newInstance() {
        return new TipCalculatorServiceImpl();
    }

    @Override
    public TipCalculatorResult calculate(TipCalculatorForm form) {
        try {
            double billAmount = form.billAmount();
            double tipPercent = form.tipPercent();
            int    splitBy    = form.splitBy() > 0 ? form.splitBy() : 1;

            if (billAmount < 0) {
                return TipCalculatorResult.error("billAmount cannot be negative");
            }
            if (tipPercent < 0) {
                return TipCalculatorResult.error("tipPercent cannot be negative");
            }
            if (splitBy <= 0) {
                return TipCalculatorResult.error("splitBy must be at least 1");
            }

            double tipAmount   = billAmount * tipPercent / 100.0;
            double totalAmount = billAmount + tipAmount;
            double perPerson   = totalAmount / splitBy;

            return TipCalculatorResult.success(tipAmount, totalAmount, perPerson);

        } catch (Exception e) {
            return TipCalculatorResult.error(e.getMessage());
        }
    }
}
