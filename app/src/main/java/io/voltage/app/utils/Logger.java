package io.voltage.app.utils;

import android.util.Log;

public class Logger {

    private static int sLevel = Level.DEBUG;

    public interface Level {
        int NONE = 0, ERROR = 1, WARNING = 2, VERBOSE = 3, INFO = 4, DEBUG = 5;
    }

    public static void level(final int level) {
        sLevel = level;
    }

    public static void d(final String message) {
        if (sLevel >= Level.DEBUG) {
            Log.d("voltage", message);
        }
    }

    public static void i(final String message) {
        if (sLevel >= Level.INFO) {
            Log.d("voltage", message);
        }
    }

    public static void v(final String message) {
        if (sLevel >= Level.VERBOSE) {
            Log.v("voltage", message);
        }
    }

    public static void w(final String message) {
        if (sLevel >= Level.WARNING) {
            Log.w("voltage", message);
        }
    }

    public static void ex(final Throwable throwable) {
        if (sLevel >= Level.ERROR) {
            Log.e("voltage", throwable.getLocalizedMessage(), throwable);
        }
    }
}
