package com.techdeveloper.calculator.service.impl;

import com.techdeveloper.calculator.constants.AngleMode;
import com.techdeveloper.calculator.dto.ScientificCalculatorResult;
import com.techdeveloper.calculator.form.ScientificCalculatorForm;
import com.techdeveloper.calculator.service.CalculatorService;

/**
 * Package-private implementation of scientific calculator operations.
 * mode field from the form carries AngleMode.DEG or AngleMode.RAD.
 */
class ScientificCalculatorServiceImpl implements CalculatorService<ScientificCalculatorForm, ScientificCalculatorResult> {

    private ScientificCalculatorServiceImpl() {}

    static ScientificCalculatorServiceImpl newInstance() {
        return new ScientificCalculatorServiceImpl();
    }

    @Override
    public ScientificCalculatorResult calculate(ScientificCalculatorForm form) {
        try {
            String operation = form.operation();

            if (operation == null || operation.trim().isEmpty()) {
                return ScientificCalculatorResult.error("operation is required");
            }

            operation = operation.trim().toLowerCase();

            if ("pi".equals(operation)) {
                return ScientificCalculatorResult.success(formatResult(Math.PI));
            }
            if ("e".equals(operation)) {
                return ScientificCalculatorResult.success(formatResult(Math.E));
            }

            String valueStr = form.value();
            if (valueStr == null || valueStr.trim().isEmpty()) {
                return ScientificCalculatorResult.error("value is required");
            }

            double value = Double.parseDouble(valueStr.trim());
            boolean isDeg = form.mode() == AngleMode.DEG;

            switch (operation) {
                case "sin":    return ScientificCalculatorResult.success(formatResult(Math.sin(toRadians(value, isDeg))));
                case "cos":    return ScientificCalculatorResult.success(formatResult(Math.cos(toRadians(value, isDeg))));
                case "tan": {
                    double radians = toRadians(value, isDeg);
                    if (Math.abs(Math.cos(radians)) < 1e-10) {
                        return ScientificCalculatorResult.error("tan is undefined for this angle");
                    }
                    return ScientificCalculatorResult.success(formatResult(Math.tan(radians)));
                }
                case "asin": {
                    if (value < -1 || value > 1) {
                        return ScientificCalculatorResult.error("asin input must be between -1 and 1");
                    }
                    double result = Math.asin(value);
                    return ScientificCalculatorResult.success(formatResult(isDeg ? Math.toDegrees(result) : result));
                }
                case "acos": {
                    if (value < -1 || value > 1) {
                        return ScientificCalculatorResult.error("acos input must be between -1 and 1");
                    }
                    double result = Math.acos(value);
                    return ScientificCalculatorResult.success(formatResult(isDeg ? Math.toDegrees(result) : result));
                }
                case "atan": {
                    double result = Math.atan(value);
                    return ScientificCalculatorResult.success(formatResult(isDeg ? Math.toDegrees(result) : result));
                }
                case "log": {
                    if (value <= 0) {
                        return ScientificCalculatorResult.error("log requires positive number");
                    }
                    return ScientificCalculatorResult.success(formatResult(Math.log10(value)));
                }
                case "ln": {
                    if (value <= 0) {
                        return ScientificCalculatorResult.error("ln requires positive number");
                    }
                    return ScientificCalculatorResult.success(formatResult(Math.log(value)));
                }
                case "sqrt": {
                    if (value < 0) {
                        return ScientificCalculatorResult.error("Square root of negative number");
                    }
                    return ScientificCalculatorResult.success(formatResult(Math.sqrt(value)));
                }
                case "square":   return ScientificCalculatorResult.success(formatResult(value * value));
                case "cube":     return ScientificCalculatorResult.success(formatResult(value * value * value));
                case "factorial": {
                    if (value < 0 || value != Math.floor(value)) {
                        return ScientificCalculatorResult.error("Factorial requires a non-negative integer");
                    }
                    if (value > 20) {
                        return ScientificCalculatorResult.error("Input too large for factorial (max 20)");
                    }
                    long n = (long) value;
                    long result = 1;
                    for (long i = 2; i <= n; i++) {
                        result *= i;
                    }
                    return ScientificCalculatorResult.success(String.valueOf(result));
                }
                case "power": {
                    // exponent is passed via the value field when operation is power;
                    // the form carries a separate exponent accessor — check the ScientificCalculatorForm
                    // for an exponent() field. If not present, re-read form.value() as base only
                    // and require exponent to be provided separately via form.
                    // ScientificCalculatorForm has: value, operation, mode — no exponent field.
                    // We fall through to unsupported since the form has no exponent component.
                    return ScientificCalculatorResult.error("power operation requires an exponent — use a form with exponent field");
                }
                default:
                    return ScientificCalculatorResult.error("Unknown operation: " + operation);
            }

        } catch (NumberFormatException e) {
            return ScientificCalculatorResult.error("Invalid number format");
        } catch (Exception e) {
            return ScientificCalculatorResult.error(e.getMessage());
        }
    }

    private double toRadians(double value, boolean isDeg) {
        return isDeg ? Math.toRadians(value) : value;
    }

    private String formatResult(double value) {
        if (Double.isInfinite(value)) {
            return "Infinite";
        }
        if (Double.isNaN(value)) {
            return "NaN";
        }
        double rounded = Math.round(value * 1e10) / 1e10;
        if (rounded == Math.floor(rounded) && Math.abs(rounded) < 1e15) {
            return String.valueOf((long) rounded);
        }
        return String.valueOf(rounded);
    }
}
