package io.voltage.app.requests;

import android.content.ContentValues;

import io.pivotal.arca.dispatcher.Update;
import io.voltage.app.application.VoltageContentProvider;
import io.voltage.app.application.VoltageContentProvider.UserTable;

public class UserUpdate extends Update {

    public UserUpdate(final String name, final String regId) {
        super(VoltageContentProvider.Uris.USERS, values(name));

        setWhere(UserTable.Columns.REG_ID + "=?", regId);
    }

    public UserUpdate(final String name, final String regId, final String publicKey) {
        super(VoltageContentProvider.Uris.USERS, values(name, publicKey));

        setWhere(UserTable.Columns.REG_ID + "=?", regId);
    }

    private static ContentValues values(final String name) {
        final ContentValues values = new ContentValues();
        values.put(UserTable.Columns.NAME, name);
        return values;
    }

    private static ContentValues values(final String name, final String publicKey) {
        final ContentValues values = new ContentValues();
        if (name != null) values.put(UserTable.Columns.NAME, name);
        if (publicKey != null) values.put(UserTable.Columns.PUBLIC_KEY, publicKey);
        return values;
    }
}
