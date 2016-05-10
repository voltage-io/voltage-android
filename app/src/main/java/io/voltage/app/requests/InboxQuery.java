package io.voltage.app.requests;

import java.util.Random;

import io.pivotal.arca.dispatcher.Query;
import io.voltage.app.application.VoltageContentProvider;

public class InboxQuery extends Query {

    private static final int ID = new Random().nextInt(1000) + 1000;

    public InboxQuery() {
        super(VoltageContentProvider.Uris.INBOX, ID);
    }
}
