package com.techdeveloper.calculator.service;

import java.util.Map;

/**
 * Implements programmer/bitwise calculator operations.
 * Required inputs: "value", "operation"
 * Optional inputs: "operand2" (for binary ops), "inputBase" (HEX/DEC/OCT/BIN), "outputBase"
 * Base defaults to DEC if not specified.
 * Bitwise operations work on 32-bit signed integers.
 */
public class ProgrammerCalculatorService implements CalculatorService {

    public ProgrammerCalculatorService() {
        // required for FXML
    }

    @Override
    public String calculate(Map<String, String> inputs) {
        try {
            String valueStr = inputs.get("value");
            String operation = inputs.get("operation");

            if (valueStr == null || valueStr.trim().isEmpty()) {
                return "Error: value is required";
            }
            if (operation == null || operation.trim().isEmpty()) {
                return "Error: operation is required";
            }

            operation = operation.trim().toUpperCase();
            String inputBase = inputs.getOrDefault("inputBase", "DEC").trim().toUpperCase();
            String outputBase = inputs.getOrDefault("outputBase", "DEC").trim().toUpperCase();

            int inputRadix = baseToRadix(inputBase);
            if (inputRadix < 0) {
                return "Error: Invalid inputBase. Use HEX, DEC, OCT, or BIN";
            }
            int outputRadix = baseToRadix(outputBase);
            if (outputRadix < 0) {
                return "Error: Invalid outputBase. Use HEX, DEC, OCT, or BIN";
            }

            int value;
            try {
                value = Integer.parseInt(valueStr.trim(), inputRadix);
            } catch (NumberFormatException e) {
                return "Error: Invalid value for base " + inputBase;
            }

            // NOT is unary — no operand2 needed
            if ("NOT".equals(operation)) {
                return formatResult(~value, outputRadix, outputBase);
            }

            // CONVERT base — no operation on value, just re-format
            if ("CONVERT".equals(operation)) {
                return formatResult(value, outputRadix, outputBase);
            }

            // All remaining operations require operand2
            String op2Str = inputs.get("operand2");
            if (op2Str == null || op2Str.trim().isEmpty()) {
                return "Error: operand2 is required for " + operation;
            }
            int operand2;
            try {
                operand2 = Integer.parseInt(op2Str.trim(), inputRadix);
            } catch (NumberFormatException e) {
                return "Error: Invalid operand2 for base " + inputBase;
            }

            switch (operation) {
                case "AND": return formatResult(value & operand2, outputRadix, outputBase);
                case "OR":  return formatResult(value | operand2, outputRadix, outputBase);
                case "XOR": return formatResult(value ^ operand2, outputRadix, outputBase);
                case "SHL": return formatResult(value << operand2, outputRadix, outputBase);
                case "SHR": return formatResult(value >> operand2, outputRadix, outputBase);
                case "MOD": {
                    if (operand2 == 0) {
                        return "Error: Division by zero";
                    }
                    return formatResult(value % operand2, outputRadix, outputBase);
                }
                default:
                    return "Error: Unknown operation: " + operation;
            }

        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private int baseToRadix(String base) {
        switch (base) {
            case "HEX": return 16;
            case "DEC": return 10;
            case "OCT": return 8;
            case "BIN": return 2;
            default:    return -1;
        }
    }

    private String formatResult(int value, int outputRadix, String outputBase) {
        String result = Integer.toString(value, outputRadix).toUpperCase();
        return outputBase + ": " + result;
    }
}
