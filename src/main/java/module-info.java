module com.techdeveloper.calculator {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.graphics;

    opens com.techdeveloper.calculator to javafx.fxml;
    exports com.techdeveloper.calculator;
}
