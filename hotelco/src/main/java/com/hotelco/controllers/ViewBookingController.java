package com.hotelco.controllers;

import java.time.format.DateTimeFormatter;

import com.hotelco.entities.Reservation;
import com.hotelco.utilities.FXMLPaths;
import com.hotelco.utilities.Instances;
import com.hotelco.utilities.ReservationCalculator;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

/**
 * The ViewBookingController class is the associated controller class of the
 * 'ViewBookingGUI' view. It handles connection between the GUI and internal
 * data.
 * 
 * @author Grigor Azakian
 */
public class ViewBookingController extends BaseController {

    /**
     * Text that will display a Reservation objects reservation number.
     */
    @FXML
    private Text reservationNumber;

    /**
     * Text that will display a Reservation objects check-in date.
     */
    @FXML
    private Text checkIn;

    /**
     * Text that will display a Reservation objects check-out date.
     */
    @FXML
    private Text checkOut;

    /**
     * Text that will display the room type of a Reservation.
     */
    @FXML
    private Text room;

    /**
     * Text that will display the total cost of the Reservation.
     */
    @FXML
    private Text total;

    /**
     * Button that will call cancel() method when pressed
     */
    @FXML
    private Button cancelButton;
    /**
     * Button that will call change() method when pressed
     */
    @FXML
    private Button changeButton;
    /**
     * reserviation that will hold the reservation selected.
     */
    private Reservation reservation;

    /**
     * This method is called by pressing the 'Cancel' button. It changes the
     * right anchorpane to the ReservationHistoryGUI scene
     * 
     * @param event The 'mouse released' event that is triggered by pressing the
     *              'cancel' button.
     */
    @FXML
    private void cancel(MouseEvent event) {
        reservation.cancel(true);
        ReservationHistoryController rhc = (ReservationHistoryController) Instances.getDashboardController()
                .switchAnchor(FXMLPaths.RHGUI);
        rhc.setNotification("Reservation successfully cancelled");
    }
    /**
     * This method is called by pressing the 'Change' button. It changes the
     * right anchorpane to the ReservationGUI scene
     * 
     * @param event The 'mouse released' event that is triggered by pressing the
     *              'Change' button.
     */
    @FXML
    private void change(MouseEvent event) {
        ReservationController rc = (ReservationController) Instances.getDashboardController()
                .switchAnchor(FXMLPaths.RESERVATION);
        rc.setToCancel(reservation);
    }

    /**
     * This method will print the details of the current reservation onto the
     * screen.
     * 
     * @param reservation The current reservation to print details for.
     */
    void writeReservationInfo(Reservation reservation) {
        this.reservation = reservation;
        reservationNumber.setText(Integer.toString(reservation.getReservationId()));
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        checkIn.setText(reservation.getStartDate().format(dateTimeFormatter));
        checkOut.setText(reservation.getEndDate().format(dateTimeFormatter));
        room.setText(reservation.getRoom().getRoomType().toPrettyString());
        total.setText("$" + ReservationCalculator.calcTotal(reservation).toString());

        if (reservation.getIsCancelled() || reservation.getIsCheckedOut()) {
            cancelButton.setDisable(true);
            changeButton.setDisable(true);
        }
    }

}
