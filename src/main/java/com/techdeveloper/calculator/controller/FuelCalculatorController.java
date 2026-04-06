package com.techdeveloper.calculator.controller;

import com.techdeveloper.calculator.dto.FuelCalculatorResult;
import com.techdeveloper.calculator.form.FuelCalculatorForm;
import com.techdeveloper.calculator.service.CalculatorService;
import com.techdeveloper.calculator.service.HistoryService;
import com.techdeveloper.calculator.service.impl.ServiceFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for fuel-calculator.fxml.
 * Collects distance, fuelUsed, and fuelPrice (optional); delegates to FuelCalculatorService.
 * Service form: FuelCalculatorForm(distance, fuelUsed, fuelPrice).
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

    private CalculatorService<FuelCalculatorForm, FuelCalculatorResult> service;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        service = ServiceFactory.getInstance().getFuelService();
        log.debug("FuelCalculatorController initialized, service={}", service.getClass().getSimpleName());
    }

    @FXML
    private void onCalculate(ActionEvent event) {
        String distanceText  = fieldDistance.getText().trim();
        String fuelUsedText  = fieldFuelUsed.getText().trim();
        String fuelPriceText = fieldFuelPrice.getText().trim();

        if (distanceText.isEmpty() || fuelUsedText.isEmpty()) {
            showErrorDialog("Missing Input", "Please enter at least Distance and Fuel Used.");
            return;
        }

        try {
            double distance  = Double.parseDouble(distanceText);
            double fuelUsed  = Double.parseDouble(fuelUsedText);
            // Only pass fuelPrice when user supplied a value — null means no cost calc
            Double fuelPrice = fuelPriceText.isEmpty() ? null : Double.parseDouble(fuelPriceText);

            FuelCalculatorForm form = new FuelCalculatorForm(distance, fuelUsed, fuelPrice);
            FuelCalculatorResult result = service.calculate(form);
            log.debug("Fuel result: isError={}", result.isError());

            if (result.isError()) {
                displayResult("Error: " + result.errorMessage(), true);
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append(String.format("L/100km: %.2f%n", result.per100km()));
                sb.append(String.format("km/L: %.2f", result.kmPerLiter()));
                if (result.costPerKm() != null) {
                    sb.append(String.format("%nCost/km: %.2f", result.costPerKm()));
                }
                if (result.totalCost() != null) {
                    sb.append(String.format("%nTotal Cost: %.2f", result.totalCost()));
                }
                String formatted = sb.toString();
                displayResult(formatted, false);
                String inputSummary = "Dist=" + distanceText + ", Fuel=" + fuelUsedText
                        + (fuelPriceText.isEmpty() ? "" : ", Price=" + fuelPriceText);
                HistoryService.getInstance().addEntry("Fuel", inputSummary, formatted);
            }
        } catch (NumberFormatException e) {
            log.warn("Fuel controller: invalid number input", e);
            displayResult("Error: Invalid numeric input", true);
        }
    }

    private void displayResult(String text, boolean isError) {
        if (isError) {
            resultArea.setStyle(ERROR_STYLE);
            log.warn("Fuel service returned error: {}", text);
        } else {
            resultArea.setStyle(NORMAL_STYLE);
        }
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
