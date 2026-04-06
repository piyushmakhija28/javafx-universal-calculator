package com.techdeveloper.calculator;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 * Universal JavaFX Calculator Suite
 * Entry point for the application.
 * Supports 15+ calculator types via a Menu Bar with dynamic FXML loading.
 *
 * @author piyushmakhija28
 * @version 1.0.0
 */
public class UniversalCalculatorApp extends Application {

    @Override
    public void start(Stage stage) {
        // Placeholder root — javafx-engineer agent will replace this
        // with full BorderPane + MenuBar + ViewRouter setup in Phase B
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 900, 650);
        stage.setTitle("Universal Calculator Suite");
        stage.setScene(scene);
        stage.setMinWidth(750);
        stage.setMinHeight(500);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
