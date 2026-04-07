package com.techdeveloper.calculator.controller.helper;

import com.techdeveloper.calculator.dto.UnitConverterResult;

/**
 * Helper base class for UnitConverterController.
 * Contains utility methods extracted to keep the controller's onConvert method ≤ 35 lines.
 * All methods are protected for use by the controller subclass.
 */
public class UnitConverterHelper {

    protected String formatConversionResult(double value, UnitConverterResult result) {
        return String.format("%.6g %s = %.6g %s",
                value, result.fromUnit(), result.result(), result.toUnit());
    }
}
