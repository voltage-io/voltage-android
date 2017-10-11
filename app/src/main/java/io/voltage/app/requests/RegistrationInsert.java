package io.voltage.app.requests;

import android.content.ContentValues;

import io.pivotal.arca.dispatcher.Insert;
import io.voltage.app.application.VoltageContentProvider;
import io.voltage.app.application.VoltageContentProvider.RegistrationTable;

public class RegistrationInsert extends Insert {

    public RegistrationInsert(final String regId) {
        super(VoltageContentProvider.Uris.REGISTRATIONS, values(regId));
    }

    private static ContentValues values(final String regId) {
        final ContentValues values = new ContentValues();
        values.put(RegistrationTable.Columns.REG_ID, regId);
        return values;
    }
}
