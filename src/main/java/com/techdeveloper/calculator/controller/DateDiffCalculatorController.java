package com.techdeveloper.calculator.controller;

import com.techdeveloper.calculator.service.CalculatorService;
import com.techdeveloper.calculator.service.CalculatorType;
import com.techdeveloper.calculator.service.HistoryService;
import com.techdeveloper.calculator.service.ServiceFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Controller for date-diff-calculator.fxml.
 * Reads start and end dates and delegates difference computation to DateDiffCalculatorService.
 * Service inputs: "startDate" (YYYY-MM-DD), "endDate" (YYYY-MM-DD).
 * Result displayed directly in resultArea — no pipe-parsing.
 */
public class DateDiffCalculatorController implements Initializable {

    public DateDiffCalculatorController() {
        // required for FXML
    }

    private static final Logger log = LoggerFactory.getLogger(DateDiffCalculatorController.class);

    private static final String NORMAL_STYLE = "-fx-control-inner-background: #1a1a1a; -fx-text-fill: #e0e0e0;";
    private static final String ERROR_STYLE  = "-fx-control-inner-background: #1a1a1a; -fx-text-fill: #ff6b6b;";

    @FXML private DatePicker pickerStart;
    @FXML private DatePicker pickerEnd;
    @FXML private TextArea   resultArea;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        CalculatorService svc = ServiceFactory.getInstance().getService(CalculatorType.DATE_DIFF);
        log.debug("DateDiffCalculatorController initialized, service={}", svc.getClass().getSimpleName());
    }

    @FXML
    private void onCalculate(ActionEvent event) {
        LocalDate start = pickerStart.getValue();
        LocalDate end   = pickerEnd.getValue();

        if (start == null || end == null) {
            showErrorDialog("Missing Input", "Please select both a start date and an end date.");
            return;
        }

        // Ensure chronological order — allow reversed input gracefully
        if (end.isBefore(start)) {
            LocalDate temp = start;
            start = end;
            end = temp;
        }

        Map<String, String> inputs = new LinkedHashMap<>();
        inputs.put("startDate", start.toString());
        inputs.put("endDate",   end.toString());

        try {
            CalculatorService svc = ServiceFactory.getInstance().getService(CalculatorType.DATE_DIFF);
            String result = svc.calculate(inputs);
            log.debug("DateDiff result: {}", result);
            displayResult(result);
            if (!result.startsWith("Error:")) {
                String inputSummary = start + " to " + end;
                HistoryService.getInstance().addEntry("Date Diff", inputSummary, result);
            }
        } catch (IllegalArgumentException e) {
            log.warn("DATE_DIFF service not registered", e);
            displayResult("Error: Service not available");
        }
    }

    private void displayResult(String result) {
        if (result.startsWith("Error:")) {
            resultArea.setStyle(ERROR_STYLE);
        } else {
            resultArea.setStyle(NORMAL_STYLE);
        }
        resultArea.setText(result);
    }

    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Input Error");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
