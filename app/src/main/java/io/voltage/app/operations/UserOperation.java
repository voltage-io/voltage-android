package io.voltage.app.operations;

import android.content.Context;
import android.os.Parcel;

import java.util.Collections;
import java.util.List;

import io.pivotal.arca.threading.Identifier;
import io.voltage.app.application.VoltageContentProvider;
import io.voltage.app.helpers.DatabaseHelper;
import io.voltage.app.models.GcmFriend;
import io.voltage.app.models.GcmPayload;
import io.voltage.app.models.User;

public class UserOperation extends GcmPayloadOperation {

    private final DatabaseHelper mDatabaseHelper = new DatabaseHelper();

    private final String mUserId;
    private final String mRecipientId;

    public UserOperation(final String userId, final String recipientId) {
        super(VoltageContentProvider.Uris.USERS);
        mUserId = userId;
        mRecipientId = recipientId;
    }

    private UserOperation(final Parcel in) {
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
        return new Identifier<String>("USER:" + mUserId);
    }

    @Override
    public List<String> onCreateRecipientList(final Context context) {
        return Collections.singletonList(mRecipientId);
    }

    @Override
    public GcmPayload onCreateGcmPayload(final Context context) {
        final User user = mDatabaseHelper.getUser(context, mUserId);

        return new GcmFriend(user.getName(), user.getRegId());
    }

    public static final Creator CREATOR = new Creator() {
        @Override
        public UserOperation createFromParcel(final Parcel in) {
            return new UserOperation(in);
        }

        @Override
        public UserOperation[] newArray(final int size) {
            return new UserOperation[size];
        }
    };
}
