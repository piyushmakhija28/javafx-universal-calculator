package com.techdeveloper.calculator.service;

import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Plain Java Singleton that fetches live exchange rates from the open.er-api.com
 * free endpoint (no API key required).
 *
 * <p>Endpoint: {@code https://open.er-api.com/v6/latest/USD}
 * <p>Response shape: {@code {"rates": {"EUR": 0.92, "GBP": 0.79, ...}}}
 *
 * <p>All rates are stored relative to 1 USD — the same convention used by
 * {@link CurrencyCalculatorService} — so the conversion formula is identical:
 * {@code convertedAmount = amount * ratesMap.get(toCurrency) / ratesMap.get(fromCurrency)}
 *
 * <p>Cache TTL is 60 minutes. Expired or absent cache triggers a fresh network call.
 * On any failure the method returns {@code null} and logs a warning; callers must
 * handle the null and fall back to the static-rate service.
 */
public final class LiveCurrencyService {

    private static final Logger log = LoggerFactory.getLogger(LiveCurrencyService.class);

    private static final String RATES_URL = "https://open.er-api.com/v6/latest/USD";
    private static final long CACHE_TTL_MINUTES = 60;
    private static final DateTimeFormatter TIME_FMT =
            DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.systemDefault());

    // ── Singleton ─────────────────────────────────────────────────────────────
    private static volatile LiveCurrencyService instance;

    public static LiveCurrencyService getInstance() {
        if (instance == null) {
            synchronized (LiveCurrencyService.class) {
                if (instance == null) {
                    instance = new LiveCurrencyService();
                }
            }
        }
        return instance;
    }

    private LiveCurrencyService() {
        // private — use getInstance()
    }

    // ── State ─────────────────────────────────────────────────────────────────
    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    /** Cached rates map; null means never fetched or last fetch failed. */
    private volatile Map<String, Double> cachedRates = null;
    private volatile Instant lastFetched = null;

    // ── Public API ────────────────────────────────────────────────────────────

    /**
     * Fetches exchange rates synchronously.
     *
     * <p>Returns the cached map if it was populated within the last 60 minutes.
     * Otherwise performs a blocking HTTP GET and parses the JSON response.
     *
     * @return map of 3-letter currency code → rate vs USD, or {@code null} on failure
     */
    public Map<String, Double> fetchRates() {
        if (isCacheValid()) {
            log.debug("LiveCurrencyService: returning cached rates (fetched at {})",
                    lastFetched);
            return cachedRates;
        }

        log.info("LiveCurrencyService: fetching live rates from {}", RATES_URL);
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(RATES_URL))
                    .timeout(Duration.ofSeconds(15))
                    .GET()
                    .build();

            HttpResponse<String> response =
                    httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                log.warn("LiveCurrencyService: HTTP {} received, falling back to static rates",
                        response.statusCode());
                return null;
            }

            Map<String, Double> parsed = parseRates(response.body());
            if (parsed == null || parsed.isEmpty()) {
                log.warn("LiveCurrencyService: parsed rates map is empty, falling back to static rates");
                return null;
            }

            cachedRates = parsed;
            lastFetched = Instant.now();
            log.info("LiveCurrencyService: fetched {} rates at {}", parsed.size(), lastFetched);
            return cachedRates;

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("LiveCurrencyService: fetch interrupted, falling back to static rates", e);
            return null;
        } catch (Exception e) {
            log.warn("LiveCurrencyService: fetch failed ({}), falling back to static rates",
                    e.getMessage());
            return null;
        }
    }

    /**
     * Returns a JavaFX {@link Task} that fetches live rates on a background thread.
     *
     * <p>Wire up the task callbacks before starting the thread:
     * <pre>{@code
     * Task<Map<String,Double>> task = LiveCurrencyService.getInstance().fetchRatesAsync();
     * task.setOnSucceeded(e -> Platform.runLater(() -> handleRates(task.getValue())));
     * task.setOnFailed(e  -> Platform.runLater(() -> handleOffline()));
     * new Thread(task).start();
     * }</pre>
     *
     * @return a Task whose value is the rates map, or {@code null} on failure
     */
    public Task<Map<String, Double>> fetchRatesAsync() {
        return new Task<>() {
            @Override
            protected Map<String, Double> call() {
                return fetchRates();
            }
        };
    }

    /**
     * Returns {@code true} if the cache holds a non-null, non-expired rates map.
     * "Live" here means rates were successfully fetched from the network at some point
     * and the cache has not yet expired.
     *
     * @return true when live rates are currently available
     */
    public boolean isLive() {
        return isCacheValid();
    }

    /**
     * Returns the instant at which rates were last successfully fetched,
     * or {@link Optional#empty()} if no successful fetch has occurred.
     *
     * @return optional fetch timestamp
     */
    public Optional<Instant> getLastFetchTime() {
        return Optional.ofNullable(lastFetched);
    }

    /**
     * Formats the last fetch time as "HH:mm" using the system default time zone.
     * Returns "unknown" if no fetch has occurred.
     *
     * @return formatted time string suitable for display in the UI status label
     */
    public String getLastFetchTimeFormatted() {
        return getLastFetchTime()
                .map(TIME_FMT::format)
                .orElse("unknown");
    }

    // ── Internal helpers ──────────────────────────────────────────────────────

    private boolean isCacheValid() {
        if (cachedRates == null || lastFetched == null) {
            return false;
        }
        long minutesElapsed = Duration.between(lastFetched, Instant.now()).toMinutes();
        return minutesElapsed < CACHE_TTL_MINUTES;
    }

    /**
     * Parses the open.er-api.com JSON response body.
     *
     * <p>Expected shape:
     * <pre>{@code
     * {
     *   "result": "success",
     *   "rates": {
     *     "USD": 1,
     *     "EUR": 0.92,
     *     ...
     *   }
     * }
     * }</pre>
     *
     * @param body raw JSON response body
     * @return map of currency code → rate vs USD, or {@code null} on parse failure
     */
    private static final Pattern RESULT_PATTERN =
            Pattern.compile("\"result\"\\s*:\\s*\"([^\"]+)\"");
    private static final Pattern RATES_ENTRY_PATTERN =
            Pattern.compile("\"([A-Z]{3,4})\"\\s*:\\s*([0-9]+(?:\\.[0-9]+)?(?:[eE][+-]?[0-9]+)?)");

    private Map<String, Double> parseRates(String body) {
        try {
            // Verify the API reported success (if "result" key is present)
            Matcher resultMatcher = RESULT_PATTERN.matcher(body);
            if (resultMatcher.find() && !"success".equals(resultMatcher.group(1))) {
                log.warn("LiveCurrencyService: API result is '{}', not 'success'",
                        resultMatcher.group(1));
                return null;
            }

            // Locate the "rates" object block
            int ratesKeyIdx = body.indexOf("\"rates\"");
            if (ratesKeyIdx == -1) {
                log.warn("LiveCurrencyService: 'rates' key not found in response");
                return null;
            }
            int braceOpen = body.indexOf('{', ratesKeyIdx);
            int braceClose = body.indexOf('}', braceOpen);
            if (braceOpen == -1 || braceClose == -1) {
                log.warn("LiveCurrencyService: could not delimit rates object");
                return null;
            }
            String ratesBlock = body.substring(braceOpen + 1, braceClose);

            // Extract "CODE": number pairs
            Map<String, Double> rates = new HashMap<>();
            Matcher m = RATES_ENTRY_PATTERN.matcher(ratesBlock);
            while (m.find()) {
                rates.put(m.group(1).toUpperCase(), Double.parseDouble(m.group(2)));
            }

            return rates.isEmpty() ? null : rates;

        } catch (Exception e) {
            log.warn("LiveCurrencyService: JSON parse error — {}", e.getMessage());
            return null;
        }
    }
}
