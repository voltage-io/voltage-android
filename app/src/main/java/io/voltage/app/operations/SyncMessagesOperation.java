package io.voltage.app.operations;

import android.content.Context;
import android.os.Parcel;

import java.util.ArrayList;
import java.util.List;

import io.pivotal.arca.service.TaskOperation;
import io.pivotal.arca.threading.Identifier;
import io.voltage.app.application.VoltageContentProvider;
import io.voltage.app.application.VoltagePreferences;
import io.voltage.app.helpers.DatabaseHelper;
import io.voltage.app.helpers.MessagingHelper;
import io.voltage.app.models.GcmMessage;
import io.voltage.app.models.GcmResponse;
import io.voltage.app.models.GcmSyncMessage;
import io.voltage.app.models.Message;
import io.voltage.app.models.User;

public class SyncMessagesOperation extends TaskOperation<List<GcmResponse>> {

    private final MessagingHelper mMessagingHelper = new MessagingHelper.Default();
    private final DatabaseHelper mDatabaseHelper = new DatabaseHelper.Default();

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
    public List<GcmResponse> onExecute(final Context context) throws Exception {

        final User user = mDatabaseHelper.getUser(context, mRecipientId);
        final List<Message> messages = mDatabaseHelper.getThreadMetadata(context, mThreadId);

        final List<GcmResponse> responses = new ArrayList<>();

        for (int index = (messages.size() - mCount); index < messages.size(); index++) {

            final String regId = VoltagePreferences.getRegId(context);
            final GcmMessage gcmMessage = new GcmMessage(messages.get(index));
            final GcmSyncMessage gcmSyncMessage = new GcmSyncMessage(mThreadId, regId, gcmMessage);

            responses.add(mMessagingHelper.rsaEncryptAndSend(context, user, gcmSyncMessage));
        }

        return responses;
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
