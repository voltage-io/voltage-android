package io.voltage.app.requests;

import io.pivotal.arca.dispatcher.Delete;
import io.voltage.app.application.VoltageContentProvider;
import io.voltage.app.application.VoltageContentProvider.MessageTable;

public class ThreadMessagesDelete extends Delete {

    public ThreadMessagesDelete(final String threadId) {
        super(VoltageContentProvider.Uris.MESSAGES);

        setWhere(MessageTable.Columns.THREAD_ID + "=?", threadId);
    }
}
