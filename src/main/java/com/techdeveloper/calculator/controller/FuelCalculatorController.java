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
 * Controller for fuel-calculator.fxml.
 * Collects distance, fuel used, and cost per litre; delegates to FUEL service.
 * Displays L/100km, km/L, and cost per km in result labels.
 */
public class FuelCalculatorController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(FuelCalculatorController.class);

    @FXML private TextField fieldDistance;
    @FXML private TextField fieldFuel;
    @FXML private TextField fieldCostPerLitre;
    @FXML private Label labelL100km;
    @FXML private Label labelKmPerL;
    @FXML private Label labelCostPerKm;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // No pre-population required
    }

    @FXML
    private void onCalculate(ActionEvent event) {
        String distance     = fieldDistance.getText().trim();
        String fuel         = fieldFuel.getText().trim();
        String costPerLitre = fieldCostPerLitre.getText().trim();

        if (distance.isEmpty() || fuel.isEmpty()) {
            showErrorDialog("Missing Input", "Please enter at least distance and fuel used.");
            return;
        }

        Map<String, String> inputs = new LinkedHashMap<>();
        inputs.put("distance",     distance);
        inputs.put("fuel",         fuel);
        inputs.put("costPerLitre", costPerLitre.isEmpty() ? "0" : costPerLitre);

        try {
            CalculatorService svc = ServiceFactory.getInstance().getService(CalculatorType.FUEL);
            String result = svc.calculate(inputs);
            log.debug("Fuel result: {}", result);
            displayResult(result);
        } catch (IllegalArgumentException e) {
            log.warn("FUEL service not registered", e);
            displayError("Service not available");
        }
    }

    // Result format: "l100km=<v>|kmPerL=<v>|costPerKm=<v>"

    private void displayResult(String result) {
        if (result.startsWith("Error:")) {
            displayError(result.substring("Error:".length()).trim());
            return;
        }
        Map<String, String> parsed = parseKeyValue(result);
        setLabel(labelL100km,    parsed.getOrDefault("l100km",    "—"), "#4a90d9", "18px");
        setLabel(labelKmPerL,    parsed.getOrDefault("kmPerL",    "—"), "#2ecc71", "18px");
        setLabel(labelCostPerKm, parsed.getOrDefault("costPerKm", "—"), "#ffffff", "16px");
    }

    private void displayError(String message) {
        setLabel(labelL100km,    "Error: " + message, "#ff6b6b", "14px");
        setLabel(labelKmPerL,    "—", "#9e9e9e", "14px");
        setLabel(labelCostPerKm, "—", "#9e9e9e", "14px");
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
