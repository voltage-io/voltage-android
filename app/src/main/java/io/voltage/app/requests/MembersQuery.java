package io.voltage.app.requests;

import java.util.Random;

import io.pivotal.arca.dispatcher.Query;
import io.voltage.app.application.VoltageContentProvider;
import io.voltage.app.application.VoltageContentProvider.MemberView;

public class MembersQuery extends Query {

    private static final int ID = new Random().nextInt(1000) + 1000;

    public MembersQuery(final String threadId) {
        super(VoltageContentProvider.Uris.MEMBERS, ID);

        setWhere(MemberView.Columns.THREAD_ID + "=?", threadId);
    }
}
