package com.techdeveloper.calculator.service.impl;

import com.techdeveloper.calculator.constants.BMIUnit;
import com.techdeveloper.calculator.dto.BMICalculatorResult;
import com.techdeveloper.calculator.form.BMICalculatorForm;
import com.techdeveloper.calculator.service.CalculatorService;

/**
 * Package-private implementation of BMI (Body Mass Index) calculator.
 * METRIC: weight in kg, height in cm.
 * IMPERIAL: weight in lbs, height in inches.
 */
class BMICalculatorServiceImpl implements CalculatorService<BMICalculatorForm, BMICalculatorResult> {

    private BMICalculatorServiceImpl() {}

    static BMICalculatorServiceImpl newInstance() {
        return new BMICalculatorServiceImpl();
    }

    @Override
    public BMICalculatorResult calculate(BMICalculatorForm form) {
        try {
            double weight = form.weight();
            double height = form.height();
            BMIUnit unit  = form.unit() != null ? form.unit() : BMIUnit.METRIC;

            if (weight <= 0) {
                return BMICalculatorResult.error("weight must be positive");
            }
            if (height <= 0) {
                return BMICalculatorResult.error("height must be positive");
            }

            double bmi;
            if (unit == BMIUnit.METRIC) {
                double heightMeters = height / 100.0;
                bmi = weight / (heightMeters * heightMeters);
            } else {
                bmi = 703.0 * weight / (height * height);
            }

            if (Double.isInfinite(bmi) || Double.isNaN(bmi)) {
                return BMICalculatorResult.error("Invalid BMI result — check inputs");
            }

            return BMICalculatorResult.success(bmi, getCategory(bmi));

        } catch (Exception e) {
            return BMICalculatorResult.error(e.getMessage());
        }
    }

    private String getCategory(double bmi) {
        if (bmi < 18.5)       return "Underweight";
        else if (bmi < 25.0)  return "Normal weight";
        else if (bmi < 30.0)  return "Overweight";
        else                  return "Obese";
    }
}
