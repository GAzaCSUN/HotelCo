package com.hotelco.utilities;

import javafx.scene.control.TextFormatter;

/**
 * The TextFormatters class contains the relevant TextField logic for
 * CreateAccountController.
 * 
 * @author Grigor Azakian
 */
public class TextFormatters {
    /**
     * The TextFormatters that makes sure phone number is 9 digits and a number.
     */
    public final TextFormatter<String> PHONE_NUMBER = new TextFormatter<>(changed -> {
        if (changed.getControlNewText().length() > 10) {
            return null;
        }
        if (changed.getControlNewText().matches("\\d*")) {
            return changed;
        } else {
            return null;
        }
    });
    /**
     * The TextFormatters that makes sure first name only contains letters.
     */
    public final TextFormatter<String> FIRST_NAME = new TextFormatter<>(changed -> {
        if (changed.getControlNewText().isEmpty()) {
            return changed;
        } else if (Character.isLetter(changed.getControlNewText().charAt(changed.getControlNewText().length() - 1))) {
            return changed;
        } else {
            return null;
        }
    });
    /**
     * The TextFormatters that makes sure last name only contains letters.
     */
    public final TextFormatter<String> LAST_NAME = new TextFormatter<>(changed -> {
        if (changed.getControlNewText().isEmpty()) {
            return changed;
        } else if (Character.isLetter(changed.getControlNewText().charAt(changed.getControlNewText().length() - 1))) {
            return changed;
        } else {
            return null;
        }
    });
    /**
     * The TextFormatters that makes sure credit card number length is 16 and all of
     * it are numbers.
     */
    public final TextFormatter<String> CREDIT_CARD = new TextFormatter<>(changed -> {
        if (changed.getControlNewText().length() > 16) {
            return null;
        }
        if (changed.getControlNewText().matches("\\d*")) {
            return changed;
        } else {
            return null;
        }
    });
    /**
     * The TextFormatters that makes sure that expiration month is 2 characters and
     * that it is a numbers.
     */
    public final TextFormatter<String> EXP_DATE_MONTH = new TextFormatter<>(changed -> {
        if (changed.getControlNewText().length() > 2) {
            return null;
        }
        if (changed.getControlNewText().matches("\\d*")) {
            return changed;
        } else {
            return null;
        }
    });
    /**
     * The TextFormatters that makes sure that expiration year is 2 characters and
     * that it is a numbers.
     */
    public final TextFormatter<String> EXP_DATE_YEAR = new TextFormatter<>(changed -> {
        if (changed.getControlNewText().length() > 2) {
            return null;
        }
        if (changed.getControlNewText().matches("\\d*")) {
            return changed;
        } else {
            return null;
        }
    });
    /**
     * The TextFormatters that makes sure that CVC is 3 characters and that it is a
     * numbers.
     */
    public final TextFormatter<String> CVC = new TextFormatter<>(changed -> {
        if (changed.getControlNewText().length() > 4) {
            return null;
        }
        if (changed.getControlNewText().matches("\\d*")) {
            return changed;
        } else {
            return null;
        }
    });
}
