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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Controller for basic-calculator.fxml.
 * Maintains a simple expression string and delegates evaluation to CalculatorService.
 * No math logic lives here — all arithmetic is in the service layer.
 */
public class BasicCalculatorController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(BasicCalculatorController.class);

    @FXML private TextField display;

    /** Current operand being typed. */
    private String currentInput = "0";
    /** Pending operand (left side of binary operation). */
    private String pendingOperand = "";
    /** Pending operator (+, -, *, /). */
    private String pendingOperator = "";
    /** True immediately after an equals press — next digit clears display. */
    private boolean resultJustShown = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        display.setText("0");
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
        // If there is a pending operation, evaluate it first
        if (!pendingOperand.isEmpty() && !pendingOperator.isEmpty()) {
            String result = evaluate(pendingOperand, pendingOperator, currentInput);
            if (result.startsWith("Error:")) {
                showInlineError(result);
                pendingOperand = "";
                pendingOperator = "";
                currentInput = "0";
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
        String result = evaluate(pendingOperand, pendingOperator, currentInput);
        if (result.startsWith("Error:")) {
            showInlineError(result);
        } else {
            display.setText(result);
            currentInput = result;
        }
        pendingOperand = "";
        pendingOperator = "";
        resultJustShown = true;
    }

    // ── Special operations ─────────────────────────────────────────────────

    @FXML
    private void onClear(ActionEvent event) {
        currentInput = "0";
        pendingOperand = "";
        pendingOperator = "";
        resultJustShown = false;
        display.setText("0");
    }

    @FXML
    private void onClearEntry(ActionEvent event) {
        currentInput = "0";
        resultJustShown = false;
        display.setText("0");
    }

    @FXML
    private void onPercent(ActionEvent event) {
        Map<String, String> inputs = new LinkedHashMap<>();
        inputs.put("expression", currentInput + "%");
        String result = callService(CalculatorType.BASIC, inputs);
        if (!result.startsWith("Error:")) {
            currentInput = result;
            display.setText(result);
        } else {
            showInlineError(result);
        }
    }

    @FXML
    private void onNegate(ActionEvent event) {
        Map<String, String> inputs = new LinkedHashMap<>();
        inputs.put("expression", "negate(" + currentInput + ")");
        String result = callService(CalculatorType.BASIC, inputs);
        if (!result.startsWith("Error:")) {
            currentInput = result;
            display.setText(result);
        } else {
            showInlineError(result);
        }
    }

    @FXML
    private void onSqrt(ActionEvent event) {
        Map<String, String> inputs = new LinkedHashMap<>();
        inputs.put("expression", "sqrt(" + currentInput + ")");
        String result = callService(CalculatorType.BASIC, inputs);
        if (!result.startsWith("Error:")) {
            currentInput = result;
            display.setText(result);
            resultJustShown = true;
        } else {
            showInlineError(result);
        }
    }

    // ── Helpers ────────────────────────────────────────────────────────────

    /**
     * Build an expression string and forward to the BASIC service.
     * No arithmetic logic here — purely constructs the input map.
     */
    private String evaluate(String left, String operator, String right) {
        // Map operator symbols to expression form the service understands
        String opToken = switch (operator) {
            case "+" -> "+";
            case "−" -> "-";
            case "×" -> "*";
            case "÷" -> "/";
            default  -> operator;
        };
        Map<String, String> inputs = new LinkedHashMap<>();
        inputs.put("expression", left + opToken + right);
        return callService(CalculatorType.BASIC, inputs);
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
        log.warn("BasicCalculator inline error: {}", errorMessage);
    }

    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Calculation Error");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
