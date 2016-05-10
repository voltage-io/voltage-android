package io.voltage.app.models;

import android.content.ContentValues;

import io.pivotal.arca.provider.ColumnName;
import io.voltage.app.application.VoltageContentProvider.UserTable;

public class User {

    @ColumnName(UserTable.Columns.NAME)
    private String mName;

    @ColumnName(UserTable.Columns.REG_ID)
    private String mRegId;

    public User() {}

    public User(final GcmFriend gcmFriend) {
        mName = gcmFriend.getName();
        mRegId = gcmFriend.getRegId();
    }

    public User(final ContentValues values) {
        mName = values.getAsString(UserTable.Columns.NAME);
        mRegId = values.getAsString(UserTable.Columns.REG_ID);
    }

    public String getName() {
        return mName;
    }

    public String getRegId() {
        return mRegId;
    }
}
