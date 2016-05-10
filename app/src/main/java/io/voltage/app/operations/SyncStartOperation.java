package io.voltage.app.operations;

import android.content.Context;
import android.os.Parcel;

import java.util.List;

import io.pivotal.arca.threading.Identifier;
import io.voltage.app.application.VoltagePreferences;
import io.voltage.app.models.GcmPayload;
import io.voltage.app.models.GcmSyncStart;
import io.voltage.app.models.Transactions;

public class SyncStartOperation extends SyncOperation {

    private final String mMsgUuid;
    private final int mMsgIndex;

    public SyncStartOperation(final String threadId, final String senderId, final String msgUuid, final int msgIndex) {
        super(threadId, senderId);
        mMsgUuid = msgUuid;
        mMsgIndex = msgIndex;
    }

    private SyncStartOperation(final Parcel in) {
        super(in);
        mMsgUuid = in.readString();
        mMsgIndex = in.readInt();
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(mMsgUuid);
        dest.writeInt(mMsgIndex);
    }

    @Override
    public Identifier<?> onCreateIdentifier() {
        return new Identifier<String>("SYNC_START:" + mThreadId + ":" + mRecipientId);
    }

    @Override
    public GcmPayload onCreateGcmPayload(final Context context) {
        final Transactions transactions = mDatabaseHelper.getTransactions(context, mThreadId);

        final String regId = VoltagePreferences.getRegId(context);
        final List<String> list = transactions.getMsgUuidsList();

        if (mMsgIndex < list.size()) {
            return onCreateGcmSyncStart(list, regId);
        } else {
            throw new RuntimeException("Transactions ahead of peer");
        }
    }

    private GcmPayload onCreateGcmSyncStart(final List<String> list, final String regId) {
        if (list.get(mMsgIndex).equals(mMsgUuid)) {
            final int count = list.size() - mMsgIndex - 1;
            return new GcmSyncStart(mThreadId, regId, count);

        } else {
            return new GcmSyncStart(mThreadId, regId, list.size());
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
