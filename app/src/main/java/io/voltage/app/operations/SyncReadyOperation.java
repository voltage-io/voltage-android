package io.voltage.app.operations;

import android.content.Context;
import android.os.Parcel;

import java.util.Collections;
import java.util.List;

import io.pivotal.arca.service.TaskOperation;
import io.pivotal.arca.threading.Identifier;
import io.voltage.app.application.VoltageContentProvider;
import io.voltage.app.application.VoltagePreferences;
import io.voltage.app.helpers.MessagingHelper;
import io.voltage.app.models.GcmPayload;
import io.voltage.app.models.GcmResponse;
import io.voltage.app.models.GcmSyncReady;

public class SyncReadyOperation extends TaskOperation<GcmResponse> {

    private final MessagingHelper mMessagingHelper = new MessagingHelper.Default();

    private final String mThreadId;
    private final String mRecipientId;
    private final int mCount;

    public SyncReadyOperation(final String threadId, final String recipientId, final int count) {
        super(VoltageContentProvider.Uris.TRANSACTIONS);
        mThreadId = threadId;
        mRecipientId = recipientId;
        mCount = count;
    }

    private SyncReadyOperation(final Parcel in) {
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
        return new Identifier<String>("SYNC_READY:" + mThreadId + ":" + mRecipientId);
    }

    @Override
    public GcmResponse onExecute(final Context context) throws Exception {

        final List<String> regIds = Collections.singletonList(mRecipientId);

        final String regId = VoltagePreferences.getRegId(context);
        final GcmPayload gcmPayload = new GcmSyncReady(mThreadId, regId, mCount);

        return mMessagingHelper.sendGcmRequest(context, regIds, gcmPayload);
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
