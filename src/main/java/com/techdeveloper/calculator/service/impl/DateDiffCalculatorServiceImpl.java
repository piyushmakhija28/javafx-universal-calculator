package com.techdeveloper.calculator.service.impl;

import com.techdeveloper.calculator.dto.DateDiffCalculatorResult;
import com.techdeveloper.calculator.form.DateDiffCalculatorForm;
import com.techdeveloper.calculator.service.CalculatorService;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Package-private implementation of Date Difference calculator.
 * Form fields: startDate (LocalDate), endDate (LocalDate).
 */
class DateDiffCalculatorServiceImpl implements CalculatorService<DateDiffCalculatorForm, DateDiffCalculatorResult> {

    private DateDiffCalculatorServiceImpl() {}

    static DateDiffCalculatorServiceImpl newInstance() {
        return new DateDiffCalculatorServiceImpl();
    }

    @Override
    public DateDiffCalculatorResult calculate(DateDiffCalculatorForm form) {
        try {
            LocalDate startDate = form.startDate();
            LocalDate endDate   = form.endDate();

            if (startDate == null) {
                return DateDiffCalculatorResult.error("startDate is required");
            }
            if (endDate == null) {
                return DateDiffCalculatorResult.error("endDate is required");
            }

            long totalDays = ChronoUnit.DAYS.between(startDate, endDate);
            long absDays   = Math.abs(totalDays);

            long remainingDays = absDays % 7;
            int  approxMonths  = (int) (absDays / 30);
            int  approxYears   = (int) (absDays / 365);

            return DateDiffCalculatorResult.success(totalDays, approxYears, approxMonths, (int) remainingDays);

        } catch (Exception e) {
            return DateDiffCalculatorResult.error(e.getMessage());
        }
    }
}
