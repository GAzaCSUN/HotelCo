package com.hotelco.developer;

/**
 * Tool to easily and repeatedly report execution time between lines of code.
 */
public class TimerTool {
    /**
     * The time of the previous print()
     */
    private Long prevTime;
    
    /**
     * The line of the previous print()
     */
    private Integer prevLine;

    /**
     * Constructs a TimerTool based on the current time and supplied line number.
     * @param line the line with which to start the timer
     */
    public TimerTool(Integer line){
        prevTime = System.currentTimeMillis();
        prevLine = line;
    }
    
    /**
     * Prints and updates the previous line/time members for future printing.
     * @param line current line of code
     */
    public void print(Integer line){
        Long currTime = System.currentTimeMillis();
        System.out.println((currTime - prevTime) + " ms from lines " + prevLine + " - " + line);
        prevTime = currTime;
        prevLine = line;
    }
}
