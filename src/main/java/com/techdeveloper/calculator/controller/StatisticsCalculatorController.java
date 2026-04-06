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
 * Platform.runLater() is used to update the resultArea TextArea after completion.
 *
 * Service input: "data" — comma-separated numbers.
 * Service returns a pipe-delimited result string; displayed directly in resultArea.
 */
public class StatisticsCalculatorController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(StatisticsCalculatorController.class);

    private static final String NORMAL_STYLE = "-fx-control-inner-background: #1a1a1a; -fx-text-fill: #e0e0e0;";
    private static final String ERROR_STYLE  = "-fx-control-inner-background: #1a1a1a; -fx-text-fill: #ff6b6b;";

    @FXML private TextArea inputArea;
    @FXML private TextArea resultArea;

    /** Cancellable reference to the in-flight computation task. */
    private Task<String> currentTask;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        CalculatorService svc = ServiceFactory.getInstance().getService(CalculatorType.STATISTICS);
        log.debug("StatisticsCalculatorController initialized, service={}", svc.getClass().getSimpleName());
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
                resultArea.setStyle(ERROR_STYLE);
                resultArea.setText("Error: Computation failed — " + ex.getMessage());
                showErrorDialog("Computation Error",
                        "Statistics calculation failed.\n" + ex.getMessage());
            });
        });

        Thread taskThread = new Thread(currentTask, "statistics-calc-thread");
        taskThread.setDaemon(true);
        taskThread.start();
        log.debug("Statistics task started for {} chars of input", rawInput.length());
    }

    private void displayResult(String result) {
        if (result.startsWith("Error:")) {
            resultArea.setStyle(ERROR_STYLE);
            log.warn("Statistics service returned error: {}", result);
        } else {
            resultArea.setStyle(NORMAL_STYLE);
        }
        resultArea.setText(result);
    }

    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Statistics Error");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
