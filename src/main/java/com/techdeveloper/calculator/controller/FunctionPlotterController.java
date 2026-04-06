package com.techdeveloper.calculator.controller;

import com.techdeveloper.calculator.service.PlotterService;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for function-plotter.fxml.
 *
 * Threading contract (from javafx-ide-designer skill):
 *   - PlotterService.sample() runs in a background Task.
 *   - All Canvas writes happen on the JavaFX Application Thread via Platform.runLater().
 *   - The active Task is cancelled when a new plot is requested to avoid stale renders.
 *
 * Drawing details:
 *   - Canvas background : #1e1e1e
 *   - Grid lines        : #2a2a2a (subtle)
 *   - X-axis            : #cc3333 (red)
 *   - Y-axis            : #3366cc (blue)
 *   - Function curve    : #00cc44 (green)
 *   - Padding           : 40px on each side for axis labels
 */
public class FunctionPlotterController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(FunctionPlotterController.class);

    // ── Drawing constants ────────────────────────────────────────────────────
    private static final double PADDING        = 40.0;
    private static final Color  BG_COLOR       = Color.web("#1e1e1e");
    private static final Color  GRID_COLOR     = Color.web("#2a2a2a");
    private static final Color  AXIS_X_COLOR   = Color.web("#cc3333");
    private static final Color  AXIS_Y_COLOR   = Color.web("#3366cc");
    private static final Color  CURVE_COLOR    = Color.web("#00cc44");
    private static final Color  LABEL_COLOR    = Color.web("#9e9e9e");
    private static final Color  ERROR_COLOR    = Color.web("#ff6b6b");
    private static final Color  STATUS_OK      = Color.web("#9e9e9e");
    private static final int    SAMPLE_POINTS  = 500;

    // ── FXML injected fields ─────────────────────────────────────────────────
    @FXML private TextField fieldExpression;
    @FXML private Slider    sliderXMin;
    @FXML private Slider    sliderXMax;
    @FXML private Label     labelRange;
    @FXML private Label     labelStatus;
    @FXML private Canvas    plotCanvas;
    @FXML private Button    btnPlot;
    @FXML private Button    btnClear;

    // ── Service ──────────────────────────────────────────────────────────────
    private final PlotterService plotterService = new PlotterService();

    // ── State ────────────────────────────────────────────────────────────────
    /** Currently running plot task — cancelled before starting a new one. */
    private Task<double[]> activeTask;

    // ── Lifecycle ────────────────────────────────────────────────────────────

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Slider defaults (also specified in FXML; set here for safety)
        sliderXMin.setMin(-20);
        sliderXMin.setMax(0);
        sliderXMin.setValue(-10);

        sliderXMax.setMin(0);
        sliderXMax.setMax(20);
        sliderXMax.setValue(10);

        // Bind labelRange to both sliders so it updates live
        ChangeListener<Number> rangeListener = (obs, oldVal, newVal) ->
            updateRangeLabel(sliderXMin.getValue(), sliderXMax.getValue());

        sliderXMin.valueProperty().addListener(rangeListener);
        sliderXMax.valueProperty().addListener(rangeListener);

        // Seed the label with the initial values
        updateRangeLabel(sliderXMin.getValue(), sliderXMax.getValue());

        // Draw empty dark canvas so the area is not blank white on startup
        drawIdleCanvas();

        log.debug("FunctionPlotterController initialized");
    }

    // ── Event handlers ────────────────────────────────────────────────────────

    @FXML
    private void onPlot() {
        String expression = fieldExpression.getText().trim();

        // Validate before launching the background task
        if (expression.isEmpty()) {
            setStatus("Enter an expression, e.g. sin(x)", false);
            return;
        }

        if (!plotterService.validate(expression)) {
            setStatus("Invalid expression: check syntax (supported: sin, cos, tan, log, sqrt, abs, pi, e, ^, *, /, +, -)", false);
            return;
        }

        double xMin = sliderXMin.getValue();
        double xMax = sliderXMax.getValue();

        // Prevent degenerate range — XMin constraint is 0..max, XMax is 0..20, so they
        // should not overlap, but guard anyway.
        if (xMin >= xMax) {
            setStatus("X min must be less than X max", false);
            return;
        }

        // Cancel any running task before starting a new one (prevents memory leak)
        if (activeTask != null && activeTask.isRunning()) {
            activeTask.cancel(true);
            log.debug("Cancelled previous plot task");
        }

        setStatus("Computing...", true);
        btnPlot.setDisable(true);

        final String exprFinal  = expression;
        final double xMinFinal  = xMin;
        final double xMaxFinal  = xMax;

        Task<double[]> task = new Task<>() {
            @Override
            protected double[] call() {
                return plotterService.sample(exprFinal, xMinFinal, xMaxFinal, SAMPLE_POINTS);
            }
        };

        task.setOnSucceeded(evt -> {
            double[] yValues = task.getValue();
            Platform.runLater(() -> {
                if (yValues.length == 0) {
                    setStatus("Could not evaluate expression — check for domain errors", false);
                } else {
                    drawPlot(yValues, xMinFinal, xMaxFinal);
                    setStatus("f(x) = " + exprFinal + "  |  x \u2208 [" +
                              fmt(xMinFinal) + ", " + fmt(xMaxFinal) + "]", true);
                }
                btnPlot.setDisable(false);
            });
        });

        task.setOnFailed(evt -> {
            Throwable ex = task.getException();
            log.error("Plot task failed for expression='{}': {}", exprFinal, ex != null ? ex.getMessage() : "null", ex);
            Platform.runLater(() -> {
                setStatus("Error: " + (ex != null ? ex.getMessage() : "Unknown error"), false);
                btnPlot.setDisable(false);
            });
        });

        task.setOnCancelled(evt ->
            Platform.runLater(() -> btnPlot.setDisable(false))
        );

        activeTask = task;
        Thread thread = new Thread(task, "plotter-thread");
        thread.setDaemon(true);   // Do not prevent JVM shutdown
        thread.start();

        log.debug("Plot task started for expression='{}' xMin={} xMax={}", expression, xMin, xMax);
    }

    @FXML
    private void onClear() {
        if (activeTask != null && activeTask.isRunning()) {
            activeTask.cancel(true);
        }
        drawIdleCanvas();
        labelStatus.setText("");
        labelStatus.setStyle("-fx-text-fill: #9e9e9e; -fx-font-size: 12px;");
        btnPlot.setDisable(false);
        log.debug("Canvas cleared");
    }

    // ── Drawing ───────────────────────────────────────────────────────────────

    /**
     * Main plot rendering method.  Always called on the JavaFX Application Thread.
     *
     * @param yValues array of y-samples (length == SAMPLE_POINTS); NaN entries are skipped
     * @param xMin    left bound of x range
     * @param xMax    right bound of x range
     */
    private void drawPlot(double[] yValues, double xMin, double xMax) {
        GraphicsContext gc = plotCanvas.getGraphicsContext2D();
        double W = plotCanvas.getWidth();
        double H = plotCanvas.getHeight();
        double plotW = W - 2 * PADDING;
        double plotH = H - 2 * PADDING;

        // ── Find y range from non-NaN values ──────────────────────────────
        double yMin = Double.MAX_VALUE;
        double yMax = -Double.MAX_VALUE;
        for (double y : yValues) {
            if (!Double.isNaN(y)) {
                yMin = Math.min(yMin, y);
                yMax = Math.max(yMax, y);
            }
        }

        // Guard: no valid y points
        if (yMin == Double.MAX_VALUE) {
            drawIdleCanvas();
            setStatus("Function has no real values in this range", false);
            return;
        }

        // Add 5% vertical margin so the curve does not touch edges
        double yRange = yMax - yMin;
        if (yRange < 1e-12) {
            // Horizontal line — give it some vertical room
            yMin -= 1.0;
            yMax += 1.0;
            yRange = 2.0;
        } else {
            yMin -= yRange * 0.05;
            yMax += yRange * 0.05;
            yRange = yMax - yMin;
        }

        // ── 1. Background fill ─────────────────────────────────────────────
        gc.setFill(BG_COLOR);
        gc.fillRect(0, 0, W, H);

        // ── 2. Grid lines ──────────────────────────────────────────────────
        gc.setStroke(GRID_COLOR);
        gc.setLineWidth(0.5);
        double gridStep = 50.0;  // pixels between grid lines

        for (double px = PADDING; px <= W - PADDING; px += gridStep) {
            gc.strokeLine(px, PADDING, px, H - PADDING);
        }
        for (double py = PADDING; py <= H - PADDING; py += gridStep) {
            gc.strokeLine(PADDING, py, W - PADDING, py);
        }

        // ── Axis pixel positions ───────────────────────────────────────────
        // X-axis (y=0): if 0 is within y range, place it there; else at bottom
        double xAxisPy;
        if (yMin <= 0 && 0 <= yMax) {
            xAxisPy = H - PADDING - ((0 - yMin) / yRange) * plotH;
        } else if (yMax < 0) {
            xAxisPy = PADDING;  // all values negative → axis at top
        } else {
            xAxisPy = H - PADDING;  // all values positive → axis at bottom
        }

        // Y-axis (x=0): if 0 is within x range, place it there; else at left
        double yAxisPx;
        double xRange = xMax - xMin;
        if (xMin <= 0 && 0 <= xMax) {
            yAxisPx = PADDING + ((-xMin) / xRange) * plotW;
        } else if (xMax < 0) {
            yAxisPx = W - PADDING;  // all x values negative → y-axis at right
        } else {
            yAxisPx = PADDING;  // all x values positive → y-axis at left
        }

        // ── 3. Axis lines ──────────────────────────────────────────────────
        gc.setLineWidth(1.0);

        // X-axis (red)
        gc.setStroke(AXIS_X_COLOR);
        gc.strokeLine(PADDING, xAxisPy, W - PADDING, xAxisPy);

        // Y-axis (blue)
        gc.setStroke(AXIS_Y_COLOR);
        gc.strokeLine(yAxisPx, PADDING, yAxisPx, H - PADDING);

        // ── 4. Axis labels ─────────────────────────────────────────────────
        gc.setFill(LABEL_COLOR);
        gc.setFont(javafx.scene.text.Font.font("Consolas", 10));

        // X-axis: labels at grid line positions
        int xLabelCount = 5;
        for (int i = 0; i <= xLabelCount; i++) {
            double xVal   = xMin + (xRange * i / xLabelCount);
            double labelPx = PADDING + (plotW * i / xLabelCount);
            gc.fillText(fmt(xVal), labelPx - 12, H - PADDING + 14);
        }

        // Y-axis: labels along left edge
        int yLabelCount = 5;
        for (int i = 0; i <= yLabelCount; i++) {
            double yVal   = yMin + (yRange * i / yLabelCount);
            double labelPy = H - PADDING - (plotH * i / yLabelCount);
            gc.fillText(fmt(yVal), 2, labelPy + 4);
        }

        // ── 5. Function curve (green) ─────────────────────────────────────
        gc.setStroke(CURVE_COLOR);
        gc.setLineWidth(1.5);

        double prevPx = Double.NaN;
        double prevPy = Double.NaN;

        for (int i = 0; i < yValues.length; i++) {
            double xVal = xMin + (xRange * i / (yValues.length - 1));
            double yVal = yValues[i];

            if (Double.isNaN(yVal)) {
                // Break the polyline — skip this segment
                prevPx = Double.NaN;
                prevPy = Double.NaN;
                continue;
            }

            double px = PADDING + ((xVal - xMin) / xRange) * plotW;
            double py = H - PADDING - ((yVal - yMin) / yRange) * plotH;

            // Clamp to canvas bounds so clipped segments do not draw wild diagonals
            py = Math.max(PADDING - 2, Math.min(H - PADDING + 2, py));

            if (!Double.isNaN(prevPx)) {
                gc.strokeLine(prevPx, prevPy, px, py);
            }

            prevPx = px;
            prevPy = py;
        }

        log.debug("drawPlot complete: yMin={} yMax={} xMin={} xMax={}", yMin, yMax, xMin, xMax);
    }

    /**
     * Fills the canvas with the dark background and a hint text — used for idle and cleared state.
     * Must be called on the JavaFX Application Thread.
     */
    private void drawIdleCanvas() {
        GraphicsContext gc = plotCanvas.getGraphicsContext2D();
        double W = plotCanvas.getWidth();
        double H = plotCanvas.getHeight();
        gc.setFill(BG_COLOR);
        gc.fillRect(0, 0, W, H);
        gc.setFill(Color.web("#3a3a3a"));
        gc.setFont(javafx.scene.text.Font.font("Segoe UI", 14));
        gc.fillText("Enter an expression and press Plot", PADDING, H / 2.0);
    }

    // ── Utilities ─────────────────────────────────────────────────────────────

    private void updateRangeLabel(double xMin, double xMax) {
        labelRange.setText("[" + fmt(xMin) + ", " + fmt(xMax) + "]");
    }

    private void setStatus(String message, boolean isOk) {
        String color = isOk ? STATUS_OK.toString() : "#ff6b6b";
        // JavaFX Color.toString() gives a hex code we can use in inline style
        labelStatus.setStyle("-fx-text-fill: " + (isOk ? "#9e9e9e" : "#ff6b6b") + "; -fx-font-size: 12px;");
        labelStatus.setText(message);
    }

    /**
     * Formats a double for display, removing unnecessary trailing decimal zeros.
     * Limits precision to 3 decimal places for readability on axis labels.
     */
    private static String fmt(double value) {
        if (value == Math.floor(value) && !Double.isInfinite(value) && Math.abs(value) < 1e6) {
            return String.valueOf((long) value);
        }
        // Three significant decimal digits
        return String.format("%.2f", value);
    }
}
