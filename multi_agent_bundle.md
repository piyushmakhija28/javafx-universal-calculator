# Universal JavaFX Calculator — Multi-Agent Orchestration Bundle
# Version: 2.0.0 | Generated: 2026-04-06 | Owner: piyushmakhija028

---

## ① ORCHESTRATION PROMPT

### YOUR TASK
Build a **Universal Multi-Calculator Desktop Application** using **JavaFX** and push it publicly to
GitHub under the account `piyushmakhija028/Calculator`. The application must have a **Menu Bar** that
lets users select from 15+ calculator types. The selected calculator must load dynamically into the
main window center using FXML view swapping. All code must strictly follow **MVC architecture** using
FXML (View), Controller (Behavior), and Service (Logic) layers.

**Research-backed Calculator Types and their Buttons:**

| # | Calculator Type        | Key Buttons / Fields                                                                                      |
|---|------------------------|-----------------------------------------------------------------------------------------------------------|
| 1 | Basic                  | 0-9, +, -, *, /, =, C, CE, %, ±, √                                                                       |
| 2 | Scientific             | sin, cos, tan, log, ln, e^x, x^y, x², √, !, π, e, Deg/Rad toggle, (, )                                   |
| 3 | Programmer             | HEX/DEC/OCT/BIN toggle, AND, OR, XOR, NOT, SHL, SHR, MOD, Byte/Word/DWord/QWord                          |
| 4 | Financial / EMI        | Principal (P), Rate (R%), Tenure (N months), EMI= P*R*(1+R)^N/((1+R)^N-1), Total Interest, Total Amount   |
| 5 | Health / BMI           | Weight (kg/lbs), Height (cm/in), BMI = weight/height², Category label, Metric/Imperial toggle             |
| 6 | Age Calculator         | DatePicker (DOB), DatePicker (Target Date), Years/Months/Days result using java.time.Period                |
| 7 | Date Difference        | DatePicker (Start), DatePicker (End), Days/Weeks/Months/Years between                                     |
| 8 | Currency Converter     | Amount input, From ComboBox (20+ currencies), To ComboBox, Converted amount, static rate map              |
| 9 | Unit Converter         | Category ComboBox (Length/Weight/Temp/Volume/Speed/Area), From unit, To unit, Value input, Result          |
| 10| Tip Calculator         | Bill Amount, Tip % slider (0-30%), Split (1-10 people), Tip amount, Total, Per Person                     |
| 11| Discount Calculator    | Original Price, Discount % input, Final Price, Amount Saved                                               |
| 12| Matrix Calculator      | 2×2 / 3×3 grid of TextFields, Add, Subtract, Multiply, Transpose, Determinant buttons                    |
| 13| Statistics Calculator  | TextArea (comma-separated numbers), Mean, Median, Mode, StdDev, Variance, Min, Max, Count                 |
| 14| Speed/Distance/Time    | Speed (km/h), Distance (km), Time (hrs), solve for missing variable                                       |
| 15| Fuel Efficiency        | Distance (km), Fuel used (L), Calculate L/100km and km/L, Cost per km                                     |

**Benefits vs Normal Calculators (must be in README):**
- All-in-one: 15+ calculators in a single app vs installing separate tools
- Dark-themed professional UI with JavaFX CSS
- Non-blocking async calculations — UI stays responsive
- MVC architecture — easily extensible to add new calculators
- No internet needed — fully offline
- Keyboard shortcut support for all calculators

---

### CONSTRAINTS
- Architecture: JavaFX MVC (FXML View + Controller + Service Layer)
- GitHub: Push to public `piyushmakhija028/Calculator` repo
- Menu Bar: Dynamic FXML loading via `FXMLLoader` into `BorderPane` center
- Java: 11+ | JavaFX: 13+
- CSS: Dark theme mandatory (`-fx-background-color: #1e1e1e`)
- Thread safety: NO UI updates from background threads — use `Platform.runLater()`
- Per-file deliverable report mandatory for every file created/modified
- Silent skips = hard failure
- pom.xml must include javafx-controls, javafx-fxml, javafx-maven-plugin dependencies

