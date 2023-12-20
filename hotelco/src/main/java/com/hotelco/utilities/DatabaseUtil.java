package com.hotelco.utilities;

import com.hotelco.constants.DatabaseStatus;
import com.hotelco.constants.RoomType;
import com.hotelco.entities.Reservation;
import com.hotelco.entities.ReservationSystem;
import com.hotelco.entities.User;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
/**
 * Utility class to check if user ids or emails exist.
 * @author Daniel Schwartz, John Lee
 */
public class DatabaseUtil{
/**
 * Checks if ID exists in the database.
 * @param userId id to check
 * @return true if id exists in the database, false if not.
 */
    public static Boolean doesIdExist(Integer userId){
        Boolean result = false;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sqlQuery =
            "SELECT count(*) as total FROM users where user_id = " + userId;
        Connection con = ReservationSystem.getDatabaseConnection();

        try {
            ps = con.prepareStatement(sqlQuery);
            rs = ps.executeQuery();
            if(rs.next()){
                result = rs.getInt("total") == 1;
            }
                    }
        catch(SQLException e)
        {
            System.out.println("doesIdExist()");
            System.out.println(e);
        }
        DatabaseUtil.ready();
        return result;
    }
    /**
    * Checks if email exists in the database.
    * @param email email to check
    * @return true if email exists in the database, false if not.
    */
    public static Boolean doesEmailExist(String email){
        Boolean result = false;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sqlQuery = "SELECT count(*) AS total FROM users where email = '" +
                email + "'";
        Connection con = ReservationSystem.getDatabaseConnection();
        
        try {
            ps = con.prepareStatement(sqlQuery);
            rs = ps.executeQuery();
            if(rs.next()){
                result = rs.getInt("total") == 1;
            }
                    }
        catch (SQLException e){
            System.out.println("DatabaseUtil.doesEmailExist()");
            System.out.println(Thread.currentThread().getStackTrace()[2].getLineNumber());
            System.out.println(Thread.currentThread().getStackTrace()[2].getMethodName());
            System.out.println(e);
        }
        DatabaseUtil.ready();
        return result;
    };

    /**
     * Gets the rate from the database for the supplied RoomType.
     * @param roomType the room type for this rate request 
     * @return the rate of this room type
     */
    public static BigDecimal getRate(RoomType roomType){
        BigDecimal rate = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sqlQuery = "SELECT rate FROM rates " +
            "WHERE type = '" + roomType.toString().toLowerCase() + "'";
        Connection con = ReservationSystem.getDatabaseConnection();
        
        try {
            ps = con.prepareStatement(sqlQuery);
            rs = ps.executeQuery();
            if(rs.next()){
                rate = rs.getBigDecimal("rate");
            }
                    }
        catch (SQLException e){
            System.out.println("DatabaseUtil.getRate");
            System.out.println(Thread.currentThread().getStackTrace()[2].getLineNumber());
            System.out.println(Thread.currentThread().getStackTrace()[2].getMethodName());
            System.out.println(e);
        }
        DatabaseUtil.ready();
        return rate;
    }

    /**
     * Helper function that starts a recursive search for availability ranges
     * @param startDate start date to check, inclusive
     * @param endDate end date to check, exclusive
     * @param roomtype room type  for which to check availabilities
     * @return a start-date-based array of availabilities for the supplied range
     */
    public static Boolean[] binaryAvailabilitySearch(
        LocalDate startDate, LocalDate endDate, RoomType roomtype){
        Boolean result[] = new Boolean[0];
        Long left = startDate.toEpochDay();
        Long right = endDate.toEpochDay() - 1;

        ArrayList<Boolean> availabilities = getAvailableDays(left, right, roomtype);

        result = new Boolean[availabilities.size()];
        availabilities.toArray(result);

        return result;
    }

