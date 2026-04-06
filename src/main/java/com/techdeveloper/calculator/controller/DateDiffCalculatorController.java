package com.techdeveloper.calculator.controller;

import com.techdeveloper.calculator.dto.DateDiffCalculatorResult;
import com.techdeveloper.calculator.form.DateDiffCalculatorForm;
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
 * Controller for date-diff-calculator.fxml.
 * Reads start and end dates and delegates difference computation to DateDiffCalculatorService.
 * Service form: DateDiffCalculatorForm(startDate, endDate).
 * Result displayed directly in resultArea — no pipe-parsing.
 */
public class DateDiffCalculatorController implements Initializable {

    public DateDiffCalculatorController() {
        // required for FXML
    }

    private static final Logger log = LoggerFactory.getLogger(DateDiffCalculatorController.class);

    private static final String NORMAL_STYLE = "-fx-control-inner-background: #1a1a1a; -fx-text-fill: #e0e0e0;";
    private static final String ERROR_STYLE  = "-fx-control-inner-background: #1a1a1a; -fx-text-fill: #ff6b6b;";

    @FXML private DatePicker pickerStart;
    @FXML private DatePicker pickerEnd;
    @FXML private TextArea   resultArea;

    private CalculatorService<DateDiffCalculatorForm, DateDiffCalculatorResult> service;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        service = ServiceFactory.getInstance().getDateDiffService();
        log.debug("DateDiffCalculatorController initialized, service={}", service.getClass().getSimpleName());
    }

    @FXML
    private void onCalculate(ActionEvent event) {
        LocalDate start = pickerStart.getValue();
        LocalDate end   = pickerEnd.getValue();

        if (start == null || end == null) {
            showErrorDialog("Missing Input", "Please select both a start date and an end date.");
            return;
        }

        // Ensure chronological order — allow reversed input gracefully
        if (end.isBefore(start)) {
            LocalDate temp = start;
            start = end;
            end = temp;
        }

        DateDiffCalculatorForm form = new DateDiffCalculatorForm(start, end);
        DateDiffCalculatorResult result = service.calculate(form);
        log.debug("DateDiff result: isError={}", result.isError());

        if (result.isError()) {
            displayResult("Error: " + result.errorMessage(), true);
        } else {
            String formatted = String.format(
                "Total Days: %d%n%d years, %d months, %d days",
                result.totalDays(), result.years(), result.months(), result.days());
            displayResult(formatted, false);
            String inputSummary = start + " to " + end;
            HistoryService.getInstance().addEntry("Date Diff", inputSummary, formatted);
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
