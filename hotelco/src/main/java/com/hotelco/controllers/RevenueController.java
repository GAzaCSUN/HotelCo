package com.hotelco.controllers;

import com.hotelco.utilities.Reports;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.text.Text;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.LocalDate;

/**
 * The RevenueController class is the associated controller class of the
 * 'Revenue' view. It handles connection between the GUI and internal data for
 * Mangers.
 * 
 * @author Bilin Pattasseril
 */

public class RevenueController extends BaseController {

    /**
     * Text that contains the Daily Revenue made.
     */
    @FXML
    private Text dailyRevenue;

    /**
     * Text that contains the life time Revenue made.
     */
    @FXML
    private Text lifetimeRevenue;

    /**
     * Barchart that contains a chart for the revenue made each month for the last 6
     * months.
     */
    @FXML
    private BarChart<String, BigDecimal> revenueChart;

    /**
     * Text that contains the week Revenue made.
     */
    @FXML
    private Text weeklyRevenue;

    /**
     * Text that contains the year Revenue made.
     */
    @FXML
    private Text yearlyRevenue;

    /**
     * This method is called immediately upon controller creation. It updates the
     * the text 'yearlyRevenue','weeklyRevenue', 'lifetimeRevenue', 'dailyRevenue'
     * to what is stored in the database.It as well sets a barchart cart by
     * updateing the revenueChart and calling the database for that assoicae month
     * for the last 6 months.
     */
    @FXML
    private void initialize() {
        NumberFormat numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMinimumFractionDigits(2);
        numberFormat.setMaximumFractionDigits(2);
        dailyRevenue.setText("$ " + numberFormat.format(Reports.getDailyRevenue()));
        lifetimeRevenue.setText("$ " + numberFormat.format(Reports.getLifetimeRevenue()));
        weeklyRevenue.setText("$ " + numberFormat.format(Reports.getWeeklyRevenue()));
        yearlyRevenue.setText("$ " + numberFormat.format(Reports.getYearlyRevenue()));

        XYChart.Series<String, BigDecimal> plot = new XYChart.Series<>();
        String[] monthAbb = { "Jan", "Feb", "Mar", "Apr", "May", "June", "July", "Aug", "Sept", "Oct", "Nov", "Dec" };
        for (int m = 5; m >= 0; m--) {
            LocalDate monthDate = LocalDate.now().minusMonths(m);
            String monthString = monthAbb[monthDate.getMonthValue() - 1];
            BigDecimal revenueBDecimal = Reports.getRevenueOfMonth(monthDate.getMonth(), monthDate.getYear());
            plot.getData().add(new XYChart.Data<>(monthString, revenueBDecimal));
        }
        revenueChart.getData().add(plot);
    }

}
