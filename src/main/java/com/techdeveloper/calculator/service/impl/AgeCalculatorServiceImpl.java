package com.techdeveloper.calculator.service.impl;

import com.techdeveloper.calculator.dto.AgeCalculatorResult;
import com.techdeveloper.calculator.form.AgeCalculatorForm;
import com.techdeveloper.calculator.service.CalculatorService;

import java.time.LocalDate;
import java.time.Period;

/**
 * Package-private implementation of Age calculator.
 * Form fields: birthDate (LocalDate), toDate (LocalDate, defaults to today when null).
 */
class AgeCalculatorServiceImpl implements CalculatorService<AgeCalculatorForm, AgeCalculatorResult> {

    private AgeCalculatorServiceImpl() {}

    static AgeCalculatorServiceImpl newInstance() {
        return new AgeCalculatorServiceImpl();
    }

    @Override
    public AgeCalculatorResult calculate(AgeCalculatorForm form) {
        try {
            LocalDate birthDate = form.birthDate();
            if (birthDate == null) {
                return AgeCalculatorResult.error("birthDate is required");
            }

            LocalDate toDate = form.toDate() != null ? form.toDate() : LocalDate.now();

            if (birthDate.isAfter(toDate)) {
                return AgeCalculatorResult.error("birthDate cannot be in the future");
            }

            Period period = Period.between(birthDate, toDate);
            return AgeCalculatorResult.success(period.getYears(), period.getMonths(), period.getDays());

        } catch (Exception e) {
            return AgeCalculatorResult.error(e.getMessage());
        }
    }
}
