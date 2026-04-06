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
 * Controller for discount-calculator.fxml.
 * Reads original price and discount percentage, delegates to service.
 */
public class DiscountCalculatorController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(DiscountCalculatorController.class);

    @FXML private TextField fieldOriginalPrice;
    @FXML private TextField fieldDiscount;
    @FXML private Label labelFinalPrice;
    @FXML private Label labelAmountSaved;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // No pre-population required
    }

    @FXML
    private void onCalculate(ActionEvent event) {
        String price    = fieldOriginalPrice.getText().trim();
        String discount = fieldDiscount.getText().trim();

        if (price.isEmpty() || discount.isEmpty()) {
            showErrorDialog("Missing Input", "Please enter both the original price and the discount percentage.");
            return;
        }

        Map<String, String> inputs = new LinkedHashMap<>();
        inputs.put("originalPrice", price);
        inputs.put("discount",      discount);

        try {
            CalculatorService svc = ServiceFactory.getInstance().getService(CalculatorType.DISCOUNT);
            String result = svc.calculate(inputs);
            log.debug("Discount result: {}", result);
            displayResult(result);
        } catch (IllegalArgumentException e) {
            log.warn("DISCOUNT service not registered", e);
            displayError("Service not available");
        }
    }

    // Result format: "finalPrice=<value>|amountSaved=<value>"

    private void displayResult(String result) {
        if (result.startsWith("Error:")) {
            displayError(result.substring("Error:".length()).trim());
            return;
        }
        Map<String, String> parsed = parseKeyValue(result);
        setLabel(labelFinalPrice,  parsed.getOrDefault("finalPrice",  "—"), "#2ecc71", "22px");
        setLabel(labelAmountSaved, parsed.getOrDefault("amountSaved", "—"), "#4a90d9", "18px");
    }

    private void displayError(String message) {
        setLabel(labelFinalPrice,  "Error: " + message, "#ff6b6b", "14px");
        setLabel(labelAmountSaved, "—", "#9e9e9e", "14px");
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
