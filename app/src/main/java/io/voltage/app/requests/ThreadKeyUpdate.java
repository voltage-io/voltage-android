package io.voltage.app.requests;

import android.content.ContentValues;

import io.pivotal.arca.dispatcher.Update;
import io.voltage.app.application.VoltageContentProvider;
import io.voltage.app.application.VoltageContentProvider.ThreadTable;

public class ThreadKeyUpdate extends Update {

    public ThreadKeyUpdate(final String threadId, final String key) {
        super(VoltageContentProvider.Uris.THREADS, values(key));

        setWhere(ThreadTable.Columns.ID + "=?", threadId);
    }

    private static ContentValues values(final String key) {
        final ContentValues values = new ContentValues();
        values.put(ThreadTable.Columns.KEY, key);
        return values;
    }
}
