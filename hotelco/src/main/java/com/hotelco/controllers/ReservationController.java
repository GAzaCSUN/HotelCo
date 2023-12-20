package com.hotelco.controllers;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import com.hotelco.constants.Constants;
import com.hotelco.constants.RoomType;
import com.hotelco.entities.CreditCard;
import com.hotelco.entities.Reservation;
import com.hotelco.entities.ReservationSystem;
import com.hotelco.entities.Room;
import com.hotelco.utilities.DatabaseUtil;
import com.hotelco.utilities.EmailGenerator;
import com.hotelco.utilities.FXMLPaths;
import com.hotelco.utilities.Instances;
import com.hotelco.utilities.ReservationCalculator;
import com.hotelco.utilities.TaxRate;
import com.hotelco.utilities.TextFormatters;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.DateCell;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import javafx.util.Callback;

/**
 * The RoomSearchController class is the associated controller class of the 'RoomSearchGUI' view. 
 * It handles connection between the GUI and internal data.
 * 
 * @author      Grigor Azakian
 */
public class ReservationController extends BaseController {

    /***************************************************************************
     *                                                                         *
     * JavaFX Nodes                                                            *
     *                                                                         *
     **************************************************************************/    

    /**
     * DatePicker that contains the date the user wants to be the first day of their booking.
     */
    @FXML
    private DatePicker startDate;

    /**
     * DatePicker that contains the date the user wants to be the last day of their booking.
     */
    @FXML
    private DatePicker endDate;

    /**
     * Text that contains the number of guests.
     */
    @FXML
    private Text guests;

    /**
     * Text that notifies user of improper payment details.
     */
    @FXML
    private Text paymentNotification;

    /**
     * Text that notifies user of empty DatePicker fields.
     */
    @FXML
    private Text dateNotification;

    /**
     * TextField that contains a users credit card number.
     */
    @FXML
    private TextField cardNumber;

    /**
     * TextField that contains a users credit cards expiration month.
     */
    @FXML
    private TextField expDateMonth;

    /**
     * TextField that contains a users credit cards expiration year.
     */    
    @FXML
    private TextField expDateYear;

    /**
     * TextField that contains a users credit cards CVC.
     */    
    @FXML
    private TextField CVC;

    /**
     * Text that displays the number of nights a user will be staying.
     */
    @FXML
    private Text nights;

    /**
     * Text that displays the tax rate.
     */
    @FXML
    private Text tax;

    /**
     * Text that displays a bookings total cost.
     */
    @FXML
    private Text total;

    /**
     * Text of a '/' that appears when entering expiration date.
     */
    @FXML
    private Text slash;

    /**
     * Text that displays the users current room choice.
     */
    @FXML
    private Text roomText;

    /**
     * Text that displays a bookings subtotal cost.
     */
    @FXML
    private Text rate;
    
     /**
     * Button named 'Change Room Type' that calls 'switchToRoomChoiceScene()' when pressed.
     */
    @FXML
    private Button changeButton;

    /***************************************************************************
     *                                                                         *
     * Instance Variables                                                      *
     *                                                                         *
     **************************************************************************/

     /**
      * RoomType that stored a users selected RoomType.
      */
    private RoomType room;

    /**
     * Boolean that is true if the card number TextField has been interacted with.
     */
    private boolean cardNumberIsInteractedWith = false;

    /**
     * Boolean that is true if either the expiration date month or year TextFields have been interacted with.
     */
    private boolean expDateIsInteractedWith = false;

    /**
     * Boolean that is true if the CVC TextField has been interacted with.
     */
    private boolean CVCIsInteractedWith = false;

    /**
     * Contains a Reservation to be cancelled if the user had a reservation to change.
     */
    private Reservation toCancel;

    /***************************************************************************
     *                                                                         *
     * ChangeListeners                                                         *
     *                                                                         *
     **************************************************************************/