---

### COMPLEXITY CLASSIFICATION
**Enterprise** — 6 agents, cross-squad, requires solution-architect + consensus-agent gate first.

---

### DOMAIN DETECTION & AGENT SELECTION

| Domain                       | Primary Agent              | Support Agent                 | Skills Used                                          |
|------------------------------|----------------------------|-------------------------------|------------------------------------------------------|
| JavaFX Desktop UI             | `javafx-engineer`          | `ui-ux-designer`              | javafx-ide-designer, java-design-patterns-core       |
| Java Math/Logic (Backend)    | `spring-boot-microservices`| `mathematics-engineer`        | java-spring-boot-microservices, java-design-patterns-core |
| CI/CD, Git, GitHub            | `devops-engineer`          | `infra-squad-lead`            | docker, github-actions-ci                            |
| Finance Math (EMI/Currency)  | `fintech-mathematics-expert` | (auto-invoked)              | derivatives-pricing-core, fixed-income-core          |
| Health Math (BMI/Age)        | `healthcare-mathematics-expert` | (auto-invoked)          | biostatistics-core                                   |
| UIlayout/Color/Animation Math| `ui-ux-mathematics-engineer`| (auto-invoked)               | (OKLCH palette, 8pt grid, animation Bezier math)     |
| Testing                      | `qa-testing-agent`         | `devops-engineer`             | testing-core                                         |

**Collaboration Pattern:** Custom — Desktop JavaFX App (Pattern derived from Pattern 1 + Pattern 2)

---

### MATH ROUTING
- Scientific/Matrix/Statistics algorithms → `mathematics-engineer` (auto-invoked by javafx-engineer)
- EMI/Currency/Discount → `fintech-mathematics-expert` (auto-invoked by spring-boot-microservices)
- BMI/Age/Health → `healthcare-mathematics-expert` (auto-invoked by spring-boot-microservices)
- UI color/spacing/animation → `ui-ux-mathematics-engineer` (auto-invoked by javafx-engineer)
- NOTE: Math experts provide formulas/logic — they do NOT write Java code directly

---

### ARCHITECTURE & VALIDATION GATES
a. **`solution-architect` FIRST** — must produce package structure, FXML-Controller mapping, and Service layer design.
b. **`consensus-agent` gate** — validates blueprint; returns `APPROVED` or `REJECTED` with blockers.
c. **Blueprint injection** — every agent receives relevant ADRs + contracts from approved blueprint.
d. **GSD (Global State Document)** — `solution-architect` creates GSD v1.0. Squad leads update it as work progresses. All squads must always execute against the latest GSD version.

---

### SQUAD LEAD ROUTING

**App Squad (app-squad-lead):**
- TO: app-squad-lead
- TASK: Coordinate javafx-engineer (UI/FXML) + spring-boot-microservices (Calculator Logic Services)
- BLUEPRINT: solution-architect approved blueprint + GSD v1.0
- DEPENDS ON: consensus-agent APPROVED verdict
- PRODUCES: Complete src/main/java code, all FXML files, CSS stylesheet, pom.xml

**Infra Squad (infra-squad-lead):**
- TO: infra-squad-lead
- TASK: GitHub public repo setup, initial push, README.md enrichment
- BLUEPRINT: Standard Maven project structure
- DEPENDS ON: NONE (can run in parallel with app-squad-lead)
- PRODUCES: GitHub repo URL, enriched README.md, git commit log

---

### PHASE EXECUTION PLAN

