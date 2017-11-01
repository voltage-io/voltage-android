package io.voltage.app.operations;

import android.content.Context;
import android.os.Parcel;

import io.pivotal.arca.service.TaskOperation;
import io.pivotal.arca.threading.Identifier;
import io.voltage.app.application.VoltageContentProvider;
import io.voltage.app.application.VoltagePreferences;
import io.voltage.app.helpers.MessagingHelper;
import io.voltage.app.models.GcmFriendResponse;
import io.voltage.app.models.GcmPayload;
import io.voltage.app.models.GcmResponse;

public class FriendResponseOperation extends TaskOperation<GcmResponse> {

    private final MessagingHelper mMessagingHelper = new MessagingHelper.Default();

    private final String mUserId;

    public FriendResponseOperation(final String userId) {
        super(VoltageContentProvider.Uris.USERS);
        mUserId = userId;
    }

    private FriendResponseOperation(final Parcel in) {
        super(in);
        mUserId = in.readString();
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(mUserId);
    }

    @Override
    public Identifier<?> onCreateIdentifier() {
        return new Identifier<String>("FRIEND_RESPONSE:" + mUserId);
    }

    @Override
    public GcmResponse onExecute(final Context context) throws Exception {

        final String name = VoltagePreferences.getUserName(context);
        final String regId = VoltagePreferences.getRegId(context);
        final String publicKey = VoltagePreferences.getPublicKey(context);

        final GcmPayload gcmPayload = new GcmFriendResponse(name, regId, publicKey);

        return mMessagingHelper.send(context, mUserId, gcmPayload);
    }

    public static final Creator CREATOR = new Creator() {
        @Override
        public FriendResponseOperation createFromParcel(final Parcel in) {
            return new FriendResponseOperation(in);
        }

        @Override
        public FriendResponseOperation[] newArray(final int size) {
            return new FriendResponseOperation[size];
        }
    };
}
