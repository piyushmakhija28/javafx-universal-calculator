package com.techdeveloper.calculator.controller;

import com.techdeveloper.calculator.service.CalculatorService;
import com.techdeveloper.calculator.service.CalculatorType;
import com.techdeveloper.calculator.service.ServiceFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Controller for scientific-calculator.fxml.
 * Handles DEG/RAD toggle, scientific function buttons, and basic arithmetic.
 * Delegates to ScientificCalculatorService using keys: "value", "operation", "mode".
 */
public class ScientificCalculatorController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(ScientificCalculatorController.class);

    private static final String NORMAL_STYLE = "-fx-text-fill: #e0e0e0;";
    private static final String ERROR_STYLE  = "-fx-text-fill: #ff6b6b;";

    /** Maps FXML button display text → service operation token. */
    private static final Map<String, String> BUTTON_TO_OP = Map.of(
        "x²",  "square",
        "x^y", "power",
        "n!",  "factorial",
        "e^x", "exp",
        "√",   "sqrt",
        "log", "log",
        "ln",  "ln"
    );

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
        CalculatorService svc = ServiceFactory.getInstance().getService(CalculatorType.SCIENTIFIC);
        log.debug("ScientificCalculatorController initialized, service={}", svc.getClass().getSimpleName());
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
        display.setStyle(NORMAL_STYLE);
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
            display.setStyle(NORMAL_STYLE);
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
    // Button text must match the operation name understood by ScientificCalculatorService:
    // sin, cos, tan, asin, acos, atan, log, ln, sqrt, square, cube, factorial

    @FXML
    private void onScientific(ActionEvent event) {
        String btnText = ((Button) event.getSource()).getText().trim();
        String func = BUTTON_TO_OP.getOrDefault(btnText, btnText.toLowerCase());
        Map<String, String> inputs = new LinkedHashMap<>();
        inputs.put("value",     currentInput);
        inputs.put("operation", func);
        inputs.put("mode",      isRadians ? "RAD" : "DEG");
        String result = callService(inputs);
        applyResult(result);
        resultJustShown = true;
    }

    @FXML
    private void onConstant(ActionEvent event) {
        String constant = ((Button) event.getSource()).getText().toLowerCase().trim();
        // "pi" and "e" are handled by the service without a value input
        Map<String, String> inputs = new LinkedHashMap<>();
        inputs.put("operation", constant);
        String result = callService(inputs);
        if (!result.startsWith("Error:")) {
            currentInput = result;
            display.setStyle(NORMAL_STYLE);
            display.setText(result);
        } else {
            showInlineError(result);
        }
    }

    // ── Clear ──────────────────────────────────────────────────────────────

    @FXML
    private void onClear(ActionEvent event) {
        reset();
        display.setStyle(NORMAL_STYLE);
        display.setText("0");
    }

    @FXML
    private void onClearEntry(ActionEvent event) {
        currentInput = "0";
        display.setStyle(NORMAL_STYLE);
        display.setText("0");
    }

    // ── Helpers ────────────────────────────────────────────────────────────

    /**
     * For basic binary arithmetic (+, -, *, /) the scientific service does not directly
     * expose a binary op — delegate those to the BASIC service via the proper keys.
     */
    private String evaluateBinary(String left, String operator, String right) {
        String opToken = switch (operator) {
            case "+", "+" -> "+";
            case "−", "-" -> "-";
            case "×", "*" -> "*";
            case "÷", "/" -> "/";
            default       -> operator;
        };
        Map<String, String> inputs = new LinkedHashMap<>();
        inputs.put("operand1", left);
        inputs.put("operator", opToken);
        inputs.put("operand2", right);
        try {
            CalculatorService basic = ServiceFactory.getInstance().getService(CalculatorType.BASIC);
            return basic.calculate(inputs);
        } catch (IllegalArgumentException e) {
            log.warn("BASIC service not registered for binary eval in Scientific controller", e);
            return "Error: Service not available";
        }
    }

    private void applyResult(String result) {
        if (result.startsWith("Error:")) {
            showInlineError(result);
        } else {
            currentInput = result;
            display.setStyle(NORMAL_STYLE);
            display.setText(result);
        }
    }

    private void reset() {
        currentInput = "0";
        pendingOperand = "";
        pendingOperator = "";
        resultJustShown = false;
    }

    private String callService(Map<String, String> inputs) {
        try {
            CalculatorService svc = ServiceFactory.getInstance().getService(CalculatorType.SCIENTIFIC);
            return svc.calculate(inputs);
        } catch (IllegalArgumentException e) {
            log.warn("SCIENTIFIC service not registered", e);
            return "Error: Service not available";
        }
    }

    private void showInlineError(String errorMessage) {
        display.setStyle(ERROR_STYLE);
        display.setText(errorMessage);
        log.warn("ScientificCalculator inline error: {}", errorMessage);
    }
}
