package com.techdeveloper.calculator.service.impl;

import com.techdeveloper.calculator.service.LiveCurrencyService;
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
 * Package-private singleton implementation of {@link LiveCurrencyService}.
 * Fetches live exchange rates from open.er-api.com (no API key required).
 * Cache TTL is 60 minutes. On failure returns null; callers fall back to static rates.
 */
final class LiveCurrencyServiceImpl implements LiveCurrencyService {

    private static final Logger log = LoggerFactory.getLogger(LiveCurrencyServiceImpl.class);

    private static final String RATES_URL = "https://open.er-api.com/v6/latest/USD";
    private static final long CACHE_TTL_MINUTES = 60;
    private static final DateTimeFormatter TIME_FMT =
            DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.systemDefault());

    private static volatile LiveCurrencyServiceImpl instance;

    static LiveCurrencyServiceImpl getInstance() {
        if (instance == null) {
            synchronized (LiveCurrencyServiceImpl.class) {
                if (instance == null) {
                    instance = new LiveCurrencyServiceImpl();
                }
            }
        }
        return instance;
    }

    private LiveCurrencyServiceImpl() {}

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .build();

    private volatile Map<String, Double> cachedRates = null;
    private volatile Instant lastFetched = null;

    @Override
    public Map<String, Double> fetchRates() {
        if (isCacheValid()) {
            log.debug("LiveCurrencyService: returning cached rates (fetched at {})", lastFetched);
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

            Instant now = Instant.now();
            cachedRates = parsed;
            lastFetched = now;
            log.info("LiveCurrencyService: fetched {} rates at {}", parsed.size(), now);
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

    @Override
    public Task<Map<String, Double>> fetchRatesAsync() {
        return new Task<>() {
            @Override
            protected Map<String, Double> call() {
                return fetchRates();
            }
        };
    }

    @Override
    public boolean isLive() {
        return isCacheValid();
    }

    @Override
    public Optional<Instant> getLastFetchTime() {
        return Optional.ofNullable(lastFetched);
    }

    @Override
    public String getLastFetchTimeFormatted() {
        return getLastFetchTime()
                .map(TIME_FMT::format)
                .orElse("unknown");
    }

    private boolean isCacheValid() {
        if (cachedRates == null || lastFetched == null) {
            return false;
        }
        long minutesElapsed = Duration.between(lastFetched, Instant.now()).toMinutes();
        return minutesElapsed < CACHE_TTL_MINUTES;
    }

    private static final Pattern RESULT_PATTERN =
            Pattern.compile("\"result\"\\s*:\\s*\"([^\"]+)\"");
    private static final Pattern RATES_ENTRY_PATTERN =
            Pattern.compile("\"([A-Z]{3,4})\"\\s*:\\s*([0-9]+(?:\\.[0-9]+)?(?:[eE][+-]?[0-9]+)?)");

    private Map<String, Double> parseRates(String body) {
        try {
            Matcher resultMatcher = RESULT_PATTERN.matcher(body);
            if (resultMatcher.find() && !"success".equals(resultMatcher.group(1))) {
                log.warn("LiveCurrencyService: API result is '{}', not 'success'",
                        resultMatcher.group(1));
                return null;
            }

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
