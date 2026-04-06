# GSD v1.0 — Universal JavaFX Calculator Suite
# Global State Document | Phase A.1 Output | solution-architect
# Date: 2026-04-06 | Status: APPROVED (pending consensus-agent)

---

## 1. Maven Project Structure

```
javafx-universal-calculator/
├── pom.xml
├── README.md
├── .gitignore
├── docs/
│   └── GSD-v1.0.md                          ← This file
└── src/
    └── main/
        ├── java/
        │   ├── module-info.java
        │   └── com/techdeveloper/calculator/
        │       ├── UniversalCalculatorApp.java       ← Application entry point
        │       ├── ViewRouter.java                   ← Singleton FXML loader
        │       ├── controller/
        │       │   ├── BasicCalculatorController.java
        │       │   ├── ScientificCalculatorController.java
        │       │   ├── ProgrammerCalculatorController.java
        │       │   ├── EMICalculatorController.java
        │       │   ├── BMICalculatorController.java
        │       │   ├── AgeCalculatorController.java
        │       │   ├── DateDiffCalculatorController.java
        │       │   ├── CurrencyCalculatorController.java
        │       │   ├── UnitConverterController.java
        │       │   ├── TipCalculatorController.java
        │       │   ├── DiscountCalculatorController.java
        │       │   ├── MatrixCalculatorController.java
        │       │   ├── StatisticsCalculatorController.java
        │       │   ├── SpeedCalculatorController.java
        │       │   └── FuelCalculatorController.java
        │       └── service/
        │           ├── CalculatorService.java          ← Interface
        │           ├── CalculatorType.java             ← Enum
        │           ├── ServiceFactory.java             ← Singleton factory
        │           ├── BasicCalculatorService.java
        │           ├── ScientificCalculatorService.java
        │           ├── ProgrammerCalculatorService.java
        │           ├── EMICalculatorService.java
        │           ├── BMICalculatorService.java
        │           ├── AgeCalculatorService.java
        │           ├── DateDiffCalculatorService.java
        │           ├── CurrencyCalculatorService.java
        │           ├── UnitConverterService.java
        │           ├── TipCalculatorService.java
        │           ├── DiscountCalculatorService.java
        │           ├── MatrixCalculatorService.java
        │           ├── StatisticsCalculatorService.java
        │           ├── SpeedCalculatorService.java
        │           └── FuelCalculatorService.java
        └── resources/
            ├── fxml/
            │   ├── main.fxml                           ← Root: BorderPane + MenuBar
            │   ├── basic-calculator.fxml
            │   ├── scientific-calculator.fxml
            │   ├── programmer-calculator.fxml
            │   ├── emi-calculator.fxml
            │   ├── bmi-calculator.fxml
            │   ├── age-calculator.fxml
            │   ├── date-diff-calculator.fxml
            │   ├── currency-calculator.fxml
            │   ├── unit-converter.fxml
            │   ├── tip-calculator.fxml
            │   ├── discount-calculator.fxml
            │   ├── matrix-calculator.fxml
            │   ├── statistics-calculator.fxml
            │   ├── speed-calculator.fxml
            │   └── fuel-calculator.fxml
            └── css/
                └── dark-theme.css
```

---

## 2. FXML → Controller → Service Mapping Table

| # | Calculator       | FXML File                      | Controller Class                    | Service Class                    |
|---|-----------------|-------------------------------|-------------------------------------|----------------------------------|
| 1 | Basic           | basic-calculator.fxml          | BasicCalculatorController           | BasicCalculatorService           |
| 2 | Scientific      | scientific-calculator.fxml     | ScientificCalculatorController      | ScientificCalculatorService      |
| 3 | Programmer      | programmer-calculator.fxml     | ProgrammerCalculatorController      | ProgrammerCalculatorService      |
| 4 | EMI / Financial | emi-calculator.fxml            | EMICalculatorController             | EMICalculatorService             |
| 5 | BMI / Health    | bmi-calculator.fxml            | BMICalculatorController             | BMICalculatorService             |
| 6 | Age             | age-calculator.fxml            | AgeCalculatorController             | AgeCalculatorService             |
| 7 | Date Difference | date-diff-calculator.fxml      | DateDiffCalculatorController        | DateDiffCalculatorService        |
| 8 | Currency        | currency-calculator.fxml       | CurrencyCalculatorController        | CurrencyCalculatorService        |
| 9 | Unit Converter  | unit-converter.fxml            | UnitConverterController             | UnitConverterService             |
| 10| Tip             | tip-calculator.fxml            | TipCalculatorController             | TipCalculatorService             |
| 11| Discount        | discount-calculator.fxml       | DiscountCalculatorController        | DiscountCalculatorService        |
| 12| Matrix          | matrix-calculator.fxml         | MatrixCalculatorController          | MatrixCalculatorService          |
| 13| Statistics      | statistics-calculator.fxml     | StatisticsCalculatorController      | StatisticsCalculatorService      |
| 14| Speed/Dist/Time | speed-calculator.fxml          | SpeedCalculatorController           | SpeedCalculatorService           |
| 15| Fuel Efficiency | fuel-calculator.fxml           | FuelCalculatorController            | FuelCalculatorService            |

