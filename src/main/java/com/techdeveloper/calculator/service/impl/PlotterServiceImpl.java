package com.techdeveloper.calculator.service.impl;

import com.techdeveloper.calculator.service.PlotterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Package-private implementation of {@link PlotterService}.
 * Evaluates single-variable mathematical expressions for function plotting.
 * Stateless after construction; safe to share as a singleton.
 *
 * Supported tokens:
 *   Variables : x
 *   Constants : pi, e
 *   Functions : sin(x), cos(x), tan(x), log(x), sqrt(x), abs(x)
 *   Operators : ^ (right-associative), *, /, +, - (with standard precedence)
 *   Grouping  : ( )
 */
final class PlotterServiceImpl implements PlotterService {

    private static final Logger log = LoggerFactory.getLogger(PlotterServiceImpl.class);

    private PlotterServiceImpl() {}

    static PlotterServiceImpl newInstance() {
        return new PlotterServiceImpl();
    }

    @Override
    public boolean validate(String expression) {
        if (expression == null || expression.isBlank()) {
            return false;
        }
        try {
            evaluate(expression, 0.0);
            return true;
        } catch (Exception e) {
            log.debug("Expression validation failed for '{}': {}", expression, e.getMessage());
            return false;
        }
    }

    @Override
    public double evaluate(String expression, double x) {
        if (expression == null || expression.isBlank()) {
            throw new IllegalArgumentException("Expression must not be empty");
        }
        Parser parser = new Parser(expression.trim(), x);
        double result = parser.parseExpr();
        parser.expectEnd();
        return result;
    }

    @Override
    public double[] sample(String expression, double xMin, double xMax, int points) {
        if (points < 2) {
            log.warn("sample() called with points={}, returning empty array", points);
            return new double[0];
        }
        if (xMin >= xMax) {
            log.warn("sample() called with xMin={} >= xMax={}, returning empty array", xMin, xMax);
            return new double[0];
        }

        double[] yValues = new double[points];
        double step = (xMax - xMin) / (points - 1);

        for (int i = 0; i < points; i++) {
            double x = xMin + i * step;
            try {
                double y = evaluate(expression, x);
                yValues[i] = (Double.isInfinite(y) || Double.isNaN(y)) ? Double.NaN : y;
            } catch (Exception e) {
                if (i == 0) {
                    log.warn("PlotterService.sample() parse error for expression='{}': {}",
                             expression, e.getMessage());
                    return new double[0];
                }
                yValues[i] = Double.NaN;
            }
        }

        log.debug("sample() expression='{}' xMin={} xMax={} points={} computed",
                  expression, xMin, xMax, points);
        return yValues;
    }

    // ── Recursive Descent Parser ────────────────────────────────────────────

    private static final class Parser {

        private final String input;
        private final double x;
        private int pos;

        Parser(String input, double x) {
            this.input = input;
            this.x = x;
            this.pos = 0;
        }

        double parseExpr() {
            double result = parseTerm();
            while (pos < input.length()) {
                skipWhitespace();
                if (pos < input.length() && input.charAt(pos) == '+') {
                    pos++;
                    result += parseTerm();
                } else if (pos < input.length() && input.charAt(pos) == '-') {
                    pos++;
                    result -= parseTerm();
                } else {
                    break;
                }
            }
            return result;
        }

        private double parseTerm() {
            double result = parsePower();
            while (pos < input.length()) {
                skipWhitespace();
                if (pos < input.length() && input.charAt(pos) == '*') {
                    pos++;
                    result *= parsePower();
                } else if (pos < input.length() && input.charAt(pos) == '/') {
                    pos++;
                    double divisor = parsePower();
                    if (divisor == 0.0) {
                        return Double.NaN;
                    }
                    result /= divisor;
                } else {
                    break;
                }
            }
            return result;
        }

