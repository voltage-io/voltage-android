package io.voltage.app.requests;

import io.pivotal.arca.dispatcher.Delete;
import io.voltage.app.application.VoltageContentProvider;
import io.voltage.app.application.VoltageContentProvider.UserTable;

public class UserDelete extends Delete {

    public UserDelete(final String regId) {
        super(VoltageContentProvider.Uris.USERS);

        setWhere(UserTable.Columns.REG_ID + "=?", regId);
    }
}
