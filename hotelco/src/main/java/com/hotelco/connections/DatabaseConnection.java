package com.hotelco.connections;

import java.sql.*;

/**
 * Creates and maintains a connection to a pre-defined database.
 *
 * @author Daniel Schwartz
 */
public class DatabaseConnection {

/**
 * Connects static member con to the database
 * @return Connection object, connected to database
 */
    public static Connection connectDB()
    {
        try
        {
            Class.forName("com.mysql.cj.jdbc.Driver");

            return DriverManager.getConnection(
                    "jdbc:mysql://sql3.freemysqlhosting.net:3306/sql3651321?autoreconnect=true", "sql3651321",
                    "fB8SKw8fPQ");
        }

        catch (SQLException | ClassNotFoundException e) {
            System.out.println(e);

            return null;
        }
    }
}