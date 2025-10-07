package com.baizeli.eternisstarrysky.Bypass;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashUtils {
    // 本来用于加密的
    private static final String BYPASS_COMMAND_HASH = "8c7dd922ad47494fc02c388e12c00eac278fb030d83d04c55a4a12a2f4a5fca7";

    private static final String[] DECOY_HASHES = {
            "a1b2c3d4e5f6789012345678901234567890abcdef1234567890abcdef123456",
            "fedcba0987654321fedcba0987654321fedcba0987654321fedcba0987654321",
            "1234567890abcdef1234567890abcdef1234567890abcdef1234567890abcdef"
    };

    public static String calculateSHA256(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 算法不可用", e);
        }
    }

    public static boolean isBypassCommand(String message) {
        if (message == null || message.length() < 3) {
            return false;
        }

        String potentialCommand = extractPotentialCommand(message);
        if (potentialCommand == null) {
            return false;
        }

        String messageHash = calculateSHA256(potentialCommand);
        return BYPASS_COMMAND_HASH.equals(messageHash);
    }

    private static String extractPotentialCommand(String message) {
        String[] possiblePrefixes = {"\\", "/", ".", "!"};

        for (String prefix : possiblePrefixes) {
            if (message.startsWith(prefix)) {
                int spaceIndex = message.indexOf(' ');
                if (spaceIndex == -1) {
                    return message;
                } else {
                    return message.substring(0, spaceIndex);
                }
            }
        }

        return null;
    }
}