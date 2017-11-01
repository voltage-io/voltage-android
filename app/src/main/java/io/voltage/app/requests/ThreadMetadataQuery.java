package io.voltage.app.requests;

import io.pivotal.arca.dispatcher.Query;
import io.voltage.app.application.VoltageApplication;
import io.voltage.app.application.VoltageContentProvider;
import io.voltage.app.application.VoltageContentProvider.MessageTable;

public class ThreadMetadataQuery extends Query {

    private static final int ID = VoltageApplication.nextId();

    public ThreadMetadataQuery(final String threadId) {
        super(VoltageContentProvider.Uris.MESSAGES, ID);

        setWhere(MessageTable.Columns.THREAD_ID + "=?" + " AND " + MessageTable.Columns.TYPE + "!='MESSAGE'", threadId);

        setSortOrder(MessageTable.Columns.TIMESTAMP);
    }
}
