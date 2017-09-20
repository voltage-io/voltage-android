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
import io.voltage.app.models.GcmSyncRequest;
import io.voltage.app.models.Transactions;

public class SyncRequestOperation extends TaskOperation<GcmResponse> {

    private final MessagingHelper mMessagingHelper = new MessagingHelper.Default();
    private final DatabaseHelper mDatabaseHelper = new DatabaseHelper.Default();

    private final String mThreadId;
    private final String mRecipientId;

    public SyncRequestOperation(final String threadId, final String recipientId) {
        super(VoltageContentProvider.Uris.TRANSACTIONS);
        mThreadId = threadId;
        mRecipientId = recipientId;
    }

    private SyncRequestOperation(final Parcel in) {
        super(in);
        mThreadId = in.readString();
        mRecipientId = in.readString();
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(mThreadId);
        dest.writeString(mRecipientId);
    }

    @Override
    public Identifier<?> onCreateIdentifier() {
        return new Identifier<String>("SYNC_REQUEST:" + mThreadId + ":" + mRecipientId);
    }

    @Override
    public GcmResponse onExecute(final Context context) throws Exception {

        final List<String> regIds = Collections.singletonList(mRecipientId);

        final Transactions transactions = mDatabaseHelper.getTransactions(context, mThreadId);

        final String regId = VoltagePreferences.getRegId(context);

        if (transactions != null) {
            final int msgIndex = transactions.getCount() - 1;
            final String msgUuid = transactions.getMsgUuidsList().get(msgIndex);

            final GcmPayload gcmPayload = new GcmSyncRequest(mThreadId, regId, msgUuid, msgIndex);
            return mMessagingHelper.sendGcmRequest(context, regIds, gcmPayload);
        } else {
            final GcmPayload gcmPayload = new GcmSyncRequest(mThreadId, regId, null, 0);
            return mMessagingHelper.sendGcmRequest(context, regIds, gcmPayload);
        }
    }

    public static final Creator CREATOR = new Creator() {
        @Override
        public SyncRequestOperation createFromParcel(final Parcel in) {
            return new SyncRequestOperation(in);
        }

        @Override
        public SyncRequestOperation[] newArray(final int size) {
            return new SyncRequestOperation[size];
        }
    };
}