    /**
     * Gets a today-based array of availabilities for the supplied number of days
     * and {@link RoomType}. For just today's availability, set numDays = 0.
     * @param numDays number of days into the future for which to retreive
     * availabilities
     * @param roomType room type for which to check availabilities
     * @return a today-based array of availabilities for the supplied number of
     * days
     */
    public static Boolean[] getAvailabilities(Integer numDays, RoomType roomType){
        LocalDate today = LocalDate.now();

        return binaryAvailabilitySearch(today, today.plusDays(numDays), roomType);
    }

    /**
     * Recursive check for availabilities.
     * @param left epoch day of leftmost date to check
     * @param right epoch day of rightmost date to check
     * @param roomType room type for which to check availabilities
     * @return a list of availabilities for the supplied range
     */
    public static ArrayList<Boolean> getAvailableDays(
        Long left, Long right, RoomType roomType){
        ArrayList<Boolean> result = new ArrayList<Boolean>();
        ArrayList<Boolean> lowList = new ArrayList<Boolean>();
        ArrayList<Boolean> highList = new ArrayList<Boolean>();
        Long numDays = right - left + 1;
        Long low, high, lowLen, highLen;

        if(checkAvailability(
            LocalDate.ofEpochDay(left), LocalDate.ofEpochDay(right + 1), roomType)){
                System.out.println(
                    LocalDate.ofEpochDay(left) + " - " + LocalDate.ofEpochDay(right));
            for (int i = 0; i <= numDays; i++){
                result.add(true);
            }
            return result;
        }
        else if (numDays == 1){
            result.add(false);
            return result;
        }
        
        high = left + numDays/2;
        low = high - 1;
        

        if (checkAvailability(
            LocalDate.ofEpochDay(left), LocalDate.ofEpochDay(low + 1), roomType)){
            lowLen = low - left + 1;
            for (int i = 0; i < lowLen; i++){
                lowList.add(true);
            }
        }
        else {
            lowList.addAll(getAvailableDays(left, low, roomType));
        }

        if (checkAvailability(
            LocalDate.ofEpochDay(high), LocalDate.ofEpochDay(left + numDays), roomType)){
            highLen = right - high + 1;
            for (int i = 0; i < highLen; i++){
                highList.add(true);
            }
        }
        else {
            highList.addAll(getAvailableDays(high, left + numDays - 1, roomType));
        }

        result.addAll(lowList);
        result.addAll(highList);
        
        return result;
    }
    /**
     * Checks the availability of a room type within a given date.
     * @param startDate start date to check
     * @param endDate end date to check
     * @param roomType room type to check
     * @return the availability of a room with supplied parameters
     */
    public static Boolean checkAvailability(
            LocalDate startDate, LocalDate endDate, RoomType roomType){
        Boolean result = false;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sqlQuery =
            "SELECT COUNT(room_num) as total " +
            "FROM (" +
                "SELECT room_num " +
                "FROM rooms " +
                "WHERE room_num " +
                "NOT IN (" +
                    "SELECT room_num " + 
                    "FROM reservations " +
                    "WHERE start_date < '" + Date.valueOf(endDate) + "' " +
                    "AND end_date >= '" + Date.valueOf(startDate) + "')" +
                "AND room_type = '" + roomType.toString() + "' " +
                "LIMIT 1) AS T";
        Connection con = ReservationSystem.getDatabaseConnection();
    
        try {
            ps = con.prepareStatement(sqlQuery);
            rs = ps.executeQuery();
            if(rs.next()){
                result = rs.getBoolean("total");
            }
        }
        catch (SQLException e){
            System.out.println("DatabaseUtil.checkAvailability()");
            System.out.println(Thread.currentThread().getStackTrace()[2].getLineNumber());
            System.out.println(Thread.currentThread().getStackTrace()[2].getMethodName());
            System.out.println(e);
        }
        DatabaseUtil.ready();
        return result;
    }
    /**
     * Checks the database for room availability by room type, on each and every
     * day in a range. Not very efficient, but for a maximum range of one month,
     * it will be sufficiently fast for the user.
     * 
     * @param startDate beginning of desired availabilities range
     * @param endDate end of desired availabilities range
     * @param roomType room type for which to check availability
     * @return an ordered array of availibitilies. Element 0 is the availability
     * on the startDate, element n is the availability n days after startDate.
     */
    public static Boolean[] getAvailabilities(
            LocalDate startDate, LocalDate endDate, RoomType roomType){
        Integer periodLen = new BigDecimal(
            ChronoUnit.DAYS.between(startDate, endDate)).intValueExact();
        Boolean[] result = new Boolean[periodLen];
        LocalDate checkDate = startDate;
        LocalDate nextDate;
    
        for (Integer i = 0; i < periodLen; i++){
            nextDate = checkDate.plusDays(1);
            result[i] = checkAvailability(checkDate, nextDate, roomType);
            checkDate = nextDate;
        }
        return result;
    }
    /**
     * Checks how many rooms are currently occupied.
     * @param startDate the start date to check
     * @param endDate the end date to check
     * @param roomType the room type to check
     * @return the number of occupied rooms.
     */
    public static Integer countOccupiedRooms(
        LocalDate startDate, LocalDate endDate, RoomType roomType){
        Integer result = 0;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sqlQuery = "SELECT COUNT(reservations.room_num)" + 
            "AS total FROM reservations " + 
            "INNER JOIN rooms ON reservations.room_num = rooms.room_num " + 
            "WHERE ('" + Date.valueOf(startDate) + "' >= start_date AND '" +
                Date.valueOf(startDate) + "' <= end_date) " +
            "OR ('" + Date.valueOf(endDate) + "' >= start_date AND '" +
                Date.valueOf(endDate) + "' <= end_date) " +
            "OR (start_date >= '" + Date.valueOf(startDate) +
                "' AND start_date <= '" + Date.valueOf(endDate) + "') " +
            "OR (end_date >= '" + Date.valueOf(startDate) +
                "' AND end_date <= '" + Date.valueOf(endDate) + "') " +
            "AND room_type = '" + roomType.toString() + "' " +
            "AND is_cancelled = 0";;
        Connection con = ReservationSystem.getDatabaseConnection();
        
        try {
            ps = con.prepareStatement(sqlQuery);
            rs = ps.executeQuery();
            if(rs.next()){
                result = rs.getInt("total");
            }
        }
        catch (SQLException e){
            System.out.println("DatabaseUtil.countOccupiedRooms()");
            System.out.println(Thread.currentThread().getStackTrace()[2].getLineNumber());
            System.out.println(Thread.currentThread().getStackTrace()[2].getMethodName());
            System.out.println(e);
        }
        DatabaseUtil.ready();
        return result;
    }
    /**
     * Finds the next empty room of the supplied type.
     * @param startDate start date to check
     * @param endDate end date to check
     * @param roomType room type to check
     * @return gives a room number of an empty room.
     */
    public static Integer findEmptyRoom(
        LocalDate startDate, LocalDate endDate, RoomType roomType){
        Integer result = 0;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sqlQuery = "SELECT room_num " + 
            "FROM rooms " + 
            "WHERE room_num NOT IN (" + 
                "SELECT room_num " + 
                "FROM reservations " + 
                "WHERE start_date < '" + Date.valueOf(endDate) +
                "' AND end_date >= '" + Date.valueOf(startDate) + "') " + 
            "AND room_type = '" + roomType.toString() + "'" + 
            "LIMIT 1";
        Connection con = ReservationSystem.getDatabaseConnection();
    
        try {
            ps = con.prepareStatement(sqlQuery);
            rs = ps.executeQuery();
            if(rs.next()){
                result = rs.getInt("room_num");
            }
        }
        catch (SQLException e){
            System.out.println("DatabaseUtil.findEmptyRoom()");
            System.out.println(Thread.currentThread().getStackTrace()[2].getLineNumber());
            System.out.println(Thread.currentThread().getStackTrace()[2].getMethodName());
            System.out.println(e);
        }
        DatabaseUtil.ready();
        return result;
    }
    /**
     * Gets all the reservations with today as their check out date.
     * @return the reservations with today as their check out date
     */
    public static Reservation[] getTodayCheckouts(){
        ArrayList<Reservation> reservationList = new ArrayList<Reservation>();
        Reservation[] result = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sqlQuery = "SELECT * " + 
            "FROM reservations " + 
            "WHERE end_date = '" + Date.valueOf(LocalDate.now()) + "' " +
            "AND is_checked_in = 1";
        Connection con = ReservationSystem.getDatabaseConnection();
        try {
            ps = con.prepareStatement(sqlQuery);
            rs = ps.executeQuery();
            while(rs.next()){
                reservationList.add(
                    new Reservation(rs.getInt("reservation_id"), true));
                DatabaseUtil.processing();
            }
            result = new Reservation[reservationList.size()];
            reservationList.toArray(result);
        }
        catch (SQLException e){
            System.out.println("DatabaseUtil.getTodayCheckouts()");
            System.out.println(Thread.currentThread().getStackTrace()[2].getLineNumber());
            System.out.println(Thread.currentThread().getStackTrace()[2].getMethodName());
            System.out.println(e);
        }
        DatabaseUtil.ready();
        return result;
    }
    /**
     * Gets all the reservations with today as their check in date.
     * @return the reservations with today as their check in date
     */
    public static Reservation[] getTodayCheckIns(){
        ArrayList<Reservation> reservationList = new ArrayList<Reservation>();
        Reservation[] result = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sqlQuery = "SELECT * " + 
            "FROM reservations " + 
            "WHERE start_date = '" + Date.valueOf(LocalDate.now()) + "' " +
            "AND is_checked_in = 0";
        Connection con = ReservationSystem.getDatabaseConnection();
    
        try {
            ps = con.prepareStatement(sqlQuery);
            rs = ps.executeQuery();
            while(rs.next()){
                reservationList.add(
                    new Reservation(rs.getInt("reservation_id"), true));
                DatabaseUtil.processing();
            }
            result = new Reservation[reservationList.size()];
            reservationList.toArray(result);
        }
        catch (SQLException e){
            System.out.println("DatabaseUtil.getTodayCheckIns()");
            System.out.println(Thread.currentThread().getStackTrace()[2].getLineNumber());
            System.out.println(Thread.currentThread().getStackTrace()[2].getMethodName());
            System.out.println(e);
        }
        DatabaseUtil.ready();
        return result;
    };

