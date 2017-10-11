package io.voltage.app.requests;

import io.pivotal.arca.dispatcher.Query;
import io.voltage.app.application.VoltageApplication;
import io.voltage.app.application.VoltageContentProvider;
import io.voltage.app.application.VoltageContentProvider.RecipientView;

public class RecipientsQuery extends Query {

    private static final int ID = VoltageApplication.nextId();

    public RecipientsQuery(final String msgUuid) {
        super(VoltageContentProvider.Uris.RECIPIENTS, ID);

        setWhere(RecipientView.Columns.MSG_UUID + "=?", msgUuid);
    }
}
