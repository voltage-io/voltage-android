package io.voltage.app.operations;

import android.content.Context;
import android.os.Parcel;

import io.pivotal.arca.threading.Identifier;
import io.voltage.app.application.VoltagePreferences;
import io.voltage.app.models.GcmPayload;
import io.voltage.app.models.GcmSyncRequest;
import io.voltage.app.models.Transactions;

public class SyncRequestOperation extends SyncOperation {

    public SyncRequestOperation(final String threadId, final String recipientId) {
        super(threadId, recipientId);
    }

    private SyncRequestOperation(final Parcel in) {
        super(in);
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        super.writeToParcel(dest, flags);
    }

    @Override
    public Identifier<?> onCreateIdentifier() {
        return new Identifier<String>("SYNC_REQUEST:" + mThreadId + ":" + mRecipientId);
    }

    @Override
    public GcmPayload onCreateGcmPayload(final Context context) {
        final Transactions transactions = mDatabaseHelper.getTransactions(context, mThreadId);

        final String regId = VoltagePreferences.getRegId(context);

        if (transactions != null) {
            final int msgIndex = transactions.getCount() - 1;
            final String msgUuid = transactions.getMsgUuidsList().get(msgIndex);

            return new GcmSyncRequest(mThreadId, regId, msgUuid, msgIndex);
        } else {
            return new GcmSyncRequest(mThreadId, regId, null, 0);
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
