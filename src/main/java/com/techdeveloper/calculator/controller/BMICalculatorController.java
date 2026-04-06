package com.techdeveloper.calculator.controller;

import com.techdeveloper.calculator.service.CalculatorService;
import com.techdeveloper.calculator.service.CalculatorType;
import com.techdeveloper.calculator.service.HistoryService;
import com.techdeveloper.calculator.service.ServiceFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Controller for bmi-calculator.fxml.
 * Handles Metric/Imperial toggle and delegates BMI calculation to BMICalculatorService.
 * Service inputs: "weight", "height", "unit" (METRIC or IMPERIAL).
 * Result displayed directly in resultArea — no pipe-parsing.
 */
public class BMICalculatorController implements Initializable {

    public BMICalculatorController() {
        // required for FXML
    }

    private static final Logger log = LoggerFactory.getLogger(BMICalculatorController.class);

    private static final String NORMAL_STYLE = "-fx-control-inner-background: #1a1a1a; -fx-text-fill: #e0e0e0;";
    private static final String ERROR_STYLE  = "-fx-control-inner-background: #1a1a1a; -fx-text-fill: #ff6b6b;";

    @FXML private TextField fieldWeight;
    @FXML private TextField fieldHeight;
    @FXML private RadioButton rbMetric;
    @FXML private RadioButton rbImperial;
    @FXML private Label labelWeight;
    @FXML private Label labelHeight;
    @FXML private TextArea resultArea;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        CalculatorService svc = ServiceFactory.getInstance().getService(CalculatorType.BMI);
        log.debug("BMICalculatorController initialized, service={}", svc.getClass().getSimpleName());
    }

    @FXML
    private void onUnitChange(ActionEvent event) {
        boolean isMetric = rbMetric == null || rbMetric.isSelected();
        if (labelWeight != null) labelWeight.setText(isMetric ? "Weight (kg)" : "Weight (lb)");
        if (labelHeight != null) labelHeight.setText(isMetric ? "Height (cm)" : "Height (in)");
        if (fieldWeight != null) fieldWeight.setPromptText(isMetric ? "e.g. 70"  : "e.g. 154");
        if (fieldHeight != null) fieldHeight.setPromptText(isMetric ? "e.g. 175" : "e.g. 69");
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
        inputs.put("unit",   (rbMetric == null || rbMetric.isSelected()) ? "METRIC" : "IMPERIAL");

        try {
            CalculatorService svc = ServiceFactory.getInstance().getService(CalculatorType.BMI);
            String result = svc.calculate(inputs);
            log.debug("BMI result: {}", result);
            displayResult(result);
            if (!result.startsWith("Error:")) {
                String unit = (rbMetric == null || rbMetric.isSelected()) ? "METRIC" : "IMPERIAL";
                String inputSummary = "W=" + weight + ", H=" + height + ", " + unit;
                HistoryService.getInstance().addEntry("BMI", inputSummary, result);
            }
        } catch (IllegalArgumentException e) {
            log.warn("BMI service not registered", e);
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
