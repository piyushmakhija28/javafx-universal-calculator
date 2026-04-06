package com.techdeveloper.calculator.controller;

import com.techdeveloper.calculator.ViewRouter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for main.fxml.
 * Handles all 15 MenuBar item actions by delegating to ViewRouter.
 * ViewRouter swaps the center pane of the root BorderPane.
 */
public class MainAppController implements Initializable {

    @FXML
    private BorderPane mainLayout;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        ViewRouter.getInstance().setMainLayout(mainLayout);
    }

    // ── Basic ──────────────────────────────────────────────────────────────

    @FXML
    private void loadBasic(ActionEvent event) {
        ViewRouter.getInstance().loadView("/fxml/basic-calculator.fxml");
    }

    // ── Science ────────────────────────────────────────────────────────────

    @FXML
    private void loadScientific(ActionEvent event) {
        ViewRouter.getInstance().loadView("/fxml/scientific-calculator.fxml");
    }

    @FXML
    private void loadProgrammer(ActionEvent event) {
        ViewRouter.getInstance().loadView("/fxml/programmer-calculator.fxml");
    }

    @FXML
    private void loadMatrix(ActionEvent event) {
        ViewRouter.getInstance().loadView("/fxml/matrix-calculator.fxml");
    }

    @FXML
    private void loadStatistics(ActionEvent event) {
        ViewRouter.getInstance().loadView("/fxml/statistics-calculator.fxml");
    }

    // ── Finance ────────────────────────────────────────────────────────────

    @FXML
    private void loadEMI(ActionEvent event) {
        ViewRouter.getInstance().loadView("/fxml/emi-calculator.fxml");
    }

    @FXML
    private void loadCurrency(ActionEvent event) {
        ViewRouter.getInstance().loadView("/fxml/currency-calculator.fxml");
    }

    @FXML
    private void loadTip(ActionEvent event) {
        ViewRouter.getInstance().loadView("/fxml/tip-calculator.fxml");
    }

    @FXML
    private void loadDiscount(ActionEvent event) {
        ViewRouter.getInstance().loadView("/fxml/discount-calculator.fxml");
    }

    // ── Health ─────────────────────────────────────────────────────────────

    @FXML
    private void loadBMI(ActionEvent event) {
        ViewRouter.getInstance().loadView("/fxml/bmi-calculator.fxml");
    }

    @FXML
    private void loadAge(ActionEvent event) {
        ViewRouter.getInstance().loadView("/fxml/age-calculator.fxml");
    }

    // ── Utilities ──────────────────────────────────────────────────────────

    @FXML
    private void loadDateDiff(ActionEvent event) {
        ViewRouter.getInstance().loadView("/fxml/date-diff-calculator.fxml");
    }

    @FXML
    private void loadUnit(ActionEvent event) {
        ViewRouter.getInstance().loadView("/fxml/unit-converter.fxml");
    }

    @FXML
    private void loadSpeed(ActionEvent event) {
        ViewRouter.getInstance().loadView("/fxml/speed-calculator.fxml");
    }

    @FXML
    private void loadFuel(ActionEvent event) {
        ViewRouter.getInstance().loadView("/fxml/fuel-calculator.fxml");
    }
}
