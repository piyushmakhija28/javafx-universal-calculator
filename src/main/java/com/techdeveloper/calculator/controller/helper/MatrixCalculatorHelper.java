package com.techdeveloper.calculator.controller.helper;

import com.techdeveloper.calculator.constants.MatrixOperation;
import javafx.scene.control.TextField;

/**
 * Helper base class for MatrixCalculatorController.
 * Contains utility methods extracted from the controller to keep method bodies ≤ 35 lines.
 * All methods are protected for use by the controller subclass.
 */
public class MatrixCalculatorHelper {

    protected MatrixOperation mapButtonToOperation(String buttonText) {
        return switch (buttonText.trim().toUpperCase()) {
            case "ADD"         -> MatrixOperation.ADD;
            case "SUBTRACT"    -> MatrixOperation.SUBTRACT;
            case "MULTIPLY"    -> MatrixOperation.MULTIPLY;
            case "TRANSPOSE A" -> MatrixOperation.TRANSPOSE;
            case "DET A"       -> MatrixOperation.DETERMINANT;
            default            -> MatrixOperation.valueOf(buttonText.trim().toUpperCase());
        };
    }

    protected String formatMatrix(double[][] matrix) {
        StringBuilder sb = new StringBuilder();
        for (double[] row : matrix) {
            for (int j = 0; j < row.length; j++) {
                if (j > 0) sb.append("  ");
                sb.append(String.format("%10.4f", row[j]));
            }
            sb.append("\n");
        }
        return sb.toString().trim();
    }

    protected double safeDouble(TextField tf) {
        if (tf == null) return 0.0;
        String txt = tf.getText().trim();
        if (txt.isEmpty()) return 0.0;
        try {
            return Double.parseDouble(txt);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}
