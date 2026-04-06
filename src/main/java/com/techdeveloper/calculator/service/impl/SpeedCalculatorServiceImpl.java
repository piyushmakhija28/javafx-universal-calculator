package com.techdeveloper.calculator.service.impl;

import com.techdeveloper.calculator.constants.SolveFor;
import com.techdeveloper.calculator.dto.SpeedCalculatorResult;
import com.techdeveloper.calculator.form.SpeedCalculatorForm;
import com.techdeveloper.calculator.service.CalculatorService;

/**
 * Package-private implementation of Speed / Distance / Time calculator.
 * Form fields: solve (SolveFor), speed (Double), distance (Double), time (Double).
 */
class SpeedCalculatorServiceImpl implements CalculatorService<SpeedCalculatorForm, SpeedCalculatorResult> {

    private SpeedCalculatorServiceImpl() {}

    static SpeedCalculatorServiceImpl newInstance() {
        return new SpeedCalculatorServiceImpl();
    }

    @Override
    public SpeedCalculatorResult calculate(SpeedCalculatorForm form) {
        try {
            SolveFor solve = form.solve();
            if (solve == null) {
                return SpeedCalculatorResult.error("solve is required (SPEED, DISTANCE, or TIME)");
            }

            switch (solve) {
                case SPEED: {
                    double distance = requirePositive(form.distance(), "distance");
                    double time     = requirePositive(form.time(), "time");
                    return SpeedCalculatorResult.success(SolveFor.SPEED, distance / time);
                }
                case DISTANCE: {
                    double speed = requirePositive(form.speed(), "speed");
                    double time  = requirePositive(form.time(), "time");
                    return SpeedCalculatorResult.success(SolveFor.DISTANCE, speed * time);
                }
                case TIME: {
                    double speed    = requirePositive(form.speed(), "speed");
                    double distance = requirePositive(form.distance(), "distance");
                    if (speed == 0) {
                        return SpeedCalculatorResult.error("speed cannot be zero when solving for TIME");
                    }
                    return SpeedCalculatorResult.success(SolveFor.TIME, distance / speed);
                }
                default:
                    return SpeedCalculatorResult.error("Unknown solve target: " + solve);
            }

        } catch (IllegalArgumentException e) {
            return SpeedCalculatorResult.error(e.getMessage());
        } catch (Exception e) {
            return SpeedCalculatorResult.error(e.getMessage());
        }
    }

    private double requirePositive(Double value, String fieldName) {
        if (value == null) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
        if (value < 0) {
            throw new IllegalArgumentException(fieldName + " cannot be negative");
        }
        return value;
    }
}
