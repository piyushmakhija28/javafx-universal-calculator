package com.techdeveloper.calculator.controller;

import com.techdeveloper.calculator.dto.BasicCalculatorResult;
import com.techdeveloper.calculator.form.BasicCalculatorForm;
import com.techdeveloper.calculator.service.CalculatorService;
import com.techdeveloper.calculator.service.HistoryService;
import com.techdeveloper.calculator.service.impl.ServiceFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for basic-calculator.fxml.
 * Maintains a simple expression string and delegates evaluation to CalculatorService.
 * No math logic lives here — all arithmetic is in the service layer.
 */
public class BasicCalculatorController implements Initializable {

    public BasicCalculatorController() {
        // required for FXML
    }

    private static final Logger log = LoggerFactory.getLogger(BasicCalculatorController.class);

    private static final String NORMAL_STYLE = "-fx-text-fill: #e0e0e0;";
    private static final String ERROR_STYLE  = "-fx-text-fill: #ff6b6b;";

    @FXML private TextField display;

    private CalculatorService<BasicCalculatorForm, BasicCalculatorResult> service;

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
        service = ServiceFactory.getInstance().getBasicService();
        log.debug("BasicCalculatorController initialized, service={}", service.getClass().getSimpleName());
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
        display.setStyle(NORMAL_STYLE);
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
        String left  = pendingOperand;
        String op    = pendingOperator;
        String right = currentInput;
        String result = evaluate(left, op, right);
        if (result.startsWith("Error:")) {
            showInlineError(result);
        } else {
            display.setStyle(NORMAL_STYLE);
            display.setText(result);
            currentInput = result;
            // Record history entry on equals press only — not on intermediate digit presses.
            String inputSummary = left + " " + op + " " + right;
            HistoryService.getInstance().addEntry("Basic", inputSummary, result);
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
        display.setStyle(NORMAL_STYLE);
        display.setText("0");
    }

    @FXML
    private void onClearEntry(ActionEvent event) {
        currentInput = "0";
        resultJustShown = false;
        display.setStyle(NORMAL_STYLE);
        display.setText("0");
    }

    @FXML
    private void onPercent(ActionEvent event) {
        String result = callService(currentInput, "/", "100");
        if (!result.startsWith("Error:")) {
            currentInput = result;
            display.setStyle(NORMAL_STYLE);
            display.setText(result);
        } else {
            showInlineError(result);
        }
    }

    @FXML
    private void onNegate(ActionEvent event) {
        String result = callService(currentInput, "+-", null);
        if (!result.startsWith("Error:")) {
            currentInput = result;
            display.setStyle(NORMAL_STYLE);
            display.setText(result);
        } else {
            showInlineError(result);
        }
    }

    @FXML
    private void onSqrt(ActionEvent event) {
        String result = callService(currentInput, "sqrt", null);
        if (!result.startsWith("Error:")) {
            currentInput = result;
            display.setStyle(NORMAL_STYLE);
            display.setText(result);
            resultJustShown = true;
        } else {
            showInlineError(result);
        }
    }

    // ── Helpers ────────────────────────────────────────────────────────────

    /**
     * Build the correct typed form for BasicCalculatorService and call it.
     * Service expects: operand1, operator, operand2 (operand2 may be null for unary ops).
     */
    private String evaluate(String left, String operator, String right) {
        // Normalise display symbols to service tokens
        String opToken = switch (operator) {
            case "+"       -> "+";
            case "−", "-"  -> "-";
            case "×", "*"  -> "*";
            case "÷", "/"  -> "/";
            default        -> operator;
        };
        return callService(left, opToken, right);
    }

    private String callService(String operand1, String operator, String operand2) {
        BasicCalculatorForm form = new BasicCalculatorForm(operand1, operator, operand2);
        BasicCalculatorResult result = service.calculate(form);
        if (result.isError()) {
            log.warn("BasicCalculator service error: {}", result.errorMessage());
            return "Error: " + result.errorMessage();
        }
        return result.formattedResult();
    }

    private void showInlineError(String errorMessage) {
        display.setStyle(ERROR_STYLE);
        display.setText(errorMessage);
        log.warn("BasicCalculator inline error: {}", errorMessage);
    }
}
