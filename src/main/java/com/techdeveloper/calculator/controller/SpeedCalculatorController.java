package com.techdeveloper.calculator.controller;

import com.techdeveloper.calculator.constants.SolveFor;
import com.techdeveloper.calculator.dto.SpeedCalculatorResult;
import com.techdeveloper.calculator.form.SpeedCalculatorForm;
import com.techdeveloper.calculator.service.CalculatorService;
import com.techdeveloper.calculator.service.HistoryService;
import com.techdeveloper.calculator.service.impl.ServiceFactory;
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
import java.util.List;
import java.util.ResourceBundle;

/**
 * Controller for speed-calculator.fxml.
 * "Solve For" ComboBox tells the service which variable to compute.
 * Service form: SpeedCalculatorForm(solve, speed, distance, time).
 * Result displayed directly in labelResult — no pipe-parsing.
 */
public class SpeedCalculatorController implements Initializable {

    public SpeedCalculatorController() {
        // required for FXML
    }

    private static final Logger log = LoggerFactory.getLogger(SpeedCalculatorController.class);

    @FXML private ComboBox<String> comboSolveFor;
    @FXML private TextField fieldSpeed;
    @FXML private TextField fieldDistance;
    @FXML private TextField fieldTime;
    @FXML private Label labelResult;

    private CalculatorService<SpeedCalculatorForm, SpeedCalculatorResult> service;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        service = ServiceFactory.getInstance().getSpeedService();
        log.debug("SpeedCalculatorController initialized, service={}", service.getClass().getSimpleName());
        comboSolveFor.setItems(FXCollections.observableArrayList(
                List.of("Speed", "Distance", "Time")));
        comboSolveFor.getSelectionModel().selectFirst();
    }

    @FXML
    private void onCalculate(ActionEvent event) {
        String solveForDisplay = comboSolveFor.getValue();
        String speedText    = fieldSpeed.getText().trim();
        String distanceText = fieldDistance.getText().trim();
        String timeText     = fieldTime.getText().trim();

        try {
            // Map display selection to SolveFor enum
            SolveFor solveFor = SolveFor.valueOf(
                solveForDisplay != null ? solveForDisplay.toUpperCase() : "SPEED");

            Double speed    = speedText.isEmpty()    ? null : Double.parseDouble(speedText);
            Double distance = distanceText.isEmpty() ? null : Double.parseDouble(distanceText);
            Double time     = timeText.isEmpty()     ? null : Double.parseDouble(timeText);

            SpeedCalculatorForm form = new SpeedCalculatorForm(solveFor, speed, distance, time);
            SpeedCalculatorResult result = service.calculate(form);
            log.debug("Speed result: isError={}", result.isError());

            if (result.isError()) {
                displayResult("Error: " + result.errorMessage());
            } else {
                String formatted = String.format("%s = %.4f",
                    result.solvedVariable().name(), result.value());
                displayResult(formatted);
                String inputSummary = "Solve=" + solveFor
                        + (!speedText.isEmpty()    ? ", S=" + speedText    : "")
                        + (!distanceText.isEmpty() ? ", D=" + distanceText : "")
                        + (!timeText.isEmpty()     ? ", T=" + timeText     : "");
                HistoryService.getInstance().addEntry("Speed", inputSummary, formatted);
            }
        } catch (NumberFormatException e) {
            log.warn("Speed controller: invalid number input", e);
            displayResult("Error: Invalid numeric input");
        } catch (IllegalArgumentException e) {
            log.warn("Speed controller: unknown solve-for value={}", solveForDisplay, e);
            displayResult("Error: Unknown solve-for value");
        }
    }

    private void displayResult(String result) {
        if (result.startsWith("Error:")) {
            labelResult.setText(result);
            labelResult.setStyle("-fx-text-fill: #ff6b6b; -fx-font-size: 14px;");
        } else {
            labelResult.setText(result);
            labelResult.setStyle("-fx-text-fill: #4a90d9; -fx-font-size: 22px; -fx-font-weight: bold;");
        }
    }

    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Input Error");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
