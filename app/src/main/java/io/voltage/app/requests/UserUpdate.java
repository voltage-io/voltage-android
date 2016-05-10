package io.voltage.app.requests;

import android.content.ContentValues;

import io.pivotal.arca.dispatcher.Update;
import io.voltage.app.application.VoltageContentProvider;
import io.voltage.app.application.VoltageContentProvider.UserTable;

public class UserUpdate extends Update {

    public UserUpdate(final String name, final String regId, final String prevId) {
        super(VoltageContentProvider.Uris.USERS, values(name, regId));

        setWhere(UserTable.Columns.REG_ID + "=?", prevId);
    }

    private static ContentValues values(final String name, final String regId) {
        final ContentValues values = new ContentValues();
        values.put(UserTable.Columns.NAME, name);
        values.put(UserTable.Columns.REG_ID, regId);
        return values;
    }
}
