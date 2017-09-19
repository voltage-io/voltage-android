package io.voltage.app.requests;

import io.pivotal.arca.dispatcher.Query;
import io.voltage.app.application.VoltageApplication;
import io.voltage.app.application.VoltageContentProvider;
import io.voltage.app.application.VoltageContentProvider.ConversationView;

public class ConversationQuery extends Query {

    private static final int ID = VoltageApplication.nextId();

    public ConversationQuery(final String threadId) {
        super(VoltageContentProvider.Uris.CONVERSATION, ID);

        setWhere(ConversationView.Columns.THREAD_ID + "=?", threadId);
    }
}
