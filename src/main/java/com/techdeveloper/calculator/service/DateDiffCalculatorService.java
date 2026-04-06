package com.techdeveloper.calculator.service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Map;

/**
 * Implements Date Difference calculator.
 * Required inputs: "startDate" (YYYY-MM-DD), "endDate" (YYYY-MM-DD)
 * Returns: total days, weeks, months (approx), years (approx)
 */
public class DateDiffCalculatorService implements CalculatorService {

    public DateDiffCalculatorService() {
        // required for FXML
    }

    @Override
    public String calculate(Map<String, String> inputs) {
        try {
            String startDateStr = inputs.get("startDate");
            String endDateStr = inputs.get("endDate");

            if (startDateStr == null || startDateStr.trim().isEmpty()) {
                return "Error: startDate is required (format: YYYY-MM-DD)";
            }
            if (endDateStr == null || endDateStr.trim().isEmpty()) {
                return "Error: endDate is required (format: YYYY-MM-DD)";
            }

            LocalDate startDate = LocalDate.parse(startDateStr.trim());
            LocalDate endDate = LocalDate.parse(endDateStr.trim());

            // Allow negative difference — always calculate from start to end
            long totalDays = ChronoUnit.DAYS.between(startDate, endDate);
            boolean isNegative = totalDays < 0;
            long absDays = Math.abs(totalDays);

            long weeks = absDays / 7;
            long remainingDays = absDays % 7;
            long approxMonths = absDays / 30;
            long approxYears = absDays / 365;

            String prefix = isNegative ? "-" : "";
            return String.format(
                "Total Days: %s%d | Weeks: %s%d (+ %d days) | Approx Months: %s%d | Approx Years: %s%d",
                prefix, absDays,
                prefix, weeks, remainingDays,
                prefix, approxMonths,
                prefix, approxYears
            );

        } catch (java.time.format.DateTimeParseException e) {
            return "Error: Invalid date format. Use YYYY-MM-DD";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}
