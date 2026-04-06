# GSD v1.0 -- Universal JavaFX Calculator Suite
# Global State Document | Phase A.1 Output | solution-architect
# Date: 2026-04-06 | Status: APPROVED (pending consensus-agent)

---

## 1. Maven Project Structure

Files marked with `[A1]` exist after Phase A.1 scaffolding.
Files marked with `[B]` will be created in Phase B (per-calculator implementation).

```
Calculator/                                       [A1]
|-- pom.xml                                       [A1]
|-- README.md                                     [A1]
|-- .gitignore                                    [A1]
|-- docs/
|   +-- GSD-v1.0.md                              [A1] <-- This file
+-- src/
    +-- main/
        |-- java/
        |   |-- module-info.java                  [A1]
        |   +-- com/techdeveloper/calculator/
        |       |-- UniversalCalculatorApp.java   [A1] Application entry point
        |       |-- ViewRouter.java               [A1] Singleton FXML loader
        |       |-- controller/
        |       |   |-- MainAppController.java    [A1] Menu bar handler (main.fxml)
        |       |   |-- BasicCalculatorController.java        [B]
        |       |   |-- ScientificCalculatorController.java   [B]
        |       |   |-- ProgrammerCalculatorController.java   [B]
        |       |   |-- EMICalculatorController.java          [B]
        |       |   |-- BMICalculatorController.java          [B]
        |       |   |-- AgeCalculatorController.java          [B]
        |       |   |-- DateDiffCalculatorController.java     [B]
        |       |   |-- CurrencyCalculatorController.java     [B]
        |       |   |-- UnitConverterController.java          [B]
        |       |   |-- TipCalculatorController.java          [B]
        |       |   |-- DiscountCalculatorController.java     [B]
        |       |   |-- MatrixCalculatorController.java       [B]
        |       |   |-- StatisticsCalculatorController.java   [B]
        |       |   |-- SpeedCalculatorController.java        [B]
        |       |   +-- FuelCalculatorController.java         [B]
        |       +-- service/
        |           |-- CalculatorService.java    [A1] Interface
        |           |-- CalculatorType.java       [A1] Enum (15 types)
        |           |-- ServiceFactory.java       [A1] Singleton factory
        |           |-- BasicCalculatorService.java            [B]
        |           |-- ScientificCalculatorService.java       [B]
        |           |-- ProgrammerCalculatorService.java       [B]
        |           |-- EMICalculatorService.java              [B]
        |           |-- BMICalculatorService.java              [B]
        |           |-- AgeCalculatorService.java              [B]
        |           |-- DateDiffCalculatorService.java         [B]
        |           |-- CurrencyCalculatorService.java         [B]
        |           |-- UnitConverterService.java              [B]
        |           |-- TipCalculatorService.java              [B]
        |           |-- DiscountCalculatorService.java         [B]
        |           |-- MatrixCalculatorService.java           [B]
        |           |-- StatisticsCalculatorService.java       [B]
        |           |-- SpeedCalculatorService.java            [B]
        |           +-- FuelCalculatorService.java             [B]
        +-- resources/
            |-- fxml/
            |   |-- main.fxml                     [A1] Root: BorderPane + MenuBar
            |   |-- basic-calculator.fxml                      [B]
            |   |-- scientific-calculator.fxml                  [B]
            |   |-- programmer-calculator.fxml                  [B]
            |   |-- emi-calculator.fxml                         [B]
            |   |-- bmi-calculator.fxml                         [B]
            |   |-- age-calculator.fxml                         [B]
            |   |-- date-diff-calculator.fxml                   [B]
            |   |-- currency-calculator.fxml                    [B]
            |   |-- unit-converter.fxml                         [B]
            |   |-- tip-calculator.fxml                         [B]
            |   |-- discount-calculator.fxml                    [B]
            |   |-- matrix-calculator.fxml                      [B]
            |   |-- statistics-calculator.fxml                  [B]
            |   |-- speed-calculator.fxml                       [B]
            |   +-- fuel-calculator.fxml                        [B]
            +-- css/
                +-- dark-theme.css                [A1]
```

