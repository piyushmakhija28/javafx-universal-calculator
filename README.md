# 🧮 Universal JavaFX Calculator Suite

[![Java](https://img.shields.io/badge/Java-21-orange?logo=java)](https://openjdk.org/)
[![JavaFX](https://img.shields.io/badge/JavaFX-21-blue)](https://openjfx.io/)
[![Maven](https://img.shields.io/badge/Maven-3.8+-red?logo=apache-maven)](https://maven.apache.org/)
[![License](https://img.shields.io/badge/License-MIT-green)](LICENSE)
[![GitHub](https://img.shields.io/badge/GitHub-piyushmakhija28-black?logo=github)](https://github.com/piyushmakhija28/javafx-universal-calculator)

> **One app. 15 calculators. Zero internet required.**
> A professional-grade desktop application built with JavaFX, MVC architecture, and a dark-themed UI
> — by [piyushmakhija28](https://github.com/piyushmakhija28/javafx-universal-calculator)

---

## 📌 Table of Contents

- [Why This Calculator?](#-why-this-calculator)
- [Supported Calculator Types](#-supported-calculator-types)
- [Tech Stack](#-tech-stack)
- [Project Architecture](#-project-architecture)
- [Build & Run](#-build--run)
- [Development Execution Plan](#-development-execution-plan)
  - [Phase A — Foundation & Architecture](#phase-a--foundation--architecture)
  - [Phase B — Core Implementation](#phase-b--core-implementation-parallel)
  - [Phase C — Integration](#phase-c--integration-sequential)
  - [Phase D — QA Verification](#phase-d--qa-verification-sequential)
  - [Phase E — Documentation & Final Push](#phase-e--documentation--final-push-sequential)
- [Agent Execution Map](#-agent-execution-map)
- [Contributing](#-contributing)
- [License](#-license)

---

## 🚀 Why This Calculator?

| Feature | Universal JavaFX Calculator | Normal Calculator App |
|---|---|---|
| Number of calculators | ✅ **15+ types** in one app | ❌ Usually 1-3 types |
| UI Theme | ✅ **Professional dark theme** (CSS) | ❌ Basic system UI |
| Offline usage | ✅ **Fully offline** | ⚠️ Some need internet for currency |
| Performance | ✅ **Async (non-blocking UI)** with JavaFX Task | ❌ Often freezes on heavy math |
| Architecture | ✅ **MVC — easily extensible** (add new calculator = add 3 files) | ❌ Monolithic / hard to extend |
| Keyboard Shortcuts | ✅ **Full keyboard support** | ⚠️ Usually mouse-only |
| Platform | ✅ **Cross-platform Java desktop** | ❌ Platform-specific |
| Open Source | ✅ MIT License — fork and extend | ❌ Mostly closed |

---

## 🧰 Supported Calculator Types

| # | Calculator | Key Buttons / Fields |
|---|---|---|
| 1 | **Basic** | 0-9, +, -, ×, ÷, =, C, CE, %, ±, √ |
| 2 | **Scientific** | sin, cos, tan, log, ln, eˣ, xʸ, x², √, !, π, e, Deg/Rad toggle, ( ) |
| 3 | **Programmer** | HEX/DEC/OCT/BIN toggle, AND, OR, XOR, NOT, SHL, SHR, MOD, Byte/Word/DWord/QWord |
| 4 | **Financial / EMI** | Principal (P), Rate (R%), Tenure (N months), EMI, Total Interest, Total Amount |
| 5 | **Health / BMI** | Weight (kg/lbs), Height (cm/in), BMI result, Category label, Metric/Imperial toggle |
| 6 | **Age Calculator** | DOB DatePicker, Target Date DatePicker, Years / Months / Days output |
| 7 | **Date Difference** | Start DatePicker, End DatePicker, Days / Weeks / Months / Years between |
| 8 | **Currency Converter** | Amount, From currency (20+), To currency, Converted result |
| 9 | **Unit Converter** | Category (Length/Weight/Temp/Volume/Speed/Area), From unit, To unit, Value, Result |
| 10 | **Tip Calculator** | Bill Amount, Tip % slider (0-30%), Split count (1-10), Tip / Total / Per Person |
| 11 | **Discount Calculator** | Original Price, Discount %, Final Price, Amount Saved |
| 12 | **Matrix Calculator** | 2×2 or 3×3 TextField grid, Add / Subtract / Multiply / Transpose / Determinant |
| 13 | **Statistics Calculator** | Comma-separated input, Mean / Median / Mode / StdDev / Variance / Min / Max |
| 14 | **Speed / Distance / Time** | Any 2 inputs → solves for the 3rd variable |
| 15 | **Fuel Efficiency** | Distance (km), Fuel used (L), Output: L/100km, km/L, Cost per km |

---

## 🛠 Tech Stack

| Layer | Technology |
|---|---|
| Language | Java 21 |
| UI Framework | JavaFX 21 (FXML + CSS) |
| Build Tool | Maven 3.8+ |
| Architecture | MVC — FXML (View) + Controller (Behavior) + Service (Logic) |
| Theme | Dark CSS (`-fx-background-color: #1e1e1e`) |
| Threading | JavaFX `Task<T>` + `Platform.runLater()` |

---

## 📐 Project Architecture

```
src/
└── main/
    ├── java/com/techdeveloper/calculator/
    │   ├── App.java                        ← JavaFX Application entry point
    │   ├── ViewRouter.java                 ← Singleton: loads FXML into BorderPane.center
    │   ├── controller/
    │   │   ├── BasicCalculatorController.java
    │   │   ├── ScientificCalculatorController.java
    │   │   └── ... (15 controllers total)
    │   └── service/
    │       ├── CalculatorService.java      ← Interface: calculate(Map<String,String>) → String
    │       ├── BasicCalculatorService.java
    │       ├── ScientificCalculatorService.java
    │       └── ... (15 service classes + 1 ServiceFactory)
    └── resources/
        ├── fxml/
        │   ├── main.fxml                   ← Root layout (BorderPane + MenuBar)
        │   ├── basic-calculator.fxml
        │   └── ... (15 calculator FXML files)
        └── css/
            └── dark-theme.css              ← Global dark theme tokens
```

---

## ▶ Build & Run

**Prerequisites:** Java 21, Maven 3.8+

```bash
# Clone the repo
git clone https://github.com/piyushmakhija28/javafx-universal-calculator.git
cd javafx-universal-calculator

# Run the application
mvn clean javafx:run

# Run with debug
mvn clean javafx:run@debug
```

---

## 📋 Development Execution Plan

The project is built across **5 phases** using a multi-agent orchestration system.
Each phase has subtasks, assigned agents, execution mode (parallel/sequential), and ready-to-use prompts.

---

## Phase A — Foundation & Architecture

> **Mode: SEQUENTIAL** | Gate: consensus-agent must return `APPROVED` before Phase B starts

### A.1 — Architecture Blueprint

| Field | Detail |
|---|---|
| **Agent** | `solution-architect` |
| **Mode** | Sequential (first step — nothing runs before this) |
| **Depends On** | NONE |
| **Produces** | `GSD-v1.0.md` — complete design document |

**What to do:**
- Define full Maven project structure tree
- Map all 15 FXML files → Controller class → Service class
- Define `CalculatorService` interface with method signatures
- Define CSS dark-theme token list (colors, fonts, sizes)
- Define Menu Bar structure with `fx:id` values
- Define `ViewRouter` pattern (how menus swap FXML into BorderPane.center)
- Define threading strategy (which ops use `Task<T>`, which are direct)
- Define error-handling strategy (divide-by-zero, empty input, invalid format)

**Prompt for agent:**
```
AGENT: solution-architect

Objective: Create GSD v1.0 for a Universal JavaFX Calculator with 15 calculator types.

Requirements: Java 21, JavaFX 21, Maven, MVC (FXML+Controller+Service), dark CSS theme,
Menu Bar with dynamic FXML loading. Calculator types: Basic, Scientific, Programmer,
EMI, BMI, Age, Date Difference, Currency, Unit Converter, Tip, Discount, Matrix,
Statistics, Speed/Distance/Time, Fuel Efficiency.

Produce a GSD-v1.0.md document containing:
1. Maven project structure tree (src/main/java/com/techdeveloper/calculator/ layout)
2. FXML-Controller-Service mapping table (15 rows)
3. CalculatorService interface: String calculate(Map<String,String> inputs)
4. CSS token list: background, button, text, accent, hover colors + font family
5. Menu Bar structure: menus, sub-menus, menu items with fx:id values
6. ViewRouter pattern: loadView(String fxmlPath) → sets BorderPane.center
7. Threading rule: matrix/statistics use Task<String>, others are synchronous
8. Error handling: every service returns "Error: <message>" — never throws to Controller

DO NOT write Java implementation code. This is design only.
Output: Structured Markdown document titled GSD-v1.0.
```

---

### A.2 — Blueprint Validation Gate

| Field | Detail |
|---|---|
| **Agent** | `consensus-agent` |
| **Mode** | Sequential (after A.1 completes) |
| **Depends On** | A.1 — solution-architect output |
| **Produces** | `APPROVED` or `REJECTED` verdict with blockers |

**What to do:**
- Verify FXML-Controller-Service table covers ALL 15 calculators
- Verify `CalculatorService` interface is defined
- Verify `ViewRouter` pattern is described
- Verify thread-safety strategy is defined
- Verify CSS dark-theme tokens are listed
- Verify error-handling strategy is defined

**Prompt for agent:**
```
AGENT: consensus-agent

Objective: Validate GSD v1.0 from solution-architect for the Universal JavaFX Calculator.

Run all 6 validation checks:
1. FXML-Controller-Service table covers all 15 calculator types?
2. CalculatorService interface defined with method signature?
3. ViewRouter pattern defined (how menus load FXML into BorderPane.center)?
4. Threading strategy defined (Task<T> for heavy ops, Platform.runLater() for UI update)?
5. CSS dark theme tokens listed (min: background, button, text, accent colors)?
6. Error handling strategy defined (empty input, divide-by-zero, invalid format)?

Output format:
  VALIDATION REPORT
  [PASS/FAIL] Check 1: ...
  [PASS/FAIL] Check 2: ...
  [PASS/FAIL] Check 3: ...
  [PASS/FAIL] Check 4: ...
  [PASS/FAIL] Check 5: ...
  [PASS/FAIL] Check 6: ...
  Verdict: APPROVED | REJECTED
  Blockers (if REJECTED): <list each failing check with required fix>

If REJECTED: return control to orchestrator with blocker list.
APPROVED verdict unblocks Phase B.
```

> ⛔ **GATE**: Phase B does NOT start until `consensus-agent` returns `APPROVED`.

---

## Phase B — Core Implementation (PARALLEL)

> **Mode: PARALLEL** — All 3 subtasks launch simultaneously in a single orchestrator response
> Gate: Every agent must produce per-file deliverable report. Silent skips = hard failure.

```
Phase B launches all 3 agents AT THE SAME TIME:
  ┌─────────────────────────┐
  │  B.1 javafx-engineer    │  ← UI Layer (FXML + CSS + Controllers)
  │  B.2 spring-boot-ms     │  ← Logic Layer (15 Service classes)
  │  B.3 infra-squad-lead   │  ← GitHub + README + pom.xml
  └─────────────────────────┘
         (all parallel)
```

---

### B.1 — JavaFX UI Layer

| Field | Detail |
|---|---|
| **Agent** | `javafx-engineer` |
| **Mode** | Parallel (with B.2 and B.3) |
| **Depends On** | A.2 — consensus-agent APPROVED |
| **Auto-invokes** | `ui-ux-mathematics-engineer` (for CSS color math), `mathematics-engineer` (for DSA/layout) |
| **Skills** | `javafx-ide-designer`, `java-design-patterns-core`, `performance-optimization`, `error-handling-patterns` |
| **Produces** | 34 files: App.java, ViewRouter.java, main.fxml, 15 FXML files, 15 Controllers, dark-theme.css |

**What to do:**
- Create `App.java` (Application entry point, loads main.fxml, applies dark-theme.css)
- Create `main.fxml` (BorderPane root + MenuBar with 15 calculator menu items)
- Create `ViewRouter.java` singleton with `loadView(String fxmlPath)` method
- Create 15 FXML files (one per calculator — all inputs, buttons, labels with `fx:id`)
- Create 15 Controller classes (all `@FXML` fields + action methods calling Service layer)
- Create `dark-theme.css` (dark background, styled buttons, hover effects)
- Wire Menu Bar handlers: each item calls `ViewRouter.loadView("fxml/<name>.fxml")`
- For heavy ops (Matrix, Statistics): use `Task<String>` + `Platform.runLater()`
- Attach `onFailed` handler to every Task → show `Alert.AlertType.ERROR`

**Prompt for agent:**
```
AGENT: javafx-engineer
Skills: javafx-ide-designer, java-design-patterns-core, performance-optimization,
        error-handling-patterns, logging-patterns

CONSTRAINT (read first): Do NOT write math/calculation logic — that belongs in Service
classes. Per-file deliverable report is MANDATORY for all 34 files. Silent skips = failure.

Objective: Build the complete JavaFX UI layer for the Universal Calculator.

Project path: c:\Users\techd\Documents\workspace-spring-tool-suite-4-4.27.0-new\Calculator\
Java: 21 | JavaFX: 21 | Maven (pom.xml already exists)

Create these files:
1. App.java — extends Application, loads main.fxml, sets stage "Universal Calculator",
   applies src/main/resources/css/dark-theme.css
2. ViewRouter.java — Singleton, loadView(String fxmlPath) sets BorderPane center pane
3. main.fxml — BorderPane root with MenuBar (15 menu items, each with onAction)
4. dark-theme.css — tokens: background #1e1e1e, buttons #3a3a3a, text #e0e0e0,
   accent #4a90d9, hover #5a5a5a. Font: "Roboto" or system default monospace.
5. 15 FXML files in src/main/resources/fxml/:
   - basic-calculator.fxml: Display TextField + 4×5 GridPane buttons (0-9,+,-,*,/,=,C,CE,%,±,√)
   - scientific-calculator.fxml: Display + sin/cos/tan/log/ln/e^x/x^y/x²/√/!/π/e + DEG/RAD
   - programmer-calculator.fxml: Display + HEX/DEC/OCT/BIN tab + AND/OR/XOR/NOT/SHL/SHR/MOD
   - emi-calculator.fxml: P/R/N TextFields + Calculate Button + EMI/TotalInterest/TotalAmount Labels
   - bmi-calculator.fxml: Weight/Height TextFields + Metric/Imperial RadioButtons + BMI Label + Category
   - age-calculator.fxml: DOB DatePicker + Target DatePicker + Calculate + Years/Months/Days Labels
   - date-diff-calculator.fxml: Start/End DatePickers + Days/Weeks/Months/Years Labels
   - currency-calculator.fxml: Amount TextField + From/To ComboBox (20 currencies) + Result Label
   - unit-converter.fxml: Category ComboBox + From/To Unit ComboBox + Value TextField + Result
   - tip-calculator.fxml: Bill TextField + Tip Slider(0-30%) + Split Spinner(1-10) + Tip/Total/PerPerson
   - discount-calculator.fxml: Original Price + Discount% + Final Price + Amount Saved
   - matrix-calculator.fxml: 2x2/3x3 toggle + TextField GridPane + Add/Sub/Mul/Transpose/Det buttons
   - statistics-calculator.fxml: TextArea + Mean/Median/Mode/StdDev/Variance/Min/Max Labels
   - speed-calculator.fxml: Speed/Distance/Time TextFields + Solve dropdown + Calculate + Result
   - fuel-calculator.fxml: Distance/Fuel TextFields + L100km/kmL/CostPerKm Labels
6. 15 Controller classes — @FXML fields matching FXML fx:id, action methods delegate to Service
   (Service classes will be provided by spring-boot-microservices agent — use interfaces from GSD v1.0)

Threading rule: Matrix multiply + Statistics → wrap in Task<String>, use Platform.runLater()
Error dialog: onFailed handler on every Task → Alert.AlertType.ERROR

Output for every file:
FILE REPORT: Path | Lines Added | Lines Removed | Summary

CONSTRAINT (read last): No math logic in controllers. No UI updates from background threads.
Dark theme CSS is not optional. Per-file report for ALL 34 files — zero silent skips.
```

---

### B.2 — Calculator Logic / Service Layer

| Field | Detail |
|---|---|
| **Agent** | `spring-boot-microservices` |
| **Mode** | Parallel (with B.1 and B.3) |
| **Depends On** | A.2 — consensus-agent APPROVED |
| **Auto-invokes** | `fintech-mathematics-expert` (EMI, Currency, Discount), `healthcare-mathematics-expert` (BMI, Age), `mathematics-engineer` (Matrix, Statistics, Scientific) |
| **Skills** | `java-spring-boot-microservices`, `java-design-patterns-core`, `error-handling-patterns` |
| **Produces** | 17 files: 1 interface + 15 Service classes + 1 ServiceFactory |

**What to do:**
- Create `CalculatorService` interface (`String calculate(Map<String,String>)`)
- Create 15 Service implementation classes (one per calculator)
- Create `ServiceFactory.java` singleton (`getService(CalculatorType)`)
- All services must catch ALL exceptions and return `"Error: <message>"` — never throw

**Formulas to implement:**
- EMI = P × r × (1+r)ⁿ / ((1+r)ⁿ - 1) where r = annualRate/12/100
- BMI metric = weight / height_m²; imperial = (weight_lbs × 703) / height_in²
- Age = `Period.between(dob, targetDate)` → years, months, days
- Date Diff = `ChronoUnit.DAYS.between(start, end)` → then ÷7 for weeks, ÷365.25 for years
- Statistics: Mean = Σx/n; Median = sorted middle; StdDev = √(Σ(x-mean)²/n)
- Matrix Det (2×2) = ad-bc; Det (3×3) = Sarrus rule (cofactor expansion)

**Prompt for agent:**
```
AGENT: spring-boot-microservices
Skills: java-spring-boot-microservices, java-design-patterns-core, error-handling-patterns

CONSTRAINT (read first): Plain Java only — NO Spring annotations, NO web framework.
Do NOT touch FXML or CSS. Per-file report mandatory. Silent skips = hard failure.

Objective: Implement all 15 Calculator Service classes for the Universal JavaFX Calculator.

Auto-delegate formulas BEFORE coding:
- To fintech-mathematics-expert: EMI formula, currency conversion logic, discount math
- To healthcare-mathematics-expert: BMI formula (metric + imperial), Age via java.time.Period
- To mathematics-engineer: Statistics (mean/median/mode/stddev/variance), Matrix ops
  (add/sub/mul/transpose/determinant 2x2 and 3x3), Scientific (factorial, log, ln, trig)

Create:
1. CalculatorService.java (interface) — String calculate(Map<String,String> inputs)
2. BasicCalculatorService.java — +,-,*,/,%,√,± with divide-by-zero guard
3. ScientificCalculatorService.java — Math.sin/cos/tan (degrees/radians), log/log10,
   factorial (iterative), power, π, e constants
4. ProgrammerCalculatorService.java — Integer.parseInt(val, radix), bitwise ops, SHL/SHR/MOD
5. EMICalculatorService.java — EMI = P*r*(1+r)^n/((1+r)^n-1); TotalAmount; TotalInterest
6. BMICalculatorService.java — metric and imperial formulas + category string output
7. AgeCalculatorService.java — Period.between(dob, targetDate) → format output
8. DateDiffCalculatorService.java — ChronoUnit.DAYS + derived weeks/months/years
9. CurrencyCalculatorService.java — static Map<String,Double> rateVsUSD for 20+ currencies
10. UnitConverterService.java — maps for Length, Weight, Temperature, Volume, Speed, Area
11. TipCalculatorService.java — tip=bill*pct/100; total=bill+tip; perPerson=total/people
12. DiscountCalculatorService.java — final=original*(1-pct/100); saved=original-final
13. MatrixCalculatorService.java — 2x2 and 3x3: add/sub/mul/transpose/determinant
14. StatisticsCalculatorService.java — Mean/Median/Mode/StdDev/Variance/Min/Max/Count
15. SpeedCalculatorService.java — solve for any of: speed=dist/time, dist=speed*time, time=dist/speed
16. FuelCalculatorService.java — L100km=(fuel/dist)*100; kmL=dist/fuel; costPerKm=cost/dist
17. ServiceFactory.java — Singleton: getService(CalculatorType type) enum-based factory

Every service MUST: catch ArithmeticException, NumberFormatException, NullPointerException
→ return "Error: <specific message>" string — NEVER throw to Controller layer.

Output per file:
FILE REPORT: Path | Lines Added | Summary: <formula implemented>

CONSTRAINT (read last): No Spring annotations. No FXML. No web code.
All 17 files must appear in deliverable report — zero silent skips allowed.
```

---

### B.3 — GitHub Setup & Infrastructure

| Field | Detail |
|---|---|
| **Agent** | `infra-squad-lead` |
| **Mode** | Parallel (with B.1 and B.2) |
| **Depends On** | NONE (no dependency on B.1 or B.2 output) |
| **Skills** | `github-actions-ci`, `docker` |
| **Produces** | GitHub repo URL, updated pom.xml, initial README.md, git push |

**What to do:**
- Update `pom.xml` → Java 21, JavaFX 21, javafx-maven-plugin 0.0.8
- Create public GitHub repo `piyushmakhija28/Calculator`
- Initialize git, set branch `main`, push all files
- Write comprehensive `README.md` (users need project info before code is done)

**Prompt for agent:**
```
AGENT: infra-squad-lead

CONSTRAINT (read first): Must NOT modify Java source files or FXML files.
GitHub username is exactly: piyushmakhija28 (no variations).
Per-file deliverable report mandatory.

Objective: Setup GitHub repo and write README.md for the Universal JavaFX Calculator.

Project path: c:\Users\techd\Documents\workspace-spring-tool-suite-4-4.27.0-new\Calculator\

Task 1 — Update pom.xml:
  - maven.compiler.source → 21
  - maven.compiler.target → 21
  - javafx-controls version → 21
  - javafx-fxml version → 21
  - javafx-maven-plugin version → 0.0.8
  - mainClass → com.techdeveloper.calculator.App

Task 2 — GitHub Repo:
  gh repo create piyushmakhija28/javafx-universal-calculator --public --source=. --remote=origin
  git init (if needed) ; git add . ; git commit -m "feat: initial Universal Calculator project"
  git branch -M main ; git push -u origin main

Task 3 — Write README.md with sections:
  1. Project title + badges (Java, JavaFX, Maven, License)
  2. "Why This Calculator?" table (Universal vs Normal comparison — 6+ rows)
  3. All 15 Calculator Types table (type + key buttons)
  4. Tech Stack table
  5. Project Architecture (package tree)
  6. Build & Run instructions (mvn clean javafx:run)
  7. Development Phase Plan (5-phase table: Phase, Agents, Mode, Goal, Gate)
  8. Contributing guide (Fork + PR)
  9. License: MIT

Output:
FILE REPORT: Path | Lines Added | Summary
GitHub Repo URL: https://github.com/piyushmakhija28/javafx-universal-calculator

CONSTRAINT (read last): Do NOT touch any .java or .fxml files.
README must include all 15 calculator types with button details.
"Benefits vs Normal Calculator" section is mandatory.
```

---

## Phase C — Integration (SEQUENTIAL)

> **Mode: SEQUENTIAL** | Runs AFTER B.1 and B.2 both complete
> Gate: All 15 calculators must load without NullPointerException

### C.1 — Wire Service Layer into Controllers

| Field | Detail |
|---|---|
| **Agent** | `javafx-engineer` |
| **Mode** | Sequential (after B.1 + B.2 complete) |
| **Depends On** | B.1 (controllers), B.2 (service classes) |
| **Produces** | 15 Controllers updated — each correctly calls its Service class |

**What to do:**
- Inject Service instances (via `ServiceFactory`) into each Controller
- Replace any stub logic in Controllers with real Service calls
- Test that `ViewRouter.loadView()` correctly loads each FXML without crash
- Ensure all 15 menu items load their correct calculator view

**Prompt for agent:**
```
AGENT: javafx-engineer (Phase C — Integration)

Objective: Wire the 15 Service classes into their corresponding Controller classes.

Context: javafx-engineer (Phase B) created Controller stubs.
         spring-boot-microservices (Phase B) created Service classes.
         ServiceFactory.java provides getService(CalculatorType) method.

For each of the 15 Controllers:
1. Add ServiceFactory.getService(CalculatorType.BASIC) call in initialize() method
2. Inject the returned service instance as a field
3. In each @FXML action handler, call service.calculate(inputs) and update result Label
4. Confirm that ViewRouter.loadView("fxml/basic-calculator.fxml") etc. load without error

Verify integration: try loading all 15 views via ViewRouter, confirm no NullPointerException.

Output per file modified:
FILE REPORT: Path | Lines Added | Lines Removed | Summary

Do NOT add new FXML files. Do NOT change Service logic. Integration only.
```

---

## Phase D — QA Verification (SEQUENTIAL)

> **Mode: SEQUENTIAL** | Runs AFTER Phase C completes
> Gate: Coverage matrix must be PASS or CONDITIONAL PASS before Phase E

### D.1 — Full QA Coverage Check

| Field | Detail |
|---|---|
| **Agent** | `qa-testing-agent` |
| **Mode** | Sequential (after Phase C) |
| **Depends On** | Phase C — all controllers wired |
| **Skills** | `testing-core`, `error-handling-patterns` |
| **Produces** | QA Coverage Matrix (15 rows × 4 cols) + Verdict |

**What to do:**
- Read ALL 15 FXML files → verify fx:controller, fx:id on all inputs/buttons
- Read ALL 15 Controllers → verify @FXML fields match, action methods call service
- Read ALL 15 Services → verify error handling, `String calculate()` method exists
- Verify `ViewRouter.java` loads FXML into BorderPane correctly
- Verify `dark-theme.css` has minimum 3 color token definitions
- Generate full coverage matrix
- Issue final verdict (`PASS` / `CONDITIONAL PASS` / `FAIL`)

**Prompt for agent:**
```
AGENT: qa-testing-agent
Skills: testing-core, error-handling-patterns

CONSTRAINT (read first): PASS requires concrete file-read evidence per item.
FORBIDDEN: grep as sole verification, file existence as proof, self-reporting.

Objective: Verify all 15 calculators in the Universal JavaFX Calculator project.

Project path: c:\Users\techd\Documents\workspace-spring-tool-suite-4-4.27.0-new\Calculator\

For each of the 15 calculator types, read the actual files and verify:
  Col 1 — FXML Present: File exists AND has correct fx:controller AND all buttons have onAction
  Col 2 — Controller Bound: @FXML fields match fx:id in FXML, action method delegates to service
  Col 3 — Service Tested: calculate() method exists, divide-by-zero / NumberFormat handled
  Col 4 — Error Handling: service returns "Error: ..." string (not throws), no raw exceptions to UI

Also verify:
  - ViewRouter.java: loadView(String) method exists and sets BorderPane center
  - App.java: loads main.fxml and applies dark-theme.css
  - dark-theme.css: minimum 3 color token definitions present

Generate QA Coverage Matrix:
| Calculator         | FXML Present | Controller Bound | Service Tested | Error Handling |
|--------------------|:------------:|:----------------:|:--------------:|:--------------:|
| Basic              | PASS/FAIL    | PASS/FAIL        | PASS/FAIL      | PASS/FAIL      |
| Scientific         | ...          | ...              | ...            | ...            |
| Programmer         | ...          | ...              | ...            | ...            |
| EMI                | ...          | ...              | ...            | ...            |
| BMI                | ...          | ...              | ...            | ...            |
| Age                | ...          | ...              | ...            | ...            |
| Date Difference    | ...          | ...              | ...            | ...            |
| Currency           | ...          | ...              | ...            | ...            |
| Unit Converter     | ...          | ...              | ...            | ...            |
| Tip                | ...          | ...              | ...            | ...            |
| Discount           | ...          | ...              | ...            | ...            |
| Matrix             | ...          | ...              | ...            | ...            |
| Statistics         | ...          | ...              | ...            | ...            |
| Speed/Time/Dist    | ...          | ...              | ...            | ...            |
| Fuel Efficiency    | ...          | ...              | ...            | ...            |

Issue verdict:
  QA VERDICT: PASS | CONDITIONAL PASS | FAIL
  Evidence summary: <concrete per-check evidence>
  Unverifiable items (CONDITIONAL PASS only): <list with reason>

CONSTRAINT (read last): Read actual files — do NOT use agent self-reports.
Missing file = FAIL for that row. Correctness unverifiable without execution = CONDITIONAL PASS.
```

> ⛔ **GATE**: If QA returns `FAIL` → fix blocking items and re-run D.1 before Phase E starts.

---

## Phase E — Documentation & Final Push (SEQUENTIAL)

> **Mode: SEQUENTIAL** | Runs AFTER Phase D returns PASS or CONDITIONAL PASS
> Gate: `git diff --stat` must show ≥80% of expected 54 files changed

### E.1 — Final README Enrichment & Git Push

| Field | Detail |
|---|---|
| **Agent** | `infra-squad-lead` |
| **Mode** | Sequential |
| **Depends On** | Phase D — QA PASS |
| **Produces** | Final README.md pushed, all source files committed |

**What to do:**
- Update README.md with final project structure and file list
- Add `How to Contribute` and `Known Limitations` sections
- Final `git add . ; git commit -m "feat: complete Universal Calculator v1.0" ; git push`

### E.2 — Ground-Truth Verification Gate

| Field | Detail |
|---|---|
| **Agent** | `devops-engineer` |
| **Mode** | Sequential (last step) |
| **Depends On** | E.1 |
| **Produces** | Completion report with git diff stats |

**Prompt for agent:**
```
AGENT: devops-engineer (Ground-Truth Verification)

Objective: Verify that the Universal JavaFX Calculator project is complete and pushed.

Run these commands and report output:
1. git diff --stat HEAD~1 (or compare to initial commit)
2. Verify file count: expect ~54 files (34 UI + 17 service + 3 infra)
   - If actual < 80% of 54 (i.e., < 44 files) → BLOCK completion, report missing files
3. git log --oneline -5 (confirm commits exist)
4. Confirm GitHub repo is accessible: https://github.com/piyushmakhija28/javafx-universal-calculator

Output:
  GROUND-TRUTH REPORT
  Files Changed: <actual count> / 54 expected
  Status: COMPLETE (≥80%) | BLOCKED (<80% — list missing files)
  GitHub URL: https://github.com/piyushmakhija28/javafx-universal-calculator
  Last Commit: <hash and message>
```

---

## 📊 Agent Execution Map

```
Phase A (Sequential)
────────────────────────────────────────────────────
[A.1] solution-architect ──── produces GSD v1.0
         │
         ▼
[A.2] consensus-agent ──────── APPROVED / REJECTED
         │  APPROVED
         ▼
Phase B (All 3 in PARALLEL — launch in single response)
────────────────────────────────────────────────────
[B.1] javafx-engineer          [B.2] spring-boot-ms     [B.3] infra-squad-lead
  (FXML + CSS + Controllers)    (15 Service classes)      (GitHub + README)
         │                             │
         └──────────────┬──────────────┘
                        ▼
Phase C (Sequential)
────────────────────────────────────────────────────
[C.1] javafx-engineer ─── wire Services into Controllers
         │
         ▼
Phase D (Sequential)
────────────────────────────────────────────────────
[D.1] qa-testing-agent ─── 15×4 coverage matrix + verdict
         │  PASS or CONDITIONAL PASS
         ▼
Phase E (Sequential)
────────────────────────────────────────────────────
[E.1] infra-squad-lead ─── final commit + push
         │
         ▼
[E.2] devops-engineer ──── git diff --stat (Ground-Truth Gate)
────────────────────────────────────────────────────
Total Agent Invocations: 7 (A.1, A.2, B.1, B.2, B.3, C.1, D.1, E.1, E.2 = 9 calls)
Expected Files: ~54 | Phase B is true parallel (3 agents, 1 response)
```

---

## 📁 File Count by Phase

| Phase | Files Created/Modified | Details |
|---|---|---|
| A | 1 | GSD-v1.0.md (design doc) |
| B.1 | 34 | App.java, ViewRouter.java, main.fxml, 15 FXMLs, 15 Controllers, dark-theme.css |
| B.2 | 17 | CalculatorService interface, 15 Services, ServiceFactory |
| B.3 | 2 | pom.xml (updated), README.md (initial) |
| C | 15 | 15 Controllers (updated with Service wiring) |
| D | 1 | QA Coverage Matrix report |
| E | 2 | README.md (final), git commit |
| **Total** | **~54** | |

---

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch: `git checkout -b feat/new-calculator`
3. Add your calculator:
   - New FXML view in `src/main/resources/fxml/`
   - New Controller in `controller/` package
   - New Service in `service/` package implementing `CalculatorService`
   - Register in `ServiceFactory` and add menu item in `main.fxml`
4. Submit a Pull Request

---

## 📄 License

MIT License — see [LICENSE](LICENSE) file for details.

---

> Built with ❤️ using JavaFX 21 | Multi-agent orchestration by [piyushmakhija28](https://github.com/piyushmakhija28)
> 🔗 Repo: [github.com/piyushmakhija28/javafx-universal-calculator](https://github.com/piyushmakhija28/javafx-universal-calculator)