    /**
     * Gets all reservations in the database.
     * @param withReservations whether the fetch should populate each user's
     * reservations
     * @return all reservations in the database.
     */
    public static Reservation[] getAllReservations(Boolean withReservations){
        ArrayList<Reservation> reservationList = new ArrayList<Reservation>();
        Reservation[] result = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sqlQuery = "SELECT * FROM reservations";
        Connection con = ReservationSystem.getDatabaseConnection();
        try {
            ps = con.prepareStatement(sqlQuery);
            rs = ps.executeQuery();
            while(rs.next()){
                reservationList.add(
                    new Reservation(rs.getInt("reservation_id"), withReservations));
                DatabaseUtil.processing();
            }
            result = new Reservation[reservationList.size()];
            reservationList.toArray(result);
        }
        catch (SQLException e){
            System.out.println("DatabaseUtil.getAllReservations()");
            System.out.println(Thread.currentThread().getStackTrace()[2].getLineNumber());
            System.out.println(Thread.currentThread().getStackTrace()[2].getMethodName());
            System.out.println(e);
        }
        DatabaseUtil.ready();
        return result;
    }

    /**
     * Gets the current user's potential check-ins.
     * @param user the user for whom to retrieve their check-ins
     * @return the current user's reservations from today.
     */
    public static Reservation[] getUserCheckIns(User user){
        ArrayList<Reservation> reservationList = new ArrayList<Reservation>();
        Reservation[] result = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sqlQuery = "SELECT * " + 
            "FROM reservations " + 
            "WHERE start_date = '" + Date.valueOf(LocalDate.now()) + "' " +
            "AND user_id = " + user.getUserId() + " " +
            "AND is_checked_in = 0 " +
            "AND is_cancelled = 0";
        Connection con = ReservationSystem.getDatabaseConnection();
    
        try {
            ps = con.prepareStatement(sqlQuery);
            rs = ps.executeQuery();
            while(rs.next()){
                reservationList.add(
                    new Reservation(rs.getInt("reservation_id"), false));
                DatabaseUtil.processing();
            }
            result = new Reservation[reservationList.size()];
            reservationList.toArray(result);
        }
        catch (SQLException e){
            System.out.println("DatabaseUtil.getTodayCheckIns()");
            System.out.println(Thread.currentThread().getStackTrace()[2].getLineNumber());
            System.out.println(Thread.currentThread().getStackTrace()[2].getMethodName());
            System.out.println(e);
        }
        DatabaseUtil.ready();
        return result;
    };

