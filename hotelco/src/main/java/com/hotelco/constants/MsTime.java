package com.hotelco.constants;
/**
 * Converts common small units of time to milliseconds.
 * @author Daniel Schwartz
 */
public class MsTime{
    /**
    * A millisecond, in milliseconds
    */
    public static final Integer MILLISECOND = 1;
    /**
     * A second, in milliseconds
     */
    public static final Integer SECOND = 1000 * MILLISECOND;
    /**
     * A minute, in milliseconds
     */
    public static final Integer MINUTE = 60 * SECOND;
    /**
     * An hour, in milliseconds
     */
    public static final Integer HOUR = 60 * MINUTE;
    /**
     * A day, in milliseconds
     */
    public static final Integer DAY = 24 * HOUR;
}