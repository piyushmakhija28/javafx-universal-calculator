package com.techdeveloper.calculator.controller;

import com.techdeveloper.calculator.service.CalculatorService;
import com.techdeveloper.calculator.service.CalculatorType;
import com.techdeveloper.calculator.service.HistoryService;
import com.techdeveloper.calculator.service.LiveCurrencyService;
import com.techdeveloper.calculator.service.ServiceFactory;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.concurrent.Task;
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
 *
 * <p>On initialization a background thread attempts to fetch live rates via
 * {@link LiveCurrencyService}. The {@code labelRateStatus} label reflects the
 * current state ("Fetching live rates...", "Live (HH:mm)", or "Offline (static rates)").
 *
 * <p>Conversion logic:
 * <ul>
 *   <li>If live rates are available: {@code convertedAmount = amount * ratesTo / ratesFrom}
 *   <li>Fallback: delegates to {@link com.techdeveloper.calculator.service.CurrencyCalculatorService}
 * </ul>
 *
 * <p>Service inputs when delegating to the static service:
 * "amount", "fromCurrency" (3-letter code), "toCurrency" (3-letter code).
 */
public class CurrencyCalculatorController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(CurrencyCalculatorController.class);

    @FXML private TextField   fieldAmount;
    @FXML private ComboBox<String> comboFrom;
    @FXML private ComboBox<String> comboTo;
    @FXML private Label       labelResult;
    @FXML private Label       labelRateStatus;

    /**
     * Holds live rates fetched from the network.
     * Null means the fetch failed or has not completed yet — use static fallback.
     */
    private Map<String, Double> liveRates = null;

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
        comboFrom.setItems(FXCollections.observableArrayList(CURRENCIES));
        comboTo.setItems(FXCollections.observableArrayList(CURRENCIES));
        comboFrom.getSelectionModel().select(0); // USD
        comboTo.getSelectionModel().select(3);   // INR

        setStatusLabel("Fetching live rates...", "#9e9e9e");

        Task<Map<String, Double>> fetchTask = LiveCurrencyService.getInstance().fetchRatesAsync();

        fetchTask.setOnSucceeded(event -> {
            Map<String, Double> rates = fetchTask.getValue();
            Platform.runLater(() -> {
                if (rates != null && !rates.isEmpty()) {
                    liveRates = rates;
                    String time = LiveCurrencyService.getInstance().getLastFetchTimeFormatted();
                    setStatusLabel("Live (" + time + ")", "#2ecc71");
                    log.info("CurrencyCalculatorController: live rates loaded ({} currencies, fetched at {})",
                            rates.size(), time);
                } else {
                    setStatusLabel("Offline (static rates)", "#9e9e9e");
                    log.info("CurrencyCalculatorController: live rate fetch returned null — using static rates");
                }
            });
        });

        fetchTask.setOnFailed(event -> Platform.runLater(() -> {
            setStatusLabel("Offline (static rates)", "#9e9e9e");
            log.warn("CurrencyCalculatorController: live rate fetch task failed — using static rates",
                    fetchTask.getException());
        }));

        Thread fetchThread = new Thread(fetchTask);
        fetchThread.setDaemon(true);
        fetchThread.setName("currency-rate-fetch");
        fetchThread.start();

        log.debug("CurrencyCalculatorController initialized — background rate fetch started");
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

        if (liveRates != null && liveRates.containsKey(fromCode) && liveRates.containsKey(toCode)) {
            convertWithLiveRates(amountText, fromCode, toCode);
        } else {
            convertWithStaticRates(amountText, fromCode, toCode);
        }
    }

    // ── Conversion helpers ────────────────────────────────────────────────────

    private void convertWithLiveRates(String amountText, String fromCode, String toCode) {
        try {
            double amount = Double.parseDouble(amountText);
            if (amount < 0) {
                displayResult("Error: amount cannot be negative");
                return;
            }

            double rateFrom = liveRates.get(fromCode);
            double rateTo   = liveRates.get(toCode);

            // Both rates are relative to USD; formula: amount / rateFrom * rateTo
            double converted    = amount * rateTo / rateFrom;
            double exchangeRate = rateTo / rateFrom;

            String result = String.format("%.2f %s = %.2f %s (Rate: 1 %s = %.4f %s)",
                    amount, fromCode, converted, toCode, fromCode, exchangeRate, toCode);

            log.debug("CurrencyCalculatorController: live conversion — {}", result);
            displayResult(result);
            String inputSummary = amountText + " " + fromCode + " -> " + toCode;
            HistoryService.getInstance().addEntry("Currency", inputSummary, result);

        } catch (NumberFormatException e) {
            displayResult("Error: Invalid number format for amount");
        }
    }

    private void convertWithStaticRates(String amountText, String fromCode, String toCode) {
        Map<String, String> inputs = new LinkedHashMap<>();
        inputs.put("amount",       amountText);
        inputs.put("fromCurrency", fromCode);
        inputs.put("toCurrency",   toCode);

        try {
            CalculatorService svc = ServiceFactory.getInstance().getService(CalculatorType.CURRENCY);
            String result = svc.calculate(inputs);
            log.debug("CurrencyCalculatorController: static conversion result — {}", result);
            displayResult(result);
            if (!result.startsWith("Error:")) {
                String inputSummary = amountText + " " + fromCode + " -> " + toCode;
                HistoryService.getInstance().addEntry("Currency", inputSummary, result);
            }
        } catch (IllegalArgumentException e) {
            log.warn("CurrencyCalculatorController: CURRENCY service not registered", e);
            displayResult("Error: Service not available");
        }
    }

    // ── UI helpers ────────────────────────────────────────────────────────────

    private void displayResult(String result) {
        if (result.startsWith("Error:")) {
            labelResult.setText(result);
            labelResult.setStyle("-fx-text-fill: #ff6b6b; -fx-font-size: 14px;");
        } else {
            labelResult.setText(result);
            labelResult.setStyle("-fx-text-fill: #4a90d9; -fx-font-size: 16px; -fx-font-weight: bold;");
        }
    }

    private void setStatusLabel(String text, String colorHex) {
        if (labelRateStatus != null) {
            labelRateStatus.setText(text);
            labelRateStatus.setStyle(
                    "-fx-font-size: 11px; -fx-font-style: italic; -fx-text-fill: " + colorHex + ";");
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