     /**
      * ChangeListener associated with startDate.<p>
      * If startDate isn't set to null:<ul>
      * <li>Enables the ability to set endDate after making a selection.
      * <li>Resets error status.
      * <li>If endDate is already set, updates total cost.
      * </ul>
      */
    final ChangeListener<LocalDate> startDateChangeListener = new ChangeListener<LocalDate>() {
        @Override
        public void changed(ObservableValue<? extends LocalDate> observable, LocalDate oldValue,
                LocalDate newValue) {  
            if (newValue == null) {   
                return;
            }
            if (startDate.getStyleClass().contains("red-date-picker")) {
                startDate.getStyleClass().remove("red-date-picker");
                startDate.getStyleClass().add("normal-date-picker");
            }

            endDate.setDisable(false);
            if (endDate.getValue() != null) {
                updateTotals();
            }
        }
    };

     /**
      * ChangeListener associated with endDate.<p>
      * If endDate isn't set to null, resets error status and updates total costs.<p>
      * Since endDate is associated with an expiry date check, a change in endDate will
      * retrigger the expiry date check. 
      */    
    final ChangeListener<LocalDate> endDateChangeListener = new ChangeListener<LocalDate>() {
        @Override
        public void changed(ObservableValue<? extends LocalDate> observable, LocalDate oldValue,
                LocalDate newValue) {
            if (newValue == null) {
                return;
            }
            if (endDate.getStyleClass().contains("red-date-picker")) {
                endDate.getStyleClass().remove("red-date-picker");
                endDate.getStyleClass().add("normal-date-picker");
                dateNotification.setText("");
            }
            updateTotals();
            if (expDateIsInteractedWith) {
                setExpDateErrorStatus();   
            }
        }
    };

