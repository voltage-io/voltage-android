package io.voltage.app.monitors;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;

import io.pivotal.arca.dispatcher.Insert;
import io.pivotal.arca.dispatcher.InsertResult;
import io.pivotal.arca.monitor.RequestMonitor.AbstractRequestMonitor;
import io.pivotal.arca.service.Operation;
import io.pivotal.arca.service.OperationService;
import io.voltage.app.application.VoltageContentProvider;
import io.voltage.app.application.VoltageContentProvider.MessageTable;
import io.voltage.app.operations.MessageOperation;
import io.voltage.app.requests.MessageInsert;

public class MessageSendMonitor extends AbstractRequestMonitor {

    @Override
    public int onPostExecute(final Context context, final Insert request, final InsertResult result) {

        if (request instanceof MessageInsert) {
            final ContentValues values = request.getContentValues()[0];
            final String msgUuid = values.getAsString(MessageTable.Columns.MSG_UUID);
            final Operation operation = new MessageOperation(msgUuid);
            OperationService.start(context, operation);
        }

        final ContentResolver resolver = context.getContentResolver();
        resolver.notifyChange(VoltageContentProvider.Uris.CONVERSATION, null);
        resolver.notifyChange(VoltageContentProvider.Uris.INBOX, null);
        return Flags.DATA_VALID;
    }

}
