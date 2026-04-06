package com.techdeveloper.calculator.service.impl;

import com.techdeveloper.calculator.service.CalculationEvent;
import com.techdeveloper.calculator.service.HistoryEntry;
import com.techdeveloper.calculator.service.HistoryObserver;
import com.techdeveloper.calculator.service.HistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Package-private singleton implementation of {@link HistoryService}.
 * Backed by a CopyOnWriteArrayList — no JavaFX dependency, safe for concurrent access.
 * Observer dispatch is synchronous and inline; no Platform.runLater.
 * Maximum 20 entries enforced via oldest-entry eviction.
 */
final class HistoryServiceImpl implements HistoryService {

    private static final Logger log = LoggerFactory.getLogger(HistoryServiceImpl.class);

    private static final int MAX_ENTRIES = 20;

    private static volatile HistoryServiceImpl instance;

    private final List<HistoryEntry>   entries   = new CopyOnWriteArrayList<>();
    private final List<HistoryObserver> observers = new CopyOnWriteArrayList<>();

    private HistoryServiceImpl() {}

    static HistoryServiceImpl getInstance() {
        if (instance == null) {
            synchronized (HistoryServiceImpl.class) {
                if (instance == null) {
                    instance = new HistoryServiceImpl();
                }
            }
        }
        return instance;
    }

    @Override
    public void addEntry(String calculatorType, String inputSummary, String result) {
        if (entries.size() >= MAX_ENTRIES) {
            entries.remove(0);
        }
        entries.add(new HistoryEntry(calculatorType, inputSummary, result));
        log.debug("History entry added: type={}, result={}", calculatorType, result);

        var event = CalculationEvent.of(calculatorType, inputSummary, result);
        observers.forEach(o -> o.onCalculation(event));
    }

    @Override
    public List<HistoryEntry> getEntries() {
        return Collections.unmodifiableList(entries);
    }

    @Override
    public void clear() {
        entries.clear();
        log.info("Calculation history cleared");
    }

    @Override
    public void addObserver(HistoryObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(HistoryObserver observer) {
        observers.remove(observer);
    }
}
