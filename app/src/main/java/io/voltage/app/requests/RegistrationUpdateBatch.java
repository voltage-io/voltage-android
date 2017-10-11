package io.voltage.app.requests;

import android.content.ContentProviderOperation;

import java.util.ArrayList;

import io.pivotal.arca.dispatcher.Batch;
import io.voltage.app.application.VoltageContentProvider;
import io.voltage.app.helpers.OperationHelper;


public class RegistrationUpdateBatch extends Batch {

    public RegistrationUpdateBatch(final String newRegId, final String oldRegId) {
        super(VoltageContentProvider.BASE_URI, operations(newRegId, oldRegId));
    }

    private static ArrayList<ContentProviderOperation> operations(final String newRegId, final String oldRegId) {
        final OperationHelper helper = new OperationHelper();
        final ArrayList<ContentProviderOperation> list = new ArrayList<>();
        list.add(helper.updateUserOperation(newRegId, oldRegId));
        list.add(helper.updateThreadUsersOperation(newRegId, oldRegId));
        list.add(helper.updateMessagesOperation(newRegId, oldRegId));
        list.add(helper.updateMessagesMetadataOperation(newRegId, oldRegId));
        return list;
    }
}
