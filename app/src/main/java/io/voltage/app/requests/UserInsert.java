package io.voltage.app.requests;

import android.content.ContentValues;

import io.pivotal.arca.dispatcher.Insert;
import io.voltage.app.application.VoltageContentProvider;
import io.voltage.app.application.VoltageContentProvider.UserTable;

public class UserInsert extends Insert {

    public UserInsert(final String name, final String regId, final String publicKey) {
        super(VoltageContentProvider.Uris.USERS, values(name, regId, publicKey));
    }

    private static ContentValues values(final String name, final String regId, final String publicKey) {
        final ContentValues values = new ContentValues();
        values.put(UserTable.Columns.NAME, name);
        values.put(UserTable.Columns.REG_ID, regId);
        values.put(UserTable.Columns.PUBLIC_KEY, regId);
        return values;
    }
}
