module com.techdeveloper.calculator {
    requires transitive javafx.base;
    requires transitive javafx.controls;
    requires transitive javafx.fxml;
    requires transitive javafx.graphics;
    requires java.net.http;
    requires org.slf4j;

    opens com.techdeveloper.calculator to javafx.fxml;
    opens com.techdeveloper.calculator.controller to javafx.fxml;
    opens com.techdeveloper.calculator.service to javafx.fxml;

    exports com.techdeveloper.calculator;
    exports com.techdeveloper.calculator.controller;
    exports com.techdeveloper.calculator.service;
    exports com.techdeveloper.calculator.service.impl;
    exports com.techdeveloper.calculator.constants;
    exports com.techdeveloper.calculator.form;
    exports com.techdeveloper.calculator.dto;
}
