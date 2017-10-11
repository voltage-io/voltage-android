package io.voltage.app.models;

import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class GcmSyncMessage extends GcmSync {
    private interface Fields extends GcmSync.Fields {
        String MESSAGE = "message";
    }

    @SerializedName(Fields.MESSAGE)
    private GcmMessage mMessage;

    public GcmSyncMessage(final Map<String, String> data) {
        super(data);
        final String string = data.get(Fields.MESSAGE);
        mMessage = new Gson().fromJson(string, GcmMessage.class);
        setType(data.get(Fields.TYPE));
    }

    public GcmSyncMessage(final String threadId, final String senderId, final GcmMessage gcmMessage) {
        super(threadId, senderId);
        mMessage = gcmMessage;
        setEnumType(Type.SYNC_MESSAGE);
    }

    public String getTimestamp() {
        return mMessage != null ? mMessage.getTimestamp() : null;
    }

    public String getMsgUuid() {
        return mMessage != null ? mMessage.getMsgUuid() : null;
    }

    public Map<String, String> toMap() {
        return mMessage != null ? mMessage.toMap() : null;
    }
}
