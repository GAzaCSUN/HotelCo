package com.hotelco.developer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Month;
import com.hotelco.entities.ReservationSystem;
import com.hotelco.entities.User;
import com.hotelco.utilities.DatabaseUtil;
import com.hotelco.utilities.Reports;

/**
 * Contains the developer sandbox and methods usefull to developers
 */
public class Developer {
    /**
     * Developer sandbox to run snippets of code on their own without launching
     * main.
     */
    public static void runDevMode(){
        //run1();
        //run2();
        //run3();
        run4();
    }

    /**
     * developer sandbox 1
     */
    public static void run1(){
        setUserEmail("p@p.com", "daniel.schwartz.447@my.csun.edu");
    }

    /**
     * developer sandbox 2
     */
    public static void run2(){
        setUserEmail("daniel.schwartz.447@my.csun.edu", "p@p.com");
    }

    /**
     * developer sandbox 3
     */
    public static void run3(){
        DatabaseUtil.deleteAllCreditCards();
    }

    /**
     * developer sandbox 4
     */
    public static void run4(){
        Reports.getRevenueOfMonth(Month.of(12), 2023);
    }

    /**
     * Changes a user's email in the database
     * @param oldEmail the old email
     * @param newEmail the new email
     */
    public static void setUserEmail(String oldEmail, String newEmail){
        Integer userId = new User(oldEmail, false).getUserId();
        DatabaseUtil.ready();
        PreparedStatement ps = null;
        String sqlQuery = "UPDATE users " +
            "SET email = '" + newEmail + "' " +
            "WHERE user_id = " + userId;
        Connection con = ReservationSystem.getDatabaseConnection();
        System.out.println(sqlQuery);

        try {
            ps = con.prepareStatement(sqlQuery);
            ps.execute();
        }
        catch (SQLException e){
            System.out.println("DatabaseUtil.setUserEmail()");
            System.out.println(Thread.currentThread().getStackTrace()[2].getLineNumber());
            System.out.println(Thread.currentThread().getStackTrace()[2].getMethodName());
            System.out.println(e);
        }
        DatabaseUtil.ready();
    }
}
