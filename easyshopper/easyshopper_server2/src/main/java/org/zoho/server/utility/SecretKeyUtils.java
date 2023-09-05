package org.zoho.server.utility;

import java.util.Random;

public class SecretKeyUtils {
    public static String generateSecretKey() {
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        int length = 20;
        Random random = new Random();
        StringBuilder sessionId = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(characters.length());
            sessionId.append(characters.charAt(randomIndex));
        }
        return sessionId.toString();
    }
}
