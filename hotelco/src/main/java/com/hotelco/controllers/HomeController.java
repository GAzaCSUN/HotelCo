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
     * Called immediately upon controller creation.
     * It creates an array of images and sets up the fade in and out transitions of images.
     */
    @FXML
    private void initialize() {
        Image images[] = {
            new Image("/com/hotelco/images/boracay.jpg"),
            new Image("/com/hotelco/images/pool.jpg"),
            new Image("/com/hotelco/images/resort.jpg"),
            new Image("/com/hotelco/images/rocks.jpg"),
            new Image("/com/hotelco/images/zanzibar.jpg"),
        };

            setChangeListeners(images);
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
     * Sets up the ChangeListeners for fo and fi.
     * The ChangeListeners will listen to the opacity value of fo and fi
     * which will set the next image to be shown an trigger the next FadeTransition.
     * @param images  holds all the images different which will be used for transitions
     */
    private void setChangeListeners(Image[] images) {
        fo.opacityProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if(oldValue.doubleValue() > newValue.doubleValue() && newValue.doubleValue() <= 0.65) {
                    if (!isFadingIn) {
                        isFadingIn = true;
                        Image nextImage = images[0];
                        System.arraycopy(images, 1, images, 0, images.length - 1);
                        images[images.length - 1] = nextImage;
                        fi.setImage(nextImage);
                        FadeTransition fadeIn = new FadeTransition(Duration.millis(3000), fi);
                        PauseTransition pauseTransition = new PauseTransition(Duration.seconds(1.65));
                        fadeIn.setFromValue(0.0);
                        fadeIn.setToValue(1.0);
                        fadeIn.setOnFinished(e -> {
                            pauseTransition.play();
                        });
                        pauseTransition.setOnFinished(e -> {
                            isFadingIn = false;
                            FadeTransition fadeOut = new FadeTransition(Duration.millis(3000), fi);
                            fadeOut.setFromValue(1.0);
                            fadeOut.setToValue(0.0);
                            fadeOut.play();
                        });
                        fadeIn.play();
                    }
                }
            }
        });

        fi.opacityProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if(oldValue.doubleValue() > newValue.doubleValue() && newValue.doubleValue() <= 0.65) {
                    if (!isFadingIn) {
                        isFadingIn = true;
                        Image nextImage = images[0];
                        System.arraycopy(images, 1, images, 0, images.length - 1);
                        images[images.length - 1] = nextImage;
                        fo.setImage(nextImage);
                        FadeTransition fadeIn = new FadeTransition(Duration.millis(3000), fo);
                        PauseTransition pauseTransition = new PauseTransition(Duration.seconds(1.65));
                        fadeIn.setFromValue(0.0);
                        fadeIn.setToValue(1.0);
                        fadeIn.setOnFinished(e -> {
                            pauseTransition.play();
                        });
                        pauseTransition.setOnFinished(e -> {
                            isFadingIn = false;
                            FadeTransition fadeOut = new FadeTransition(Duration.millis(3000), fo);
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