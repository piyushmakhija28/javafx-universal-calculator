package com.techdeveloper.calculator.service.impl;

import com.techdeveloper.calculator.constants.CalculatorType;
import com.techdeveloper.calculator.dto.*;
import com.techdeveloper.calculator.form.*;
import com.techdeveloper.calculator.service.CalculatorService;
import com.techdeveloper.calculator.service.HistoryService;
import com.techdeveloper.calculator.service.LiveCurrencyService;
import com.techdeveloper.calculator.service.PlotterService;

/**
 * Singleton factory — returns pre-instantiated typed service instances.
 * Pattern: Eager initialization (all services created at startup — lightweight, no Spring).
 * Concrete implementations are package-private; this factory is the sole public access point
 * for the service.impl package.
 *
 * Refactored: the 15 CalculatorService fields are replaced by a single ServiceRegistry
 * (EnumMap-backed, O(1) lookup). The three non-CalculatorService fields (historyService,
 * liveCurrencyService, plotterService) are kept as explicit fields because they do not
 * implement CalculatorService and are not keyed by CalculatorType.
 */
public class ServiceFactory {

    private static final ServiceFactory INSTANCE = new ServiceFactory();

    private final ServiceRegistry registry = new ServiceRegistry();

    private final HistoryService      historyService;
    private final LiveCurrencyService liveCurrencyService;
    private final PlotterService      plotterService;

    private ServiceFactory() {
        registry.register(CalculatorType.BASIC,          BasicCalculatorServiceImpl.newInstance());
        registry.register(CalculatorType.SCIENTIFIC,     ScientificCalculatorServiceImpl.newInstance());
        registry.register(CalculatorType.PROGRAMMER,     ProgrammerCalculatorServiceImpl.newInstance());
        registry.register(CalculatorType.EMI,            EMICalculatorServiceImpl.newInstance());
        registry.register(CalculatorType.BMI,            BMICalculatorServiceImpl.newInstance());
        registry.register(CalculatorType.AGE,            AgeCalculatorServiceImpl.newInstance());
        registry.register(CalculatorType.DATE_DIFF,      DateDiffCalculatorServiceImpl.newInstance());
        registry.register(CalculatorType.CURRENCY,       CurrencyCalculatorServiceImpl.newInstance());
        registry.register(CalculatorType.UNIT_CONVERTER, UnitConverterServiceImpl.newInstance());
        registry.register(CalculatorType.TIP,            TipCalculatorServiceImpl.newInstance());
        registry.register(CalculatorType.DISCOUNT,       DiscountCalculatorServiceImpl.newInstance());
        registry.register(CalculatorType.MATRIX,         MatrixCalculatorServiceImpl.newInstance());
        registry.register(CalculatorType.STATISTICS,     StatisticsCalculatorServiceImpl.newInstance());
        registry.register(CalculatorType.SPEED,          SpeedCalculatorServiceImpl.newInstance());
        registry.register(CalculatorType.FUEL,           FuelCalculatorServiceImpl.newInstance());

        historyService      = HistoryServiceImpl.getInstance();
        liveCurrencyService = LiveCurrencyServiceImpl.getInstance();
        plotterService      = PlotterServiceImpl.newInstance();
    }

    public static ServiceFactory getInstance() {
        return INSTANCE;
    }

    public CalculatorService<BasicCalculatorForm, BasicCalculatorResult> getBasicService() {
        return registry.get(CalculatorType.BASIC);
    }

    public CalculatorService<ScientificCalculatorForm, ScientificCalculatorResult> getScientificService() {
        return registry.get(CalculatorType.SCIENTIFIC);
    }

    public CalculatorService<ProgrammerCalculatorForm, ProgrammerCalculatorResult> getProgrammerService() {
        return registry.get(CalculatorType.PROGRAMMER);
    }

    public CalculatorService<EMICalculatorForm, EMICalculatorResult> getEmiService() {
        return registry.get(CalculatorType.EMI);
    }

    public CalculatorService<BMICalculatorForm, BMICalculatorResult> getBmiService() {
        return registry.get(CalculatorType.BMI);
    }

    public CalculatorService<AgeCalculatorForm, AgeCalculatorResult> getAgeService() {
        return registry.get(CalculatorType.AGE);
    }

    public CalculatorService<DateDiffCalculatorForm, DateDiffCalculatorResult> getDateDiffService() {
        return registry.get(CalculatorType.DATE_DIFF);
    }

    public CalculatorService<CurrencyCalculatorForm, CurrencyCalculatorResult> getCurrencyService() {
        return registry.get(CalculatorType.CURRENCY);
    }

    public CalculatorService<UnitConverterForm, UnitConverterResult> getUnitConverterService() {
        return registry.get(CalculatorType.UNIT_CONVERTER);
    }

    public CalculatorService<TipCalculatorForm, TipCalculatorResult> getTipService() {
        return registry.get(CalculatorType.TIP);
    }

    public CalculatorService<DiscountCalculatorForm, DiscountCalculatorResult> getDiscountService() {
        return registry.get(CalculatorType.DISCOUNT);
    }

    public CalculatorService<MatrixCalculatorForm, MatrixCalculatorResult> getMatrixService() {
        return registry.get(CalculatorType.MATRIX);
    }

    public CalculatorService<StatisticsCalculatorForm, StatisticsCalculatorResult> getStatisticsService() {
        return registry.get(CalculatorType.STATISTICS);
    }

    public CalculatorService<SpeedCalculatorForm, SpeedCalculatorResult> getSpeedService() {
        return registry.get(CalculatorType.SPEED);
    }

    public CalculatorService<FuelCalculatorForm, FuelCalculatorResult> getFuelService() {
        return registry.get(CalculatorType.FUEL);
    }

    public HistoryService getHistoryService() {
        return historyService;
    }

    public LiveCurrencyService getLiveCurrencyService() {
        return liveCurrencyService;
    }

    public PlotterService getPlotterService() {
        return plotterService;
    }
}
