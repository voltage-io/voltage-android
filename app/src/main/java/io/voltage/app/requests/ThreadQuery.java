package io.voltage.app.requests;

import java.util.Random;

import io.pivotal.arca.dispatcher.Query;
import io.voltage.app.application.VoltageContentProvider;
import io.voltage.app.application.VoltageContentProvider.ThreadTable;

public class ThreadQuery extends Query {

    private static final int ID = new Random().nextInt(1000) + 1000;

    public ThreadQuery(final String threadId) {
        super(VoltageContentProvider.Uris.THREADS, ID);

        setWhere(ThreadTable.Columns.ID + "=?", threadId);
    }
}
