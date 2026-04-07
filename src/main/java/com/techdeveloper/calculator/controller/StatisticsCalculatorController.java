package com.techdeveloper.calculator.controller;

import com.techdeveloper.calculator.controller.helper.StatisticsCalculatorHelper;
import com.techdeveloper.calculator.dto.StatisticsCalculatorResult;
import com.techdeveloper.calculator.form.StatisticsCalculatorForm;
import com.techdeveloper.calculator.service.CalculatorService;
import com.techdeveloper.calculator.service.impl.ServiceFactory;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for statistics-calculator.fxml.
 * Statistics on large data sets run on a background Task.
 * Platform.runLater() is used to update the resultArea TextArea after completion.
 *
 * Service form: StatisticsCalculatorForm(data) — double[] parsed from comma-separated input.
 */
public class StatisticsCalculatorController extends StatisticsCalculatorHelper implements Initializable {

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
        if (currentTask != null && currentTask.isRunning()) currentTask.cancel();
        resultArea.setStyle(NORMAL_STYLE);
        resultArea.setText("Computing...");
        final double[] data;
        try {
            data = parseInputData(rawInput);
        } catch (NumberFormatException e) {
            log.warn("Statistics controller: invalid number in input", e);
            resultArea.setStyle(ERROR_STYLE);
            resultArea.setText("Error: Invalid number in input");
            return;
        }
        currentTask = buildComputeTask(data);
        wireTaskSucceeded(currentTask, rawInput, resultArea, NORMAL_STYLE, ERROR_STYLE);
        wireTaskFailed(currentTask, resultArea, ERROR_STYLE);
        startDaemonThread(currentTask, data.length);
    }

    private Task<StatisticsCalculatorResult> buildComputeTask(double[] data) {
        return new Task<>() {
            @Override
            protected StatisticsCalculatorResult call() {
                return service.calculate(new StatisticsCalculatorForm(data));
            }
        };
    }

    private void startDaemonThread(Task<?> task, int dataPoints) {
        Thread thread = new Thread(task, "statistics-calc-thread");
        thread.setDaemon(true);
        thread.start();
        log.debug("Statistics task started for {} data points", dataPoints);
    }

    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Statistics Error");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