---

## 3. CalculatorService Interface Definition

```java
package com.techdeveloper.calculator.service;

import java.util.Map;

/**
 * Universal interface for all calculator service implementations.
 * Every calculator type implements this interface.
 * The calculate() method accepts named inputs and returns a formatted result string.
 * On any error (divide-by-zero, empty input, invalid format), returns "Error: <message>"
 * and NEVER throws an exception to the calling Controller.
 */
public interface CalculatorService {

    /**
     * Perform the calculation based on the given named inputs.
     *
     * @param inputs Map of input field name → string value (e.g., "principal" → "10000")
     * @return Formatted result string, or "Error: <specific reason>" on failure
     */
    String calculate(Map<String, String> inputs);
}
```

---

## 4. CalculatorType Enum Definition

```java
package com.techdeveloper.calculator.service;

/**
 * Enum representing every supported calculator type.
 * Used by ServiceFactory to return the correct service instance.
 */
public enum CalculatorType {
    BASIC,
    SCIENTIFIC,
    PROGRAMMER,
    EMI,
    BMI,
    AGE,
    DATE_DIFF,
    CURRENCY,
    UNIT_CONVERTER,
    TIP,
    DISCOUNT,
    MATRIX,
    STATISTICS,
    SPEED,
    FUEL
}
```

---

## 5. ServiceFactory Pattern

```java
// Singleton factory — returns pre-instantiated service instances
// Pattern: Eager initialization (all services created at startup — lightweight, no Spring)
public class ServiceFactory {
    private static final ServiceFactory INSTANCE = new ServiceFactory();
    private final Map<CalculatorType, CalculatorService> services;

    private ServiceFactory() {
        services = new EnumMap<>(CalculatorType.class);
        services.put(CalculatorType.BASIC,         new BasicCalculatorService());
        services.put(CalculatorType.SCIENTIFIC,    new ScientificCalculatorService());
        // ... all 15 types
    }

    public static ServiceFactory getInstance() { return INSTANCE; }
    public CalculatorService getService(CalculatorType type) { return services.get(type); }
}
```

---

## 6. ViewRouter Pattern

```java
// Singleton — manages the main BorderPane center region
// Called by every Menu Bar action handler
public class ViewRouter {
    private static ViewRouter instance;
    private BorderPane mainLayout;

    public static ViewRouter getInstance() { ... }

    public void setMainLayout(BorderPane layout) {
        this.mainLayout = layout;
    }

    /**
     * Load an FXML file and set it as the center of the main BorderPane.
     * @param fxmlPath e.g., "/fxml/basic-calculator.fxml"
     */
    public void loadView(String fxmlPath) {
        try {
            Parent view = FXMLLoader.load(getClass().getResource(fxmlPath));
            mainLayout.setCenter(view);
        } catch (IOException e) {
            // Show user-facing error dialog
            showError("Failed to load view: " + fxmlPath, e.getMessage());
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Navigation Error");
        alert.setHeaderText(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
```

---

## 7. Main FXML Menu Bar Structure

