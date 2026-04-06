package com.techdeveloper.calculator.controller;

import com.techdeveloper.calculator.service.CalculatorService;
import com.techdeveloper.calculator.service.CalculatorType;
import com.techdeveloper.calculator.service.HistoryService;
import com.techdeveloper.calculator.service.ServiceFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Controller for tip-calculator.fxml.
 * Binds the slider to its percentage label and delegates computation to TipCalculatorService.
 * Service inputs: "billAmount", "tipPercent", "splitBy".
 * Result displayed directly in resultArea — no pipe-parsing.
 */
public class TipCalculatorController implements Initializable {

    public TipCalculatorController() {
        // required for FXML
    }

    private static final Logger log = LoggerFactory.getLogger(TipCalculatorController.class);

    private static final String NORMAL_STYLE = "-fx-control-inner-background: #1a1a1a; -fx-text-fill: #e0e0e0;";
    private static final String ERROR_STYLE  = "-fx-control-inner-background: #1a1a1a; -fx-text-fill: #ff6b6b;";

    @FXML private TextField fieldBill;
    @FXML private Slider sliderTip;
    @FXML private Label labelTipPercent;
    @FXML private TextField spinnerSplit;
    @FXML private TextArea resultArea;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        CalculatorService svc = ServiceFactory.getInstance().getService(CalculatorType.TIP);
        log.debug("TipCalculatorController initialized, service={}", svc.getClass().getSimpleName());

        // Bind slider to the percentage label
        sliderTip.valueProperty().addListener((obs, oldVal, newVal) -> {
            int pct = newVal.intValue();
            if (labelTipPercent != null) labelTipPercent.setText(pct + "%");
        });
    }

    @FXML
    private void onCalculate(ActionEvent event) {
        String bill = fieldBill.getText().trim();
        if (bill.isEmpty()) {
            showErrorDialog("Missing Input", "Please enter the bill amount.");
            return;
        }

        int tipPercent = (int) sliderTip.getValue();
        String splitText = spinnerSplit.getText().trim();
        int splitCount = splitText.isEmpty() ? 1 : Integer.parseInt(splitText);

        Map<String, String> inputs = new LinkedHashMap<>();
        inputs.put("billAmount", bill);
        inputs.put("tipPercent", String.valueOf(tipPercent));
        inputs.put("splitBy",    String.valueOf(splitCount));

        try {
            CalculatorService svc = ServiceFactory.getInstance().getService(CalculatorType.TIP);
            String result = svc.calculate(inputs);
            log.debug("Tip result: {}", result);
            displayResult(result);
            if (!result.startsWith("Error:")) {
                String inputSummary = "Bill=" + bill + ", Tip=" + tipPercent + "%, Split=" + splitCount;
                HistoryService.getInstance().addEntry("Tip", inputSummary, result);
            }
        } catch (IllegalArgumentException e) {
            log.warn("TIP service not registered", e);
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
