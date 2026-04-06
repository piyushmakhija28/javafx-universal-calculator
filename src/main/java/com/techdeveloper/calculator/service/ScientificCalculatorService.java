package com.techdeveloper.calculator.service;

import java.util.Map;

/**
 * Implements scientific calculator operations.
 * Required inputs: "value", "operation", "mode" (DEG or RAD)
 * "mode" defaults to DEG for trig functions.
 */
public class ScientificCalculatorService implements CalculatorService {

    public ScientificCalculatorService() {
        // required for FXML
    }

    @Override
    public String calculate(Map<String, String> inputs) {
        try {
            String valueStr = inputs.get("value");
            String operation = inputs.get("operation");

            if (operation == null || operation.trim().isEmpty()) {
                return "Error: operation is required";
            }

            operation = operation.trim().toLowerCase();

            // pi and e don't need a value
            if ("pi".equals(operation)) {
                return formatResult(Math.PI);
            }
            if ("e".equals(operation)) {
                return formatResult(Math.E);
            }

            if (valueStr == null || valueStr.trim().isEmpty()) {
                return "Error: value is required";
            }

            double value = Double.parseDouble(valueStr.trim());
            String mode = inputs.getOrDefault("mode", "DEG").trim().toUpperCase();
            boolean isDeg = "DEG".equals(mode);

            switch (operation) {
                case "sin":    return formatResult(Math.sin(toRadians(value, isDeg)));
                case "cos":    return formatResult(Math.cos(toRadians(value, isDeg)));
                case "tan": {
                    double radians = toRadians(value, isDeg);
                    // Check for undefined tan (90, 270 degrees)
                    double cosVal = Math.cos(radians);
                    if (Math.abs(cosVal) < 1e-10) {
                        return "Error: tan is undefined for this angle";
                    }
                    return formatResult(Math.tan(radians));
                }
                case "asin": {
                    if (value < -1 || value > 1) {
                        return "Error: asin input must be between -1 and 1";
                    }
                    double result = Math.asin(value);
                    return formatResult(isDeg ? Math.toDegrees(result) : result);
                }
                case "acos": {
                    if (value < -1 || value > 1) {
                        return "Error: acos input must be between -1 and 1";
                    }
                    double result = Math.acos(value);
                    return formatResult(isDeg ? Math.toDegrees(result) : result);
                }
                case "atan": {
                    double result = Math.atan(value);
                    return formatResult(isDeg ? Math.toDegrees(result) : result);
                }
                case "log":  {
                    if (value <= 0) {
                        return "Error: log requires positive number";
                    }
                    return formatResult(Math.log10(value));
                }
                case "ln": {
                    if (value <= 0) {
                        return "Error: ln requires positive number";
                    }
                    return formatResult(Math.log(value));
                }
                case "sqrt": {
                    if (value < 0) {
                        return "Error: Square root of negative number";
                    }
                    return formatResult(Math.sqrt(value));
                }
                case "square": return formatResult(value * value);
                case "cube":   return formatResult(value * value * value);
                case "factorial": {
                    if (value < 0 || value != Math.floor(value)) {
                        return "Error: Factorial requires a non-negative integer";
                    }
                    if (value > 20) {
                        return "Error: Input too large for factorial (max 20)";
                    }
                    long n = (long) value;
                    long result = 1;
                    for (long i = 2; i <= n; i++) {
                        result *= i;
                    }
                    return String.valueOf(result);
                }
                case "power": {
                    String expStr = inputs.get("exponent");
                    if (expStr == null || expStr.trim().isEmpty()) {
                        return "Error: exponent is required for power operation";
                    }
                    double exponent = Double.parseDouble(expStr.trim());
                    return formatResult(Math.pow(value, exponent));
                }
                default:
                    return "Error: Unknown operation: " + operation;
            }

        } catch (NumberFormatException e) {
            return "Error: Invalid number format";
        } catch (NullPointerException e) {
            return "Error: Missing required input";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private double toRadians(double value, boolean isDeg) {
        return isDeg ? Math.toRadians(value) : value;
    }

    private String formatResult(double value) {
        if (Double.isInfinite(value)) {
            return "Error: Result is infinite";
        }
        if (Double.isNaN(value)) {
            return "Error: Result is not a number";
        }
        // Round to 10 significant decimal places to avoid floating-point noise
        double rounded = Math.round(value * 1e10) / 1e10;
        if (rounded == Math.floor(rounded) && Math.abs(rounded) < 1e15) {
            return String.valueOf((long) rounded);
        }
        return String.valueOf(rounded);
    }
}
