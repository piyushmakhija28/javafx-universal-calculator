package com.techdeveloper.calculator.service.impl;

import com.techdeveloper.calculator.dto.BasicCalculatorResult;
import com.techdeveloper.calculator.form.BasicCalculatorForm;
import com.techdeveloper.calculator.service.CalculatorService;

/**
 * Package-private implementation of basic arithmetic operations: +, -, *, /, %, sqrt, negate.
 * For sqrt and +/-: only operand1 is used.
 */
class BasicCalculatorServiceImpl implements CalculatorService<BasicCalculatorForm, BasicCalculatorResult> {

    private BasicCalculatorServiceImpl() {}

    static BasicCalculatorServiceImpl newInstance() {
        return new BasicCalculatorServiceImpl();
    }

    @Override
    public BasicCalculatorResult calculate(BasicCalculatorForm form) {
        try {
            String op1Str = form.operand1();
            String operator = form.operator();

            if (op1Str == null || op1Str.trim().isEmpty()) {
                return BasicCalculatorResult.error("operand1 is required");
            }
            if (operator == null || operator.trim().isEmpty()) {
                return BasicCalculatorResult.error("operator is required");
            }

            double operand1 = Double.parseDouble(op1Str.trim());

            switch (operator.trim()) {
                case "sqrt":
                case "\u221a": {
                    if (operand1 < 0) {
                        return BasicCalculatorResult.error("Square root of negative number");
                    }
                    return BasicCalculatorResult.success(formatResult(Math.sqrt(operand1)));
                }
                case "+-":
                case "\u00b1": {
                    return BasicCalculatorResult.success(formatResult(-operand1));
                }
                default:
                    break;
            }

            String op2Str = form.operand2();
            if (op2Str == null || op2Str.trim().isEmpty()) {
                return BasicCalculatorResult.error("operand2 is required");
            }
            double operand2 = Double.parseDouble(op2Str.trim());

            switch (operator.trim()) {
                case "+": return BasicCalculatorResult.success(formatResult(operand1 + operand2));
                case "-": return BasicCalculatorResult.success(formatResult(operand1 - operand2));
                case "*": return BasicCalculatorResult.success(formatResult(operand1 * operand2));
                case "/": {
                    if (operand2 == 0) {
                        return BasicCalculatorResult.error("Division by zero");
                    }
                    return BasicCalculatorResult.success(formatResult(operand1 / operand2));
                }
                case "%": {
                    if (operand2 == 0) {
                        return BasicCalculatorResult.error("Division by zero");
                    }
                    return BasicCalculatorResult.success(formatResult(operand1 % operand2));
                }
                default:
                    return BasicCalculatorResult.error("Unknown operator: " + operator);
            }

        } catch (NumberFormatException e) {
            return BasicCalculatorResult.error("Invalid number format");
        } catch (Exception e) {
            return BasicCalculatorResult.error(e.getMessage());
        }
    }

    private String formatResult(double value) {
        if (value == Math.floor(value) && !Double.isInfinite(value)) {
            return String.valueOf((long) value);
        }
        return String.valueOf(value);
    }
}
