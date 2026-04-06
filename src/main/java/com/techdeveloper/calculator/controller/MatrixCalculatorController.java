package com.techdeveloper.calculator.controller;

import com.techdeveloper.calculator.service.CalculatorService;
import com.techdeveloper.calculator.service.CalculatorType;
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
 */
public class MatrixCalculatorController implements Initializable {

    private static final Logger log = LoggerFactory.getLogger(MatrixCalculatorController.class);

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
        // 3rd row/column fields start hidden (2x2 default); toggled by onSizeChange
        setThirdRowVisible(false);
    }

    @FXML
    private void onSizeChange(ActionEvent event) {
        matrixSize = rb3x3.isSelected() ? 3 : 2;
        setThirdRowVisible(matrixSize == 3);
        resultArea.clear();
        log.debug("Matrix size changed to {}x{}", matrixSize, matrixSize);
    }

    @FXML
    private void onOperation(ActionEvent event) {
        String operation = ((Button) event.getSource()).getText();

        // Collect matrix inputs from TextFields
        Map<String, String> inputs = new LinkedHashMap<>();
        inputs.put("operation", operation);
        inputs.put("size",      String.valueOf(matrixSize));
        collectMatrix(inputs, "A", a00, a01, a02, a10, a11, a12, a20, a21, a22);
        collectMatrix(inputs, "B", b00, b01, b02, b10, b11, b12, b20, b21, b22);

        // Cancel any in-flight task
        if (currentTask != null && currentTask.isRunning()) {
            currentTask.cancel();
        }

        resultArea.setText("Computing...");

        // Background Task — matrix ops can be CPU-intensive for large sizes
        currentTask = new Task<>() {
            @Override
            protected String call() {
                CalculatorService svc = ServiceFactory.getInstance().getService(CalculatorType.MATRIX);
                return svc.calculate(inputs);
            }
        };

        currentTask.setOnSucceeded(workerState -> {
            String result = currentTask.getValue();
            // Platform.runLater() — mandatory for any UI mutation from a non-FX thread
            Platform.runLater(() -> displayResult(result));
        });

        currentTask.setOnFailed(workerState -> {
            Throwable ex = currentTask.getException();
            log.error("Matrix Task failed for operation={}", operation, ex);
            Platform.runLater(() -> {
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
            resultArea.setStyle(resultArea.getStyle() + "; -fx-text-fill: #ff6b6b;");
            resultArea.setText(result);
            log.warn("Matrix calculation returned error: {}", result);
        } else {
            resultArea.setStyle("-fx-background-color: #1a1a1a; -fx-text-fill: #ffffff;");
            resultArea.setText(result);
        }
    }

    /** Reads up to 9 TextFields into the input map as "A00", "A01", ... etc. */
    private void collectMatrix(Map<String, String> inputs, String prefix,
                               TextField f00, TextField f01, TextField f02,
                               TextField f10, TextField f11, TextField f12,
                               TextField f20, TextField f21, TextField f22) {
        inputs.put(prefix + "00", safeText(f00));
        inputs.put(prefix + "01", safeText(f01));
        inputs.put(prefix + "10", safeText(f10));
        inputs.put(prefix + "11", safeText(f11));
        if (matrixSize == 3) {
            inputs.put(prefix + "02", safeText(f02));
            inputs.put(prefix + "12", safeText(f12));
            inputs.put(prefix + "20", safeText(f20));
            inputs.put(prefix + "21", safeText(f21));
            inputs.put(prefix + "22", safeText(f22));
        }
    }

    private String safeText(TextField tf) {
        if (tf == null) return "0";
        String txt = tf.getText().trim();
        return txt.isEmpty() ? "0" : txt;
    }

    private void setThirdRowVisible(boolean visible) {
        // Toggle the 3rd column/row TextFields for both matrices
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
