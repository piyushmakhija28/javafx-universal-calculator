package com.techdeveloper.calculator.controller;

import com.techdeveloper.calculator.service.CalculatorService;
import com.techdeveloper.calculator.service.CalculatorType;
import com.techdeveloper.calculator.service.ServiceFactory;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Controller for programmer-calculator.fxml.
 * Handles base toggle (HEX/DEC/OCT/BIN), bitwise operations, and display.
 * No conversion logic resides here — all delegated to PROGRAMMER service.
 */
public class ProgrammerCalculatorController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(ProgrammerCalculatorController.class);

    @FXML private TextField display;
    @FXML private Label bitDisplay;
    @FXML private RadioButton rbHex;
    @FXML private RadioButton rbDec;
    @FXML private RadioButton rbOct;
    @FXML private RadioButton rbBin;
    @FXML private Button btnA;
    @FXML private Button btnB;
    @FXML private Button btnC;
    @FXML private Button btnD;
    @FXML private Button btnE;
    @FXML private Button btnF;

    private String currentInput = "0";
    private String pendingOperand = "";
    private String pendingOperator = "";
    private boolean resultJustShown = false;
    private String currentBase = "DEC";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        display.setText("0");
        updateHexButtonState();
    }

    // ── Base change ────────────────────────────────────────────────────────

    @FXML
    private void onBaseChange(ActionEvent event) {
        if (rbHex.isSelected()) currentBase = "HEX";
        else if (rbDec.isSelected()) currentBase = "DEC";
        else if (rbOct.isSelected()) currentBase = "OCT";
        else currentBase = "BIN";

        // Request service to convert the current display value to the new base
        Map<String, String> inputs = new LinkedHashMap<>();
        inputs.put("operation", "CONVERT");
        inputs.put("value", currentInput);
        inputs.put("fromBase", "DEC");
        inputs.put("toBase", currentBase);
        String result = callService(inputs);
        if (!result.startsWith("Error:")) {
            currentInput = result;
            display.setText(result);
            updateBitDisplay();
        }
        updateHexButtonState();
        log.debug("Base changed to {}", currentBase);
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
        updateBitDisplay();
    }

    @FXML
    private void onHexDigit(ActionEvent event) {
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

    // ── Bitwise operations ─────────────────────────────────────────────────

    @FXML
    private void onBitwise(ActionEvent event) {
        String op = ((Button) event.getSource()).getText();
        if ("NOT".equals(op)) {
            Map<String, String> inputs = new LinkedHashMap<>();
            inputs.put("operation", "NOT");
            inputs.put("operand", currentInput);
            inputs.put("base", currentBase);
            applyResult(callService(inputs));
        } else {
            // Binary bitwise: AND, OR, XOR, SHL, SHR, MOD — store as pending operator
            pendingOperand = currentInput;
            pendingOperator = op;
            resultJustShown = true;
        }
    }

    // ── Clear ──────────────────────────────────────────────────────────────

    @FXML
    private void onClear(ActionEvent event) {
        currentInput = "0";
        pendingOperand = "";
        pendingOperator = "";
        resultJustShown = false;
        display.setText("0");
        if (bitDisplay != null) bitDisplay.setText("0000 0000 0000 0000");
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
        inputs.put("base", currentBase);
        return callService(inputs);
    }

    private void applyResult(String result) {
        if (result.startsWith("Error:")) {
            showInlineError(result);
        } else {
            currentInput = result;
            display.setText(result);
            updateBitDisplay();
        }
    }

    private void updateBitDisplay() {
        if (bitDisplay == null) return;
        Map<String, String> inputs = new LinkedHashMap<>();
        inputs.put("operation", "TO_BINARY_DISPLAY");
        inputs.put("value", currentInput);
        inputs.put("base", currentBase);
        String bits = callService(inputs);
        bitDisplay.setText(bits.startsWith("Error:") ? "0000 0000 0000 0000" : bits);
    }

    private void updateHexButtonState() {
        boolean hexEnabled = "HEX".equals(currentBase);
        if (btnA != null) btnA.setDisable(!hexEnabled);
        if (btnB != null) btnB.setDisable(!hexEnabled);
        if (btnC != null) btnC.setDisable(!hexEnabled);
        if (btnD != null) btnD.setDisable(!hexEnabled);
        if (btnE != null) btnE.setDisable(!hexEnabled);
        if (btnF != null) btnF.setDisable(!hexEnabled);
    }

    private String callService(Map<String, String> inputs) {
        try {
            CalculatorService svc = ServiceFactory.getInstance().getService(CalculatorType.PROGRAMMER);
            return svc.calculate(inputs);
        } catch (IllegalArgumentException e) {
            log.warn("PROGRAMMER service not registered", e);
            return "Error: Service not available";
        }
    }

    private void showInlineError(String errorMessage) {
        display.setStyle(display.getStyle() + "; -fx-text-fill: #ff6b6b;");
        display.setText(errorMessage);
        log.warn("ProgrammerCalculator inline error: {}", errorMessage);
    }
}
