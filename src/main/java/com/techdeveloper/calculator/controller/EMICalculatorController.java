package com.techdeveloper.calculator.controller;

import com.techdeveloper.calculator.service.CalculatorService;
import com.techdeveloper.calculator.service.CalculatorType;
import com.techdeveloper.calculator.service.ServiceFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Controller for emi-calculator.fxml.
 * Collects principal, annualRate, and tenureMonths; passes them to EMICalculatorService.
 * Displays the full service result string in a single resultArea — no pipe-parsing.
 */
public class EMICalculatorController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(EMICalculatorController.class);

    private static final String NORMAL_STYLE = "-fx-control-inner-background: #1a1a1a; -fx-text-fill: #e0e0e0;";
    private static final String ERROR_STYLE  = "-fx-control-inner-background: #1a1a1a; -fx-text-fill: #ff6b6b;";

    @FXML private TextField fieldPrincipal;
    @FXML private TextField fieldRate;
    @FXML private TextField fieldTenure;
    @FXML private TextArea  resultArea;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        CalculatorService svc = ServiceFactory.getInstance().getService(CalculatorType.EMI);
        log.debug("EMICalculatorController initialized, service={}", svc.getClass().getSimpleName());
    }

    @FXML
    private void onCalculate(ActionEvent event) {
        String principal = fieldPrincipal.getText().trim();
        String rate      = fieldRate.getText().trim();
        String tenure    = fieldTenure.getText().trim();

        if (principal.isEmpty() || rate.isEmpty() || tenure.isEmpty()) {
            showErrorDialog("Missing Input", "Please enter Principal, Annual Rate, and Tenure (months).");
            return;
        }

        Map<String, String> inputs = new LinkedHashMap<>();
        inputs.put("principal",    principal);
        inputs.put("annualRate",   rate);
        inputs.put("tenureMonths", tenure);

        try {
            CalculatorService svc = ServiceFactory.getInstance().getService(CalculatorType.EMI);
            String result = svc.calculate(inputs);
            log.debug("EMI calculation result: {}", result);
            displayResult(result);
        } catch (IllegalArgumentException e) {
            log.warn("EMI service not registered", e);
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
