package com.hotelco.entities;

import java.math.BigDecimal;
/**
 * Contains an adjustment amount and associated comment.
 * @author Daniel Schwartz
 */
public class Adjustment {
    /**
     * A comment explaining the reason for this adjustment.
     */
    private String comment;
    /**
     * Amount of the adjustment. Can be positive or negative.
     */
    private BigDecimal amount;
    /**
     * Constructs an empty Adjustment
     */
    public Adjustment(){
        comment = "";
        amount = new BigDecimal(0);
    }
    /**
     * Constructs an adjustment with the supplied arguments
     * @param newComment String comment to be associated with the adjustment
     * @param newAmount BigDecimal amount to be associated with the adjustment
     */
    public Adjustment(String newComment, BigDecimal newAmount){
        comment = newComment;
        amount = newAmount;
    }
    /**
     * Gets the comment associated with this adjusment
     * @return the comment associated with this adjusment
     */
    public String getComment(){return comment;}
    /**
     * Gets the amount associated with this adjustment
     * @return the amount associated with this adjustment
     */
    public BigDecimal getAmount(){return amount;}
    /**
     * Sets the comment to be associated with this adjusmtent
     * @param newComment the comment to be associated with this adjusmtent
     */
    public void setComment(String newComment){comment = newComment;}
    /**
     * Sets the amount to be associated with this adjustment
     * @param newAmount the amount to be associated with this adjustment
     */
    public void setAmount(BigDecimal newAmount){amount = newAmount;}
}
