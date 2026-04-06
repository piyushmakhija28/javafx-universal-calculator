package com.techdeveloper.calculator.controller;

import com.techdeveloper.calculator.service.CalculatorService;
import com.techdeveloper.calculator.service.CalculatorType;
import com.techdeveloper.calculator.service.ServiceFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Controller for tip-calculator.fxml.
 * Binds the slider to its percentage label and delegates computation to service.
 */
public class TipCalculatorController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(TipCalculatorController.class);

    @FXML private TextField fieldBill;
    @FXML private Slider sliderTip;
    @FXML private Label labelTipPercent;
    @FXML private Spinner<Integer> spinnerSplit;
    @FXML private Label labelTipAmount;
    @FXML private Label labelTotal;
    @FXML private Label labelPerPerson;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Wire spinner value factory (FXML SpinnerValueFactory requires specific setup)
        spinnerSplit.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1));

        // Bind slider to the percentage label
        sliderTip.valueProperty().addListener((obs, oldVal, newVal) -> {
            int pct = newVal.intValue();
            labelTipPercent.setText(pct + "%");
        });
        log.debug("TipCalculatorController initialized");
    }

    @FXML
    private void onCalculate(ActionEvent event) {
        String bill = fieldBill.getText().trim();
        if (bill.isEmpty()) {
            showErrorDialog("Missing Input", "Please enter the bill amount.");
            return;
        }

        int tipPercent = (int) sliderTip.getValue();
        int splitCount = spinnerSplit.getValue();

        Map<String, String> inputs = new LinkedHashMap<>();
        inputs.put("bill",      bill);
        inputs.put("tipPercent", String.valueOf(tipPercent));
        inputs.put("split",     String.valueOf(splitCount));

        try {
            CalculatorService svc = ServiceFactory.getInstance().getService(CalculatorType.TIP);
            String result = svc.calculate(inputs);
            log.debug("Tip result: {}", result);
            displayResult(result);
        } catch (IllegalArgumentException e) {
            log.warn("TIP service not registered", e);
            displayError("Service not available");
        }
    }

    // Result format: "tip=<value>|total=<value>|perPerson=<value>"

    private void displayResult(String result) {
        if (result.startsWith("Error:")) {
            displayError(result.substring("Error:".length()).trim());
            return;
        }
        Map<String, String> parsed = parseKeyValue(result);
        setLabel(labelTipAmount, parsed.getOrDefault("tip",       "—"), "#4a90d9", "18px");
        setLabel(labelTotal,     parsed.getOrDefault("total",     "—"), "#ffffff", "16px");
        setLabel(labelPerPerson, parsed.getOrDefault("perPerson", "—"), "#2ecc71", "18px");
    }

    private void displayError(String message) {
        setLabel(labelTipAmount, "Error: " + message, "#ff6b6b", "14px");
        setLabel(labelTotal,     "—", "#9e9e9e", "14px");
        setLabel(labelPerPerson, "—", "#9e9e9e", "14px");
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
