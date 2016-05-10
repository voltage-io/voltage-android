package io.voltage.app.requests;

import java.util.Random;

import io.pivotal.arca.dispatcher.Query;
import io.voltage.app.application.VoltageContentProvider;
import io.voltage.app.application.VoltageContentProvider.UserTable;

public class UserQuery extends Query {

    private static final int ID1 = new Random().nextInt(1000) + 1000;
    private static final int ID2 = new Random().nextInt(1000) + 1000;
    private static final int ID3 = new Random().nextInt(1000) + 1000;

    public UserQuery() {
        super(VoltageContentProvider.Uris.USERS, ID1);

        setSortOrder(UserTable.Columns.NAME);
    }

    public UserQuery(final String text, final String sortOrder) {
        super(VoltageContentProvider.Uris.USERS, ID2);

        setWhere(UserTable.Columns.NAME + " LIKE ?", "%" + text + "%");
        setSortOrder(sortOrder);
    }

    public UserQuery(final String regId) {
        super(VoltageContentProvider.Uris.USERS, ID3);

        setWhere(UserTable.Columns.REG_ID + "=?", regId);
    }
}