    /**
     * Gets the current user's potential check-outs.
     * @param user the user for whom to retrieve their check-outs.
     * @return the current user's check-outs from today.
     */
    public static Reservation[] getUserCheckOuts(User user){
        ArrayList<Reservation> reservationList = new ArrayList<Reservation>();
        Reservation[] result = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sqlQuery = "SELECT * " + 
            "FROM reservations " + 
            "WHERE end_date >= '" + Date.valueOf(LocalDate.now()) + "' " +
            "AND user_id = " + user.getUserId() + " " +
            "AND is_checked_in = 1 " +
            "AND is_checked_out = 0";
        Connection con = ReservationSystem.getDatabaseConnection();
    
        try {
            ps = con.prepareStatement(sqlQuery);
            rs = ps.executeQuery();
            while(rs.next()){
                reservationList.add(
                    new Reservation(rs.getInt("reservation_id"), false));
                DatabaseUtil.processing();
            }
            result = new Reservation[reservationList.size()];
            reservationList.toArray(result);
        }
        catch (SQLException e){
            System.out.println("DatabaseUtil.getUserCheckOuts()");
            System.out.println(Thread.currentThread().getStackTrace()[2].getLineNumber());
            System.out.println(Thread.currentThread().getStackTrace()[2].getMethodName());
            System.out.println(e);
        }
        DatabaseUtil.ready();
        return result;
    };

