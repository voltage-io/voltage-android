package io.voltage.app.operations;

import android.content.Context;
import android.os.Parcel;

import io.pivotal.arca.service.TaskOperation;
import io.pivotal.arca.threading.Identifier;
import io.voltage.app.application.VoltageContentProvider;
import io.voltage.app.application.VoltagePreferences;
import io.voltage.app.helpers.DatabaseHelper;
import io.voltage.app.helpers.MessagingHelper;
import io.voltage.app.models.GcmChecksumFailed;
import io.voltage.app.models.GcmPayload;
import io.voltage.app.models.GcmResponse;
import io.voltage.app.models.Transactions;
import io.voltage.app.utils.CryptoUtils;

public class ChecksumReplyOperation extends TaskOperation<GcmResponse> {

    private final MessagingHelper mMessagingHelper = new MessagingHelper.Default();
    private final DatabaseHelper mDatabaseHelper = new DatabaseHelper.Default();

    private final String mThreadId;
    private final String mRecipientId;
    private final String mChecksum;

    public ChecksumReplyOperation(final String threadId, final String recipientId, final String checksum) {
        super(VoltageContentProvider.Uris.TRANSACTIONS);
        mThreadId = threadId;
        mRecipientId = recipientId;
        mChecksum = checksum;
    }

    private ChecksumReplyOperation(final Parcel in) {
        super(in);
        mThreadId = in.readString();
        mRecipientId = in.readString();
        mChecksum = in.readString();
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(mThreadId);
        dest.writeString(mRecipientId);
        dest.writeString(mChecksum);
    }

    @Override
    public Identifier<?> onCreateIdentifier() {
        return new Identifier<String>("CHECKSUM_REPLY:" + mThreadId + ":" + mRecipientId);
    }

    @Override
    public GcmResponse onExecute(final Context context) throws Exception {

        final Transactions transactions = mDatabaseHelper.getTransactions(context, mThreadId);
        final String checksum = CryptoUtils.checksum(transactions);

        if (checksum == null) {
            throw new RuntimeException("No transactions available");
        }

        if (mChecksum.equals(checksum)) {
            throw new RuntimeException("Transactions already in sync");
        }

        final String regId = VoltagePreferences.getRegId(context);
        final GcmPayload gcmPayload = new GcmChecksumFailed(mThreadId, regId);

        return mMessagingHelper.send(context, mRecipientId, gcmPayload);
    }

    public static final Creator CREATOR = new Creator() {
        @Override
        public ChecksumReplyOperation createFromParcel(final Parcel in) {
            return new ChecksumReplyOperation(in);
        }

        @Override
        public ChecksumReplyOperation[] newArray(final int size) {
            return new ChecksumReplyOperation[size];
        }
    };
}
