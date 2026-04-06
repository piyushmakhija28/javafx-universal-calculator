package com.techdeveloper.calculator.service.impl;

import com.techdeveloper.calculator.constants.CalculatorType;
import com.techdeveloper.calculator.service.CalculatorService;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Set;

/**
 * O(1) HashMap-based registry of all CalculatorService implementations.
 * Uses EnumMap for maximum performance (array-backed, no hashing overhead).
 * Design Pattern: Registry (variant of Factory + Strategy).
 * DSA: EnumMap over if-else chain — O(1) lookup vs O(n) linear scan.
 */
class ServiceRegistry {

    private final Map<CalculatorType, CalculatorService<?, ?>> registry =
            new EnumMap<>(CalculatorType.class);

    void register(CalculatorType type, CalculatorService<?, ?> service) {
        registry.put(type, service);
    }

    @SuppressWarnings("unchecked")
    <F, R> CalculatorService<F, R> get(CalculatorType type) {
        CalculatorService<?, ?> service = registry.get(type);
        if (service == null) {
            throw new IllegalArgumentException("No service registered for CalculatorType: " + type);
        }
        return (CalculatorService<F, R>) service;
    }

    Set<CalculatorType> registeredTypes() {
        return Collections.unmodifiableSet(registry.keySet());
    }
}
