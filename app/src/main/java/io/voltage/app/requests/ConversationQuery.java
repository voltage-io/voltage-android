package io.voltage.app.requests;

import java.util.Random;

import io.pivotal.arca.dispatcher.Query;
import io.voltage.app.application.VoltageContentProvider;
import io.voltage.app.application.VoltageContentProvider.ConversationView;

public class ConversationQuery extends Query {

    private static final int ID = new Random().nextInt(1000) + 1000;

    public ConversationQuery(final String threadId) {
        super(VoltageContentProvider.Uris.CONVERSATION, ID);

        setWhere(ConversationView.Columns.THREAD_ID + "=?", threadId);
    }
}
