package io.voltage.app.requests;

import io.pivotal.arca.dispatcher.Delete;
import io.voltage.app.application.VoltageContentProvider;
import io.voltage.app.application.VoltageContentProvider.CrashTable;

public class CrashDelete extends Delete {

    public CrashDelete(final String id) {
        super(VoltageContentProvider.Uris.CRASHES);

        setWhere(CrashTable.Columns.ID + "=?", id);
    }
}
