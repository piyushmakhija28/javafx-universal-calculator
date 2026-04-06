package com.techdeveloper.calculator.controller;

import com.techdeveloper.calculator.service.CalculatorService;
import com.techdeveloper.calculator.service.CalculatorType;
import com.techdeveloper.calculator.service.ServiceFactory;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Controller for statistics-calculator.fxml.
 * Statistics on large data sets run on a background Task.
 * Platform.runLater() is used to update all result Labels after completion.
 */
public class StatisticsCalculatorController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(StatisticsCalculatorController.class);

    @FXML private TextArea inputArea;
    @FXML private Label labelMean;
    @FXML private Label labelMedian;
    @FXML private Label labelMode;
    @FXML private Label labelStdDev;
    @FXML private Label labelVariance;
    @FXML private Label labelMin;
    @FXML private Label labelMax;
    @FXML private Label labelCount;

    /** Cancellable reference to the in-flight computation task. */
    private Task<String> currentTask;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // No setup needed; TextArea starts empty
    }

    @FXML
    private void onCalculate(ActionEvent event) {
        String rawInput = inputArea.getText().trim();
        if (rawInput.isEmpty()) {
            showErrorDialog("Missing Input", "Please enter comma-separated numbers.");
            return;
        }

        // Cancel any previously running task before starting a new one
        if (currentTask != null && currentTask.isRunning()) {
            currentTask.cancel();
        }

        clearResults();
        setAllResultLabels("Computing...", "#9e9e9e", "14px");

        Map<String, String> inputs = new LinkedHashMap<>();
        inputs.put("data", rawInput);

        currentTask = new Task<>() {
            @Override
            protected String call() {
                CalculatorService svc = ServiceFactory.getInstance().getService(CalculatorType.STATISTICS);
                return svc.calculate(inputs);
            }
        };

        currentTask.setOnSucceeded(workerState -> {
            String result = currentTask.getValue();
            Platform.runLater(() -> displayResult(result));
        });

        currentTask.setOnFailed(workerState -> {
            Throwable ex = currentTask.getException();
            log.error("Statistics Task failed", ex);
            Platform.runLater(() -> {
                setAllResultLabels("Error", "#ff6b6b", "13px");
                showErrorDialog("Computation Error",
                        "Statistics calculation failed.\n" + ex.getMessage());
            });
        });

        Thread taskThread = new Thread(currentTask, "statistics-calc-thread");
        taskThread.setDaemon(true);
        taskThread.start();
        log.debug("Statistics task started for {} chars of input", rawInput.length());
    }

    // ── Result parsing ─────────────────────────────────────────────────────
    // Expected format: "mean=<v>|median=<v>|mode=<v>|stddev=<v>|variance=<v>|min=<v>|max=<v>|count=<v>"

    private void displayResult(String result) {
        if (result.startsWith("Error:")) {
            setAllResultLabels("Error", "#ff6b6b", "13px");
            log.warn("Statistics service returned error: {}", result);
            return;
        }
        Map<String, String> parsed = parseKeyValue(result);
        setLabel(labelMean,     parsed.getOrDefault("mean",     "—"), "#4a90d9", "18px");
        setLabel(labelMedian,   parsed.getOrDefault("median",   "—"), "#4a90d9", "18px");
        setLabel(labelMode,     parsed.getOrDefault("mode",     "—"), "#4a90d9", "18px");
        setLabel(labelCount,    parsed.getOrDefault("count",    "—"), "#ffffff", "18px");
        setLabel(labelStdDev,   parsed.getOrDefault("stddev",   "—"), "#ffffff", "16px");
        setLabel(labelVariance, parsed.getOrDefault("variance", "—"), "#ffffff", "16px");
        setLabel(labelMin,      parsed.getOrDefault("min",      "—"), "#ffffff", "16px");
        setLabel(labelMax,      parsed.getOrDefault("max",      "—"), "#ffffff", "16px");
    }

    private void clearResults() {
        for (Label lbl : new Label[]{labelMean, labelMedian, labelMode, labelStdDev,
                                      labelVariance, labelMin, labelMax, labelCount}) {
            if (lbl != null) lbl.setText("—");
        }
    }

    private void setAllResultLabels(String text, String color, String size) {
        for (Label lbl : new Label[]{labelMean, labelMedian, labelMode, labelStdDev,
                                      labelVariance, labelMin, labelMax, labelCount}) {
            setLabel(lbl, text, color, size);
        }
    }

    private void setLabel(Label label, String text, String color, String size) {
        if (label == null) return;
        label.setText(text);
        label.setStyle("-fx-text-fill: " + color + "; -fx-font-size: " + size + "; -fx-font-weight: bold;");
    }

    private Map<String, String> parseKeyValue(String result) {
        Map<String, String> map = new LinkedHashMap<>();
        for (String pair : result.split("\\|")) {
            String[] kv = pair.split("=", 2);
            if (kv.length == 2) map.put(kv[0].trim(), kv[1].trim());
        }
        return map;
    }

    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Statistics Error");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
