package io.voltage.app.monitors;

import android.content.ContentResolver;
import android.content.Context;

import io.pivotal.arca.dispatcher.Batch;
import io.pivotal.arca.dispatcher.BatchResult;
import io.pivotal.arca.monitor.RequestMonitor.AbstractRequestMonitor;
import io.voltage.app.application.VoltageContentProvider;
import io.voltage.app.helpers.AccountHelper;
import io.voltage.app.requests.ThreadUserInsertBatch;

public class MembersAddMonitor extends AbstractRequestMonitor {

    @Override
    public int onPostExecute(final Context context, final Batch request, final BatchResult result) {

        final ContentResolver resolver = context.getContentResolver();
        resolver.notifyChange(VoltageContentProvider.Uris.USERS, null);
        resolver.notifyChange(VoltageContentProvider.Uris.INBOX, null);
        resolver.notifyChange(VoltageContentProvider.Uris.PARTICIPANTS, null);

        if (request instanceof ThreadUserInsertBatch) {
            new AccountHelper.Default().requestSync(context);
        }

        return Flags.DATA_VALID;
    }
}