```
Phase A — Foundation
  Agents : solution-architect → consensus-agent
  Output : MVC blueprint, FXML-Controller mapping, GSD v1.0
  GATE   : consensus-agent returns APPROVED → unblocks Phase B

Phase B — Core Implementation [PARALLEL]
  Group 1 (parallel):
    - javafx-engineer    : All FXML views + CSS dark theme + Menu Bar routing + App.java
    - spring-boot-microservices : All 15 Calculator Service classes with math logic
  Group 2 (parallel with Group 1):
    - infra-squad-lead   : GitHub repo creation + README.md (no dependency on Group 1 output)
  GATE   : Per-file deliverable report from each agent. Silent skips = block completion.

Phase C — Integration
  Agents : javafx-engineer wires Service classes into Controllers (single sequential step)
  GATE   : All 15 calculators load without NullPointerException, all buttons have @FXML handlers

Phase D — QA Verification
  Agents : qa-testing-agent (MANDATORY)
  GATE   : Coverage matrix produced; verdict PASS or CONDITIONAL PASS with evidence.
           FORBIDDEN: grep-only checks or self-reporting without file reads.

Phase E — Documentation + Git Commit
  Agents : infra-squad-lead finalizes README, devops-engineer confirms `git diff --stat`
  GATE   : Ground-Truth Gate — git diff must show ≥80% of expected files changed.
```

---

### INTERFACE CONTRACTS

**solution-architect → consensus-agent:**
- INPUT: Blank — produces blueprint from requirements
- OUTPUT: `GSD-v1.0.md` with: package tree, FXML-Controller map, Service interface definitions, CSS token list
- ASSUMES: Java 11, JavaFX 13+, Maven
- MUST NOT: Write implementation code

**consensus-agent → app-squad-lead (on APPROVED):**
- INPUT: GSD v1.0 + approved blueprint
- OUTPUT: `APPROVED` verdict with highlighted ADRs
- MUST NOT: Approve without checking FXML naming convention, thread-safety pattern, service abstraction

**javafx-engineer → spring-boot-microservices:**
- INPUT: Service interfaces from GSD v1.0
- OUTPUT (javafx-engineer): FXML files + Controller stubs calling Service interfaces
- OUTPUT (spring-boot-microservices): Service implementation classes with calculation logic
- ASSUMES: Service classes are injected via Singleton pattern or static factory
- MUST NOT: javafx-engineer must NOT write math logic; spring-boot-microservices must NOT write FXML

**app-squad-lead → qa-testing-agent:**
- INPUT: All src/main/java files + FXML resources
- OUTPUT: QA Coverage Matrix (15 rows × 4 cols: FXML present, Controller bound, Service tested, Error handler)
- MUST NOT: Issue PASS based on file existence alone

---

### RESILIENCE & QA
- **QA Gate Rule**: PASS = concrete evidence per item. CONDITIONAL PASS = document unknown items. FAIL = block Phase E.
- **Model Fallback**: sonnet → opus → escalate to user (on rate limit)
- **Ground-Truth Gate**: Run `git diff --stat` before declaring DONE. Actual changed files must be ≥80% of expected. Cross-check: git diff + agent self-reports + QA matrix — must agree.
- **Prompt Generation Gate**: After Phase A completes, orchestrator MUST invoke `prompt-generation-expert` in Batch Mode with all Phase B-E agent tasks. No agent launches until bundle is returned.

---

## ② MULTI-AGENT PROMPT BUNDLE

---

═══════════════════════════════════════════════════════
AGENT: solution-architect
Phase: Phase A
Parallel With: NONE
Depends On: NONE
═══════════════════════════════════════════════════════
PROMPT:
**CONSTRAINT (critical — read first):** Do NOT write implementation code. Output is design-only.

**Objective:** Create the complete architectural blueprint (GSD v1.0) for the Universal JavaFX Calculator project — a desktop app with 15 calculator types, Menu Bar switching, MVC architecture, and dark theme CSS.

