package com.techdeveloper.calculator.controller;

import com.techdeveloper.calculator.service.CalculatorService;
import com.techdeveloper.calculator.service.CalculatorType;
import com.techdeveloper.calculator.service.ServiceFactory;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Controller for speed-calculator.fxml.
 * "Solve For" ComboBox tells the service which variable to compute.
 * Service input key is "solve" (SPEED / DISTANCE / TIME), plus "speed", "distance", "time".
 * Result displayed directly in labelResult — no pipe-parsing.
 */
public class SpeedCalculatorController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(SpeedCalculatorController.class);

    @FXML private ComboBox<String> comboSolveFor;
    @FXML private TextField fieldSpeed;
    @FXML private TextField fieldDistance;
    @FXML private TextField fieldTime;
    @FXML private Label labelResult;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        CalculatorService svc = ServiceFactory.getInstance().getService(CalculatorType.SPEED);
        log.debug("SpeedCalculatorController initialized, service={}", svc.getClass().getSimpleName());
        comboSolveFor.setItems(FXCollections.observableArrayList(
                List.of("Speed", "Distance", "Time")));
        comboSolveFor.getSelectionModel().selectFirst();
    }

    @FXML
    private void onCalculate(ActionEvent event) {
        String solveForDisplay = comboSolveFor.getValue();
        String speed    = fieldSpeed.getText().trim();
        String distance = fieldDistance.getText().trim();
        String time     = fieldTime.getText().trim();

        // Map display selection to service token (uppercase)
        String solveToken = solveForDisplay != null ? solveForDisplay.toUpperCase() : "SPEED";

        Map<String, String> inputs = new LinkedHashMap<>();
        inputs.put("solve",    solveToken);
        inputs.put("speed",    speed);
        inputs.put("distance", distance);
        inputs.put("time",     time);

        try {
            CalculatorService svc = ServiceFactory.getInstance().getService(CalculatorType.SPEED);
            String result = svc.calculate(inputs);
            log.debug("Speed result: {}", result);
            displayResult(result);
        } catch (IllegalArgumentException e) {
            log.warn("SPEED service not registered", e);
            displayResult("Error: Service not available");
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
