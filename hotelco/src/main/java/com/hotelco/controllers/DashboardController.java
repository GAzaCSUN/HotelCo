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
 * The Dashboard class is the associated controller class of the 'dashboard'
 * view. It handles connection between the GUI and internal data.
 * 
 * @author Bilin Pattasseril
 */
public class DashboardController extends BaseController {

    /**
     * Button named 'Book Room' that calls 'switchToRoomSearch()' when pressed.
     */
    @FXML
    private Button bookRoomButton;

    /**
     * Button named 'Check In' that calls 'switchToCheckIn()' when pressed.
     */
    @FXML
    private Button checkInButton;

    /**
     * Button named 'Check out' that calls 'switchToCheckOut()' when pressed.
     */
    @FXML
    private Button checkOutButton;

    /**
     * Button named 'home' that calls 'switchToHome()' when pressed.
     */
    @FXML
    private Button homeButton;

    /**
     * Button named 'profile' that calls 'switchToProfile()' when pressed.
     */
    @FXML
    private Button profileButton;

    /**
     * A AnchorPane which is the right of the dashboard.
     */
    @FXML
    public AnchorPane rightAnchor;

    /**
     * Button named 'Reservation History' that calls 'switchToReservationHistory()'
     * when pressed.
     */
    @FXML
    private Button viewBookingButton;

    /**
     * Array of Button that stores all the menu button.
     */
    private final Button[] menubuttons = new Button[6];

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
        switchAnchor(FXMLPaths.HOME);
        menubuttons[0] = bookRoomButton;
        menubuttons[1] = checkInButton;
        menubuttons[2] = homeButton;
        menubuttons[3] = profileButton;
        menubuttons[4] = viewBookingButton;
        menubuttons[5] = checkOutButton;
        buttonSelection(homeButton);
    }

    /**
     * This method is called by pressing the 'Check In' button. It changes the right
     * anchorpane to the 'CheckInGUI' scene
     * 
     * @param event The 'mouse released' event that is triggered by pressing the
     *              'Check In' button.
     */
    @FXML
    void switchToCheckIn(MouseEvent event) {
        switchAnchor(FXMLPaths.CHECK_IN);
        buttonSelection(checkInButton);
    }

    /**
     * This method is called by pressing the 'Check Out' button. It changes the right
     * anchorpane to the 'CheckOutGUI' scene
     * 
     * @param event The 'mouse released' event that is triggered by pressing the
     *              'Check Out' button.
     */
    @FXML
    void switchToCheckOut(MouseEvent event) {
        switchAnchor(FXMLPaths.CHECK_OUT);
        buttonSelection(checkOutButton);
    }

    /**
     * This method is called by pressing the 'home' button. It changes the right
     * anchorpane to the 'HomeGUI' scene
     * 
     * @param event The 'mouse released' event that is triggered by pressing the
     *              'home' button.
     */
    @FXML
    private void switchToHome(MouseEvent event) {
        switchAnchor(FXMLPaths.HOME);
        buttonSelection(homeButton);
    }

    /**
     * This method is called by pressing the 'Log Out' button. It will switch
     *  the scene to 'LoginGUI' scene
     * 
     * @param event The 'mouse released' event that is triggered by pressing the
     *              'Log Out' button.
     */
    @FXML
    void switchToLoginScene(MouseEvent event) {
        ReservationSystem.logout();
        switchScene(FXMLPaths.LOGIN);
    }

    /**
     * This method is called by pressing the 'Profile' button. It changes the right
     * anchorpane to the 'ProfileGUI' scene
     * 
     * @param event The 'mouse released' event that is triggered by pressing the
     *              'profile' button.
     */
    @FXML
    void switchToProfile(MouseEvent event) {
        buttonSelection(profileButton);
        switchAnchor(FXMLPaths.PROFILE);
    }

    /**
     * This method is called by pressing the 'Reservation History' button. It
     * changes the right anchorpane to the 'ReservationHistoryGUI' scene
     * 
     * @param event The 'mouse released' event that is triggered by pressing the
     *              'Reservation History' button.
     */
    @FXML
    void switchToReservationHistory(MouseEvent event) {
        switchAnchor(FXMLPaths.RHGUI);
        buttonSelection(viewBookingButton);
    }

    /**
     * This method is called by pressing the 'Book Room' button. It changes the
     * right anchorpane to the RoomChoiceGUI scene
     * 
     * @param event The 'mouse released' event that is triggered by pressing the
     *              'Book ROom' button.
     */
    @FXML
    void switchToRoomSearch(MouseEvent event) {
        switchAnchor(FXMLPaths.ROOMS);
        buttonSelection(bookRoomButton);
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
