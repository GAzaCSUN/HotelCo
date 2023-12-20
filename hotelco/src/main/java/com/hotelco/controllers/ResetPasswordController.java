package com.hotelco.controllers;

import com.hotelco.entities.User;
import com.hotelco.utilities.DatabaseUtil;
import com.hotelco.utilities.EmailGenerator;
import com.hotelco.utilities.FXMLPaths;
import com.hotelco.utilities.Instances;

import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import java.security.SecureRandom;

/**
 * The ResetPasswordController class is the associated controller class of the
 * 'ResetPasswordGUI' view. It handles connection between the GUI and internal
 * data.
 * 
 * @author Bilin Pattasseril, Grigor Azakian
 */
public class ResetPasswordController extends BaseController {

    /**
     * ImageView that contains the whole image with rounded corners.
     */
    @FXML
    private ImageView image;

    /**
     * ImageView that contains the right half of the image.
     */
    @FXML
    private ImageView imageRight;
    /**
     * Text that can be displayed to notify the user of any potential invalid
     * inputs.
     */
    @FXML
    private Text notification;

    /**
     * TextField that contains the email of the user.
     */
    @FXML
    private TextField email;

    /**
     * PauseTransition that is set to switch to ScreenSaverGUI after the user is
     * idle for some amount of time
     */
    private PauseTransition idleTimer;

    /**
     * EventHandler used to determine if the user is making inputs.
     */
    private EventHandler<Event> handler;

    /**
     * Rounds the corners of the beach ImageView upon initialization of the
     * Controller.
     */
    @FXML
    private void initialize() {
        Rectangle rectangle = new Rectangle(image.getFitWidth(), image.getFitHeight());

        Rectangle clip = new Rectangle(image.getFitWidth() / 2, image.getFitHeight());

        rectangle.setArcWidth(50);
        rectangle.setArcHeight(50);
        clip.setX(imageRight.getFitWidth() / 2);
        image.setClip(rectangle);
        imageRight.setClip(clip);
        Platform.runLater(() -> {

        });
    }

    /**
     * This method is called when pressing the 'Reset Password' button. It handles
     * input verification,reset current password, and sends email of new password.
     * It will enter 'Login GUI' upon successful reset.
     * 
     * @param event The 'mouse released' event that is triggered by pressing the
     *              'Reset Password' button.
     */
    @FXML
    private void resetPassword(MouseEvent event) {
        if (email.getText().isEmpty()) {
            notification.setText("Please enter an Email Address");
            notification.setFill(Color.RED);
            return;
        }
        String emailStr = email.getText();
        if (DatabaseUtil.doesEmailExist(emailStr)) {
            User user = new User(emailStr, true);
            String password = generateTempPassword();
            user.push(password);
            user.fetch(true);
            EmailGenerator.resetPassword(password, user);
            switchScene(FXMLPaths.LOGIN);
        } else {
            notification.setText("Email does not exisit");
            notification.setFill(Color.RED);
        }
    }

    /**
     * This method is called to get a temp password with length 10 which is
     * randomize.
     * 
     * @return a random temporary password of size 9
     */

    public static String generateTempPassword() {
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!@#$%^&*";
        StringBuilder password = new StringBuilder();

        SecureRandom random = new SecureRandom();

        for (int i = 0; i < 10; i++) {
            int randomIndex = random.nextInt(characters.length());
            password.append(characters.charAt(randomIndex));
        }

        return password.toString();
    }

    /**
     * This method is called by pressing the 'Return to Login?' button. It exits the
     * 'ResetPasswordGUI' and enters the 'LoginGUI'.
     * 
     * @param event The 'mouse released' event that is triggered by pressing the
     *              'Return to Login?' button.
     */
    @FXML
    private void switchToLogin(MouseEvent event) {
        idleTimer.stop();
        Instances.getScene().removeEventHandler(Event.ANY, handler);
        switchScene(FXMLPaths.LOGIN);
    }

    /**
     * This method is a setter for the variable 'notification'.
     * 
     * @param text  The string to set the variable 'notification' to.
     * @param color sets the string color for 'notification' to specifed color and
     *              if it is null it will not set a color.
     */
    void setNotification(String text, Color color) {
        notification.setText(text);
        if (color != null) {
            notification.setFill(color);
        }
    }

    /**
     * Must be called upon initializing ResetPasswordController. Sets up idleTimer
     * and the current scene's EventHandler. idleTimer will call
     * switchToScreenSaver() when its timer reaches 0. The scene will track every
     * event and reset the timer when an event is made.
     */
    public void initializeIdleTimer() {
        idleTimer = new PauseTransition(Duration.seconds(10));
        idleTimer.setOnFinished(e -> {
            switchToScreenSaver();
        });

        handler = e -> {
            idleTimer.stop();
            idleTimer.playFromStart();
        };

        Instances.getScene().addEventHandler(Event.ANY, handler);
        idleTimer.play();
    }

    /**
     * Called if idleTimer reaches the end of its timer. Switches the scene to
     * ScreenSaverGUI.
     */
    private void switchToScreenSaver() {
        idleTimer.stop();
        Instances.getScene().removeEventHandler(Event.ANY, handler);
        switchScene(FXMLPaths.SCREENSAVER);
    }

}