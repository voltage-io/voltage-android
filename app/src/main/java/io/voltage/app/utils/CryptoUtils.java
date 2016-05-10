package io.voltage.app.utils;

import android.util.Base64;

import java.security.MessageDigest;

import io.pivotal.arca.utils.StringUtils;
import io.voltage.app.models.Transactions;

public class CryptoUtils {

    public static String checksum(final Transactions transactions) {
        if (transactions != null) {
            final byte[] bytes = encode(transactions.getMsgUuids(), "SHA-256");
            return Base64.encodeToString(bytes, 0).trim();
        } else {
            return null;
        }
    }

    public static String checksum(final String message) {
        final byte[] bytes = encode(message, "MD5");
        final StringBuilder hexString = toHexadecimal(bytes);
        hexString.insert('-', 8).insert('-', 12);
        hexString.insert('-', 16).insert('-', 20);
        return StringUtils.left(hexString.toString(), 36);
    }

    private static byte[] encode(final String input, final String algorithm) {
        try {
            final MessageDigest digest = MessageDigest.getInstance(algorithm);
            digest.update(input.getBytes("UTF-8"));
            return digest.digest();
        } catch (final Exception e) {
            Logger.ex(e);
            return new byte[0];
        }
    }

    private static StringBuilder toHexadecimal(final byte[] bytes) {
        final StringBuilder hexString = new StringBuilder();
        for (final byte b : bytes) {
            hexString.append(Integer.toHexString(0xFF & b));
        }
        return hexString;
    }
}
