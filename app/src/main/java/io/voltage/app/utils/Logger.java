package io.voltage.app.utils;

import android.util.Log;

public class Logger {

    public static void v(final String message) {
        Log.v("voltage", message);
    }

    public static void ex(final Throwable throwable) {
        Log.e("voltage", throwable.getLocalizedMessage(), throwable);
    }
}
