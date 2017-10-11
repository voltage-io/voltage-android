package io.voltage.app.requests;

import android.content.ContentProviderOperation;

import java.util.ArrayList;

import io.pivotal.arca.dispatcher.Batch;
import io.voltage.app.application.VoltageContentProvider;
import io.voltage.app.helpers.OperationHelper;
import io.voltage.app.models.Message;
import io.voltage.app.models.Thread;
import io.voltage.app.models.ThreadUser;


public class MessageInsertBatch extends Batch {

    public MessageInsertBatch(final Thread thread, final ThreadUser threadUser, final Message message) {
        super(VoltageContentProvider.BASE_URI, operations(thread, threadUser, message));
    }

    private static ArrayList<ContentProviderOperation> operations(final Thread thread, final ThreadUser threadUser, final Message message) {
        final OperationHelper helper = new OperationHelper();
        final ArrayList<ContentProviderOperation> list = new ArrayList<>();
        list.add(helper.insertThreadOperation(thread));
        list.add(helper.insertMessageOperation(message));
        list.add(helper.insertThreadUserOperation(threadUser));
        return list;
    }
}
