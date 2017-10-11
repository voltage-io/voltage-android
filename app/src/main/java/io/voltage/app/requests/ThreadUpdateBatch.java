package io.voltage.app.requests;

import android.content.ContentProviderOperation;

import java.util.ArrayList;

import io.pivotal.arca.dispatcher.Batch;
import io.voltage.app.application.VoltageContentProvider;
import io.voltage.app.helpers.OperationHelper;
import io.voltage.app.models.GcmPayload;


public class ThreadUpdateBatch extends Batch {

    public ThreadUpdateBatch(final String threadId, final String senderId, final String name) {
        super(VoltageContentProvider.BASE_URI, operations(threadId, senderId, name));
    }

    private static ArrayList<ContentProviderOperation> operations(final String threadId, final String senderId, final String name) {
        final OperationHelper helper = new OperationHelper();
        final ArrayList<ContentProviderOperation> list = new ArrayList<>();
        list.add(helper.updateThreadOperation(threadId, name));
        list.add(helper.insertMessageOperation(threadId, senderId, GcmPayload.Type.THREAD_RENAMED.name(), name, GcmPayload.Type.THREAD_RENAMED));
        return list;
    }
}
