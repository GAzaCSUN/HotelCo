package com.hotelco.controllers;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;

import com.hotelco.entities.Reservation;
import com.hotelco.entities.ReservationSystem;
import com.hotelco.utilities.DatabaseUtil;
import com.hotelco.utilities.FXMLPaths;
import com.hotelco.utilities.Instances;
import com.hotelco.utilities.ReservationCalculator;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

/**
 * The RHController class is the associated controller class of the
 * 'ReservationHistoryGUI' view. It handles connection between the GUI and
 * internal data.
 * 
 * @author Grigor Azakian, Bilin Pattasseril
 */
public class CheckOutController extends BaseController {

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
     * TableColumn containing information about a bookings total cost.
     */
    @FXML
    private TableColumn<Reservation, String> total;

      /**
     * List containing all the Reservation that are selected by User.
     */
    private List<Reservation> selectedReservations = new ArrayList<>();

    /**
     * Called immediately upon controller creation. Sets up the parameters for the
     * data to be displayed in each TableColumn. Afterwards, it calls
     * displayOrders().
     */
    @FXML
    private void initialize() {
        roomType.setCellValueFactory(
                cell -> new SimpleStringProperty(cell.getValue().getRoom().getRoomType().toPrettyString()));
        orderNumber.setCellValueFactory(new PropertyValueFactory<Reservation, Integer>("reservationId"));
        checkInDate.setCellValueFactory(new PropertyValueFactory<Reservation, LocalDate>("startDate"));
        checkOutDate.setCellValueFactory(new PropertyValueFactory<Reservation, LocalDate>("endDate"));
        total.setCellValueFactory(cell -> {
            Reservation reservation = cell.getValue();
            return new SimpleStringProperty("$" + ReservationCalculator.calcTotal(reservation).toString());
        });
        displayOrders();
        Platform.runLater(() -> {

        });
    }

    /**
     * Uses an array of reservations for the current user and passes it to the
     * TableView. This will set the data in each TableColumn.
     */
    private void displayOrders() {
        Task<ObservableList<Reservation>> task = new Task<ObservableList<Reservation>>() {
            @Override
            protected ObservableList<Reservation> call() throws Exception {
                Reservation reservations[] = DatabaseUtil.getUserCheckOuts(ReservationSystem.getCurrentUser());
                Collections.reverse(Arrays.asList(reservations));
                return FXCollections.observableArrayList(Arrays.asList(reservations));
            }
        };

        task.setOnSucceeded(e -> {
            table.setItems(task.getValue());
        });

        new Thread(task).start();
    }
     /**
     * Upon clicking on a reservation on a table it will retrieve the reservation
     * then it will call the toggle(reservation) function and finally clear the
     * selection.
     * 
     * @param event that triggers this function is toggleSelection upon mouse click
     *              on table.
     */
    @FXML
    void toggleSelection(MouseEvent event) {
        Reservation res = table.getSelectionModel().getSelectedItem();
        if (res != null) {
            toggle(res);
        }
        table.getSelectionModel().clearSelection();
    }

     /**
     * Moves a reservation into the selectedReservations ArrayList only if it is not
     * already in the list. If the reservation is already in the list, it will be
     * removed. Additionally, this method will recolor the table to visually
     * represent the selected reservation.
     * 
     * @param reservation the reserivation that was selected.
     */
    private void toggle(Reservation reservation) {
        if (selectedReservations.contains(reservation)) {
            selectedReservations.remove(reservation);

        } else {
            selectedReservations.add(reservation);
        }
        table.setRowFactory(view -> new TableRow<Reservation>() {
            @Override
            protected void updateItem(Reservation item, boolean empty) {
                super.updateItem(item, empty);
                if (selectedReservations.contains(item)) {
                    getStyleClass().add("table-row-cell-selectedtoggle");
                }
            }
        });
        table.refresh();

    }
    /**
     * Initiates the check-in process by calling the method upon pressing the
     * 'Check-Out' button. Adds the reservation to the selectedReservations list and
     * switches to the confirmationController scene.
     * 
     * @param event The event that triggers the check-out process
     */
    @FXML
    void checkOut(MouseEvent event) {
        if (!selectedReservations.isEmpty()) {
            for (Reservation reservation : selectedReservations) {
                reservation.setIsCheckedOut(true);
                reservation.push();
            }
            ConfirmationController cc = (ConfirmationController) Instances.getDashboardController()
                    .switchAnchor(FXMLPaths.CONFIRMATION);
            cc.setIsCheckin(false);
            cc.writeInfo(ReservationSystem.getCurrentUser());
        }
    }

}