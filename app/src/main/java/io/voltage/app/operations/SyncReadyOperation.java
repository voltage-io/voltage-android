package io.voltage.app.operations;

import android.content.Context;
import android.os.Parcel;

import io.pivotal.arca.threading.Identifier;
import io.voltage.app.application.VoltagePreferences;
import io.voltage.app.models.GcmPayload;
import io.voltage.app.models.GcmSyncReady;

public class SyncReadyOperation extends SyncOperation {

    private final int mCount;

    public SyncReadyOperation(final String threadId, final String recipientId, final int count) {
        super(threadId, recipientId);
        mCount = count;
    }

    private SyncReadyOperation(final Parcel in) {
        super(in);
        mCount = in.readInt();
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(mCount);
    }

    @Override
    public Identifier<?> onCreateIdentifier() {
        return new Identifier<String>("SYNC_READY:" + mThreadId + ":" + mRecipientId);
    }

    @Override
    public GcmPayload onCreateGcmPayload(final Context context) {
        final String regId = VoltagePreferences.getRegId(context);
        return new GcmSyncReady(mThreadId, regId, mCount);
    }

    public static final Creator CREATOR = new Creator() {
        @Override
        public SyncReadyOperation createFromParcel(final Parcel in) {
            return new SyncReadyOperation(in);
        }

        @Override
        public SyncReadyOperation[] newArray(final int size) {
            return new SyncReadyOperation[size];
        }
    };
}
