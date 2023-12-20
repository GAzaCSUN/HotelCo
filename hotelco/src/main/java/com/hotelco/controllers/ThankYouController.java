package com.hotelco.controllers;

import java.time.format.DateTimeFormatter;

import com.hotelco.entities.Reservation;
import com.hotelco.utilities.FXMLPaths;
import com.hotelco.utilities.Instances;
import com.hotelco.utilities.ReservationCalculator;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

/**
 * The ThankYouController class is the associated controller class of the 'ThankYouGUI' view. 
 * It handles connection between the GUI and internal data.
 * 
 * @author      Grigor Azakian
 */
public class ThankYouController extends BaseController {

    /**
     * Text that will display a Reservation objects reservation ID.
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
     * Text that will display the Users first name.
     */
    @FXML
    private Text thankYou;
    
    /**
     * Text that will display the total cost of the Reservation.
     */
    @FXML
    private Text total;    


    /**
     * This method is called by pressing the 'BACK TO HOME' button.
     * It exits the 'ThankYouGUI' and enters the 'HomeGUI'.
     * @param event The 'mouse released' event that is triggered by pressing the 'BACK TO HOME' button.
     */
    @FXML
    private void switchToHomeScene(MouseEvent event) {
        Instances.getDashboardController().switchAnchor(FXMLPaths.HOME);
    }

    /**
     * This method will print the details of the current reservation onto the screen.
     * @param reservation The current reservation to print details for.
     */
    void writeReservationInfo(Reservation reservation) {
        reservationNumber.setText(Integer.toString(reservation.getReservationId()));
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        checkIn.setText(reservation.getStartDate().format(dateTimeFormatter));
        checkOut.setText(reservation.getEndDate().format(dateTimeFormatter));
        room.setText(reservation.getRoom().getRoomType().toPrettyString());
        thankYou.setText("Thank you, " + reservation.getUser().getFirstName());
        total.setText("$" + ReservationCalculator.calcTotal(reservation).toString());
    }

}
