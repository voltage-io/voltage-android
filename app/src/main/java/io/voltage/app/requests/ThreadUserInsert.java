package io.voltage.app.requests;

import android.content.ContentValues;

import io.pivotal.arca.dispatcher.Insert;
import io.voltage.app.application.VoltageContentProvider;
import io.voltage.app.application.VoltageContentProvider.ThreadUserTable;

public class ThreadUserInsert extends Insert {

    public ThreadUserInsert(final String threadId, final String userId) {
        super(VoltageContentProvider.Uris.THREAD_USERS, values(threadId, userId));
    }

    private static ContentValues values(final String threadId, final String userId) {
        final ContentValues values = new ContentValues();
        values.put(ThreadUserTable.Columns.THREAD_ID, threadId);
        values.put(ThreadUserTable.Columns.USER_ID, userId);
        return values;
    }
}
