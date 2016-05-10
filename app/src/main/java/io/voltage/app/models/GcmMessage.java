package io.voltage.app.models;

import android.os.Bundle;

import com.google.gson.annotations.SerializedName;

public class GcmMessage extends GcmPayload {
    protected interface Fields extends GcmPayload.Fields {
        String MSG_UUID = "msg_uuid";
        String THREAD_ID = "thread_id";
        String SENDER_ID = "sender_id";
        String TEXT = "text";
        String TIMESTAMP = "timestamp";
        String METADATA = "metadata";
        String STATE = "state";
    }

    @SerializedName(Fields.MSG_UUID)
    private String mMsgUuid;

    @SerializedName(Fields.THREAD_ID)
    private String mThreadId;

    @SerializedName(Fields.TEXT)
    private String mText;

    @SerializedName(Fields.SENDER_ID)
    private String mSenderId;

    @SerializedName(Fields.TIMESTAMP)
    private String mTimestamp;

    @SerializedName(Fields.METADATA)
    private String mMetadata;

    @SerializedName(Fields.STATE)
    private String mState;

    public GcmMessage(final Bundle extras) {
        mMsgUuid = extras.getString(Fields.MSG_UUID);
        mThreadId = extras.getString(Fields.THREAD_ID);
        mSenderId = extras.getString(Fields.SENDER_ID);
        mText = extras.getString(Fields.TEXT);
        mTimestamp = extras.getString(Fields.TIMESTAMP);
        mMetadata = extras.getString(Fields.METADATA);
        mState = extras.getString(Fields.STATE);
        setType(extras.getString(Fields.TYPE));
    }

    public GcmMessage(final Message message) {
        mMsgUuid = message.getMsgUuid();
        mThreadId = message.getThreadId();
        mText = message.getText();
        mSenderId = message.getSenderId();
        mTimestamp = String.valueOf(message.getTimestamp());
        mMetadata = message.getMetadata();
        mState = String.valueOf(message.getState());
        setType(message.getType());
    }

    public Bundle getBundle() {
        final Bundle bundle = super.getBundle();
        bundle.putString(Fields.MSG_UUID, mMsgUuid);
        bundle.putString(Fields.THREAD_ID, mThreadId);
        bundle.putString(Fields.SENDER_ID, mSenderId);
        bundle.putString(Fields.TEXT, mText);
        bundle.putString(Fields.TIMESTAMP, mTimestamp);
        bundle.putString(Fields.METADATA, mMetadata);
        bundle.putString(Fields.STATE, mState);
        return bundle;
    }

    public String getMsgUuid() {
        return mMsgUuid;
    }

    public String getThreadId() {
        return mThreadId;
    }

    public String getText() {
        return mText;
    }

    public String getSenderId() {
        return mSenderId;
    }

    public String getTimestamp() {
        return mTimestamp;
    }

    public String getMetadata() {
        return mMetadata;
    }

    public String getState() {
        return mState;
    }
}
