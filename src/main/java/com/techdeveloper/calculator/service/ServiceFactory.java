package com.techdeveloper.calculator.service;

import java.util.EnumMap;
import java.util.Map;

/**
 * Singleton factory — returns pre-instantiated service instances.
 * Pattern: Eager initialization (all services created at startup — lightweight, no Spring).
 * Phase B will register concrete service implementations for each CalculatorType.
 */
public class ServiceFactory {

    private static final ServiceFactory INSTANCE = new ServiceFactory();
    private final Map<CalculatorType, CalculatorService> services;

    private ServiceFactory() {
        services = new EnumMap<>(CalculatorType.class);
        services.put(CalculatorType.BASIC,          new BasicCalculatorService());
        services.put(CalculatorType.SCIENTIFIC,     new ScientificCalculatorService());
        services.put(CalculatorType.PROGRAMMER,     new ProgrammerCalculatorService());
        services.put(CalculatorType.EMI,            new EMICalculatorService());
        services.put(CalculatorType.BMI,            new BMICalculatorService());
        services.put(CalculatorType.AGE,            new AgeCalculatorService());
        services.put(CalculatorType.DATE_DIFF,      new DateDiffCalculatorService());
        services.put(CalculatorType.CURRENCY,       new CurrencyCalculatorService());
        services.put(CalculatorType.UNIT_CONVERTER, new UnitConverterService());
        services.put(CalculatorType.TIP,            new TipCalculatorService());
        services.put(CalculatorType.DISCOUNT,       new DiscountCalculatorService());
        services.put(CalculatorType.MATRIX,         new MatrixCalculatorService());
        services.put(CalculatorType.STATISTICS,     new StatisticsCalculatorService());
        services.put(CalculatorType.SPEED,          new SpeedCalculatorService());
        services.put(CalculatorType.FUEL,           new FuelCalculatorService());
    }

    public static ServiceFactory getInstance() {
        return INSTANCE;
    }

    /**
     * Returns the service for the given calculator type.
     *
     * @param type the calculator type
     * @return the corresponding CalculatorService implementation
     * @throws IllegalArgumentException if no service is registered for the type
     */
    public CalculatorService getService(CalculatorType type) {
        CalculatorService service = services.get(type);
        if (service == null) {
            throw new IllegalArgumentException("No service registered for type: " + type);
        }
        return service;
    }
}
