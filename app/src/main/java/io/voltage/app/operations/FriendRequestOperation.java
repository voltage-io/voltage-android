package io.voltage.app.operations;

import android.content.Context;
import android.os.Parcel;

import io.pivotal.arca.service.TaskOperation;
import io.pivotal.arca.threading.Identifier;
import io.voltage.app.application.VoltageContentProvider;
import io.voltage.app.application.VoltagePreferences;
import io.voltage.app.helpers.MessagingHelper;
import io.voltage.app.models.GcmFriendRequest;
import io.voltage.app.models.GcmPayload;
import io.voltage.app.models.GcmResponse;

public class FriendRequestOperation extends TaskOperation<GcmResponse> {

    private final MessagingHelper mMessagingHelper = new MessagingHelper.Default();

    private final String mUserId;

    public FriendRequestOperation(final String userId) {
        super(VoltageContentProvider.Uris.USERS);
        mUserId = userId;
    }

    private FriendRequestOperation(final Parcel in) {
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
        return new Identifier<String>("FRIEND_REQUEST:" + mUserId);
    }

    @Override
    public GcmResponse onExecute(final Context context) throws Exception {

        final String regId = VoltagePreferences.getRegId(context);
        final GcmPayload gcmPayload = new GcmFriendRequest(regId, mUserId);

        return mMessagingHelper.send(context, mUserId, gcmPayload);
    }

    public static final Creator CREATOR = new Creator() {
        @Override
        public FriendRequestOperation createFromParcel(final Parcel in) {
            return new FriendRequestOperation(in);
        }

        @Override
        public FriendRequestOperation[] newArray(final int size) {
            return new FriendRequestOperation[size];
        }
    };
}
