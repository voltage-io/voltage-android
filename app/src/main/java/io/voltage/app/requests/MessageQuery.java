package io.voltage.app.requests;

import io.pivotal.arca.dispatcher.Query;
import io.voltage.app.application.VoltageApplication;
import io.voltage.app.application.VoltageContentProvider;
import io.voltage.app.application.VoltageContentProvider.MessageTable;

public class MessageQuery extends Query {

    private static final int ID1 = VoltageApplication.nextId();
    private static final int ID2 = VoltageApplication.nextId();

    public MessageQuery() {
        super(VoltageContentProvider.Uris.MESSAGES, ID1);

        final String stateSending = MessageTable.Columns._STATE + "=" + MessageTable.State.SENDING;
        final String stateError = MessageTable.Columns._STATE + "=" + MessageTable.State.ERROR;

        setWhere(stateSending + " OR " + stateError);
    }

    public MessageQuery(final String msgUuid) {
        super(VoltageContentProvider.Uris.MESSAGES, ID2);

        setWhere(MessageTable.Columns.MSG_UUID + "=?", msgUuid);
    }
}