    /**
     * Gets the current user's potential check-outs.
     * @return the current user's check-outs from today.
     */
    public static Reservation[] getTodayNoShows(){
        ArrayList<Reservation> reservationList = new ArrayList<Reservation>();
        Reservation[] result = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sqlQuery = "SELECT * " + 
            "FROM reservations " + 
            "WHERE start_date = '" + Date.valueOf(LocalDate.now()) + "' " +
            "AND is_checked_in = 0 ";
        Connection con = ReservationSystem.getDatabaseConnection();
    
        try {
            ps = con.prepareStatement(sqlQuery);
            rs = ps.executeQuery();
            while(rs.next()){
                reservationList.add(
                    new Reservation(rs.getInt("reservation_id"), false));
                DatabaseUtil.processing();
            }
            result = new Reservation[reservationList.size()];
            reservationList.toArray(result);
        }
        catch (SQLException e){
            System.out.println("DatabaseUtil.getTodayNoShows()");
            System.out.println(Thread.currentThread().getStackTrace()[2].getLineNumber());
            System.out.println(Thread.currentThread().getStackTrace()[2].getMethodName());
            System.out.println(e);
        }
        DatabaseUtil.ready();
        return result;
    };
    /**
     * Gets all active reservations in the database. This includes reservations
     * where the date is >= today, reservations not marked as cancelled, and
     * reservations not marked as checked out.
     * @param withUserReservations whether the fetch should populate the user's
     * reservations
     * @return all active reservations in the database
     */
    public static Reservation[] getActiveReservations(Boolean withUserReservations){
        ArrayList<Reservation> reservationList = new ArrayList<Reservation>();
        Reservation[] result = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sqlQuery = "SELECT * "
        + "FROM reservations "
        + "WHERE end_date >= '" + Date.valueOf(LocalDate.now())
        + "' AND is_cancelled = 0 "
        + "AND is_checked_out = 0";
        Connection con = ReservationSystem.getDatabaseConnection();
        try {
            ps = con.prepareStatement(sqlQuery);
            rs = ps.executeQuery();
            while(rs.next()){
                reservationList.add(
                    new Reservation(rs.getInt("reservation_id"), withUserReservations));
                DatabaseUtil.processing();
            }
            result = new Reservation[reservationList.size()];
            reservationList.toArray(result);
        }
        catch (SQLException e){
            System.out.println("DatabaseUtil.getActiveReservations()");
            System.out.println(Thread.currentThread().getStackTrace()[2].getLineNumber());
            System.out.println(Thread.currentThread().getStackTrace()[2].getMethodName());
            System.out.println(e);
        }
        DatabaseUtil.ready();
        return result;
    }

