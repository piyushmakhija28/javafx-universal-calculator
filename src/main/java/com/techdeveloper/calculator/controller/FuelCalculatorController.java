package com.techdeveloper.calculator.controller;

import com.techdeveloper.calculator.service.CalculatorService;
import com.techdeveloper.calculator.service.CalculatorType;
import com.techdeveloper.calculator.service.HistoryService;
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
 * Controller for fuel-calculator.fxml.
 * Collects distance, fuelUsed, and fuelPrice (optional); delegates to FUEL service.
 *
 * Service input keys: "distance", "fuelUsed", "fuelPrice" (optional).
 * Service returns: "L/100km: X | km/L: Y" or "L/100km: X | km/L: Y | Cost/km: Z | Total Cost: W"
 * Result displayed directly in resultArea — no pipe-parsing.
 */
public class FuelCalculatorController implements Initializable {

    public FuelCalculatorController() {
        // required for FXML
    }

    private static final Logger log = LoggerFactory.getLogger(FuelCalculatorController.class);

    private static final String NORMAL_STYLE = "-fx-control-inner-background: #1a1a1a; -fx-text-fill: #e0e0e0;";
    private static final String ERROR_STYLE  = "-fx-control-inner-background: #1a1a1a; -fx-text-fill: #ff6b6b;";

    @FXML private TextField fieldDistance;
    @FXML private TextField fieldFuelUsed;
    @FXML private TextField fieldFuelPrice;
    @FXML private TextArea  resultArea;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        CalculatorService svc = ServiceFactory.getInstance().getService(CalculatorType.FUEL);
        log.debug("FuelCalculatorController initialized, service={}", svc.getClass().getSimpleName());
    }

    @FXML
    private void onCalculate(ActionEvent event) {
        String distance  = fieldDistance.getText().trim();
        String fuelUsed  = fieldFuelUsed.getText().trim();
        String fuelPrice = fieldFuelPrice.getText().trim();

        if (distance.isEmpty() || fuelUsed.isEmpty()) {
            showErrorDialog("Missing Input", "Please enter at least Distance and Fuel Used.");
            return;
        }

        Map<String, String> inputs = new LinkedHashMap<>();
        inputs.put("distance", distance);
        inputs.put("fuelUsed", fuelUsed);
        // Only pass fuelPrice when user supplied a value — service treats absence as "no cost calc"
        if (!fuelPrice.isEmpty()) {
            inputs.put("fuelPrice", fuelPrice);
        }

        try {
            CalculatorService svc = ServiceFactory.getInstance().getService(CalculatorType.FUEL);
            String result = svc.calculate(inputs);
            log.debug("Fuel result: {}", result);
            displayResult(result);
            if (!result.startsWith("Error:")) {
                String inputSummary = "Dist=" + distance + ", Fuel=" + fuelUsed
                        + (fuelPrice.isEmpty() ? "" : ", Price=" + fuelPrice);
                HistoryService.getInstance().addEntry("Fuel", inputSummary, result);
            }
        } catch (IllegalArgumentException e) {
            log.warn("FUEL service not registered", e);
            displayResult("Error: Service not available");
        }
    }

    private void displayResult(String result) {
        if (result.startsWith("Error:")) {
            resultArea.setStyle(ERROR_STYLE);
            log.warn("Fuel service returned error: {}", result);
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
