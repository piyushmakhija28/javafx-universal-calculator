package com.techdeveloper.calculator.controller;

import com.techdeveloper.calculator.dto.DiscountCalculatorResult;
import com.techdeveloper.calculator.form.DiscountCalculatorForm;
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
 * Controller for discount-calculator.fxml.
 * Reads original price and discount percentage, delegates to DiscountCalculatorService.
 * Service form: DiscountCalculatorForm(originalPrice, discountPercent).
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

    private CalculatorService<DiscountCalculatorForm, DiscountCalculatorResult> service;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        service = ServiceFactory.getInstance().getDiscountService();
        log.debug("DiscountCalculatorController initialized, service={}", service.getClass().getSimpleName());
    }

    @FXML
    private void onCalculate(ActionEvent event) {
        String price    = fieldOriginalPrice.getText().trim();
        String discount = fieldDiscount.getText().trim();

        if (price.isEmpty() || discount.isEmpty()) {
            showErrorDialog("Missing Input", "Please enter both the original price and the discount percentage.");
            return;
        }

        try {
            double originalPrice = Double.parseDouble(price);
            double discountPercent = Double.parseDouble(discount);

            DiscountCalculatorForm form = new DiscountCalculatorForm(originalPrice, discountPercent);
            DiscountCalculatorResult result = service.calculate(form);
            log.debug("Discount result: isError={}", result.isError());

            if (result.isError()) {
                displayResult("Error: " + result.errorMessage(), true);
            } else {
                String formatted = String.format(
                    "Discount Amount: %.2f%nFinal Price: %.2f",
                    result.discountAmount(), result.finalPrice());
                displayResult(formatted, false);
                String inputSummary = "Price=" + price + ", Discount=" + discount + "%";
                HistoryService.getInstance().addEntry("Discount", inputSummary, formatted);
            }
        } catch (NumberFormatException e) {
            log.warn("Discount controller: invalid number input", e);
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
