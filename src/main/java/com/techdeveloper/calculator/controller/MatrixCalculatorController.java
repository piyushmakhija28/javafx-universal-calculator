package com.techdeveloper.calculator.controller;

import com.techdeveloper.calculator.constants.MatrixOperation;
import com.techdeveloper.calculator.controller.helper.MatrixCalculatorHelper;
import com.techdeveloper.calculator.dto.MatrixCalculatorResult;
import com.techdeveloper.calculator.form.MatrixCalculatorForm;
import com.techdeveloper.calculator.service.CalculatorService;
import com.techdeveloper.calculator.service.HistoryService;
import com.techdeveloper.calculator.service.impl.ServiceFactory;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for matrix-calculator.fxml.
 * Matrix operations run on a background Task to avoid blocking the FX Application Thread.
 * Platform.runLater() is used to push results back to the UI after Task completion.
 *
 * Service form: MatrixCalculatorForm(operation, matrixA, matrixB, size).
 * Button display text is mapped to MatrixOperation enum values.
 */
public class MatrixCalculatorController extends MatrixCalculatorHelper implements Initializable {

    public MatrixCalculatorController() {
        // required for FXML
    }

    private static final Logger log = LoggerFactory.getLogger(MatrixCalculatorController.class);

    private static final String NORMAL_STYLE = "-fx-background-color: #1a1a1a; -fx-text-fill: #ffffff;";
    private static final String ERROR_STYLE  = "-fx-background-color: #1a1a1a; -fx-text-fill: #ff6b6b;";

    // ── Matrix A TextFields ────────────────────────────────────────────────
    @FXML private TextField a00; @FXML private TextField a01; @FXML private TextField a02;
    @FXML private TextField a10; @FXML private TextField a11; @FXML private TextField a12;
    @FXML private TextField a20; @FXML private TextField a21; @FXML private TextField a22;

    // ── Matrix B TextFields ────────────────────────────────────────────────
    @FXML private TextField b00; @FXML private TextField b01; @FXML private TextField b02;
    @FXML private TextField b10; @FXML private TextField b11; @FXML private TextField b12;
    @FXML private TextField b20; @FXML private TextField b21; @FXML private TextField b22;

    @FXML private RadioButton rb2x2;
    @FXML private RadioButton rb3x3;
    @FXML private TextArea resultArea;

    private CalculatorService<MatrixCalculatorForm, MatrixCalculatorResult> service;

    private int matrixSize = 2;

