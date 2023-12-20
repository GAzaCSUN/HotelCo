package com.hotelco.utilities;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;

import com.hotelco.constants.RoomType;
import com.hotelco.entities.ReservationSystem;

/**
 * Makes database calls for general hotel information.
 */
public class Reports {

    /**
     * Gets the revenue based on the number of days in the past. Ex: For daily 
     * revenue report, numDays should be 1, 7 for a weekly report, etc.
     * @param numDays the number of days for which to generate this report
     * @return revenue during the supplied period
     */
    public static BigDecimal getRevenue(Integer numDays){   
        BigDecimal result = new BigDecimal(0);
        PreparedStatement ps = null;
        ResultSet rs = null;
        LocalDate startDate = LocalDate.now().minusDays(numDays);
        String sqlQuery = "SELECT SUM(amount) AS total FROM payments " +
            "WHERE DATE(time) > '" + Date.valueOf(startDate) + "'";
        Connection con = ReservationSystem.getDatabaseConnection();

        try {
            ps = con.prepareStatement(sqlQuery);
            rs = ps.executeQuery();
            if(rs.next())
            result = rs.getBigDecimal("total");
            result = result == null ? new BigDecimal("0") : result;
        }
        catch (SQLException e){
            System.out.println("DatabaseUtil.getRevenue()");
            System.out.println(Thread.currentThread().getStackTrace()[2].getLineNumber());
            System.out.println(Thread.currentThread().getStackTrace()[2].getMethodName());
            System.out.println(e);
        }
        DatabaseUtil.ready();
        return result;
    }

    /**
     * Gets the daily revenue of the hotel collected thus far
     * @return the daily revenue of the hotel collected thus far
     */
    public static BigDecimal getDailyRevenue(){
        return getRevenue(1);
    }

    /**
     * Gets the weekly revenue of the hotel collected thus far
     * @return the weekly revenue of the hotel collected thus far
     */
    public static BigDecimal getWeeklyRevenue(){
        return getRevenue(LocalDate.now().getDayOfWeek().getValue());
    }

    /**
     * Gets the monthly revenue of the hotel collected thus far
     * @return the monthly revenue of the hotel collected thus far
     */
    public static BigDecimal getMonthlyRevenue(){
        int day = LocalDate.now().getDayOfMonth();
        return getRevenue(day);
    }

    /**
     * Gets the annual revenue of the hotel collected thus far
     * @return the annual revenue of the hotel collected thus far
     */
    public static BigDecimal getYearlyRevenue(){
        return getRevenue(LocalDate.now().getDayOfYear());
    }

    /**
     * Gets the lifetime revenue of the hotel
     * @return the lifetime revenue of the hotel
     */
    public static BigDecimal getLifetimeRevenue(){   
        BigDecimal result = new BigDecimal(0);
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sqlQuery = "SELECT SUM(amount) AS total FROM payments";
        Connection con = ReservationSystem.getDatabaseConnection();
    
        try {
            ps = con.prepareStatement(sqlQuery);
            rs = ps.executeQuery();
            if(rs.next())
            result = rs.getBigDecimal("total");
        }
        catch (SQLException e){
            System.out.println("DatabaseUtil.getLifetimeRevenue()");
            System.out.println(Thread.currentThread().getStackTrace()[2].getLineNumber());
            System.out.println(Thread.currentThread().getStackTrace()[2].getMethodName());
            System.out.println(e);
        }
        DatabaseUtil.ready();
        return result;
    }

    /**
     * Gets the revenue of a given month year
     * @param month Month to check
     * @param year Year to check
     * @return the revenue for the supplied month/year
     */
    public static BigDecimal getRevenueOfMonth(Month month, Integer year) {   
        BigDecimal result = new BigDecimal(0);
        PreparedStatement ps = null;
        ResultSet rs = null;
        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = LocalDate.of(year, month, month.maxLength());
        String sqlQuery = "SELECT SUM(amount) AS total FROM payments " +
            "WHERE DATE(time) >= '" + Date.valueOf(startDate) + "' " +
            "AND DATE(time) <= '" + Date.valueOf(endDate) + "'";
        Connection con = ReservationSystem.getDatabaseConnection();

        try {
            ps = con.prepareStatement(sqlQuery);
            rs = ps.executeQuery();
            if(rs.next())
            result = rs.getBigDecimal("total");
            result = result == null ? new BigDecimal("0") : result;
        }
        catch (SQLException e){
            System.out.println("DatabaseUtil.getRevenue()");
            System.out.println(Thread.currentThread().getStackTrace()[2].getLineNumber());
            System.out.println(Thread.currentThread().getStackTrace()[2].getMethodName());
            System.out.println(e);
        }
        DatabaseUtil.ready();
        return result;
    }

