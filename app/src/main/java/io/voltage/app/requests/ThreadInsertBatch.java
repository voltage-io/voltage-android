package io.voltage.app.requests;

import android.content.ContentProviderOperation;

import java.util.ArrayList;
import java.util.Set;

import io.pivotal.arca.dispatcher.Batch;
import io.voltage.app.application.VoltageContentProvider;
import io.voltage.app.helpers.OperationHelper;
import io.voltage.app.models.GcmPayload;
import io.voltage.app.utils.Logger;


public class ThreadInsertBatch extends Batch {

    public ThreadInsertBatch(final String threadId, final String threadKey, final String threadName, final String senderId, final Set<String> regIds) {
        super(VoltageContentProvider.BASE_URI, operations(threadId, threadKey, threadName, senderId, regIds));
    }

    private static ArrayList<ContentProviderOperation> operations(final String threadId, final String threadKey, final String threadName, final String senderId, final Set<String> regIds) {
        final ArrayList<ContentProviderOperation> list = new ArrayList<>();
        try {

            final OperationHelper helper = new OperationHelper();
            list.add(helper.insertThreadOperation(threadId, threadName));
            list.add(helper.insertMessageOperation(threadId, senderId, GcmPayload.Type.THREAD_CREATED.name(), threadName, GcmPayload.Type.THREAD_CREATED));

            Thread.sleep(1); // sleep so timestamp is different than THREAD_CREATED for ordering
            list.add(helper.updateThreadKeyOperation(threadId, threadKey));
            list.add(helper.insertMessageOperation(threadId, senderId, GcmPayload.Type.THREAD_KEY_ROTATED.name(), threadKey, GcmPayload.Type.THREAD_KEY_ROTATED));

            if (!regIds.contains(senderId)) {
                Thread.sleep(1); // sleep so timestamp is different than THREAD_CREATED for ordering
                list.add(helper.insertThreadUserOperation(threadId, senderId));
                list.add(helper.insertMessageOperation(threadId, senderId, GcmPayload.Type.USER_ADDED.name(), senderId, GcmPayload.Type.USER_ADDED));
            }

            for (final String regId : regIds) {
                Thread.sleep(1); // sleep so timestamp is different than THREAD_CREATED for ordering
                list.add(helper.insertThreadUserOperation(threadId, regId));
                list.add(helper.insertMessageOperation(threadId, senderId, GcmPayload.Type.USER_ADDED.name(), regId, GcmPayload.Type.USER_ADDED));
            }

        } catch (final Exception e) {
            Logger.ex(e);
        }
        return list;
    }
}
