package com.techdeveloper.calculator.form;

import java.time.LocalDate;

public record AgeCalculatorForm(
        LocalDate birthDate,
        LocalDate toDate
) {}
