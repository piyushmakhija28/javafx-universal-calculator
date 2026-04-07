package com.techdeveloper.calculator.controller.helper;

import com.techdeveloper.calculator.dto.StatisticsCalculatorResult;
import com.techdeveloper.calculator.service.HistoryService;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.control.TextArea;

/**
 * Helper base class for StatisticsCalculatorController.
 * Contains extracted methods to keep the controller's onCalculate method ≤ 35 lines.
 * All methods are protected for use by the controller subclass.
 */
public class StatisticsCalculatorHelper {

    protected double[] parseInputData(String rawInput) {
        String[] tokens = rawInput.split(",");
        double[] data = new double[tokens.length];
        for (int i = 0; i < tokens.length; i++) {
            data[i] = Double.parseDouble(tokens[i].trim());
        }
        return data;
    }

    protected String formatStatisticsResult(StatisticsCalculatorResult result) {
        return String.format(
            "Count: %d%nSum: %.4f%nMean: %.4f%nMedian: %.4f%nStd Dev: %.4f%nMin: %.4f%nMax: %.4f",
            result.count(), result.sum(), result.mean(),
            result.median(), result.stdDev(), result.min(), result.max());
    }

    protected String truncateForHistory(String input) {
        return input.length() > 40 ? input.substring(0, 37) + "..." : input;
    }

    protected void wireTaskSucceeded(Task<StatisticsCalculatorResult> task, String inputSnapshot,
            TextArea resultArea, String normalStyle, String errorStyle) {
        task.setOnSucceeded(ws -> {
            StatisticsCalculatorResult r = task.getValue();
            Platform.runLater(() -> applyTaskResult(r, inputSnapshot, resultArea, normalStyle, errorStyle));
        });
    }

    protected void applyTaskResult(StatisticsCalculatorResult result, String inputSnapshot,
            TextArea resultArea, String normalStyle, String errorStyle) {
        if (result.isError()) {
            resultArea.setStyle(errorStyle);
            resultArea.setText("Error: " + result.errorMessage());
        } else {
            String formatted = formatStatisticsResult(result);
            resultArea.setStyle(normalStyle);
            resultArea.setText(formatted);
            HistoryService.getInstance().addEntry("Statistics", truncateForHistory(inputSnapshot), formatted);
        }
    }

    protected void wireTaskFailed(Task<?> task, TextArea resultArea, String errorStyle) {
        task.setOnFailed(ws -> {
            Throwable ex = task.getException();
            Platform.runLater(() -> {
                resultArea.setStyle(errorStyle);
                resultArea.setText("Error: Computation failed — " + ex.getMessage());
            });
        });
    }
}