```xml
<!-- main.fxml — root layout -->
<BorderPane fx:id="mainLayout">
  <top>
    <MenuBar>
      <Menu text="Basic">
        <MenuItem fx:id="menuBasic"       text="Basic Calculator"        onAction="#loadBasic"/>
      </Menu>
      <Menu text="Science">
        <MenuItem fx:id="menuScientific"  text="Scientific Calculator"   onAction="#loadScientific"/>
        <MenuItem fx:id="menuProgrammer"  text="Programmer Calculator"   onAction="#loadProgrammer"/>
        <MenuItem fx:id="menuMatrix"      text="Matrix Calculator"       onAction="#loadMatrix"/>
        <MenuItem fx:id="menuStatistics"  text="Statistics Calculator"   onAction="#loadStatistics"/>
      </Menu>
      <Menu text="Finance">
        <MenuItem fx:id="menuEMI"         text="EMI / Loan Calculator"   onAction="#loadEMI"/>
        <MenuItem fx:id="menuCurrency"    text="Currency Converter"      onAction="#loadCurrency"/>
        <MenuItem fx:id="menuTip"         text="Tip Calculator"          onAction="#loadTip"/>
        <MenuItem fx:id="menuDiscount"    text="Discount Calculator"     onAction="#loadDiscount"/>
      </Menu>
      <Menu text="Health">
        <MenuItem fx:id="menuBMI"         text="BMI Calculator"          onAction="#loadBMI"/>
        <MenuItem fx:id="menuAge"         text="Age Calculator"          onAction="#loadAge"/>
      </Menu>
      <Menu text="Utilities">
        <MenuItem fx:id="menuDateDiff"    text="Date Difference"         onAction="#loadDateDiff"/>
        <MenuItem fx:id="menuUnit"        text="Unit Converter"          onAction="#loadUnit"/>
        <MenuItem fx:id="menuSpeed"       text="Speed / Distance / Time" onAction="#loadSpeed"/>
        <MenuItem fx:id="menuFuel"        text="Fuel Efficiency"         onAction="#loadFuel"/>
      </Menu>
    </MenuBar>
  </top>
  <!-- center: loaded dynamically by ViewRouter -->
</BorderPane>
```

**Menu Groups:**
- **Basic** → Basic Calculator
- **Science** → Scientific, Programmer, Matrix, Statistics
- **Finance** → EMI, Currency, Tip, Discount
- **Health** → BMI, Age
- **Utilities** → Date Diff, Unit Converter, Speed, Fuel

---

## 8. CSS Dark Theme Token List

```css
/* dark-theme.css — Design Tokens */

/* ── Colors ─────────────────────────────── */
--bg-primary:    #1e1e1e;   /* main window background */
--bg-secondary:  #2d2d2d;   /* panel / card background */
--bg-button:     #3a3a3a;   /* button default */
--bg-hover:      #4a4a4a;   /* button hover */
--bg-active:     #5a5a5a;   /* button pressed */
--bg-input:      #252525;   /* text field background */
--accent:        #4a90d9;   /* primary action, equals button */
--accent-hover:  #5ba3f0;   /* accent hover */
--text-primary:  #e0e0e0;   /* main text */
--text-secondary:#9e9e9e;   /* labels, hints */
--text-result:   #ffffff;   /* result display */
--border:        #404040;   /* input borders */
--error:         #e74c3c;   /* error state */
--success:       #2ecc71;   /* success / positive result */

/* ── Typography ──────────────────────────── */
--font-primary:  "Segoe UI", "Inter", system-ui, sans-serif;
--font-mono:     "Consolas", "Roboto Mono", monospace;  /* programmer calc display */
--font-size-sm:  12px;
--font-size-md:  14px;
--font-size-lg:  18px;
--font-size-xl:  28px;   /* result display */

/* ── Spacing (8-point grid) ──────────────── */
--spacing-xs:    4px;
--spacing-sm:    8px;
--spacing-md:    16px;
--spacing-lg:    24px;
--spacing-xl:    32px;

/* ── Border Radius ───────────────────────── */
--radius-sm:     4px;
--radius-md:     8px;
--radius-lg:     12px;

/* ── Window ──────────────────────────────── */
--window-width:  900px;
--window-height: 650px;
--min-width:     750px;
--min-height:    500px;
```

---

## 9. Threading Strategy

| Operation | Thread Strategy | Reason |
|---|---|---|
| Basic arithmetic (+,-,*,/) | **Direct (FX Thread)** | Instant, < 1ms |
| Scientific (sin/cos/log/factorial) | **Direct (FX Thread)** | Instant, < 1ms |
| Programmer (bitwise/base conversion) | **Direct (FX Thread)** | Instant, < 1ms |
| EMI, BMI, Age, Date, Tip, Discount | **Direct (FX Thread)** | Simple formula, < 5ms |
| Currency, Unit Converter | **Direct (FX Thread)** | Static map lookup, < 1ms |
| Speed, Fuel | **Direct (FX Thread)** | Simple arithmetic, < 1ms |
| **Matrix multiply (3×3)** | **Task\<String\> + Platform.runLater()** | Could be slow for large matrices |
| **Statistics (large dataset)** | **Task\<String\> + Platform.runLater()** | Sorting + iteration could be slow |

