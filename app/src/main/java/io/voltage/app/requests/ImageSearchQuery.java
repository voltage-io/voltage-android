package io.voltage.app.requests;

import io.pivotal.arca.dispatcher.Query;
import io.voltage.app.application.VoltageApplication;
import io.voltage.app.application.VoltageContentProvider;

public class ImageSearchQuery extends Query {

    private static final int ID = VoltageApplication.nextId();

    public ImageSearchQuery(final String query) {
        super(VoltageContentProvider.Uris.IMAGE_SEARCH, ID);

        setWhere("query=?", query);
    }
}
