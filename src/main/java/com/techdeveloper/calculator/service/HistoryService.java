package com.techdeveloper.calculator.service;

import com.techdeveloper.calculator.service.impl.ServiceFactory;

import java.util.List;

/**
 * Pure-Java history service interface. Zero JavaFX dependencies.
 * Controllers that need ObservableList must implement HistoryObserver
 * and maintain their own ObservableList internally.
 */
public interface HistoryService {

    /**
     * Record a calculation result in history and notify all observers.
     * Thread-safe — may be called from any thread.
     */
    void addEntry(String calculatorType, String inputSummary, String result);

    /**
     * Returns an immutable snapshot of current history entries.
     * For live binding in JavaFX, implement HistoryObserver instead.
     */
    List<HistoryEntry> getEntries();

    /**
     * Clear all history entries.
     */
    void clear();

    /**
     * Register an observer to receive CalculationEvent notifications.
     */
    void addObserver(HistoryObserver observer);

    /**
     * Unregister a previously added observer.
     */
    void removeObserver(HistoryObserver observer);

    /**
     * Returns the application-wide singleton instance via ServiceFactory.
     */
    static HistoryService getInstance() {
        return ServiceFactory.getInstance().getHistoryService();
    }
}
