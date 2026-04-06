package com.techdeveloper.calculator.service.impl;

import com.techdeveloper.calculator.dto.FuelCalculatorResult;
import com.techdeveloper.calculator.form.FuelCalculatorForm;
import com.techdeveloper.calculator.service.CalculatorService;

/**
 * Package-private implementation of Fuel Efficiency Calculator.
 * Form fields: distance (double, km), fuelUsed (double, liters), fuelPrice (Double, nullable per liter).
 */
class FuelCalculatorServiceImpl implements CalculatorService<FuelCalculatorForm, FuelCalculatorResult> {

    private FuelCalculatorServiceImpl() {}

    static FuelCalculatorServiceImpl newInstance() {
        return new FuelCalculatorServiceImpl();
    }

    @Override
    public FuelCalculatorResult calculate(FuelCalculatorForm form) {
        try {
            double distance  = form.distance();
            double fuelUsed  = form.fuelUsed();
            Double fuelPrice = form.fuelPrice();

            if (distance <= 0) {
                return FuelCalculatorResult.error("distance must be positive");
            }
            if (fuelUsed <= 0) {
                return FuelCalculatorResult.error("fuelUsed must be positive");
            }

            double litersPer100km = (fuelUsed / distance) * 100.0;
            double kmPerLiter     = distance / fuelUsed;

            if (fuelPrice != null) {
                if (fuelPrice < 0) {
                    return FuelCalculatorResult.error("fuelPrice cannot be negative");
                }
                double totalCost  = fuelUsed * fuelPrice;
                double costPerKm  = totalCost / distance;
                return FuelCalculatorResult.success(litersPer100km, kmPerLiter, costPerKm, totalCost);
            }

            return FuelCalculatorResult.success(litersPer100km, kmPerLiter, null, null);

        } catch (Exception e) {
            return FuelCalculatorResult.error(e.getMessage());
        }
    }
}
