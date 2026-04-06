package com.techdeveloper.calculator.controller;

import com.techdeveloper.calculator.service.CalculatorService;
import com.techdeveloper.calculator.service.CalculatorType;
import com.techdeveloper.calculator.service.HistoryService;
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
 * Delegates to ProgrammerCalculatorService using keys:
 *   "value", "operation", "operand2", "inputBase", "outputBase"
 */
public class ProgrammerCalculatorController implements Initializable {

    public ProgrammerCalculatorController() {
        // required for FXML
    }

    private static final Logger log = LoggerFactory.getLogger(ProgrammerCalculatorController.class);

    private static final String NORMAL_STYLE = "-fx-text-fill: #e0e0e0;";
    private static final String ERROR_STYLE  = "-fx-text-fill: #ff6b6b;";

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
        CalculatorService svc = ServiceFactory.getInstance().getService(CalculatorType.PROGRAMMER);
        log.debug("ProgrammerCalculatorController initialized, service={}", svc.getClass().getSimpleName());
        display.setText("0");
        updateHexButtonState();
    }

    // ── Base change ────────────────────────────────────────────────────────

    @FXML
    private void onBaseChange(ActionEvent event) {
        String previousBase = currentBase;
        if (rbHex != null && rbHex.isSelected()) currentBase = "HEX";
        else if (rbDec != null && rbDec.isSelected()) currentBase = "DEC";
        else if (rbOct != null && rbOct.isSelected()) currentBase = "OCT";
        else currentBase = "BIN";

        // Convert current displayed value from previous base to new base
        Map<String, String> inputs = new LinkedHashMap<>();
        inputs.put("value",      currentInput);
        inputs.put("operation",  "CONVERT");
        inputs.put("inputBase",  previousBase);
        inputs.put("outputBase", currentBase);
        String result = callService(inputs);
        if (!result.startsWith("Error:")) {
            // Service returns "BASE: value" — extract the value part after ": "
            currentInput = extractValue(result);
            display.setStyle(NORMAL_STYLE);
            display.setText(currentInput);
            updateBitDisplay();
        }
        updateHexButtonState();
        log.debug("Base changed from {} to {}", previousBase, currentBase);
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
        String result = evaluateBinary(left, op, right);
        applyResult(result);
        if (!result.startsWith("Error:")) {
            String inputSummary = left + " " + op + " " + right + " (" + currentBase + ")";
            HistoryService.getInstance().addEntry("Programmer", inputSummary, result);
        }
        pendingOperand = "";
        pendingOperator = "";
        resultJustShown = true;
    }

    // ── Bitwise operations ─────────────────────────────────────────────────

    @FXML
    private void onBitwise(ActionEvent event) {
        String op = ((Button) event.getSource()).getText().toUpperCase();
        if ("NOT".equals(op)) {
            Map<String, String> inputs = new LinkedHashMap<>();
            inputs.put("value",      currentInput);
            inputs.put("operation",  "NOT");
            inputs.put("inputBase",  currentBase);
            inputs.put("outputBase", currentBase);
            String result = callService(inputs);
            applyResult(result);
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
        display.setStyle(NORMAL_STYLE);
        display.setText("0");
        if (bitDisplay != null) bitDisplay.setText("0000 0000 0000 0000");
    }

    @FXML
    private void onClearEntry(ActionEvent event) {
        currentInput = "0";
        display.setStyle(NORMAL_STYLE);
        display.setText("0");
    }

    // ── Helpers ────────────────────────────────────────────────────────────

    /**
     * Evaluate a binary operation on the programmer calculator.
     * Bitwise ops (AND/OR/XOR/SHL/SHR/MOD) → ProgrammerCalculatorService.
     * Arithmetic ops (+/−/×/÷) → BasicCalculatorService (after converting to DEC).
     */
    private String evaluateBinary(String left, String operator, String right) {
        switch (operator.toUpperCase()) {
            case "AND", "OR", "XOR", "SHL", "SHR", "MOD" -> {
                Map<String, String> inputs = new LinkedHashMap<>();
                inputs.put("value",      left);
                inputs.put("operation",  operator.toUpperCase());
                inputs.put("operand2",   right);
                inputs.put("inputBase",  currentBase);
                inputs.put("outputBase", currentBase);
                String result = callService(inputs);
                return result.startsWith("Error:") ? result : extractValue(result);
            }
            default -> {
                // Arithmetic: map display symbols to basic-service tokens, operate in DEC
                String opToken = switch (operator) {
                    case "+"       -> "+";
                    case "−", "-"  -> "-";
                    case "×", "*"  -> "*";
                    case "÷", "/"  -> "/";
                    default        -> operator;
                };
                // Convert operands to DEC first if not already
                String decLeft  = toDecimal(left);
                String decRight = toDecimal(right);
                Map<String, String> inputs = new LinkedHashMap<>();
                inputs.put("operand1", decLeft);
                inputs.put("operator", opToken);
                inputs.put("operand2", decRight);
                try {
                    CalculatorService basic = ServiceFactory.getInstance().getService(CalculatorType.BASIC);
                    String decResult = basic.calculate(inputs);
                    if (decResult.startsWith("Error:")) return decResult;
                    // Convert result back to currentBase
                    Map<String, String> conv = new LinkedHashMap<>();
                    conv.put("value",      decResult);
                    conv.put("operation",  "CONVERT");
                    conv.put("inputBase",  "DEC");
                    conv.put("outputBase", currentBase);
                    String converted = callService(conv);
                    return converted.startsWith("Error:") ? converted : extractValue(converted);
                } catch (IllegalArgumentException e) {
                    log.warn("BASIC service unavailable for arithmetic in Programmer controller", e);
                    return "Error: Service not available";
                }
            }
        }
    }

    /** Convert a value from currentBase to DEC string for arithmetic ops. */
    private String toDecimal(String value) {
        if ("DEC".equals(currentBase)) return value;
        Map<String, String> inputs = new LinkedHashMap<>();
        inputs.put("value",      value);
        inputs.put("operation",  "CONVERT");
        inputs.put("inputBase",  currentBase);
        inputs.put("outputBase", "DEC");
        String result = callService(inputs);
        return result.startsWith("Error:") ? value : extractValue(result);
    }

    private void applyResult(String result) {
        if (result.startsWith("Error:")) {
            showInlineError(result);
        } else {
            // Service may return "BASE: value" format — extract plain value
            String value = extractValue(result);
            currentInput = value;
            display.setStyle(NORMAL_STYLE);
            display.setText(currentInput);
            updateBitDisplay();
        }
    }

    private void updateBitDisplay() {
        if (bitDisplay == null) return;
        // Convert current value to BIN to display the bit pattern
        Map<String, String> inputs = new LinkedHashMap<>();
        inputs.put("value",      currentInput);
        inputs.put("operation",  "CONVERT");
        inputs.put("inputBase",  currentBase);
        inputs.put("outputBase", "BIN");
        String bits = callService(inputs);
        if (!bits.startsWith("Error:")) {
            String binValue = extractValue(bits);
            // Pad to 16 bits and insert spaces every 4 bits for readability
            String padded = String.format("%16s", binValue).replace(' ', '0');
            String formatted = padded.substring(0, 4) + " " + padded.substring(4, 8) + " "
                             + padded.substring(8, 12) + " " + padded.substring(12, 16);
            bitDisplay.setText(formatted);
        } else {
            bitDisplay.setText("0000 0000 0000 0000");
        }
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

    /**
     * Service returns "BASE: value" (e.g., "DEC: 42", "HEX: 2A").
     * Extract only the value part after ": ".
     */
    private String extractValue(String serviceResult) {
        int colonIdx = serviceResult.indexOf(": ");
        if (colonIdx >= 0 && colonIdx + 2 < serviceResult.length()) {
            return serviceResult.substring(colonIdx + 2).trim();
        }
        return serviceResult;
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
        display.setStyle(ERROR_STYLE);
        display.setText(errorMessage);
        log.warn("ProgrammerCalculator inline error: {}", errorMessage);
    }
}
