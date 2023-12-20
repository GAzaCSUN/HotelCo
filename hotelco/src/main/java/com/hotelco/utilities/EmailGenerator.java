package com.hotelco.utilities;

import java.time.format.DateTimeFormatter;

import com.hotelco.entities.Reservation;
import com.hotelco.entities.ReservationSystem;
import com.hotelco.entities.User;

/**
* Contains different email templates and starts the process of sending
* an email.
* @author      Grigor Azakian
*/
public class EmailGenerator {

    /**
     * Ensures that EmaiLGenerator is a utility class that cannot be created
     * as an object.
     */    
    private EmailGenerator() {}
    
    /**
     * HotelCo signature that should always appear at the end of an email.
     */
    public static final String SIGNATURE = "Phone: 818 - 555 - 1337\r\n" + //
            "Email: HotelCoDesk@gmail.com\r\n" + //
            "Website: hotelco.hotel.co\r\n" + //
            "Address: 18111 Nordhoff St, Northridge, CA 91330\r\n\n" + //
            "Remember, at Hotel Co.,\n~We do hotels~";

    /**
     * Sends an email to a user upon reservation creation.
     * @param reservation Reservation to write details from.
     */
    public static void reservationConfirmation(Reservation reservation) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd/yy");
        String subject = "Reservation confirmation";
        String message = "Hello " + reservation.getUser().getFirstName() + " " +
            reservation.getUser().getLastName() +
            ", and thank you for booking with Hotel Co.\n\n" +
            "Please find your booking details below:\n\n" +
            "Reservation number: " + reservation.getReservationId() +
            "\nStart Date: "  + reservation.getStartDate().format(dateTimeFormatter) +
            "\nEnd Date: "  + reservation.getEndDate().format(dateTimeFormatter) +
            "\nNumber of guests: "  + reservation.getGroupSize() +
            "\nRoom type: "  + reservation.getRoom().getRoomType().toPrettyString() +
            "\n\nWe hope you thoroughly enjoy your stay with us." +
            "\n\nSincerely,\nHotel Co.\n";
        SendMail.startSend(reservation.getUser().getEmail(), subject, message);
    }

    /**
     * Sends an email to a user upon payment failure.
     * @param reservationID Number of reservation that has had a payment failure.
     * @param user User that must rectify payment failure.
     */
    public static void paymentWarning(Integer reservationID, User user) {
            String subject = "Reservation " + reservationID + " payment";
            String message = "Dear " + user.getFirstName() + " " +
            user.getLastName() + ", it has come to our attention that " +
            "your payment could not be processed at the time of checkout. " +
            "Please ensure that payment is promptly issued to Hotel Co. to avoid " +
            "further charges.\n\t\tSincerely, Hotel Co.";
            SendMail.startSend(
                    ReservationSystem.getCurrentUser().getEmail(), subject, message);
    }

    /**
     * Sends an email to a user upon successful password reset.
     * @param tempPassword Temporary password created upon password reset.
     * @param user User that reset their password.
     */
    public static void resetPassword(String tempPassword, User user) {
        String subject = "Password Reset";
        String message = "Hello," + user.getFirstName()+" "+ user.getLastName()+","+
            "\n\nYou have requested a password reset for your account. Your new password is:\n\n" +
            tempPassword +
            "\n\nPlease use this  password to log in" +
             "\n\nSincerely,\nHotel Co.";
        SendMail.startSend(user.getEmail(), subject, message);
    }
}