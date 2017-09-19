package io.voltage.app.requests;

import io.pivotal.arca.dispatcher.Query;
import io.voltage.app.application.VoltageApplication;
import io.voltage.app.application.VoltageContentProvider;

public class UserSearchQuery extends Query {

    private static final int ID = VoltageApplication.nextId();

    public UserSearchQuery(final String lookup) {
        super(VoltageContentProvider.Uris.USER_SEARCH, ID);

        setWhere("search=?", lookup);
    }
}
