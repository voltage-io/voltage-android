package io.voltage.app.requests;

import io.pivotal.arca.dispatcher.Query;
import io.voltage.app.application.VoltageApplication;
import io.voltage.app.application.VoltageContentProvider;

public class InboxQuery extends Query {

    private static final int ID = VoltageApplication.nextId();

    public InboxQuery() {
        super(VoltageContentProvider.Uris.INBOX, ID);
    }
}
