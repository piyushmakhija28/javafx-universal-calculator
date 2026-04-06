package com.techdeveloper.calculator.controller;

import com.techdeveloper.calculator.service.CalculatorService;
import com.techdeveloper.calculator.service.CalculatorType;
import com.techdeveloper.calculator.service.ServiceFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Controller for bmi-calculator.fxml.
 * Handles Metric/Imperial toggle and delegates BMI calculation to service.
 */
public class BMICalculatorController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(BMICalculatorController.class);

    @FXML private TextField fieldWeight;
    @FXML private TextField fieldHeight;
    @FXML private RadioButton rbMetric;
    @FXML private RadioButton rbImperial;
    @FXML private Label labelWeight;
    @FXML private Label labelHeight;
    @FXML private Label labelBMI;
    @FXML private Label labelCategory;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Default: metric
    }

    @FXML
    private void onUnitChange(ActionEvent event) {
        boolean isMetric = rbMetric.isSelected();
        labelWeight.setText(isMetric ? "Weight (kg)" : "Weight (lb)");
        labelHeight.setText(isMetric ? "Height (m)"  : "Height (in)");
        fieldWeight.setPromptText(isMetric ? "e.g. 70"   : "e.g. 154");
        fieldHeight.setPromptText(isMetric ? "e.g. 1.75" : "e.g. 69");
    }

    @FXML
    private void onCalculate(ActionEvent event) {
        String weight = fieldWeight.getText().trim();
        String height = fieldHeight.getText().trim();

        if (weight.isEmpty() || height.isEmpty()) {
            showErrorDialog("Missing Input", "Please enter both weight and height.");
            return;
        }

        Map<String, String> inputs = new LinkedHashMap<>();
        inputs.put("weight", weight);
        inputs.put("height", height);
        inputs.put("unit", rbMetric.isSelected() ? "METRIC" : "IMPERIAL");

        try {
            CalculatorService svc = ServiceFactory.getInstance().getService(CalculatorType.BMI);
            String result = svc.calculate(inputs);
            log.debug("BMI result: {}", result);
            displayResult(result);
        } catch (IllegalArgumentException e) {
            log.warn("BMI service not registered", e);
            displayError("Service not available");
        }
    }

    // Result format: "bmi=<value>|category=<text>"

    private void displayResult(String result) {
        if (result.startsWith("Error:")) {
            displayError(result.substring("Error:".length()).trim());
            return;
        }
        Map<String, String> parsed = parseKeyValue(result);
        String bmi = parsed.getOrDefault("bmi", "—");
        String cat = parsed.getOrDefault("category", "—");

        labelBMI.setText(bmi);
        labelBMI.setStyle("-fx-text-fill: #4a90d9; -fx-font-size: 22px; -fx-font-weight: bold;");

        labelCategory.setText(cat);
        String catColor = categoryColor(cat);
        labelCategory.setStyle("-fx-text-fill: " + catColor + "; -fx-font-size: 16px; -fx-font-weight: bold;");
    }

    private void displayError(String message) {
        labelBMI.setText("Error: " + message);
        labelBMI.setStyle("-fx-text-fill: #ff6b6b; -fx-font-size: 14px;");
        labelCategory.setText("—");
    }

    private String categoryColor(String category) {
        if (category == null) return "#9e9e9e";
        return switch (category.toLowerCase()) {
            case "underweight"        -> "#4a90d9";
            case "normal weight"      -> "#2ecc71";
            case "overweight"         -> "#f0a500";
            case "obese"              -> "#e74c3c";
            default                   -> "#9e9e9e";
        };
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
