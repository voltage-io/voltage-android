package io.voltage.app.requests;

import android.content.ContentValues;

import java.util.UUID;

import io.pivotal.arca.dispatcher.Insert;
import io.voltage.app.application.VoltageContentProvider;
import io.voltage.app.application.VoltageContentProvider.CrashTable;

public class CrashInsert extends Insert {

    public CrashInsert(final String stacktrace) {
        super(VoltageContentProvider.Uris.CRASHES, values(stacktrace));
    }

    private static ContentValues values(final String stacktrace) {
        final ContentValues values = new ContentValues();
        values.put(CrashTable.Columns.ID, UUID.randomUUID().toString());
        values.put(CrashTable.Columns.TRACE, stacktrace);
        return values;
    }
}
