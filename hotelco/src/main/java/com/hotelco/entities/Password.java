package com.hotelco.entities;

import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.security.NoSuchAlgorithmException;
import java.math.BigInteger;

/**
 * The password class represents a password with a salt for security. It provides methods to encrypt and verify.  
 * @author Bilin Pattasseril
 */
public class Password{
    /**
     * Represents the salt for an unhashed password.
     */
    private String salt;

    /**
     * Constructs A password with a given salt.
     * @param saltInput the salt associate with password
     */

    public Password(String saltInput){
        salt=saltInput;
    }

     /**
     * Construct a password with a given salt 
     */

    public Password(){
        SecureRandom rand=new SecureRandom();
        byte[]saltByte= new byte[16];
        rand.nextBytes(saltByte);
        salt = Base64.getEncoder().encodeToString(saltByte);
    }


    /**
     * Encrypt a given password with the salt.
     * 
     * @param pass the password to be encrypted
     * @return the hashed password, which is in hexadecimal
     * @throws NoSuchAlgorithmException 
     */
    
    public String encrypt(String pass)throws NoSuchAlgorithmException{
    
        byte[]saltPass= genSaltWithPass(pass.getBytes());
      
        MessageDigest md= MessageDigest.getInstance("SHA-256"); 
        byte[] messageDigest= md.digest(saltPass);
        
        BigInteger bigInt= new BigInteger(1, messageDigest);

        return bigInt.toString(16);
    }
    /**
     * Verify that the given input password is the same as the hashed password, which is done by concatenating the salt and input password.
     * 
     * @param inputPassword password that is input to be verified
     * @param hashedPassword hashed password to compare against
     * @return a Boolean where true means the passwords are the same and false if not
     * @throws NoSuchAlgorithmException
     */    
    public Boolean verify(
        String inputPassword,String hashedPassword)throws NoSuchAlgorithmException{
        BigInteger bigInt= new BigInteger(hashedPassword,16);
        byte[] hashedPasswordBytes = bigInt.toByteArray();
        
        if (hashedPasswordBytes.length > 16 && hashedPasswordBytes[0] == 0) {
           byte[] temp = new byte[hashedPasswordBytes.length - 1];
           System.arraycopy(hashedPasswordBytes, 1, temp,
           0, temp.length);
        hashedPasswordBytes = temp;
        }
        

        byte[] saltByte =Base64.getDecoder().decode(salt);
        
        byte[] nonHashedInputPassword =
            new byte[inputPassword.getBytes().length+16];
        System.arraycopy(
            saltByte, 0, nonHashedInputPassword, 0, 16);
        System.arraycopy(
            inputPassword.getBytes(), 0, nonHashedInputPassword,
            16, inputPassword.getBytes().length);
       
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hashedPasswordToVerify = md.digest(nonHashedInputPassword);
       
        for (Integer i = 16; i < hashedPasswordToVerify.length; i++) {
            if (hashedPasswordBytes[i] != hashedPasswordToVerify[i]) {
                return false; 
            }
        }
        return true;
    }
    /**
     * Generates a byte array with both the Salt and Password together.
     *
     * @param pass a byte array of the password
     * @return a byte array of the Salt and Password together
     */

    private byte[] genSaltWithPass(byte[] pass){
        byte[] saltByte = Base64.getDecoder().decode(salt);
        byte[]result = new byte[pass.length+16];
        System.arraycopy(saltByte, 0, result, 0, 16);
        System.arraycopy(pass, 0, result, 16, pass.length);
        return result;
    }

    /**
     * Gets the salt
     * @return the salt
     */

    public String getSalt(){
        return this.salt;
    }    
}
