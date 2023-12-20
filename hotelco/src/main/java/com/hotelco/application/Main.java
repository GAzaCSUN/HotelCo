package com.hotelco.application;

import com.hotelco.administrator.Settings;
import com.hotelco.controllers.LoginController;
import com.hotelco.developer.Developer;
import com.hotelco.utilities.DailyTask;
import com.hotelco.utilities.FXMLPaths;
import com.hotelco.utilities.FrequentTask;
import com.hotelco.utilities.IdleTimer;
import com.hotelco.utilities.Instances;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;

/**
 * Initializes the JavaFX application, setting up the primary stage, loading the
 * login scene, and performing various tasks upon application start.
 * 
 * @author Bilin Pattasseril, Grigor Azakian, Daniel Schwartz
 */
public class Main extends Application {

	/**
	 * This method performs the necessary initialization steps, such as scheduling
	 * daily and frequent tasks, loading the login scene, and configuring the
	 * primary stage.
	 */
	@Override
	public void start(Stage primaryStage) {

		try {
			if (com.hotelco.developer.Settings.DEV_MODE) {
				Developer.runDevMode();
			}
			if (com.hotelco.developer.Settings.RUN_MAIN) {
				DailyTask.scheduleDailyTasks();
				FrequentTask.scheduleFrequentTasks();
				FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(FXMLPaths.LOGIN));
				Parent root = fxmlLoader.load();
				Scene scene = new Scene(root, Screen.getPrimary().getBounds().getWidth(),
						Screen.getPrimary().getBounds().getHeight());
				Instances.setScene(scene);
				Instances.setStage(primaryStage);
				IdleTimer.initialize();
				LoginController lc = (LoginController) fxmlLoader.getController();
				lc.initializeIdleTimer();
				primaryStage.centerOnScreen();
				primaryStage.initStyle(StageStyle.UNDECORATED);
				primaryStage.setTitle("HotelCo");
				primaryStage.getIcons()
						.add(new Image(Main.class.getResourceAsStream("/com/hotelco/images/hotelco.png")));
				primaryStage.setResizable(false);
				primaryStage.setScene(scene);
				primaryStage.show();
				if (Settings.FULL_SCREEN) {
					primaryStage.setFullScreen(true);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * This method serves as the starting point for the application's execution.
	 */
	public static void main(String[] args) {
		launch(args);
	}
}