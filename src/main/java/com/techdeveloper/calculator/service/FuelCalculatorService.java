package com.techdeveloper.calculator.service;

import java.util.Map;

/**
 * Implements Fuel Efficiency Calculator.
 * Required inputs: "distance" (km), "fuelUsed" (liters)
 * Optional input: "fuelPrice" (per liter, for cost calculations)
 * Returns: L/100km, km/L (fuel efficiency), and optionally cost/km and total cost.
 */
public class FuelCalculatorService implements CalculatorService {

    public FuelCalculatorService() {
        // required for FXML
    }

    @Override
    public String calculate(Map<String, String> inputs) {
        try {
            String distanceStr = inputs.get("distance");
            String fuelUsedStr = inputs.get("fuelUsed");

            if (distanceStr == null || distanceStr.trim().isEmpty()) {
                return "Error: distance is required (in km)";
            }
            if (fuelUsedStr == null || fuelUsedStr.trim().isEmpty()) {
                return "Error: fuelUsed is required (in liters)";
            }

            double distance = Double.parseDouble(distanceStr.trim());
            double fuelUsed = Double.parseDouble(fuelUsedStr.trim());

            if (distance <= 0) {
                return "Error: distance must be positive";
            }
            if (fuelUsed <= 0) {
                return "Error: fuelUsed must be positive";
            }

            double litersPer100km = (fuelUsed / distance) * 100.0;
            double kmPerLiter = distance / fuelUsed;

            String fuelPriceStr = inputs.get("fuelPrice");
            if (fuelPriceStr != null && !fuelPriceStr.trim().isEmpty()) {
                double fuelPrice = Double.parseDouble(fuelPriceStr.trim());
                if (fuelPrice < 0) {
                    return "Error: fuelPrice cannot be negative";
                }
                double totalCost = fuelUsed * fuelPrice;
                double costPerKm = totalCost / distance;
                return String.format(
                    "L/100km: %s | km/L: %s | Cost/km: %.2f | Total Cost: %.2f",
                    formatNum(litersPer100km), formatNum(kmPerLiter), costPerKm, totalCost
                );
            }

            return String.format("L/100km: %s | km/L: %s",
                    formatNum(litersPer100km), formatNum(kmPerLiter));

        } catch (NumberFormatException e) {
            return "Error: Invalid number format";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private String formatNum(double value) {
        if (value == Math.floor(value) && !Double.isInfinite(value) && Math.abs(value) < 1e15) {
            return String.valueOf((long) value);
        }
        double rounded = Math.round(value * 1e4) / 1e4;
        return String.valueOf(rounded);
    }
}
