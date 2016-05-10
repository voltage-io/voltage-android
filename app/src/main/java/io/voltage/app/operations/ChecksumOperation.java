package io.voltage.app.operations;

import android.content.Context;
import android.os.Parcel;

import java.util.List;

import io.pivotal.arca.threading.Identifier;
import io.voltage.app.application.VoltageContentProvider;
import io.voltage.app.application.VoltagePreferences;
import io.voltage.app.models.GcmChecksum;
import io.voltage.app.models.GcmPayload;
import io.voltage.app.models.Participants;
import io.voltage.app.models.Transactions;
import io.voltage.app.utils.CryptoUtils;

public class ChecksumOperation extends GcmPayloadOperation {

    private final String mThreadId;

    public ChecksumOperation(final String threadId) {
        super(VoltageContentProvider.Uris.TRANSACTIONS);
        mThreadId = threadId;
    }

    private ChecksumOperation(final Parcel in) {
        super(in);
        mThreadId = in.readString();
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(mThreadId);
    }

    @Override
    public Identifier<?> onCreateIdentifier() {
        return new Identifier<String>("CHECKSUM:" + mThreadId);
    }

    @Override
    public List<String> onCreateRecipientList(final Context context) {
        final Participants participants = mDatabaseHelper.getParticipants(context, mThreadId);
        return participants != null ? participants.getUserIdsList() : null;
    }

    @Override
    public GcmPayload onCreateGcmPayload(final Context context) {
        final Transactions transactions = mDatabaseHelper.getTransactions(context, mThreadId);

        final String checksum = CryptoUtils.checksum(transactions);
        final String regId = VoltagePreferences.getRegId(context);

        return new GcmChecksum(mThreadId, regId, checksum);
    }

    public static final Creator CREATOR = new Creator() {
        @Override
        public ChecksumOperation createFromParcel(final Parcel in) {
            return new ChecksumOperation(in);
        }

        @Override
        public ChecksumOperation[] newArray(final int size) {
            return new ChecksumOperation[size];
        }
    };
}
