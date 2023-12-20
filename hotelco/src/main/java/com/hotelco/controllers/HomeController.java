package com.hotelco.controllers;

import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.util.Duration;

/**
 * The HomeController class is the associated controller class of the 'HomeGUI' view. 
 * It handles connection between the GUI and internal data.
 * 
 * @author      Grigor Azakian 
 */
public class HomeController extends BaseController {

    /**
     * ImageView that is set to kick off the initial FadeTransition out.
     */
    @FXML
    private ImageView fo;

    /**
     * ImageView that is set to FadeTransition in first.
     */
    @FXML
    private ImageView fi;

    /**
     * Triggered when fo or fi's opacity reaches a certain value.
     * Ensures the ChangeListeners do not keep triggering during one iteration of a FadeTransition.
     */
    private boolean isFadingIn = false;

    /**
     * Image array of all images used for Transition effect.
     */
    Image[] images = { 
        new Image("/com/hotelco/images/boracay.jpg"), 
        new Image("/com/hotelco/images/pool.jpg"),
        new Image("/com/hotelco/images/resort.jpg"), 
        new Image("/com/hotelco/images/rocks.jpg"),
        new Image("/com/hotelco/images/zanzibar.jpg")
    };    

    /**
     * Called immediately upon controller creation.
     * It creates an array of images and sets up the fade in and out transitions of images.
     */
    @FXML
    private void initialize() {
        fo.opacityProperty().addListener(constructChangeListener(fi));
        fi.opacityProperty().addListener(constructChangeListener(fo));
        playTransition();        
    }

    /**
     * Sets up the initital FadeTransition to be played.
     */
    private void playTransition() {
        FadeTransition fadeOut = new FadeTransition(Duration.millis(3000), fo);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        PauseTransition pauseTransition = new PauseTransition(Duration.millis(1800));
        pauseTransition.setOnFinished(e -> {
            fadeOut.play();
        });
        pauseTransition.play();
    }

    /**
     * Returns a ChangeListener that will set up transitions for an ImageView pair.
     * The ChangeListeners will listen to the opacity value of the ImageView it
     * binds to, and set up FadeTransitions for its pair.
     * 
     * @param pair The ImageView to have FadeTransitions triggered.
     * @return a new changeListner 
     */
    private ChangeListener<Number> constructChangeListener(ImageView pair) {
        return (new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (oldValue.doubleValue() > newValue.doubleValue() && newValue.doubleValue() <= 0.65) {
                    if (!isFadingIn) {
                        isFadingIn = true;
                        Image nextImage = images[0];
                        System.arraycopy(images, 1, images, 0, images.length - 1);
                        images[images.length - 1] = nextImage;
                        pair.setImage(nextImage);
                        FadeTransition fadeIn = new FadeTransition(Duration.millis(3000), pair);
                        PauseTransition pauseTransition = new PauseTransition(Duration.seconds(1.75));
                        fadeIn.setFromValue(0.0);
                        fadeIn.setToValue(1.0);
                        fadeIn.setOnFinished(e -> {
                            pauseTransition.play();
                        });
                        pauseTransition.setOnFinished(e -> {
                            isFadingIn = false;
                            FadeTransition fadeOut = new FadeTransition(Duration.millis(3000), pair);
                            fadeOut.setFromValue(1.0);
                            fadeOut.setToValue(0.0);
                            fadeOut.play();
                        });
                        fadeIn.play();
                    }
                }
            }
        });
    }

}