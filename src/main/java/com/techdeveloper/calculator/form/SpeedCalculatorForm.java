package com.techdeveloper.calculator.form;

import com.techdeveloper.calculator.constants.SolveFor;

public record SpeedCalculatorForm(
        SolveFor solve,
        Double speed,
        Double distance,
        Double time
) {}
