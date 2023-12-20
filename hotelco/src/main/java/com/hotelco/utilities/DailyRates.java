package com.hotelco.utilities;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.hotelco.constants.RoomType;
import com.hotelco.entities.ReservationSystem;
/**
 * Gives the rate of a room from a database.
 * @author Daniel Schwartz
 */
public class DailyRates {
    /**
     * Fetches the room rate from the database for the supplied RoomType
     * @param roomType RoomType for which to check the rate
     * @return the room rate for the supplied RoomType
     */
    public static BigDecimal getRoomRate(RoomType roomType){
        BigDecimal result = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sqlQuery =
            "SELECT rate FROM rates " +
            "WHERE type = '" + roomType.toString().toLowerCase() + "'";
        Connection con = ReservationSystem.getDatabaseConnection();

        try {
            ps = con.prepareStatement(sqlQuery);
            rs = ps.executeQuery();
            if (rs.next()){
                result = rs.getBigDecimal("rate");
            }
        }
        catch (SQLException e) {
            System.out.println(e);
            System.out.println("DailyRates.getRoomRate()");
        }
        DatabaseUtil.ready();
        return result;
    }
}
