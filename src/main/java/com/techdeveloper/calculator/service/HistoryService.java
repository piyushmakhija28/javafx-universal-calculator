package com.techdeveloper.calculator.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Singleton service that stores calculation history.
 *
 * Design decisions:
 *   - Backed by an ObservableList so that a bound ListView auto-refreshes on every addEntry().
 *   - Maximum 20 entries enforced via a LinkedList-style eviction: oldest entry removed when full.
 *   - All mutations are performed on the FX Application Thread contract:
 *     callers that invoke addEntry() from a background thread (Matrix, Statistics tasks) must
 *     wrap the call inside Platform.runLater() — see MatrixCalculatorController for the pattern.
 *   - getInstance() is thread-safe via double-checked locking with a volatile field (Java 5+
 *     memory model guarantee).
 */
public final class HistoryService {

    private static final Logger log = LoggerFactory.getLogger(HistoryService.class);

    /** Maximum number of history entries retained. */
    private static final int MAX_ENTRIES = 20;

    private static volatile HistoryService instance;

    /** ObservableList allows direct ListView binding — mutations update the UI automatically. */
    private final ObservableList<HistoryEntry> entries =
            FXCollections.observableArrayList();

    private HistoryService() {}

    /**
     * Thread-safe singleton accessor using double-checked locking.
     * The volatile guarantee on the instance field ensures correct publication under the
     * Java Memory Model (JLS 17.4.5).
     */
    public static HistoryService getInstance() {
        if (instance == null) {
            synchronized (HistoryService.class) {
                if (instance == null) {
                    instance = new HistoryService();
                }
            }
        }
        return instance;
    }

    /**
     * Add a new calculation history entry.
     * If the list has reached MAX_ENTRIES, the oldest entry (index 0) is removed first.
     *
     * THREAD SAFETY: this method mutates an ObservableList, which must only be done on the
     * JavaFX Application Thread.  Background-thread callers (e.g., Matrix/Statistics tasks)
     * MUST wrap this call in Platform.runLater().
     *
     * @param calculatorType human-readable calculator name (e.g. "Basic", "EMI")
     * @param inputSummary   short description of inputs (e.g. "10 + 5")
     * @param result         the string result from the calculator service
     */
    public void addEntry(String calculatorType, String inputSummary, String result) {
        if (entries.size() >= MAX_ENTRIES) {
            entries.remove(0);
        }
        HistoryEntry entry = new HistoryEntry(calculatorType, inputSummary, result);
        entries.add(entry);
        log.debug("History entry added: type={}, result={}", calculatorType, result);
    }

    /**
     * Returns the live ObservableList.
     * Bind a ListView directly: listView.setItems(HistoryService.getInstance().getEntries())
     * The ListView will auto-refresh whenever entries are added or removed.
     */
    public ObservableList<HistoryEntry> getEntries() {
        return entries;
    }

    /**
     * Remove all history entries.
     * Must be called on the JavaFX Application Thread.
     */
    public void clear() {
        entries.clear();
        log.info("Calculation history cleared");
    }
}
