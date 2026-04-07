package com.techdeveloper.calculator.controller.helper;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Helper base class for FunctionPlotterController.
 * Contains drawing constants and extracted drawing methods to keep controller method bodies ≤ 35 lines.
 * All methods and constants are protected for use by the controller subclass.
 */
public class FunctionPlotterHelper {

    protected static final double PADDING       = 40.0;
    protected static final Color  BG_COLOR      = Color.web("#1e1e1e");
    protected static final Color  GRID_COLOR    = Color.web("#2a2a2a");
    protected static final Color  AXIS_X_COLOR  = Color.web("#cc3333");
    protected static final Color  AXIS_Y_COLOR  = Color.web("#3366cc");
    protected static final Color  CURVE_COLOR   = Color.web("#00cc44");
    protected static final Color  LABEL_COLOR   = Color.web("#9e9e9e");
    protected static final Color  ERROR_COLOR   = Color.web("#ff6b6b");
    protected static final Color  STATUS_OK     = Color.web("#9e9e9e");
    protected static final int    SAMPLE_POINTS = 500;

    protected static String fmt(double value) {
        if (value == Math.floor(value) && !Double.isInfinite(value) && Math.abs(value) < 1e6) {
            return String.valueOf((long) value);
        }
        return String.format("%.2f", value);
    }

    protected double[] findYBounds(double[] yValues) {
        double yMin = Double.MAX_VALUE, yMax = -Double.MAX_VALUE;
        for (double y : yValues) {
            if (!Double.isNaN(y)) { yMin = Math.min(yMin, y); yMax = Math.max(yMax, y); }
        }
        if (yMin == Double.MAX_VALUE) return new double[]{Double.NaN, Double.NaN, Double.NaN};
        double yRange = yMax - yMin;
        if (yRange < 1e-12) { yMin -= 1.0; yMax += 1.0; yRange = 2.0; }
        else { yMin -= yRange * 0.05; yMax += yRange * 0.05; yRange = yMax - yMin; }
        return new double[]{yMin, yMax, yRange};
    }

    protected void drawBackground(GraphicsContext gc, double W, double H) {
        gc.setFill(BG_COLOR);
        gc.fillRect(0, 0, W, H);
    }

    protected void drawGrid(GraphicsContext gc, double W, double H) {
        gc.setStroke(GRID_COLOR);
        gc.setLineWidth(0.5);
        for (double px = PADDING; px <= W - PADDING; px += 50.0)
            gc.strokeLine(px, PADDING, px, H - PADDING);
        for (double py = PADDING; py <= H - PADDING; py += 50.0)
            gc.strokeLine(PADDING, py, W - PADDING, py);
    }

    protected double computeXAxisPy(double yMin, double yMax, double yRange, double H, double plotH) {
        if (yMin <= 0 && 0 <= yMax) return H - PADDING - ((0 - yMin) / yRange) * plotH;
        return yMax < 0 ? PADDING : H - PADDING;
    }

    protected double computeYAxisPx(double xMin, double xMax, double xRange, double W, double plotW) {
        if (xMin <= 0 && 0 <= xMax) return PADDING + ((-xMin) / xRange) * plotW;
        return xMax < 0 ? W - PADDING : PADDING;
    }

    protected void drawAxes(GraphicsContext gc, double xAxisPy, double yAxisPx, double W, double H) {
        gc.setLineWidth(1.0);
        gc.setStroke(AXIS_X_COLOR);
        gc.strokeLine(PADDING, xAxisPy, W - PADDING, xAxisPy);
        gc.setStroke(AXIS_Y_COLOR);
        gc.strokeLine(yAxisPx, PADDING, yAxisPx, H - PADDING);
    }

    protected void drawAxisLabels(GraphicsContext gc, double xMin, double xRange,
            double yMin, double yRange, double W, double H, double plotW, double plotH) {
        gc.setFill(LABEL_COLOR);
        gc.setFont(javafx.scene.text.Font.font("Consolas", 10));
        for (int i = 0; i <= 5; i++) {
            double xVal = xMin + (xRange * i / 5);
            double labelPx = PADDING + (plotW * i / 5);
            gc.fillText(fmt(xVal), labelPx - 12, H - PADDING + 14);
        }
        for (int i = 0; i <= 5; i++) {
            double yVal = yMin + (yRange * i / 5);
            double labelPy = H - PADDING - (plotH * i / 5);
            gc.fillText(fmt(yVal), 2, labelPy + 4);
        }
    }

    protected void drawCurve(GraphicsContext gc, double[] yValues,
            double xMin, double xRange, double yMin, double yRange,
            double W, double H, double plotW, double plotH) {
        gc.setStroke(CURVE_COLOR);
        gc.setLineWidth(1.5);
        double prevPx = Double.NaN, prevPy = Double.NaN;
        for (int i = 0; i < yValues.length; i++) {
            double yVal = yValues[i];
            if (Double.isNaN(yVal)) { prevPx = Double.NaN; prevPy = Double.NaN; continue; }
            double xVal = xMin + (xRange * i / (yValues.length - 1));
            double px = PADDING + ((xVal - xMin) / xRange) * plotW;
            double py = H - PADDING - ((yVal - yMin) / yRange) * plotH;
            py = Math.max(PADDING - 2, Math.min(H - PADDING + 2, py));
            if (!Double.isNaN(prevPx)) gc.strokeLine(prevPx, prevPy, px, py);
            prevPx = px; prevPy = py;
        }
    }
}
