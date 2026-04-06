package com.techdeveloper.calculator.controller;

import com.techdeveloper.calculator.ViewRouter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for main.fxml.
 *
 * Handles:
 *   - All 15 MenuBar item actions by delegating to ViewRouter.
 *   - History panel toggle via View > "History Panel" CheckMenuItem.
 *
 * Layout change from Phase F.1:
 *   The root BorderPane now contains a SplitPane in its center.
 *   The SplitPane left side is "calculatorPane" (inner BorderPane) and the right side
 *   is the history-panel.fxml include.  ViewRouter is registered with calculatorPane,
 *   not with the root mainLayout.
 *
 * Toggle behaviour:
 *   When the history panel is hidden, the SplitPane divider moves to position 1.0 so
 *   the calculator pane fills the full width.  When shown, it is restored to 0.80.
 */
public class MainAppController implements Initializable {

    public MainAppController() {
        // required for FXML
    }

    private static final Logger log = LoggerFactory.getLogger(MainAppController.class);

    /** The inner BorderPane that acts as the calculator area (left side of SplitPane). */
    @FXML private BorderPane calculatorPane;

    /** The SplitPane holding calculatorPane (left) and the history panel (right). */
    @FXML private SplitPane splitPane;

    /** CheckMenuItem in the View menu — tracks history panel visibility state. */
    @FXML private CheckMenuItem menuHistoryToggle;

    /** Divider position when the history panel is visible (80 / 20 split). */
    private static final double HISTORY_VISIBLE_DIVIDER = 0.80;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Register the inner calculator BorderPane with ViewRouter so it swaps
        // calculator views into the correct pane, not the root layout.
        ViewRouter.getInstance().setMainLayout(calculatorPane);
        log.info("MainAppController initialized — calculatorPane registered with ViewRouter");
    }

    // ── View menu ──────────────────────────────────────────────────────────

    /**
     * Toggle the history panel.
     * When hidden: divider is pushed to 1.0 so the calculator pane fills the full width.
     * When shown:  divider returns to the 80/20 split.
     */
    @FXML
    private void onToggleHistory(ActionEvent event) {
        boolean show = menuHistoryToggle.isSelected();
        if (splitPane != null && !splitPane.getDividers().isEmpty()) {
            splitPane.getDividers().get(0).setPosition(show ? HISTORY_VISIBLE_DIVIDER : 1.0);
        }
        log.debug("History panel toggled: visible={}", show);
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
