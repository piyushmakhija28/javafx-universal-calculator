package com.techdeveloper.calculator.service;

import java.util.Map;

/**
 * Implements basic arithmetic operations: +, -, *, /, %, sqrt, negate.
 * Required inputs: "operand1", "operator", "operand2"
 * For sqrt and +/-: only "operand1" is used.
 */
public class BasicCalculatorService implements CalculatorService {

    @Override
    public String calculate(Map<String, String> inputs) {
        try {
            String op1Str = inputs.get("operand1");
            String operator = inputs.get("operator");

            if (op1Str == null || op1Str.trim().isEmpty()) {
                return "Error: operand1 is required";
            }
            if (operator == null || operator.trim().isEmpty()) {
                return "Error: operator is required";
            }

            double operand1 = Double.parseDouble(op1Str.trim());

            switch (operator.trim()) {
                case "sqrt":
                case "\u221a": {
                    if (operand1 < 0) {
                        return "Error: Square root of negative number";
                    }
                    double result = Math.sqrt(operand1);
                    return formatResult(result);
                }
                case "+-":
                case "\u00b1": {
                    double result = -operand1;
                    return formatResult(result);
                }
                default:
                    break;
            }

            String op2Str = inputs.get("operand2");
            if (op2Str == null || op2Str.trim().isEmpty()) {
                return "Error: operand2 is required";
            }
            double operand2 = Double.parseDouble(op2Str.trim());

            switch (operator.trim()) {
                case "+": return formatResult(operand1 + operand2);
                case "-": return formatResult(operand1 - operand2);
                case "*": return formatResult(operand1 * operand2);
                case "/": {
                    if (operand2 == 0) {
                        return "Error: Division by zero";
                    }
                    return formatResult(operand1 / operand2);
                }
                case "%": {
                    if (operand2 == 0) {
                        return "Error: Division by zero";
                    }
                    return formatResult(operand1 % operand2);
                }
                default:
                    return "Error: Unknown operator: " + operator;
            }

        } catch (NumberFormatException e) {
            return "Error: Invalid number format";
        } catch (NullPointerException e) {
            return "Error: Missing required input";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private String formatResult(double value) {
        if (value == Math.floor(value) && !Double.isInfinite(value)) {
            return String.valueOf((long) value);
        }
        return String.valueOf(value);
    }
}
