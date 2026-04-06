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
 * Controller for discount-calculator.fxml.
 * Reads original price and discount percentage, delegates to DiscountCalculatorService.
 * Service inputs: "originalPrice", "discountPercent".
 * Result displayed directly in resultArea — no pipe-parsing.
 */
public class DiscountCalculatorController implements Initializable {

    public DiscountCalculatorController() {
        // required for FXML
    }

    private static final Logger log = LoggerFactory.getLogger(DiscountCalculatorController.class);

    private static final String NORMAL_STYLE = "-fx-control-inner-background: #1a1a1a; -fx-text-fill: #e0e0e0;";
    private static final String ERROR_STYLE  = "-fx-control-inner-background: #1a1a1a; -fx-text-fill: #ff6b6b;";

    @FXML private TextField fieldOriginalPrice;
    @FXML private TextField fieldDiscount;
    @FXML private TextArea  resultArea;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        CalculatorService svc = ServiceFactory.getInstance().getService(CalculatorType.DISCOUNT);
        log.debug("DiscountCalculatorController initialized, service={}", svc.getClass().getSimpleName());
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
        inputs.put("originalPrice",   price);
        inputs.put("discountPercent", discount);

        try {
            CalculatorService svc = ServiceFactory.getInstance().getService(CalculatorType.DISCOUNT);
            String result = svc.calculate(inputs);
            log.debug("Discount result: {}", result);
            displayResult(result);
            if (!result.startsWith("Error:")) {
                String inputSummary = "Price=" + price + ", Discount=" + discount + "%";
                HistoryService.getInstance().addEntry("Discount", inputSummary, result);
            }
        } catch (IllegalArgumentException e) {
            log.warn("DISCOUNT service not registered", e);
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
