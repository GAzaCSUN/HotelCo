package com.hotelco.utilities;

import java.security.NoSuchAlgorithmException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hotelco.constants.CreditCardType;
import com.hotelco.entities.CreditCard;
import com.hotelco.entities.Password;
import com.hotelco.entities.User;
/**
 * Utility class for verifications
 * @author Bilin Pattasseril, Daniel Schwartz
 */

public class Verifier {
    /**
    * Utility to check and verify if a proper password is inputted.
    * @return a Boolean for a proper password.
    * @param email email address 
    * @param password Inputed password 
    */
    public static Boolean verifyPassword(String email, String password)
    {
            User temp = new User(email, false);
            Password pass = new Password(temp.getSalt());
            Boolean isVerified = false;
            try {
                isVerified = pass.verify(password, temp.getHashedPassword());
            }
            catch (NoSuchAlgorithmException e)
            {
                System.out.println(e);
            }
            return isVerified;
    }

    /**
     * Method checks if given email address matches a regular expression pattern
     * which is checking the format of the email.
     * @param email The email address to be validated.
     * @return a Boolean where true means the email is valid and false if not
     */
    public static Boolean isValidEmail(String email){
        if(email.length()>320){return false;}
        final String regex = "^[A-Za-z0-9]+([. -][A-Za-z0-9]+)*@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
    /**
     * Verifies a credit card could be real and is not expired
     * @param creditCard credit card to be verified
     * @return true when credit card can be valid, false when credit card
     * could not be valid
     */
    public static Boolean verifyCreditCard(CreditCard creditCard){
            return creditCard.verify();
    }

    /**
     * Returns the card type, which can be null. If the combination of the
     * first character and length implies it is not a possibly valid card (those
     * in CreditCardType.java), returns null.
     * @param creditCardNum credit card number for which the issuer is being
     * requested 
     * @return CreditCardType of this credit card number
     */
    public static CreditCardType getIssuer(String creditCardNum){
        CreditCardType result = null;

        Integer cardNumLen = creditCardNum.length();
        if (cardNumLen == 15 && creditCardNum.charAt(0) == '3'){
            result = CreditCardType.AMEX;
        }
        if(cardNumLen == 16){
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
 * @param creditCardNum the credit card number to verify
 * @return true if passes Luhn algorithm check, false if it fails
 */
    public static Boolean luhnCheck(String creditCardNum){
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
     * @param cvvNum CVV number to check
     * @param creditCardType credit card type against which to check the cvv
     * @return true if cvv card requirements are met, false otherwise
     */
    public static boolean cvvCheck(String cvvNum, CreditCardType creditCardType){
        Boolean result = false;
        Integer cvvNumLen = cvvNum.length();

        if (cvvNumLen == 4 && creditCardType == CreditCardType.AMEX ||
            cvvNumLen == 3 && creditCardType != CreditCardType.AMEX){
            result = true;
        }
        return result;
    }
}
