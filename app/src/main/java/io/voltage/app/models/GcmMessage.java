package io.voltage.app.models;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

import io.voltage.app.utils.CryptoUtils;

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

    public GcmMessage(final Map<String, String> data) {
        mMsgUuid = data.get(Fields.MSG_UUID);
        mThreadId = data.get(Fields.THREAD_ID);
        mSenderId = data.get(Fields.SENDER_ID);
        mText = data.get(Fields.TEXT);
        mTimestamp = data.get(Fields.TIMESTAMP);
        mMetadata = data.get(Fields.METADATA);
        mState = data.get(Fields.STATE);
        setType(data.get(Fields.TYPE));
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

    public Map<String, String> toMap() {
        final Map<String, String> map = super.toMap();
        map.put(Fields.MSG_UUID, mMsgUuid);
        map.put(Fields.THREAD_ID, mThreadId);
        map.put(Fields.SENDER_ID, mSenderId);
        map.put(Fields.TEXT, mText);
        map.put(Fields.TIMESTAMP, mTimestamp);
        map.put(Fields.METADATA, mMetadata);
        map.put(Fields.STATE, mState);
        return map;
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

    public void attemptAesEncrypt(final String threadKey) {
        mText = CryptoUtils.attemptAesEncrypt(threadKey, mText);
    }

    public void attemptAesDecrypt(final String threadKey) {
        mText = CryptoUtils.attemptAesDecrypt(threadKey, mText);
    }

    public void attemptRsaEncrypt(final String publicKey) {
        mMetadata = CryptoUtils.attemptRsaEncrypt(publicKey, mMetadata);
    }

    public void attemptRsaDecrypt(final String publicKey) {
        mMetadata = CryptoUtils.attemptRsaDecrypt(publicKey, mMetadata);
    }
}
