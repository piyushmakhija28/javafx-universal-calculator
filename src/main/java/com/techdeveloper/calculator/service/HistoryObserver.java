package com.techdeveloper.calculator.service;

/**
 * Observer interface for calculation history events.
 * JavaFX controllers implement this to bind history to UI components.
 * The service layer only knows about this interface — never about JavaFX ObservableList.
 */
public interface HistoryObserver {
    void onCalculation(CalculationEvent<?, ?> event);
}
