package io.voltage.app.monitors;

import android.content.ContentResolver;
import android.content.Context;

import io.pivotal.arca.dispatcher.Insert;
import io.pivotal.arca.dispatcher.InsertResult;
import io.pivotal.arca.monitor.RequestMonitor.AbstractRequestMonitor;
import io.voltage.app.application.VoltageContentProvider;
import io.voltage.app.helpers.AccountHelper;
import io.voltage.app.requests.UserInsert;

public class UserAddMonitor extends AbstractRequestMonitor {

    @Override
    public int onPostExecute(final Context context, final Insert request, final InsertResult result) {
        final ContentResolver resolver = context.getContentResolver();
        resolver.notifyChange(VoltageContentProvider.Uris.USERS, null);
        resolver.notifyChange(VoltageContentProvider.Uris.INBOX, null);

        if (request instanceof UserInsert) {
            new AccountHelper.Default().requestSync(context);
        }

        return Flags.DATA_VALID;
    }
}