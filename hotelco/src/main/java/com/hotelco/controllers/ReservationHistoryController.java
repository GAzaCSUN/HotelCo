package com.hotelco.controllers;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;

import com.hotelco.entities.Reservation;
import com.hotelco.entities.ReservationSystem;
import com.hotelco.utilities.FXMLPaths;
import com.hotelco.utilities.Instances;
import com.hotelco.utilities.ReservationCalculator;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

/**
 * The RHController class is the associated controller class of the
 * 'ReservationHistoryGUI' view. It handles connection between the GUI and
 * internal data.
 * 
 * @author Grigor Azakian
 */
public class ReservationHistoryController extends BaseController {

    /**
     * TableView that contains every TableColumn.
     */
    @FXML
    private TableView<Reservation> table;

    /**
     * TableColumn containing information about a bookings room type.
     */
    @FXML
    private TableColumn<Reservation, String> roomType;

    /**
     * TableColumn containing information about a bookings reservation number.
     */
    @FXML
    private TableColumn<Reservation, Integer> orderNumber;

    /**
     * TableColumn containing information about a bookings check in date.
     */
    @FXML
    private TableColumn<Reservation, LocalDate> checkInDate;

    /**
     * TableColumn containing information about a bookings check out date.
     */
    @FXML
    private TableColumn<Reservation, LocalDate> checkOutDate;

    /**
     * TableColumn containing information about a bookings current active status.
     */
    @FXML
    private TableColumn<Reservation, String> status;

    /**
     * TableColumn containing information about a bookings total cost.
     */
    @FXML
    private TableColumn<Reservation, String> total;
    /**
     * Text containing notifcation message.
     */
    @FXML
    private Text notification;

    /**
     * Called immediately upon controller creation. Sets up the parameters for the
     * data to be displayed in each TableColumn. Afterwards, it calls
     * displayOrders().
     */
    @FXML
    private void initialize() {
        table.getSelectionModel().selectedItemProperty().addListener((property, oldItem, newItem) -> {
            if (newItem != null) {
                ViewBookingController vbc = (ViewBookingController) Instances.getDashboardController()
                        .switchAnchor(FXMLPaths.VIEW_BOOKING);
                vbc.writeReservationInfo(newItem);
            }
        });

        roomType.setCellValueFactory(
                cell -> new SimpleStringProperty(cell.getValue().getRoom().getRoomType().toPrettyString()));
        orderNumber.setCellValueFactory(new PropertyValueFactory<Reservation, Integer>("reservationId"));
        checkInDate.setCellValueFactory(new PropertyValueFactory<Reservation, LocalDate>("startDate"));
        checkOutDate.setCellValueFactory(new PropertyValueFactory<Reservation, LocalDate>("endDate"));
        status.setCellValueFactory(cell -> new SimpleStringProperty(cell.getValue().getIsCancelled() ? "Cancelled"
                : cell.getValue().getEndDate().isBefore(LocalDate.now()) ? "Completed" : "Active"));
        total.setCellValueFactory(cell -> {
            Reservation reservation = cell.getValue();
            return new SimpleStringProperty("$" + ReservationCalculator.calcTotal(reservation).toString());
        });
        table.addEventFilter(MouseEvent.MOUSE_DRAGGED, Event::consume);
        displayOrders();

    }

    /**
     * Uses an array of reservations for the current user and passes it to the
     * TableView. This will set the data in each TableColumn.
     */
    private void displayOrders() {
        Task<ObservableList<Reservation>> task = new Task<ObservableList<Reservation>>() {
            @Override
            protected ObservableList<Reservation> call() throws Exception {
                Reservation[] reservation = ReservationSystem.getCurrentUser().getReservations();
                Collections.reverse(Arrays.asList(reservation));
                return FXCollections.observableArrayList(Arrays.asList(reservation));
            }
        };

        task.setOnSucceeded(e -> table.setItems(task.getValue()));

        new Thread(task).start();
    }

    /**
     * This method is a setter for the variable 'notification'. Which sets the text
     * to the given notifcation text.
     * 
     * @param text The string to set the variable 'notification' to.
     */
    void setNotification(String text) {
        notification.setText(text);
    }
}