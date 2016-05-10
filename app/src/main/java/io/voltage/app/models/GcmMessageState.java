package io.voltage.app.models;

import android.os.Bundle;

import com.google.gson.annotations.SerializedName;

public class GcmMessageState extends GcmPayload {
    private interface Fields extends GcmPayload.Fields {
        String MSG_UUID = "msg_uuid";
        String STATE = "state";
    }

    @SerializedName(Fields.MSG_UUID)
    private String mMsgUuid;

    @SerializedName(Fields.STATE)
    private int mState;


    public GcmMessageState(final Bundle extras) {
        mMsgUuid = extras.getString(Fields.MSG_UUID);
        mState = Integer.parseInt(extras.getString(Fields.STATE));
    }

    public GcmMessageState(final MessageState state) {
        mMsgUuid = state.getMsgUuid();
        mState = state.getState();
        setEnumType(Type.RECEIPT);
    }

    public String getMsgUuid() {
        return mMsgUuid;
    }

    public int getState() {
        return mState;
    }
}
