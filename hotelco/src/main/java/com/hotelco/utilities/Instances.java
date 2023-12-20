package com.hotelco.utilities;

import com.hotelco.controllers.DashboardController;

import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Stores several instances of necessary front-end objects that are commonly
 * referenced.
 * 
 * @author      Grigor Azakian
 */
public class Instances {

    /**
     * Ensures that Instances is a utility class that cannot be created
     * as an object.
     */
    private Instances() {}

    /**
     * Instance of the current Scene being shown.
     */
    private static Scene scene;

    /**
     * Instance of the stage for the lifetime of the application.
     */
    private static Stage stage;

    /**
     * Instance of the current DashboardController.
     */
    private static DashboardController dbc;

    /**
     * Sets the current Scene.
     * @param scene The current Scene.
     */
    public static void setScene(Scene scene) {
        Instances.scene = scene;
    }

    /**
     * Sets the current Stage.
     * @param stage The current Stage.
     */
    public static void setStage(Stage stage) {
        Instances.stage = stage;
    }

    /**
     * Sets the current DashboardController.
     * @param dbc The current DashboardController.
     */
    public static void setDashboardController(DashboardController dbc) {
        Instances.dbc = dbc;
    }

    /**
     * Returns the current scene.
     * @return The current scene.
     */
    public static Scene getScene() {
        return scene;
    }

    /**
     * Returns the current stage.
     * @return The current stage.
     */
    public static Stage getStage() {
        return stage;
    }

    /**
     * Returns the current DashboardController.
     * @return The current DashboardController.
     */
    public static DashboardController getDashboardController() {
        return dbc;
    }

}


