package com.techdeveloper.calculator.form;

import java.time.LocalDate;

public record DateDiffCalculatorForm(
        LocalDate startDate,
        LocalDate endDate
) {}
