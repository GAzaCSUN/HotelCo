package com.hotelco.entities;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import com.hotelco.administrator.Settings;
import com.hotelco.utilities.DatabaseUtil;
import com.hotelco.utilities.ReservationCalculator;
/**
 * Maintains the payment associated with a reservation.
 * @author Daniel Schwartz
 */
public class Payment {
    /**
     * Total amount after calculation.
     */
    private BigDecimal amount;
    /**
     * Unique ID for payment.
     */
    private Integer paymentId;
    /**
     * Time payment is created.
     */
    private LocalDateTime timeOfPayment;
    /**
     * Gets the amount.
     * @return the amount
     */
    public BigDecimal getAmount(){return amount;}
    /**
     * Gets the paymentId.
     * @return the paymentId
     */
    public Integer getPaymentId(){return paymentId;}
    /**
     * Gets the time of payment
     * @return the time of payment
     */
    public LocalDateTime getTimeOfPayment(){return timeOfPayment;}
    /**
     * Sets the amount due by this payment
     * @param newAmount the new amount for this payment
     */
    public void setAmount(BigDecimal newAmount){amount = newAmount;}
    /**
     * Sets the paymentId
     * @param newPaymentId the new paymentId
     */
    public void setPaymentId(Integer newPaymentId){paymentId = newPaymentId;}
    /**
     * Sets the time of payment
     * @param newTimeOfPayment the new time of payment
     */
    public void setTimeOfPayment(LocalDateTime newTimeOfPayment){
        timeOfPayment = newTimeOfPayment;
    }
    /**
     * Attemps to pay for the active reservation
 * @param reservation reservation to pay
     */
    public Payment(Reservation reservation){
        Reservation temp = ReservationSystem.getCurrentReservation();
        
        ReservationSystem.setCurrentReservation(reservation);
        if (LocalTime.now().isBefore(Settings.CHECK_OUT_TIME)
            && LocalDate.now().isBefore(reservation.getEndDate())){
            reservation.setEndDate(LocalDate.now());
            reservation.push();
        }
        amount = ReservationCalculator.calcTotal(reservation);
        timeOfPayment = LocalDateTime.now();
        if(ReservationSystem.requestCreditCardPayment(amount)){
            push();
        }

        ReservationSystem.setCurrentReservation(temp);
    }
    
    /**
     * Inserts a new paid payment into database. ReservationSystem's
     * currentReservation will be associated with this payment in the database.
     * Note that there is no push function to update a payment in the database,
     * only to insert a new payment.
     */
    public void push(){
        PreparedStatement ps1 = null;
        PreparedStatement ps2 = null;
        ResultSet rs = null;
        DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
        String sqlQuery = "INSERT INTO payments " +
            "SET amount = " + amount +
            ", time = '" + formatter.format(timeOfPayment) +
            "', reservation_id = " +
            ReservationSystem.getCurrentReservation().getReservationId();
        Connection con = ReservationSystem.getDatabaseConnection();

        try {
            ps1 = con.prepareStatement(sqlQuery);
            ps1.execute();
            ps2 = con.prepareStatement("SELECT LAST_INSERT_ID() as id");
            rs = ps2.executeQuery();
            if(rs.next()){
                paymentId = rs.getInt("id");
            }
        }
        catch (SQLException e){ 
            System.out.println("Payment.push()");
            System.out.println(Thread.currentThread().getStackTrace()[2].getLineNumber());
            System.out.println(Thread.currentThread().getStackTrace()[2].getMethodName());
            System.out.println(e);
        }
        DatabaseUtil.ready();
    }
}
