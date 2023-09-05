package org.zoho.server.persist.validation;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static org.zoho.server.utility.ValidationUtils.*;

public class Validation {

    private static final Logger LOG = LogManager.getLogger(Validation.class);

    public static boolean validateUser(String username, String email, String mobileNo, String password) {
        if (validateUserName(username) && validateEmail(email) && validateMobileNo(mobileNo) && validatePassword(password)) {
            return true;
        }
        return false;
    }
    public static boolean validateUserName(String username) {
        if (username == null || username.isEmpty()) {
            LOG.info("USERNAME IS INVALID");
            return false;
        }
        return true;
    }

    public static boolean validateEmail(String email) {
        if (email == null || email.isEmpty() || !isValidEmail(email)) {
            LOG.info("EMAIL IS INVALID");
            return false;
        }
        return true;
    }

    public static boolean validateMobileNo(String mobileNo) {
        if (mobileNo == null || mobileNo.isEmpty() || !isValidMobileNumber(mobileNo)) {
            LOG.info("MOBILE NO IS INVALID");
            return false;
        }
        return true;
    }

    public static boolean validatePassword(String password) {
        if (password == null || password.isEmpty() || !isValidPassword(password)) {
            LOG.info("PASSWORD IS INVALID");
            return false;
        }
        return true;
    }

    public static boolean validateDescription(String description) {
        if (description == null || description.isEmpty() ) {
            LOG.info("DESCRIPTION IS INVALID");
            return false;
        }
        return true;
    }

    public static boolean validateProductName(String productName) {
        if (productName == null || productName.isEmpty() ) {
            LOG.info("PRODUCT NAME IS INVALID");
            return false;
        }
        return true;
    }

}
