package com.techdeveloper.calculator.controller;

import com.techdeveloper.calculator.constants.NumberBase;
import com.techdeveloper.calculator.dto.BasicCalculatorResult;
import com.techdeveloper.calculator.dto.ProgrammerCalculatorResult;
import com.techdeveloper.calculator.form.BasicCalculatorForm;
import com.techdeveloper.calculator.form.ProgrammerCalculatorForm;
import com.techdeveloper.calculator.service.CalculatorService;
import com.techdeveloper.calculator.service.HistoryService;
import com.techdeveloper.calculator.service.impl.ServiceFactory;
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
import java.util.ResourceBundle;

/**
 * Controller for programmer-calculator.fxml.
 * Handles base toggle (HEX/DEC/OCT/BIN), bitwise operations, and display.
 * Delegates to ProgrammerCalculatorService using ProgrammerCalculatorForm.
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

    private CalculatorService<ProgrammerCalculatorForm, ProgrammerCalculatorResult> service;
    private CalculatorService<BasicCalculatorForm, BasicCalculatorResult> basicService;

    private String currentInput = "0";
    private String pendingOperand = "";
    private String pendingOperator = "";
    private boolean resultJustShown = false;
    private NumberBase currentBase = NumberBase.DEC;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        service = ServiceFactory.getInstance().getProgrammerService();
        basicService = ServiceFactory.getInstance().getBasicService();
        log.debug("ProgrammerCalculatorController initialized, service={}", service.getClass().getSimpleName());
        display.setText("0");
        updateHexButtonState();
    }

    // ── Base change ────────────────────────────────────────────────────────

    @FXML
    private void onBaseChange(ActionEvent event) {
        NumberBase previousBase = currentBase;
        if (rbHex != null && rbHex.isSelected()) currentBase = NumberBase.HEX;
        else if (rbDec != null && rbDec.isSelected()) currentBase = NumberBase.DEC;
        else if (rbOct != null && rbOct.isSelected()) currentBase = NumberBase.OCT;
        else currentBase = NumberBase.BIN;

        // Convert current displayed value from previous base to new base
        ProgrammerCalculatorResult result = callService(currentInput, "CONVERT", null, previousBase, currentBase);
        if (!result.isError()) {
            currentInput = result.value();
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
            ProgrammerCalculatorResult result = callService(currentInput, "NOT", null, currentBase, currentBase);
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
     * Bitwise ops (AND/OR/XOR/SHL/SHR/MOD) -> ProgrammerCalculatorService.
     * Arithmetic ops (+/-/x//) -> BasicCalculatorService (after converting to DEC).
     */
    private String evaluateBinary(String left, String operator, String right) {
        switch (operator.toUpperCase()) {
            case "AND", "OR", "XOR", "SHL", "SHR", "MOD" -> {
                ProgrammerCalculatorResult result = callService(left, operator.toUpperCase(), right, currentBase, currentBase);
                return result.isError() ? "Error: " + result.errorMessage() : result.value();
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
                BasicCalculatorForm basicForm = new BasicCalculatorForm(decLeft, opToken, decRight);
                BasicCalculatorResult basicResult = basicService.calculate(basicForm);
                if (basicResult.isError()) {
                    log.warn("BASIC service error in Programmer evaluateBinary: {}", basicResult.errorMessage());
                    return "Error: " + basicResult.errorMessage();
                }
                String decResult = basicResult.formattedResult();
                // Convert result back to currentBase
                ProgrammerCalculatorResult converted = callService(decResult, "CONVERT", null, NumberBase.DEC, currentBase);
                return converted.isError() ? "Error: " + converted.errorMessage() : converted.value();
            }
        }
    }

    /** Convert a value from currentBase to DEC string for arithmetic ops. */
    private String toDecimal(String value) {
        if (NumberBase.DEC.equals(currentBase)) return value;
        ProgrammerCalculatorResult result = callService(value, "CONVERT", null, currentBase, NumberBase.DEC);
        return result.isError() ? value : result.value();
    }

    private void applyResult(String result) {
        if (result.startsWith("Error:")) {
            showInlineError(result);
        } else {
            currentInput = result;
            display.setStyle(NORMAL_STYLE);
            display.setText(currentInput);
            updateBitDisplay();
        }
    }

    private void applyResult(ProgrammerCalculatorResult result) {
        if (result.isError()) {
            showInlineError("Error: " + result.errorMessage());
        } else {
            currentInput = result.value();
            display.setStyle(NORMAL_STYLE);
            display.setText(currentInput);
            updateBitDisplay();
        }
    }

    private void updateBitDisplay() {
        if (bitDisplay == null) return;
        // Convert current value to BIN to display the bit pattern
        ProgrammerCalculatorResult bits = callService(currentInput, "CONVERT", null, currentBase, NumberBase.BIN);
        if (!bits.isError()) {
            String binValue = bits.value();
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
        boolean hexEnabled = NumberBase.HEX.equals(currentBase);
        if (btnA != null) btnA.setDisable(!hexEnabled);
        if (btnB != null) btnB.setDisable(!hexEnabled);
        if (btnC != null) btnC.setDisable(!hexEnabled);
        if (btnD != null) btnD.setDisable(!hexEnabled);
        if (btnE != null) btnE.setDisable(!hexEnabled);
        if (btnF != null) btnF.setDisable(!hexEnabled);
    }

    private ProgrammerCalculatorResult callService(String value, String operation, String operand2,
                                                    NumberBase inputBase, NumberBase outputBase) {
        ProgrammerCalculatorForm form = new ProgrammerCalculatorForm(value, operation, operand2, inputBase, outputBase);
        ProgrammerCalculatorResult result = service.calculate(form);
        if (result.isError()) {
            log.warn("PROGRAMMER service error: {}", result.errorMessage());
        }
        return result;
    }

    private void showInlineError(String errorMessage) {
        display.setStyle(ERROR_STYLE);
        display.setText(errorMessage);
        log.warn("ProgrammerCalculator inline error: {}", errorMessage);
    }
}
