package io.voltage.app.monitors;

import android.content.ContentResolver;
import android.content.Context;

import io.pivotal.arca.dispatcher.Delete;
import io.pivotal.arca.dispatcher.DeleteResult;
import io.pivotal.arca.dispatcher.Query;
import io.pivotal.arca.dispatcher.QueryResult;
import io.pivotal.arca.dispatcher.Update;
import io.pivotal.arca.dispatcher.UpdateResult;
import io.pivotal.arca.monitor.RequestMonitor.AbstractRequestMonitor;
import io.voltage.app.application.VoltageContentProvider;
import io.voltage.app.helpers.AccountHelper;
import io.voltage.app.helpers.NotificationHelper;
import io.voltage.app.requests.ConversationQuery;
import io.voltage.app.requests.MessageUpdate;

public class ConversationMonitor extends AbstractRequestMonitor {

    private final NotificationHelper mNotificationHelper = new NotificationHelper.Default();


    @Override
    public int onPostExecute(final Context context, final Query request, final QueryResult result) {

        if (request instanceof ConversationQuery) {
            final String threadId = request.getWhereArgs()[0];
            mNotificationHelper.cancelNotification(context, threadId);
        }

        return Flags.DATA_VALID;
    }

    @Override
    public int onPostExecute(final Context context, final Update request, final UpdateResult result) {

        final ContentResolver resolver = context.getContentResolver();
        resolver.notifyChange(VoltageContentProvider.Uris.CONVERSATION, null);
        resolver.notifyChange(VoltageContentProvider.Uris.INBOX, null);

        if (request instanceof MessageUpdate) {
            new AccountHelper.Default().requestSync(context);
        }

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
