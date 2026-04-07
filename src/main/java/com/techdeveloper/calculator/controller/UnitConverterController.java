package com.techdeveloper.calculator.controller;

import com.techdeveloper.calculator.constants.UnitCategory;
import com.techdeveloper.calculator.controller.helper.UnitConverterHelper;
import com.techdeveloper.calculator.dto.UnitConverterResult;
import com.techdeveloper.calculator.form.UnitConverterForm;
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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Controller for unit-converter.fxml.
 * Populates category and unit ComboBoxes in initialize()/onCategoryChange().
 * Delegates to UnitConverterService using UnitConverterForm.
 */
public class UnitConverterController extends UnitConverterHelper implements Initializable {

    public UnitConverterController() {
        // required for FXML
    }

    private static final Logger log = LoggerFactory.getLogger(UnitConverterController.class);

    @FXML private ComboBox<String> comboCategory;
    @FXML private ComboBox<String> comboFrom;
    @FXML private ComboBox<String> comboTo;
    @FXML private TextField fieldValue;
    @FXML private Label labelResult;

    private CalculatorService<UnitConverterForm, UnitConverterResult> service;

    /** Categories -> display unit names (shown in ComboBox). */
    private static final Map<String, List<String>> UNIT_DISPLAY = new LinkedHashMap<>();

    static {
        UNIT_DISPLAY.put("Length",      List.of("MM", "CM", "M", "KM", "INCH", "FOOT", "YARD", "MILE"));
        UNIT_DISPLAY.put("Weight",      List.of("MG", "G", "KG", "TONNE", "LB", "OZ", "STONE"));
        UNIT_DISPLAY.put("Temperature", List.of("C", "F", "K"));
        UNIT_DISPLAY.put("Volume",      List.of("ML", "L", "CUBICM", "GALLON", "QUART", "PINT", "FLOZ", "CUBICCM"));
        UNIT_DISPLAY.put("Speed",       List.of("MPS", "KPH", "MPH", "KNOT", "FPS"));
        UNIT_DISPLAY.put("Area",        List.of("SQM", "SQKM", "SQFT", "SQMILE", "HECTARE", "ACRE"));
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        service = ServiceFactory.getInstance().getUnitConverterService();
        log.debug("UnitConverterController initialized, service={}", service.getClass().getSimpleName());
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
        String category  = comboCategory.getValue();
        String fromUnit  = comboFrom.getValue();
        String toUnit    = comboTo.getValue();
        String valueText = fieldValue.getText().trim();
        if (!validateConversionInputs(valueText, fromUnit, toUnit)) return;
        try {
            double value = Double.parseDouble(valueText);
            UnitCategory unitCategory = UnitCategory.valueOf(category.toUpperCase());
            executeConversion(value, unitCategory, fromUnit, toUnit, valueText, category);
        } catch (NumberFormatException e) {
            log.warn("UnitConverter controller: invalid value input", e);
            displayResult("Error: Invalid numeric value");
        } catch (IllegalArgumentException e) {
            log.warn("UnitConverter controller: unknown category={}", category, e);
            displayResult("Error: Unknown category");
        }
    }

    private boolean validateConversionInputs(String valueText, String fromUnit, String toUnit) {
        if (valueText.isEmpty()) {
            showErrorDialog("Missing Input", "Please enter a value to convert.");
            return false;
        }
        if (fromUnit == null || toUnit == null) {
            showErrorDialog("Missing Input", "Please select both From and To units.");
            return false;
        }
        return true;
    }

    private void executeConversion(double value, UnitCategory cat, String from, String to,
            String valueText, String category) {
        UnitConverterForm form = new UnitConverterForm(value, from, to, cat);
        UnitConverterResult result = service.calculate(form);
        log.debug("Unit conversion isError={}", result.isError());
        if (result.isError()) {
            displayResult("Error: " + result.errorMessage());
        } else {
            String formatted = formatConversionResult(value, result);
            displayResult(formatted);
            String inputSummary = valueText + " " + from + " -> " + to + " (" + category + ")";
            HistoryService.getInstance().addEntry("Unit", inputSummary, formatted);
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
