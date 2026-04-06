package com.techdeveloper.calculator.controller;

import com.techdeveloper.calculator.service.CalculatorService;
import com.techdeveloper.calculator.service.CalculatorType;
import com.techdeveloper.calculator.service.ServiceFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Controller for scientific-calculator.fxml.
 * Handles DEG/RAD toggle, scientific function buttons, and basic arithmetic.
 * All calculations are delegated to the SCIENTIFIC service — no math here.
 */
public class ScientificCalculatorController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(ScientificCalculatorController.class);

    @FXML private TextField display;
    @FXML private ToggleButton btnDeg;
    @FXML private ToggleButton btnRad;

    private String currentInput = "0";
    private String pendingOperand = "";
    private String pendingOperator = "";
    private boolean resultJustShown = false;
    private boolean isRadians = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        display.setText("0");
    }

    // ── Angle mode ─────────────────────────────────────────────────────────

    @FXML
    private void onAngleModeChange(ActionEvent event) {
        isRadians = btnRad != null && btnRad.isSelected();
        log.debug("Angle mode changed to {}", isRadians ? "RAD" : "DEG");
    }

    // ── Digit entry ────────────────────────────────────────────────────────

    @FXML
    private void onDigit(ActionEvent event) {
        String digit = ((Button) event.getSource()).getText();
        if (resultJustShown) {
            currentInput = digit;
            resultJustShown = false;
        } else {
            currentInput = "0".equals(currentInput) ? digit : currentInput + digit;
        }
        display.setText(currentInput);
    }

    // ── Operator ───────────────────────────────────────────────────────────

    @FXML
    private void onOperator(ActionEvent event) {
        String op = ((Button) event.getSource()).getText();
        if (!pendingOperand.isEmpty() && !pendingOperator.isEmpty()) {
            String result = evaluateBinary(pendingOperand, pendingOperator, currentInput);
            if (result.startsWith("Error:")) {
                showInlineError(result);
                reset();
                return;
            }
            currentInput = result;
            display.setText(currentInput);
        }
        pendingOperand = currentInput;
        pendingOperator = op;
        resultJustShown = true;
    }

    @FXML
    private void onEquals(ActionEvent event) {
        if (pendingOperand.isEmpty() || pendingOperator.isEmpty()) return;
        String result = evaluateBinary(pendingOperand, pendingOperator, currentInput);
        applyResult(result);
        pendingOperand = "";
        pendingOperator = "";
        resultJustShown = true;
    }

    @FXML
    private void onParenthesis(ActionEvent event) {
        String paren = ((Button) event.getSource()).getText();
        currentInput = currentInput + paren;
        display.setText(currentInput);
    }

    // ── Scientific functions ───────────────────────────────────────────────

    @FXML
    private void onScientific(ActionEvent event) {
        String func = ((Button) event.getSource()).getText();
        Map<String, String> inputs = new LinkedHashMap<>();
        inputs.put("function", func);
        inputs.put("operand", currentInput);
        inputs.put("angleMode", isRadians ? "RAD" : "DEG");
        String result = callService(CalculatorType.SCIENTIFIC, inputs);
        applyResult(result);
        resultJustShown = true;
    }

    @FXML
    private void onConstant(ActionEvent event) {
        String constant = ((Button) event.getSource()).getText();
        Map<String, String> inputs = new LinkedHashMap<>();
        inputs.put("constant", constant);
        String result = callService(CalculatorType.SCIENTIFIC, inputs);
        if (!result.startsWith("Error:")) {
            currentInput = result;
            display.setText(result);
        }
    }

    // ── Clear ──────────────────────────────────────────────────────────────

    @FXML
    private void onClear(ActionEvent event) {
        reset();
        display.setText("0");
    }

    @FXML
    private void onClearEntry(ActionEvent event) {
        currentInput = "0";
        display.setText("0");
    }

    // ── Helpers ────────────────────────────────────────────────────────────

    private String evaluateBinary(String left, String operator, String right) {
        String opToken = switch (operator) {
            case "+" -> "+";
            case "−" -> "-";
            case "×" -> "*";
            case "÷" -> "/";
            default  -> operator;
        };
        Map<String, String> inputs = new LinkedHashMap<>();
        inputs.put("expression", left + opToken + right);
        inputs.put("angleMode", isRadians ? "RAD" : "DEG");
        return callService(CalculatorType.SCIENTIFIC, inputs);
    }

    private void applyResult(String result) {
        if (result.startsWith("Error:")) {
            showInlineError(result);
        } else {
            currentInput = result;
            display.setText(result);
        }
    }

    private void reset() {
        currentInput = "0";
        pendingOperand = "";
        pendingOperator = "";
        resultJustShown = false;
    }

    private String callService(CalculatorType type, Map<String, String> inputs) {
        try {
            CalculatorService svc = ServiceFactory.getInstance().getService(type);
            return svc.calculate(inputs);
        } catch (IllegalArgumentException e) {
            log.warn("Service not registered for type={}", type, e);
            return "Error: Service not available";
        }
    }

    private void showInlineError(String errorMessage) {
        display.setStyle(display.getStyle() + "; -fx-text-fill: #ff6b6b;");
        display.setText(errorMessage);
        log.warn("ScientificCalculator inline error: {}", errorMessage);
    }
}