    /**
     * Gets all active reservations from the database
     * @return all active reservations in the databse
     */
    public static ResultSet getActiveReservationsRS(){
        PreparedStatement ps = null;
        ResultSet result = null;
        String sqlQuery = "SELECT * "
        + "FROM reservations "
        + "WHERE end_date >= '" + Date.valueOf(LocalDate.now())
        + "' AND is_cancelled = 0 "
        + "AND is_checked_out = 0";
        Connection con = ReservationSystem.getDatabaseConnection();
        try {
            ps = con.prepareStatement(sqlQuery);
            result = ps.executeQuery();
        }
        catch (SQLException e){
            System.out.println("DatabaseUtil.getActiveReservationsRS()");
            System.out.println(Thread.currentThread().getStackTrace()[2].getLineNumber());
            System.out.println(Thread.currentThread().getStackTrace()[2].getMethodName());
            System.out.println(e);
        }
        DatabaseUtil.ready();
        return result;
    }
    /**
     * Sets the Database status as READY
     */
    public static void ready(){
        ReservationSystem.setDatabaseStatus(DatabaseStatus.READY);
    }
    /**
     * Sets the Database status as PROCESSING
     */
    public static void processing(){
        ReservationSystem.setDatabaseStatus(DatabaseStatus.PROCESSING);
    }
    /**
     * Checks if a credit card number is in the databse
     * @param creditCardNumber credit card number to check
     * @return true if creditCardNumber already exists in the database, false if they
     * do not
     */
    public static User getCreditCardUser(String creditCardNumber){
        PreparedStatement ps = null;
        String sqlQuery = "SELECT user_id " +
            "FROM credit_cards " +
            "WHERE card_num = '" + creditCardNumber + "'";
        ResultSet rs = null;
        User result = null;
        Connection con = ReservationSystem.getDatabaseConnection();
    
        try {
            ps = con.prepareStatement(sqlQuery);
            rs = ps.executeQuery();
            if(rs.next()){
                result = new User(rs.getInt("user_id"), false);
            }
        }
        catch (SQLException e){
            System.out.println("DatabaseUtil.getCreditCardUser()");
            System.out.println(Thread.currentThread().getStackTrace()[2].getLineNumber());
            System.out.println(Thread.currentThread().getStackTrace()[2].getMethodName());
            System.out.println(e);
        }
        ready();
        return result;
    }
    /**
     * Deletes all credit cards in the database. DO NOT CALL.
     */
    public static void deleteAllCreditCards(){
        if (com.hotelco.developer.Settings.DEV_MODE == true){
            PreparedStatement ps = null;
            String sqlQuery = "DELETE FROM credit_cards";
            Connection con = ReservationSystem.getDatabaseConnection();
        
            try {
                ps = con.prepareStatement(sqlQuery);
                ps.execute();
            }
            catch (SQLException e){
                System.out.println("DatabaseUtil.deleteAllCreditCards()");
                System.out.println(Thread.currentThread().getStackTrace()[2].getLineNumber());
                System.out.println(Thread.currentThread().getStackTrace()[2].getMethodName());
                System.out.println(e);
            }
            ready();
        }
    }
}