package io.voltage.app.operations;

import android.content.Context;
import android.os.Parcel;

import java.util.Collections;
import java.util.List;

import io.pivotal.arca.threading.Identifier;
import io.voltage.app.application.VoltageContentProvider;
import io.voltage.app.application.VoltagePreferences;
import io.voltage.app.models.GcmFriendRequest;
import io.voltage.app.models.GcmPayload;
import io.voltage.app.models.User;

public class UserRequestOperation extends GcmPayloadOperation {

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
    public List<String> onCreateRecipientList(final Context context) {
        return Collections.singletonList(mRecipientId);
    }

    @Override
    public GcmPayload onCreateGcmPayload(final Context context) {
        final User user = mDatabaseHelper.getUser(context, mUserId);
        final String regId = VoltagePreferences.getRegId(context);

        if (user == null) {
            return new GcmFriendRequest(regId, mUserId);
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
