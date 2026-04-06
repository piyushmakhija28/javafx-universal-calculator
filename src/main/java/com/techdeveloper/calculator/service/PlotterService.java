package com.techdeveloper.calculator.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * PlotterService — evaluates single-variable mathematical expressions for function plotting.
 *
 * Supported tokens:
 *   Variables : x
 *   Constants : pi, e
 *   Functions : sin(x), cos(x), tan(x), log(x), sqrt(x), abs(x)
 *   Operators : ^ (right-associative), *, /, +, - (with standard precedence)
 *   Grouping  : ( )
 *
 * Parser design: recursive descent with these grammar rules:
 *   expr        -> term (('+' | '-') term)*
 *   term        -> power (('*' | '/') power)*
 *   power       -> unary ('^' power)?          (right-associative)
 *   unary       -> '-' unary | primary
 *   primary     -> number | 'x' | 'pi' | 'e'
 *               | 'sin' '(' expr ')' | 'cos' '(' expr ')'
 *               | 'tan' '(' expr ')' | 'log' '(' expr ')'
 *               | 'sqrt' '(' expr ')' | 'abs' '(' expr ')'
 *               | '(' expr ')'
 *
 * Thread-safety: instances are stateless after construction; safe to share.
 * All exceptions are caught internally — callers receive empty arrays or Double.NaN on error.
 */
public class PlotterService {

    public PlotterService() {
        // required for FXML
    }

    private static final Logger log = LoggerFactory.getLogger(PlotterService.class);

    // ── Public API ──────────────────────────────────────────────────────────

    /**
     * Validates whether an expression contains only legal tokens.
     * A quick structural check — invalid character or malformed token returns false.
     *
     * @param expression the f(x) expression string (may be null)
     * @return true if the expression is non-empty and passes tokenisation
     */
    public boolean validate(String expression) {
        if (expression == null || expression.isBlank()) {
            return false;
        }
        // Try a parse against a known-safe x value; any exception means invalid
        try {
            double result = evaluate(expression, 0.0);
            // NaN from sqrt(-1) is a valid evaluation result, not a parse error
            return true;
        } catch (Exception e) {
            log.debug("Expression validation failed for '{}': {}", expression, e.getMessage());
            return false;
        }
    }

    /**
     * Evaluates the expression for a given x value.
     *
     * @param expression the f(x) expression
     * @param x          the value to substitute for the variable x
     * @return the computed y value, or Double.NaN if a domain error occurs (e.g. sqrt(-1))
     * @throws IllegalArgumentException if the expression cannot be parsed
     */
    public double evaluate(String expression, double x) {
        if (expression == null || expression.isBlank()) {
            throw new IllegalArgumentException("Expression must not be empty");
        }
        Parser parser = new Parser(expression.trim(), x);
        double result = parser.parseExpr();
        parser.expectEnd();
        return result;
    }

    /**
     * Samples the expression over [xMin, xMax] at {@code points} evenly-spaced x positions.
     * Points where the function is undefined or infinite are mapped to Double.NaN.
     * Any parse error returns an empty array (never throws).
     *
     * @param expression the f(x) expression
     * @param xMin       left bound of the x range
     * @param xMax       right bound of the x range
     * @param points     number of sample points (must be >= 2)
     * @return double[] of y-values, length == points; undefined points are Double.NaN
     */
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
                // Parse error on first iteration → log once and abort with empty array
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

    /**
     * Single-use parser object for one expression evaluation.
     * Not thread-safe by design; PlotterService creates a new instance per call.
     */
    private static final class Parser {

        private final String input;
        private final double x;
        private int pos;

        Parser(String input, double x) {
            this.input = input;
            this.x = x;
            this.pos = 0;
        }

        // ── Grammar rules ──────────────────────────────────────────────────

        /** expr -> term (('+' | '-') term)* */
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

        /** term -> power (('*' | '/') power)* */
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
                        return Double.NaN;  // 0/0 or x/0 — domain error
                    }
                    result /= divisor;
                } else {
                    break;
                }
            }
            return result;
        }

        /** power -> unary ('^' power)?  — right-associative */
        private double parsePower() {
            double base = parseUnary();
            skipWhitespace();
            if (pos < input.length() && input.charAt(pos) == '^') {
                pos++;
                double exponent = parsePower();  // right-recursive for right-associativity
                return Math.pow(base, exponent);
            }
            return base;
        }

        /** unary -> '-' unary | primary */
        private double parseUnary() {
            skipWhitespace();
            if (pos < input.length() && input.charAt(pos) == '-') {
                pos++;
                return -parseUnary();
            }
            return parsePrimary();
        }

        /**
         * primary -> number | 'x' | 'pi' | 'e'
         *          | func '(' expr ')' | '(' expr ')'
         */
        private double parsePrimary() {
            skipWhitespace();
            if (pos >= input.length()) {
                throw new IllegalArgumentException("Unexpected end of expression at position " + pos);
            }

            char ch = input.charAt(pos);

            // Parenthesised sub-expression
            if (ch == '(') {
                pos++;
                double value = parseExpr();
                skipWhitespace();
                expectChar(')');
                return value;
            }

            // Numeric literal (including decimal point and scientific notation)
            if (Character.isDigit(ch) || ch == '.') {
                return parseNumber();
            }

            // Named token: variable, constant, or function
            if (Character.isLetter(ch)) {
                String name = parseName();
                return resolveNamedToken(name);
            }

            throw new IllegalArgumentException(
                "Unexpected character '" + ch + "' at position " + pos);
        }

        // ── Token helpers ──────────────────────────────────────────────────

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
                case "x":
                    return x;

                case "pi":
                    return Math.PI;

                case "e":
                    return Math.E;

                case "sin": {
                    double arg = parseFunctionArg();
                    return Math.sin(arg);
                }

                case "cos": {
                    double arg = parseFunctionArg();
                    return Math.cos(arg);
                }

                case "tan": {
                    double arg = parseFunctionArg();
                    double cosArg = Math.cos(arg);
                    if (Math.abs(cosArg) < 1e-12) {
                        return Double.NaN;  // tan undefined
                    }
                    return Math.tan(arg);
                }

                case "log": {
                    double arg = parseFunctionArg();
                    if (arg <= 0) {
                        return Double.NaN;
                    }
                    return Math.log10(arg);
                }

                case "ln": {
                    double arg = parseFunctionArg();
                    if (arg <= 0) {
                        return Double.NaN;
                    }
                    return Math.log(arg);
                }

                case "sqrt": {
                    double arg = parseFunctionArg();
                    if (arg < 0) {
                        return Double.NaN;
                    }
                    return Math.sqrt(arg);
                }

                case "abs": {
                    double arg = parseFunctionArg();
                    return Math.abs(arg);
                }

                default:
                    throw new IllegalArgumentException("Unknown function or variable: '" + name + "'");
            }
        }

        /** Parses a single function argument enclosed in parentheses: '(' expr ')' */
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
