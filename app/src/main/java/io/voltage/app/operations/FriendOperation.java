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
import io.voltage.app.models.GcmFriend;
import io.voltage.app.models.GcmPayload;
import io.voltage.app.models.GcmResponse;

public class FriendOperation extends TaskOperation<GcmResponse> {

    private final MessagingHelper mMessagingHelper = new MessagingHelper.Default();

    private final String mRecipientId;

    public FriendOperation(final String recipientId) {
        super(VoltageContentProvider.Uris.USERS);
        mRecipientId = recipientId;
    }

    private FriendOperation(final Parcel in) {
        super(in);
        mRecipientId = in.readString();
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(mRecipientId);
    }

    @Override
    public Identifier<?> onCreateIdentifier() {
        return new Identifier<String>("FRIEND:" + mRecipientId);
    }

    @Override
    public GcmResponse onExecute(final Context context) throws Exception {

        final List<String> regIds = Collections.singletonList(mRecipientId);

        final String name = VoltagePreferences.getUserName(context);
        final String senderId = VoltagePreferences.getRegId(context);

        final GcmPayload gcmPayload = new GcmFriend(name, senderId);

        return mMessagingHelper.sendGcmRequest(context, regIds, gcmPayload);
    }

    public static final Creator CREATOR = new Creator() {
        @Override
        public FriendOperation createFromParcel(final Parcel in) {
            return new FriendOperation(in);
        }

        @Override
        public FriendOperation[] newArray(final int size) {
            return new FriendOperation[size];
        }
    };
}
