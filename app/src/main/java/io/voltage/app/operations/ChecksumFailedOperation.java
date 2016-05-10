package io.voltage.app.operations;

import android.content.Context;
import android.os.Parcel;

import io.pivotal.arca.threading.Identifier;
import io.voltage.app.application.VoltagePreferences;
import io.voltage.app.models.GcmChecksumFailed;
import io.voltage.app.models.GcmPayload;
import io.voltage.app.models.Transactions;
import io.voltage.app.utils.CryptoUtils;

public class ChecksumFailedOperation extends SyncOperation {

    private final String mChecksum;

    public ChecksumFailedOperation(final String threadId, final String recipientId, final String checksum) {
        super(threadId, recipientId);
        mChecksum = checksum;
    }

    private ChecksumFailedOperation(final Parcel in) {
        super(in);
        mChecksum = in.readString();
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(mChecksum);
    }

    @Override
    public Identifier<?> onCreateIdentifier() {
        return new Identifier<String>("CHECKSUM_FAILED:" + mThreadId + ":" + mRecipientId);
    }

    @Override
    public GcmPayload onCreateGcmPayload(final Context context) {
        final Transactions transactions = mDatabaseHelper.getTransactions(context, mThreadId);

        if (transactions == null) {
            throw new RuntimeException("No transactions available");
        }

        if (!CryptoUtils.checksum(transactions).equals(mChecksum)) {
            final String regId = VoltagePreferences.getRegId(context);

            return new GcmChecksumFailed(mThreadId, regId);
        } else {
            throw new RuntimeException("Transactions already in sync");
        }
    }

    public static final Creator CREATOR = new Creator() {
        @Override
        public ChecksumFailedOperation createFromParcel(final Parcel in) {
            return new ChecksumFailedOperation(in);
        }

        @Override
        public ChecksumFailedOperation[] newArray(final int size) {
            return new ChecksumFailedOperation[size];
        }
    };
}
