package com.techdeveloper.calculator.controller;

import com.techdeveloper.calculator.service.CalculatorService;
import com.techdeveloper.calculator.service.CalculatorType;
import com.techdeveloper.calculator.service.ServiceFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Controller for date-diff-calculator.fxml.
 * Reads start and end dates and delegates difference computation to service.
 */
public class DateDiffCalculatorController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(DateDiffCalculatorController.class);

    @FXML private DatePicker pickerStart;
    @FXML private DatePicker pickerEnd;
    @FXML private Label labelDays;
    @FXML private Label labelWeeks;
    @FXML private Label labelMonths;
    @FXML private Label labelYears;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // No pre-population required
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
        inputs.put("start", start.toString());
        inputs.put("end",   end.toString());

        try {
            CalculatorService svc = ServiceFactory.getInstance().getService(CalculatorType.DATE_DIFF);
            String result = svc.calculate(inputs);
            log.debug("DateDiff result: {}", result);
            displayResult(result);
        } catch (IllegalArgumentException e) {
            log.warn("DATE_DIFF service not registered", e);
            displayError("Service not available");
        }
    }

    // Result format: "days=<n>|weeks=<n>|months=<n>|years=<n>"

    private void displayResult(String result) {
        if (result.startsWith("Error:")) {
            displayError(result.substring("Error:".length()).trim());
            return;
        }
        Map<String, String> parsed = parseKeyValue(result);
        setLabel(labelDays,   parsed.getOrDefault("days",   "—"), "#4a90d9", "22px");
        setLabel(labelWeeks,  parsed.getOrDefault("weeks",  "—"), "#ffffff", "16px");
        setLabel(labelMonths, parsed.getOrDefault("months", "—"), "#ffffff", "16px");
        setLabel(labelYears,  parsed.getOrDefault("years",  "—"), "#ffffff", "16px");
    }

    private void displayError(String message) {
        setLabel(labelDays,   "Error: " + message, "#ff6b6b", "14px");
        setLabel(labelWeeks,  "—", "#9e9e9e", "14px");
        setLabel(labelMonths, "—", "#9e9e9e", "14px");
        setLabel(labelYears,  "—", "#9e9e9e", "14px");
    }

    private void setLabel(Label label, String text, String color, String size) {
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
        alert.setTitle("Input Error");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
