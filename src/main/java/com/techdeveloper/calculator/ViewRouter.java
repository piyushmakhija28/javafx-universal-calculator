package com.techdeveloper.calculator;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;

import java.io.IOException;

/**
 * Singleton that manages the main BorderPane center region.
 * Called by MainAppController for every menu item action.
 * Loads FXML files and swaps them into the center of the root layout.
 */
public class ViewRouter {

    private static ViewRouter instance;
    private BorderPane mainLayout;

    private ViewRouter() {
    }

    public static ViewRouter getInstance() {
        if (instance == null) {
            instance = new ViewRouter();
        }
        return instance;
    }

    /**
     * Register the root BorderPane so ViewRouter can update its center.
     *
     * @param layout the root BorderPane from main.fxml
     */
    public void setMainLayout(BorderPane layout) {
        this.mainLayout = layout;
    }

    /**
     * Load an FXML file and set it as the center of the main BorderPane.
     *
     * @param fxmlPath classpath-relative path, e.g. "/fxml/basic-calculator.fxml"
     */
    public void loadView(String fxmlPath) {
        if (mainLayout == null) {
            showError("Router not initialized", "setMainLayout() must be called before loadView().");
            return;
        }
        try {
            Parent view = FXMLLoader.load(getClass().getResource(fxmlPath));
            mainLayout.setCenter(view);
        } catch (IOException e) {
            showError("Failed to load view: " + fxmlPath, e.getMessage());
        } catch (NullPointerException e) {
            showError("FXML not found: " + fxmlPath, "Check that the file exists in resources/fxml/");
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