**Input:** Requirements: JavaFX 13+, Java 11+, Maven, 15 calculator types (Basic, Scientific, Programmer, EMI, BMI, Age, Date Diff, Currency, Unit Converter, Tip, Discount, Matrix, Statistics, Speed/Distance/Time, Fuel Calculator), Menu Bar dynamic FXML loading.

**Instructions:**
1. Define the full Maven project structure tree (`src/main/java/com/techdeveloper/calculator/` tree).
2. Map every FXML file → its Controller class → its Service class (all 15 calculators).
3. Define the `CalculatorService` interface with method signatures that each service must implement.
4. Define the CSS token list (background colors, button colors, text colors, font families).
5. Define the Menu Bar structure: menus, sub-menus, menu items with `fx:id` values.
6. Define thread-safety pattern: which operations use `Task<T>`, which use direct execution.
7. Define the `ViewRouter` service: how clicking a menu item loads the correct FXML into `BorderPane.center`.
8. Define error-handling strategy: invalid inputs, divide-by-zero, empty fields.
9. Create GSD v1.0 document with all of the above.

**Output Format:** Structured Markdown document titled `GSD-v1.0` with sections: Project Structure, FXML-Controller-Service Mapping Table (15 rows), Interface Definitions, CSS Tokens, Menu Bar Layout, Threading Strategy, Error Handling Strategy.

**Constraint (critical — read last):** MUST NOT write Java code. MUST NOT write FXML markup. Output is architecture design document only.

---

═══════════════════════════════════════════════════════
AGENT: consensus-agent
Phase: Phase A
Parallel With: NONE
Depends On: solution-architect
═══════════════════════════════════════════════════════
PROMPT:
**CONSTRAINT (critical — read first):** Return APPROVED or REJECTED. If REJECTED, list exact blockers. Do NOT rewrite the blueprint.

**Objective:** Validate the solution-architect's GSD v1.0 for the Universal JavaFX Calculator project.

**Input:** The complete GSD v1.0 document from solution-architect.

**Validation Checklist (check every item):**
1. Does the FXML-Controller-Service table cover all 15 calculator types?
2. Is `CalculatorService` interface defined with method signatures?
3. Is the `ViewRouter` pattern described (how menus swap FXML into BorderPane.center)?
4. Is thread-safety strategy defined (no UI updates from background threads)?
5. Are CSS dark-theme tokens listed (minimum: background, button, text, accent colors)?
6. Is error-handling strategy defined (divide by zero, empty input, invalid format)?

**Output Format:**
```
VALIDATION REPORT
Workflow: Universal JavaFX Calculator
Blueprint Version: GSD v1.0
Checks:
  [PASS/FAIL] 1. FXML-Controller-Service table covers all 15 calculators
  [PASS/FAIL] 2. CalculatorService interface defined
  [PASS/FAIL] 3. ViewRouter pattern defined
  [PASS/FAIL] 4. Thread-safety strategy defined
  [PASS/FAIL] 5. CSS dark theme tokens listed
  [PASS/FAIL] 6. Error-handling strategy defined
Verdict: APPROVED | REJECTED
Blockers (if REJECTED): <list each failing check with required fix>
```

**Constraint (critical — read last):** Must output APPROVED before Phase B begins. If REJECTED, return control to orchestrator-agent with blocker list.

---

═══════════════════════════════════════════════════════
AGENT: javafx-engineer
Phase: Phase B
Parallel With: spring-boot-microservices, infra-squad-lead
Depends On: consensus-agent (APPROVED)
Skills: javafx-ide-designer, java-design-patterns-core, java-spring-boot-microservices, performance-optimization, testing-core, error-handling-patterns, logging-patterns
═══════════════════════════════════════════════════════
PROMPT:
**CONSTRAINT (critical — read first):** Do NOT write math/calculation logic — that belongs in Service classes (handled by spring-boot-microservices agent). Do NOT depend on infra-squad-lead output. Per-file deliverable report is MANDATORY for every file created. Silent skips = hard failure.

