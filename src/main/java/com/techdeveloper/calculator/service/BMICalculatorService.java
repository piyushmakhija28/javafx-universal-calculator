package com.techdeveloper.calculator.service;

import java.util.Map;

/**
 * Implements BMI (Body Mass Index) calculator.
 * Required inputs: "weight", "height", "unit" (METRIC or IMPERIAL)
 * METRIC: weight in kg, height in cm
 * IMPERIAL: weight in lbs, height in inches
 * Formula METRIC:   BMI = weight(kg) / (height(m))^2
 * Formula IMPERIAL: BMI = 703 * weight(lbs) / (height(inches))^2
 * Categories: Underweight < 18.5, Normal 18.5–24.9, Overweight 25–29.9, Obese >= 30
 */
public class BMICalculatorService implements CalculatorService {

    public BMICalculatorService() {
        // required for FXML
    }

    @Override
    public String calculate(Map<String, String> inputs) {
        try {
            String weightStr = inputs.get("weight");
            String heightStr = inputs.get("height");
            String unit = inputs.getOrDefault("unit", "METRIC").trim().toUpperCase();

            if (weightStr == null || weightStr.trim().isEmpty()) {
                return "Error: weight is required";
            }
            if (heightStr == null || heightStr.trim().isEmpty()) {
                return "Error: height is required";
            }

            double weight = Double.parseDouble(weightStr.trim());
            double height = Double.parseDouble(heightStr.trim());

            if (weight <= 0) {
                return "Error: weight must be positive";
            }
            if (height <= 0) {
                return "Error: height must be positive";
            }

            double bmi;
            switch (unit) {
                case "METRIC": {
                    // height is in cm, convert to meters
                    double heightMeters = height / 100.0;
                    bmi = weight / (heightMeters * heightMeters);
                    break;
                }
                case "IMPERIAL": {
                    // weight in lbs, height in inches
                    bmi = 703.0 * weight / (height * height);
                    break;
                }
                default:
                    return "Error: Invalid unit. Use METRIC or IMPERIAL";
            }

            if (Double.isInfinite(bmi) || Double.isNaN(bmi)) {
                return "Error: Invalid BMI result — check inputs";
            }

            String category = getCategory(bmi);
            return String.format("BMI: %.2f | Category: %s", bmi, category);

        } catch (NumberFormatException e) {
            return "Error: Invalid number format";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private String getCategory(double bmi) {
        if (bmi < 18.5) {
            return "Underweight";
        } else if (bmi < 25.0) {
            return "Normal weight";
        } else if (bmi < 30.0) {
            return "Overweight";
        } else {
            return "Obese";
        }
    }
}
