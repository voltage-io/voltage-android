package io.voltage.app.requests;

import android.content.ContentValues;

import io.pivotal.arca.dispatcher.Insert;
import io.voltage.app.application.VoltageContentProvider;
import io.voltage.app.application.VoltageContentProvider.ThreadTable;

public class ThreadInsert extends Insert {

    public ThreadInsert(final String threadId) {
        super(VoltageContentProvider.Uris.THREADS, values(threadId));
    }

    public ThreadInsert(final String threadId, final String name) {
        super(VoltageContentProvider.Uris.THREADS, values(threadId, name));
    }

    private static ContentValues values(final String threadId) {
        final ContentValues values = new ContentValues();
        values.put(ThreadTable.Columns.ID, threadId);
        return values;
    }

    private static ContentValues values(final String threadId, final String name) {
        final ContentValues values = values(threadId);
        values.put(ThreadTable.Columns.NAME, name);
        return values;
    }
}
