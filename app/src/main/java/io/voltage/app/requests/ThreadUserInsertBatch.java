package io.voltage.app.requests;

import android.content.ContentProviderOperation;

import java.util.ArrayList;

import io.pivotal.arca.dispatcher.Batch;
import io.voltage.app.application.VoltageContentProvider;
import io.voltage.app.helpers.OperationHelper;
import io.voltage.app.models.GcmPayload;


public class ThreadUserInsertBatch extends Batch {

    public ThreadUserInsertBatch(final String threadId, final String senderId, final String regId) {
        super(VoltageContentProvider.BASE_URI, operations(threadId, senderId, regId));
    }

    private static ArrayList<ContentProviderOperation> operations(final String threadId, final String senderId, final String regId) {
        final OperationHelper helper = new OperationHelper();

        final ArrayList<ContentProviderOperation> list = new ArrayList<>();
        list.add(helper.insertThreadUserOperation(threadId, regId));
        list.add(helper.insertMessageOperation(senderId, threadId, GcmPayload.Type.USER_ADDED.name(), regId, GcmPayload.Type.USER_ADDED));

        return list;
    }
}
