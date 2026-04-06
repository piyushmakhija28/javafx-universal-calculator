package com.techdeveloper.calculator.service.impl;

import com.techdeveloper.calculator.constants.UnitCategory;
import com.techdeveloper.calculator.dto.UnitConverterResult;
import com.techdeveloper.calculator.form.UnitConverterForm;
import com.techdeveloper.calculator.service.CalculatorService;

import java.util.HashMap;
import java.util.Map;

/**
 * Package-private implementation of Unit Converter for 6 categories.
 * Form fields: value (double), fromUnit (String), toUnit (String), category (UnitCategory).
 */
class UnitConverterServiceImpl implements CalculatorService<UnitConverterForm, UnitConverterResult> {

    private UnitConverterServiceImpl() {}

    static UnitConverterServiceImpl newInstance() {
        return new UnitConverterServiceImpl();
    }

    private static final Map<String, Double> LENGTH_TO_METERS  = new HashMap<>();
    private static final Map<String, Double> WEIGHT_TO_KG      = new HashMap<>();
    private static final Map<String, Double> AREA_TO_SQM       = new HashMap<>();
    private static final Map<String, Double> VOLUME_TO_LITERS  = new HashMap<>();
    private static final Map<String, Double> SPEED_TO_MPS      = new HashMap<>();

    static {
        LENGTH_TO_METERS.put("MM", 0.001);    LENGTH_TO_METERS.put("CM", 0.01);
        LENGTH_TO_METERS.put("M", 1.0);       LENGTH_TO_METERS.put("KM", 1000.0);
        LENGTH_TO_METERS.put("INCH", 0.0254); LENGTH_TO_METERS.put("FOOT", 0.3048);
        LENGTH_TO_METERS.put("YARD", 0.9144); LENGTH_TO_METERS.put("MILE", 1609.344);

        WEIGHT_TO_KG.put("MG", 0.000001); WEIGHT_TO_KG.put("G", 0.001);
        WEIGHT_TO_KG.put("KG", 1.0);      WEIGHT_TO_KG.put("TONNE", 1000.0);
        WEIGHT_TO_KG.put("LB", 0.453592); WEIGHT_TO_KG.put("OZ", 0.0283495);
        WEIGHT_TO_KG.put("STONE", 6.35029);

        AREA_TO_SQM.put("SQM", 1.0);          AREA_TO_SQM.put("SQM2", 1.0);
        AREA_TO_SQM.put("SQKM", 1_000_000.0); AREA_TO_SQM.put("SQFT", 0.092903);
        AREA_TO_SQM.put("SQMILE", 2_589_988.11); AREA_TO_SQM.put("HECTARE", 10_000.0);
        AREA_TO_SQM.put("ACRE", 4046.856);

        VOLUME_TO_LITERS.put("ML", 0.001);      VOLUME_TO_LITERS.put("L", 1.0);
        VOLUME_TO_LITERS.put("CUBICM", 1000.0); VOLUME_TO_LITERS.put("GALLON", 3.78541);
        VOLUME_TO_LITERS.put("QUART", 0.946353); VOLUME_TO_LITERS.put("PINT", 0.473176);
        VOLUME_TO_LITERS.put("FLOZ", 0.0295735); VOLUME_TO_LITERS.put("CUBICCM", 0.001);

        SPEED_TO_MPS.put("MPS", 1.0);    SPEED_TO_MPS.put("KPH", 0.277778);
        SPEED_TO_MPS.put("MPH", 0.44704); SPEED_TO_MPS.put("KNOT", 0.514444);
        SPEED_TO_MPS.put("FPS", 0.3048);
    }

    @Override
    public UnitConverterResult calculate(UnitConverterForm form) {
        try {
            double       value    = form.value();
            String       from     = form.fromUnit() != null ? form.fromUnit().trim().toUpperCase() : null;
            String       to       = form.toUnit()   != null ? form.toUnit().trim().toUpperCase()   : null;
            UnitCategory category = form.category();

            if (from == null || from.isEmpty()) return UnitConverterResult.error("fromUnit is required");
            if (to   == null || to.isEmpty())   return UnitConverterResult.error("toUnit is required");
            if (category == null)               return UnitConverterResult.error("category is required");

            switch (category) {
                case TEMPERATURE: return convertTemperature(value, from, to);
                case LENGTH:      return convertBaseUnit(value, from, to, LENGTH_TO_METERS, "LENGTH");
                case WEIGHT:      return convertBaseUnit(value, from, to, WEIGHT_TO_KG, "WEIGHT");
                case AREA:        return convertBaseUnit(value, from, to, AREA_TO_SQM, "AREA");
                case VOLUME:      return convertBaseUnit(value, from, to, VOLUME_TO_LITERS, "VOLUME");
                case SPEED:       return convertBaseUnit(value, from, to, SPEED_TO_MPS, "SPEED");
                default:
                    return UnitConverterResult.error("Unknown category: " + category);
            }

        } catch (Exception e) {
            return UnitConverterResult.error(e.getMessage());
        }
    }

    private UnitConverterResult convertTemperature(double value, String from, String to) {
        double celsius;
        switch (from) {
            case "C": celsius = value; break;
            case "F": celsius = (value - 32) * 5.0 / 9.0; break;
            case "K": celsius = value - 273.15; break;
            default:  return UnitConverterResult.error("Unknown temperature unit: " + from + ". Use C, F, or K");
        }
        double result;
        switch (to) {
            case "C": result = celsius; break;
            case "F": result = celsius * 9.0 / 5.0 + 32; break;
            case "K": result = celsius + 273.15; break;
            default:  return UnitConverterResult.error("Unknown temperature unit: " + to + ". Use C, F, or K");
        }
        return UnitConverterResult.success(result, from, to);
    }

    private UnitConverterResult convertBaseUnit(double value, String from, String to,
                                                Map<String, Double> table, String categoryName) {
        if (!table.containsKey(from)) return UnitConverterResult.error("Unknown " + categoryName + " unit: " + from);
        if (!table.containsKey(to))   return UnitConverterResult.error("Unknown " + categoryName + " unit: " + to);
        double baseValue = value * table.get(from);
        double result    = baseValue / table.get(to);
        return UnitConverterResult.success(result, from, to);
    }
}
