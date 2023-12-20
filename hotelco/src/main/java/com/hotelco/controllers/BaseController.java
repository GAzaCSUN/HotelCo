package com.hotelco.controllers;

import com.hotelco.utilities.IdleTimer;
import com.hotelco.utilities.Instances;

import javafx.fxml.FXMLLoader;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;

/**
 * The BaseController is a base class that all controllers inherit from. 
 * It contains common functionality all controllers must use.
 * 
 * @author      Grigor Azakian
 */
public class BaseController  {
    /**
     * This method will switch the scene the user is currently viewing to the provided FXML file located in 'fxmlLocation'.
     * @param fxmlLocation The file path of the FXML file to switch to.
     * @return Returns the controller associated with 'fxmlLocation'. If the method fails to switch the scene, returns null.
     */
    protected BaseController switchScene(String fxmlLocation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlLocation));
            Parent root = loader.load();
            BaseController controller = loader.getController();

            Stage stage = Instances.getStage();
            Scene scene = new Scene(root, Screen.getPrimary().getBounds().getWidth(), Screen.getPrimary().getBounds().getHeight());
            Instances.setScene(scene);

            IdleTimer.initialize();
            if (controller instanceof LoginController) {
                ((LoginController)controller).initializeIdleTimer();
            }
            else if (controller instanceof DashboardController) {
                Instances.setDashboardController((DashboardController) controller);
            }

            stage.setResizable(false);
            stage.setScene(scene);
            stage.show();
            stage.setFullScreen(true);

            return controller;
            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}