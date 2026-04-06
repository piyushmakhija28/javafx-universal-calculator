package com.techdeveloper.calculator.service;

import com.techdeveloper.calculator.service.impl.ServiceFactory;
import javafx.concurrent.Task;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

/**
 * Public interface for the live currency rate service.
 * Use {@link #getInstance()} to obtain the singleton via ServiceFactory.
 */
public interface LiveCurrencyService {

    /**
     * Fetches exchange rates synchronously (cached, TTL 60 min).
     *
     * @return map of 3-letter currency code → rate vs USD, or {@code null} on failure
     */
    Map<String, Double> fetchRates();

    /**
     * Returns a JavaFX Task that fetches live rates on a background thread.
     *
     * @return Task whose value is the rates map, or {@code null} on failure
     */
    Task<Map<String, Double>> fetchRatesAsync();

    /**
     * Returns {@code true} if cached rates exist and have not expired.
     */
    boolean isLive();

    /**
     * Returns the instant at which rates were last successfully fetched.
     */
    Optional<Instant> getLastFetchTime();

    /**
     * Formats the last fetch time as "HH:mm". Returns "unknown" if no fetch has occurred.
     */
    String getLastFetchTimeFormatted();

    /**
     * Returns the application-wide singleton instance.
     */
    static LiveCurrencyService getInstance() {
        return ServiceFactory.getInstance().getLiveCurrencyService();
    }
}
