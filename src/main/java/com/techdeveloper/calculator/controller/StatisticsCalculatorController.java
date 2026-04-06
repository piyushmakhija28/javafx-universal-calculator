package com.techdeveloper.calculator.controller;

import com.techdeveloper.calculator.dto.StatisticsCalculatorResult;
import com.techdeveloper.calculator.form.StatisticsCalculatorForm;
import com.techdeveloper.calculator.service.CalculatorService;
import com.techdeveloper.calculator.service.HistoryService;
import com.techdeveloper.calculator.service.impl.ServiceFactory;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.Arrays;
import java.util.ResourceBundle;

/**
 * Controller for statistics-calculator.fxml.
 * Statistics on large data sets run on a background Task.
 * Platform.runLater() is used to update the resultArea TextArea after completion.
 *
 * Service form: StatisticsCalculatorForm(data) — double[] parsed from comma-separated input.
 */
public class StatisticsCalculatorController implements Initializable {

    public StatisticsCalculatorController() {
        // required for FXML
    }

    private static final Logger log = LoggerFactory.getLogger(StatisticsCalculatorController.class);

    private static final String NORMAL_STYLE = "-fx-control-inner-background: #1a1a1a; -fx-text-fill: #e0e0e0;";
    private static final String ERROR_STYLE  = "-fx-control-inner-background: #1a1a1a; -fx-text-fill: #ff6b6b;";

    @FXML private TextArea inputArea;
    @FXML private TextArea resultArea;

    private CalculatorService<StatisticsCalculatorForm, StatisticsCalculatorResult> service;

    /** Cancellable reference to the in-flight computation task. */
    private Task<StatisticsCalculatorResult> currentTask;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        service = ServiceFactory.getInstance().getStatisticsService();
        log.debug("StatisticsCalculatorController initialized, service={}", service.getClass().getSimpleName());
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

        resultArea.setStyle(NORMAL_STYLE);
        resultArea.setText("Computing...");

        // Parse the comma-separated input into a double array here — fail fast before Task launch
        final double[] data;
        try {
            String[] tokens = rawInput.split(",");
            data = new double[tokens.length];
            for (int i = 0; i < tokens.length; i++) {
                data[i] = Double.parseDouble(tokens[i].trim());
            }
        } catch (NumberFormatException e) {
            log.warn("Statistics controller: invalid number in input", e);
            resultArea.setStyle(ERROR_STYLE);
            resultArea.setText("Error: Invalid number in input");
            return;
        }

        currentTask = new Task<>() {
            @Override
            protected StatisticsCalculatorResult call() {
                StatisticsCalculatorForm form = new StatisticsCalculatorForm(data);
                return service.calculate(form);
            }
        };

        final String inputSnapshot = rawInput;
        currentTask.setOnSucceeded(workerState -> {
            StatisticsCalculatorResult result = currentTask.getValue();
            // Platform.runLater() — mandatory for any UI mutation from a non-FX thread.
            // resultArea is a UI node and must be updated on the FX Application Thread.
            Platform.runLater(() -> {
                if (result.isError()) {
                    resultArea.setStyle(ERROR_STYLE);
                    resultArea.setText("Error: " + result.errorMessage());
                } else {
                    String formatted = String.format(
                        "Count: %d%nSum: %.4f%nMean: %.4f%nMedian: %.4f%nStd Dev: %.4f%nMin: %.4f%nMax: %.4f",
                        result.count(), result.sum(), result.mean(),
                        result.median(), result.stdDev(), result.min(), result.max());
                    resultArea.setStyle(NORMAL_STYLE);
                    resultArea.setText(formatted);
                    // Truncate long input arrays to keep history readable
                    String truncated = inputSnapshot.length() > 40
                            ? inputSnapshot.substring(0, 37) + "..."
                            : inputSnapshot;
                    HistoryService.getInstance().addEntry("Statistics", truncated, formatted);
                }
            });
        });

        currentTask.setOnFailed(workerState -> {
            Throwable ex = currentTask.getException();
            log.error("Statistics Task failed", ex);
            Platform.runLater(() -> {
                resultArea.setStyle(ERROR_STYLE);
                resultArea.setText("Error: Computation failed — " + ex.getMessage());
                showErrorDialog("Computation Error",
                        "Statistics calculation failed.\n" + ex.getMessage());
            });
        });

        Thread taskThread = new Thread(currentTask, "statistics-calc-thread");
        taskThread.setDaemon(true);
        taskThread.start();
        log.debug("Statistics task started for {} data points", data.length);
    }

    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Statistics Error");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