---

## 2. FXML -> Controller -> Service Mapping Table

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
     * @param inputs Map of input field name to string value (e.g., "principal" to "10000")
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
// Singleton factory -- returns pre-instantiated service instances
// Pattern: Eager initialization (all services created at startup -- lightweight, no Spring)
// Phase A1: EnumMap created but empty (no concrete services yet)
// Phase B: each concrete service registered via services.put(...)
public class ServiceFactory {
    private static final ServiceFactory INSTANCE = new ServiceFactory();
    private final Map<CalculatorType, CalculatorService> services;

    private ServiceFactory() {
        services = new EnumMap<>(CalculatorType.class);
        // Phase B: concrete service registrations go here
        // services.put(CalculatorType.BASIC,         new BasicCalculatorService());
        // services.put(CalculatorType.SCIENTIFIC,    new ScientificCalculatorService());
        // ... all 15 types
    }

    public static ServiceFactory getInstance() { return INSTANCE; }

    /**
     * Returns the service for the given calculator type.
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
```

---

## 6. ViewRouter Pattern

```java
// Singleton -- manages the main BorderPane center region
// Called by MainAppController for every menu item action
// Includes defensive null-check and NullPointerException guard
public class ViewRouter {
    private static ViewRouter instance;
    private BorderPane mainLayout;

    public static ViewRouter getInstance() {
        if (instance == null) {
            instance = new ViewRouter();
        }
        return instance;
    }

    public void setMainLayout(BorderPane layout) {
        this.mainLayout = layout;
    }

    /**
     * Load an FXML file and set it as the center of the main BorderPane.
     * @param fxmlPath e.g., "/fxml/basic-calculator.fxml"
     */
    public void loadView(String fxmlPath) {
        if (mainLayout == null) {
            showError("Router not initialized",
                      "setMainLayout() must be called before loadView().");
            return;
        }
        try {
            Parent view = FXMLLoader.load(getClass().getResource(fxmlPath));
            mainLayout.setCenter(view);
        } catch (IOException e) {
            showError("Failed to load view: " + fxmlPath, e.getMessage());
        } catch (NullPointerException e) {
            showError("FXML not found: " + fxmlPath,
                      "Check that the file exists in resources/fxml/");
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
<!-- main.fxml -- root layout -->
<!-- SeparatorMenuItem elements added between logical subgroups for visual clarity -->
<BorderPane fx:id="mainLayout"
            fx:controller="com.techdeveloper.calculator.controller.MainAppController"
            stylesheets="@../css/dark-theme.css">
  <top>
    <MenuBar styleClass="menu-bar">
      <Menu text="Basic">
        <MenuItem fx:id="menuBasic"       text="Basic Calculator"        onAction="#loadBasic"/>
      </Menu>
      <Menu text="Science">
        <MenuItem fx:id="menuScientific"  text="Scientific Calculator"   onAction="#loadScientific"/>
        <MenuItem fx:id="menuProgrammer"  text="Programmer Calculator"   onAction="#loadProgrammer"/>
        <SeparatorMenuItem/>
        <MenuItem fx:id="menuMatrix"      text="Matrix Calculator"       onAction="#loadMatrix"/>
        <MenuItem fx:id="menuStatistics"  text="Statistics Calculator"   onAction="#loadStatistics"/>
      </Menu>
      <Menu text="Finance">
        <MenuItem fx:id="menuEMI"         text="EMI / Loan Calculator"   onAction="#loadEMI"/>
        <MenuItem fx:id="menuCurrency"    text="Currency Converter"      onAction="#loadCurrency"/>
        <SeparatorMenuItem/>
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
        <SeparatorMenuItem/>
        <MenuItem fx:id="menuSpeed"       text="Speed / Distance / Time" onAction="#loadSpeed"/>
        <MenuItem fx:id="menuFuel"        text="Fuel Efficiency"         onAction="#loadFuel"/>
      </Menu>
    </MenuBar>
  </top>
  <!-- center: loaded dynamically by ViewRouter via MainAppController -->
</BorderPane>
```

**Menu Groups:**
- **Basic** -- Basic Calculator
- **Science** -- Scientific, Programmer | Matrix, Statistics
- **Finance** -- EMI, Currency | Tip, Discount
- **Health** -- BMI, Age
- **Utilities** -- Date Diff, Unit Converter | Speed, Fuel

---

## 8. CSS Dark Theme Token List

**JavaFX Note:** JavaFX CSS does not support standard CSS custom properties (`--var-name`).
All design tokens use the JavaFX convention of single-dash prefix (`-var-name`) and are
defined in the `.root` selector so they can be referenced across all component styles.

```css
/* dark-theme.css -- Design Tokens (JavaFX convention: -name, not --name) */

/* -- Colors ---------------------------------------- */
-bg-primary:    #1e1e1e;   /* main window background */
-bg-secondary:  #2d2d2d;   /* panel / card background */
-bg-button:     #3a3a3a;   /* button default */
-bg-hover:      #4a4a4a;   /* button hover */
-bg-active:     #5a5a5a;   /* button pressed */
-bg-input:      #252525;   /* text field background */
-accent:        #4a90d9;   /* primary action, equals button */
-accent-hover:  #5ba3f0;   /* accent hover */
-text-primary:  #e0e0e0;   /* main text */
-text-secondary:#9e9e9e;   /* labels, hints */
-text-result:   #ffffff;   /* result display */
-border:        #404040;   /* input borders */
-error:         #e74c3c;   /* error state */
-success:       #2ecc71;   /* success / positive result */

/* -- Typography ------------------------------------ */
-fx-font-family: "Segoe UI", "Inter", system;
-fx-font-size:   14px;
/* Mono (programmer calc display): "Consolas", "Roboto Mono", monospace */
/* Display size (result field): 28px */
/* Label size (hints): 12px */
/* Result label size: 18px */

/* -- Spacing (8-point grid) ------------------------ */
/* xs=4px, sm=8px, md=16px, lg=24px, xl=32px */

/* -- Border Radius --------------------------------- */
/* sm=4px, md=8px, lg=12px */

/* -- Window ---------------------------------------- */
/* Initial: 900x650, Min: 750x500 */
```

**Component styles included in dark-theme.css (Phase A1):**
MenuBar, Menu, MenuItem, Button (default + accent), TextField (default + display),
Label (default + result + error + success), ComboBox, Slider, DatePicker, ScrollPane,
GridPane/VBox/HBox, panel-card.

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
| **Matrix multiply (3x3)** | **Task\<String\> + Platform.runLater()** | Could be slow for large matrices |
| **Statistics (large dataset)** | **Task\<String\> + Platform.runLater()** | Sorting + iteration could be slow |

**Rule:** Any operation that could take > 16ms (one UI frame) MUST use `Task<String>`.
Every `Task` MUST have `.setOnFailed()` handler that shows `Alert.AlertType.ERROR`.

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
| FXML not found | `ViewRouter` NullPointerException guard | Shows Alert with "FXML not found" |
| Task failure | `.setOnFailed()` with Alert.ERROR | Shows exception message in dialog |

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
- **Decision**: Use no Spring/DI framework -- plain Java Singleton pattern
- **Reason**: Desktop app does not need Spring overhead; keeps startup fast
- **Impact**: ServiceFactory.java replaces Spring @Autowired injection

### ADR-002: Static Currency Rates
- **Decision**: Use hardcoded `Map<String, Double>` for currency rates vs USD
- **Reason**: No internet dependency; keeps app fully offline
- **Impact**: Rates will not be real-time; acceptable trade-off for offline-first design

### ADR-003: FXML Over Pure Java DSL
- **Decision**: Use FXML + Scene Builder layouting for all views
- **Reason**: Clean MVC separation; FXML = View, Java = Behavior
- **Impact**: Each calculator needs a separate .fxml file (15 fxml files)

### ADR-004: BorderPane as Root Layout
- **Decision**: Single `BorderPane` -- MenuBar at top, calculator view swapped at center
- **Reason**: Standard IDE-style layout; ViewRouter only updates `.center`
- **Impact**: Consistent frame; no screen flash on calculator switch

### ADR-005: Dark Theme Mandatory
- **Decision**: CSS dark theme applied via FXML `stylesheets` attribute on root BorderPane
- **Reason**: Professional look; all design tokens defined once in dark-theme.css
- **Impact**: All button/label/field colors come from CSS, not hardcoded Java
- **Note**: The stylesheet is loaded via `stylesheets="@../css/dark-theme.css"` on the
  root BorderPane in main.fxml. Child FXMLs loaded into the center pane inherit the
  stylesheet from their parent. If a child FXML defines its own root node that does not
  inherit, add `stylesheets="@../css/dark-theme.css"` to that FXML as well. Alternatively,
  applying the stylesheet at Scene level in `UniversalCalculatorApp.start()` provides a
  safety net for all nodes.

### ADR-006: Java 21 Target with JavaFX 21
- **Decision**: Target Java 21 LTS with JavaFX 21 dependencies
- **Reason**: Java 21 is the current LTS release; JavaFX 21 aligns with it
- **Impact**: pom.xml uses `<release>21</release>` in maven-compiler-plugin; all source
  and bytecode targets Java 21

---

## 13. Phase A1 Audit Notes

The following items were identified during the Phase A1 post-implementation audit and
should be addressed before Phase B begins:

| # | Item | Severity | Status |
|---|---|---|---|
| 1 | pom.xml `maven.compiler.source/target` properties say `25` but `<release>21</release>` correctly overrides. Properties should be set to `21` for consistency. | MEDIUM | Fix in Phase B start |
| 2 | `UniversalCalculatorApp.start()` does not apply dark-theme.css at Scene level. Currently applied via FXML `stylesheets` attribute on root BorderPane. Consider adding `scene.getStylesheets().add(...)` as safety net for child FXMLs. | MEDIUM | Fix in Phase B start |
| 3 | ViewRouter is lazy singleton (not thread-safe). Acceptable for JavaFX since all access is from the FX Application Thread, but should be documented. | LOW | Documented |

---

## GSD Status

| Section | Status |
|---|---|
| Project Structure (with phase markers) | COMPLETE |
| FXML-Controller-Service Map (15) | COMPLETE |
| CalculatorService Interface | COMPLETE |
| CalculatorType Enum | COMPLETE |
| ServiceFactory Pattern | COMPLETE |
| ViewRouter Pattern | COMPLETE |
| Menu Bar Structure | COMPLETE |
| CSS Token List (JavaFX convention) | COMPLETE |
| Threading Strategy | COMPLETE |
| Error Handling Strategy | COMPLETE |
| Controller Pattern Template | COMPLETE |
| ADRs (6) | COMPLETE |
| Phase A1 Audit Notes | COMPLETE |

**GSD Version**: 1.0.1
**Produced by**: solution-architect (Phase A.1)
**Audited by**: solution-architect (Phase A.1 post-audit)
**Next**: consensus-agent validation (Phase A.2) -> Phase B unlock

## Change Log

| Version | Updated By | Change |
|---------|-----------|--------|
| v1.0 | solution-architect | Initial GSD -- all sections drafted |
| v1.0.1 | solution-architect | Post-implementation audit: added phase markers [A1]/[B] to project tree, added MainAppController to tree, fixed CSS token syntax documentation to note JavaFX convention, added SeparatorMenuItem to menu structure, updated ViewRouter pseudocode with null-check guard, updated ServiceFactory with IllegalArgumentException, added ADR-006 (Java 21 target), added Section 13 (Audit Notes), added Change Log |
