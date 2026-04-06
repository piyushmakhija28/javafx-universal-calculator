package com.techdeveloper.calculator.controller;

import com.techdeveloper.calculator.constants.BMIUnit;
import com.techdeveloper.calculator.dto.BMICalculatorResult;
import com.techdeveloper.calculator.form.BMICalculatorForm;
import com.techdeveloper.calculator.service.CalculatorService;
import com.techdeveloper.calculator.service.HistoryService;
import com.techdeveloper.calculator.service.impl.ServiceFactory;
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
import java.util.ResourceBundle;

/**
 * Controller for bmi-calculator.fxml.
 * Handles Metric/Imperial toggle and delegates BMI calculation to BMICalculatorService.
 * Service form: BMICalculatorForm(weight, height, BMIUnit).
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

    private CalculatorService<BMICalculatorForm, BMICalculatorResult> service;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        service = ServiceFactory.getInstance().getBmiService();
        log.debug("BMICalculatorController initialized, service={}", service.getClass().getSimpleName());
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
        String weightText = fieldWeight.getText().trim();
        String heightText = fieldHeight.getText().trim();

        if (weightText.isEmpty() || heightText.isEmpty()) {
            showErrorDialog("Missing Input", "Please enter both weight and height.");
            return;
        }

        try {
            double weight = Double.parseDouble(weightText);
            double height = Double.parseDouble(heightText);
            BMIUnit unit = (rbMetric == null || rbMetric.isSelected()) ? BMIUnit.METRIC : BMIUnit.IMPERIAL;

            BMICalculatorForm form = new BMICalculatorForm(weight, height, unit);
            BMICalculatorResult result = service.calculate(form);
            log.debug("BMI result: isError={}", result.isError());

            if (result.isError()) {
                displayResult("Error: " + result.errorMessage(), true);
            } else {
                String formatted = String.format("BMI: %.2f%nCategory: %s", result.bmi(), result.category());
                displayResult(formatted, false);
                String inputSummary = "W=" + weightText + ", H=" + heightText + ", " + unit;
                HistoryService.getInstance().addEntry("BMI", inputSummary, formatted);
            }
        } catch (NumberFormatException e) {
            log.warn("BMI controller: invalid number input", e);
            displayResult("Error: Invalid numeric input", true);
        }
    }

    private void displayResult(String text, boolean isError) {
        resultArea.setStyle(isError ? ERROR_STYLE : NORMAL_STYLE);
        resultArea.setText(text);
    }

    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Input Error");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
