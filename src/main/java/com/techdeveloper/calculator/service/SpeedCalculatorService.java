package com.techdeveloper.calculator.service;

import java.util.Map;

/**
 * Implements Speed / Distance / Time calculator.
 * Required input: "solve" — what to calculate: SPEED, DISTANCE, or TIME
 * For SPEED:    inputs "distance" and "time"
 * For DISTANCE: inputs "speed" and "time"
 * For TIME:     inputs "speed" and "distance"
 * All values are unitless — caller provides values in consistent units.
 * Optionally accepts "distanceUnit" and "timeUnit" labels for result formatting.
 */
public class SpeedCalculatorService implements CalculatorService {

    @Override
    public String calculate(Map<String, String> inputs) {
        try {
            String solve = inputs.get("solve");
            if (solve == null || solve.trim().isEmpty()) {
                return "Error: solve is required (SPEED, DISTANCE, or TIME)";
            }

            solve = solve.trim().toUpperCase();
            String distanceUnit = inputs.getOrDefault("distanceUnit", "").trim();
            String timeUnit = inputs.getOrDefault("timeUnit", "").trim();
            String speedUnit = distanceUnit.isEmpty() || timeUnit.isEmpty()
                    ? "" : distanceUnit + "/" + timeUnit;

            switch (solve) {
                case "SPEED": {
                    double distance = requirePositive(inputs, "distance");
                    double time = requirePositive(inputs, "time");
                    double speed = distance / time;
                    String unit = speedUnit.isEmpty() ? "" : " " + speedUnit;
                    return "Speed: " + formatNum(speed) + unit;
                }
                case "DISTANCE": {
                    double speed = requirePositive(inputs, "speed");
                    double time = requirePositive(inputs, "time");
                    double distance = speed * time;
                    String unit = distanceUnit.isEmpty() ? "" : " " + distanceUnit;
                    return "Distance: " + formatNum(distance) + unit;
                }
                case "TIME": {
                    double speed = requirePositive(inputs, "speed");
                    double distance = requirePositive(inputs, "distance");
                    if (speed == 0) {
                        return "Error: speed cannot be zero when solving for TIME";
                    }
                    double time = distance / speed;
                    String unit = timeUnit.isEmpty() ? "" : " " + timeUnit;
                    return "Time: " + formatNum(time) + unit;
                }
                default:
                    return "Error: Unknown solve target: " + solve + ". Use SPEED, DISTANCE, or TIME";
            }

        } catch (IllegalArgumentException e) {
            return "Error: " + e.getMessage();
        } catch (NumberFormatException e) {
            return "Error: Invalid number format";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private double requirePositive(Map<String, String> inputs, String key) {
        String valStr = inputs.get(key);
        if (valStr == null || valStr.trim().isEmpty()) {
            throw new IllegalArgumentException(key + " is required");
        }
        double val = Double.parseDouble(valStr.trim());
        if (val < 0) {
            throw new IllegalArgumentException(key + " cannot be negative");
        }
        return val;
    }

    private String formatNum(double value) {
        if (value == Math.floor(value) && !Double.isInfinite(value) && Math.abs(value) < 1e15) {
            return String.valueOf((long) value);
        }
        double rounded = Math.round(value * 1e6) / 1e6;
        return String.valueOf(rounded);
    }
}
