package com.hotelco.controllers;

import java.io.IOException;

import com.hotelco.entities.ReservationSystem;
import com.hotelco.utilities.FXMLPaths;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

/**
 * The Dashboard class is the associated controller class of the
 * 'ManagerDashboard' view. It handles connection between the GUI and internal
 * data.
 * 
 * @author Bilin Pattasseril
 */
public class ManagerDashboardController extends BaseController {

    /**
     * Button named 'Book Room' that calls 'switchToRevenue()' when pressed.
     */
    @FXML
    private Button revenueButton;

    /**
     * Button named 'cart' that calls 'switchToRoomVacancy()' when pressed.
     */
    @FXML
    private Button vacancyButton;

    /**
     * Button named 'home' that calls 'switchToAllReservactions()' when pressed.
     */
    @FXML
    private Button allReservationsButton;

    /**
     * A AnchorPane which is the right of the dashboard.
     */
    @FXML
    public AnchorPane rightAnchor;

    /**
     * Array of Button that stores all the menu button.
     */
    private final Button[] menubuttons = new Button[3];

    /**
     * String which stores the current anchor scene.
     */
    private String currentPath;

    /**
     * Called immediately upon controller creation. It changes the anchor to the
     * home and initalize each menu button
     */
    @FXML
    private void initialize() {
        switchAnchor(FXMLPaths.REVENUE);
        menubuttons[0] = revenueButton;
        menubuttons[1] = vacancyButton;
        menubuttons[2] = allReservationsButton;
        buttonSelection(revenueButton);
    }

    /**
     * This method is called by pressing the 'All Reservation' button. It changes
     * the right anchorpane to the 'AllReservationGUI' scene
     * 
     * @param event The 'mouse released' event that is triggered by pressing the
     *              'All Reservation' button.
     */
    @FXML
    void switchToAllReservactions(MouseEvent event) {
        switchAnchor(FXMLPaths.ALL_RESERVATION);
        buttonSelection(allReservationsButton);
    }

    /**
     * This method is called by pressing the 'Revenue' button. It changes the right
     * anchorpane to the 'RevenueGUI'
     * 
     * @param event The 'mouse released' event that is triggered by pressing the
     *              'Revenue' button.
     */
    @FXML
    private void switchToRevenue(MouseEvent event) {
        switchAnchor(FXMLPaths.REVENUE);
        buttonSelection(revenueButton);
    }

    /**
     * This method is called by pressing the 'LOGOUT' button. It changes the right
     * anchorpane to the 'LoginGUI'
     * 
     * @param event The 'mouse released' event that is triggered by pressing the
     *              'LOGOUT' button.
     */
    @FXML
    void switchToLoginScene(MouseEvent event) {
        ReservationSystem.logout();
        switchScene(FXMLPaths.LOGIN);
    }

    /**
     * This method is called by pressing the 'Room Vacancy' button. It changes the
     * right anchorpane to the RoomVacancyGUI scene
     * 
     * @param event The 'mouse released' event that is triggered by pressing the
     *              'Room Vacancy' button.
     */
    @FXML
    void switchToRoomVacancy(MouseEvent event) {
        switchAnchor(FXMLPaths.VACANCY);
        buttonSelection(vacancyButton);
    }

    /**
     * This method is used to change the style of the button to emphasis the *
     * current selection and the non current selection.
     * 
     * @param selectedButton The button that is selected
     */
    private void buttonSelection(Button selectedButton) {
        for (Button button : menubuttons) {
            if (button != null && button.equals(selectedButton)) {
                button.setStyle("-fx-text-fill: #3c4149;-fx-background-color:#f9bd1a;-fx-border-color: transparent;");
            } else if (button != null) {
                button.setStyle("-fx-text-fill: #f9bd1a;-fx-background-color:#3c4149;-fx-border-color: transparent;");
            }
        }

    }

    /**
     * This method will switch the anchor the user is currently viewing to the
     * provided FXML file located in 'fxmlLocation'.
     * 
     * @param path The file path of the FXML file to switch to.
     * @return null return's null if the anchor is already being shown. 
     */
    public BaseController switchAnchor(String path) {
        if (!path.equals(currentPath)) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(path));
            try {
                AnchorPane newContent = loader.load();
                rightAnchor.getChildren().setAll(newContent);
                return loader.getController();
            } catch (IOException e) {
                e.printStackTrace();
            }
            currentPath = path;
        }
        return null;
    }
}
