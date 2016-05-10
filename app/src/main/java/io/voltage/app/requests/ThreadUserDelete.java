package io.voltage.app.requests;

import io.pivotal.arca.dispatcher.Delete;
import io.voltage.app.application.VoltageContentProvider;
import io.voltage.app.application.VoltageContentProvider.ThreadUserTable;

public class ThreadUserDelete extends Delete {

    public ThreadUserDelete(final String thread, final String regId) {
        super(VoltageContentProvider.Uris.THREAD_USERS);

        final String threadId = ThreadUserTable.Columns.THREAD_ID;
        final String userId = ThreadUserTable.Columns.USER_ID;

        setWhere(threadId + "=? AND " + userId + "=?", thread, regId);
    }
}
