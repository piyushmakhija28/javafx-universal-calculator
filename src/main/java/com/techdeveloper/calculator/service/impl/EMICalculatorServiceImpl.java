package com.techdeveloper.calculator.service.impl;

import com.techdeveloper.calculator.dto.EMICalculatorResult;
import com.techdeveloper.calculator.form.EMICalculatorForm;
import com.techdeveloper.calculator.service.CalculatorService;

/**
 * Package-private implementation of EMI (Equated Monthly Installment) calculator.
 * Formula: EMI = P * r * (1+r)^n / ((1+r)^n - 1)
 * Form fields: principal, annualRate (percentage), tenureMonths.
 */
class EMICalculatorServiceImpl implements CalculatorService<EMICalculatorForm, EMICalculatorResult> {

    private EMICalculatorServiceImpl() {}

    static EMICalculatorServiceImpl newInstance() {
        return new EMICalculatorServiceImpl();
    }

    @Override
    public EMICalculatorResult calculate(EMICalculatorForm form) {
        try {
            double principal    = form.principal();
            double annualRate   = form.annualRate();
            int    tenureMonths = form.tenureMonths();

            if (principal <= 0) {
                return EMICalculatorResult.error("principal must be positive");
            }
            if (annualRate < 0) {
                return EMICalculatorResult.error("annualRate cannot be negative");
            }
            if (tenureMonths <= 0) {
                return EMICalculatorResult.error("tenureMonths must be positive");
            }

            if (annualRate == 0) {
                double emi = principal / tenureMonths;
                return EMICalculatorResult.success(emi, 0, principal);
            }

            double monthlyRate    = annualRate / 12.0 / 100.0;
            double onePlusRPowN   = Math.pow(1 + monthlyRate, tenureMonths);
            double emi            = principal * monthlyRate * onePlusRPowN / (onePlusRPowN - 1);
            double totalAmount    = emi * tenureMonths;
            double totalInterest  = totalAmount - principal;

            return EMICalculatorResult.success(emi, totalInterest, totalAmount);

        } catch (Exception e) {
            return EMICalculatorResult.error(e.getMessage());
        }
    }
}