**Objective:** Build the complete JavaFX UI layer for the Universal Calculator app: `App.java`, all 15 FXML view files, all 15 Controller classes, 1 CSS dark theme file, and `ViewRouter` service.

**Input:** GSD v1.0 from solution-architect (FXML-Controller-Service map, CSS tokens, ViewRouter pattern, Menu Bar layout, threading strategy).

**Instructions:**
1. Create `App.java` extending `Application`: load `main.fxml`, set stage title "Universal Calculator", apply `dark-theme.css`.
2. Create `main.fxml` with `BorderPane` root: `MenuBar` at top with 15 calculator menu items.
3. Create `ViewRouter.java` singleton: `loadView(String fxmlPath)` method that loads FXML and sets `BorderPane.center`.
4. Create all 15 FXML files (one per calculator type) with correct `fx:controller` and `fx:id` for every input/button:
   - `basic-calculator.fxml`: Display TextField + GridPane (4 cols × 5 rows) of buttons (0-9, +,-,*,/,=,C,CE,%,±,√)
   - `scientific-calculator.fxml`: Display + scientific buttons (sin,cos,tan,log,ln,e^x,x^y,x²,√,!,π,e) + DEG/RAD toggle
   - `programmer-calculator.fxml`: Display + HEX/DEC/OCT/BIN TabPane + AND,OR,XOR,NOT,SHL,SHR,MOD buttons
   - `emi-calculator.fxml`: P/R/N TextFields + Calculate Button + EMI/TotalInterest/TotalAmount Labels
   - `bmi-calculator.fxml`: Weight/Height TextFields + Metric/Imperial RadioButtons + BMI result + Category label
   - `age-calculator.fxml`: DOB DatePicker + Target DatePicker + Calculate + Years/Months/Days Labels
   - `date-diff-calculator.fxml`: Start/End DatePickers + Days/Weeks/Months/Years between Labels
   - `currency-calculator.fxml`: Amount TextField + From/To ComboBox (20 currencies) + Result Label
   - `unit-converter.fxml`: Category ComboBox + From/To Unit ComboBox + Value TextField + Result Label
   - `tip-calculator.fxml`: Bill TextField + Tip Slider (0-30%) + Split Spinner (1-10) + Tip/Total/PerPerson
   - `discount-calculator.fxml`: Original Price + Discount% + Final Price + Amount Saved Labels
   - `matrix-calculator.fxml`: 2x2/3x3 toggle + TextField grid (GridPane) + Add/Sub/Mul/Transpose/Det buttons
   - `statistics-calculator.fxml`: TextArea (comma-separated) + Mean/Median/Mode/StdDev/Variance/Min/Max Labels
   - `speed-calculator.fxml`: Speed/Distance/Time TextFields + Solve dropdown + Calculate button + Result Label
   - `fuel-calculator.fxml`: Distance/Fuel TextFields + L100km + kmL + CostPerKm Labels
