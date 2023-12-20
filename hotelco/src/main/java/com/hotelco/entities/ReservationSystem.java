package com.hotelco.entities;

import com.hotelco.connections.DatabaseConnection;
import com.hotelco.constants.DatabaseStatus;
import com.hotelco.utilities.DatabaseUtil;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Main system that drives the application and holds the active elements
 * with which the overall system interacts. Aggregates most elements and
 * holds the open database connection.
 * @author Daniel Schwartz
 */
public class ReservationSystem {
    /**
     * Status of the database, as an enum DatabaseStatus
     */
    private static DatabaseStatus status = DatabaseStatus.NOT_CONNECTED;
    /**
     * Connection to database. Not safe to use directly. Instead, use
     * getDabaseConnection().
     */
    private static Connection connection = DatabaseConnection.connectDB();
    /**
     * The logged in user
     */
    private static User currentUser;
    /**
     * The single reservation held by the Reservation System for processing.
     */
    private static Reservation currentReservation;
    /**
     * Gets the logged in user
     * @return the logged in user
     */
    public static User getCurrentUser(){return currentUser;}
    /**
     * Gets the active reservation held by the Reservation System
     * @return the active reservation held by the Reservation System
     */
    public static Reservation getCurrentReservation(){return currentReservation;}
    /**
     * Gets the current status of the DB as a DatabaseStatus enum
     * @return the current status of the DB as a DatabaseStatus enum
     */
    public static DatabaseStatus getDatabaseStatus(){return status;}
    /**
     * Logs in the supplied user
     * @param newUser user to be logged in
     */
    public static void setCurrentUser(User newUser) {currentUser = newUser;}
    /**
     * Sets the active reservation to be held by the Reservation System.
     * @param newReservation reservation to be held by the Reservation System
     */
    public static void setCurrentReservation(Reservation newReservation) {
        currentReservation = newReservation;
    }
    /**
     * Sets the current database status
     * @param newStatus current status of the DB
     */
    public static void setDatabaseStatus(DatabaseStatus newStatus){status = newStatus;}
    /**
     * Checks if the database is ready for processing
     * @return true if databse is ready, false if not
     */
    public static Boolean isReady(){return status == DatabaseStatus.READY;};
    /**
     * Logs out the logged in user.
     */
    public static void logout()
    {
        currentUser = null;
        currentReservation = null;
    }
    /**
     * Connects to the database. Reconnects if connection is closed. Changes
     * status to DatabaseStatus.PROCESSING. Every call should be coupled with
     * ReservationSystem.ready() after processing completes.
     * @return connection to the database
     */
    public static Connection getDatabaseConnection() {
        status = DatabaseStatus.PROCESSING;
        try{
            if (connection.isClosed()){
                connection = DatabaseConnection.connectDB();
            }
        }
        catch (SQLException e)
        {
            System.out.println(e);
            System.out.println("DatabaseConnection.getDatabaseConnection()");
        }
        return connection;
    }
    /**
     * Inserts a reservation into the database, associated with the
     * logged in user.
     */
    public static void book(){
        currentReservation.create();
        currentUser.fetch(true);
    }
    /**
     * Checks out every reservation whose check out date is today
     */
    public static void dailyCheckOut(){
        int i;
        Reservation[] todayCheckOuts = DatabaseUtil.getTodayCheckouts();
        if(todayCheckOuts != null){
            for(i = 0; i < todayCheckOuts.length; i++){
                todayCheckOuts[i].checkOut();
            }
        }
        else{
            //System.out.println("No checkouts found today.");
        }
        
    }
    /**
     * Checks in every reservation whose check in date is today
     */
    public static void dailyCheckIn(){
        System.out.println("dailyCheckIn() called");
        int i;
        Reservation[] todayCheckIns = DatabaseUtil.getTodayCheckIns();
        if (todayCheckIns != null){
            for(i = 0; i < todayCheckIns.length; i++){
                // System.out.println("Checking in Reservation " +
                //     todayCheckIns[i].getReservationId());
                todayCheckIns[i].checkIn();            
            }
        }
    }
    /**
     * Updates the current user and reservation from the database.
     */
    public static void update(){
        if (ReservationSystem.getCurrentUser() != null){
            ReservationSystem.getCurrentUser().fetch(true);
        }
        if (ReservationSystem.getCurrentReservation() != null){
            ReservationSystem.getCurrentReservation().fetch(true);
        }
    }
    /**
     * Dummy function that would connect and request payment
     * @param amount the amount to be paid
     * @return true if payment succeeded, false if payment fails
     */
    public static Boolean requestCreditCardPayment(BigDecimal amount){
        return true;
    }
    /**
     * Makes a payment for a reservation based on their user's credit card
     * @param reservation Reservation to be paid
     * @return true if payment succeeded, false if it failed
     */
    public static Boolean makePayment(Reservation reservation){
        Payment payment = new Payment(reservation);
        return payment.getPaymentId() != null;
    }
    
    /**
     * "Changes" a reservation by cancelling it and generating a new one.
     * @param oldReservation reservation to be cancelled
     * @param newReservation reservation to book
     */
    public static void changeReservation(Reservation oldReservation, Reservation newReservation){
        Reservation temp = ReservationSystem.getCurrentReservation();

        oldReservation.cancel(true);
        ReservationSystem.setCurrentReservation(newReservation);
        ReservationSystem.book();

        ReservationSystem.setCurrentReservation(temp);
    }
}