    /** Reference to the currently running Task — cancelled when a new operation starts. */
    private Task<MatrixCalculatorResult> currentTask;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        service = ServiceFactory.getInstance().getMatrixService();
        log.debug("MatrixCalculatorController initialized, service={}", service.getClass().getSimpleName());
        // 3rd row/column fields start hidden (2x2 default)
        setThirdRowVisible(false);
    }

    @FXML
    private void onSizeChange(ActionEvent event) {
        matrixSize = (rb3x3 != null && rb3x3.isSelected()) ? 3 : 2;
        setThirdRowVisible(matrixSize == 3);
        resultArea.clear();
        log.debug("Matrix size changed to {}x{}", matrixSize, matrixSize);
    }

    @FXML
    private void onOperation(ActionEvent event) {
        MatrixOperation operation = mapButtonToOperation(((Button) event.getSource()).getText());
        double[][] matrixA = buildMatrix("A");
        double[][] matrixB = buildMatrix("B");
        cancelCurrentTask();
        setComputingState();
        launchMatrixTask(operation, matrixA, matrixB);
    }

    // ── Private helpers ────────────────────────────────────────────────────

    private void cancelCurrentTask() {
        if (currentTask != null && currentTask.isRunning()) currentTask.cancel();
    }

    private void setComputingState() {
        resultArea.setStyle(NORMAL_STYLE);
        resultArea.setText("Computing...");
    }

    private void launchMatrixTask(MatrixOperation operation, double[][] matA, double[][] matB) {
        final int taskSize = matrixSize;
        currentTask = new Task<>() {
            @Override
            protected MatrixCalculatorResult call() {
                MatrixCalculatorForm form = new MatrixCalculatorForm(operation, matA, matB, taskSize);
                return service.calculate(form);
            }
        };
        final MatrixOperation historyOp = operation;
        currentTask.setOnSucceeded(ws ->
            Platform.runLater(() -> displayResult(currentTask.getValue(), historyOp, taskSize)));
        currentTask.setOnFailed(ws -> onTaskFailed(currentTask.getException(), operation));
        Thread t = new Thread(currentTask, "matrix-calc-thread");
        t.setDaemon(true);
        t.start();
        log.debug("Matrix task started for operation={}", operation);
    }

    private void onTaskFailed(Throwable ex, MatrixOperation operation) {
        log.error("Matrix Task failed for operation={}", operation, ex);
        Platform.runLater(() -> {
            resultArea.setStyle(ERROR_STYLE);
            resultArea.setText("Error: Computation failed — " + ex.getMessage());
            showErrorDialog("Computation Error",
                    "Matrix " + operation + " failed.\n" + ex.getMessage());
        });
    }

    // ── Helpers ────────────────────────────────────────────────────────────

    private void displayResult(MatrixCalculatorResult result, MatrixOperation operation, int size) {
        if (result.isError()) {
            resultArea.setStyle(ERROR_STYLE);
            resultArea.setText("Error: " + result.errorMessage());
            log.warn("Matrix calculation returned error: {}", result.errorMessage());
        } else if (result.determinant() != null) {
            // DETERMINANT result
            String formatted = String.format("Determinant = %.4f", result.determinant());
            resultArea.setStyle(NORMAL_STYLE);
            resultArea.setText(formatted);
            String inputSummary = operation + ", " + size + "x" + size;
            HistoryService.getInstance().addEntry("Matrix", inputSummary, formatted);
        } else if (result.resultMatrix() != null) {
            // Matrix result — format as grid
            String formatted = formatMatrix(result.resultMatrix());
            resultArea.setStyle(NORMAL_STYLE);
            resultArea.setText(formatted);
            String inputSummary = operation + ", " + size + "x" + size;
            HistoryService.getInstance().addEntry("Matrix", inputSummary, formatted);
        }
    }

    /**
     * Build a double[][] matrix from the TextFields for the given prefix ("A" or "B").
     */
    private double[][] buildMatrix(String prefix) {
        double[][] m = new double[matrixSize][matrixSize];
        if ("A".equals(prefix)) {
            m[0][0] = safeDouble(a00); m[0][1] = safeDouble(a01);
            m[1][0] = safeDouble(a10); m[1][1] = safeDouble(a11);
            if (matrixSize == 3) {
                m[0][2] = safeDouble(a02);
                m[1][2] = safeDouble(a12);
                m[2][0] = safeDouble(a20); m[2][1] = safeDouble(a21); m[2][2] = safeDouble(a22);
            }
        } else {
            m[0][0] = safeDouble(b00); m[0][1] = safeDouble(b01);
            m[1][0] = safeDouble(b10); m[1][1] = safeDouble(b11);
            if (matrixSize == 3) {
                m[0][2] = safeDouble(b02);
                m[1][2] = safeDouble(b12);
                m[2][0] = safeDouble(b20); m[2][1] = safeDouble(b21); m[2][2] = safeDouble(b22);
            }
        }
        return m;
    }

    private void setThirdRowVisible(boolean visible) {
        for (TextField tf : new TextField[]{a02, a12, a20, a21, a22,
                                             b02, b12, b20, b21, b22}) {
            if (tf != null) {
                tf.setVisible(visible);
                tf.setManaged(visible);
            }
        }
    }

    private void showErrorDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Matrix Error");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
