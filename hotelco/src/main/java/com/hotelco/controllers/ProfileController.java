package com.hotelco.controllers;

import com.hotelco.entities.ReservationSystem;
import com.hotelco.entities.User;
import com.hotelco.utilities.TextFormatters;

import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;

/**
 * The ProfileController class is the associated controller class of the
 * 'ProfileGUI' view. It handles connection between the GUI and internal data.
 * 
 * @author Bilin Pattasseril
 */

public class ProfileController extends BaseController {

    /**
     * TextField that contains the email of the user.
     */
    @FXML
    private TextField email;

    /**
     * TextField that contains the first name of the user.
     */
    @FXML
    private TextField first;

    /**
     * TextField that contains the last name of the user.
     */
    @FXML
    private TextField last;

    /**
     * TextField that contains the phone number of the user.
     */
    @FXML
    private TextField number;

    /**
     * User that contains the current user.
     */
    private User user;

    /**
     * This method is called immediately upon controller creation. It updates the
     * the current user and then updates the textfields 'email',
     * 'first','last','number' to what is stored in the database.It as well sets a
     * formatter for the 'number', 'first', and 'email'.
     */
    @FXML
    private void initialize() {
        TextFormatters textFormatters = new TextFormatters();
        user = ReservationSystem.getCurrentUser();
        number.setTextFormatter(textFormatters.PHONE_NUMBER);
        first.setTextFormatter(textFormatters.FIRST_NAME);
        first.setFocusTraversable(true);
        last.setTextFormatter(textFormatters.LAST_NAME);
        email.setText(user.getEmail());
        first.setText(user.getFirstName());
        last.setText(user.getLastName());
        number.setText(user.getPhone());
    }

    /**
     * This method is called by pressing the 'Save Changes' button. It Saves any
     * changes made in the Profile scene and push it to the Database.
     * 
     * @param event The 'mouse released' event that is triggered by pressing the
     *              'Save Changes' button.
     */
    @FXML
    private void saveProfileContent(MouseEvent event) {
        user.setEmail(email.getText());
        user.setFirstName(first.getText());
        user.setLastName(last.getText());
        user.setPhone(number.getText());
        user.push();
    }

}
