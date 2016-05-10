package io.voltage.app.requests;

import java.util.Random;

import io.pivotal.arca.dispatcher.Query;
import io.voltage.app.application.VoltageContentProvider;
import io.voltage.app.application.VoltageContentProvider.UserTable;
import io.voltage.app.application.VoltageContentProvider.MemberView;

public class NonMembersQuery extends Query {

    private static final int ID = new Random().nextInt(1000) + 1000;

    public NonMembersQuery(final String threadId) {
        super(VoltageContentProvider.Uris.USERS, ID);

        final String select = "SELECT " + MemberView.Columns.USER_ID + " FROM MemberView where " + MemberView.Columns.THREAD_ID + " = ?";

        setWhere(UserTable.Columns.REG_ID + " NOT IN (" + select + ")", threadId);
    }
}
