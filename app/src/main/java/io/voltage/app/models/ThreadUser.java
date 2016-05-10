package io.voltage.app.models;

import android.content.ContentValues;

import io.pivotal.arca.provider.ColumnName;
import io.voltage.app.application.VoltageContentProvider.ThreadUserTable;

public class ThreadUser {

    @ColumnName(ThreadUserTable.Columns.THREAD_ID)
    private String mThreadId;

    @ColumnName(ThreadUserTable.Columns.USER_ID)
    private String mUserId;

    public ThreadUser() {}

    public ThreadUser(final GcmMessage gcmMessage) {
        mThreadId = gcmMessage.getThreadId();
        mUserId = gcmMessage.getSenderId();
    }

    public ThreadUser(final ContentValues values) {
        mThreadId = values.getAsString(ThreadUserTable.Columns.THREAD_ID);
        mUserId = values.getAsString(ThreadUserTable.Columns.USER_ID);
    }

    public String getThreadId() {
        return mThreadId;
    }

    public String getUserId() {
        return mUserId;
    }
}
