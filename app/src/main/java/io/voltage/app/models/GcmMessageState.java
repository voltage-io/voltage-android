package io.voltage.app.models;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class GcmMessageState extends GcmPayload {
    private interface Fields extends GcmPayload.Fields {
        String MSG_UUID = "msg_uuid";
        String STATE = "state";
    }

    @SerializedName(Fields.MSG_UUID)
    private String mMsgUuid;

    @SerializedName(Fields.STATE)
    private int mState;


    public GcmMessageState(final Map<String, String> data) {
        mMsgUuid = data.get(Fields.MSG_UUID);
        mState = Integer.parseInt(data.get(Fields.STATE));
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
