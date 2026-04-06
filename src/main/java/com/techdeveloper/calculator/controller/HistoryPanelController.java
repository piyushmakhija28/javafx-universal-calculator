package com.techdeveloper.calculator.controller;

import com.techdeveloper.calculator.service.CalculationEvent;
import com.techdeveloper.calculator.service.HistoryEntry;
import com.techdeveloper.calculator.service.HistoryObserver;
import com.techdeveloper.calculator.service.HistoryService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Priority;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for history-panel.fxml.
 *
 * Responsibilities:
 *   - Own a local ObservableList<HistoryEntry> seeded from the service snapshot.
 *   - Implement HistoryObserver to receive new entries and update the list on the FX thread.
 *   - Install a custom cell factory that renders each HistoryEntry as:
 *       [time] Type | inputs = result    [Copy]
 *     The Copy button writes the entry's toString() to the system clipboard.
 *   - Handle "Clear History" button.
 *
 * Thread safety:
 *   onCalculation() is dispatched to the JavaFX Application Thread via Platform.runLater().
 *   All FXML lifecycle methods and event handlers run on the JavaFX Application Thread.
 */
public class HistoryPanelController implements Initializable, HistoryObserver {

    public HistoryPanelController() {
        // required for FXML
    }

    private static final Logger log = LoggerFactory.getLogger(HistoryPanelController.class);

    @FXML private ListView<HistoryEntry> historyList;
    @FXML private Button btnClear;

    /** Local observable mirror — updated via HistoryObserver and on clear. */
    private final ObservableList<HistoryEntry> observableEntries =
            FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Seed the observable list from the current snapshot.
        observableEntries.setAll(HistoryService.getInstance().getEntries());

        // Bind the ListView to our local observable list.
        historyList.setItems(observableEntries);

        // Register as observer so new entries arrive via onCalculation().
        HistoryService.getInstance().addObserver(this);

        // Custom cell factory: each cell shows the entry text plus a "Copy" button.
        historyList.setCellFactory(listView -> new HistoryCell());

        log.debug("HistoryPanelController initialized — ListView bound to local ObservableList");
    }

    /**
     * Called by HistoryService when a new calculation is recorded.
     * Dispatched to the JavaFX Application Thread.
     */
    @Override
    public void onCalculation(CalculationEvent<?, ?> event) {
        // The service addEntry already created the HistoryEntry — re-read the snapshot
        // to stay in sync (handles MAX_ENTRIES eviction automatically).
        Platform.runLater(() -> {
            observableEntries.setAll(HistoryService.getInstance().getEntries());
        });
    }

    @FXML
    private void onClear(ActionEvent event) {
        HistoryService.getInstance().clear();
        observableEntries.clear();
        log.info("History cleared by user via history panel");
    }

    // ── Inner class: custom ListCell ──────────────────────────────────────

    /**
     * Custom ListCell that renders a HistoryEntry as an HBox containing:
     *   - A Label showing entry.toString() (grows to fill width)
     *   - A compact "Copy" Button that copies the text to the system clipboard
     *
     * Cell recycling: updateItem() is the standard JavaFX cell recycling hook.
     * Setting graphic to null on empty cells is mandatory to prevent ghost rows.
     */
    private static final class HistoryCell extends ListCell<HistoryEntry> {

        private final HBox  cellBox;
        private final Label entryLabel;
        private final Button copyButton;

        HistoryCell() {
            entryLabel = new Label();
            entryLabel.setWrapText(false);
            entryLabel.setMaxWidth(Double.MAX_VALUE);
            entryLabel.getStyleClass().add("history-cell-label");
            HBox.setHgrow(entryLabel, Priority.ALWAYS);

            copyButton = new Button("Copy");
            copyButton.getStyleClass().add("copy-btn");
            copyButton.setFocusTraversable(false);

            cellBox = new HBox(6, entryLabel, copyButton);
            cellBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            getStyleClass().add("history-cell");
        }

        @Override
        protected void updateItem(HistoryEntry entry, boolean empty) {
            super.updateItem(entry, empty);
            if (empty || entry == null) {
                setText(null);
                setGraphic(null);
                // Remove any stale action handler from recycled cells
                copyButton.setOnAction(null);
            } else {
                entryLabel.setText(entry.toString());
                // Capture entry for the lambda to avoid holding a reference to 'this'
                final String textToCopy = entry.toString();
                copyButton.setOnAction(evt -> {
                    ClipboardContent content = new ClipboardContent();
                    content.putString(textToCopy);
                    Clipboard.getSystemClipboard().setContent(content);
                });
                setText(null);
                setGraphic(cellBox);
            }
        }
    }
}
