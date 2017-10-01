package io.voltage.app.requests;

import android.content.ContentProviderOperation;

import java.util.ArrayList;
import java.util.Set;

import io.pivotal.arca.dispatcher.Batch;
import io.voltage.app.application.VoltageContentProvider;
import io.voltage.app.helpers.OperationHelper;
import io.voltage.app.models.GcmPayload;


public class ThreadInsertBatch extends Batch {

    public ThreadInsertBatch(final String threadId, final String senderId, final String name, final Set<String> regIds) {
        super(VoltageContentProvider.BASE_URI, operations(threadId, senderId, name, regIds));
    }

    private static ArrayList<ContentProviderOperation> operations(final String threadId, final String senderId, final String name, final Set<String> regIds) {
        final OperationHelper helper = new OperationHelper();

        final ArrayList<ContentProviderOperation> list = new ArrayList<>();
        list.add(helper.insertThreadOperation(threadId, name));
        list.add(helper.insertMessageOperation(senderId, threadId, GcmPayload.Type.THREAD_CREATED.name(), name, GcmPayload.Type.THREAD_CREATED));
        list.add(helper.insertMessageOperation(senderId, threadId, GcmPayload.Type.USER_ADDED.name(), senderId, GcmPayload.Type.USER_ADDED));

        for (final String regId : regIds) {
            list.add(helper.insertThreadUserOperation(threadId, regId));
            list.add(helper.insertMessageOperation(senderId, threadId, GcmPayload.Type.USER_ADDED.name(), regId, GcmPayload.Type.USER_ADDED));
        }

        return list;
    }
}
