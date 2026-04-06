package com.techdeveloper.calculator.controller;

import com.techdeveloper.calculator.service.CalculatorService;
import com.techdeveloper.calculator.service.CalculatorType;
import com.techdeveloper.calculator.service.HistoryService;
import com.techdeveloper.calculator.service.ServiceFactory;
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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Controller for age-calculator.fxml.
 * Reads DOB and target date (defaults to today) from DatePickers and delegates to service.
 * Service inputs: "birthDate" (YYYY-MM-DD), "toDate" (YYYY-MM-DD, optional).
 * Result displayed directly in resultArea — no pipe-parsing.
 */
public class AgeCalculatorController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(AgeCalculatorController.class);

    private static final String NORMAL_STYLE = "-fx-control-inner-background: #1a1a1a; -fx-text-fill: #e0e0e0;";
    private static final String ERROR_STYLE  = "-fx-control-inner-background: #1a1a1a; -fx-text-fill: #ff6b6b;";

    @FXML private DatePicker pickerDOB;
    @FXML private DatePicker pickerTarget;
    @FXML private TextArea   resultArea;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        CalculatorService svc = ServiceFactory.getInstance().getService(CalculatorType.AGE);
        log.debug("AgeCalculatorController initialized, service={}", svc.getClass().getSimpleName());
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

        Map<String, String> inputs = new LinkedHashMap<>();
        inputs.put("birthDate", dob.toString());
        inputs.put("toDate",    target.toString());

        try {
            CalculatorService svc = ServiceFactory.getInstance().getService(CalculatorType.AGE);
            String result = svc.calculate(inputs);
            log.debug("Age result: {}", result);
            displayResult(result);
            if (!result.startsWith("Error:")) {
                String inputSummary = "DOB=" + dob + ", To=" + target;
                HistoryService.getInstance().addEntry("Age", inputSummary, result);
            }
        } catch (IllegalArgumentException e) {
            log.warn("AGE service not registered", e);
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