        private double parsePower() {
            double base = parseUnary();
            skipWhitespace();
            if (pos < input.length() && input.charAt(pos) == '^') {
                pos++;
                double exponent = parsePower();
                return Math.pow(base, exponent);
            }
            return base;
        }

        private double parseUnary() {
            skipWhitespace();
            if (pos < input.length() && input.charAt(pos) == '-') {
                pos++;
                return -parseUnary();
            }
            return parsePrimary();
        }

        private double parsePrimary() {
            skipWhitespace();
            if (pos >= input.length()) {
                throw new IllegalArgumentException("Unexpected end of expression at position " + pos);
            }

            char ch = input.charAt(pos);

            if (ch == '(') {
                pos++;
                double value = parseExpr();
                skipWhitespace();
                expectChar(')');
                return value;
            }

            if (Character.isDigit(ch) || ch == '.') {
                return parseNumber();
            }

            if (Character.isLetter(ch)) {
                String name = parseName();
                return resolveNamedToken(name);
            }

            throw new IllegalArgumentException(
                "Unexpected character '" + ch + "' at position " + pos);
        }

        private double parseNumber() {
            int start = pos;
            while (pos < input.length() &&
                   (Character.isDigit(input.charAt(pos)) || input.charAt(pos) == '.' ||
                    input.charAt(pos) == 'E' || input.charAt(pos) == 'e' ||
                    ((input.charAt(pos) == '+' || input.charAt(pos) == '-') &&
                     pos > start && (input.charAt(pos - 1) == 'E' || input.charAt(pos - 1) == 'e')))) {
                pos++;
            }
            String numStr = input.substring(start, pos);
            try {
                return Double.parseDouble(numStr);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid number literal: '" + numStr + "'");
            }
        }

        private String parseName() {
            int start = pos;
            while (pos < input.length() && Character.isLetter(input.charAt(pos))) {
                pos++;
            }
            return input.substring(start, pos);
        }

        private double resolveNamedToken(String name) {
            switch (name.toLowerCase()) {
                case "x":   return x;
                case "pi":  return Math.PI;
                case "e":   return Math.E;
                case "sin": return Math.sin(parseFunctionArg());
                case "cos": return Math.cos(parseFunctionArg());
                case "tan": {
                    double arg = parseFunctionArg();
                    double cosArg = Math.cos(arg);
                    if (Math.abs(cosArg) < 1e-12) {
                        return Double.NaN;
                    }
                    return Math.tan(arg);
                }
                case "log": {
                    double arg = parseFunctionArg();
                    return arg <= 0 ? Double.NaN : Math.log10(arg);
                }
                case "ln": {
                    double arg = parseFunctionArg();
                    return arg <= 0 ? Double.NaN : Math.log(arg);
                }
                case "sqrt": {
                    double arg = parseFunctionArg();
                    return arg < 0 ? Double.NaN : Math.sqrt(arg);
                }
                case "abs": return Math.abs(parseFunctionArg());
                default:
                    throw new IllegalArgumentException("Unknown function or variable: '" + name + "'");
            }
        }

        private double parseFunctionArg() {
            skipWhitespace();
            expectChar('(');
            double value = parseExpr();
            skipWhitespace();
            expectChar(')');
            return value;
        }

        private void skipWhitespace() {
            while (pos < input.length() && Character.isWhitespace(input.charAt(pos))) {
                pos++;
            }
        }

        private void expectChar(char expected) {
            if (pos >= input.length() || input.charAt(pos) != expected) {
                String found = (pos < input.length()) ? "'" + input.charAt(pos) + "'" : "end of input";
                throw new IllegalArgumentException(
                    "Expected '" + expected + "' at position " + pos + " but found " + found);
            }
            pos++;
        }

        void expectEnd() {
            skipWhitespace();
            if (pos < input.length()) {
                throw new IllegalArgumentException(
                    "Unexpected characters after expression: '"
                    + input.substring(pos) + "'");
            }
        }
    }
}
