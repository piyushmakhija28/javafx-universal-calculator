module com.techdeveloper.calculator {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens com.techdeveloper.calculator to javafx.fxml;
    opens com.techdeveloper.calculator.controller to javafx.fxml;
    opens com.techdeveloper.calculator.service to javafx.fxml;

    exports com.techdeveloper.calculator;
    exports com.techdeveloper.calculator.controller;
    exports com.techdeveloper.calculator.service;
}
