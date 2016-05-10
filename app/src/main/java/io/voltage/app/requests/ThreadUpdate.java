package io.voltage.app.requests;

import android.content.ContentValues;

import io.pivotal.arca.dispatcher.Update;
import io.voltage.app.application.VoltageContentProvider;
import io.voltage.app.application.VoltageContentProvider.ThreadTable;

public class ThreadUpdate extends Update {

    public ThreadUpdate(final String name, final String threadId) {
        super(VoltageContentProvider.Uris.THREADS, values(name));

        setWhere(ThreadTable.Columns.ID + "=?", threadId);
    }

    public ThreadUpdate(final String threadId, final int state) {
        super(VoltageContentProvider.Uris.THREADS, values(state));

        setWhere(ThreadTable.Columns.ID + "=?", threadId);
    }

    private static ContentValues values(final String name) {
        final ContentValues values = new ContentValues();
        values.put(ThreadTable.Columns.NAME, name);
        return values;
    }

    private static ContentValues values(final int state) {
        final ContentValues values = new ContentValues();
        values.put(ThreadTable.Columns._STATE, state);
        return values;
    }
}
