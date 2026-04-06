package com.techdeveloper.calculator.controller;

import com.techdeveloper.calculator.dto.TipCalculatorResult;
import com.techdeveloper.calculator.form.TipCalculatorForm;
import com.techdeveloper.calculator.service.CalculatorService;
import com.techdeveloper.calculator.service.HistoryService;
import com.techdeveloper.calculator.service.impl.ServiceFactory;
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
import java.util.ResourceBundle;

/**
 * Controller for tip-calculator.fxml.
 * Binds the slider to its percentage label and delegates computation to TipCalculatorService.
 * Service form: TipCalculatorForm(billAmount, tipPercent, splitBy).
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

    private CalculatorService<TipCalculatorForm, TipCalculatorResult> service;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        service = ServiceFactory.getInstance().getTipService();
        log.debug("TipCalculatorController initialized, service={}", service.getClass().getSimpleName());

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

        try {
            double billAmount = Double.parseDouble(bill);
            double tipPercent = sliderTip.getValue();
            String splitText = spinnerSplit.getText().trim();
            int splitCount = splitText.isEmpty() ? 1 : Integer.parseInt(splitText);

            TipCalculatorForm form = new TipCalculatorForm(billAmount, tipPercent, splitCount);
            TipCalculatorResult result = service.calculate(form);
            log.debug("Tip result: isError={}", result.isError());

            if (result.isError()) {
                displayResult("Error: " + result.errorMessage(), true);
            } else {
                String formatted = String.format(
                    "Tip Amount: %.2f%nTotal: %.2f%nPer Person: %.2f",
                    result.tipAmount(), result.total(), result.perPerson());
                displayResult(formatted, false);
                String inputSummary = "Bill=" + bill + ", Tip=" + (int) tipPercent + "%, Split=" + splitCount;
                HistoryService.getInstance().addEntry("Tip", inputSummary, formatted);
            }
        } catch (NumberFormatException e) {
            log.warn("Tip controller: invalid number input", e);
            displayResult("Error: Invalid numeric input", true);
        }
    }

    private void displayResult(String text, boolean isError) {
        resultArea.setStyle(isError ? ERROR_STYLE : NORMAL_STYLE);
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
