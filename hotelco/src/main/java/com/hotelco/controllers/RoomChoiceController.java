package com.hotelco.controllers;

import com.hotelco.constants.Constants;
import com.hotelco.constants.RoomType;
import com.hotelco.utilities.DatabaseUtil;
import com.hotelco.utilities.FXMLPaths;
import com.hotelco.utilities.Instances;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

/**
 * The RoomChoiceController class is the associated controller class of the
 * 'RoomChoiceGUI' view. It handles connection between the GUI and internal
 * data.
 * 
 * @author Grigor Azakian
 */
public class RoomChoiceController extends BaseController {

    /**
     * Text that contains the benefits of booking a suite.
     */
    @FXML
    private Text suiteFlavor;

    /**
     * Button that allows the user to book a double.
     */
    @FXML
    private Button dbl;

    /**
     * Text that contains the cost of booking a double.
     */
    @FXML
    private Text dblRate;

    /**
     * Text that contains the benefits of booking a double.
     */
    @FXML
    private Text dblFlavor;

    /**
     * Button that allows the user to book a king.
     */
    @FXML
    private Button king;

    /**
     * Text that contains the benefits of booking a king.
     */
    @FXML
    private Text kingFlavor;

    /**
     * Text that contains the cost of booking a king.
     */
    @FXML
    private Text kingRate;

    /**
     * Button that allows the user to book a queen.
     */
    @FXML
    private Button queen;

    /**
     * Text that contains the benefits of booking a queen.
     */
    @FXML
    private Text queenFlavor;

    /**
     * Text that contains the cost of booking a double.
     */
    @FXML
    private Text queenRate;

    /**
     * Button that allows the user to book a suite.
     */
    @FXML
    private Button suite;

    /**
     * Text that contains the cost of booking a double.
     */
    @FXML
    private Text suiteRate;

    /**
     * Called upon controller creation. Sets up various text fields.
     */
    @FXML
    private void initialize() {
        kingRate.setText("$" + DatabaseUtil.getRate(RoomType.KING).toString());
        queenRate.setText("$" + DatabaseUtil.getRate(RoomType.QUEEN).toString());
        dblRate.setText("$" + DatabaseUtil.getRate(RoomType.DBL).toString());
        suiteRate.setText("$" + DatabaseUtil.getRate(RoomType.SUITE).toString());
        kingFlavor.setText(Constants.KingRoomFlavor);
        queenFlavor.setText(Constants.QueenRoomFlavor);
        dblFlavor.setText(Constants.DblRoomFlavor);
        suiteFlavor.setText(Constants.SuiteRoomFlavor);
    }

    /**
     * This method is called by pressing any of the 'BOOK' buttons. It exits
     * 'RoomChoiceGUI' and enters 'RoomSearchGUI'.
     * 
     * @param event The 'mouse released' event caused by pressing any of the 'BOOK'
     *              buttons.
     */
    @FXML
    void switchToBooking(MouseEvent event) {
        Button clickedButton = (Button) event.getSource();
        RoomType roomType = RoomType.of(clickedButton.getId().toUpperCase());
        ReservationController rc = (ReservationController) Instances.getDashboardController()
                .switchAnchor(FXMLPaths.RESERVATION);
        rc.setRoomType(roomType);
    }

}