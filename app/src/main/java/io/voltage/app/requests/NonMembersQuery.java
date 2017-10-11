package io.voltage.app.requests;

import io.pivotal.arca.dispatcher.Query;
import io.voltage.app.application.VoltageApplication;
import io.voltage.app.application.VoltageContentProvider;
import io.voltage.app.application.VoltageContentProvider.UserTable;

public class NonMembersQuery extends Query {

    private static final int ID = VoltageApplication.nextId();

    public NonMembersQuery(final String threadId) {
        super(VoltageContentProvider.Uris.USERS, ID);

        final String select = "SELECT reg_id FROM RegistrationTable" +
                " UNION SELECT user_id FROM MemberView where thread_id = ?";

        setWhere(UserTable.Columns.REG_ID + " NOT IN (" + select + ")", threadId);
    }
}
