package io.voltage.app.operations;

import android.content.Context;
import android.os.Parcel;

import java.util.Collections;
import java.util.List;

import io.pivotal.arca.threading.Identifier;
import io.voltage.app.application.VoltageContentProvider;

public abstract class SyncOperation extends GcmPayloadOperation {

    protected final String mThreadId;
    protected final String mRecipientId;

    public SyncOperation(final String threadId, final String recipientId) {
        super(VoltageContentProvider.Uris.TRANSACTIONS);
        mThreadId = threadId;
        mRecipientId = recipientId;
    }

    protected SyncOperation(final Parcel in) {
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
        return new Identifier<String>("SYNC:" + mThreadId + ":" + mRecipientId);
    }

    @Override
    public List<String> onCreateRecipientList(final Context context) {
        return Collections.singletonList(mRecipientId);
    }

}
