package io.voltage.app.monitors;

import android.content.ContentResolver;
import android.content.Context;

import io.pivotal.arca.dispatcher.Update;
import io.pivotal.arca.dispatcher.UpdateResult;
import io.pivotal.arca.monitor.RequestMonitor.AbstractRequestMonitor;
import io.voltage.app.application.VoltageContentProvider;

public class MarkAsReadMonitor extends AbstractRequestMonitor {

    @Override
    public int onPostExecute(final Context context, final Update request, final UpdateResult result) {

//        if (VoltagePreferences.shouldSendReadReceipt(context)) {
//            final String msgUuid = request.getWhereArgs()[0];
//            final Operation operation = new MessageStateOperation(msgUuid, 0);
//            OperationService.start(context, operation);
//        }

        final ContentResolver resolver = context.getContentResolver();
        resolver.notifyChange(VoltageContentProvider.Uris.CONVERSATION, null);
        resolver.notifyChange(VoltageContentProvider.Uris.INBOX, null);
        return Flags.DATA_VALID;
    }
}
