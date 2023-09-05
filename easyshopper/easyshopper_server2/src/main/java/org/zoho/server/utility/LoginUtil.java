package org.zoho.server.utility;

public class LoginUtil {
    public static String encryption(String password) {
        StringBuilder encryptPassword = new StringBuilder();
        for (int c : password.toCharArray()) {
            if (c == 122 || c == 90) {
                encryptPassword.append((char) (c - 25));
            } else if (c == 57) {
                encryptPassword.append((char) (c - 9));
            } else {
                encryptPassword.append((char) (c + 1));
            }
        }
        return encryptPassword.toString();
    }
}