**Rule:** Any operation that could take > 16ms (one UI frame) MUST use `Task<String>`.
Every `Task` MUST have `.setOnFailed()` handler → show `Alert.AlertType.ERROR`.

---

## 10. Error Handling Strategy

| Error Type | Handling Rule | User-Facing Message |
|---|---|---|
| Empty input field | Check before calculation, return early | "Error: Please fill all required fields" |
| Divide by zero | Catch `ArithmeticException` | "Error: Division by zero" |
| Invalid number format | Catch `NumberFormatException` | "Error: Invalid number format" |
| Null input in Map | Catch `NullPointerException` | "Error: Missing input value" |
| Negative value where invalid | Explicit check | "Error: Value must be positive" |
| Date parsing failure | Catch `DateTimeException` | "Error: Invalid date selection" |
| FXML load failure | `ViewRouter.showError()` | Shows Alert dialog with file path |
| Task failure | `.setOnFailed()` → `Alert.ERROR` | Shows exception message in dialog |

**Golden Rule:** Services NEVER throw exceptions to Controllers.
Services ALWAYS return `"Error: <specific message>"` string on failure.
Controllers check if result starts with `"Error:"` and show it in red font on result Label.

---

## 11. Controller Pattern (Standard Template)

```java
public class BasicCalculatorController {
    @FXML private TextField displayField;
    @FXML private Label resultLabel;
    // ... other @FXML fields

    private final CalculatorService service =
        ServiceFactory.getInstance().getService(CalculatorType.BASIC);

    @FXML
    private void handleCalculate(ActionEvent event) {
        Map<String, String> inputs = new HashMap<>();
        inputs.put("expression", displayField.getText().trim());

        String result = service.calculate(inputs);

        if (result.startsWith("Error:")) {
            resultLabel.setStyle("-fx-text-fill: #e74c3c;");  // red
        } else {
            resultLabel.setStyle("-fx-text-fill: #2ecc71;");  // green
        }
        resultLabel.setText(result);
    }
}
```

---

## 12. ADR (Architecture Decision Records)

### ADR-001: Plain Java (No Spring)
- **Decision**: Use no Spring/DI framework — plain Java Singleton pattern
- **Reason**: Desktop app doesn't need Spring overhead; keeps startup fast
- **Impact**: ServiceFactory.java replaces Spring @Autowired injection

### ADR-002: Static Currency Rates
- **Decision**: Use hardcoded `Map<String, Double>` for currency rates vs USD
- **Reason**: No internet dependency; keeps app fully offline
- **Impact**: Rates won't be real-time; acceptable trade-off for offline-first design

### ADR-003: FXML Over Pure Java DSL
- **Decision**: Use FXML + Scene Builder layouting for all views
- **Reason**: Clean MVC separation; FXML = View, Java = Behavior
- **Impact**: Each calculator needs a separate .fxml file (15 fxml files)

### ADR-004: BorderPane as Root Layout
- **Decision**: Single `BorderPane` — MenuBar at top, calculator view swapped at center
- **Reason**: Standard IDE-style layout; ViewRouter only updates `.center`
- **Impact**: Consistent frame; no screen flash on calculator switch

### ADR-005: Dark Theme Mandatory
- **Decision**: CSS dark theme applied at Application level — no light mode toggle
- **Reason**: Professional look; all design tokens defined once in dark-theme.css
- **Impact**: All button/label/field colors come from CSS, not hardcoded Java

---

## GSD Status

| Section | Status |
|---|---|
| Project Structure | ✅ COMPLETE |
| FXML-Controller-Service Map (15) | ✅ COMPLETE |
| CalculatorService Interface | ✅ COMPLETE |
| CalculatorType Enum | ✅ COMPLETE |
| ServiceFactory Pattern | ✅ COMPLETE |
| ViewRouter Pattern | ✅ COMPLETE |
| Menu Bar Structure | ✅ COMPLETE |
| CSS Token List | ✅ COMPLETE |
| Threading Strategy | ✅ COMPLETE |
| Error Handling Strategy | ✅ COMPLETE |
| Controller Pattern Template | ✅ COMPLETE |
| ADRs (5) | ✅ COMPLETE |

**GSD Version**: 1.0
**Produced by**: solution-architect (Phase A.1)
**Next**: consensus-agent validation (Phase A.2) → Phase B unlock
