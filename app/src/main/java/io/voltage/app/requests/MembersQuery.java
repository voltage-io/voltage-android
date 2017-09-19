package io.voltage.app.requests;

import io.pivotal.arca.dispatcher.Query;
import io.voltage.app.application.VoltageApplication;
import io.voltage.app.application.VoltageContentProvider;
import io.voltage.app.application.VoltageContentProvider.MemberView;

public class MembersQuery extends Query {

    private static final int ID = VoltageApplication.nextId();

    public MembersQuery(final String threadId) {
        super(VoltageContentProvider.Uris.MEMBERS, ID);

        setWhere(MemberView.Columns.THREAD_ID + "=?", threadId);
    }
}
