package com.hotelco.utilities;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.hotelco.entities.ReservationSystem;
/**
 * Uses the tax rate from the database to generate relevant Tax BigDecimals.
 * @author Daniel Schwartz
 */
public class TaxRate {
    /**
     * Gets the tax rate from the database
     * @return the tax rate
     */
    public static BigDecimal getTaxRate(){
        BigDecimal result = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sqlQuery =
            "SELECT rate FROM rates " +
            "WHERE type = 'tax'";
        Connection con = ReservationSystem.getDatabaseConnection();

        try {
            ps = con.prepareStatement(sqlQuery);
            rs = ps.executeQuery();
            if (rs.next()){
                result = rs.getBigDecimal("rate");
            }
            else {
                result = null;
                System.out.println("Tax rate not fetched");
            }
        }
        catch (SQLException e) {
            System.out.println(e);
            System.out.println("TaxRate.getTaxRate()");
        }
        DatabaseUtil.ready();
        return result;
    }
    /**
     * Calculates the tax multiplier from the database. Multiplier is a
     * BigDecimal equal to 1 + TaxRate.
     * @return the tax multiplier
     */
     public static BigDecimal getTaxMultiplier(){
        BigDecimal result = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sqlQuery =
            "SELECT rate FROM rates " +
            "WHERE type = 'tax'";
        Connection con = ReservationSystem.getDatabaseConnection();

        try {
            ps = con.prepareStatement(sqlQuery);
            rs = ps.executeQuery();
            if (rs.next()){
                result = rs.getBigDecimal("rate");
            }
            else {
                result = null;
                System.out.println("Tax rate not fetched");
            }
            
        }
        catch (SQLException e) {
            System.out.println(e);
            System.out.println(Thread.currentThread().getStackTrace()[2].getLineNumber());
            System.out.println("TaxRate.getTaxMultiplier()");
        }
        DatabaseUtil.ready();
        
        if (result != null){
            result = result.divide(new BigDecimal("100"));
            result = result.add(new BigDecimal("1"));
        }

        return result;
    }
}
