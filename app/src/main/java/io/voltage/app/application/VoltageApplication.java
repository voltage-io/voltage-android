package io.voltage.app.application;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.facebook.drawee.backends.pipeline.Fresco;

public class VoltageApplication extends Application {

    public static String getVersion(final Context context) {
        try {
            final String packageName = context.getPackageName();
            final PackageManager manager = context.getPackageManager();
            final PackageInfo packageInfo = manager.getPackageInfo(packageName, 0);
            return packageInfo.versionName;
        } catch (final PackageManager.NameNotFoundException e) {
            throw new RuntimeException("Could not get package name: " + e);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Fresco.initialize(this);

        // Logger.setup(true, "voltage");
    }
}
