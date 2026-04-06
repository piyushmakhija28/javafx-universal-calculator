package com.techdeveloper.calculator;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Universal JavaFX Calculator Suite
 * Entry point for the application.
 * Loads main.fxml (BorderPane + MenuBar) and applies the dark CSS theme.
 * ViewRouter handles dynamic FXML swapping in the center pane.
 * Supports 15 calculator types — see MainAppController for menu routing.
 *
 * @author piyushmakhija28
 * @version 1.0.0
 */
public class UniversalCalculatorApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(
                getClass().getResource("/fxml/main.fxml"));

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
