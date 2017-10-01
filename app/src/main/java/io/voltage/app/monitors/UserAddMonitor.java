package io.voltage.app.monitors;

import android.content.ContentResolver;
import android.content.Context;

import io.pivotal.arca.dispatcher.Insert;
import io.pivotal.arca.dispatcher.InsertResult;
import io.pivotal.arca.monitor.RequestMonitor.AbstractRequestMonitor;
import io.voltage.app.application.VoltageContentProvider;

public class UserAddMonitor extends AbstractRequestMonitor {

    @Override
    public int onPostExecute(final Context context, final Insert request, final InsertResult result) {
        final ContentResolver resolver = context.getContentResolver();
        resolver.notifyChange(VoltageContentProvider.Uris.USERS, null);
        resolver.notifyChange(VoltageContentProvider.Uris.INBOX, null);
        return Flags.DATA_VALID;
    }

}