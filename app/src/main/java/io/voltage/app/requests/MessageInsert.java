package io.voltage.app.requests;

import android.content.ContentValues;

import java.util.UUID;

import io.pivotal.arca.dispatcher.Insert;
import io.pivotal.arca.provider.DataUtils;
import io.voltage.app.application.VoltageContentProvider;
import io.voltage.app.application.VoltageContentProvider.MessageTable;
import io.voltage.app.models.GcmPayload;
import io.voltage.app.models.Message;

public class MessageInsert extends Insert {

    public MessageInsert(final String threadId, final String senderId, final String text, final String metadata, final GcmPayload.Type type) {
        super(VoltageContentProvider.Uris.MESSAGES, values(threadId, senderId, text, metadata, type));
    }

    public MessageInsert(final Message message) {
        super(VoltageContentProvider.Uris.MESSAGES, DataUtils.getContentValues(message));
    }

    private static ContentValues values(final String threadId, final String senderId, final String text, final String metadata, final GcmPayload.Type type) {
        final ContentValues values = new ContentValues();
        values.put(MessageTable.Columns.TEXT, text);
        values.put(MessageTable.Columns.THREAD_ID, threadId);
        values.put(MessageTable.Columns.SENDER_ID, senderId);
        values.put(MessageTable.Columns.MSG_UUID, UUID.randomUUID().toString());
        values.put(MessageTable.Columns.TIMESTAMP, System.currentTimeMillis());
        values.put(MessageTable.Columns.METADATA, metadata);
        values.put(MessageTable.Columns.TYPE, type.name());
        values.put(MessageTable.Columns._STATE, MessageTable.State.SENDING);
        return values;
    }
}
