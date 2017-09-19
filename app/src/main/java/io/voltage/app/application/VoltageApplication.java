package io.voltage.app.application;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

public class VoltageApplication extends Application {

    private static int sID = 1000;

    public static int nextId() {
        return sID++;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Fresco.initialize(this);
    }
}
