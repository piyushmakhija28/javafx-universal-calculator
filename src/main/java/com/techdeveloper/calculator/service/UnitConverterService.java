package com.techdeveloper.calculator.service;

import java.util.HashMap;
import java.util.Map;

/**
 * Implements Unit Converter for 6 categories.
 * Required inputs: "value", "fromUnit", "toUnit", "category"
 * Categories: LENGTH, WEIGHT, TEMPERATURE, AREA, VOLUME, SPEED
 * All non-temperature categories use a base-unit conversion pattern.
 * Temperature uses direct formulas (C, F, K).
 */
public class UnitConverterService implements CalculatorService {

    public UnitConverterService() {
        // required for FXML
    }

    // LENGTH base unit: meters
    private static final Map<String, Double> LENGTH_TO_METERS = new HashMap<>();
    // WEIGHT base unit: kilograms
    private static final Map<String, Double> WEIGHT_TO_KG = new HashMap<>();
    // AREA base unit: square meters
    private static final Map<String, Double> AREA_TO_SQM = new HashMap<>();
    // VOLUME base unit: liters
    private static final Map<String, Double> VOLUME_TO_LITERS = new HashMap<>();
    // SPEED base unit: meters/second
    private static final Map<String, Double> SPEED_TO_MPS = new HashMap<>();

    static {
        LENGTH_TO_METERS.put("MM", 0.001);
        LENGTH_TO_METERS.put("CM", 0.01);
        LENGTH_TO_METERS.put("M", 1.0);
        LENGTH_TO_METERS.put("KM", 1000.0);
        LENGTH_TO_METERS.put("INCH", 0.0254);
        LENGTH_TO_METERS.put("FOOT", 0.3048);
        LENGTH_TO_METERS.put("YARD", 0.9144);
        LENGTH_TO_METERS.put("MILE", 1609.344);

        WEIGHT_TO_KG.put("MG", 0.000001);
        WEIGHT_TO_KG.put("G", 0.001);
        WEIGHT_TO_KG.put("KG", 1.0);
        WEIGHT_TO_KG.put("TONNE", 1000.0);
        WEIGHT_TO_KG.put("LB", 0.453592);
        WEIGHT_TO_KG.put("OZ", 0.0283495);
        WEIGHT_TO_KG.put("STONE", 6.35029);

        AREA_TO_SQM.put("SQM", 1.0);
        AREA_TO_SQM.put("SQM2", 1.0);
        AREA_TO_SQM.put("SQKM", 1_000_000.0);
        AREA_TO_SQM.put("SQFT", 0.092903);
        AREA_TO_SQM.put("SQMILE", 2_589_988.11);
        AREA_TO_SQM.put("HECTARE", 10_000.0);
        AREA_TO_SQM.put("ACRE", 4046.856);

        VOLUME_TO_LITERS.put("ML", 0.001);
        VOLUME_TO_LITERS.put("L", 1.0);
        VOLUME_TO_LITERS.put("CUBICM", 1000.0);
        VOLUME_TO_LITERS.put("GALLON", 3.78541);
        VOLUME_TO_LITERS.put("QUART", 0.946353);
        VOLUME_TO_LITERS.put("PINT", 0.473176);
        VOLUME_TO_LITERS.put("FLOZ", 0.0295735);
        VOLUME_TO_LITERS.put("CUBICCM", 0.001);

        SPEED_TO_MPS.put("MPS", 1.0);
        SPEED_TO_MPS.put("KPH", 0.277778);
        SPEED_TO_MPS.put("MPH", 0.44704);
        SPEED_TO_MPS.put("KNOT", 0.514444);
        SPEED_TO_MPS.put("FPS", 0.3048);
    }

    @Override
    public String calculate(Map<String, String> inputs) {
        try {
            String valueStr = inputs.get("value");
            String fromUnit = inputs.get("fromUnit");
            String toUnit = inputs.get("toUnit");
            String category = inputs.get("category");

            if (valueStr == null || valueStr.trim().isEmpty()) {
                return "Error: value is required";
            }
            if (fromUnit == null || fromUnit.trim().isEmpty()) {
                return "Error: fromUnit is required";
            }
            if (toUnit == null || toUnit.trim().isEmpty()) {
                return "Error: toUnit is required";
            }
            if (category == null || category.trim().isEmpty()) {
                return "Error: category is required (LENGTH, WEIGHT, TEMPERATURE, AREA, VOLUME, SPEED)";
            }

            double value = Double.parseDouble(valueStr.trim());
            String from = fromUnit.trim().toUpperCase();
            String to = toUnit.trim().toUpperCase();
            String cat = category.trim().toUpperCase();

            switch (cat) {
                case "TEMPERATURE": return convertTemperature(value, from, to);
                case "LENGTH":      return convertBaseUnit(value, from, to, LENGTH_TO_METERS, "LENGTH");
                case "WEIGHT":      return convertBaseUnit(value, from, to, WEIGHT_TO_KG, "WEIGHT");
                case "AREA":        return convertBaseUnit(value, from, to, AREA_TO_SQM, "AREA");
                case "VOLUME":      return convertBaseUnit(value, from, to, VOLUME_TO_LITERS, "VOLUME");
                case "SPEED":       return convertBaseUnit(value, from, to, SPEED_TO_MPS, "SPEED");
                default:
                    return "Error: Unknown category: " + cat + ". Use LENGTH, WEIGHT, TEMPERATURE, AREA, VOLUME, or SPEED";
            }

        } catch (NumberFormatException e) {
            return "Error: Invalid number format";
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }

    private String convertTemperature(double value, String from, String to) {
        double celsius;
        switch (from) {
            case "C":  celsius = value; break;
            case "F":  celsius = (value - 32) * 5.0 / 9.0; break;
            case "K":  celsius = value - 273.15; break;
            default:   return "Error: Unknown temperature unit: " + from + ". Use C, F, or K";
        }

        double result;
        switch (to) {
            case "C":  result = celsius; break;
            case "F":  result = celsius * 9.0 / 5.0 + 32; break;
            case "K":  result = celsius + 273.15; break;
            default:   return "Error: Unknown temperature unit: " + to + ". Use C, F, or K";
        }

        return formatConversion(value, from, result, to);
    }

    private String convertBaseUnit(double value, String from, String to,
                                   Map<String, Double> table, String categoryName) {
        if (!table.containsKey(from)) {
            return "Error: Unknown " + categoryName + " unit: " + from;
        }
        if (!table.containsKey(to)) {
            return "Error: Unknown " + categoryName + " unit: " + to;
        }
        double baseValue = value * table.get(from);
        double result = baseValue / table.get(to);
        return formatConversion(value, from, result, to);
    }

    private String formatConversion(double inputValue, String fromUnit, double result, String toUnit) {
        // Use integer format if result is a whole number and within safe long range
        if (result == Math.floor(result) && !Double.isInfinite(result) && Math.abs(result) < 1e15) {
            return String.format("%.6g %s = %d %s", inputValue, fromUnit, (long) result, toUnit);
        }
        return String.format("%.6g %s = %.6g %s", inputValue, fromUnit, result, toUnit);
    }
}
