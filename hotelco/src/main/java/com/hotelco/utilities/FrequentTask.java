package com.hotelco.utilities;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.hotelco.administrator.Settings;
import com.hotelco.entities.ReservationSystem;

/**
 * Can create tasks that happen frequently and repeatedly.
 */
public class FrequentTask {
    /**
     * initiates a DatabaseReconnectTask that occurs every minutesFrequency
     * minutes 
     * @param minutesFrequency how often to schedule the task
     */
    public FrequentTask(Integer minutesFrequency){
        Timer timer = new Timer(true);
        timer.scheduleAtFixedRate(
            new DatabaseReconnectTask(),
            new Date(new Date().getTime() + minutesFrequency),
            minutesFrequency);
    }
/**
 * Reconnects the database frequently. Uses DatabaseStatus to determine if there
 * already a current transaction. If there is, the reconnect task is rescheduled
 * for RETRY_INTERVAL seconds later.
 */
    class DatabaseReconnectTask extends TimerTask {
        public void run() {
            //check if system is ready, i.e. there is no active processing
            if (ReservationSystem.isReady()){
                Connection con = ReservationSystem.getDatabaseConnection();
                try{
                    con.close();
                    con = ReservationSystem.getDatabaseConnection();
                    DatabaseUtil.ready();
                }
                catch (SQLException e){
                    System.out.println(e);
                }
                DatabaseUtil.ready();
            }
            else {
                System.out.println("Rescheduling reconnection attempt");
                Timer timer = new Timer(true);
                timer.schedule(
                    new DatabaseReconnectTask(), Settings.RETRY_INTERVAL);
            }
        }
    }
    /**
     * Schedules the frequent task(s).
     */
    public static void scheduleFrequentTasks(){
        new FrequentTask(Settings.RECONNECT_INTERVAL);
    }

}
