package com.techdeveloper.calculator.controller;

import com.techdeveloper.calculator.service.CalculatorService;
import com.techdeveloper.calculator.service.CalculatorType;
import com.techdeveloper.calculator.service.HistoryService;
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
 * Delegates to UnitConverterService using keys: "value", "fromUnit", "toUnit", "category".
 * Unit display names are mapped to the service's expected codes before calling calculate().
 */
public class UnitConverterController implements Initializable {

    public UnitConverterController() {
        // required for FXML
    }

    private static final Logger log = LoggerFactory.getLogger(UnitConverterController.class);

    @FXML private ComboBox<String> comboCategory;
    @FXML private ComboBox<String> comboFrom;
    @FXML private ComboBox<String> comboTo;
    @FXML private TextField fieldValue;
    @FXML private Label labelResult;

    /** Categories -> display unit names (shown in ComboBox). */
    private static final Map<String, List<String>> UNIT_DISPLAY = new LinkedHashMap<>();

    /**
     * Mapping: display name -> service code (as expected by UnitConverterService).
     * Keys must match exactly what UnitConverterService uses in its static maps.
     */
    private static final Map<String, String> DISPLAY_TO_CODE = new LinkedHashMap<>();

    static {
        UNIT_DISPLAY.put("Length",      List.of("MM", "CM", "M", "KM", "INCH", "FOOT", "YARD", "MILE"));
        UNIT_DISPLAY.put("Weight",      List.of("MG", "G", "KG", "TONNE", "LB", "OZ", "STONE"));
        UNIT_DISPLAY.put("Temperature", List.of("C", "F", "K"));
        UNIT_DISPLAY.put("Volume",      List.of("ML", "L", "CUBICM", "GALLON", "QUART", "PINT", "FLOZ", "CUBICCM"));
        UNIT_DISPLAY.put("Speed",       List.of("MPS", "KPH", "MPH", "KNOT", "FPS"));
        UNIT_DISPLAY.put("Area",        List.of("SQM", "SQKM", "SQFT", "SQMILE", "HECTARE", "ACRE"));

        // For this controller, display codes ARE the service codes — no translation needed.
        // Codes are passed directly to the service.
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        CalculatorService svc = ServiceFactory.getInstance().getService(CalculatorType.UNIT_CONVERTER);
        log.debug("UnitConverterController initialized, service={}", svc.getClass().getSimpleName());
        comboCategory.setItems(FXCollections.observableArrayList(UNIT_DISPLAY.keySet()));
        comboCategory.getSelectionModel().selectFirst();
        updateUnitLists("Length");
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

        // category must be uppercase for the service
        String serviceCategory = category.toUpperCase();

        Map<String, String> inputs = new LinkedHashMap<>();
        inputs.put("value",    value);
        inputs.put("fromUnit", fromUnit);
        inputs.put("toUnit",   toUnit);
        inputs.put("category", serviceCategory);

        try {
            CalculatorService svc = ServiceFactory.getInstance().getService(CalculatorType.UNIT_CONVERTER);
            String result = svc.calculate(inputs);
            log.debug("Unit conversion result: {}", result);
            displayResult(result);
            if (!result.startsWith("Error:")) {
                String inputSummary = value + " " + fromUnit + " -> " + toUnit + " (" + category + ")";
                HistoryService.getInstance().addEntry("Unit", inputSummary, result);
            }
        } catch (IllegalArgumentException e) {
            log.warn("UNIT_CONVERTER service not registered", e);
            displayResult("Error: Service not available");
        }
    }

    // ── Helpers ────────────────────────────────────────────────────────────

    private void updateUnitLists(String category) {
        List<String> units = UNIT_DISPLAY.getOrDefault(category, List.of());
        comboFrom.setItems(FXCollections.observableArrayList(units));
        comboTo.setItems(FXCollections.observableArrayList(units));
        comboFrom.getSelectionModel().selectFirst();
        if (units.size() > 1) comboTo.getSelectionModel().select(1);
    }

    private void displayResult(String result) {
        if (result.startsWith("Error:")) {
            labelResult.setText(result);
            labelResult.setStyle("-fx-text-fill: #ff6b6b; -fx-font-size: 14px;");
        } else {
            labelResult.setText(result);
            labelResult.setStyle("-fx-text-fill: #4a90d9; -fx-font-size: 18px; -fx-font-weight: bold;");
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
