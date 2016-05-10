package io.voltage.app.utils;

public class NumberUtils {

    public static long parseLong(final String state, final long defaultValue) {
        try {
            return Long.parseLong(state);
        } catch (final NumberFormatException e) {
            return defaultValue;
        }
    }

    public static int parseInt(final String state, final int defaultValue) {
        try {
            return Integer.parseInt(state);
        } catch (final NumberFormatException e) {
            return defaultValue;
        }
    }
}
