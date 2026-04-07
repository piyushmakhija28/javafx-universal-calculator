package com.techdeveloper.calculator.controller;

import com.techdeveloper.calculator.controller.helper.FunctionPlotterHelper;
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
 * Threading contract:
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
public class FunctionPlotterController extends FunctionPlotterHelper implements Initializable {

    public FunctionPlotterController() {
        // required for FXML
    }

    private static final Logger log = LoggerFactory.getLogger(FunctionPlotterController.class);

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
    private final PlotterService plotterService = PlotterService.newInstance();

    // ── State ────────────────────────────────────────────────────────────────
    /** Currently running plot task — cancelled before starting a new one. */
    private Task<double[]> activeTask;

    // ── Lifecycle ────────────────────────────────────────────────────────────

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        sliderXMin.setMin(-20);
        sliderXMin.setMax(0);
        sliderXMin.setValue(-10);

        sliderXMax.setMin(0);
        sliderXMax.setMax(20);
        sliderXMax.setValue(10);

        ChangeListener<Number> rangeListener = (obs, oldVal, newVal) ->
            updateRangeLabel(sliderXMin.getValue(), sliderXMax.getValue());

        sliderXMin.valueProperty().addListener(rangeListener);
        sliderXMax.valueProperty().addListener(rangeListener);

        updateRangeLabel(sliderXMin.getValue(), sliderXMax.getValue());
        drawIdleCanvas();

        log.debug("FunctionPlotterController initialized");
    }

    // ── Event handlers ────────────────────────────────────────────────────────

    @FXML
    private void onPlot() {
        String expression = fieldExpression.getText().trim();
        String error = validatePlotRequest(expression);
        if (error != null) { setStatus(error, false); return; }
        if (activeTask != null && activeTask.isRunning()) activeTask.cancel(true);
        setStatus("Computing...", true);
        btnPlot.setDisable(true);
        final double xMin = sliderXMin.getValue(), xMax = sliderXMax.getValue();
        activeTask = buildPlotTask(expression, xMin, xMax);
        wireSuccessHandler(activeTask, expression, xMin, xMax);
        wireFailureHandler(activeTask, expression);
        activeTask.setOnCancelled(evt -> Platform.runLater(() -> btnPlot.setDisable(false)));
        Thread thread = new Thread(activeTask, "plotter-thread");
        thread.setDaemon(true);
        thread.start();
        log.debug("Plot task started for expression='{}' xMin={} xMax={}", expression, xMin, xMax);
    }

    private String validatePlotRequest(String expression) {
        if (expression.isEmpty()) return "Enter an expression, e.g. sin(x)";
        if (!plotterService.validate(expression))
            return "Invalid expression: check syntax (supported: sin, cos, tan, log, sqrt, abs, pi, e, ^, *, /, +, -)";
        if (sliderXMin.getValue() >= sliderXMax.getValue()) return "X min must be less than X max";
        return null;
    }

    private Task<double[]> buildPlotTask(String expr, double xMin, double xMax) {
        return new Task<>() {
            @Override
            protected double[] call() {
                return plotterService.sample(expr, xMin, xMax, SAMPLE_POINTS);
            }
        };
    }

    private void wireSuccessHandler(Task<double[]> task, String expr, double xMin, double xMax) {
        task.setOnSucceeded(evt -> Platform.runLater(() -> {
            double[] yValues = task.getValue();
            if (yValues.length == 0) {
                setStatus("Could not evaluate expression — check for domain errors", false);
            } else {
                drawPlot(yValues, xMin, xMax);
                setStatus("f(x) = " + expr + "  |  x \u2208 [" + fmt(xMin) + ", " + fmt(xMax) + "]", true);
            }
            btnPlot.setDisable(false);
        }));
    }

    private void wireFailureHandler(Task<double[]> task, String expr) {
        task.setOnFailed(evt -> {
            Throwable ex = task.getException();
            log.error("Plot task failed for expression='{}': {}", expr,
                    ex != null ? ex.getMessage() : "null", ex);
            Platform.runLater(() -> {
                setStatus("Error: " + (ex != null ? ex.getMessage() : "Unknown error"), false);
                btnPlot.setDisable(false);
            });
        });
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

    private void drawPlot(double[] yValues, double xMin, double xMax) {
        GraphicsContext gc = plotCanvas.getGraphicsContext2D();
        double W = plotCanvas.getWidth(), H = plotCanvas.getHeight();
        double plotW = W - 2 * PADDING, plotH = H - 2 * PADDING;
        double[] bounds = findYBounds(yValues);
        double yMin = bounds[0], yMax = bounds[1], yRange = bounds[2];
        if (Double.isNaN(yRange)) {
            drawIdleCanvas();
            setStatus("Function has no real values in this range", false);
            return;
        }
        double xRange = xMax - xMin;
        double xAxisPy = computeXAxisPy(yMin, yMax, yRange, H, plotH);
        double yAxisPx = computeYAxisPx(xMin, xMax, xRange, W, plotW);
        drawBackground(gc, W, H);
        drawGrid(gc, W, H);
        drawAxes(gc, xAxisPy, yAxisPx, W, H);
        drawAxisLabels(gc, xMin, xRange, yMin, yRange, W, H, plotW, plotH);
        drawCurve(gc, yValues, xMin, xRange, yMin, yRange, W, H, plotW, plotH);
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
        labelStatus.setStyle("-fx-text-fill: " + (isOk ? "#9e9e9e" : "#ff6b6b") + "; -fx-font-size: 12px;");
        labelStatus.setText(message);
    }
}
