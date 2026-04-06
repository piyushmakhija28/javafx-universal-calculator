package com.techdeveloper.calculator.service;

import com.techdeveloper.calculator.service.impl.ServiceFactory;

/**
 * Public interface for the function plotter service.
 * Use {@link #newInstance()} to obtain an instance via ServiceFactory.
 */
public interface PlotterService {

    /**
     * Validates whether an expression contains only legal tokens.
     *
     * @param expression the f(x) expression string (may be null)
     * @return true if the expression is non-empty and passes tokenisation
     */
    boolean validate(String expression);

    /**
     * Evaluates the expression for a given x value.
     *
     * @param expression the f(x) expression
     * @param x          the value to substitute for the variable x
     * @return the computed y value, or Double.NaN if a domain error occurs
     * @throws IllegalArgumentException if the expression cannot be parsed
     */
    double evaluate(String expression, double x);

    /**
     * Samples the expression over [xMin, xMax] at {@code points} evenly-spaced x positions.
     * Points where the function is undefined or infinite are mapped to Double.NaN.
     *
     * @param expression the f(x) expression
     * @param xMin       left bound of the x range
     * @param xMax       right bound of the x range
     * @param points     number of sample points (must be >= 2)
     * @return double[] of y-values, length == points; undefined points are Double.NaN
     */
    double[] sample(String expression, double xMin, double xMax, int points);

    /**
     * Returns a PlotterService instance from the ServiceFactory.
     */
    static PlotterService newInstance() {
        return ServiceFactory.getInstance().getPlotterService();
    }
}
