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
import io.voltage.app.application.VoltageContentProvider.UserTable;
import io.voltage.app.operations.FriendOperation;
import io.voltage.app.requests.UserInsert;

public class UserAddMonitor extends AbstractRequestMonitor {

    @Override
    public int onPostExecute(final Context context, final Insert request, final InsertResult result) {

        if (request instanceof UserInsert) {
            final ContentValues values = request.getContentValues()[0];
            final String regId = values.getAsString(UserTable.Columns.REG_ID);
            final Operation operation = new FriendOperation(regId);
            OperationService.start(context, operation);
        }

        final ContentResolver resolver = context.getContentResolver();
        resolver.notifyChange(VoltageContentProvider.Uris.USERS, null);
        resolver.notifyChange(VoltageContentProvider.Uris.INBOX, null);
        return Flags.DATA_VALID;
    }

}