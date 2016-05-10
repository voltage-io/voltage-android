package io.voltage.app.monitors;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;

import io.pivotal.arca.dispatcher.Delete;
import io.pivotal.arca.dispatcher.DeleteResult;
import io.pivotal.arca.dispatcher.Update;
import io.pivotal.arca.dispatcher.UpdateResult;
import io.pivotal.arca.monitor.RequestMonitor.AbstractRequestMonitor;
import io.pivotal.arca.service.Operation;
import io.pivotal.arca.service.OperationService;
import io.voltage.app.application.VoltageContentProvider;
import io.voltage.app.application.VoltageContentProvider.MessageTable;
import io.voltage.app.operations.MessageOperation;

public class ConversationMonitor extends AbstractRequestMonitor {

    @Override
    public int onPostExecute(final Context context, final Update request, final UpdateResult result) {
        final ContentValues values = request.getContentValues();
        final int state = values.getAsInteger(MessageTable.Columns._STATE);

        if (state == MessageTable.State.SENDING) {
            final String msgUuid = values.getAsString(MessageTable.Columns.MSG_UUID);
            final Operation operation = new MessageOperation(msgUuid);
            OperationService.start(context, operation);
        }

        final ContentResolver resolver = context.getContentResolver();
        resolver.notifyChange(VoltageContentProvider.Uris.CONVERSATION, null);
        resolver.notifyChange(VoltageContentProvider.Uris.INBOX, null);
        return Flags.DATA_VALID;
    }

    @Override
    public int onPostExecute(final Context context, final Delete request, final DeleteResult result) {
        final ContentResolver resolver = context.getContentResolver();
        resolver.notifyChange(VoltageContentProvider.Uris.CONVERSATION, null);
        resolver.notifyChange(VoltageContentProvider.Uris.INBOX, null);
        return Flags.DATA_VALID;
    }
}
