package io.voltage.app.models;

import io.pivotal.arca.provider.ColumnName;
import io.voltage.app.application.VoltageContentProvider.ThreadTable;

public class Thread {

    @ColumnName(ThreadTable.Columns.ID)
    private String mId;

    @ColumnName(ThreadTable.Columns.NAME)
    private String mName;

    @ColumnName(ThreadTable.Columns._STATE)
    private int mState;

    public Thread() {}

    public Thread(final GcmMessage gcmMessage) {
        mId = gcmMessage.getThreadId();
    }

    public String getId() {
        return mId;
    }

    public String getName() {
        return mName;
    }

    public int getState() {
        return mState;
    }
}