    /**
     * Counts the number of occupied rooms in the database
     * @param roomType type of room to count
     * @return the number of occupied rooms
     */
    public static Integer countOccupiedRooms(RoomType roomType){
        Integer result = 0;
        LocalDate today = LocalDate.now();
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sqlQuery =
            "SELECT COUNT(room_num) as total " +
            "FROM (" +
                "SELECT room_num " +
                "FROM rooms " +
                "WHERE room_num " +
                "IN (" +
                    "SELECT room_num " + 
                    "FROM reservations " +
                    "WHERE start_date <= '" + Date.valueOf(today) + "' " +
                    "AND end_date > '" + Date.valueOf(today) + "') " +
                "AND room_type = '" + roomType.toString() + "') " +
                "AS T";
        Connection con = ReservationSystem.getDatabaseConnection();
    
        try {
            ps = con.prepareStatement(sqlQuery);
            rs = ps.executeQuery();
            if(rs.next()){
                result = rs.getInt("total");
            }
        }
        catch (SQLException e){
            System.out.println("DatabaseUtil.countAvailableRooms()");
            System.out.println(Thread.currentThread().getStackTrace()[2].getLineNumber());
            System.out.println(Thread.currentThread().getStackTrace()[2].getMethodName());
            System.out.println(e);
        }
        DatabaseUtil.ready();
        return result;
    }
    /**
     * Counts the amount of available rooms within the current day
     * @param roomType type of room to count
     * @return number of available rooms
     */
    public static Integer countAvailableRooms(RoomType roomType){
        Integer result = 0;
        LocalDate today = LocalDate.now();
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
                    "WHERE start_date <= '" + Date.valueOf(today) + "' " +
                    "AND end_date > '" + Date.valueOf(today) + "') " +
                "AND room_type = '" + roomType.toString() + "') " +
                "AS T";
        Connection con = ReservationSystem.getDatabaseConnection();
    
        try {
            ps = con.prepareStatement(sqlQuery);
            rs = ps.executeQuery();
            if(rs.next()){
                result = rs.getInt("total");
            }
        }
        catch (SQLException e){
            System.out.println("DatabaseUtil.countAvailableRooms()");
            System.out.println(Thread.currentThread().getStackTrace()[2].getLineNumber());
            System.out.println(Thread.currentThread().getStackTrace()[2].getMethodName());
            System.out.println(e);
        }
        DatabaseUtil.ready();
        return result;
    }

    /**
     * Gives an integer for the number of check-ins within the current day
     * @return number of check-ins
     */
    public static Integer countTodayCheckIns(){   
        Integer result = 0;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sqlQuery = "SELECT COUNT(*) AS total " + 
            "FROM reservations " + 
            "WHERE start_Date = '" + Date.valueOf(LocalDate.now()) + "' " +
            "AND is_checked_in = 1 ";
        Connection con = ReservationSystem.getDatabaseConnection();
    
        try {
            ps = con.prepareStatement(sqlQuery);
            rs = ps.executeQuery();
            if(rs.next()){
                result = rs.getInt("total");
            }
        }
        catch (SQLException e){
            System.out.println("DatabaseUtil.countTodayCheckIns()");
            System.out.println(Thread.currentThread().getStackTrace()[2].getLineNumber());
            System.out.println(Thread.currentThread().getStackTrace()[2].getMethodName());
            System.out.println(e);
        }
        DatabaseUtil.ready();
        return result;
    }
    
    /**
     * Gives an integer for the number of check-outs within the current day
     * @return number of check-outs
     */
    public static Integer countTodayCheckOuts(){   
        Integer result = 0;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sqlQuery = "SELECT COUNT(*) AS total " + 
            "FROM reservations " + 
            "WHERE end_Date = '" + Date.valueOf(LocalDate.now()) + "' " +
            "AND is_checked_out = 1";
        Connection con = ReservationSystem.getDatabaseConnection();
    
        try {
            ps = con.prepareStatement(sqlQuery);
            rs = ps.executeQuery();
            if(rs.next()){
                result = rs.getInt("total");
            }
        }
        catch (SQLException e){
            System.out.println("DatabaseUtil.countTodayCheckOuts()");
            System.out.println(Thread.currentThread().getStackTrace()[2].getLineNumber());
            System.out.println(Thread.currentThread().getStackTrace()[2].getMethodName());
            System.out.println(e);
        }
        DatabaseUtil.ready();
        return result;
    }

