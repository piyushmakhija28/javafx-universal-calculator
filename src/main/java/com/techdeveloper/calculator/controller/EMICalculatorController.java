package com.techdeveloper.calculator.controller;

import com.techdeveloper.calculator.service.CalculatorService;
import com.techdeveloper.calculator.service.CalculatorType;
import com.techdeveloper.calculator.service.ServiceFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Controller for emi-calculator.fxml.
 * Collects principal, rate, and tenure from the form and passes them to EMI service.
 * Displays EMI, total interest, and total amount payable in result labels.
 */
public class EMICalculatorController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(EMICalculatorController.class);

    @FXML private TextField fieldPrincipal;
    @FXML private TextField fieldRate;
    @FXML private TextField fieldTenure;
    @FXML private Label labelEMI;
    @FXML private Label labelTotalInterest;
    @FXML private Label labelTotalAmount;

    private static final String ERROR_STYLE = "-fx-text-fill: #ff6b6b;";
    private static final String ACCENT_STYLE = "-fx-text-fill: #4a90d9; -fx-font-size: 18px; -fx-font-weight: bold;";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // No pre-population required; fields start empty
    }

    @FXML
    private void onCalculate(ActionEvent event) {
        String principal = fieldPrincipal.getText().trim();
        String rate      = fieldRate.getText().trim();
        String tenure    = fieldTenure.getText().trim();

        if (principal.isEmpty() || rate.isEmpty() || tenure.isEmpty()) {
            showErrorDialog("Missing Input", "Please enter Principal, Rate, and Tenure.");
            return;
        }

        Map<String, String> inputs = new LinkedHashMap<>();
        inputs.put("principal", principal);
        inputs.put("rate", rate);
        inputs.put("tenure", tenure);

        try {
            CalculatorService svc = ServiceFactory.getInstance().getService(CalculatorType.EMI);
            String result = svc.calculate(inputs);
            log.debug("EMI calculation result: {}", result);
            displayResult(result);
        } catch (IllegalArgumentException e) {
            log.warn("EMI service not registered", e);
            displayError("Service not available");
        }
    }

    // ── Result parsing ─────────────────────────────────────────────────────
    // The service is expected to return a pipe-delimited string:
    //   "emi=<value>|interest=<value>|total=<value>"
    // or "Error: <message>"

    private void displayResult(String result) {
        if (result.startsWith("Error:")) {
            displayError(result.substring("Error:".length()).trim());
            return;
        }
        try {
            Map<String, String> parsed = parseKeyValue(result);
            setLabel(labelEMI, parsed.getOrDefault("emi", "—"), ACCENT_STYLE);
            setLabel(labelTotalInterest, parsed.getOrDefault("interest", "—"),
                    "-fx-text-fill: #ffffff; -fx-font-size: 16px;");
            setLabel(labelTotalAmount, parsed.getOrDefault("total", "—"),
                    "-fx-text-fill: #ffffff; -fx-font-size: 16px;");
        } catch (Exception e) {
            log.error("Failed to parse EMI result: {}", result, e);
            displayError("Invalid result format");
        }
    }

    private void displayError(String message) {
        setLabel(labelEMI, "Error: " + message, ERROR_STYLE);
        setLabel(labelTotalInterest, "—", "-fx-text-fill: #9e9e9e;");
        setLabel(labelTotalAmount,   "—", "-fx-text-fill: #9e9e9e;");
    }

    private void setLabel(Label label, String text, String style) {
        label.setText(text);
        label.setStyle(style);
    }

    /** Parses "key1=val1|key2=val2" into a Map. */
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
