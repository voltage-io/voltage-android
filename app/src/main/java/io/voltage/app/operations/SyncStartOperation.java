package io.voltage.app.operations;

import android.content.Context;
import android.os.Parcel;

import java.util.Collections;
import java.util.List;

import io.pivotal.arca.service.TaskOperation;
import io.pivotal.arca.threading.Identifier;
import io.voltage.app.application.VoltageContentProvider;
import io.voltage.app.application.VoltagePreferences;
import io.voltage.app.helpers.DatabaseHelper;
import io.voltage.app.helpers.MessagingHelper;
import io.voltage.app.models.GcmPayload;
import io.voltage.app.models.GcmResponse;
import io.voltage.app.models.GcmSyncStart;
import io.voltage.app.models.Transactions;

public class SyncStartOperation extends TaskOperation<GcmResponse> {

    private final MessagingHelper mMessagingHelper = new MessagingHelper.Default();
    private final DatabaseHelper mDatabaseHelper = new DatabaseHelper.Default();

    private final String mThreadId;
    private final String mRecipientId;
    private final String mMsgUuid;
    private final int mMsgIndex;

    public SyncStartOperation(final String threadId, final String recipientId, final String msgUuid, final int msgIndex) {
        super(VoltageContentProvider.Uris.TRANSACTIONS);
        mThreadId = threadId;
        mRecipientId = recipientId;
        mMsgUuid = msgUuid;
        mMsgIndex = msgIndex;
    }

    private SyncStartOperation(final Parcel in) {
        super(in);
        mThreadId = in.readString();
        mRecipientId = in.readString();
        mMsgUuid = in.readString();
        mMsgIndex = in.readInt();
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(mThreadId);
        dest.writeString(mRecipientId);
        dest.writeString(mMsgUuid);
        dest.writeInt(mMsgIndex);
    }

    @Override
    public Identifier<?> onCreateIdentifier() {
        return new Identifier<String>("SYNC_START:" + mThreadId + ":" + mRecipientId);
    }

    @Override
    public GcmResponse onExecute(final Context context) throws Exception {

        final List<String> regIds = Collections.singletonList(mRecipientId);

        final Transactions transactions = mDatabaseHelper.getTransactions(context, mThreadId);
        final List<String> list = transactions.getMsgUuidsList();

        if (mMsgIndex >= list.size()) {
            throw new RuntimeException("Transactions ahead of peer");
        }

        final String regId = VoltagePreferences.getRegId(context);

        if (list.get(mMsgIndex).equals(mMsgUuid)) {
            final int count = list.size() - mMsgIndex - 1;
            final GcmPayload gcmPayload = new GcmSyncStart(mThreadId, regId, count);
            return mMessagingHelper.sendGcmRequest(context, regIds, gcmPayload);

        } else {
            final GcmPayload gcmPayload = new GcmSyncStart(mThreadId, regId, list.size());
            return mMessagingHelper.sendGcmRequest(context, regIds, gcmPayload);
        }
    }

    public static final Creator CREATOR = new Creator() {
        @Override
        public SyncStartOperation createFromParcel(final Parcel in) {
            return new SyncStartOperation(in);
        }

        @Override
        public SyncStartOperation[] newArray(final int size) {
            return new SyncStartOperation[size];
        }
    };
}
