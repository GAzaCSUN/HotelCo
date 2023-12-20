package com.hotelco.controllers;

import com.hotelco.constants.RoomType;
import com.hotelco.utilities.Reports;

import javafx.fxml.FXML;

import javafx.scene.text.Text;

/**
 * The VacancyController class is the associated controller class of the 'Room
 * Vacancy' view. It handles connection between the GUI and internal data.
 * 
 * @author Bilin Pattasseril
 */

public class VacancyController extends BaseController {

    /**
     * Text that will display the amount of double room in use.
     */
    @FXML
    private Text doubleInUse;
    /**
     * Text that will display the amount of double room that are Vacant.
     */
    @FXML
    private Text doubleVacant;

    /**
     * Text that will display the amount of king room in use.
     */
    @FXML
    private Text kingInUse;
    /**
     * Text that will display the amount of a king room that are Vacant.
     */
    @FXML
    private Text kingVacant;

    /**
     * Text that will display the amount of queen room in use.
     */
    @FXML
    private Text queenInUse;
    /**
     * Text that will display the amount of a queen room that are Vacant.
     */
    @FXML
    private Text queenVacant;

    /**
     * Text that will display the amount of suite room in use.
     */
    @FXML
    private Text suiteInUse;
    /**
     * Text that will display the amount of a suite room that are Vacant.
     */
    @FXML
    private Text suiteVacant;

    /**
     * Text that will display the amount of check-in's for today
     */
    @FXML
    private Text totalCheckIn;

    /**
     * Text that will display the amount of check-out's for today
     */
    @FXML
    private Text totalCheckOut;

    /**
     * Text that will display the total room's in use
     */
    @FXML
    private Text totalInUse;
    /**
     * Text that will display the total room's vacant
     */
    @FXML
    private Text totalVacant;

    /**
     * This method is called immediately upon controller creation. It updates the
     * the text's 'doubleVacant', 'doubleInUse','queenVacant','queenInUse',
     * 'kingVacant','kingInUse','suiteVacant', 'suiteInUse','totalInUse',
     * 'totalVacant', 'totalCheckIn','totalCheckOut', to what is stored in the
     * database.
     */
    @FXML
    private void initialize() {

        doubleVacant.setText("Vacant: " + Reports.countAvailableRooms(RoomType.of("DOUBLE")));
        doubleInUse.setText("Occupied: " + Reports.countOccupiedRooms(RoomType.of("DOUBLE")));

        queenVacant.setText("Vacant: " + Reports.countAvailableRooms(RoomType.of("QUEEN")));
        queenInUse.setText("Occupied: " + Reports.countOccupiedRooms(RoomType.of("QUEEN")));

        kingVacant.setText("Vacant: " + Reports.countAvailableRooms(RoomType.of("KING")));
        kingInUse.setText("Occupied: " + Reports.countOccupiedRooms(RoomType.of("KING")));

        suiteVacant.setText("Vacant: " + Reports.countAvailableRooms(RoomType.of("SUITE")));
        suiteInUse.setText("Occupied: " + Reports.countOccupiedRooms(RoomType.of("SUITE")));

        totalInUse.setText("Occupied: " + Reports.getOccupancy());
        totalVacant.setText("Vacant: " + Reports.getVacancy());

        totalCheckIn.setText("Check In: " + Reports.countTodayCheckIns());
        totalCheckOut.setText("Check Out: " + Reports.countTodayCheckOuts());
    }

}
