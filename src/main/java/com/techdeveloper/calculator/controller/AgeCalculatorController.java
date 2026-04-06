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
 * Controller for age-calculator.fxml.
 * Reads DOB and target date (defaults to today) from DatePickers and delegates to service.
 */
public class AgeCalculatorController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(AgeCalculatorController.class);

    @FXML private DatePicker pickerDOB;
    @FXML private DatePicker pickerTarget;
    @FXML private Label labelYears;
    @FXML private Label labelMonths;
    @FXML private Label labelDays;
    @FXML private Label labelTotalDays;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Pre-populate target date with today
        pickerTarget.setValue(LocalDate.now());
    }

    @FXML
    private void onCalculate(ActionEvent event) {
        LocalDate dob = pickerDOB.getValue();
        if (dob == null) {
            showErrorDialog("Missing Input", "Please select a date of birth.");
            return;
        }
        LocalDate target = pickerTarget.getValue();
        if (target == null) target = LocalDate.now();

        if (dob.isAfter(target)) {
            showErrorDialog("Invalid Date", "Date of birth cannot be after the target date.");
            return;
        }

        Map<String, String> inputs = new LinkedHashMap<>();
        inputs.put("dob",    dob.toString());
        inputs.put("target", target.toString());

        try {
            CalculatorService svc = ServiceFactory.getInstance().getService(CalculatorType.AGE);
            String result = svc.calculate(inputs);
            log.debug("Age result: {}", result);
            displayResult(result);
        } catch (IllegalArgumentException e) {
            log.warn("AGE service not registered", e);
            displayError("Service not available");
        }
    }

    // Result format: "years=<n>|months=<n>|days=<n>|totalDays=<n>"

    private void displayResult(String result) {
        if (result.startsWith("Error:")) {
            displayError(result.substring("Error:".length()).trim());
            return;
        }
        Map<String, String> parsed = parseKeyValue(result);
        setLabel(labelYears,     parsed.getOrDefault("years",     "—"), "#4a90d9", "22px");
        setLabel(labelMonths,    parsed.getOrDefault("months",    "—"), "#ffffff", "16px");
        setLabel(labelDays,      parsed.getOrDefault("days",      "—"), "#ffffff", "16px");
        setLabel(labelTotalDays, parsed.getOrDefault("totalDays", "—"), "#9e9e9e", "14px");
    }

    private void displayError(String message) {
        setLabel(labelYears,     "Error: " + message, "#ff6b6b", "14px");
        setLabel(labelMonths,    "—", "#9e9e9e", "14px");
        setLabel(labelDays,      "—", "#9e9e9e", "14px");
        setLabel(labelTotalDays, "—", "#9e9e9e", "14px");
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
