package com.techdeveloper.calculator.controller;

import com.techdeveloper.calculator.dto.EMICalculatorResult;
import com.techdeveloper.calculator.form.EMICalculatorForm;
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
 * Controller for emi-calculator.fxml.
 * Collects principal, annualRate, and tenureMonths; passes them to EMICalculatorService.
 * Displays the formatted result in resultArea — no pipe-parsing.
 */
public class EMICalculatorController implements Initializable {

    public EMICalculatorController() {
        // required for FXML
    }

    private static final Logger log = LoggerFactory.getLogger(EMICalculatorController.class);

    private static final String NORMAL_STYLE = "-fx-control-inner-background: #1a1a1a; -fx-text-fill: #e0e0e0;";
    private static final String ERROR_STYLE  = "-fx-control-inner-background: #1a1a1a; -fx-text-fill: #ff6b6b;";

    @FXML private TextField fieldPrincipal;
    @FXML private TextField fieldRate;
    @FXML private TextField fieldTenure;
    @FXML private TextArea  resultArea;

    private CalculatorService<EMICalculatorForm, EMICalculatorResult> service;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        service = ServiceFactory.getInstance().getEmiService();
        log.debug("EMICalculatorController initialized, service={}", service.getClass().getSimpleName());
    }

    @FXML
    private void onCalculate(ActionEvent event) {
        String principalText = fieldPrincipal.getText().trim();
        String rateText      = fieldRate.getText().trim();
        String tenureText    = fieldTenure.getText().trim();

        if (principalText.isEmpty() || rateText.isEmpty() || tenureText.isEmpty()) {
            showErrorDialog("Missing Input", "Please enter Principal, Annual Rate, and Tenure (months).");
            return;
        }

        try {
            double principal = Double.parseDouble(principalText);
            double annualRate = Double.parseDouble(rateText);
            int tenureMonths = Integer.parseInt(tenureText);

            EMICalculatorForm form = new EMICalculatorForm(principal, annualRate, tenureMonths);
            EMICalculatorResult result = service.calculate(form);
            log.debug("EMI calculation result: isError={}", result.isError());

            if (result.isError()) {
                displayResult("Error: " + result.errorMessage(), true);
            } else {
                String formatted = String.format(
                    "Monthly EMI: %.2f%nTotal Interest: %.2f%nTotal Amount: %.2f",
                    result.monthlyEMI(), result.totalInterest(), result.totalAmount());
                displayResult(formatted, false);
                String inputSummary = "P=" + principalText + ", R=" + rateText + ", N=" + tenureText;
                HistoryService.getInstance().addEntry("EMI", inputSummary, formatted);
            }
        } catch (NumberFormatException e) {
            log.warn("EMI controller: invalid number input", e);
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
