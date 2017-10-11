package io.voltage.app.requests;

import android.content.ContentProviderOperation;

import java.util.ArrayList;

import io.pivotal.arca.dispatcher.Batch;
import io.voltage.app.application.VoltageContentProvider;
import io.voltage.app.helpers.OperationHelper;


public class RegistrationInsertBatch extends Batch {

    public RegistrationInsertBatch(final String regId) {
        super(VoltageContentProvider.BASE_URI, operations(regId));
    }

    private static ArrayList<ContentProviderOperation> operations(final String regId) {
        final OperationHelper helper = new OperationHelper();
        final ArrayList<ContentProviderOperation> list = new ArrayList<>();
        list.add(helper.deleteRegistrationOperation());
        list.add(helper.insertRegistrationOperation(regId));
        return list;
    }
}
