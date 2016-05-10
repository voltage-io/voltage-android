package io.voltage.app.models;

import android.content.ContentValues;

import io.pivotal.arca.provider.ColumnName;
import io.voltage.app.application.VoltageContentProvider.MessageTable;
import io.voltage.app.utils.NumberUtils;

public class Message {

    @ColumnName(MessageTable.Columns.MSG_UUID)
    private String mMsgUuid;

    @ColumnName(MessageTable.Columns.THREAD_ID)
    private String mThreadId;

    @ColumnName(MessageTable.Columns.TEXT)
    private String mText;

    @ColumnName(MessageTable.Columns.SENDER_ID)
    private String mSenderId;

    @ColumnName(MessageTable.Columns.TIMESTAMP)
    private long mTimestamp;

    @ColumnName(MessageTable.Columns.METADATA)
    private String mMetadata;

    @ColumnName(MessageTable.Columns.TYPE)
    private String mType;

    @ColumnName(MessageTable.Columns._STATE)
    private int mState;

    public Message() {}

    public Message(final GcmMessage gcmMessage) {
        mMsgUuid = gcmMessage.getMsgUuid();
        mThreadId = gcmMessage.getThreadId();
        mText = gcmMessage.getText();
        mSenderId = gcmMessage.getSenderId();
        mTimestamp = NumberUtils.parseLong(gcmMessage.getTimestamp(), 0);
        mMetadata = gcmMessage.getMetadata();
        mType = gcmMessage.getType();
        mState = NumberUtils.parseInt(gcmMessage.getState(), 0);
    }

    public Message(final ContentValues values) {
        mMsgUuid = values.getAsString(MessageTable.Columns.MSG_UUID);
        mThreadId = values.getAsString(MessageTable.Columns.THREAD_ID);
        mText = values.getAsString(MessageTable.Columns.TEXT);
        mSenderId = values.getAsString(MessageTable.Columns.SENDER_ID);
        mTimestamp = values.getAsLong(MessageTable.Columns.TIMESTAMP);
        mMetadata = values.getAsString(MessageTable.Columns.METADATA);
        mType = values.getAsString(MessageTable.Columns.TYPE);
        mState = values.getAsInteger(MessageTable.Columns._STATE);
    }

    public String getMsgUuid() {
        return mMsgUuid;
    }

    public String getSenderId() {
        return mSenderId;
    }

    public String getThreadId() {
        return mThreadId;
    }

    public String getText() {
        return mText;
    }

    public long getTimestamp() {
        return mTimestamp;
    }

    public String getMetadata() {
        return mMetadata;
    }

    public int getState() {
        return mState;
    }

    public void setState(final int state) {
        mState = state;
    }

    public String getType() {
        return mType;
    }
}
