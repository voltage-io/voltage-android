package io.voltage.app.requests;

import io.pivotal.arca.dispatcher.Query;
import io.voltage.app.application.VoltageApplication;
import io.voltage.app.application.VoltageContentProvider;
import io.voltage.app.application.VoltageContentProvider.ThreadTable;

public class ThreadQuery extends Query {

    private static final int ID = VoltageApplication.nextId();

    public ThreadQuery(final String threadId) {
        super(VoltageContentProvider.Uris.THREADS, ID);

        setWhere(ThreadTable.Columns.ID + "=?", threadId);
    }
}
