package io.voltage.app.monitors;

import android.content.ContentResolver;
import android.content.Context;

import io.pivotal.arca.dispatcher.Delete;
import io.pivotal.arca.dispatcher.DeleteResult;
import io.pivotal.arca.monitor.RequestMonitor.AbstractRequestMonitor;
import io.voltage.app.application.VoltageContentProvider;

public class UserListMonitor extends AbstractRequestMonitor {

    @Override
    public int onPostExecute(final Context context, final Delete request, final DeleteResult result) {
        final ContentResolver resolver = context.getContentResolver();
        resolver.notifyChange(VoltageContentProvider.Uris.USERS, null);
        resolver.notifyChange(VoltageContentProvider.Uris.INBOX, null);
        return Flags.DATA_VALID;
    }
}
