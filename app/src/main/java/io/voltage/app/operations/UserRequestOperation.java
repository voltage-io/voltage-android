package io.voltage.app.operations;

import android.content.Context;
import android.os.Parcel;

import java.util.Collections;
import java.util.List;

import io.pivotal.arca.service.TaskOperation;
import io.pivotal.arca.threading.Identifier;
import io.voltage.app.application.VoltageContentProvider;
import io.voltage.app.application.VoltagePreferences;
import io.voltage.app.helpers.DatabaseHelper;
import io.voltage.app.helpers.MessagingHelper;
import io.voltage.app.models.GcmFriendRequest;
import io.voltage.app.models.GcmPayload;
import io.voltage.app.models.GcmResponse;
import io.voltage.app.models.User;

public class UserRequestOperation extends TaskOperation<GcmResponse> {

    private final MessagingHelper mMessagingHelper = new MessagingHelper.Default();
    private final DatabaseHelper mDatabaseHelper = new DatabaseHelper.Default();

    private final String mUserId;
    private final String mRecipientId;

    public UserRequestOperation(final String userId, final String recipientId) {
        super(VoltageContentProvider.Uris.USERS);
        mUserId = userId;
        mRecipientId = recipientId;
    }

    private UserRequestOperation(final Parcel in) {
        super(in);
        mUserId = in.readString();
        mRecipientId = in.readString();
    }

    @Override
    public void writeToParcel(final Parcel dest, final int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(mUserId);
        dest.writeString(mRecipientId);
    }

    @Override
    public Identifier<?> onCreateIdentifier() {
        return new Identifier<String>("USER_REQUEST:" + mUserId);
    }

    @Override
    public GcmResponse onExecute(final Context context) throws Exception {

        final List<String> regIds = Collections.singletonList(mRecipientId);

        final User user = mDatabaseHelper.getUser(context, mUserId);
        final String regId = VoltagePreferences.getRegId(context);

        if (user == null) {
            final GcmPayload gcmPayload = new GcmFriendRequest(regId, mUserId);

            return mMessagingHelper.sendGcmRequest(context, regIds, gcmPayload);
        } else {
            throw new RuntimeException("User already exists.");
        }
    }

    public static final Creator CREATOR = new Creator() {
        @Override
        public UserRequestOperation createFromParcel(final Parcel in) {
            return new UserRequestOperation(in);
        }

        @Override
        public UserRequestOperation[] newArray(final int size) {
            return new UserRequestOperation[size];
        }
    };
}
