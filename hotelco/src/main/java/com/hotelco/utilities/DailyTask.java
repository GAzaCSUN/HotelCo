package com.hotelco.utilities;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import com.hotelco.constants.Constants;
import com.hotelco.constants.MsTime;
import com.hotelco.constants.Tasks;
import com.hotelco.entities.Reservation;
import com.hotelco.entities.ReservationSystem;
/**
 * Creates a task that runs every day. 
 * @author Daniel Schwartz
 */
public class DailyTask {
    /**
     * Creates either a daily check in or check out task that runs at a fixed
     * time.
     * @param timeOfDay time of day to run this task
     * @param task Task type
     */
    public DailyTask(LocalTime timeOfDay, Tasks task){
        Timer timer = new Timer(true);
        LocalDateTime now = LocalDateTime.of(LocalDate.now(), timeOfDay);
        Date time = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
       switch (task){
            case DAILY_CHECK_OUT:
                timer.scheduleAtFixedRate(new CheckOutScheduledTask(), time,
                MsTime.DAY);
                break;
            case CANCEL_NO_SHOWS:
                timer.scheduleAtFixedRate(new DailyCancelScheduledTask(), time,
                MsTime.DAY);
                break;
        }
    }
    /**
     * A special class that extends Timertask and gives it a unique run() 
     * method.
     */
    class CheckOutScheduledTask extends TimerTask {
        /**
         * Runs the daily check out task
         */
        public void run() {
            //System.out.println("Running daily check outs");
            ReservationSystem.dailyCheckOut();
        }
    }
    /**
     * A special class that extends Timertask and gives it a unique run()
     * method.
     */
    class DailyCancelScheduledTask extends TimerTask {
        /**
         * Runs the daily check out task
         */
        public void run() {
            //System.out.println("Running daily check outs");
            Reservation reservations[] = DatabaseUtil.getTodayNoShows();
            Integer numReservations = reservations.length;

            for (Integer i = 0; i < numReservations; i++){
                reservations[i].setIsCancelled(true);
                reservations[i].setIsCheckedIn(false);
                reservations[i].setIsCheckedOut(false);
                reservations[i].push();
                System.out.println("Cancelled reservation "
                    + reservations[i].getReservationId());
            }
        }
    }
    /**
     * Schedules all the daily tasks
     */
    public static void scheduleDailyTasks(){        
        new DailyTask(Constants.CHECK_OUT_TIME, Tasks.DAILY_CHECK_OUT);
        new DailyTask(Constants.NO_SHOW_TIME, Tasks.CANCEL_NO_SHOWS);
    }
}
