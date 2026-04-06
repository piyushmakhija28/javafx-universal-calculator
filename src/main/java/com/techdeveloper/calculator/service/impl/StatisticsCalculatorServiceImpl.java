package com.techdeveloper.calculator.service.impl;

import com.techdeveloper.calculator.dto.StatisticsCalculatorResult;
import com.techdeveloper.calculator.form.StatisticsCalculatorForm;
import com.techdeveloper.calculator.service.CalculatorService;

import java.util.Arrays;

/**
 * Package-private implementation of Statistics Calculator.
 * Form field: data (double[]) — pre-parsed array of values.
 * Returns: count, sum, mean, median, stdDev, min, max.
 */
class StatisticsCalculatorServiceImpl implements CalculatorService<StatisticsCalculatorForm, StatisticsCalculatorResult> {

    private StatisticsCalculatorServiceImpl() {}

    static StatisticsCalculatorServiceImpl newInstance() {
        return new StatisticsCalculatorServiceImpl();
    }

    @Override
    public StatisticsCalculatorResult calculate(StatisticsCalculatorForm form) {
        try {
            double[] values = form.data();

            if (values == null || values.length == 0) {
                return StatisticsCalculatorResult.error("data must contain at least one value");
            }

            int    count    = values.length;
            double sum      = computeSum(values);
            double mean     = sum / count;
            double[] sorted = Arrays.copyOf(values, count);
            Arrays.sort(sorted);
            double median   = computeMedian(sorted, count);
            double variance = computeVariance(values, mean);
            double stdDev   = Math.sqrt(variance);
            double min      = sorted[0];
            double max      = sorted[count - 1];

            return StatisticsCalculatorResult.success(mean, median, stdDev, min, max, sum, count);

        } catch (Exception e) {
            return StatisticsCalculatorResult.error(e.getMessage());
        }
    }

    private double computeSum(double[] values) {
        double sum = 0;
        for (double v : values) sum += v;
        return sum;
    }

    private double computeMedian(double[] sorted, int count) {
        return (count % 2 == 1)
                ? sorted[count / 2]
                : (sorted[count / 2 - 1] + sorted[count / 2]) / 2.0;
    }

    private double computeVariance(double[] values, double mean) {
        double sumSq = 0;
        for (double v : values) {
            double diff = v - mean;
            sumSq += diff * diff;
        }
        return sumSq / values.length;
    }
}
