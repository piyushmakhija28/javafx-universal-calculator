package com.techdeveloper.calculator.controller;

import com.techdeveloper.calculator.service.HistoryEntry;
import com.techdeveloper.calculator.service.HistoryService;
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
 *   - Bind historyList to HistoryService.getEntries() (live ObservableList).
 *   - Install a custom cell factory that renders each HistoryEntry as:
 *       [time] Type | inputs = result    [Copy]
 *     The Copy button writes the entry's toString() to the system clipboard.
 *   - Handle "Clear History" button.
 *
 * Thread safety:
 *   All FXML lifecycle methods and event handlers run on the JavaFX Application Thread.
 *   No background work is performed here.
 */
public class HistoryPanelController implements Initializable {

    public HistoryPanelController() {
        // required for FXML
    }

    private static final Logger log = LoggerFactory.getLogger(HistoryPanelController.class);

    @FXML private ListView<HistoryEntry> historyList;
    @FXML private Button btnClear;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Bind the ListView directly to the live ObservableList.
        // Any addEntry() or clear() call on HistoryService will automatically
        // refresh the ListView without any additional wiring.
        historyList.setItems(HistoryService.getInstance().getEntries());

        // Custom cell factory: each cell shows the entry text plus a "Copy" button.
        historyList.setCellFactory(listView -> new HistoryCell());

        log.debug("HistoryPanelController initialized — ListView bound to HistoryService");
    }

    @FXML
    private void onClear(ActionEvent event) {
        HistoryService.getInstance().clear();
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
