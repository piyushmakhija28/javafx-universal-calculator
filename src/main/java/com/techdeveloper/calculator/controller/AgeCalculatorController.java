package com.techdeveloper.calculator.controller;

import com.techdeveloper.calculator.dto.AgeCalculatorResult;
import com.techdeveloper.calculator.form.AgeCalculatorForm;
import com.techdeveloper.calculator.service.CalculatorService;
import com.techdeveloper.calculator.service.HistoryService;
import com.techdeveloper.calculator.service.impl.ServiceFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextArea;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

/**
 * Controller for age-calculator.fxml.
 * Reads DOB and target date (defaults to today) from DatePickers and delegates to service.
 * Service form: AgeCalculatorForm(birthDate, toDate).
 * Result displayed directly in resultArea — no pipe-parsing.
 */
public class AgeCalculatorController implements Initializable {

    public AgeCalculatorController() {
        // required for FXML
    }

    private static final Logger log = LoggerFactory.getLogger(AgeCalculatorController.class);

    private static final String NORMAL_STYLE = "-fx-control-inner-background: #1a1a1a; -fx-text-fill: #e0e0e0;";
    private static final String ERROR_STYLE  = "-fx-control-inner-background: #1a1a1a; -fx-text-fill: #ff6b6b;";

    @FXML private DatePicker pickerDOB;
    @FXML private DatePicker pickerTarget;
    @FXML private TextArea   resultArea;

    private CalculatorService<AgeCalculatorForm, AgeCalculatorResult> service;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        service = ServiceFactory.getInstance().getAgeService();
        log.debug("AgeCalculatorController initialized, service={}", service.getClass().getSimpleName());
        // Pre-populate target date with today
        if (pickerTarget != null) pickerTarget.setValue(LocalDate.now());
    }

    @FXML
    private void onCalculate(ActionEvent event) {
        LocalDate dob = pickerDOB.getValue();
        if (dob == null) {
            showErrorDialog("Missing Input", "Please select a date of birth.");
            return;
        }
        LocalDate target = (pickerTarget != null && pickerTarget.getValue() != null)
                           ? pickerTarget.getValue()
                           : LocalDate.now();

        if (dob.isAfter(target)) {
            showErrorDialog("Invalid Date", "Date of birth cannot be after the target date.");
            return;
        }

        AgeCalculatorForm form = new AgeCalculatorForm(dob, target);
        AgeCalculatorResult result = service.calculate(form);
        log.debug("Age result: isError={}", result.isError());

        if (result.isError()) {
            displayResult("Error: " + result.errorMessage(), true);
        } else {
            String formatted = String.format("%d years, %d months, %d days",
                result.years(), result.months(), result.days());
            displayResult(formatted, false);
            String inputSummary = "DOB=" + dob + ", To=" + target;
            HistoryService.getInstance().addEntry("Age", inputSummary, formatted);
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
