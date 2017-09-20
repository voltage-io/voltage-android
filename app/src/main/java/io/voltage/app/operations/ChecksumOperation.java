package io.voltage.app.operations;

import android.content.Context;
import android.os.Parcel;

import java.util.List;

import io.pivotal.arca.service.TaskOperation;
import io.pivotal.arca.threading.Identifier;
import io.voltage.app.application.VoltageContentProvider;
import io.voltage.app.application.VoltagePreferences;
import io.voltage.app.helpers.DatabaseHelper;
import io.voltage.app.helpers.MessagingHelper;
import io.voltage.app.models.GcmChecksum;
import io.voltage.app.models.GcmPayload;
import io.voltage.app.models.GcmResponse;
import io.voltage.app.models.Participants;
import io.voltage.app.models.Transactions;
import io.voltage.app.utils.CryptoUtils;

public class ChecksumOperation extends TaskOperation<GcmResponse> {

    private final MessagingHelper mMessagingHelper = new MessagingHelper.Default();
    private final DatabaseHelper mDatabaseHelper = new DatabaseHelper.Default();

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
    public GcmResponse onExecute(final Context context) throws Exception {

        final Participants participants = mDatabaseHelper.getParticipants(context, mThreadId);
        final List<String> regIds = participants != null ? participants.getUserIdsList() : null;

        final Transactions transactions = mDatabaseHelper.getTransactions(context, mThreadId);

        final String checksum = CryptoUtils.checksum(transactions);
        final String regId = VoltagePreferences.getRegId(context);

        final GcmPayload gcmPayload = new GcmChecksum(mThreadId, regId, checksum);

        return mMessagingHelper.sendGcmRequest(context, regIds, gcmPayload);
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