    /**
     * Gives the amount of revenue made within the current day
     * @return revenue
     */
    public static BigDecimal getTodayRevenue(){
        
        BigDecimal result = new BigDecimal(0);
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sqlQuery = "SELECT amount " + 
            "FROM payments " + 
            "WHERE time > '" + Date.valueOf(LocalDate.now()) + "'";
            System.out.println(sqlQuery);
        Connection con = ReservationSystem.getDatabaseConnection();
    
        try {
            ps = con.prepareStatement(sqlQuery);
            rs = ps.executeQuery();
            while(rs.next()){
                result = result.add(rs.getBigDecimal("amount"));
            }
        }
        catch (SQLException e){
            System.out.println("DatabaseUtil.getTodayRevenue()");
            System.out.println(Thread.currentThread().getStackTrace()[2].getLineNumber());
            System.out.println(Thread.currentThread().getStackTrace()[2].getMethodName());
            System.out.println(e);
        }
        DatabaseUtil.ready();
        return result;
    }

    /**
     * Fetches the amount of vacant rooms for tonight
     * @return the number of vacant rooms tonight
     */
    public static Integer getOccupancy(){
        
        Integer result = 0;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sqlQuery = "SELECT COUNT(room_num) AS total " + 
            "FROM rooms " + 
            "WHERE room_num IN (" + 
                "SELECT room_num " + 
                "FROM reservations " + 
                "WHERE start_date <= '" + Date.valueOf(LocalDate.now()) +
                "' AND end_date > '" + Date.valueOf(LocalDate.now()) + "') "; 
        Connection con = ReservationSystem.getDatabaseConnection();
    
        try {
            ps = con.prepareStatement(sqlQuery);
            rs = ps.executeQuery();
            if(rs.next()){
                result = rs.getInt("total");
            }
        }
        catch (SQLException e){
            System.out.println("Reports.numEmptyRooms");
            System.out.println(Thread.currentThread().getStackTrace()[2].getLineNumber());
            System.out.println(Thread.currentThread().getStackTrace()[2].getMethodName());
            System.out.println(e);
        }
        DatabaseUtil.ready();
        return result;
    
    }

    /**
     * Gets the total vacancy of the hotel from the database.
     * @return the total vacancy of the hotel
     */
    public static Integer getVacancy(){
        Integer result = 0;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sqlQuery = "SELECT COUNT(room_num) AS total " + 
            "FROM rooms " + 
            "WHERE room_num NOT IN (" + 
                "SELECT room_num " + 
                "FROM reservations " + 
                "WHERE start_date <= '" + Date.valueOf(LocalDate.now()) +
                "' AND end_date > '" + Date.valueOf(LocalDate.now()) + "') "; 
        Connection con = ReservationSystem.getDatabaseConnection();
    
        try {
            ps = con.prepareStatement(sqlQuery);
            rs = ps.executeQuery();
            if(rs.next()){
                result = rs.getInt("total");
            }
        }
        catch (SQLException e){
            System.out.println("Reports.numEmptyRooms");
            System.out.println(Thread.currentThread().getStackTrace()[2].getLineNumber());
            System.out.println(Thread.currentThread().getStackTrace()[2].getMethodName());
            System.out.println(e);
        }
        DatabaseUtil.ready();
        return result;
    
    }
    /**
     * Gets the total amount of rooms in the database by room type
     * @param roomType the room type to count
     * @return the total amount of rooms of the supplied room type
     */
    public static Integer countRooms(RoomType roomType){
        Integer result = 0;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sqlQuery = "SELECT COUNT(room_num) " +
            "AS total FROM rooms WHERE room_type = '" +
            roomType.toString() + "'";;
        Connection con = ReservationSystem.getDatabaseConnection();
    
        try {
            ps = con.prepareStatement(sqlQuery);
            rs = ps.executeQuery();
            if(rs.next()){
                result = rs.getInt("total");
            }
        }
        catch (SQLException e){
            System.out.println("DatabaseUtil.countRooms()");
            System.out.println(Thread.currentThread().getStackTrace()[2].getLineNumber());
            System.out.println(Thread.currentThread().getStackTrace()[2].getMethodName());
            System.out.println(e);
        }
        DatabaseUtil.ready();
        return result;
    }
    
    /**
     * Gets the number of rooms in the hotel
     * @return the number of rooms in the hotel
     */
    public static Integer getNumRooms(){
        Integer result = 0;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sqlQuery = "SELECT COUNT(room_num) " +
            "AS total FROM rooms";
        Connection con = ReservationSystem.getDatabaseConnection();
    
        try {
            ps = con.prepareStatement(sqlQuery);
            rs = ps.executeQuery();
            if(rs.next()){
                result = rs.getInt("total");
            }
        }
        catch (SQLException e){
            System.out.println("DatabaseUtil.countRooms()");
            System.out.println(Thread.currentThread().getStackTrace()[2].getLineNumber());
            System.out.println(Thread.currentThread().getStackTrace()[2].getMethodName());
            System.out.println(e);
        }
        DatabaseUtil.ready();
        return result;
    }

    
}
