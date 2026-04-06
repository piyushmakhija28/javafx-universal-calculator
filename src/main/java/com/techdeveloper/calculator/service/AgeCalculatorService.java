package com.techdeveloper.calculator.service;

import java.time.LocalDate;
import java.time.Period;
import java.util.Map;

/**
 * Implements Age calculator.
 * Required inputs: "birthDate" (ISO format: YYYY-MM-DD)
 * Optional input: "toDate" (ISO format, defaults to today)
 * Returns: "Age: X Years, Y Months, Z Days"
 */
public class AgeCalculatorService implements CalculatorService {

    @Override
    public String calculate(Map<String, String> inputs) {
        try {
            String birthDateStr = inputs.get("birthDate");
            if (birthDateStr == null || birthDateStr.trim().isEmpty()) {
                return "Error: birthDate is required (format: YYYY-MM-DD)";
            }

            LocalDate birthDate = LocalDate.parse(birthDateStr.trim());

            String toDateStr = inputs.get("toDate");
            LocalDate toDate;
            if (toDateStr == null || toDateStr.trim().isEmpty()) {
                toDate = LocalDate.now();
            } else {
                toDate = LocalDate.parse(toDateStr.trim());
            }

            if (birthDate.isAfter(toDate)) {
                return "Error: birthDate cannot be in the future";
            }

            Period period = Period.between(birthDate, toDate);
            int years = period.getYears();
            int months = period.getMonths();
            int days = period.getDays();

            return String.format("Age: %d Years, %d Months, %d Days", years, months, days);

        } catch (java.time.format.DateTimeParseException e) {
            return "Error: Invalid date format. Use YYYY-MM-DD";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}