    /**
     * ChangeListener associated with the String property of expDateMonth.<p>
     * Creates a '/' and jumps to expDateYear upon field completion.
     * Manages if field properties are correct or false, and displays relevant error message.
     */
    final ChangeListener<String> expDateMonthStringChangeListener = new ChangeListener<String>() {
        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue,
                String newValue) {
            expDateIsInteractedWith = true;
            if (newValue.length() == 2) {
                expDateYear.requestFocus();
                slash.setVisible(true);
            }    
            refreshExpDateErrorStatus();
            if ((expDateMonth.getLength() == 2 && expDateYear.getLength() == 2)
            && !setExpDateErrorStatus()) {
                paymentNotification.setText("");
                setAllPaymentFieldErrorStatus();
            }
        }
    };

    /**
     * ChangeListener associated with the String property of expDateYear.<p>
     * Removes the '/' and jumps to expDateMonth upon field deletion.
     * Manages if field properties are correct or false, and displays relevant error message.
     */
    final ChangeListener<String> expDateYearStringChangeListener = new ChangeListener<String>() {
        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue,
                String newValue) {
            expDateIsInteractedWith = true;
            if (newValue.length() == 0 && oldValue.length() == 1) {
                expDateMonth.requestFocus();
                expDateMonth.positionCaret(expDateMonth.getLength() + 1);
                slash.setVisible(false);
            }
            refreshExpDateErrorStatus();  
            if ((expDateMonth.getLength() == 2 && expDateYear.getLength() == 2)
            && !setExpDateErrorStatus()) {
                paymentNotification.setText("");
                setAllPaymentFieldErrorStatus();
            }               
        }
    };

    /**
     * ChangeListener associated with the focus property of expDateMonth.<p>
     * Displays error message if user leaves focus without finishing entering MM field.
     * Sets a '/' if MM field is successfully entered.
     */
    final ChangeListener<Boolean> expDateMonthFocusChangeListener = new ChangeListener<Boolean>() {
        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            if (!newValue && expDateMonth.getLength() == 2) {
                slash.setVisible(true);
            }
            if (expDateIsInteractedWith && !expDateYear.getText().isEmpty()) {
                processIncompleteExpDate(newValue);
            }
        }
    };
  
    /**
     * ChangeListener associated with the focus property of expDateMonth.<p>
     * Displays error message if user leaves focus without finishing entering YY field.
     */    
    final ChangeListener<Boolean> expDateYearFocusChangeListener = new ChangeListener<Boolean>() {
        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            if (expDateIsInteractedWith) {
                processIncompleteExpDate(newValue);
            }
        }
    };     

    /**
     * ChangeListener associated with the focus property of cardNumber.<p>
     * Displays error message if user leaves focus without finishing entering credit card field.
     */       
    final ChangeListener<Boolean> cardNumberFocusChangeListener = new ChangeListener<Boolean>() {
        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            if (!newValue && cardNumberIsInteractedWith) {
                setCardNumberErrorStatus();
            }
        }
    };

    /**
     * ChangeListener associated with the String property of cardNumber.<p>
     * Resets error status if user successfully finishes entering credit card information.
     */           
    final ChangeListener<String> cardNumberStringChangeListener = new ChangeListener<String>() {
        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            cardNumberIsInteractedWith = true;
            if (cardNumber.getLength() >= 15 && !setCardNumberErrorStatus()) {
                setWhiteBorder(cardNumber);
                paymentNotification.setText("");
                setAllPaymentFieldErrorStatus();
            }
        }  
    };

     
    /**
     * ChangeListener associated with the focus property of CVC.<p>
     * Displays error message if user leaves focus without finishing entering CVC field.
     */          
    final ChangeListener<Boolean> CVCFocusChangeListener = new ChangeListener<Boolean>() {
        @Override
        public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
            if (!newValue && CVCIsInteractedWith) {
                setCVCErrorStatus();
            }
        }
    };          

    /**
     * ChangeListener associated with the String property of CVC.<p>
     * Resets error status if user successfully finishes entering CVC information.
     */        
    final ChangeListener<String> CVCStringChangeListener = new ChangeListener<String>() {
        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            CVCIsInteractedWith = true;
            if (CVC.getLength() >= 3 && !setCVCErrorStatus()) {
                setWhiteBorder(CVC);
                paymentNotification.setText("");
                setAllPaymentFieldErrorStatus();
            }
        }  
    };

    /***************************************************************************
     *                                                                         *
     * DayCellFactories                                                        *
     *                                                                         *
     **************************************************************************/  

     /**
      * DayCellFactory associated with startDate.<p>
      * Disables the ability to choose a date if:<ul>
      * <li>Day is before the current date
      * <li>Selected room type is fully booked on that day
      * <li>If endDate has been chosen, the day is not the same as the date selected in endDate
      * <li>If endDate has been chosen, the day is not after the date selected in endDate
      * </ul>
      */
    final Callback<DatePicker, DateCell> startDayCellFactory = new Callback<DatePicker, DateCell>() {
        @Override
        public DateCell call(DatePicker param) {
            return new DateCell() {
                @Override
                public void updateItem(LocalDate item, boolean empty) {
                    super.updateItem(item, empty);
                    LocalDate nextDay = item.plusDays(1);
                    Boolean availability = DatabaseUtil.checkAvailability(item, nextDay, room);
                    LocalDate currentDate = LocalDate.now();
                    setDisable(empty || item.compareTo(currentDate) < 0 || 
                        !availability || checkConflict(item));
                }

                private boolean checkConflict(LocalDate item) {
                    if (endDate.getValue() != null) {
                        if (item.isEqual(endDate.getValue())) {
                            return true;
                        }
                        return item.isAfter(endDate.getValue());
                    }
                    else {
                        return false;
                    }
                }
            };
        }
    };

     /**
      * DayCellFactory associated with endDate.<p>
      * Disables the ability to choose a date if:<ul>
      * <li>Day is before the current date
      * <li>Day is today
      * <li>Day is before the day selected in startDate.
      * <li>Selected room type is fully booked on that day
      * </ul>
      */    
    final Callback<DatePicker, DateCell> endDayCellFactory = new Callback<DatePicker, DateCell>() {
        @Override
        public DateCell call(DatePicker param) {
            return new DateCell() {
                @Override
                public void updateItem(LocalDate item, boolean empty) {
                    super.updateItem(item, empty);
                    Boolean availability = DatabaseUtil.checkAvailability(startDate.getValue(), item, room);
                    setDisable(empty || item.compareTo(startDate.getValue()) < 0 || !availability ||
                        item.isEqual(LocalDate.now()) || item.isEqual(startDate.getValue()));
                }
            };
        }
    }; 

    /**
     * This method is called immediately upon controller creation.
     * It applies the DayCellFactories and ChangeListeners to all relevant DatePicker and TextField variables.
     */
    @FXML
    private void initialize() {
        TextFormatters textFormatters = new TextFormatters();
        startDate.valueProperty().addListener(startDateChangeListener);
        startDate.setDayCellFactory(startDayCellFactory);
        endDate.setDayCellFactory(endDayCellFactory);
        endDate.valueProperty().addListener(endDateChangeListener);
        expDateMonth.setTextFormatter(textFormatters.EXP_DATE_MONTH);
        expDateMonth.textProperty().addListener(expDateMonthStringChangeListener);
        expDateMonth.focusedProperty().addListener(expDateMonthFocusChangeListener); 
        expDateYear.setTextFormatter(textFormatters.EXP_DATE_YEAR);          
        expDateYear.textProperty().addListener(expDateYearStringChangeListener);
        expDateYear.focusedProperty().addListener(expDateYearFocusChangeListener);
        cardNumber.setTextFormatter(textFormatters.CREDIT_CARD);
        cardNumber.focusedProperty().addListener(cardNumberFocusChangeListener);
        cardNumber.textProperty().addListener(cardNumberStringChangeListener);
        CVC.textProperty().addListener(CVCStringChangeListener);
        CVC.focusedProperty().addListener(CVCFocusChangeListener);
        CVC.setTextFormatter(textFormatters.CVC);        
        Platform.runLater(() -> {

        });
    }

    /**
     * Called when pressing the 'Book' button.<p> 
     * Starts by performing a series of checks:<p>
     * <ul>
     * <li>Checks to see if any of the date pickers are empty.
     * <li>Checks to see if any of the payment details are empty.
     * <li>Checks to see if payment details are accurate.
     * </ul>
     * If any of these checks fail, it will return from the function.<p>
     * Otherwise, creates the users desired reservation.
     * After creating the booking, it will enter 'ThankYouGUI'.
     * @param event The 'mouse released' event that is triggered by pressing the 'Book' button.
     */
    @FXML
    private void createBooking(MouseEvent event) {
        cardNumberIsInteractedWith = true;
        expDateIsInteractedWith = true;  
        CVCIsInteractedWith = true;      
        boolean datePickerStatus = isAnyDatePickerEmpty();
        boolean paymentFieldStatus = setAllPaymentFieldErrorStatus();
        
        if (datePickerStatus || paymentFieldStatus || !assignCard()) {
            return;
        }
        
        
        
        Reservation reservation = createReservation();
        ThankYouController tyc = (ThankYouController) Instances.getDashboardController().switchAnchor(FXMLPaths.THANK_YOU);
        tyc.writeReservationInfo(reservation);

        if (toCancel != null) {
            toCancel.cancel(false);
        }
    }

    /**
     * This method is called by pressing the '-' text to the right of the guest number.
     * It decrements the current guest number as long as the current guest number is greater than '1'.
     * @param event The 'mouse released' event that is triggered by pressing the '-' text to the left of the guest number.
     */
    @FXML
    private void decrementGuest(MouseEvent event) {
        if (Integer.parseInt(guests.getText()) > 1) {
            guests.setText(Integer.toString(Integer.parseInt(guests.getText()) - 1));
        }
    }

    /**
     * This method is called by pressing the '+' text to the right of the guest number.
     * It increments the current guest number as long as it is below the maximum amount of allowed guests.
     * @param event The 'mouse released' event that is triggered by pressing the '+' text to the right of the guest number.
     */
    @FXML
    private void incrementGuest(MouseEvent event) {
        if (Integer.parseInt(guests.getText()) < Constants.CAPACITIES.get(room)) {
            guests.setText(Integer.toString(Integer.parseInt(guests.getText()) + 1));
        }
    }

    /**
     * This method is called by pressing the 'Change Room Type' text.
     * It exits the 'ReservationGUI' and enters the 'RoomChoiceGUI'.
     * @param event The 'mouse released' event that is triggered by pressing the 'Change Room Type' text.
     */
    @FXML
    private void switchToRoomChoiceScene(MouseEvent event) {
        Instances.getDashboardController().switchAnchor(FXMLPaths.ROOMS);
    }

    /**
     * Sets all DatePickers to null and resets all Text displaying payment information to their default values.
     * @param event The 'mouse released' event that is triggered by pressing the Button labeled 'x'.
     */
    @FXML
    private void resetDates(MouseEvent event) {
        endDate.setValue(null);
        startDate.setValue(null);
        endDate.setDisable(true);
        nights.setText("0");
        rate.setText("$0.00");
        tax.setText("$0.00");
        total.setText("$0.00");
    }

    /**
     * Checks to see if any of the payment TextFields are empty.
     * Marks all empty TextFields with a red border.
     * @return true if any of the payment TextFields are empty, false if they are all filled
     */
    private boolean setAllPaymentFieldErrorStatus() {
        boolean CVCStatus = CVCIsInteractedWith ? setCVCErrorStatus() : true;
        boolean expDateStatus = expDateIsInteractedWith ? setExpDateErrorStatus() : true;
        boolean cardNumberStatus = cardNumberIsInteractedWith ? setCardNumberErrorStatus() : true;
        boolean result = CVCStatus || expDateStatus || cardNumberStatus;
        if (!result) {
            paymentNotification.setText("");
        }
        return result;
    }

    /**
     * Performs basic checks on the CVC TextField.
     * If the checks fail, sets the border of the CVC TextField to red and
     * writes an error message.
     * @return 'true' if an error was detected, 'false' otherwise.
     */
    private boolean setCVCErrorStatus() {
        if (CVC.getLength() < 3) {
            setRedBorder(CVC);
            paymentNotification.setText("Please enter a valid CVC");
            return true;
        }
        setWhiteBorder(CVC);
        return false;
    }

    /**
     * Performs basic checks on the card number TextField.
     * If the checks fail, sets the border of the card number TextField to red and
     * writes an error message.
     * @return 'true' if an error was detected, 'false' otherwise.
     */    
    private boolean setCardNumberErrorStatus() {
        if (cardNumber.getLength() < 15) {
            setRedBorder(cardNumber);
            paymentNotification.setText("Please enter a valid credit card number");
            return true;
        }
        setWhiteBorder(cardNumber);
        return false;
    }

    /**
     * Checks to see if any of the DatePickers are empty.
     * Marks all empty DatePickers with a red border.
     * @return true if any of the DatePickers are empty, false if they are all filled
     */    
    private boolean isAnyDatePickerEmpty() {
        DatePicker[] datePickers = new DatePicker[] {
            startDate, endDate
        };
        boolean empty = false;
        for (DatePicker datePicker: datePickers) {
            if (datePicker.getValue() == null) {
                empty = true;
                if (!datePicker.getStyleClass().contains("red-date-picker")) {
                    datePicker.getStyleClass().add("red-date-picker");
                }
                dateNotification.setText("Please fill out reservation details");
            }
        }
        return empty;
    }

    /**
     * Changes the border color of a TextField to red.
     * @param textField TextField to change the border color of.
     */
    private void setRedBorder(TextField textField) {
        textField.setStyle("-fx-border-color: #FF0000;");
    }

    /**
     * Changes the border color of a TextField to white.
     * @param textField TextField to change the border color of.
     */
    private void setWhiteBorder(TextField textField) {
        textField.setStyle("-fx-border-color: white;");
    }

    /**
     * Calculates booking cost based on users input and updates relevant Text fields.
     */
    private void updateTotals() {
        long nightsLong = ChronoUnit.DAYS.between(startDate.getValue(), endDate.getValue());
        nights.setText(Long.toString(nightsLong));
        BigDecimal subTotal = ReservationCalculator.calcSubTotal(startDate.getValue(), endDate.getValue(), room);
        rate.setText("$" + subTotal.toString());
        BigDecimal totalCost = ReservationCalculator.calcSubTotal(startDate.getValue(), endDate.getValue(), room).multiply(TaxRate.getTaxMultiplier()).setScale(2, RoundingMode.HALF_UP);
        total.setText("$" + totalCost.toString());
        tax.setText("$" + totalCost.subtract(subTotal).toString());
    }

    /**
     * Sets expiration date TextField borders to red and displays error message if
     * user goes out of focus without entering a valid expiration date.
     * @param newValue The focus state of any expiration date TextField.
     */
    private void processIncompleteExpDate(Boolean newValue) {
        if (!newValue && !(expDateMonth.getLength() == 2 && expDateYear.getLength() == 2)) {
            setErrorOnExpDateFields();
        }
    }

    /**
     * Sets expiration date TextField borders to white and clears error message if
     * user finished entering either MM or YY, or if the user deletes either the MM or YY fields. 
     * Used to refresh the error status if user is retrying to enter a valid expiration date.
     */    
    private void refreshExpDateErrorStatus() {
            setWhiteBorder(expDateMonth);
            setWhiteBorder(expDateYear);
    }

    /**
     * Sets expiration date TextField borders to red and displays error message if
     * entered expiration date has already passed.
     * @return true if expDate is valid, false otherwise
     */
    private boolean setExpDateErrorStatus() {
        if (expDateMonth.getLength() == 2 && expDateYear.getLength() == 2) {
            if (Integer.parseInt(expDateMonth.getText()) > 12) {
                setErrorOnExpDateFields();
                return true;
            }
            if (endDate.getValue() != null &&  (parseExpDate().plusMonths(1).isBefore(endDate.getValue()))) {
                    setErrorOnExpDateFields();
                    return true;                    
            }            
            if (parseExpDate().plusMonths(1).isBefore(LocalDate.now())) {
                setErrorOnExpDateFields();
                return true;    
            }
            setWhiteBorder(expDateMonth);
            setWhiteBorder(expDateYear);
            return false;
        }
        setErrorOnExpDateFields();
        return true;
    }

    /**
     * Parses the users entered expiration date.
     * @return LocalDate of entered expiration date.
     */
    private LocalDate parseExpDate() {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MM yy");
        YearMonth yearMonth = YearMonth.parse(expDateMonth.getText() + " " + expDateYear.getText(), dateFormatter);
        LocalDate expDate = yearMonth.atDay(1);      
        return expDate;
    }

    /**
     * Sets a red border on expiration date month and year TextFields
     * as well as writes an error message on screen.
     */
    private void setErrorOnExpDateFields() {
        setRedBorder(expDateMonth);
        setRedBorder(expDateYear);        
        paymentNotification.setText("Please enter a valid expiry date");        
    }

    /**
     * Verifies a users credit card information, and assigns CreditCard to a User if valid.
     * If any details are inaccurate, sets payment TextField borders to red and displays an error message.
     * @return true if card details are accurate, false otherwise
     */
    private boolean assignCard() {
        Boolean result = false;
        CreditCard card = new CreditCard(
            cardNumber.getText(), CVC.getText(), parseExpDate(),
            ReservationSystem.getCurrentUser()
        );

        if (card.checkUserConflict()){
            paymentNotification.setText("Credit card is already in use by another user");
            result = false;  
        }
        else if (card.assign()) {
            result = true;
                      
        }
        else {
            paymentNotification.setText("Please check your payment details");
            setRedBorder(CVC);
            setRedBorder(cardNumber);
            setRedBorder(expDateMonth);
            setRedBorder(expDateYear);
            result = false;
        }
        
        return result;
    }

    /**
     * Creates a reservation based on users input.
     * @return The newly created Reservation.
     */
    private Reservation createReservation() {
        Room room = new Room(
            DatabaseUtil.findEmptyRoom(
                startDate.getValue(), endDate.getValue(),
                this.room));
        Reservation reservation = new Reservation(
            room, startDate.getValue(), endDate.getValue(),
            ReservationSystem.getCurrentUser(), Integer.parseInt(guests.getText()));
        ReservationSystem.setCurrentReservation(reservation);
        ReservationSystem.book();
        EmailGenerator.reservationConfirmation(reservation);
        return ReservationSystem.getCurrentReservation();
    }

    /**
     * <b>This function must be manually called upon switching to ReservationGUI.</b><p>
     * Sets the users chosen RoomType upon controller creation.
     * @param roomType Users chosen RoomType
     */
    void setRoomType(RoomType roomType) {
        this.room = roomType;
        roomText.setText(roomType.toPrettyString());
    }

    /**
     * Sets up scene for the relevant Reservation that must be changed.
     * Called when entering scene from ViewBookingGUI.
     * @param reservation Reservation to be cancelled upon successful booking
     */
    void setToCancel(Reservation reservation) {
        toCancel = reservation;
        room = toCancel.getRoom().getRoomType();
        roomText.setText(room.toPrettyString());
        Platform.runLater(() -> changeButton.setDisable(true));
    }



}