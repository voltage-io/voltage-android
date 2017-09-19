package io.voltage.app.models;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

public class GcmSync extends GcmPayload {
    protected interface Fields extends GcmPayload.Fields {
        String THREAD_ID = "thread_id";
        String SENDER_ID = "sender_id";
    }

    @SerializedName(Fields.THREAD_ID)
    private String mThreadId;

    @SerializedName(Fields.SENDER_ID)
    private String mSenderId;

    public GcmSync(final Map<String, String> data) {
        mThreadId = data.get(Fields.THREAD_ID);
        mSenderId = data.get(Fields.SENDER_ID);
    }

    public GcmSync(final String threadId, final String senderId) {
        mThreadId = threadId;
        mSenderId = senderId;
    }

    public String getThreadId() {
        return mThreadId;
    }

    public String getSenderId() {
        return mSenderId;
    }

}
