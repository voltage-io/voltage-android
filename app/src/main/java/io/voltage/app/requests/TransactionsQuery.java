package io.voltage.app.requests;

import io.pivotal.arca.dispatcher.Query;
import io.voltage.app.application.VoltageApplication;
import io.voltage.app.application.VoltageContentProvider;
import io.voltage.app.application.VoltageContentProvider.TransactionView;

public class TransactionsQuery extends Query {

    private static final int ID = VoltageApplication.nextId();

    public TransactionsQuery(final String threadId) {
        super(VoltageContentProvider.Uris.TRANSACTIONS, ID);

        setWhere(TransactionView.Columns.THREAD_ID + "=?", threadId);
    }
}
