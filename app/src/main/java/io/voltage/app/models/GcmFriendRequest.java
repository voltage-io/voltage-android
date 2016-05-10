package io.voltage.app.models;

import android.os.Bundle;

import com.google.gson.annotations.SerializedName;

public class GcmFriendRequest extends GcmPayload {
    private interface Fields extends GcmPayload.Fields {
        String SENDER_ID = "sender_id";
        String REG_ID = "reg_id";
    }

    @SerializedName(Fields.SENDER_ID)
    private String mSenderId;

    @SerializedName(Fields.REG_ID)
    private String mRegId;

    public GcmFriendRequest(final Bundle extras) {
        mSenderId = extras.getString(Fields.SENDER_ID);
        mRegId = extras.getString(Fields.REG_ID);
    }

    public GcmFriendRequest(final String senderId, final String regId) {
        mSenderId = senderId;
        mRegId = regId;
        setEnumType(Type.FRIEND_REQUEST);
    }

    public String getSenderId() {
        return mSenderId;
    }

    public String getRegId() {
        return mRegId;
    }
}
