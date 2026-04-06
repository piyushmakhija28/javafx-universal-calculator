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
 * Controller for unit-converter.fxml.
 * Populates category and unit ComboBoxes in initialize()/onCategoryChange().
 * Delegates conversion to UNIT_CONVERTER service — no conversion math here.
 */
public class UnitConverterController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(UnitConverterController.class);

    @FXML private ComboBox<String> comboCategory;
    @FXML private ComboBox<String> comboFrom;
    @FXML private ComboBox<String> comboTo;
    @FXML private TextField fieldValue;
    @FXML private Label labelResult;

    /** Categories and their corresponding units. */
    private static final Map<String, List<String>> UNITS = new LinkedHashMap<>(Map.of(
        "Length",      List.of("Millimeter", "Centimeter", "Meter", "Kilometer", "Inch", "Foot", "Yard", "Mile"),
        "Weight",      List.of("Milligram", "Gram", "Kilogram", "Tonne", "Ounce", "Pound", "Stone"),
        "Temperature", List.of("Celsius", "Fahrenheit", "Kelvin"),
        "Volume",      List.of("Milliliter", "Liter", "Cubic Meter", "Fluid Ounce", "Pint", "Quart", "Gallon"),
        "Speed",       List.of("m/s", "km/h", "mph", "knot"),
        "Area",        List.of("sq cm", "sq m", "sq km", "sq in", "sq ft", "sq yd", "Acre", "Hectare")
    ));

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        comboCategory.setItems(FXCollections.observableArrayList(UNITS.keySet()));
        comboCategory.getSelectionModel().selectFirst();
        updateUnitLists("Length");
        log.debug("UnitConverterController initialized");
    }

    @FXML
    private void onCategoryChange(ActionEvent event) {
        String category = comboCategory.getValue();
        if (category != null) {
            updateUnitLists(category);
        }
    }

    @FXML
    private void onConvert(ActionEvent event) {
        String category = comboCategory.getValue();
        String fromUnit = comboFrom.getValue();
        String toUnit   = comboTo.getValue();
        String value    = fieldValue.getText().trim();

        if (value.isEmpty()) {
            showErrorDialog("Missing Input", "Please enter a value to convert.");
            return;
        }
        if (fromUnit == null || toUnit == null) {
            showErrorDialog("Missing Input", "Please select both From and To units.");
            return;
        }

        Map<String, String> inputs = new LinkedHashMap<>();
        inputs.put("category", category);
        inputs.put("from",     fromUnit);
        inputs.put("to",       toUnit);
        inputs.put("value",    value);

        try {
            CalculatorService svc = ServiceFactory.getInstance().getService(CalculatorType.UNIT_CONVERTER);
            String result = svc.calculate(inputs);
            log.debug("Unit conversion result: {}", result);
            displayResult(result, toUnit);
        } catch (IllegalArgumentException e) {
            log.warn("UNIT_CONVERTER service not registered", e);
            setErrorLabel("Service not available");
        }
    }

    // ── Helpers ────────────────────────────────────────────────────────────

    private void updateUnitLists(String category) {
        List<String> units = UNITS.getOrDefault(category, List.of());
        comboFrom.setItems(FXCollections.observableArrayList(units));
        comboTo.setItems(FXCollections.observableArrayList(units));
        comboFrom.getSelectionModel().selectFirst();
        comboTo.getSelectionModel().select(1);
    }

    private void displayResult(String result, String toUnit) {
        if (result.startsWith("Error:")) {
            setErrorLabel(result.substring("Error:".length()).trim());
        } else {
            labelResult.setText(result + " " + toUnit);
            labelResult.setStyle("-fx-text-fill: #4a90d9; -fx-font-size: 22px; -fx-font-weight: bold;");
        }
    }

    private void setErrorLabel(String message) {
        labelResult.setText("Error: " + message);
        labelResult.setStyle("-fx-text-fill: #ff6b6b; -fx-font-size: 14px;");
    }

    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Input Error");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
