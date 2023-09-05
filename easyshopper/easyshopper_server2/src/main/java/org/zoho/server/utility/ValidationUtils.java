package org.zoho.server.utility;

import java.util.regex.Pattern;

public class ValidationUtils {
    public static boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        return Pattern.matches(emailRegex, email);
    }

    public static boolean isValidMobileNumber(String mobileNumber) {
        String mobileRegex = "^[0-9]{10}$";
        return Pattern.matches(mobileRegex, mobileNumber);
    }

    public static boolean isValidPassword(String password) {
        String passwordRegex = "^(?=.*[0-9])(?=.*[a-zA-Z]).{8,}$";
        return Pattern.matches(passwordRegex, password);
    }
}
