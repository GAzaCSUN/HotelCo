package com.hotelco.entities;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import com.hotelco.constants.CreditCardType;
import com.hotelco.utilities.DatabaseUtil;

/**
 * Maintains a credit card associated with a single user. Assumes that
 * every User has 1 credit card at most.
 * @author John Lee, Daniel Schwartz
 */
public class CreditCard{
    /**
     * Credit card number
     */
    private String creditCardNum;
    /**
     * CVV number
     */
    private String cvvNum;
    /**
     * Expiration date. LocalDate created with first day of expDate's year-month
     * 
     */
    private LocalDate expDate;
    /**
     * Type of credit card. See CreditCardType.java for types available.
     */
    private CreditCardType creditCardType;
    /**
     * User associated with this card.
     */
    private User user;
    /**
     * Gets the credit card number
     * @return credit card number
     */
    public String getCreditCardNum(){return creditCardNum;}
    /**
     * Gets the CVV number
     * @return the CVV number
     */
    public String getCvvNum(){return cvvNum;}
    /**
     * Gets the expiration date
     * @return the expiration date
     */
    public LocalDate getExpDate(){return expDate;}
    /**
     * Gets the credit card type
     * @return the credit card type
     */
    public CreditCardType getCreditCardType(){return creditCardType;}
    /**
     * Sets the credit card number
     * @param newCreditCardNum the credit card number to be associated with
     * this card
     */
    public void setCreditCardNum(String newCreditCardNum){
        creditCardNum = newCreditCardNum;
    }
    /** 
     * Sets the CVV number
     * @param newCvvNum the CVV number to be associated wiih this credit card
     */
    public void setCvvNum(String newCvvNum){cvvNum = newCvvNum;}
    /**
     * Sets the expiration date
     * @param newExpDate the expiration date to be associated with this credit card
     */
    public void setExpDate(LocalDate newExpDate){expDate = newExpDate;}
    /**
     * Sets the credit card type
     * @param newCreditCardType the credit card type to be associated
     * with this credit card
     */
    public void setCreditCardType(CreditCardType newCreditCardType){
        creditCardType = newCreditCardType;
    }
    /**
     * Creates CreditCard by fetching from database by userId.
     * @param owner user to be associated with this credit card
     */
    public CreditCard(User owner){
        user = owner;
        fetch();
    }
    /**
     * Creates a credit card by explicitly defining every member except
     * creditCardType. creditCardType is determined inside the constructor
     * algorithmically.
     * @param newCreditCardNum credit card number to associate with this credit card
     * @param newCVVNum cvv number to associate with this credit card
     * @param newExpDate expiration date to associate with this credit card
     * @param newUser user to associate with this credit card
     */
    public CreditCard(
        String newCreditCardNum, String newCVVNum,
        LocalDate newExpDate, User newUser){
        
        creditCardNum = newCreditCardNum;
        cvvNum = newCVVNum;
        expDate = newExpDate;
        user = newUser;
        creditCardType = getIssuer();
        }

        
    /**
     * Returns the card type, which can be null. If the combination of the
     * first character and length implies it is not a possibly valid card (those
     * in CreditCardType.java), returns null.
     * @return CreditCardType of this card
     */
    public CreditCardType getIssuer(){
        CreditCardType result = null;

        Integer cardNumLen = creditCardNum.length();
        if (cardNumLen == 15
            && creditCardNum.charAt(0) == '3'){
            result = CreditCardType.AMEX;
        }
        else if(cardNumLen == 16){
            switch(creditCardNum.charAt(0)){
                case '4':
                result = CreditCardType.VISA;
                break;
                case '5':
                result = CreditCardType.MASTERCARD;
                break;
                case '6':
                result = CreditCardType.DISCOVER;
                break;
            }
        }
        return result;
    }
/**
 * Uses the Luhn algorithm to check if a card is invalid.
 * @return true if passes Luhn algorithm check, false if it fails
 */
    public Boolean luhnCheck(){
        Integer cardNumLen = creditCardNum.length();
        Boolean isSecond = false;
        Integer totalSum = 0;
        for (int i = cardNumLen - 1; i >= 0; --i){ 

                int currDigit = creditCardNum.charAt(i) - '0';

                if (isSecond == true){
                    currDigit = currDigit * 2;
                }
                totalSum += currDigit / 10;
                totalSum += currDigit % 10;

                isSecond = !isSecond;
            }
        return (totalSum % 10 == 0);
    }
    /**
     * Checks if the CVV number matches the card type's requirements, only use
     * after credit type is set
     * @return true if cvv card requirements are met
     */
    public boolean cvvCheck(){
        Boolean result = false;
        Integer cvvNumLen = cvvNum.length();

        if (cvvNumLen == 4 && getCreditCardType() == CreditCardType.AMEX ||
            cvvNumLen == 3 && getCreditCardType() != CreditCardType.AMEX){
            result = true;
        }
        return result;
    }
    /**
     * Verifies the credit card is a valid type of card, passes Luhn algorithm check,
     * and is not expired
     * @return true if credit card is possibly valid, false if it cannot be valid
     */
    public Boolean verify(){
        Boolean result = false;

        result = creditCardType != null
            && luhnCheck()
            && expDate.isAfter(LocalDate.now())
            && cvvCheck();
        
        return result;
    }
    /**
     * Database push function that assigns a credit card to CreditCard's user.
     * Adds a new entry if the user has no credit card, replaces the entry if
     * the user already has a credit card. It is recommended to use verify()
     * before assign().
     */
    private void assignToUser(){
        PreparedStatement ps = null;
        String sqlQuery = null;
        Connection con = null;
        
        if(user != null){
            if (userHasOneCard()){
                DatabaseUtil.processing();
                sqlQuery = "UPDATE credit_cards " +
                "SET card_num = '" + creditCardNum +
                "', cvv = '" + cvvNum +
                "', exp_date = '" + Date.valueOf(expDate) +
                "' WHERE user_id = " + user.getUserId();
            }
            else {
                DatabaseUtil.processing();
                sqlQuery = "INSERT INTO credit_cards " +
                "SET card_num = '" + creditCardNum +
                "', cvv = '" + cvvNum +
                "', exp_date = '" + Date.valueOf(expDate) +
                "', user_id = " + user.getUserId();
            }
            con = ReservationSystem.getDatabaseConnection();
            try {
                ps = con.prepareStatement(sqlQuery);
                ps.execute();
                }
            catch (SQLException e){
                System.out.println("CreditCard.assign()");
                System.out.println(Thread.currentThread().getStackTrace()[2].getLineNumber());
                System.out.println(Thread.currentThread().getStackTrace()[2].getMethodName());
                System.out.println(e);
            }
            DatabaseUtil.ready();
        }
    }
    /**
     * Conditionally assigns a card to a User upon verification. See
     * assignToUser() for it's implementation details.
     * @return the success of verification AND assignment.
     */
    public Boolean assign(){
        Boolean result = false;

        if (verify()){
            assignToUser();
            result = true;
        }

        return result;
    }
    /**
     * Checks if the CreditCard already exists in the database and returns true
     * if that card's userId matches this one's.
     * @return true when there is a user conflict, false if not
     */
    public Boolean checkUserConflict(){
        Boolean result = true;
        User newUser = DatabaseUtil.getCreditCardUser(creditCardNum);
        
        result = newUser != null && user.getUserId() != newUser.getUserId();
        
        return result;
    }
    /**
     * Fetches a credit card from the database by user.
     */
    public void fetch(){
        PreparedStatement ps = null;
        ResultSet rs = null;
        String sqlQuery = "SELECT * FROM credit_cards " +
            "WHERE user_id = " + user.getUserId();;
        Connection con = ReservationSystem.getDatabaseConnection();
        
        try {
            ps = con.prepareStatement(sqlQuery);
            rs = ps.executeQuery();
            if(rs.next()){
                creditCardNum = rs.getString("card_num");
                cvvNum = rs.getString("cvv");
                expDate = rs.getDate("exp_date").toLocalDate();
            }
       }
        catch (SQLException e){
            System.out.println("CreditCard.fetch()");
            System.out.println(Thread.currentThread().getStackTrace()[2].getLineNumber());
            System.out.println(Thread.currentThread().getStackTrace()[2].getMethodName());
            System.out.println(e);
        }
        DatabaseUtil.ready();
    }
    /**
     * Checks if the CreditCard's user has a card associated with them in the
     * database.
     * @return true if the user has a card in the database already, false if they
     * do not
     */
    public Boolean userHasOneCard(){
        PreparedStatement ps = null;
        String sqlQuery = "SELECT COUNT(*) AS total " +
            "FROM credit_cards " +
            "WHERE user_id = " + user.getUserId();
        ResultSet rs = null;
        Boolean result = false;
        Connection con = ReservationSystem.getDatabaseConnection();

        try {
            ps = con.prepareStatement(sqlQuery);
            rs = ps.executeQuery();
            if(rs.next()){
                result = rs.getInt("total") == 1;
            }
        }
        catch (SQLException e){
            System.out.println("CreditCard.userHasOneCard()");
            System.out.println(Thread.currentThread().getStackTrace()[2].getLineNumber());
            System.out.println(Thread.currentThread().getStackTrace()[2].getMethodName());
            System.out.println(e);
        }
        DatabaseUtil.ready();
        return result;
    }
    /**
     * Dummy function to check if a credit card can accept a charge
     * @return true if authentication succeeds, false if it fails
     */
    public Boolean authenticate(){
        return true;
    }
}


