package io.voltage.app.models;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class GcmSyncRequest extends GcmSync {
    private interface Fields extends GcmSync.Fields {
        String MSG_UUID = "msg_uuid";
        String MSG_INDEX = "msg_index";
    }

    @SerializedName(Fields.MSG_UUID)
    private String mMsgUuid;

    @SerializedName(Fields.MSG_INDEX)
    private String mMsgIndex;

    public GcmSyncRequest(final Map<String, String> data) {
        super(data);
        mMsgUuid = data.get(Fields.MSG_UUID);
        mMsgIndex = data.get(Fields.MSG_INDEX);
    }

    public GcmSyncRequest(final String threadId, final String senderId, final String msgUuid, final int msgIndex) {
        super(threadId, senderId);
        mMsgUuid = msgUuid;
        mMsgIndex = String.valueOf(msgIndex);
        setEnumType(Type.SYNC_REQUEST);
    }

    public String getMsgUuid() {
        return mMsgUuid;
    }

    public int getMsgIndex() {
        return Integer.valueOf(mMsgIndex);
    }
}
