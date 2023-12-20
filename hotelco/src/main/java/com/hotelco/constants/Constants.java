package com.hotelco.constants;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
/**
 * Contains all constants.
 * @author Daniel Schwartz, John Lee
 */
public final class Constants {
    /**
     * Standard check in time
     */
    public static final LocalTime CHECK_IN_TIME = LocalTime.of(19, 0, 0);
    /**
     * Standard check out time
     */
    public static final LocalTime CHECK_OUT_TIME = LocalTime.of(14, 34, 0);
    /**
     * The time at which a reservation booked for today, but isn't checked in,
     * gets cancelled.
     */
    public static final LocalTime NO_SHOW_TIME = LocalTime.of(23, 50, 0);
    /**
     * Maximum capacity of a double room. 
     */
    public static final Map<RoomType,Integer> Capacities =
        new HashMap<RoomType,Integer>();
    /**
     * Room capacities for each RoomType
     */
    public static final Map<RoomType, Integer> CAPACITIES = Stream.of(
  new AbstractMap.SimpleImmutableEntry<>(RoomType.DBL, 2),    
  new AbstractMap.SimpleImmutableEntry<>(RoomType.QUEEN, 4),
  new AbstractMap.SimpleImmutableEntry<>(RoomType.KING, 4),
  new AbstractMap.SimpleImmutableEntry<>(RoomType.SUITE, 6))
  .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

    /**
     * Maximum capacity of any room in the hotel
     */ 
    public static final Integer MAX_CAP = 6;
    /**
     * Tax rate multiplier on the weekends
     */
    public static final BigDecimal WEEKEND_MULTIPLIER = new BigDecimal("1.2");
    /**
     * Flavor text for the double room
     */
    public static final String DblRoomFlavor = "\t- Free WiFi\n\n" +
                                                "\t- Free Self Parking\n\n" +
                                                "\t- Shower\n\n" +
                                                "\t- 2 Double Beds\n\n" +
                                                "\t- 500 sqft";
    /**
     * Flavor text for the queen room
     */
    public static final String QueenRoomFlavor = "\t- Free WiFi\n\n" +
                                                "\t- Free Self Parking\n\n" +
                                                "\t- Shower/Tub Combination\n\n" +
                                                "\t- 2 Queen Beds\n\n" +
                                                "\t- 700 sqft";
    /**
     * Flavor text for the king room
     */
    public static final String KingRoomFlavor = "\t- Free WiFi\n\n" +
                                                "\t- Free Self Parking\n\n" +
                                                "\t- Shower/Tub Combination\n\n" +
                                                "\t- Foldable Couch\n\n" +
                                                "\t- 1 King Bed\n\n" +
                                                "\t- 950 sqft";
    /**
     * Flavor text for the suite room
     */
    public static final String SuiteRoomFlavor = "\t- Free WiFi\n\n" +
                                                "\t- Free Self Parking\n\n" +
                                                "\t- Shower/Tub Combination\n\n" +
                                                "\t- Panoramic View\n\n" +
                                                "\t- 1 King Beds\n\n" +
                                                "\t- 1300 sqft";
}
