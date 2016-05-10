package io.voltage.app.application;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;

public class VoltageGcmReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(final Context context, final Intent intent) {
        startWakefulService(context, getGcmServiceIntent(context, intent));
        startWakefulService(context, getGcmSyncingServiceIntent(context, intent));

        setResultCode(Activity.RESULT_OK);
    }

    private Intent getGcmServiceIntent(final Context context, final Intent intent) {
        final ComponentName component = getComponent(context, VoltageGcmService.class.getName());
        return new Intent(intent.setComponent(component));
    }

    private Intent getGcmSyncingServiceIntent(final Context context, final Intent intent) {
        final ComponentName component = getComponent(context, VoltageGcmSyncingService.class.getName());
        return new Intent(intent.setComponent(component));
    }

    private ComponentName getComponent(final Context context, final String name) {
        final String packageName = context.getPackageName();
        return new ComponentName(packageName, name);
    }
}
