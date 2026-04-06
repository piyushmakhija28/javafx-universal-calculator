package com.techdeveloper.calculator.controller;

import com.techdeveloper.calculator.service.CalculatorService;
import com.techdeveloper.calculator.service.CalculatorType;
import com.techdeveloper.calculator.service.HistoryService;
import com.techdeveloper.calculator.service.ServiceFactory;
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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Controller for matrix-calculator.fxml.
 * Matrix operations run on a background Task to avoid blocking the FX Application Thread.
 * Platform.runLater() is used to push results back to the UI after Task completion.
 *
 * Service inputs: "operation", "matrixA" (comma-separated row-major), "matrixB", "size".
 * Button display text is mapped to service operation names (ADD, SUBTRACT, MULTIPLY, etc.)
 */
public class MatrixCalculatorController implements Initializable {

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

    private int matrixSize = 2;

    /** Reference to the currently running Task — cancelled when a new operation starts. */
    private Task<String> currentTask;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        CalculatorService svc = ServiceFactory.getInstance().getService(CalculatorType.MATRIX);
        log.debug("MatrixCalculatorController initialized, service={}", svc.getClass().getSimpleName());
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
        String buttonText = ((Button) event.getSource()).getText();
        // Map display button labels to the service's expected operation names
        String operation = mapButtonToOperation(buttonText);

        // Build comma-separated matrix strings for the service
        String matrixAStr = buildMatrixString("A");
        String matrixBStr = buildMatrixString("B");

        Map<String, String> inputs = new LinkedHashMap<>();
        inputs.put("operation", operation);
        inputs.put("matrixA",   matrixAStr);
        inputs.put("matrixB",   matrixBStr);
        inputs.put("size",      String.valueOf(matrixSize));

        // Cancel any in-flight task
        if (currentTask != null && currentTask.isRunning()) {
            currentTask.cancel();
        }

        resultArea.setStyle(NORMAL_STYLE);
        resultArea.setText("Computing...");

        // Background Task — matrix ops can be CPU-intensive for large sizes
        final Map<String, String> taskInputs = inputs;
        currentTask = new Task<>() {
            @Override
            protected String call() {
                CalculatorService svc = ServiceFactory.getInstance().getService(CalculatorType.MATRIX);
                return svc.calculate(taskInputs);
            }
        };

        final String historyOperation = operation;
        currentTask.setOnSucceeded(workerState -> {
            String result = currentTask.getValue();
            // Platform.runLater() — mandatory for any UI mutation from a non-FX thread.
            // HistoryService.addEntry() mutates an ObservableList and must also run on the FX thread.
            Platform.runLater(() -> {
                displayResult(result);
                if (!result.startsWith("Error:")) {
                    String inputSummary = historyOperation + ", " + matrixSize + "x" + matrixSize;
                    HistoryService.getInstance().addEntry("Matrix", inputSummary, result);
                }
            });
        });

        currentTask.setOnFailed(workerState -> {
            Throwable ex = currentTask.getException();
            log.error("Matrix Task failed for operation={}", operation, ex);
            Platform.runLater(() -> {
                resultArea.setStyle(ERROR_STYLE);
                resultArea.setText("Error: Computation failed — " + ex.getMessage());
                showErrorDialog("Computation Error",
                        "Matrix " + operation + " failed.\n" + ex.getMessage());
            });
        });

        Thread taskThread = new Thread(currentTask, "matrix-calc-thread");
        taskThread.setDaemon(true); // Daemon: JVM exit will not block on this thread
        taskThread.start();
        log.debug("Matrix task started for operation={}", operation);
    }

    // ── Helpers ────────────────────────────────────────────────────────────

    private void displayResult(String result) {
        if (result.startsWith("Error:")) {
            resultArea.setStyle(ERROR_STYLE);
            resultArea.setText(result);
            log.warn("Matrix calculation returned error: {}", result);
        } else {
            resultArea.setStyle(NORMAL_STYLE);
            resultArea.setText(result);
        }
    }

    /**
     * Build a comma-separated, row-major string from the matrix TextFields.
     * For 2x2: "a00,a01,a10,a11"
     * For 3x3: "a00,a01,a02,a10,a11,a12,a20,a21,a22"
     */
    private String buildMatrixString(String prefix) {
        if ("A".equals(prefix)) {
            if (matrixSize == 2) {
                return safeText(a00) + "," + safeText(a01) + ","
                     + safeText(a10) + "," + safeText(a11);
            } else {
                return safeText(a00) + "," + safeText(a01) + "," + safeText(a02) + ","
                     + safeText(a10) + "," + safeText(a11) + "," + safeText(a12) + ","
                     + safeText(a20) + "," + safeText(a21) + "," + safeText(a22);
            }
        } else {
            if (matrixSize == 2) {
                return safeText(b00) + "," + safeText(b01) + ","
                     + safeText(b10) + "," + safeText(b11);
            } else {
                return safeText(b00) + "," + safeText(b01) + "," + safeText(b02) + ","
                     + safeText(b10) + "," + safeText(b11) + "," + safeText(b12) + ","
                     + safeText(b20) + "," + safeText(b21) + "," + safeText(b22);
            }
        }
    }

    /**
     * Map FXML button display text to the operation name expected by MatrixCalculatorService.
     */
    private String mapButtonToOperation(String buttonText) {
        return switch (buttonText.trim().toUpperCase()) {
            case "ADD"         -> "ADD";
            case "SUBTRACT"    -> "SUBTRACT";
            case "MULTIPLY"    -> "MULTIPLY";
            case "TRANSPOSE A" -> "TRANSPOSE";
            case "DET A"       -> "DETERMINANT";
            default            -> buttonText.trim().toUpperCase();
        };
    }

    private String safeText(TextField tf) {
        if (tf == null) return "0";
        String txt = tf.getText().trim();
        return txt.isEmpty() ? "0" : txt;
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