5. Create all 15 Controller classes with `@FXML` fields + action methods (delegate math to Service layer).
6. Create `dark-theme.css` using CSS tokens from GSD v1.0 (background #1e1e1e, buttons #3a3a3a, text #e0e0e0).
7. Wire Menu Bar action handlers: each item calls `ViewRouter.loadView("fxml/<filename>.fxml")`.
8. For heavy calculations (matrix multiply, statistics), wrap in `Task<String>` and use `Platform.runLater()` for result update.
9. Attach `onFailed` handler to every Task — show `Alert.AlertType.ERROR` to user.

**Output Format:** Per-file deliverable report:
```
FILE REPORT:
  Path: src/main/java/...  |  Lines Added: +XX  |  Lines Removed: -XX  |  Summary: <what was done>
```
List all 34 files (1 App.java + 1 ViewRouter + 15 FXMLs + 15 Controllers + 1 CSS + 1 main.fxml).

**Constraint (critical — read last):** Must NOT write calculation logic in Controllers. Must NOT update UI from background threads directly. Must produce per-file report for ALL files — missing a file = incomplete task. Dark theme CSS is mandatory.

---

═══════════════════════════════════════════════════════
AGENT: spring-boot-microservices
Phase: Phase B
Parallel With: javafx-engineer, infra-squad-lead
Depends On: consensus-agent (APPROVED)
Skills: java-spring-boot-microservices, java-design-patterns-core, error-handling-patterns
═══════════════════════════════════════════════════════
PROMPT:
**CONSTRAINT (critical — read first):** Do NOT write FXML, CSS, or any UI code. Math experts are auto-invoked — wait for their formula confirmations before coding. Per-file deliverable report mandatory for every file. Silent skips = hard failure.

**Objective:** Implement all 15 Calculator Service classes in pure Java (no Spring, no web framework — desktop app only). Each service handles the computation logic for one calculator type.

**Input:** GSD v1.0 `CalculatorService` interface + 15 calculator types with their formulas.

**Math delegation (auto-invoke before implementing):**
- Delegate to `fintech-mathematics-expert`: EMI formula, currency rate logic, discount math
- Delegate to `healthcare-mathematics-expert`: BMI formula (metric + imperial), Age calculation using `java.time.Period`
- Delegate to `mathematics-engineer`: Statistics (mean, median, mode, stddev, variance), Matrix operations (add, sub, mul, determinant), Scientific functions (sin/cos/tan using Math.*, factorial, log, ln)

**Instructions:**
1. Create `CalculatorService` interface (if not already from solution-architect) with `String calculate(Map<String, String> inputs)` method.
2. Create 15 Service classes implementing `CalculatorService`:
   - `BasicCalculatorService`: +, -, *, /, %, √, ± with divide-by-zero guard
   - `ScientificCalculatorService`: Math.sin/cos/tan (in degrees/radians), Math.log/log10, factorial, power, π, e constant
   - `ProgrammerCalculatorService`: Integer parsing in HEX/DEC/OCT/BIN, bitwise AND/OR/XOR/NOT, SHL/SHR, MOD
   - `EMICalculatorService`: EMI = P*r*(1+r)^n / ((1+r)^n - 1) where r = annual_rate/12/100; TotalAmount; TotalInterest
   - `BMICalculatorService`: Metric: weight/(height_m²); Imperial: (weight_lbs * 703) / height_in²; category string
   - `AgeCalculatorService`: `Period.between(dob, targetDate)` → years, months, days
   - `DateDiffCalculatorService`: `ChronoUnit.DAYS.between(start, end)` → days, weeks (÷7), months, years (÷365.25)
   - `CurrencyCalculatorService`: Static `Map<String, Double>` rate map (20+ currencies vs USD base); convert via (amount / fromRate) * toRate
   - `UnitConverterService`: Category-based conversion maps (Length: m/cm/km/ft/in/mi; Weight: kg/g/lb/oz; Temp: C/F/K; Volume: L/mL/gal; Speed: kmh/mph/ms)
   - `TipCalculatorService`: tipAmount=bill*tipPct/100; total=bill+tip; perPerson=total/people
   - `DiscountCalculatorService`: finalPrice=original*(1-discount/100); saved=original-finalPrice
   - `MatrixCalculatorService`: 2×2 and 3×3 matrix add, subtract, multiply, transpose, determinant
   - `StatisticsCalculatorService`: Mean, Median, Mode, StdDev (population), Variance, Min, Max, Count from double[]
   - `SpeedCalculatorService`: speed=distance/time; distance=speed*time; time=distance/speed (solve for missing)
   - `FuelCalculatorService`: lPer100km=(fuelL/distKm)*100; kmPerL=distKm/fuelL; costPerKm=fuelCost/distKm
3. Every service: catch `ArithmeticException`, `NumberFormatException`, `NullPointerException` → return `"Error: <message>"` string instead of throwing.
4. Create `ServiceFactory.java` singleton: `getService(CalculatorType type)` returns correct service instance.

**Output Format:** Per-file deliverable report:
```
FILE REPORT:
  Path: src/main/java/com/techdeveloper/calculator/service/...  |  Lines Added: +XX  |  Summary: <formula implemented>
```
List all 17 files (1 interface + 15 services + 1 factory).

**Constraint (critical — read last):** Must NOT touch FXML or Controller files. Must NOT use Spring Boot annotations — plain Java only. Every service MUST handle error cases and return String result (never throw to Controller).

---

═══════════════════════════════════════════════════════
AGENT: infra-squad-lead
Phase: Phase B
Parallel With: javafx-engineer, spring-boot-microservices
Depends On: NONE
═══════════════════════════════════════════════════════
PROMPT:
**CONSTRAINT (critical — read first):** Must NOT modify Java source files or FXML files. Must use GitHub username `piyushmakhija028` exactly. Per-file deliverable report mandatory.

**Objective:** Create the public GitHub repository `piyushmakhija028/Calculator`, push the existing codebase, update `pom.xml` for JavaFX 21 compatibility, and write a comprehensive `README.md`.

**Input:** Existing local Maven project at `c:\Users\techd\Documents\workspace-spring-tool-suite-4-4.27.0-new\Calculator`.

**Instructions:**
1. Update `pom.xml`:
   - Update java version to 21, JavaFX version to 21
   - Add `javafx-controls`, `javafx-fxml` dependencies (version 21)
   - Ensure `javafx-maven-plugin 0.0.8` is configured with `mainClass = com.techdeveloper.calculator.App`
2. Create GitHub public repo `piyushmakhija028/Calculator` via `gh repo create` or API.
3. Initialize git (if not already), set branch to `main`, add remote origin.
4. Commit all files and push to GitHub.
5. Write `README.md` with the following sections:
   - **Project Title**: Universal JavaFX Calculator Suite
   - **Description**: 15 calculator types in one desktop app
   - **Calculator Types Table**: All 15 types with their key buttons/fields (see requirements)
   - **Benefits vs Normal Calculators**: (offline, all-in-one, dark UI, MVC extensible, keyboard support, async)
   - **Tech Stack**: Java 21, JavaFX 21, Maven
   - **Project Structure**: Package tree
   - **How to Run**: `mvn clean javafx:run`
   - **Phase-wise Build Plan**: 5-phase table (Foundation, Core, Integration, QA, Documentation)
   - **Contributing**: Fork + PR guide
   - **License**: MIT

**Output Format:** Per-file deliverable report + GitHub repo URL.

**Constraint (critical — read last):** Must NOT alter Java source or FXML files. README must include all 15 calculator types with button details and explicit "Benefits vs Normal Calculator" section.

---

═══════════════════════════════════════════════════════
AGENT: qa-testing-agent
Phase: Phase D
Parallel With: NONE
Depends On: javafx-engineer, spring-boot-microservices (Phase C integration complete)
Skills: testing-core, error-handling-patterns
═══════════════════════════════════════════════════════
PROMPT:
**CONSTRAINT (critical — read first):** PASS verdict requires concrete evidence from actual file reads. CONDITIONAL PASS if any item is unverifiable. FORBIDDEN: grep as sole verification, file existence as proof of functionality, self-reports without evidence.

**Objective:** Verify all 15 calculator FXML views, their Controllers, and their Service classes are correctly implemented and connected.

**Input:** All files in `src/main/java/com/techdeveloper/calculator/` and `src/main/resources/`.

**Instructions:**
1. Read each of the 15 FXML files — verify: correct `fx:controller`, all input fields have `fx:id`, all buttons have `onAction`.
2. Read each of the 15 Controller files — verify: all `@FXML` fields match FXML `fx:id`, action methods call Service layer (not direct math).
3. Read each of the 15 Service files — verify: calculate method exists, divide-by-zero/NumberFormat errors handled, returns String result.
4. Verify `ViewRouter.java` exists and has `loadView(String)` method.
5. Verify `App.java` loads `main.fxml` and applies `dark-theme.css`.
6. Verify `dark-theme.css` exists with at least 3 color token definitions.
7. Generate QA Coverage Matrix:

```
| Calculator         | FXML Present | Controller Bound | Service Tested | Error Handling |
|--------------------|--------------|------------------|----------------|----------------|
| Basic              | PASS/FAIL    | PASS/FAIL        | PASS/FAIL      | PASS/FAIL      |
| Scientific         | PASS/FAIL    | ...              | ...            | ...            |
| ... (all 15 rows)  |              |                  |                |                |
```

8. Issue final verdict:
```
QA VERDICT: PASS | CONDITIONAL PASS | FAIL
Evidence summary: <list concrete evidence per check>
Unverifiable items (if CONDITIONAL PASS): <list with reason>
```

**Constraint (critical — read last):** Read actual files — do NOT assume from agent self-reports. If a file is missing, mark FAIL. If a method exists but correctness cannot be verified without execution, mark CONDITIONAL PASS with explicit flag. Coverage matrix must have all 15 rows × 4 columns.

---

## ③ EXECUTION SUMMARY

```
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
EXECUTION SUMMARY — Universal JavaFX Calculator
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

Sequential Chain:
  solution-architect → consensus-agent → [Phase B Group] → qa-testing-agent

Parallel Groups:
  Phase B Group 1 (launch in single response — true parallel):
    [javafx-engineer, spring-boot-microservices, infra-squad-lead]

Total Agent Calls: 6
  1. solution-architect     (Phase A — Sequential)
  2. consensus-agent        (Phase A — Sequential, after #1)
  3. javafx-engineer        (Phase B — Parallel)
  4. spring-boot-microservices (Phase B — Parallel)
  5. infra-squad-lead       (Phase B — Parallel)
  6. qa-testing-agent       (Phase D — Sequential, after Phase C integration)

Agents Added vs Previous Version:
  + javafx-engineer        (was incorrectly angular-engineer — FIXED)
  + ui-ux-mathematics-engineer (auto-invoked by javafx-engineer — implicit)
  + fintech-mathematics-expert (auto-invoked by spring-boot-microservices — implicit)
  + healthcare-mathematics-expert (auto-invoked by spring-boot-microservices — implicit)
  + mathematics-engineer   (auto-invoked by spring-boot-microservices — implicit)
  + Prompt Generation Gate (mandatory before Phase B launch — orchestrator calls prompt-generation-expert)
  + GSD v1.0 (Global State Document — created by solution-architect, referenced by all agents)
  + Per-calculator button/field details (all 15 types fully specified — ADDED)
  + pom.xml update to JavaFX 21 + Java 21 — ADDED

Status: READY FOR EXECUTION
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
```

---

## ④ ORCHESTRATOR INVOCATION RULES (for orchestrator-agent)

When you receive this bundle as input:

1. **DO NOT** invoke `prompt-generation-expert` again — this bundle IS the prompt bundle.
2. **INVOKE** `solution-architect` with PROMPT from Section ② Agent 1 above.
3. **INVOKE** `consensus-agent` with PROMPT from Section ② Agent 2 above after solution-architect completes.
4. **ON APPROVED**: Launch ALL three Phase B agents in a **single response** (one message, three Agent tool calls simultaneously):
   - javafx-engineer (Section ② Agent 3)
   - spring-boot-microservices (Section ② Agent 4)
   - infra-squad-lead (Section ② Agent 5)
5. After Phase B + Phase C integration, **INVOKE** `qa-testing-agent` (Section ② Agent 6).
6. After QA PASS: run `git diff --stat` to verify file count ≥ 80% of expected (34 UI files + 17 service files + 3 infra files = ~54 files).
7. Report completion to user with GitHub repo URL.
