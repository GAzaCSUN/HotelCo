package com.hotelco.utilities;

import java.util.ArrayList;

import com.hotelco.constants.RoomType;
/**
 * Converts a groupsize into an array of roomtypes that best fit that size
 * @author Daniel Schwartz
 */
public class GroupSize {
    
/**
 * Converts a groupsize into an array of roomtypes that best fit that size
 * @param groupSize integer number of people.
 * @return the largest RoomType(s) that a group of this size can occupy
 */
    public static RoomType[] toRoomTypes(Integer groupSize){
        ArrayList<RoomType> roomtypes = new ArrayList<RoomType>();
        switch(groupSize){
            case 1: 
            case 2:
                roomtypes.add(RoomType.DBL);
                break;
            case 3:
            case 4:
                roomtypes.add(RoomType.QUEEN);
                roomtypes.add(RoomType.KING);
                break;
            case 5:
            case 6:
                roomtypes.add(RoomType.SUITE);
                break;
        }
        RoomType[] result = new RoomType[roomtypes.size()];
        roomtypes.toArray(result);
        return result;
    }
}
