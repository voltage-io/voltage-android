package io.voltage.app.requests;

import io.pivotal.arca.dispatcher.Query;
import io.voltage.app.application.VoltageApplication;
import io.voltage.app.application.VoltageContentProvider;

public class RegistrationQuery extends Query {

    private static final int ID = VoltageApplication.nextId();

    public RegistrationQuery() {
        super(VoltageContentProvider.Uris.REGISTRATIONS, ID);
    }
}
