package io.voltage.app.operations;

import android.content.ContentValues;
import android.content.Context;
import android.os.Parcel;

import java.util.Collections;
import java.util.List;

import io.pivotal.arca.threading.Identifier;
import io.voltage.app.application.VoltageContentProvider;
import io.voltage.app.application.VoltagePreferences;
import io.voltage.app.helpers.DatabaseHelper;
import io.voltage.app.models.GcmMessage;
import io.voltage.app.models.GcmSyncMessage;
import io.voltage.app.models.Message;

public class SyncMessagesOperation extends GcmOperation {

    private final DatabaseHelper mDatabaseHelper = new DatabaseHelper();

    private final String mThreadId;
    private final String mRecipientId;
    private final int mCount;

    public SyncMessagesOperation(final String threadId, final String recipientId, final int count) {
        super(VoltageContentProvider.Uris.MESSAGES);
        mThreadId = threadId;
        mRecipientId = recipientId;
        mCount = count;
    }

    private SyncMessagesOperation(final Parcel in) {
        super(in);
        mThreadId = in.readString();
        mRecipientId = in.readString();
        mCount = in.readInt();
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(mThreadId);
        dest.writeString(mRecipientId);
        dest.writeInt(mCount);
    }

    @Override
    public Identifier<?> onCreateIdentifier() {
        return new Identifier<String>("SYNC_MESSAGES:" + mThreadId + ":" + mRecipientId);
    }

    @Override
    public ContentValues[] onExecute(final Context context) throws Exception {

        final List<String> regIds = Collections.singletonList(mRecipientId);
        final List<Message> messages = mDatabaseHelper.getThreadMetadata(context, mThreadId);

        for (int index = (messages.size() - mCount); index < messages.size(); index++) {
            final Message message = messages.get(index);
            final GcmMessage gcmMessage = new GcmMessage(message);
            final String regId = VoltagePreferences.getRegId(context);

            sendGcmRequest(context, regIds, new GcmSyncMessage(mThreadId, regId, gcmMessage));
        }

        return null;
    }

    public static final Creator CREATOR = new Creator() {
        @Override
        public SyncMessagesOperation createFromParcel(final Parcel in) {
            return new SyncMessagesOperation(in);
        }

        @Override
        public SyncMessagesOperation[] newArray(final int size) {
            return new SyncMessagesOperation[size];
        }
    };
}
