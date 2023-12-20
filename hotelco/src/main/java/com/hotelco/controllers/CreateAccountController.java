package com.hotelco.controllers;

import com.hotelco.entities.ReservationSystem;
import com.hotelco.entities.User;
import com.hotelco.utilities.DatabaseUtil;
import com.hotelco.utilities.FXMLPaths;
import com.hotelco.utilities.TextFormatters;
import com.hotelco.utilities.Verifier;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

/**
 * The CreateAccountController class is the associated controller class of the 'CreateAccountGUI' view. 
 * It handles connection between the GUI and internal data.
 * 
 * @author      Grigor Azakian
 */
public class CreateAccountController extends BaseController {

    /**
    * ImageView that contains the whole image.
    */
    @FXML
    private ImageView image;

    /**
    * ImageView that contains the right half of a image.
    */
    @FXML
    private ImageView imageRight;

    /**
     * TextField that contains the first name of the user.
     */
    @FXML
    private TextField firstName;

    /**
     * TextField that contains the last name of the user.
     */
    @FXML
    private TextField lastName;

    /**
     * TextField that contains the email of the user.
     */
    @FXML
    private TextField email;

    /**
     * Text that can be displayed to notify the user of any potential invalid inputs.
     */
    @FXML
    private Text notification;

    /**
     * TextField that contains the phone number of the user.
     */
    @FXML
    private TextField phoneNumber;

    /**
     * TextField that contains the password of the user.
     */
    @FXML
    private PasswordField password;

    /**
     * TextField that contains the second entry of the password of the user.
     */
    @FXML
    private PasswordField confirmPassword;

    /**
     * This method is called immediately upon controller creation.
     * It assigns TextFormatters to several TextFields to handle input verification.
     * As well makes it so the corners of the pictures are rounded.
     */
    @FXML
    private void initialize() {
        TextFormatters textFormatters = new TextFormatters();
            phoneNumber.setTextFormatter(textFormatters.PHONE_NUMBER);
            firstName.setTextFormatter(textFormatters.FIRST_NAME);
            lastName.setTextFormatter(textFormatters.LAST_NAME);
             Rectangle rectangle = new Rectangle(
                image.getFitWidth(),
                image.getFitHeight()
           );

            Rectangle clip = new Rectangle(
                image.getFitWidth()/2,
                image.getFitHeight()
           );

            rectangle.setArcWidth(50);
            rectangle.setArcHeight(50);
            image.setClip(rectangle);
            imageRight.setClip(clip);      
              
        Platform.runLater(() ->
            email.getScene().getRoot().requestFocus()
        );
    }

    /**
     * This method is called when pressing the 'Register' button.
     * It handles input verification and account creation. It will switch to 'LoginGUI' upon successful account creation.
     * @param event The 'mouse released' event that is triggered by pressing the 'Create Account' button.
     */
    @FXML
    private void createAccount(MouseEvent event) {
        TextField[] textFields = {firstName, lastName, email, phoneNumber};
        PasswordField[] passwordFields = {password, confirmPassword};
        for (TextField textField: textFields) {
            if (textField.getText().isEmpty()) {
                notification.setText("Please complete all fields");
                notification.setFill(Color.RED);
                return;
            }
        }
        for (PasswordField passwordField: passwordFields) {
            if (passwordField.getText().isEmpty()) {
                notification.setText("Please complete all fields");
                notification.setFill(Color.RED);
                return;
            }
        }

        if(!Verifier.isValidEmail(email.getText())){
            notification.setText("Please use a valid Email address!");
            notification.setFill(Color.RED);
            return;
        }
        if(phoneNumber.getText().length()!=10){
            notification.setText("Please use a valid phone number!");
            notification.setFill(Color.RED);
            return;
        }
        if (!password.getText().equals(confirmPassword.getText())) {
            notification.setText("Passwords do not match!");
            notification.setFill(Color.RED);
            return;
        }
        if (DatabaseUtil.doesEmailExist(email.getText())) {
            notification.setText("Email is already registered!");
            notification.setFill(Color.RED);
            return;
        }

        LoginController loginController = (LoginController) switchScene(FXMLPaths.LOGIN);

        User newUser = new User(firstName.getText(), lastName.getText(),email.getText(), phoneNumber.getText());
        newUser.push(password.getText());
        newUser.fetch(false);
        ReservationSystem.setCurrentUser(newUser);
        loginController.setNotification("Account successfully created!", Color.GREEN);

    }
    


    /**
     * This method is called by pressing the 'Already Have an Account?' button  which is the blue button.
     * It exits the 'CreateAccountGUI' and enters the 'LoginGUI'.
     * @param event The 'mouse released' event that is triggered by pressing the 'Go Back' text on the top left corner.
     */
    @FXML
    private void switchToLoginScene(MouseEvent event) {
        switchScene(FXMLPaths.LOGIN);
    }

}
