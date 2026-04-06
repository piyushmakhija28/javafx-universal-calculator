package com.techdeveloper.calculator.service;

import java.util.Arrays;
import java.util.Map;

/**
 * Implements Statistics Calculator.
 * Required input: "data" — comma-separated list of numbers
 * Returns an 8-field result: Count, Sum, Mean, Median, Mode, Range, Variance, StdDev (population).
 */
public class StatisticsCalculatorService implements CalculatorService {

    public StatisticsCalculatorService() {
        // required for FXML
    }

    @Override
    public String calculate(Map<String, String> inputs) {
        try {
            String dataStr = inputs.get("data");
            if (dataStr == null || dataStr.trim().isEmpty()) {
                return "Error: data is required (comma-separated numbers)";
            }

            String[] tokens = dataStr.trim().split(",");
            if (tokens.length == 0) {
                return "Error: data must contain at least one value";
            }

            double[] values = new double[tokens.length];
            for (int i = 0; i < tokens.length; i++) {
                String token = tokens[i].trim();
                if (token.isEmpty()) {
                    return "Error: data contains an empty value at position " + (i + 1);
                }
                values[i] = Double.parseDouble(token);
            }

            int count = values.length;
            double sum = computeSum(values);
            double mean = sum / count;
            double[] sorted = Arrays.copyOf(values, count);
            Arrays.sort(sorted);
            double median = computeMedian(sorted, count);
            String mode = computeMode(sorted, count);
            double range = sorted[count - 1] - sorted[0];
            double variance = computeVariance(values, mean);
            double stdDev = Math.sqrt(variance);

            return String.format(
                "Count: %d | Sum: %s | Mean: %s | Median: %s | Mode: %s | Range: %s | Variance: %s | StdDev: %s",
                count,
                formatNum(sum),
                formatNum(mean),
                formatNum(median),
                mode,
                formatNum(range),
                formatNum(variance),
                formatNum(stdDev)
            );

        } catch (NumberFormatException e) {
            return "Error: Invalid number format in data";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private double computeSum(double[] values) {
        double sum = 0;
        for (double v : values) {
            sum += v;
        }
        return sum;
    }

    private double computeMedian(double[] sorted, int count) {
        if (count % 2 == 1) {
            return sorted[count / 2];
        }
        return (sorted[count / 2 - 1] + sorted[count / 2]) / 2.0;
    }

    private String computeMode(double[] sorted, int count) {
        int maxFreq = 1;
        int currentFreq = 1;
        double modeValue = sorted[0];
        boolean multiMode = false;

        for (int i = 1; i < count; i++) {
            if (sorted[i] == sorted[i - 1]) {
                currentFreq++;
                if (currentFreq > maxFreq) {
                    maxFreq = currentFreq;
                    modeValue = sorted[i];
                    multiMode = false;
                } else if (currentFreq == maxFreq) {
                    multiMode = true;
                }
            } else {
                currentFreq = 1;
            }
        }

        if (maxFreq == 1) {
            return "No mode";
        }
        if (multiMode) {
            return "Multiple modes";
        }
        return formatNum(modeValue);
    }

    private double computeVariance(double[] values, double mean) {
        double sumSq = 0;
        for (double v : values) {
            double diff = v - mean;
            sumSq += diff * diff;
        }
        // Population variance
        return sumSq / values.length;
    }

    private String formatNum(double value) {
        if (value == Math.floor(value) && !Double.isInfinite(value) && Math.abs(value) < 1e15) {
            return String.valueOf((long) value);
        }
        // Round to 6 decimal places
        double rounded = Math.round(value * 1e6) / 1e6;
        return String.valueOf(rounded);
    }
}
