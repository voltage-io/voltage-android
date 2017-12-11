package io.voltage.app.requests;

import android.content.ContentValues;

import io.pivotal.arca.dispatcher.Update;
import io.voltage.app.application.VoltageContentProvider;
import io.voltage.app.application.VoltageContentProvider.ThreadTable;
import io.voltage.app.application.VoltageContentProvider.ThreadTable.Columns;

public class ThreadColorUpdate extends Update {

    public ThreadColorUpdate(final String threadId, final String color) {
        super(VoltageContentProvider.Uris.THREADS, values(color));

        setWhere(ThreadTable.Columns.ID + "=?", threadId);
    }

    private static ContentValues values(final String color) {
        final ContentValues values = new ContentValues();
        values.put(ThreadTable.Columns.COLOR, color);
        return values;
    }
}
