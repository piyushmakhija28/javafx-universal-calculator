package com.techdeveloper.calculator.service.impl;

import com.techdeveloper.calculator.constants.NumberBase;
import com.techdeveloper.calculator.dto.ProgrammerCalculatorResult;
import com.techdeveloper.calculator.form.ProgrammerCalculatorForm;
import com.techdeveloper.calculator.service.CalculatorService;

/**
 * Package-private implementation of programmer/bitwise calculator operations.
 * Reads value, operation, operand2, inputBase, and outputBase from the typed form.
 */
class ProgrammerCalculatorServiceImpl implements CalculatorService<ProgrammerCalculatorForm, ProgrammerCalculatorResult> {

    private ProgrammerCalculatorServiceImpl() {}

    static ProgrammerCalculatorServiceImpl newInstance() {
        return new ProgrammerCalculatorServiceImpl();
    }

    @Override
    public ProgrammerCalculatorResult calculate(ProgrammerCalculatorForm form) {
        try {
            String valueStr = form.value();
            String operation = form.operation();

            if (valueStr == null || valueStr.trim().isEmpty()) {
                return ProgrammerCalculatorResult.error("value is required");
            }
            if (operation == null || operation.trim().isEmpty()) {
                return ProgrammerCalculatorResult.error("operation is required");
            }

            operation = operation.trim().toUpperCase();
            NumberBase inputBase = form.inputBase() != null ? form.inputBase() : NumberBase.DEC;
            NumberBase outputBase = form.outputBase() != null ? form.outputBase() : NumberBase.DEC;

            int inputRadix = baseToRadix(inputBase);
            int outputRadix = baseToRadix(outputBase);

            int value;
            try {
                value = Integer.parseInt(valueStr.trim(), inputRadix);
            } catch (NumberFormatException e) {
                return ProgrammerCalculatorResult.error("Invalid value for base " + inputBase.name());
            }

            if ("NOT".equals(operation)) {
                return buildResult(~value, outputRadix, outputBase);
            }
            if ("CONVERT".equals(operation)) {
                return buildResult(value, outputRadix, outputBase);
            }

            String op2Str = form.operand2();
            if (op2Str == null || op2Str.trim().isEmpty()) {
                return ProgrammerCalculatorResult.error("operand2 is required for " + operation);
            }
            int operand2;
            try {
                operand2 = Integer.parseInt(op2Str.trim(), inputRadix);
            } catch (NumberFormatException e) {
                return ProgrammerCalculatorResult.error("Invalid operand2 for base " + inputBase.name());
            }

            switch (operation) {
                case "AND": return buildResult(value & operand2, outputRadix, outputBase);
                case "OR":  return buildResult(value | operand2, outputRadix, outputBase);
                case "XOR": return buildResult(value ^ operand2, outputRadix, outputBase);
                case "SHL": return buildResult(value << operand2, outputRadix, outputBase);
                case "SHR": return buildResult(value >> operand2, outputRadix, outputBase);
                case "MOD": {
                    if (operand2 == 0) {
                        return ProgrammerCalculatorResult.error("Division by zero");
                    }
                    return buildResult(value % operand2, outputRadix, outputBase);
                }
                default:
                    return ProgrammerCalculatorResult.error("Unknown operation: " + operation);
            }

        } catch (Exception e) {
            return ProgrammerCalculatorResult.error(e.getMessage());
        }
    }

    private int baseToRadix(NumberBase base) {
        switch (base) {
            case HEX: return 16;
            case DEC: return 10;
            case OCT: return 8;
            case BIN: return 2;
            default:  return 10;
        }
    }

    private ProgrammerCalculatorResult buildResult(int value, int outputRadix, NumberBase outputBase) {
        String valueStr = Integer.toString(value, outputRadix).toUpperCase();
        return ProgrammerCalculatorResult.success(valueStr, outputBase);
    }
}
