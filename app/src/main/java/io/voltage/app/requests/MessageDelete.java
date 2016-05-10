package io.voltage.app.requests;

import io.pivotal.arca.dispatcher.Delete;
import io.voltage.app.application.VoltageContentProvider;
import io.voltage.app.application.VoltageContentProvider.MessageTable;

public class MessageDelete extends Delete {

    public MessageDelete(final String uuid) {
        super(VoltageContentProvider.Uris.MESSAGES);

        setWhere(MessageTable.Columns.MSG_UUID + "=?", uuid);
    }
}
