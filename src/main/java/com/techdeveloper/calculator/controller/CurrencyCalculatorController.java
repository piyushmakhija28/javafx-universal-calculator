package com.techdeveloper.calculator.controller;

import com.techdeveloper.calculator.service.CalculatorService;
import com.techdeveloper.calculator.service.CalculatorType;
import com.techdeveloper.calculator.service.ServiceFactory;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Controller for currency-calculator.fxml.
 * Populates the 20 currency combo boxes in initialize() and delegates conversion to service.
 * Service inputs: "amount", "fromCurrency" (3-letter code), "toCurrency" (3-letter code).
 * Result displayed directly in labelResult — no pipe-parsing.
 */
public class CurrencyCalculatorController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(CurrencyCalculatorController.class);

    @FXML private TextField fieldAmount;
    @FXML private ComboBox<String> comboFrom;
    @FXML private ComboBox<String> comboTo;
    @FXML private Label labelResult;

    /** 20 commonly used currencies for the offline static converter. */
    private static final List<String> CURRENCIES = List.of(
            "USD - US Dollar",
            "EUR - Euro",
            "GBP - British Pound",
            "INR - Indian Rupee",
            "JPY - Japanese Yen",
            "AUD - Australian Dollar",
            "CAD - Canadian Dollar",
            "CHF - Swiss Franc",
            "CNY - Chinese Yuan",
            "HKD - Hong Kong Dollar",
            "SGD - Singapore Dollar",
            "SEK - Swedish Krona",
            "NOK - Norwegian Krone",
            "NZD - New Zealand Dollar",
            "DKK - Danish Krone",
            "BRL - Brazilian Real",
            "ZAR - South African Rand",
            "MXN - Mexican Peso",
            "AED - UAE Dirham",
            "SAR - Saudi Riyal"
    );

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        CalculatorService svc = ServiceFactory.getInstance().getService(CalculatorType.CURRENCY);
        log.debug("CurrencyCalculatorController initialized, service={}", svc.getClass().getSimpleName());
        comboFrom.setItems(FXCollections.observableArrayList(CURRENCIES));
        comboTo.setItems(FXCollections.observableArrayList(CURRENCIES));
        comboFrom.getSelectionModel().select(0); // USD
        comboTo.getSelectionModel().select(3);   // INR
    }

    @FXML
    private void onConvert(ActionEvent event) {
        String amountText = fieldAmount.getText().trim();
        String fromItem   = comboFrom.getValue();
        String toItem     = comboTo.getValue();

        if (amountText.isEmpty()) {
            showErrorDialog("Missing Input", "Please enter an amount to convert.");
            return;
        }
        if (fromItem == null || toItem == null) {
            showErrorDialog("Missing Input", "Please select both From and To currencies.");
            return;
        }

        String fromCode = extractCode(fromItem);
        String toCode   = extractCode(toItem);

        Map<String, String> inputs = new LinkedHashMap<>();
        inputs.put("amount",       amountText);
        inputs.put("fromCurrency", fromCode);
        inputs.put("toCurrency",   toCode);

        try {
            CalculatorService svc = ServiceFactory.getInstance().getService(CalculatorType.CURRENCY);
            String result = svc.calculate(inputs);
            log.debug("Currency conversion result: {}", result);
            displayResult(result);
        } catch (IllegalArgumentException e) {
            log.warn("CURRENCY service not registered", e);
            displayResult("Error: Service not available");
        }
    }

    private void displayResult(String result) {
        if (result.startsWith("Error:")) {
            labelResult.setText(result);
            labelResult.setStyle("-fx-text-fill: #ff6b6b; -fx-font-size: 14px;");
        } else {
            labelResult.setText(result);
            labelResult.setStyle("-fx-text-fill: #4a90d9; -fx-font-size: 16px; -fx-font-weight: bold;");
        }
    }

    /** Extracts the 3-letter currency code from "USD - US Dollar". */
    private String extractCode(String item) {
        return item.substring(0, 3);
    }

    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Input Error");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
